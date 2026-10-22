---
layout: page
title: Developer Guide
---
* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* _{List the sources of reused or adapted ideas, code, documentation, and third-party libraries here, with links to the originals.}_
* The smoking-status increment follows the model, storage, and UI integration approach in the [SE-EDU Adding a Command tutorial](https://se-education.org/guides/tutorials/ab3AddRemark.html), adapted to the existing `add` and `edit` commands.
* The relationship-goal increment follows the model, storage, and UI integration approach in the [SE-EDU Adding a Command tutorial](https://se-education.org/guides/tutorials/ab3AddRemark.html), adapted to the existing `add` and `edit` commands. Its goal categories are adapted from the relationship types offered by the Hinge dating app.

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

<div markdown="span" class="alert alert-primary">

:bulb: **Tip:** The `.puml` files used to create diagrams are in `docs/diagrams`. Refer to the [_PlantUML Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit diagrams.
</div>

### Architecture

<img src="images/ArchitectureDiagram.png" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

The following provides a quick overview of the main components and their interactions.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete class, preventing them from coupling to that component's implementation, as illustrated in the following partial class diagram.

<img src="images/ComponentManagers.png" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, and `StatusBarFooter`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Person` objects from the model.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<img src="images/LogicClassDiagram.png" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

![Interactions Inside the Logic Component for the `delete 1` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.
</div>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, the command is passed to an `AddressBookParser` object, which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several interactions between the command object and the `Model` to complete the operation.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<img src="images/ParserClasses.png" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `AddressBookParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<img src="images/ModelClassDiagram.png" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the `Person` objects selected by the current filter, such as search results, in a separate _filtered_ list. It exposes this list as an unmodifiable `ObservableList<Person>` that the UI can observe and bind to, so the UI updates when the list changes.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The alternative, arguably more object-oriented, design below keeps a unique list of tags in `AddressBook`, and each `Person` references tags from that list. This lets `AddressBook` maintain one `Tag` object per unique tag instead of each `Person` holding its own `Tag` objects.<br>

<img src="images/BetterModelClassDiagram.png" width="450" />

</div>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<img src="images/StorageClassDiagram.png" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Client gender

`Person` stores a `Gender` enum (`MAN`, `WOMAN`, `NON_BINARY`, or `UNSPECIFIED`). Its command and JSON values are `m`, `w`, `nb`, and the empty string respectively. `Gender.parseValue` is the shared validation and normalization method; `ParserUtil.parseGender` translates invalid input into the existing `ParseException` flow. Gender is included in full person equality, but does not change the name-based duplicate-client check.

* `AddCommandParser` accepts optional `g/`; omitted or empty values become `UNSPECIFIED`.
* `EditPersonDescriptor` uses an absent gender for “keep existing” and `UNSPECIFIED` for “clear”. `EditCommand` preserves gender when another field is edited and uses the displayed client index as before.
* `FindCommandParser` retains name-only search and parses one `g/` into a set of genders. It rejects repeated prefixes, duplicate normalized values, unknown values and empty list items. Empty `g/` selects all three specified genders. `GenderMatchesPredicate` tests set membership, and `FindCommand` accepts `Predicate<Person>` so name, age and gender searches share the existing execution path. Age searches retain upstream's exact-age and inclusive-range validation.
* `JsonAdaptedPerson` saves the canonical gender code. Missing, null or empty gender values load as `UNSPECIFIED`, so valid records containing the required age need no gender migration. Invalid non-empty values use the existing storage error handling.
* `PersonCard` and command feedback display the full gender label, including “Unspecified”.

Name, age and gender are separate search modes. Combined-trait searches remain deferred. The required age and optional smoking fields from upstream are preserved when adding, editing and saving gender.

Tests cover gender value validation, parser rules, full command execution, filtering, editing the correct displayed client, persistence, older JSON records and unchanged data/results after invalid commands. Existing name-search tests are retained. Shared builders and command helpers carry gender so they do not silently discard it.

### Recording smoking status

The optional `s/` parameter in `add` and `edit` records a client's own smoking habit.
`SmokingStatus` stores a normalized `yes` or `no`; its empty value represents information not yet recorded,
not a non-smoker. The parsers reject empty, invalid, and repeated `s/` parameters.

`Person` includes smoking status in full equality and hashing, while its existing name-based identity rule is unchanged.
`EditPersonDescriptor` carries an optional update; edits to other fields preserve the stored status.
`JsonAdaptedPerson` saves the `smokingStatus` string and reads absent or null fields in older JSON files as unspecified.
An empty stored string also means unspecified. Other invalid stored values are rejected using the existing storage-error path.
The person card and command feedback display the status, including `Not specified` for missing information.

This increment supplies personal-characteristic data for later filtering and matching work.
Partner criteria and matching are covered under [Pairwise matching](#pairwise-matching).
Clearing a recorded smoking status is not supported.
### Application data sandbox

`AppPaths` derives the packaged application's home folder from its JAR code source,
not the working directory. `MainApp` resolves preferences and client data under this
folder; `LogsCenter` confines rotating logs there and escapes log-pattern characters.
`FileUtil` and `JsonUtil` validate packaged data paths before reading or writing,
rejecting traversal and existing symbolic links that lead outside the home folder.
Unsafe log destinations fall back to console logging rather than another directory.

`Main` configures JavaFX's native-library cache and temporary-file directory inside
that same home before starting JavaFX. These settings are JVM-process properties,
not persistent OS configuration changes. The private `.javafx/` and `.tmp/` runtime
folders must be ordinary directories: all symbolic links at either root or any
descendant are rejected, including inward links that could hide outward-linked
children in their targets. This stricter rule applies only to these runtime folders;
normal in-home data and preferences links retain the existing path-validation behavior.
An unsafe or unwritable runtime layout aborts startup before JavaFX starts, without
falling back outside the sandbox or automatically deleting or rewriting existing
caches or client data. Class-directory runs have no JAR home and retain working-directory
behavior for development and JUnit fixtures; verify the sandbox against the packaged JAR.

### Religion attributes

`Religion` accepts only the categories listed in the User Guide. It strips leading and trailing
whitespace, collapses repeated internal whitespace, and compares names without regard to case.
Each valid input is stored and displayed using the category's canonical spelling: for example,
`r/jAiNiSm` becomes `Jainism`. Other spellings (such as a misspelling or a different apostrophe)
and an `Other` category are not accepted. A finite list makes values directly comparable for
search and matching without inventing a meaning for arbitrary free text.

`AddCommandParser` and `EditCommandParser` first use the shared `DatingCriteriaParser`
to extract at most one non-nested `{...}` preference environment and one non-nested
`<...>` dealbreaker environment. They tokenize the remaining outer arguments for the
client's own fields. Thus outer `r/` records own religion, `{r/Buddhism}` records a soft
partner preference for Buddhism, and `<r/Islam>` records Islam as an excluded partner
religion: the partner **must not have** it. Exclusion is not a requirement to have that
religion. These commands record criteria; [pairwise matching](#pairwise-matching) evaluates them.

The own religion is optional in `Person`; absent means **unknown**, not the explicit
`No religion` category. `add` rejects a blank outer `r/`; `edit` supports clearing only
the own field with an empty outer `r/`. Omitting it preserves the own religion. One
`r/` is permitted in each environment independently of the outer `r/`. Blank religion
values inside environments, duplicate prefixes within an environment, repeated
environments and nested or unbalanced delimiters are rejected.

`DatingAttribute.RELIGION` normalizes criteria using the same `Religion` value type as
the own field. `Person` holds an immutable `DatingCriteria` with separate preference
and dealbreaker maps indexed by `DatingAttribute`. The resulting criteria must not
prefer and exclude the same normalized religion, including when an edit updates only
one bucket and retains the other. The own religion is independent: it may equal or differ
from either partner criterion. A failed add or edit leaves stored records unchanged.

`DatingCriteriaParser.ParsedCriteria` distinguishes an omitted environment from an
explicitly empty one. `EditPersonDescriptor` carries these optional bucket updates;
`EditCommand` preserves an omitted bucket and merges a non-empty update by attribute.
For example, `{r/Buddhism}` changes the preferred religion while preserving age, smoking
and relationship-goal preferences. `{}` clears all preferences and `<>`
clears all exclusions/dealbreakers, without changing the own religion or the other bucket.
Unrelated person fields are preserved unless separately supplied in the edit.

`ParserUtil.rejectRemovedReligionCriteria` explicitly rejects the obsolete outer
`rp/`, `rr/` and `rx/` prefixes, including blank values; the shared environment parser
also rejects them inside environments. This prevents a legacy criteria command from
silently becoming part of another field.

`JsonAdaptedPerson` saves the canonical own-religion name and treats a missing or null
own-religion property as unknown. It persists criteria via `DatingCriteria` as the
`preferences` and `dealbreakers` maps, using the key `religion` and a canonical category
string, for example `"preferences": {"religion": "Buddhism"}` and
`"dealbreakers": {"religion": "Islam"}`. `DatingCriteria.fromStorage` restores these
maps; older valid records without them load with empty criteria. Invalid religion
criteria, including null map values and a normalized preference/exclusion conflict,
are reported through the existing `IllegalValueException` invalid-data path rather
than an unchecked crash. The existing unknown-property handling ignores obsolete
`preferredReligion`, `requiredReligion` and `excludedReligions` properties; they are
omitted on the next save, not migrated into the new maps. Valid own religion and other
attributes survive this transition. All persistence continues through the existing
JAR-home-confined storage; religion criteria introduce no new files or OS configuration.

`PersonCard` shows the client's own religion and recorded partner criteria. `find r/RELIGION` compares only that own
field, not either partner-criteria map. Criteria-environment searching is not supported. Use `match` for
compatibility evaluation.

### Recording relationship goals

The optional `rg/` parameter in `add` and `edit` records a client's own relationship goal.
`RelationshipGoal` is an enumeration of the six goals listed in the User Guide, adapted from Hinge.
Each enum value holds its short keyword (for example, `long-open`) and its full name, so a goal can
be entered either way; keywords exist because fast typing is a core goal of the product.
`RelationshipGoal#fromString` strips the input, collapses repeated internal whitespace, and ignores
case when matching. Each goal is stored and displayed using its full name: for example, `rg/LONG`
becomes `Long-term relationship`.

`AddCommandParser` and `EditCommandParser` reject invalid and repeated `rg/` parameters.
`Person` exposes the goal as an `Optional`, because an absent goal means **unknown**, not
`Figuring out my goals`, which is an explicit category. `Person` includes the goal in full equality
and hashing, while its existing name-based identity rule is unchanged. `EditPersonDescriptor`
records whether `rg/` was supplied separately from its value, so `rg/` with no value clears the goal
and edits to other fields preserve it.

`JsonAdaptedPerson` saves the full category name as the `relationshipGoal` string and reads absent
or null fields in older JSON files as unknown. Other invalid stored values are rejected using the
existing storage-error path. The person card and command feedback display the goal, including
`Not specified` for missing information.

`find rg/GOAL` searches by relationship goal. `FindCommandParser` treats `rg/` as one more search
mode alongside name, `age/`, `g/` and `r/`: it counts the prefix modes present and rejects input that
combines modes, mixes a prefix with name keywords, or repeats `rg/`. The value is parsed with
`ParserUtil#parseRelationshipGoal`, so search accepts exactly the keywords and full names that `add`
and `edit` accept. `RelationshipGoalMatchesPredicate` keeps clients whose goal equals the parsed goal,
so clients with an unknown goal are never returned. A single goal is accepted rather than a
comma-separated list, because some full goal names contain commas.

Relationship-goal criteria are evaluated by [Pairwise matching](#pairwise-matching).

### Pairwise matching

`MatchCommandParser` accepts exactly two distinct positive indexes using `ParserUtil.parseIndex`.
`MatchCommand` resolves them against `Model.getFilteredPersonList()` and rejects out-of-range indexes.
`MatchAssessment` evaluates each client's `DatingCriteria` against the other's own attributes, preserving unknown
gender, smoking status, religion, and relationship goal. Age uses the existing inclusive `AgeMatchesPredicate`;
other attributes compare canonical values. Gender criteria accept a single `g/m`, `g/w`, or `g/nb` value
inside each preference or dealbreaker environment.

Preferences describe desired values; dealbreakers describe excluded values. A dealbreaker comparison is met
when a known value is outside the exclusion, unmet when it falls inside, and unknown when the attribute is absent.
Any known violation makes the pair incompatible, even if another comparison is unknown. Otherwise, an unknown
comparison or no criteria on either client gives insufficient information. All other pairs are compatible with
recorded dealbreakers, including pairs with unmet preferences.

`MatchCommandResult` supplies structured comparisons to `MatchResultView`, which displays a colored outcome and
a table per direction in the command-result area. Plain-text feedback uses the same labels for logs.
Other commands and errors retain their text display. Matching does not change model data,
the list filter, or the storage schema, and does not record a pairing. `LogicManager` still performs its normal
save after successful commands; the saved client data is unchanged. Invalid commands use the existing error display.

Tests cover parsing and dispatch, comparison rules and missing values, filtered indexes, reloads, and unchanged
records and saved data. Run `./gradlew test --tests '*Match*'` for the matching tests.

### Finding compatible clients

`find match/INDEX` lists the clients compatible with the client at `INDEX` in the displayed list.
`FindCommandParser` treats `match/` as another exclusive search mode and returns a `FindMatchCommand`.
It is a separate command because the index must be resolved against the displayed list when the
command runs, which the parser cannot do.

`FindMatchCommand` reuses `MatchAssessment` (see [Pairwise matching](#pairwise-matching)) so that it
never disagrees with `match`. For every other client in the address book, it builds one
`MatchAssessment` with the target. Clients whose outcome is `INCOMPATIBLE` are hidden; clients with
unknown details stay listed. Each remaining client is scored by the number of preference comparisons
with status `MET` in both directions, and the list is sorted by score, highest first. Scores are
computed once before sorting, and ties keep insertion order because the sort is stable.

To support sorting, `ModelManager` wraps its `FilteredList` in a `SortedList`, and
`getFilteredPersonList()` returns the sorted view. Every index-based command therefore uses the order
the user sees. `Model#updateFilteredPersonList(Predicate, Comparator)` sets both the filter and the
sort order. The one-argument `updateFilteredPersonList(Predicate)` clears the sort order, so `list`
and other searches return to insertion order without any changes.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

![UndoRedoState0](images/UndoRedoState0.png)

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

![UndoRedoState1](images/UndoRedoState1.png)

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

![UndoRedoState2](images/UndoRedoState2.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</div>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

![UndoRedoState3](images/UndoRedoState3.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</div>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Logic.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `UndoCommand` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.

</div>

Similarly, how an undo operation goes through the `Model` component is shown below:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Model.png)

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</div>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

![UndoRedoState4](images/UndoRedoState4.png)

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

![UndoRedoState5](images/UndoRedoState5.png)

The following activity diagram summarizes what happens when a user executes a new command:

<img src="images/CommitActivityDiagram.png" width="250" />

#### Design considerations:

**Aspect: How undo & redo execute:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command is correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

These are requirements for the planned CupidMaxxing product. They do not imply that every feature is implemented in the current release. The product will be evolved incrementally from AB3.

### Product scope

**Target user profile**: An independent professional matchmaker who manages a small pool of clients and potential matches, types quickly, and prefers a keyboard-driven desktop application. The matchmaker needs to record client details, retrieve relevant contacts, assess possible introductions, and organise follow-ups.

**Value proposition**: CupidMaxxing helps matchmakers organise and retrieve client information quickly, narrow down potential matches using relevant criteria, and assess whether two clients are suitable for an introduction.

**Minimum viable product (MVP)**:

* Record a client's own characteristics, dating preferences, and dealbreakers. The initially planned attributes are age, smoking habit, and religion.
* Search and filter clients using those attributes, and compare two clients against each other's preferences and dealbreakers.
* Categorise clients using labels (the existing AB3 tags) and named groups.

Other ideas, such as fuzzy search, command aliases, archiving, profile sharing, reminders, and match history, are candidates for later iterations rather than MVP commitments.

### User stories

Priorities: High (MVP) - `* * *`, Medium (useful extension) - `* *`, Low (possible future enhancement) - `*`.

| Priority | As a … | I want to … | So that I can … |
| -------- | ------ | ----------- | --------------- |
| `* * *` | matchmaker | add and update a client's contact details | keep an accurate way to reach the client |
| `* * *` | matchmaker | record a client's age, smoking habit, religion, preferences, and dealbreakers | have the information needed to filter and assess candidates |
| `* * *` | matchmaker | find clients by relevant characteristics | identify possible candidates without scanning every record |
| `* * *` | matchmaker | filter clients by several criteria at once | narrow a shortlist to clients who satisfy all criteria |
| `* * *` | matchmaker | compare two clients against each other's preferences and dealbreakers | avoid an introduction that violates a stated requirement |
| `* * *` | matchmaker | attach labels to clients | categorise and retrieve them easily |
| `* * *` | matchmaker | create named groups and assign clients to them | manage different sets of clients |
| `* *` | matchmaker | identify clients needing attention | prioritise follow-ups |
| `* *` | matchmaker | mark a client's current status | know who is available for introductions |
| `* *` | matchmaker | copy a client's contact details quickly | contact or share details with minimal effort |
| `* *` | matchmaker | archive inactive clients | keep the active list uncluttered |
| `*` | matchmaker | search despite a misspelled name or tag | find records during hurried entry |
| `*` | matchmaker | share a summary without contact details or private notes | show prospective profiles without exposing private information |
| `*` | matchmaker | record previous pairings and post-date feedback | avoid repeated unsuitable introductions |
| `*` | matchmaker | set follow-up reminders | remember to check in after an introduction |

### Use cases

For the use cases below, the **System** is CupidMaxxing and the **Actor** is an independent matchmaker. Client indexes refer to the currently displayed list.

**Use case: Record a client's matchmaking details**

**Main success scenario (MSS)**

1. The matchmaker requests to add a client with contact details and optional gender, age, smoking habit, religion, preferences, and dealbreakers.
2. CupidMaxxing validates the supplied values and checks for repeated attributes within the command.
3. CupidMaxxing creates the client record and shows the stored information.
4. At a later visit, the matchmaker requests to edit that client with a new preference or characteristic.
5. CupidMaxxing validates the update, replaces the specified existing values, and refreshes the client display.

   Use case ends.

**Extensions**

* 2a. A supplied value is invalid, an attribute is repeated, or a preference conflicts with a dealbreaker in the same command.
    * 2a1. CupidMaxxing explains the invalid input and changes no data.

      Use case ends.
* 5a. The requested client index is not in the displayed list.
    * 5a1. CupidMaxxing reports the invalid index and leaves all records unchanged.

      Use case ends.

**Use case: Filter a shortlist of clients**

**MSS**

1. The matchmaker requests `filter` with at least one criterion, such as an age range and smoking status.
2. CupidMaxxing checks that each criterion is supported and has a valid value.
3. CupidMaxxing searches the entire address book and shows each client who satisfies **all** supplied criteria exactly once.
4. CupidMaxxing reports how many clients matched. The displayed indexes are updated to match the filtered list.

   Use case ends.

**Extensions**

* 2a. No criterion is supplied, or a criterion is invalid or repeated.
    * 2a1. CupidMaxxing reports the problem and keeps the previously displayed list unchanged.

      Use case ends.
* 3a. No client satisfies every criterion.
    * 3a1. CupidMaxxing shows an empty list and reports that no clients match.

      Use case ends.

A missing client attribute does not satisfy a filter criterion. Separate client records with the same name remain separate results.

**Use case: Assess a pair of clients**

**MSS**

1. The matchmaker requests `match INDEX_A INDEX_B` using two different indexes in the displayed list.
2. CupidMaxxing compares each client's recorded preferences and dealbreakers against the other client's characteristics, in both directions.
3. CupidMaxxing shows both names, a breakdown of each comparison as met, unmet, or unknown, and an overall assessment.
4. The displayed list and stored client records remain unchanged.

   Use case ends.

**Extensions**

* 1a. An index is invalid, missing, repeated, or outside the displayed list.
    * 1a1. CupidMaxxing shows an error in the command-result area and leaves client records and the list unchanged.

      Use case ends.
* 2a. At least one dealbreaker is violated.
    * 2a1. CupidMaxxing reports the pair as incompatible and identifies the violated requirement.

      Use case resumes at step 4.
* 2b. No known dealbreaker is violated, but required information is missing.
    * 2b1. CupidMaxxing reports insufficient information rather than claiming compatibility.

      Use case resumes at step 4.

* 2c. Neither client has recorded criteria.
    * 2c1. CupidMaxxing reports insufficient information.

      Use case resumes at step 4.

**Use case: Organise clients in a named group**

**MSS**

1. The matchmaker requests `group create g/VIP`.
2. CupidMaxxing creates an empty group named VIP.
3. The matchmaker requests `group add g/VIP 2 5` using indexes in the displayed list.
4. CupidMaxxing adds the two distinct clients and confirms the number added.
5. The matchmaker requests `group show g/VIP`.
6. CupidMaxxing shows only the group's clients without changing their records.

   Use case ends.

**Extensions**

* 2a. A group with the same name already exists, ignoring letter case.
    * 2a1. CupidMaxxing reports the duplicate and creates no group.

      Use case ends.
* 4a. An index is invalid, repeated, or already belongs to the group.
    * 4a1. CupidMaxxing reports the problem and makes no partial membership change.

      Use case ends.

A client may belong to several different groups. Renaming or deleting a group does not delete its clients.

### Non-Functional Requirements

1. CupidMaxxing should run on Windows, Linux, and macOS with Java 25, without requiring another Java version, an installer, or the team's own remote server.
2. The application should support a single matchmaker working with local data. Client records should remain in a human-editable text file, with at least the data-editing support of AB3; no DBMS should be required.
3. The product should be distributable as one JAR file, or as one ZIP if additional files are unavoidable, and the distributed product should not exceed 100 MB.
4. A matchmaker who prefers typing should be able to perform the main client-management, filtering, matching, and grouping workflows using commands without a mouse.
5. Typical operations on an address book containing up to 1000 clients should not feel noticeably sluggish.
6. Validation failures should not partially change stored client records or group membership.
7. The GUI should work well at 1920×1080 or higher with 100% or 125% scaling, and remain usable at 1280×720 or higher with 150% scaling.
8. Any proposed third-party library or service should satisfy the course's external-software rules and receive teaching-team approval before use.
9. The packaged app must read and write application files only inside the folder containing its JAR or its subfolders, and must not change OS-level configuration settings (Constraint-Data-Sandbox).

### Glossary

* **Client**: A person whose contact details and matchmaking information are recorded by the matchmaker.
* **Characteristic**: A value describing a client, initially age, smoking habit, or religion.
* **Preference**: A characteristic the client would like a potential partner to have; an unmet preference need not make a pair incompatible.
* **Dealbreaker**: A requirement that excludes a potential partner when violated.
* **Filter criterion**: A condition that a client must satisfy to appear in an AND-filtered shortlist.
* **Pairwise matching**: Comparing two clients' recorded criteria in both directions without creating or modifying a relationship record.
* **Unknown**: An assessment made when a required characteristic has not been recorded; it is not equivalent to a match.
* **Label**: A categorisation attached to a client using the existing AB3 tag mechanism.
* **Group**: A named collection of client records; membership does not create duplicate client records.

### Details to confirm with the team

The draft feature notes disagree on some validation rules. Resolve these before implementing or publishing precise command specifications:

* The implemented age range is 18–99 for client data and age searches. Draft ranges up to 120 and age-range client records are not supported.
* Religion data uses the 21 fixed categories in the User Guide, with no free-text or `Other` value. Future filtering must use the same canonical representation.
* Name-only `find` is retained alongside gender-only `find g/`. Exact-age and age-range searches are also supported separately. Combining characteristics into an OR search remains pending.
* The proposed `find` command uses OR logic for characteristics, but AB3 already uses `find` for name search. Decide how to preserve or replace the inherited behavior.
* Group-list output is described, but the command format for listing all groups is not yet specified.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

### Recording religion preferences and exclusions

Use an isolated writable JAR home folder with test data, not real client records. All
JSON inspection and fixtures below are confined to that folder's `data/addressbook.json`.
Back up the test file before changing it, close the app before editing JSON, and restore
the valid backup between invalid-file tests. In steps using `edit 1`, first run
`find Criteria` and check that only the named test client is displayed.

1. Run `add n/Religion Criteria p/91234567 e/criteria@example.com a/Test Lane age/25 g/nb s/no rg/long t/demo r/jAiNiSm {r/bUdDhIsM age/25} <r/iSlAm>`.<br>
   Expected: One client is added with own religion Jainism, preference religion Buddhism
   and age 25, and excluded religion Islam. Ordinary details, gender, smoking status,
   relationship goal and tag are recorded normally. Inspect the saved JSON: outer
   `religion` is `Jainism`, `preferences` contains `religion: Buddhism` and `age: 25`,
   and `dealbreakers` contains `religion: Islam` (JSON keys and values are strings).
2. Run `find Criteria`, then `edit 1 p/92345678`.<br>
   Expected: Only the phone changes. Own religion and both entire criteria maps remain
   unchanged. In the remaining steps, check that name, this phone, email, address,
   age, gender, smoking status, relationship goal and tag remain unchanged unless
   explicitly edited.
3. Run `edit 1 r/Buddhism`, then `edit 1 r/Islam`, finding the test client again
   before each edit.<br>
   Expected: Both succeed. Own religion may equal the preferred or excluded religion;
   neither edit changes the criteria maps.
4. Run `edit 1 r/`, then `edit 1 r/Jainism`, finding the client again each time.<br>
   Expected: The first clears only own religion to unknown; the second restores it.
   Preferences and exclusions remain unchanged throughout.
5. Run `edit 1 {r/ Baha'i   Faith }`.<br>
   Expected: The preferred religion becomes Baha'i Faith; the existing age preference
   remains 25. Islam remains excluded, and own religion stays Jainism. Run
   `edit 1 <r/ Christianity >` after finding the client again.<br>
   Expected: The excluded religion is updated to canonical Christianity;
   the Baha'i Faith preference is preserved.
6. Run `edit 1 {r/Buddhism age/25} <r/Islam>` after finding the client again.<br>
   Expected: The supplied entries are updated in each bucket; other criteria are preserved. Then try
   `edit 1 {r/ iSlAm }`.<br>
   Expected: Rejected because Islam remains excluded in the omitted exclusion bucket.
   Neither the existing preference map nor any other field changes. Also try
   `edit 1 <r/buddhism>`; the retained Buddhism preference makes this invalid too.
7. Try each of `edit 1 {r/}`, `edit 1 <r/>`, `edit 1 {r/Other}`,
   `edit 1 <r/Islam r/Christianity>`, `edit 1 r/Jainism r/Islam`,
   `edit 1 {r/Buddhism} {r/Jainism}`, `edit 1 <r/Islam> <r/Christianity>`,
   `edit 1 {r/Buddhism <r/Islam>}`, and `edit 1 {r/Buddhism`.<br>
   Expected: Every command is rejected; the entire previous record is unchanged.
8. Try `add n/Religion Invalid p/91234567 e/invalid@example.com a/Test Lane age/25 {r/Islam} <r/islam>`.<br>
   Expected: Rejected after normalization; no new client is added. Repeat with the
   criteria replaced by `{r/}` and then `{r/Buddhism r/Islam}`; both are rejected.
   For each legacy prefix `rp/`, `rr/` and `rx/`, try `edit 1 PREFIXIslam`,
   `edit 1 PREFIX`, and `edit 1 {PREFIXIslam}`, replacing `PREFIX` literally.<br>
   Expected: Legacy prefixes are rejected, including blank values; no record changes.
9. Find the test client again and run `edit 1 {}`.<br>
   Expected: All preferences are cleared, including age; excluded Islam and own Jainism
   remain. Run `edit 1 {r/Buddhism}` after finding it again, then `edit 1 <>` after
   finding it once more.<br>
   Expected: The latter clears all exclusions, preserving the Buddhism preference
   and own Jainism. Saved maps reflect each change.
10. Restore `edit 1 <r/Islam>` after finding the client, exit, and restart the JAR.<br>
    Expected: Own Jainism, preferred Buddhism, excluded Islam and all ordinary details
    survive. Run `find r/Jainism`, `find r/Buddhism` and `find r/Islam`.<br>
    Expected: The test client appears only for Jainism, never solely because of a
    preference or exclusion. Use `list` to restore the full display. These searches
    do not evaluate compatibility. Exit and back up this latest valid test file before
    the following JSON fixture checks.
11. With the app closed, remove only the test client's `preferences` and `dealbreakers`
    properties from `data/addressbook.json` and restart.<br>
    Expected: The client loads with empty criteria; own religion and ordinary details
    remain intact. Restore the valid backup before continuing.
12. In separate copies of the valid test file, set the preference map's `religion`
    value to `Other`, to null, and to `ISLAM` while the exclusion remains `Islam`.
    Restart for each case, without issuing a saving command.<br>
    Expected: Each invalid file uses the existing invalid-data handling (an empty
    address book), without an unchecked crash. The invalid file remains on disk
    until a saving command; restore the backup before testing the next case.

### Finding clients by their own religion

`FindCommandParser` recognizes `r/` alongside the existing age and gender modes. It
requires exactly one search mode, rejects repeated prefixes and uses `ParserUtil.parseReligion`
to share the same fixed categories and normalization as add/edit. Blank and unsupported
values fail during parsing, before the displayed list or stored records can change.

`ReligionMatchesPredicate` compares only `Person.getReligion()` with the requested category.
Unknown religion never matches, including a request for the explicit `No religion` category.
`FindCommand` updates the full address book's filtered view, so a previous shortlist does not
restrict the next search. The existing result-count feedback and zero-result behavior apply.
No partner preferences, combined-trait OR/AND search or compatibility assessment is added.

Manual checks (use distinct test names):

1. Add `Religion Jain` with `add n/Religion Jain p/91234567 e/jain@example.com a/Test Lane age/25 r/jAiNiSm`, `Religion None` with `add n/Religion None p/91234567 e/none@example.com a/Test Lane age/25 r/No religion`, and `Religion Unknown` with `add n/Religion Unknown p/91234567 e/unknown@example.com a/Test Lane age/25`.
2. Run `find Unknown`, then `find r/JAINISM`. The Jainism client appears even though the prior list contained only the unknown client. The count equals the displayed matches across the full book.
3. Run `find r/No religion`. Only explicitly recorded No religion clients appear; unknown clients are excluded. Try a supported category absent from the book: the list is empty and the count is zero. Run `list` to restore all contacts.
4. With a non-empty result list, try `find r/`, `find r/Other`, `find r/Jainism r/Islam`, `find Religion r/Jainism`, `find r/Jainism age/25`, and `find r/Jainism g/nb`. Each fails without changing records or the previous results.
5. Restart and repeat the religion searches. Own religion and ordinary client details remain unchanged. Confirm that name, age and gender searches still work independently.

### Recording and finding gender

Use a test data file and distinct client names for these checks.

1. Add a client with `add n/Gender Demo p/91234567 e/demo@example.com a/Demo Road age/25 g/nb`. Check that its card and feedback show “Gender: Non-binary”. Add another client with a different name and no `g/`; its gender should be “Unspecified”.
2. Run `find g/nb`, then `find g/m,w`, then `find g/`. Check that each search uses the full list and the last excludes unspecified genders. Confirm that `find Gender` still searches names.
3. Run `find g/nb`, then `edit 1 g/w`. Check that the first displayed client changes, with contact details preserved. Find that client again before using `edit 1 g/`; its gender should clear. Editing only its phone should preserve its current gender.
4. Try `find g/m,M`, `find g/m,`, `add n/Invalid Demo p/91234567 e/demo@example.com a/Demo Road age/25 g/m,w`, and `edit 1 g/x`. Each should report an error without changing records or the displayed list.
5. Restart the application and verify that recorded and cleared genders persist. An older valid data file containing ages but no gender fields should still load with all clients' genders unspecified.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### Launch and shutdown

1. Initial launch

   1. Download the JAR file and copy it into an empty folder.

   1. Double-click the JAR file.<br>
      Expected: The GUI opens with a set of sample contacts. The window size may not be optimal.

1. Saving window preferences

   1. Resize the window to an optimal size. Move the window to a different location. Close the window.

   1. Relaunch the app by double-clicking the JAR file.<br>
       Expected: The most recent window size and location are retained.

1. _{ more test cases …​ }_

### Application data sandbox

1. Launch from another working folder

   1. Put the packaged JAR in a new writable home folder. From a different empty
      folder, run `java -jar /path/to/home/addressbook.jar` using Java 25. Add a
      test contact, then exit.<br>
      Expected: Preferences, logs, caches, and `data/addressbook.json` are created
      only in the JAR home folder; the working folder remains empty. Relaunching
      from either folder retains the contact.

1. Reject outward symbolic links (where supported by the OS)

   1. Back up the isolated test data. With the app closed, replace its `data`
      subfolder with a symbolic link to a separate folder containing a sentinel
      data file. Launch and try adding a contact.<br>
      Expected: External data is not loaded or overwritten. Saving is rejected;
      the sentinel file is unchanged. Restore the real in-home data folder afterward.

1. Reject all runtime-folder symbolic links (where supported by the OS)

   1. Use an isolated packaged-JAR home and back up its test data. With the app closed,
      test `.javafx` and `.tmp` separately: place a symbolic link at the runtime-folder
      root, then in a separate run place a link below an ordinary runtime folder.
      Repeat both cases with targets inside and outside the JAR home. For an inward
      directory target, also place an outward-linked child in that target.<br>
      Expected: Every layout is rejected before JavaFX starts, including inward links.
      No links, cache contents, client data, or external sentinel files are deleted or
      rewritten. Restore the test layout manually between cases. Finally, restore
      ordinary runtime folders with no symbolic links anywhere inside and relaunch;
      startup succeeds and runtime files remain inside the JAR home.

### Religion attributes

1. Adding, editing and retaining the client's own religion

   1. Enter `add n/Nia Test p/91234567 e/nia@example.com a/Test Lane age/25 s/no g/nb rg/long r/jAiNiSm`.<br>
      Expected: The card shows `Religion: Jainism`, age 25, non-smoking,
      non-binary gender and a long-term relationship goal. No partner criteria appear.

   1. Enter `find Nia`, then `edit 1 r/Christianity`.<br>
      Expected: The religion changes to `Christianity`; all other details remain unchanged.

   1. Enter each of `edit 1 rp/Buddhism`, `edit 1 rr/Buddhism`, and `edit 1 rx/Islam`.<br>
      Expected: Each command is rejected because the legacy prefixes are no longer
      supported. The error explains outer `r/` for own religion, `{r/RELIGION}` for a
      preference and `<r/RELIGION>` for an exclusion (must not have).
      Repeat with empty `rp/`, `rr/`, and `rx/`; these are also rejected.
      The contact's religion and other details remain unchanged.

   1. Enter `edit 1 r/`, then close and relaunch the app and enter `find Nia`.<br>
      Expected: Religion remains unknown, not `No religion`; the other details are retained.

1. Rejecting unsupported categories

   1. Enter `edit 1 r/Other` after locating Nia.<br>
      Expected: The edit is rejected with the supported-category list; `Other` is not stored.

1. Loading data containing obsolete partner criteria

   1. With the app closed, back up its in-home data file. Add `preferredReligion`,
      `requiredReligion`, and `excludedReligions` properties to a valid contact,
      leaving its own religion and required attributes valid. Relaunch and enter `list`.<br>
      Expected: The contact and its own attributes load. Partner criteria are not shown
      and the obsolete properties are omitted from the saved file. Restore the backup.

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command, with multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: The first contact is deleted from the list. The status message shows the deleted contact's details.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. The status message shows error details.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Relationship goals

1. Adding and retaining a relationship goal

   1. Enter `add n/Rae Test p/91234567 e/rae@example.com a/Test Lane age/30 rg/LONG`.<br>
      Expected: The contact card shows `Relationship goal: Long-term relationship`. The keyword
      is converted to the goal's full name.

   1. Enter `find Rae`, then `edit 1 rg/short-open`.<br>
      Expected: The contact card shows `Relationship goal: Short-term relationship, open to long`.

   1. Enter `edit 1 p/99999999`.<br>
      Expected: The phone number changes and the relationship goal is unchanged.

   1. Close and relaunch the app.<br>
      Expected: Rae's relationship goal is still `Short-term relationship, open to long`.

   1. Enter `find Rae`, then `edit 1 rg/`.<br>
      Expected: The contact card shows `Relationship goal: Not specified`.

1. Rejecting unsupported and repeated values

   1. Enter `add n/Sam Test p/92345678 e/sam@example.com a/Test Lane age/30 rg/marriage`.<br>
      Expected: The command is rejected with the list of accepted keywords and goals. No contact is added.

   1. Enter `add n/Sam Test p/92345678 e/sam@example.com a/Test Lane age/30 rg/short rg/life`.<br>
      Expected: The command is rejected because `rg/` is repeated. No contact is added.

1. Finding clients by relationship goal

   1. Ensure Rae has `rg/long` and another client has a different or no goal, then enter `find rg/long`.<br>
      Expected: Rae is listed and clients with other or unspecified goals are not. The status shows the number listed.

   1. Enter `find rg/Long-term relationship`.<br>
      Expected: The same clients are listed as for `find rg/long`.

   1. Enter `find rg/marriage`, `find rg/`, `find rg/long rg/short`, `find rg/long g/m` and `find Rae rg/long` in turn.<br>
      Expected: Each command is rejected with an error and the displayed list is unchanged.

### Finding clients by smoking status

The `FindCommandParser` reuses `ParserUtil.parseSmokingStatus` to validate a single `s/yes` or `s/no`
criterion. `SmokingStatusMatchesPredicate` compares only the client's recorded smoking status.
It rejects an unspecified search criterion, and unspecified client statuses match neither value.
The existing `FindCommand` filters the full address book without changing stored records.

Manual checks (use a separate test data set):

1. Add three clients with distinct names: one with `s/yes`, one with `s/no`, and one with no `s/`.
   Give the smoker a `{s/no}` preference and a `<s/yes>` dealbreaker to distinguish own status from criteria.
2. Run `find s/YES`, then `find s/no`. Each returns only the corresponding recorded status,
   even though the previous search hid that client. Check that the displayed count is correct.
3. Run `find s/`, `find s/maybe`, `find s/yes s/no`, `find s/no age/25`, and `find Alice s/yes`.
   Each should report an error and preserve the previous results and records.
4. Run `list` to restore all three clients. Restart the application and repeat the searches;
   the results should remain the same. With no clients of the searched status, expect zero matches.
5. After `find s/no`, run `edit 1 s/yes` or `delete 1` on disposable test data.
   Confirm that it targets the first displayed client, not the first client in the full book.

### Saving data

1. Dealing with missing/corrupted data files

   1. _{Explain how to simulate missing or corrupted data files and state the expected behavior.}_

1. _{ more test cases …​ }_
