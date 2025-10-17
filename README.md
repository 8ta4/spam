# spam

## Spam Well Done

> What is `spam`?

`spam` is a CLI that generates outreach messages into a spreadsheet from your context.

Let's be honest: cold outreach is spam. Most of it is tasteless. Let's cook up spam that's too good to ignore.

## Setup

> How do I set up `spam`?

1. Make sure you're using a Mac with Apple silicon.

1. Install [Homebrew](https://brew.sh/#install).

1. Open a terminal.

1. Run this command:
   ```bash
   brew install 8ta4/spam/spam
   ```

1. Run this command:
   ```bash
   mkdir -p ~/.config/spam/
   ```

1. Download a Service Account key from the Google Cloud Console.

1. Rename the file you downloaded to `google-cloud.json`.

1. Move that file to the `~/.config/spam/` directory.

1. Copy an API key from the Google AI Studio website.

1. Run this command:
   ```bash
   pbpaste > ~/.config/spam/google-ai-studio
   ```

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

Your `config.cljs` file's job is to return a single JavaScript object. You edit the prompts by changing the function that's assigned to the `prompt` property.

This function receives a JavaScript object. Your job is to check the `agent` property on that object and return the right prompt string. The `config.cljs` file that `spam init` creates for you comes with a complete example showing every property available for each agent.

> Where do I change the spreadsheet URL?

Your `config.cljs` file's job is to return a single JavaScript object. The URL is the value of the `spreadsheet` property in that object.

The `spam init` command creates a new spreadsheet and fills this in for you automatically.
