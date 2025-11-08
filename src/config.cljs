{:spreadsheet "<spreadsheet-id>"
 :prompts {:creator {:user (fn [{:keys [date endpoint messages sources]}]
                             (str date endpoint messages sources))
                     :system "You are a world-class sales copywriter specializing in hyper-personalized, concise cold outreach. Your task is to draft a single, compelling message based on the context provided."}
           :judge {:user (fn [{:keys [date endpoint messages sources a b]}]
                           (str date endpoint messages sources a b))
                   :system ""}
           :challenger {:user (fn [{:keys [date endpoint messages sources a b winner critique]}]
                                (str date endpoint messages sources a b winner critique))
                        :system ""}
           :editor {:user (fn [{:keys [date endpoint messages sources message]}]
                            (str date endpoint messages sources message))
                    :system ""}
           :gatekeeper {:user (fn [{:keys [date endpoint messages sources message]}]
                                (str date endpoint messages sources message))
                        :system ""}}}