# Lab 0: Plan Mode Warm-Up

## Objective

Get comfortable with Codex CLI's current default interaction model before diving into the coding labs. Since v0.94, Codex operates in **Plan mode** by default — it proposes a plan before executing. This exercise teaches you to read, modify, and approve plans effectively.

**Time:** 15–20 minutes

## Background: What Changed?

If you've used Codex before, you'll notice three important defaults that changed in early 2026:

| Feature | Old Behavior | New Default |
|---------|-------------|-------------|
| **Plan mode** (v0.94) | Codex executes immediately | Codex proposes a plan first, waits for approval |
| **Fast mode** (v0.111) | Standard speed | Faster output enabled by default |
| **Steer mode** (v0.98) | Enter always submits new prompt | During tasks: Enter sends immediately, Tab queues follow-up |

## Setup

```bash
cd exercises/plan-mode-warmup/starter
```

Verify you have Python and pytest available:

```bash
python --version   # 3.9+
pip install pytest  # if not already installed
```

## Part 1: Your First Plan (5 min)

### Step 1: Start Codex and observe Plan mode

```bash
codex
```

Paste this prompt:

```
Harden inventory.py. Keep remove_item safe when the item doesn't
exist, and add validation for negative quantities, prices, discounts,
and restock values.
```

**What to observe:**
- Codex will display a **plan** before making any changes
- The plan describes what it intends to do, file by file
- You'll see an approval prompt — don't approve yet!

### Step 2: Read the plan critically

Before approving, ask yourself:
- Does the plan address both bugs?
- Does it mention the right files?
- Is the approach reasonable?

### Step 3: Approve or modify

You have three options at the approval prompt:
- **Approve** — accept the plan as-is
- **Reject** — decline and provide different guidance
- **Modify** — add constraints or redirect the approach

Try approving the plan and watch Codex execute it.

### Step 4: Verify the fix

```bash
python -c "
from inventory import remove_item, apply_discount, add_item
inv = {}
add_item(inv, 'Widget', 10, 100.0)

# Test 1: remove_item should handle missing items gracefully
try:
    remove_item(inv, 'NonExistent')
    print('remove_item: FIXED')
except KeyError:
    print('remove_item: STILL BROKEN')

# Test 2: apply_discount(50) should mean 50%
apply_discount(inv, 'Widget', 50)
expected = 50.0
actual = inv['Widget']['price']
print(f'apply_discount: price={actual} (expected {expected}) — {\"FIXED\" if actual == expected else \"STILL BROKEN\"}')
"
```

## Part 2: Steer Mode (5 min)

### Step 5: Steer a running task

Start a new task — this time something bigger:

```
Add comprehensive tests to test_inventory.py. Cover all functions
including remove_item, apply_discount, and restock. Include edge
cases like empty inventory and negative quantities.
```

**While Codex is working**, try these steer-mode interactions:

- **Enter** — sends a follow-up message immediately (Codex sees it during execution)
- **Tab** — queues your message to send after the current step finishes

Try steering mid-execution:

```
Also add a test for generate_report with an empty inventory
```

**What to observe:**
- Your steer message influences the ongoing task
- Codex incorporates your feedback without starting over

### Step 6: Run the tests

```bash
pytest -v
```

All tests should pass. If any fail, use Codex to fix them:

```
Fix the failing tests
```

## Part 3: Key Slash Commands (5 min)

### Step 7: Explore new commands

Try each of these slash commands inside Codex:

| Command | What It Does | Try It |
|---------|-------------|--------|
| `/plan` | Toggle plan mode on/off | Toggle off, ask Codex to do something, notice it skips the plan step |
| `/fast` | Toggle fast/standard mode | Toggle and compare output speed |
| `/copy` | Copy the last assistant reply | Useful for pasting plans or code into other tools |
| `/theme` | Pick a syntax highlighting theme | Browse themes and pick one you like |
| `/debug-config` | Show your effective config | See what model, personality, and settings are active |
| `/permissions` | View/manage sandbox permissions | See what Codex is currently allowed to do |

### Step 8: Try Plan mode off

Toggle plan mode off with `/plan`, then ask:

```
Add type hints to all functions in inventory.py
```

**What to observe:**
- Without plan mode, Codex executes directly (the old behavior)
- Faster for simple tasks, but less control over complex ones
- Toggle it back on with `/plan` when you want review control

## Part 4: Personality and Fast Mode (3 min)

### Step 9: Check your configuration

Run `/debug-config` and note these defaults:
- **personality**: `friendly` (also try `pragmatic` or `none`)
- **fast mode**: should show enabled
- **model**: should show `gpt-5.4`

### Step 10: Compare fast vs standard

Toggle fast mode off with `/fast`, then ask:

```
Write a docstring for the generate_report function explaining the output format
```

Toggle it back on with `/fast` and ask the same thing. Notice the speed difference — fast mode uses the same model but with faster output.

## Success Criteria

- [ ] You've approved (or rejected) a plan from Codex
- [ ] You've steered a running task with a follow-up message
- [ ] You've used at least 3 of the new slash commands
- [ ] All tests pass (`pytest -v`)
- [ ] Both bugs in inventory.py are fixed
- [ ] You've toggled plan mode and fast mode on and off

## Key Takeaways

1. **Plan mode** gives you a review step before Codex modifies code — leave it on for complex work, toggle off for quick fixes
2. **Steer mode** lets you redirect Codex mid-task instead of waiting and re-prompting
3. **Fast mode** speeds up output with no capability trade-off
4. **`/debug-config`** is your friend for troubleshooting unexpected behavior

You're now ready for Lab 1!
