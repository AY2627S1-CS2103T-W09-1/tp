---
layout: page
title: Developer Guide
---
* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* _{List the sources of reused or adapted ideas, code, documentation, and third-party libraries here, with links to the originals.}_

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

### Religion attributes

`Religion` accepts only the categories listed in the User Guide. It strips leading and trailing
whitespace, collapses repeated internal whitespace, and compares names without regard to case.
Each valid input is stored and displayed using the category's canonical spelling: for example,
`r/jAiNiSm` becomes `Jainism`. Other spellings (such as a misspelling or a different apostrophe)
and an `Other` category are not accepted. A finite list makes values directly comparable for
future search and matching without inventing a meaning for arbitrary free text.

`AddCommandParser` and `EditCommandParser` parse the client's own religion (`r/`), a soft partner
preference (`rp/`), a required partner religion (`rr/`), and excluded partner religions (`rx/`).
`Person` rejects a preference that differs from the requirement or appears in the exclusions;
it also rejects a required religion that appears in the exclusions. `edit` can clear individual
fields with an empty prefix, while repeated `rx/` values replace the exclusion set. Absent
religion fields mean **unknown**, not `No religion`, which is an explicit category.

`JsonAdaptedPerson` saves the canonical names and treats missing religion properties in older
data as unknown. `PersonCard` displays the recorded values. Name-only `find` and pairwise
compatibility do not yet use these fields; they are separate increments.

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

1. The matchmaker requests to add a client with contact details and optional age, smoking habit, religion, preferences, and dealbreakers.
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
    * 1a1. CupidMaxxing explains the problem and leaves the previous comparison and list unchanged.

      Use case ends.
* 2a. At least one dealbreaker is violated.
    * 2a1. CupidMaxxing reports the pair as incompatible and identifies the violated requirement.

      Use case resumes at step 4.
* 2b. No known dealbreaker is violated, but required information is missing.
    * 2b1. CupidMaxxing reports insufficient information rather than claiming compatibility.

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

* Data collection gives client ages as 18–99, while filtering allows search ages up to 120; the mockup also displays age ranges where the proposed `add`/`edit` syntax describes one age.
* The proposed `find` command uses OR logic for characteristics, but AB3 already uses `find` for name search. Decide how to preserve or replace the inherited behavior.
* Group-list output is described, but the command format for listing all groups is not yet specified.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

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

### Religion attributes

1. Adding and retaining religion values

   1. Enter `add n/Nia Test p/91234567 e/nia@example.com a/Test Lane r/jAiNiSm rp/Buddhism rr/Buddhism rx/Islam`.<br>
      Expected: The contact card shows `Religion: Jainism`, `Preferred: Buddhism`,
      `Required: Buddhism`, and `Excluded: Islam`. Mixed case is converted to the
      category's canonical spelling.

   1. Enter `find Nia`.<br>
      Expected: The contact appears. `find` searches the name, not religion fields.

   1. Enter `edit 1 rr/Islam rx/Islam`.<br>
      Expected: The edit is rejected because a required religion cannot also be
      excluded. The contact's previous values are unchanged.

   1. Enter `edit 1 rp/ rr/ rx/`.<br>
      Expected: The partner criteria are cleared, but the client's religion remains `Jainism`.

   1. Close and relaunch the app.<br>
      Expected: Nia's religion is still `Jainism`; the cleared partner criteria stay empty.

1. Rejecting unsupported categories

   1. Enter `edit 1 r/Other` after locating Nia with `find Nia` again.<br>
      Expected: The edit is rejected with the supported-category list; `Other` is not
      stored as a religion.

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

### Saving data

1. Dealing with missing/corrupted data files

   1. _{Explain how to simulate missing or corrupted data files and state the expected behavior.}_

1. _{ more test cases …​ }_
