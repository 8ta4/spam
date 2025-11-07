(ns main
  (:require
   ["@google/genai" :refer [GoogleGenAI]]
   ["@temporalio/client" :refer [Client Connection]]
   ["@temporalio/worker" :refer [Worker]]
   ["kill-port" :as kill-port]
   [app-root-path :refer [toString]]
   [child_process :refer [exec spawn]]
   [cljs-node-io.core :refer [slurp spit]]
   [clojure.string :as string :refer [split]]
   [com.rpl.specter :refer [setval]]
   [core :refer [path]]
   [datascript.core :refer [create-conn q transact!]]
   [flatland.ordered.map :refer [ordered-map]]
   [google-auth-library :refer [JWT]]
   [google-spreadsheet :refer [GoogleSpreadsheet]]
   [lambdaisland.uri :refer [uri]]
   [malli.json-schema :refer [transform]]
   [medley.core :refer [remove-vals]]
   [mount.core :refer [defstate start]]
   [nbb :refer [loadFile]]
   [os :refer [homedir]]
   [path :refer [join]]
   [promesa.core :as promesa :refer [all]]
   [util :refer [promisify]]
   [tick.core :refer [date]]))

(defonce config
  (atom {}))

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

(defn get-spreadsheet
  []
  (promesa/let [_ (load-config)
                spreadsheet (GoogleSpreadsheet. (:spreadsheet @config) service-account-auth)
                _ (.loadInfo spreadsheet)]
    spreadsheet))

(defn initialize-spreadsheet
  []
  (promesa/let [spreadsheet (get-spreadsheet)]
    (promesa/run! (fn [[k v]] (promesa/let [sheet (.addSheet spreadsheet (clj->js {:headerValues v :title k}))]
                                (->> sample
                                     k
                                     clj->js
                                     (.addRows sheet))))
                  schema)
    (.delete (:Sheet1 (js->clj spreadsheet.sheetsByTitle :keywordize-keys true)))))

(defn init
  [url]
  (initialize-config url)
  (initialize-spreadsheet))

(defstate temporal
  :start (spawn "temporal" (clj->js ["server" "start-dev"]))
  :stop (kill-port 8233))

(def task-queue
  "spam")

(def conn
  (create-conn {:prospect/prospect {:db/unique :db.unique/identity}
                :endpoint/endpoint {:db/unique :db.unique/identity}
                :endpoint/prospects {:db/cardinality :db.cardinality/many
                                     :db/valueType :db.type/ref}
                :source/source {:db/unique :db.unique/identity}
                :source/prospects {:db/cardinality :db.cardinality/many
                                   :db/valueType :db.type/ref}
                :message/date {}
                :message/endpoint {:db/valueType :db.type/ref}
                :message/message {}}))

(defn prepare-transaction-data
  [spreadsheet-data]
  (concat (map (fn [row]
                 {:endpoint/endpoint (:endpoint row)
                  :endpoint/prospects [{:prospect/prospect (:prospect row)}]})
               (:endpoints spreadsheet-data))
          (map (fn [row]
                 {:source/source (:source row)
                  :source/prospects [{:prospect/prospect (:prospect row)}]})
               (:sources spreadsheet-data))
          (map (fn [row]
                 {:message/date (:date row)
                  :message/endpoint {:endpoint/endpoint (:endpoint row)}
                  :message/message (:message row)})
               (:messages spreadsheet-data))))

(defn find-sources
  [endpoint]
  (->> endpoint
       (q '[:find ?source
            :in $ ?endpoint
            :where
            [?e :endpoint/endpoint ?endpoint]
            [?e :endpoint/prospects ?p]
            [?s :source/prospects ?p]
            [?s :source/source ?source]]
          @conn)
       (map first)
       set))

(defn find-messages
  [endpoint]
  (->> endpoint
       (q '[:find (pull ?m [:message/date {:message/endpoint [:endpoint/endpoint]} :message/message])
            :in $ ?endpoint
            :where
            [?initial-e :endpoint/endpoint ?endpoint]
            [?initial-e :endpoint/prospects ?p]
            [?related-e :endpoint/prospects ?p]
            [?m :message/endpoint ?related-e]]
          @conn)
       (map first)
       (sort-by :message/date)))

(defn prepare-contexts
  [endpoint]
  {:endpoint endpoint
   :sources (find-sources endpoint)
   :messages (find-messages endpoint)})

(defn orchestrate
  []
  (promesa/let [spreadsheet (get-spreadsheet)
                spreadsheet-data (promesa/->> #{:endpoints :sources :messages :runs}
                                              (map (fn [k]
                                                     (promesa/let [rows (-> spreadsheet.sheetsByTitle
                                                                            (js->clj :keywordize-keys true)
                                                                            k
                                                                            .getRows)]
                                                       {k (map #(remove-vals empty? (js->clj (.toObject %) :keywordize-keys true)) rows)})))
                                              all
                                              (apply merge))]
    (transact! conn (prepare-transaction-data spreadsheet-data))
    (->> spreadsheet-data
         :runs
         (remove :message)
         (map (comp prepare-contexts :endpoint))
         clj->js)))

(defn see
  [source]
  (promesa/-> (str "see " source)
              ((promisify exec))
              (js->clj :keywordize-keys true)
              :stdout))

(def client
  (GoogleGenAI. (clj->js {:apiKey (slurp (join (homedir) ".config/spam/google-ai-studio"))})))

(defn create
  [context]
  (promesa/do (load-config)
              (promesa/-> client
                          (.models.generateContent (clj->js {:config {:responseMimeType "application/json"
                                                                      :responseJsonSchema (transform [:map [:message :string]])
                                                                      :systemInstruction (->> @config
                                                                                              :prompts
                                                                                              :creator
                                                                                              :system)}
                                                             :contents (->> (js->clj context :keywordize-keys true)
                                                                            (setval :date (date))
                                                                            clj->js
                                                                            ((->> @config
                                                                                  :prompts
                                                                                  :creator
                                                                                  :user)))
                                                             :model "gemini-2.5-flash-lite"}))
                          .-text)))

(defstate worker
; https://github.com/tolitius/mount/issues/118#issuecomment-667433275
  :start (let [worker* (atom nil)]
           (promesa/let [worker** (.create Worker (clj->js {:activities (clj->js {:orchestrate orchestrate
                                                                                  :see see
                                                                                  :create create})
                                                            :taskQueue task-queue
                                                            :workflowsPath (path/join (toString) "target/workflows.js")}))]
             (reset! worker* worker**)
             (.run worker**))
           worker*)
  :stop (.shutdown @@worker))

(defn run
  []
  (start)
  (promesa/let [connection (.connect Connection)]
    (.workflow.execute (Client. connection) "spam" (clj->js {:taskQueue task-queue
                                                             :workflowId "spam"}))))

(defn main
  [& args]
  (case (first args)
    "email" (println (:client_email google-cloud-credentials))
    "init" (init (last args))
    "run" (run)))