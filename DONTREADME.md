# spam

## Goals

### Adversariality

> Do I have to compare and choose from a list of drafts?

No. That's the tool's job. Manually comparing slightly different drafts is a great way to lose your mind.

> Does `spam` generate a fixed number of drafts for each endpoint?

No. The number of drafts generated depends on a competitive tournament designed to stop when the quality of the message peaks.

First, two `creator` agents generate separate drafts, and a `judge` agent compares them to pick the first champion.

That champion then goes into a loop of challenges. In each challenge, a `challenger` agent tries to write a better draft, and a `judge` evaluates its attempt.

The loop stops when one of two things happens:

- The champion wins a challenge. This signals the quality has probably plateaued.

- It hits the maximum of 10 challenges. That's just a safety net to keep it from wasting your time and money.

After the tournament, an `editor` agent performs a single round of conservative edits on the winning draft.

> Does `spam` give a send date to every message it generates?

No. The `gatekeeper` agent is the last step in the workflow. If a message doesn't meet the standard, it'll be rejected instead of being given a send date.

You'll see this decision recorded in the `decision` column of the `runs` sheet. That column will contain one of two things:

- A date.

- The keyword `rejected`.

This makes the `decision` column a sum type.

When you see a date, that's the recommended send date; the tool itself doesn't send anything.

### Context Awareness

> Does this tool scrape pages as an anonymous bot?

No. It uses your own browser's fingerprint to gather context.

### Spreadsheet CRM

> Does this tool use an RDBMS?

No. This tool prioritizes the flexibility of a spreadsheet. If you want to tweak your data or change the schema, you just do it.

> Does this tool integrate with Salesforce?

No. This tool is for the first step of scaling, not an enterprise beast. If you're running your outreach from a spreadsheet, this is for you. If you need Salesforce, you've graduated.

### Resumability

> Will a failed workflow re-run a completed Activity?

No. If a run fails halfway through, the next run will skip what's already done.

### Observability

> How can I see the status of Workflows?

Access the Temporal Web UI at `http://localhost:8233`.

## Architecture

> Is `config.cljs` compiled?

No, it's interpreted.

To prevent a simple error from wasting your time on a long run, the tool first runs a quick smoke test on your `config.cljs`. After you've edited the file, a compiler could add a few seconds of compilation delay before that test could even start.

A config file doesn't need the full power of ClojureScript anyway. The tool uses SCI. While SCI is missing some features of the language, it's not a problem for writing a config file.
