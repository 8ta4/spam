(ns repl
  (:require
   ["google-spreadsheet" :refer [GoogleSpreadsheet]]
   [main :refer [config load-config schema service-account-auth]]
   [promesa.core :as promesa]))

(defn clean
  []
  (promesa/let [_ (load-config)
                spreadsheet (GoogleSpreadsheet. (:spreadsheet @config) service-account-auth)]
    (.addSheet spreadsheet (clj->js {:title "Sheet1"}))
    (.loadInfo spreadsheet)
    (->> schema
         keys
         (select-keys (js->clj spreadsheet.sheetsByTitle :keywordize-keys true))
         vals
         (run! #(.delete %)))))