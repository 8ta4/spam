(ns main
  (:require
   ["nbb" :refer [loadFile]]
   [cljs-node-io.core :as io :refer [slurp spit]]
   [clojure.string :as string :refer [split]]
   [lambdaisland.uri :refer [uri]]))

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

(defn init
  [url]
  (initialize-config url))

(defn main
  [& args]
  (case (first args)
    "init" (init (last args))))

(defn load-config
  []
  (loadFile "src/bridge.cljs"))