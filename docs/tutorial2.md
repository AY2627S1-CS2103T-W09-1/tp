# Tutorial 2: adding a command (exercise only)

This branch completes the AB3 adding-a-command exercise. It is not a proposed
CupidMaxxing feature and must not be merged into the product branch.

## Usage on this tutorial branch

- `remark 1 r/Likes to swim` replaces the first displayed client's note.
- `remark 1 r/` clears the note. As in the tutorial, `remark 1` also clears it.
- The index refers to the currently displayed list, including filtered results.
- Repeated `r/` arguments are rejected instead of silently discarding a value.
- Notes are shown on the client card and retained when saving and restarting.
- Existing JSON without a remark loads with an empty note. Do not delete real data.
- Editing another client field retains the note and the client's dating fields.

Here `r/` belongs to the separate `remark` command. In the existing `add`, `edit`
and `find` commands it continues to mean religion.

## Implementation walkthrough

1. `AddressBookParser` dispatches the command to `RemarkCommandParser`.
2. The parser extracts an index and a `Remark` value.
3. `RemarkCommand` checks the displayed index and replaces the immutable `Person`,
   preserving all other characteristics, criteria and tags.
4. `PersonCard` displays the note; `JsonAdaptedPerson` saves and restores it.
5. Integration tests exercise parsing, filtered indices, clearing, invalid input,
   preservation of unrelated fields, persistence and legacy JSON.

## Acknowledgements

Adapted from the [SE-EDU AB3 adding-a-command tutorial](https://se-education.org/guides/tutorials/ab3AddRemark.html).
The exercise is adjusted for the current team's additional person fields,
backward-compatible JSON loading and existing duplicate-prefix conventions.

The [course submission instructions](https://nus-cs2103-ay2627-s1.github.io/website/schedule/week6/project.html)
allow creating a PR from `tutorial-adding-command` and closing it without merging.
This is a late tutorial submission, not work claimed to have been submitted on time.
