(ns workflows
  (:require
   ["@temporalio/workflow" :refer [proxyActivities startChild]]
   [com.rpl.specter :refer [ALL transform]]
   [promesa.core :as promesa :refer [all]]))

(def activities
  (proxyActivities (clj->js {:startToCloseTimeout (* 60 1000)})))

(defn generate
  [context]
  context)

(defn spam
  []
  (promesa/let [data (.orchestrate activities)
                sources (->> (js->clj data :keywordize-keys true)
                             (mapcat :sources)
                             distinct)
                source-content (promesa/->> sources
                                            (map #(.see activities (clj->js %)))
                                            all
                                            (zipmap sources))]
    (promesa/->> (js->clj data :keywordize-keys true)
                 (map (fn [context]
                        (startChild "generate" (clj->js {:args [(transform [:sources ALL] source-content context)]
                                                         :workflowId (:endpoint context)}))))
                 all
                 clj->js)))