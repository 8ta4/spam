(ns main
  (:require ["nbb" :refer [loadFile]]
            [clojure.string :refer [split]]
            [lambdaisland.uri :refer [uri]]))

(defn get-spreadsheet-id
  [url]
  (-> url
      uri
      :path
      (split "/")
      (nth 3)))

(defn init
  [url])

(defn main
  [& args]
  (case (first args)
    "init" (init (last args))))

(defn load-config
  []
  (loadFile "config.cljs"))