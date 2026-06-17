# Task 1: Designing a 5-Minute Engagement Loop — "Magic Doodle"

## The activity

Magic Doodle is a free-drawing canvas built for a single sitting: a child opens
the app, paints with their finger, and the session naturally closes itself
after about five minutes with a small celebration. There is no pencil-control
tutorial, no "right" way to draw, and no save-vs-discard decision to make —
the loop is built to feel like a complete little ritual rather than an
open-ended tool.

It's the activity built out in Task 2 (see the Doodle screen in the app),
scaled here to the full five-minute version this design describes.

## Why a 3-year-old enjoys it

At three, fine motor control is still developing, attention span is short,
and the biggest source of delight is *cause and effect that they control*.
Magic Doodle is built entirely around that:

- **Immediate, generous feedback.** Every touch produces color and movement
instantly — there's no possible "mistake," because a scribble is just as
visually rewarding as a careful line. This matters because a 3-year-old's
fine motor skills can't yet produce intentional shapes, so a tool that only
rewards precision would feel like failure within seconds.
- **Sensory delight over goals.** Tapping a new color, then dragging a
finger and watching sparkles trail behind it, taps into the same joy as
splashing in a puddle. There's a visible, audible "pop" of feedback (a soft
chime on the first stroke, a tiny pop sound on each color tap) that keeps
attention anchored without requiring reading or instructions.
- **No wrong answers, ever.** Toddlers this age are extremely sensitive to
implied failure. Every interaction path in this loop leads to praise.
- **Repetition with variation.** A 3-year-old will happily tap the same
color swatch ten times in a row just to hear the pop again — the loop
tolerates and even rewards that kind of repetitive exploration rather than
pushing them toward "progress."

## The five-minute arc

1. **0:00 – Invitation (0–15s).** The screen opens already colorful and
inviting — no menus, no setup. A single tap anywhere starts drawing
immediately, so there's zero friction between opening the app and the first
moment of delight.
2. **Warm-up (15s–2min).** The child experiments with colors and lines. This
is the "sensory" phase — mostly scribbling, switching colors often, maybe
clearing and starting again. Sound and color-swatch feedback are the main
engagement drivers here.
3. **Discovery / climax (2–4min).** This is where "Sparkle Mode" gets
introduced or tapped — a clear escalation that rewards continued attention
with something *new* rather than more of the same. The sparkle trail turns
ordinary scribbling into something that feels a little bit magical, which
re-engages attention right as the novelty of plain drawing would start to
fade.
4. **Wind-down (4–4:45min).** A subtle visual or audio cue (e.g., the timer
chip turning a warmer color, or a gentle two-tone chime) signals the session
is coming to a close — not as a countdown-induced anxiety trigger, but as a
gentle "almost done" cue, similar to a parent saying "two more minutes."
5. **Closure ritual (4:45–5:00min).** Drawing freezes, confetti falls, and a
sticker is "earned" with a short celebratory phrase read aloud by tone
(jingle) and text. This is the emotional payoff of the whole loop.

## What happens when time runs out

When the timer reaches zero, the canvas locks (further taps don't draw) and
a full-screen reward appears: confetti animation, a cheerful four-note
jingle, a sticker icon, and a simple, validating message ("Your masterpiece
is done! You earned a sticker for drawing today."). There is exactly one
button — a big, friendly "Yay!" — which returns the child to the home
screen. The deliberate choices here:

- **No "play again" loop on this screen.** Adding a "draw again" button
would turn a closure ritual into a decision point, which both extends screen
time past the intended five minutes and asks a 3-year-old to make a choice
they're not well-equipped to make calmly (this is usually where tantrums
about "one more time" start). If they want to draw again, they can
re-open the activity from the home screen — a small, deliberate friction
that keeps a parent in the loop of approving another session.
- **The drawing isn't lost dramatically — it's "finished."** Children this
age don't yet have a strong sense of an artifact's permanence, but they do
respond strongly to praise. Framing the end of the timer as "your
masterpiece is done" rather than "time's up, stop" reframes an externally
imposed limit as an accomplishment.
- **A consistent reward symbol (sticker) across activities.** The same
sticker-and-confetti pattern recurs across Sprout's mini-activities (and
in the Photo Hunt activity built in Task 4), so children quickly learn what
"I did it" feels like across very different tasks, building a sense of
mastery over the whole app rather than just one game.

## Sketch (loop overview)

```
   [Open]                                 
     |                                     
     v                                     
 [Invitation: tap to draw] --warm-up--> [Sparkle Mode escalation]
                                              |
                                       wind-down cue (~4:45)
                                              |
                                              v
                                   [Canvas locks, confetti + jingle]
                                              |
                                              v
                                  [Sticker earned + "Yay!" button]
                                              |
                                              v
                                        [Back to Home]
```
