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

> Does this tool use the OAuth login?

No. It uses a Service Account.

The typical OAuth flow is for me to embed an official app's client ID. That's a non-starter. One bad actor could get that client ID banned, and the tool could die for everyone.

The solution to that is for me to run a central server to manage the client ID. I rejected that too. It makes the tool dependent on me. It introduces a single point of failure.

Another alternative is for you to provide your client ID. This puts you in a bind. You're either stuck in testing mode, where refresh tokens are designed to expire, or you wait for their verification process, which can take forever.

So that leaves Service Accounts.

> Does this tool require a Google Workspace account?

No. I wanted this tool to work with a free Google account, so you don't need a Google Workspace account to try it out or to develop with it.

The trade-off for this is a more manual setup because I haven't found a way for a new Service Account to create a spreadsheet under a free account.

> Is `config.cljs` compiled?

No, it's interpreted.

To prevent a simple error from wasting your time on a long run, `spam run` first runs a quick smoke test on your `config.cljs`. After you've edited the file, a compiler could add a few seconds of compilation delay before that test could even start.

The tool uses SCI to get around that. Now, the trade-off is that some ClojureScript libraries don't work with SCI... sigh.

But a config file doesn't need the full power of ClojureScript anyway. While SCI is missing some features of the language, it's not a problem for writing a config file.

> Does a single Activity make multiple LLM calls?

No. Each LLM call gets its own Activity.

An LLM call takes time, and it might cost money. If you have two calls in one Activity and the second one fails, a standard retry would run the entire Activity. That means you're wasting time re-running the first call, and you're potentially paying for it twice.

> Is the prompt passed as an argument to an Activity that calls an LLM?

No. The Activity reads the `config.cljs` file when it needs the prompt.

The whole point is to keep iteration low-friction. I want to be able to tweak a prompt in the config file and have the system pick it up.

The audit trail is Git. Temporal gives us the timestamp of the run. If something goes wrong, I can use the timestamp to `git checkout` the exact version of the codebase.

## Agents

> Can I use a template for the prompt?

Yes. Your `config.cljs` is a program, not just a config file. If you want a template, you can write a function for it. You get the power of a language, not the constraints of some template syntax.

> Do I have to choose the LLM model?

No. You don't have to choose the LLM model or other settings like temperature. So you can focus on what matters: the prompts.

> Do the agents search the web?

No. Letting an agent search introduces uncertainty. The tool is designed to work only from the source you provide.

> Do the agents use function calling?

No. Function calling is a source of uncertainty I don't want.

For example, I could've given agents a function to query the spreadsheet for more context. But that's a risk. The last thing I need is an agent pulling data from the wrong prospect's row.

> Do the `creator` agents see each other's drafts?

No. I could've made them reference each other's drafts. But that creates a mutually dependent process. Keeping them separate means the two `creator` Activities can run in parallel.

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

> Can a message win the tournament based on a concept that violates my constraints, only to be corrected by the `editor` afterward?

Yes. That can happen. It's a known vulnerability in the architecture.

Here's how it plays out:

1. Your constraint is to offer no discount.

1. A `creator` generates a message that offers a discount.

1. The `judge`, which is blind to your constraints, sees the discount as a compelling offer and selects that message over a compliant one.

1. That message goes on to win the tournament.

1. The post-tournament `editor` sees the violation and removes the discount to make the message compliant.

1. You're left with a compliant message, but its winning concept has been gutted.

Now, I could have designed the system differently. An alternative would be to run an `editor` before the `judge` sees it. That would fix this vulnerability.

I chose not to do that for one reason: cost. The alternative adds an extra LLM call before every judgment.

> Does the `judge` vote multiple times per challenge?

No. The `judge` makes a single call for each challenge.

Imagine the `judge` repeatedly selects between the same two messages, `a` and `b`. The long-run frequency with which it picks `a` is `p`.

First, when the messages are of similar quality, `p` is close to $\frac{1}{2}$. The risk of selecting the slightly weaker message is high, but its impact is negligible.

Second, when one message is clearly superior, `p` is far from $\frac{1}{2}$. The impact of such an error would be high because a significant improvement would be lost, but the event itself is unlikely.

The choice is whether to pay the fixed cost of a majority vote on every challenge. That's just insurance against a low-probability, high-impact error. I'd rather accept the small risk than pay the insurance premium.

> Can the `challenger` use the `judge`'s critique?

Yes. A `challenger` can use the results of the preceding round.

But the `challenger` doesn't get the history from any earlier rounds. The `challenger`'s job is kept simple: beat the last winner based on the latest results.

> Does the `gatekeeper` edit the message?

No. I keep the `gatekeeper` and `editor` separate.

If I merged the two roles, the context for that final decision would be contaminated. The agent would have the original, pre-edited draft it was asked to fix, and potentially its own internal chain-of-thought about the edits.
