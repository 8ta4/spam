(ns main
  (:require ["nbb" :refer [loadFile]]))

(defn init
  [url])

(defn main
  [& args]
  (case (first args)
    "init" (init (last args))))

(defn load-config
  []
  (loadFile "config.cljs"))