# spam

## Spam Well Done

> What is `spam`?

`spam` is a CLI that generates outreach messages into a spreadsheet from your source.

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

> How do I start a new project?

1. Run the following command to copy your Service Account's email to the clipboard.
   ```bash
   spam email
   ```

1. Create a new spreadsheet.

1. Share the spreadsheet with the email you copied as an Editor.

1. Run the following command to initialize your project, providing your spreadsheet's URL as the argument.
   ```bash
   spam init <URL>
   ```

> What is the `spam` subcommand for running the generation Workflows?

The subcommand is `run`.

The full command is `spam run`.

> Where do I see the generated messages?

Look in the `message` column of the `runs` sheet. `spam` finds the blank `message` cells for any endpoint you've specified and fills them in for you.

> Can an endpoint be an email address?

Yes. An endpoint is typically an email address or a URL pointing to something like a LinkedIn profile or a contact form.

> Where do I specify which endpoints to generate messages for?

You specify endpoints in the `runs` sheet by adding a new row and filling in the `endpoint` column.

For a message to be generated for that `endpoint`, two conditions have to be met:

- The `endpoints` sheet must have at least one row where your `endpoint` and its corresponding `prospect` are both filled in.

- The `sources` sheet must have at least one row where that same `prospect` and a `source` are both filled in.

> In which file do I edit the prompts for the agents?

You edit them in `config.cljs`.

The `spam init` command creates this file. Its job is to return a map as its final expression, which acts as the configuration for your project.

> Can I edit the system prompts?

Yes.

Inside the map that your `config.cljs` file returns, you're looking for the `:prompts` key. Its value is another map, where each key is the name of an agent, like `:creator` or `:judge`. You edit the `:system` key for that agent, which expects a string.

> Can I edit the user prompts?

Yes.

The user prompt is right next to the system prompt. Inside the map for a specific agent, you'll find the `:user` key. This key expects a function. This function receives a map. Your job is to use that map to return a prompt string. The `config.cljs` file that `spam init` creates for you comes with a complete example showing every key available for each agent.

> Do I have to choose from a list of drafts?

No. That's the tool's job. Manually comparing slightly different drafts is a great way to lose your mind.

> Does `spam` generate a fixed number of drafts for each endpoint?

No. The number of drafts generated depends on a competitive tournament designed to stop when the quality of the message peaks.

First, two `creator` agents generate separate drafts, and a `judge` agent compares them to pick the first champion.

That champion then goes into a loop of challenges. In each challenge, a `challenger` agent tries to write a better draft, and a `judge` evaluates its attempt.

The loop stops when one of two things happens:

- The champion wins a challenge. This signals the quality has probably plateaued.

- It hits the maximum of 10 challenges. That's just a safety net to keep it from wasting your time and money.

After the tournament, an `editor` agent performs a single round of conservative edits on the winning draft.

> Does this tool scrape pages as an anonymous bot?

No. It delegates source gathering to [`see`](https://github.com/8ta4/see). `see` uses your own browser's fingerprint to gather source.

You don't need to run `see` yourself. `spam` calls `see` automatically for you, right when it's needed to gather context.

> Does `spam` send the messages?

No. Sending the messages is your job.

> Do I have to send every message the tool generates?

No. The `gatekeeper` agent is the final check. You'll see its decision in the `approved` column of the `runs` sheet. The `reason` column will tell you why the `gatekeeper` made its call.

> Where do I change the spreadsheet URL?

Your `config.cljs` file's job is to return a map as its final expression. The URL is the value of the `:spreadsheet` key in that map.

The `spam init` command creates a new spreadsheet and fills this in for you automatically.
