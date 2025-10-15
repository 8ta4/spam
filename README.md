# spam

## Spam Well Done

Let's be honest: cold outreach is spam. Most of it is tasteless. Let's cook up spam that's too good to ignore.

## Usage

> What's the `spam` subcommand for creating a spreadsheet?

The subcommand is `init`.

The full command is `spam init`. It creates your spreadsheet and a local project with your `config.cljs` file.

> What is the `spam` subcommand for running the generation Workflows?

The subcommand is `run`.

The full command looks like this: `spam run`.

> Where do I see the generated messages?

Look in the `message` column of the `messages` sheet. `spam` finds the blank message cells for any endpoint you've specified and fills them in for you.

> Can an endpoint be an email address?

Yes. An endpoint is typically an email address or a URL pointing to something like a LinkedIn profile or a contact form.

> Where do I specify which endpoints to generate messages for?

You specify endpoints in the `messages` sheet by adding a new row and filling in the `endpoint` column.

For a message to be generated for that `endpoint`, two conditions have to be met:

- The `endpoints` sheet must have at least one row where your `endpoint` and its corresponding `prospect` are both filled in.

- The `contexts` sheet must have at least one row where that same `prospect` and a `context` are both filled in.

> Where do I edit the prompts for the agents?

You edit the prompts for each agent in your `config.cljs` file.

The file comes with a function for generating prompts. It receives a map of data, which includes an `:agent` key. Your job is to check that key and return the right prompt string. The function is populated with examples showing every key available for each agent.

