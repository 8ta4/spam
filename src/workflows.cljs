(ns workflows
  (:require
   ["@temporalio/workflow" :refer [proxyActivities]]
   [com.rpl.specter :refer [ALL transform]]
   [promesa.core :as promesa :refer [all]]))

(def activities
  (proxyActivities (clj->js {:startToCloseTimeout (* 60 1000)})))

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
    (clj->js (transform [ALL :sources ALL] source-content (js->clj data :keywordize-keys true)))))