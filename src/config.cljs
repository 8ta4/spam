{:spreadsheet "<spreadsheet-id>"
 :prompts {:creator {:user (fn [{:keys [date endpoint messages sources]}]
                             (str date endpoint messages sources))
                     :system ""}}}