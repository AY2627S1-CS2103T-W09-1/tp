---
layout: page
title: User Guide
---

CupidMaxxing is a **desktop application for managing contacts, optimized for use through a Command Line Interface (CLI)** while retaining the benefits of a Graphical User Interface (GUI). CupidMaxxing helps independent matchmakers efficiently organize and retrieve client contact information, making it faster to identify relevant contacts and coordinate introductions between suitable clients.

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/AY2627S1-CS2103T-W09-1/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for CupidMaxxing.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar addressbook.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all contacts.

   * `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01 age/25` : Adds a contact named `John Doe` to the Address Book.

   * `delete 3` : Deletes the 3rd contact shown in the current list.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add n/NAME`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `n/NAME [t/TAG]` can be used as `n/John Doe t/friend` or as `n/John Doe`.

* Items followed by `…`​ can appear zero or more times.<br>
  For example, `[t/TAG]…​` may be omitted, or written as `t/friend` or `t/friend t/family`.

* Parameters can be in any order.<br>
  For example, if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* Extraneous parameters for commands that take no parameters, such as `help`, `list`, `exit`, and `clear`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</div>

### Viewing help: `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding a client: `add`

Adds a client to CupidMaxxing with contact details and age. Gender, smoking status, religion, relationship goal, tags and partner criteria are optional.

Format: `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS age/AGE [s/SMOKING] [g/GENDER] [rg/RELATIONSHIP_GOAL] [t/TAG]…​ [r/RELIGION] [{CRITERIA}] [<CRITERIA>]`

