# spam

## Goals

### Adversariality

> Do I have to compare and choose from a list of drafts?

No. That's the tool's job. Manually comparing five slightly different drafts is a great way to lose your mind.

### Context Awareness

> Does this tool scrape pages as an anonymous bot?

No. It uses your own browser's fingerprint to gather context.

### Spreadsheet CRM

> Does this tool use an RDBMS?

No. This tool prioritizes the flexibility of a spreadsheet. If you see a typo in a draft or want to reorganize your schema, you just do it.

> Does this tool integrate with Salesforce?

No. This tool is for the first step of scaling, not an enterprise beast. If you're running your outreach from a spreadsheet, this is for you. If you need Salesforce, you've graduated.

### Resumability

> Will a failed workflow re-run a completed Activity?

No. If a run fails halfway through, the next run will skip what's already done.

### Observability

> How can I see the status of Workflows?

Access the Temporal Web UI at `http://localhost:8233`.
