(ns workflows
  (:require
   ["@temporalio/workflow" :refer [proxyActivities]]
   [promesa.core :as promesa :refer [all]]))

(def activities
  (proxyActivities (clj->js {:startToCloseTimeout (* 60 1000)})))

(defn spam
  []
  (promesa/let [data (.orchestrate activities)]
    (promesa/->> (js->clj data :keywordize-keys true)
                 (mapcat :sources)
                 distinct
                 (map #(.see activities (clj->js %)))
                 all
                 clj->js)))