* `{CRITERIA}` records partner preferences and `<CRITERIA>` records dealbreakers. Each block accepts `age/`, `g/`, `s/`, `r/`, and `rg/`. See [Matching two clients](#matching-two-clients-match) for values and examples.
* `AGE` is compulsory and must be an integer from 18 to 99 (inclusive).
* `g/GENDER` records the client's own gender: `m` (man), `w` (woman), or `nb` (non-binary). Values are case-insensitive and surrounding whitespace is ignored. Omit `g/` or leave it empty for unspecified gender. Only one value and one outer `g/` prefix are allowed per command. Each preference or dealbreaker block may also contain one separate gender criterion.
* `rg/RELATIONSHIP_GOAL` is the client's own relationship goal. See [Relationship goal field](#relationship-goal-field) below.

The optional `s/SMOKING` field records the client's **own smoking habit**:

* Use `s/yes` for a smoker or `s/no` for a non-smoker. Values are case-insensitive; `s/YES` is accepted.
* Omit the field if the status is unknown. The person card displays `Smoking: Not specified`.
* A supplied outer `s/` must have a value, and may appear only once per command. Each preference or dealbreaker block may also contain one separate smoking criterion. Blank, invalid, or repeated values within the same scope reject the command without changing any records.
* Smoking status is saved between sessions. Older records without this field remain usable and display `Not specified`.
* This field records the client's own habit. See [Matching two clients](#matching-two-clients-match) for partner criteria.

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
A person can have any number of tags, including zero.
</div>

Examples:

* `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01 age/25`
* `add n/Betsy Crowe t/friend e/betsycrowe@example.com a/Newgate Prison p/1234567 age/30 t/criminal`
* `add n/Sarah Tan p/91234567 e/sarah@example.com a/12 Clementi Rd age/28 g/w` Records Sarah as a woman.
* `add n/Alice Tan p/91234567 e/alice@example.com a/Clementi age/30 s/no` Records Alice as a non-smoker.
* `add n/Priya Nair p/90001111 e/priya@example.com a/8 Tampines St age/28 rg/long` Records Priya as looking for a long-term relationship.
* `add n/Amy Bee p/85355255 e/amy@example.com a/Jurong West age/25 r/Jainism`

#### Religion fields

Religion is optional. Omit `r/` when a client's religion is not known; this is different from
`r/No religion`, which records an explicit answer. The accepted categories are
`Christianity`, `Islam`, `Hinduism`, `Buddhism`, `Sikhism`, `Judaism`, `Jainism`,
`Baha'i Faith`, `Shinto`, `Taoism`, `Confucianism`, `Zoroastrianism`, `Rastafari`,
`Wicca`, `Paganism`, `Tenrikyo`, `Cao Dai`, `Druze`, `Atheism`, `Agnosticism`, and
`No religion`. Use one of these names for `r/`.
Names are matched without regard to letter case or repeated spaces: for example,
`r/jAiNiSm` is accepted and displayed as `Jainism`. A misspelled name is not accepted;
use the listed spelling, although its letter case can vary. There is no free-text or
`Other` category. If none applies, leave the field unset rather than selecting
`No religion`, which means the client explicitly has no religion.

* `r/RELIGION` outside braces records the client's **own religion**, not a partner preference.
* `{r/RELIGION}` records a **soft partner preference**: a religion the client would like a partner to have.
* `<r/RELIGION>` records an **excluded partner religion** (a dealbreaker): the partner **must not have** this religion. It does not mean the partner must have it.
* Type the curly braces `{}` and angle brackets `<>` literally; square brackets in the format above only mark optional parts. For example, `r/Jainism {r/Buddhism} <r/Islam>` records own religion Jainism, a preference for Buddhism, and an exclusion of Islam.
* Each command may contain at most one preference environment `{...}` and one exclusion environment `<...>`. Environments cannot be nested. Supply at most one `r/` outside the environments and at most one `r/` inside each environment; a list of religions is not accepted.
* All three religion values use the same 21 categories and normalization above. A blank outer `r/` is rejected in `add`; a blank `r/` inside either environment is always rejected.
* The same normalized religion cannot be both preferred and excluded: `{r/Islam} <r/islam>` is rejected without changing records. The client's own religion is independent and may equal or differ from either partner criterion.
* Own religion and partner criteria are saved between sessions. The client's own religion is shown on the client card.
* The former partner-criteria prefixes `rp/`, `rr/`, and `rx/` are no longer supported. Commands containing them are rejected without changing records.
* `find r/RELIGION` searches only the client's own religion. Searching criteria environments is not implemented. Use [match](#matching-two-clients-match) to assess recorded preferences and exclusions; recording an exclusion alone does not run a comparison.

Example:

* `add n/Religion Demo p/91234567 e/religion@example.com a/Test Lane age/25 r/Jainism {r/Buddhism} <r/Islam>` Adds a 25-year-old client with the client's own religion, preference and exclusion described above. The required contact details and age are outside the environments.

#### Relationship goal field

The optional `rg/RELATIONSHIP_GOAL` field records what the client is **looking for**. The accepted
goals are adapted from Hinge. Type the short keyword for speed, or the full goal name; either way,
the full goal name is saved and shown on the client card:

Keyword | Relationship goal
--------|------------------
`life` | Life partner
`long` | Long-term relationship
`long-open` | Long-term relationship, open to short
`short-open` | Short-term relationship, open to long
`short` | Short-term fun
`unsure` | Figuring out my goals

* Keywords and goal names are matched without regard to letter case or repeated spaces: for example, `rg/LONG` and `rg/long-term   RELATIONSHIP` are both saved as `Long-term relationship`.
* Omit the field if the goal is not known. The client card then displays `Relationship goal: Not specified`. This is different from `rg/unsure`, which records that the client is still figuring out their goals.
* A supplied outer `rg/` may appear only once per command. Each preference or dealbreaker block may also contain one separate relationship-goal criterion. Other values, such as `rg/marriage`, are rejected with the list of accepted keywords, and no records are changed.
* The relationship goal is saved between sessions. Older records without this field remain usable and display `Not specified`.
* This field records the client's own goal. See [Matching two clients](#matching-two-clients-match) for partner criteria.

### Listing all clients: `list`

Shows a list of all clients in CupidMaxxing.

Format: `list`

### Editing a client: `edit`

Edits the client at `INDEX` in the currently displayed list. `INDEX` must be a positive integer.

Format: `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [age/AGE] [s/SMOKING] [g/GENDER] [rg/RELATIONSHIP_GOAL] [t/TAG]…​ [r/RELIGION] [{CRITERIA}] [<CRITERIA>]`

* Edits the person at the specified `INDEX`. The index refers to the index number shown in the displayed person list. The index **must be a positive integer** 1, 2, 3, …​
* At least one of the optional fields must be provided.
* `{CRITERIA}` and `<CRITERIA>` accept `age/`, `g/`, `s/`, `r/`, and `rg/`. See [Matching two clients](#matching-two-clients-match) for values and examples.
* A successful edit restores the full client list. Find the client again before reusing an index from filtered results.
* Existing values will be updated to the input values.
* Use `g/m`, `g/w`, or `g/nb` to change gender; empty `g/` clears it, and omitting `g/` preserves it. Gender can be edited together with the other supported fields.
* If supplied, `AGE` must be an integer from 18 to 99 (inclusive).
* When editing tags, all of the person's existing tags are removed; adding tags is not cumulative.
* To remove all of a person's tags, enter `t/` without a tag after it.
* Use `s/yes` or `s/no` to update smoking status. Omitting `s/` preserves the existing status. Clearing a recorded smoking status through a command is not supported yet; an empty `s/` is rejected.
* Use outer `r/RELIGION` to update the client's own religion. Empty outer `r/` clears only the client's own religion; omitting it preserves it. Partner criteria and other contact details remain unchanged unless separately edited.
* Use `{r/RELIGION}` to set the preferred partner religion and `<r/RELIGION>` to set an excluded partner religion. The same validation rules as [Religion fields](#religion-fields) apply, including checking the resulting record for a religion that is both preferred and excluded.
* **A non-empty environment adds or updates only the supplied criteria.** For example, `{r/Buddhism}` changes the preferred religion while preserving existing age, gender, smoking and relationship-goal preferences. The same rule applies to exclusions/dealbreakers in `<...>`.
* Omit an environment to preserve its entire bucket. Use `{}` to clear all preferences, or `<>` to clear all exclusions/dealbreakers. These do not clear the client's own religion. `{r/}` and `<r/>` are invalid, not clearing commands. A rejected edit leaves the whole record unchanged.
* The removed `rp/`, `rr/`, and `rx/` prefixes are rejected, including empty values.
* Use `rg/RELATIONSHIP_GOAL` to update the relationship goal. Omitting `rg/` preserves the existing goal, and `rg/` with no value clears it.

Examples:

*  `edit 1 p/91234567 e/johndoe@example.com` Edits the phone number and email address of the 1st person to be `91234567` and `johndoe@example.com` respectively.
*  `edit 2 n/Betsy Crower t/` Edits the name of the 2nd person to be `Betsy Crower` and clears all existing tags.
*  `edit 1 r/Christianity` Sets the client's own religion.
*  `edit 1 r/` Clears the client's own religion to unknown, preserving both partner-criteria buckets.
*  `edit 1 {r/Buddhism} <r/Islam>` Updates the preferred religion to Buddhism and the excluded religion to Islam; other criteria, own religion and other client details are preserved.
*  `edit 1 {r/Buddhism age/25}` Updates the preferred religion to Buddhism and preferred age to 25; other preferences and all exclusions are preserved.
*  `edit 1 {}` Clears all preferences, preserving exclusions and own religion.
*  `edit 1 <>` Clears all exclusions/dealbreakers, preserving preferences and own religion.
*  `edit 3 rg/short-open` Records the 3rd displayed client as looking for a short-term relationship, open to long, preserving their other details.
*  `edit 3 rg/` Clears the 3rd displayed client's relationship goal.
* `edit 1 g/nb` changes the first displayed client's gender to non-binary.
* `edit 1 g/` clears that client's gender.

### Finding clients: `find`

Search by name, age, or the client's own gender, religion, relationship goal or smoking status, or by compatibility with a client. Each search examines the full client list and replaces the displayed results.

Formats:

* `find KEYWORD [MORE_KEYWORDS]` searches names. Matching is case-insensitive and uses whole words; a client is returned if any keyword matches.
* `find age/AGE` or `find age/MIN-MAX` searches an exact age or an inclusive range. Ages must be integers from 18 to 99, with no spaces within the range and `MIN` no greater than `MAX`.
* `find g/GENDER[,GENDER]…` returns clients matching any listed gender: `m`, `w`, or `nb`, in any order, ignoring case and surrounding whitespace.
* `find g/` returns every client with a specified gender, equivalent to `find g/m,w,nb`. Unspecified genders are excluded from gender searches.
* `find r/RELIGION` searches one of the supported [religion categories](#religion-fields), using the same case and whitespace normalization as `add` and `edit`. It matches the client's **own religion**, not partner criteria.
* A client with unknown religion does not match any religion search. `find r/No religion` matches only clients explicitly recorded as `No religion`, not those with an unset `r/`.
* `find rg/GOAL` returns clients with that relationship goal. `GOAL` accepts the same keywords and full names as `add` and `edit`, such as `life`, `long`, `long-open`, `short-open`, `short` or `unsure`. Clients with no recorded goal are excluded.
* `find s/yes` or `find s/no` searches the client's **own smoking habit**, ignoring case and surrounding whitespace in the value. Clients with unspecified smoking status are excluded from both searches; unspecified does not mean non-smoker. Partner preferences and dealbreakers do not affect these results.
* A blank `s/`, unsupported values (such as `unknown`), repeated `s/` prefixes and lists such as `s/yes,no` are rejected.
* `find match/INDEX` lists clients compatible with the client at `INDEX` in the displayed list, best matches first. It uses the same rules as [`match`](#matching-two-clients-match): a client is hidden if either of the two has a dealbreaker the other is known to have (for example, a client saved with `<s/yes>` never sees smokers). The rest are sorted by how many of each other's preferences they meet. Unrecorded details never hide anyone. Use `match INDEX_A INDEX_B` to see the full breakdown for one pair. `list` or any other `find` restores the normal order.
* A blank `r/`, an unsupported category, repeated `r/` prefixes or a list of religions are rejected. Partial category names do not match.

Use one search mode at a time. Name keywords, age, gender, religion, relationship-goal, smoking and match criteria cannot be combined. Combined-trait searches and searches within preference/dealbreaker environments are not supported in this increment.

Use each prefix only once. Duplicate gender values (including `m,M`), unsupported values, and empty items such as `m,` or `m,,w` are rejected. Gender lists are supported only by `find`, not by `add` or `edit`. Commands and prefixes must be lowercase; separate each prefix from preceding text with a space. Invalid commands leave records and displayed results unchanged.

Examples:

* `find Alex Sam` finds clients whose names contain Alex or Sam.
* `find age/25-35` finds clients aged 25 to 35, inclusive.
* `find g/w` finds women.
* `find g/m,nb` finds men or non-binary clients.
* `find r/jAiNiSm` finds clients whose own religion is Jainism.
* `find r/Baha'i   Faith` finds clients recorded as Baha'i Faith, ignoring repeated spaces.
* `find r/No religion` finds clients who explicitly have no religion.
* `find rg/long` finds clients looking for a long-term relationship.
* `find s/YES` finds clients recorded as smokers.
* `find s/no` finds clients explicitly recorded as non-smokers.
* `find match/2` lists clients compatible with the 2nd displayed client, best matches first.

A valid search with no matches displays an empty result list and a count of zero.
Use `list` to show every client again; searching does not modify the stored records.

### Matching two clients: `match`

Assesses two clients against each other's saved preferences and dealbreakers.

Format: `match INDEX_A INDEX_B`

* Use two different positive indexes from the **currently displayed list**, including after `find`.
* Exactly two indexes are required. Invalid, repeated, or out-of-range indexes display an error.
* The command-result area shows both names and a colored result line: green for compatible, red for incompatible,
  and amber for insufficient information. Each direction has a table showing type, attribute, criterion, actual value,
  and status (**Met**, **Unmet**, **Violated**, or **Unknown**). Dealbreakers show the excluded value explicitly.
* The displayed list and client records stay unchanged. This command does not save a pairing or comparison history.

Set partner criteria with `add` or `edit`: `{...}` contains preferences and `<...>` contains **excluded values**.
For example, `edit 1 {age/25-35 rg/long} <s/yes>` prefers ages 25–35 and a long-term relationship,
and excludes smokers. Ordinary fields outside the blocks describe the client's own attributes.

Each block is optional and may appear once. Inside a block, use each of `age/`, `g/`, `s/`, `r/`, or `rg/` at most once.
Age accepts an exact age or inclusive range, with bounds from 18 to 99.
Gender accepts one value: `g/m`, `g/w`, or `g/nb`, ignoring case. Empty values and gender lists are rejected inside blocks.
Other criteria use the same values as the client's own fields and compare exact normalized categories.
Different relationship-goal categories are not treated as equivalent.
When editing, a non-empty block adds or updates only its supplied criteria; other criteria are preserved.
Use `{}` to clear preferences or `<>` to clear dealbreakers.

Overall results, in priority order:

1. **Incompatible**: either client violates at least one of the other's dealbreakers.
2. **Insufficient information**: no known violation, but an attribute needed for a recorded criterion is unknown,
   or neither client has any criteria.
3. **Compatible with recorded dealbreakers**: all recorded criteria can be evaluated and no dealbreaker is violated.
   Unmet preferences alone do not make a pair incompatible.

Missing gender, smoking status, religion, or relationship goal is unknown, not a match or a violation.
An omitted criterion adds no constraint. `No religion` and `unsure` are explicit values, not missing information.

Example: `list` followed by `match 1 3` assesses the first and third clients without changing their records.

### Organising clients into groups: `grp` and `show`

Keep named collections of clients, such as event invitations or a shortlist. A client can belong to
several groups. Membership is shown as a separate `Group: NAME` badge; ordinary `t/` tags are unchanged.

Action | Format | Example
-------|--------|--------
Create a group or add members | `grp NAME add INDEX [MORE_INDEXES]` | `grp VIP add 1 3`
Remove members | `grp NAME remove INDEX [MORE_INDEXES]` | `grp VIP remove 2`
Create a matching shortlist | `grp NAME matches INDEX` | `grp Candidates matches 1`
Show a group's members | `show NAME` | `show VIP`

**Names and indexes**

* A name has 1–30 ASCII letters (`A`–`Z`, `a`–`z`), digits, hyphens or underscores, and starts with a letter or digit.
  Spaces and prefixes such as `/` or `g/` are not accepted. Names are case-insensitive: `VIP` and `vip` identify
  the same group. The spelling used when the group was created is retained on badges and in feedback.
* Every index refers to the **currently displayed list**, including after `find` or `show`. Use positive integers
  separated by spaces, not commas. A repeated, invalid or out-of-range index rejects the entire command.
* Commands and operation words are lowercase (`grp`, `add`, `remove`, `matches`, `show`).

**Adding, removing and showing members**

* `add` creates a missing group with the selected clients. One member is sufficient. For an existing group,
  it adds the selected clients; if any selected client is already a member, nothing is added.
* `remove` requires an existing group. If any selected client is not a member, nothing is removed.
  Removing membership does **not** delete the client. Removing the last member retains an empty group.
* `show` searches all clients and displays the group's members in address-book order. An unknown group reports
  an error and leaves the current list unchanged; an empty group shows zero clients. `list` restores all clients.
* Membership changes update the list immediately when that group is being shown. Otherwise, they leave the
  current list unchanged. To add someone outside a shown group, run `list` or `find` first.
* Editing a client, including their name or tags, preserves their memberships. The existing `edit` and `add`
  commands restore the full client list; use `show NAME` again to return to the group.
  `delete INDEX` deletes the client record and removes it from every group. Re-adding that client later does
  not restore old memberships. `clear` removes both all clients and all groups.
* Group renaming, deleting an entire group, and listing all group names are not supported in this version.

Example: With at least three clients displayed, run `grp VIP add 1 3`, then `show VIP`.
The two members now have displayed indexes 1 and 2. `grp VIP remove 2` removes the second displayed member
from VIP, leaving their client record intact. `list` shows all clients again.

**Creating a matching shortlist**

`grp Candidates matches 1` creates a **new** group using the same eligibility rule as `find match/1`:
it checks the first displayed client against every other client and excludes known dealbreaker violations.
The source client is not included. Candidates with insufficient information are included, so group membership
is **not a guarantee of compatibility**. Unmet preferences alone do not exclude a candidate.

The command takes exactly one displayed index. An existing group name or no eligible candidates causes an
error without changing any group. The current displayed list stays unchanged; run `show Candidates` to see
the members in address-book order, rather than the preference ranking used by `find match/1`.

Membership is a snapshot: adding clients or changing dating attributes and criteria does not automatically
recompute the shortlist. Edits still update the member's details, and deleting a member removes them from
the group. Use `add`/`remove` to adjust membership or create another shortlist with a new name.

### Deleting a person: `delete`

Deletes the specified client from CupidMaxxing.

Format: `delete INDEX`

* Deletes the client at the specified `INDEX`.
* The index refers to the index number shown in the displayed client list.
* The index **must be a positive integer** 1, 2, 3, …​

Examples:

* `list` followed by `delete 2` deletes the 2nd client in CupidMaxxing.
* `find g/nb` followed by `delete 1` deletes the 1st client in the search results.

### Clearing all entries: `clear`

Clears all clients and groups from the address book.

Format: `clear`

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

CupidMaxxing automatically saves data after every successful command. You do not need to save manually. Gender is saved with each client; valid existing records without a gender load as unspecified. The existing required-age rule still applies.

Groups and memberships are saved in the same file as the clients and restored when the app restarts.
Older valid files without a `groups` field load with no groups.

The app's **home folder** is the folder containing its JAR, even if you launch it
from a different working folder. Keep that folder writable. Client data is stored
in `data/addressbook.json`, window preferences in `preferences.json`, and rotating
logs in `addressbook.log.*`, all inside that home folder. JavaFX runtime caches and
temporary files also stay there, in `.javafx/` and `.tmp/`. The app does not change
OS-level settings. Paths and symbolic links leading outside the home folder are
not supported for application data; move the data itself into the home folder instead.

The private `.javafx/` and `.tmp/` runtime folders must be ordinary folders, with no
symbolic links at either folder's root or anywhere inside, even links pointing back
inside the home folder. This prevents inward links from hiding outward links in their
targets. An unsafe or unwritable runtime layout stops startup before JavaFX starts;
the app does not automatically delete or rewrite existing caches or client data.
In-home symbolic links for normal data and preferences retain their existing behavior;
the stricter no-symbolic-link rule applies only to these two runtime folders.

### Editing the data file

CupidMaxxing data is saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Advanced users are welcome to update data directly by editing that data file.

Groups use a top-level array alongside `persons`, for example:

```json
"groups": [{"name": "VIP", "memberNames": ["Alice Pauline", "Benson Meier"]}]
```

Each member name must exactly match one existing client's name, including letter case. Reordering the
`persons` array does not change group membership. If you manually rename or remove a client, also update
every group's `memberNames` references; edits made through the app handle this automatically. Empty
`memberNames` arrays are valid. Duplicate group names (ignoring case), repeated or missing members,
invalid names and malformed entries reject the data file. Back up the file and close the app before editing it.

Records with valid required fields but no own-religion property load with unknown
own religion, not `No religion`. Partner criteria are saved in the `preferences` and
`dealbreakers` maps: for example, `"preferences": {"religion": "Buddhism"}` and
`"dealbreakers": {"religion": "Islam"}`. The latter records an exclusion, not a requirement
to have Islam. Older valid records without these maps load with empty partner criteria.
Invalid religion values (including a null religion entry inside a criteria map), or the
same normalized religion in both maps, reject the file through the existing invalid-data
handling. A missing or null own-religion property still means unknown.
Obsolete `preferredReligion`, `requiredReligion`, and `excludedReligions` properties are
ignored when loading and omitted on the next save; they are not migrated into the new
maps. Valid own religion and other contact details are retained. Back up older development
data before migrating it; replace an obsolete `Other` religion only with an accurate
supported category, or remove that optional property if the religion is unknown.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If your changes make the data file invalid, AddressBook starts with an empty address book at the next run. The invalid file remains on disk until you run a command (AddressBook saves after every command). Still, we recommend backing up the file before editing it.<br>
Furthermore, certain edits can cause the AddressBook to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</div>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous AddressBook home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

`CRITERIA` uses the partner-attribute prefixes described under [Matching two clients](#matching-two-clients-match).

Action | Format, Examples
--------|------------------
**Add** | `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS age/AGE [s/SMOKING] [g/GENDER] [rg/RELATIONSHIP_GOAL] [t/TAG]…​ [r/RELIGION] [{CRITERIA}] [<CRITERIA>]` <br> e.g., `add n/James Ho p/22224444 e/jamesho@example.com a/123, Clementi Rd, 1234665 age/25 rg/long t/friend t/colleague`
**Clear** | `clear`
**Delete** | `delete INDEX`<br> e.g., `delete 3`
**Edit** | `edit INDEX [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [age/AGE] [s/SMOKING] [g/GENDER] [rg/RELATIONSHIP_GOAL] [t/TAG]…​ [r/RELIGION] [{CRITERIA}] [<CRITERIA>]`<br> e.g., `edit 2 n/James Lee e/jameslee@example.com`
**Find** | `find KEYWORD [MORE_KEYWORDS]`, `find age/AGE`, `find age/MIN-MAX`, `find g/GENDER[,GENDER]…`, `find g/`, `find r/RELIGION`, `find rg/GOAL`, `find s/yes`, `find s/no`, or `find match/INDEX`<br> e.g., `find James Jake`, `find age/25-35`, `find g/m,nb`, `find r/Jainism`, `find rg/long`, `find s/no`, `find match/2`
**List** | `list`
**Group members** | `grp NAME add INDEX [MORE_INDEXES]` or `grp NAME remove INDEX [MORE_INDEXES]`<br> e.g., `grp VIP add 1 3`, `grp VIP remove 2`
**Group matches** | `grp NAME matches INDEX`<br> e.g., `grp Candidates matches 1`
**Show group** | `show NAME`<br> e.g., `show VIP`
**Match** | `match INDEX_A INDEX_B`<br> e.g., `match 1 3`
**Help** | `help`
**Exit** | `exit`
