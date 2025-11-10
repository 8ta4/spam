(ns workflows
  (:require
   ["@temporalio/workflow" :refer [executeChild proxyActivities]]
   [cats.builtin]
   [cats.core :refer [<*>]]
   [com.rpl.specter :refer [ALL setval transform]]
   [promesa.core :as promesa :refer [all]]))

(def activities
  (proxyActivities (clj->js {:startToCloseTimeout (* 60 1000)})))

(def get-winning-message
  (<*> (comp keyword :winner) identity))

(defn run-round
  [round context]
  (promesa/let [champion (get-winning-message context)
                challenger (.challenge activities (clj->js context))
                toss (.toss activities)
                context* (merge (select-keys context #{:endpoint :messages :sources}) (if toss
                                                                                        {:a champion
                                                                                         :b challenger}
                                                                                        {:a challenger
                                                                                         :b champion}))
                judgment (.judge activities (clj->js context*))
                context** (merge context* (js->clj judgment :keywordize-keys true))]
    (cond (= champion (get-winning-message context**)) champion
          (= round 9) (get-winning-message context**)
          :else (run-round (inc round) context**))))

(defn generate
  [context]
  (promesa/let [[a b] (all (map #(.create activities %) (repeat 2 context)))
                context* (merge (js->clj context :keywordize-keys true) {:a a
                                                                         :b b})
                judgment (.judge activities (clj->js context*))
                winning-message (run-round 0 (merge context* (js->clj judgment :keywordize-keys true)))
                edited-message (->> (js->clj context :keywordize-keys true)
                                    (setval :message winning-message)
                                    clj->js
                                    (.edit activities))
                decision (->> (js->clj context :keywordize-keys true)
                              (setval :message edited-message)
                              clj->js
                              (.gatekeep activities))]
    (->> (js->clj decision :keywordize-keys true)
         (merge {:endpoint (:endpoint (js->clj context :keywordize-keys true))
                 :message edited-message})
         clj->js
         (.save activities))))

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