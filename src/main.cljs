(ns main
  (:require
   ["nbb" :refer [loadFile]]
   [cljs-node-io.core :as io :refer [slurp spit]]
   [clojure.string :as string :refer [split]]
   [lambdaisland.uri :refer [uri]]
   [promesa.core :as p]))

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
  (comp (partial spit "config.cljs")
        (partial string/replace (slurp "src/config.cljs") "<spreadsheet-id>")
        get-spreadsheet-id))

(defn load-config
  []
  (p/let [js-config (loadFile "src/bridge.cljs")]
    (reset! config (js->clj js-config :keywordize-keys true))))

(defn init
  [url]
  (initialize-config url)
  (load-config))

(defn main
  [& args]
  (case (first args)
    "init" (init (last args))))