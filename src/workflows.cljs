(ns workflows
  (:require
   ["@temporalio/workflow" :refer [proxyActivities]]
   [promesa.core :as promesa :refer [all]]))

(def activities
  (proxyActivities (clj->js {:startToCloseTimeout (* 60 1000)})))

(defn spam
  []
  (promesa/let [data (.orchestrate activities)
                sources (->> (js->clj data :keywordize-keys true)
                             (mapcat :sources)
                             distinct)]
    (promesa/->> sources
                 (map #(.see activities (clj->js %)))
                 all
                 (zipmap sources)
                 clj->js)))