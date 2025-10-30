(ns bridge
  (:require
   [nbb.core :refer [load-file]]))

(.then (load-file "config.cljs")
       clj->js)