(ns main
  (:require ["nbb" :refer [loadFile]]))

(defn main
  [])

(defn load-config
  []
  (loadFile "config.cljs"))