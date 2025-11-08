(ns bridge
  (:require
   [clojure.walk :refer [postwalk]]
   [core :refer [path]]
   [nbb.core :refer [load-file]]
   [promesa.core :as promesa]))

(defn adapt
  [x]
  (if (fn? x)
    (comp x #(js->clj % :keywordize-keys true))
    x))

(defn marshall
  [path*]
  (promesa/->> path*
               load-file
               (postwalk adapt)
               clj->js))

(marshall path)
