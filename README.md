# spam

## Spam Well Done

Let's be honest: cold outreach is spam. Most of it is tasteless. Let's cook up spam that's too good to ignore.

## Usage

> What's the `spam` subcommand for creating a new spreadsheet?

The subcommand is `init`.

The full command is `spam init`. It spits out a new spreadsheet with the exact schema the tool expects.

> What is the `spam` subcommand for running the generation Workflows?

The subcommand is `run`.

The full command looks like this: `spam run <URL>`. `<URL>` is the URL of the spreadsheet you want to process.

> Where do I see the generated messages?

Look in the `message` column of the `messages` sheet. `spam` finds the blank message cells for any endpoint you've specified and fills them in for you.

> Can an endpoint be an email address?

Yes. An endpoint is typically an email address or a URL pointing to something like a LinkedIn profile or a contact form.

> Where do I specify which endpoints to generate messages for?

You specify endpoints in the `messages` sheet by adding a new row and filling in the `endpoint` column.

For a message to be generated for that `endpoint`, two conditions have to be met:

- The `endpoints` sheet must have at least one row where your `endpoint` and its corresponding `prospect` are both filled in.

- The `contexts` sheet must have at least one row where that same `prospect` and a `context` are both filled in.
