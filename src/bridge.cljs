(ns bridge
  (:require
   [core :refer [path]]
   [nbb.core :refer [load-file]]))

(.then (load-file path)
       clj->js)