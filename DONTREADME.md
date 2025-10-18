# spam

## Goals

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

> Can I use a template for the prompt?

Yes. Your `config.cljs` is a program, not just a config file. If you want a template, you can write a function for it. You get the power of a language, not the constraints of some template syntax.

> Can the agents search the web?

No. Letting an agent search introduces uncertainty. The tool is designed to work only from the context you provide.
