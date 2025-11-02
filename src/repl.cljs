(ns repl
  (:require
   [main :refer [get-spreadsheet schema]]
   [promesa.core :as promesa]))

(defn clean
  []
  (promesa/let [spreadsheet (get-spreadsheet)]
    (.addSheet spreadsheet (clj->js {:title "Sheet1"}))
    (->> schema
         keys
         (select-keys (js->clj spreadsheet.sheetsByTitle :keywordize-keys true))
         vals
         (run! #(.delete %)))))