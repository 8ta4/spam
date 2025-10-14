# spam

## Goals

### Adversariality

> Do I have to compare and choose from a list of drafts?

No. That's the tool's job. Manually comparing slightly different drafts is a great way to lose your mind.

### Context Awareness

> Does this tool scrape pages as an anonymous bot?

No. It uses your own browser's fingerprint to gather context.

### Spreadsheet CRM

> Does this tool use an RDBMS?

No. This tool prioritizes the flexibility of a spreadsheet. If you want to tweak your data or change the schema, you just do it.

> Does this tool integrate with Salesforce?

No. This tool is for the first step of scaling, not an enterprise beast. If you're running your outreach from a spreadsheet, this is for you. If you need Salesforce, you've graduated.

> Can the cells in the `template` column use spreadsheet formulas?

Yes.

The tool reads the value from the cell, not the formula itself. You're free to use functions like `CONCATENATE` to compose your templates from a helper sheet if you want.

I could've shipped this with a helper sheet and forced you to build templates from my pre-defined components. I didn't want to build a restrictive framework I'd end up fighting.

Editing a template in a spreadsheet is easy. Editing the logic in the code is not. If you need to alter the logic, you can fork the repository.

### Resumability

> Will a failed workflow re-run a completed Activity?

No. If a run fails halfway through, the next run will skip what's already done.

### Observability

> How can I see the status of Workflows?

Access the Temporal Web UI at `http://localhost:8233`.
