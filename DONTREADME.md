# spam

## Goals

### Cost

> What's the cost goal?

The goal is $0.10 a message.

This tool is for spam well done, which should be able to hit a 1% conversion rate. At that rate, you send 100 messages to get one customer. At $0.10 a message, you're spending $10 on this tool to land that one customer. If a customer is worth $1,000 to you, that leaves plenty of room for the rest of your sales costs.

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

To prevent a simple error from wasting your time on a long run, `spam run` first runs a quick smoke test on your `config.cljs`. After you've edited the file, a compiler could add a few seconds of compilation delay before that test could even start.

The tool uses SCI to get around that. Now, the trade-off is that some ClojureScript libraries don't work with SCI... sigh.

But a config file doesn't need the full power of ClojureScript anyway. While SCI is missing some features of the language, it's not a problem for writing a config file.

## Agents

> Can I use a template for the prompt?

Yes. Your `config.cljs` is a program, not just a config file. If you want a template, you can write a function for it. You get the power of a language, not the constraints of some template syntax.

> Can the agents search the web?

No. Letting an agent search introduces uncertainty. The tool is designed to work only from the context you provide.

> Does the tool shuffle the champion message and the challenger message before the `judge` compares them?

Yes.

Position bias is a known phenomenon where one input position can have an advantage.

A message might still get lucky and win a round if it lands in the favored spot. Shuffling doesn't stop that. But shuffling prevents a positional advantage from systematically favoring either the champion or the challenger.

Shuffling gives you a guarantee: The final message is either the result of two consecutive wins in a positionally unbiased competition, or the survivor of the 10-challenge tournament.

> Does the `judge` edit the message?

No.

The `judge`'s role is to simulate the recipient. A recipient is blind to your intent behind the message.

The `editor`'s role is to refine the message to align with that intent.

If I gave the `judge` your intent, it could corrupt its simulation of the recipient.

> Does the `judge` vote multiple times per challenge?

No. The `judge` makes a single call for each challenge.

Imagine the `judge` repeatedly selects between the same two messages, `a` and `b`. The long-run frequency with which it picks `a` is `p`.

First, when the messages are of similar quality, `p` is close to $\frac{1}{2}$. The risk of selecting the slightly weaker message is high, but its impact is negligible.

Second, when one message is clearly superior, `p` is far from $\frac{1}{2}$. The impact of such an error would be high because a significant improvement would be lost, but the event itself is unlikely.

The choice is whether to pay the fixed cost of a majority vote on every challenge. That's just insurance against a low-probability, high-impact error. I'd rather accept the small risk than pay the insurance premium.
