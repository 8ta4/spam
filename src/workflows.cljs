(ns workflows
  (:require
   ["@temporalio/workflow" :refer [proxyActivities]]
   [promesa.core :as promesa]))

(def activities
  (proxyActivities (clj->js {:startToCloseTimeout (* 60 1000)})))

(defn spam
  []
  (promesa/let [data (.orchestrate activities)]
    (->> (js->clj data :keywordize-keys true)
         (mapcat :sources)
         distinct
         clj->js)))