(ns main
  (:require
   ["./workflows" :refer [spam]]
   ["@temporalio/client" :refer [Client Connection]]
   ["@temporalio/worker" :refer [Worker]]
   [app-root-path :refer [toString]]
   [child_process :refer [spawn]]
   [cljs-node-io.core :refer [slurp spit]]
   [clojure.string :as string :refer [split]]
   [core :refer [path]]
   [flatland.ordered.map :refer [ordered-map]]
   [google-auth-library :refer [JWT]]
   [google-spreadsheet :refer [GoogleSpreadsheet]]
   [lambdaisland.uri :refer [uri]]
   [mount.core :refer [defstate start]]
   [nbb :refer [loadFile]]
   [os :refer [homedir]]
   [path :refer [join]]
   [promesa.core :as promesa]))


(defonce config
  (atom nil))

(defn get-spreadsheet-id
  [url]
  (-> url
      uri
      :path
      (split "/")
      (nth 3)))

(def initialize-config
  (comp (partial spit path)
        (partial string/replace (slurp "src/config.cljs") "<spreadsheet-id>")
        get-spreadsheet-id))

(defn load-config
  []
  (promesa/let [js-config (loadFile "src/bridge.cljs")]
    (reset! config (js->clj js-config :keywordize-keys true))))

(def google-cloud-credentials
  (-> (homedir)
      (join ".config/spam/google-cloud.json")
      slurp
      js/JSON.parse
      (js->clj :keywordize-keys true)))

(def service-account-auth
  (JWT. (clj->js {:email (:client_email google-cloud-credentials)
                  :key (:private_key google-cloud-credentials)
                  :scopes ["https://www.googleapis.com/auth/spreadsheets"]})))

(def schema
  (ordered-map :endpoints [:endpoint :prospect]
               :sources [:source :prospect]
               :messages [:date :endpoint :message]
               :runs [:timestamp :approved :endpoint :message :reason]))

(def sample
  {:endpoints [{:endpoint "contact@example.com"
                :prospect "https://example.com"}]
   :sources [{:source "https://example.com"
              :prospect "https://example.com"}]
   :messages [{:date "1/1/2000"
               :endpoint "contact@example.com"
               :message "Hello, this is a sample outreach message."}]
   :runs [{:endpoint "contact@example.com"}]})

(defn initialize-spreadsheet
  []
  (promesa/do (load-config)
              (promesa/let [spreadsheet (GoogleSpreadsheet. (:spreadsheet @config) service-account-auth)]
                (promesa/run! (fn [[k v]] (promesa/let [sheet (.addSheet spreadsheet (clj->js {:headerValues v :title k}))]
                                            (.addRows sheet (clj->js (k sample)))))
                              schema)
                (.loadInfo spreadsheet)
                (.delete (:Sheet1 (js->clj spreadsheet.sheetsByTitle :keywordize-keys true))))))

(defn init
  [url]
  (initialize-config url)
  (initialize-spreadsheet))

(defstate temporal
  :start (spawn "temporal" (clj->js ["server" "start-dev"]))
  :stop (js/process.kill (.-pid @temporal)))

(def task-queue
  "spam")

(defn run
  []
  (start)
  (promesa/let [worker (.create Worker (clj->js {:taskQueue task-queue
                                                 :workflowsPath (path/join (toString) "target/workflows.js")}))]
    (.run worker))
  (promesa/let [connection (.connect Connection)]
    (.workflow.execute (Client. connection) spam (clj->js {:taskQueue task-queue
                                                           :workflowId "spam"}))))

(defn main
  [& args]
  (case (first args)
    "email" (println (:client_email google-cloud-credentials))
    "init" (init (last args))
    "run" (run)))