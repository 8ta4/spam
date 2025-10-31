(ns main
  (:require
   ["google-auth-library" :refer [JWT]]
   ["google-spreadsheet" :refer [GoogleSpreadsheet]]
   ["nbb" :refer [loadFile]]
   ["os" :refer [homedir]]
   ["path" :refer [join]]
   [cljs-node-io.core :refer [slurp spit]]
   [clojure.string :as string :refer [split]]
   [core :refer [path]]
   [flatland.ordered.map :refer [ordered-map]]
   [lambdaisland.uri :refer [uri]]
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

(defn init
  [url]
  (initialize-config url)
  (promesa/do (load-config)
              (promesa/let [spreadsheet (GoogleSpreadsheet. (:spreadsheet @config) service-account-auth)]
                (dorun (map (fn [[k v]] (.addSheet spreadsheet (clj->js {:headerValues v :title k}))) schema)))))

(defn main
  [& args]
  (case (first args)
    "email" (println (:client_email google-cloud-credentials))
    "init" (init (last args))))