(ns workflows
  (:require
   ["@temporalio/workflow" :refer [proxyActivities executeChild]]
   [cats.builtin]
   [cats.core :refer [<*>]]
   [com.rpl.specter :refer [ALL transform]]
   [promesa.core :as promesa :refer [all]]))

(def activities
  (proxyActivities (clj->js {:startToCloseTimeout (* 60 1000)})))

(def get-winning-message
  (<*> (comp keyword :winner) identity))

(defn run-round
  [context round]
  (promesa/let [champion (get-winning-message context)
                challenger (.challenge activities (clj->js context))
                toss (.toss activities)
                context* (merge (select-keys context #{:date :endpoint :messages :sources}) (if toss
                                                                                              {:a champion
                                                                                               :b challenger}
                                                                                              {:a challenger
                                                                                               :b champion}))
                judgment (.judge activities (clj->js context*))
                context** (merge context* (js->clj judgment :keywordize-keys true))]
    (cond (= champion (get-winning-message context**)) champion
          (= round 9) (get-winning-message context**)
          :else (promesa/recur context** (inc round)))))

(defn generate
  [context]
  (promesa/let [[a b] (all (map #(.create activities %) (repeat 2 context)))
                context* (merge (js->clj context :keywordize-keys true) {:a a
                                                                         :b b})
                judgment (.judge activities (clj->js context*))]
    (run-round (merge context* (js->clj judgment :keywordize-keys true)) 0)))

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