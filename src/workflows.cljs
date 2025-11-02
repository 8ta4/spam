(ns workflows
  (:require ["@temporalio/workflow" :refer [proxyActivities]]))

(defn spam
  []
  (-> {:startToCloseTimeout (* 60 1000)}
      clj->js
      proxyActivities
      .orchestrate))