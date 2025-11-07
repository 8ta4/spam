(ns workflows
  (:require
   ["@temporalio/workflow" :refer [proxyActivities executeChild]]
   [com.rpl.specter :refer [ALL transform]]
   [promesa.core :as promesa :refer [all]]))

(def activities
  (proxyActivities (clj->js {:startToCloseTimeout (* 60 1000)})))

(defn generate
  [context]
  (promesa/let [[a b] (all (map #(.create activities %) (repeat 2 context)))]
    (.judge activities (clj->js (merge (js->clj context) {:a a
                                                          :b b})))))

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
                        (executeChild "generate"
                                      (clj->js {:args [(transform [:sources ALL] source-content context)]
                                                :workflowId (:endpoint context)}))))
                 all
                 clj->js)))