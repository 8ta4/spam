(ns bridge
  (:require
   [clojure.walk :refer [postwalk]]
   [core :refer [path]]
   [nbb.core :refer [load-file]]))

(defn adapt
  [x]
  (if (fn? x)
    (comp x #(js->clj % :keywordize-keys true))
    x))

(.then (load-file path)
       (comp clj->js
             (partial postwalk adapt)))