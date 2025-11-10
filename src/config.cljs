(def background
  "Background: My product is 'spam', a CLI tool that automates outreach for founders. My policy is to never offer discounts.")

{:spreadsheet "<spreadsheet-id>"
 :prompts {:creator {:user (fn [{:keys [date endpoint messages sources]}]
                             (str background "\n"
                                  "Date: " date "\n"
                                  "Prospect Contact: " endpoint "\n"
                                  "Message History: " messages "\n"
                                  "Prospect Research: " sources))
                     :system "As a founder, write a message to maximize the response rate."}
           :judge {:user (fn [{:keys [date endpoint messages sources a b]}]
                           (str "Date: " date "\n"
                                "Prospect Contact: " endpoint "\n"
                                "Message History: " messages "\n"
                                "Prospect Research: " sources "\n"
                                "Message A: " a "\n"
                                "Message B: " b))
                   :system "As the recipient, first bluntly critique both messages, then decide which you are more likely to reply to."}
           :challenger {:user (fn [{:keys [date endpoint messages sources a b winner critique]}]
                                (str background "\n"
                                     "Date: " date "\n"
                                     "Prospect Contact: " endpoint "\n"
                                     "Message History: " messages "\n"
                                     "Prospect Research: " sources "\n"
                                     "Message A: " a "\n"
                                     "Message B: " b "\n"
                                     "Winner: " winner "\n"
                                     "Critique: " critique))
                        :system "As a founder, write a message designed to achieve a higher response rate than the winner."}
           :editor {:user (fn [{:keys [date endpoint messages sources message]}]
                            (str background "\n"
                                 "Date: " date "\n"
                                 "Prospect Contact: " endpoint "\n"
                                 "Message History: " messages "\n"
                                 "Prospect Research: " sources "\n"
                                 "Draft: " message))
                    :system "As a fact-checker, edit the message to ensure it complies with the background, the message history, and the prospect research."}
           :gatekeeper {:user (fn [{:keys [date endpoint messages sources message]}]
                                (str background "\n"
                                     "Date: " date "\n"
                                     "Prospect Contact: " endpoint "\n"
                                     "Message History: " messages "\n"
                                     "Prospect Research: " sources "\n"
                                     "Final Message: " message))
                        :system "As an editor-in-chief with a bias for rejection, first explain your decision based on any factual, strategic, or tonal errors, then approve or reject it."}}}
