{:spreadsheet "<spreadsheet-id>"
 :prompts {:creator {:user (fn [{:keys [date endpoint messages sources]}]
                             (str date endpoint messages sources))
                     :system "You are a world-class sales copywriter specializing in hyper-personalized, concise cold outreach. Your task is to draft a single, compelling message based on the context provided."}}}