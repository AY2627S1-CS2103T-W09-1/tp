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

| Error handled | Message shown |
| --- | --- |
| None | `help` ignores additional text. |

Examples:

* `help`
* `help 123` (additional text is ignored)

### Listing all clients: `list`

Shows every client in CupidMaxxing and restores the normal display order.

Format: `list`

| Error handled | Message shown |
| --- | --- |
| None | `list` ignores additional text. |

Examples:

* `list`
* `list all`


### Adding a client: `add`

Adds a client to CupidMaxxing with contact details and dating attributes. Tags, gender, smoking status,
religion and partner criteria are optional.

Format: `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS age/AGE [s/SMOKING] [g/GENDER] [t/TAG]…​ [r/RELIGION] [{CRITERIA}] [<CRITERIA>]`

* `AGE` is compulsory. The valid values for `AGE`, `SMOKING`, `GENDER`, `RELIGION` and
  `RELATIONSHIP_GOAL` are listed in [Parameter list](#parameter-list).
* Each prefix outside a criteria block may be supplied only once. Values entered here describe the
  client's profile.
* Criteria blocks constitute preferences and dealbreakers that the client has for their partner. See
  [Adding a preference or dealbreaker](#adding-a-preference-or-dealbreaker) and the
  [Parameter list](#parameter-list) for their syntax and value rules.

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
A person can have any number of tags, including zero. Tags are user-defined labels, such as
`newclient`, `referral` and `consultationpending`, for organising client records. They are not
updated automatically and do not affect compatibility results. Named groups are managed separately
using [`grp` and `show`](#organising-clients-into-groups-grp-and-show).
</div>

Examples:

* `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01 age/25`
* `add n/Sarah Tan p/91234567 e/sarah@example.com a/12 Clementi Rd age/28 g/w` Records Sarah as a woman.
* `add n/Alice Tan p/91234567 e/alice@example.com a/Clementi age/30 s/no` Records Alice as a non-smoker.
* `add n/Priya Nair p/90001111 e/priya@example.com a/8 Tampines St age/28 {rg/long}` Records Priya as looking for a long-term relationship.

| Error handled | Message shown |
| --- | --- |
| Missing compulsory fields, malformed prefixes, or invalid profile values | `Invalid command format!` followed by the command usage or the relevant parameter constraint |
| A single-valued field is supplied more than once | `Multiple values specified for the following single-valued field(s): ...` |
| A client with the same identity already exists | `This person already exists in the address book.` |


### Editing a client: `edit`

Edits the client at `INDEX` in the currently displayed list. `INDEX` must be a positive integer.

Format: `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [age/AGE] [s/SMOKING] [g/GENDER] [t/TAG]…​ [r/RELIGION]`

* Edits the person at the specified `INDEX`. The index refers to the index number shown in the displayed person list. The index **must be a positive integer** 1, 2, 3, …​
* At least one of the optional fields must be provided.
* A successful edit restores the full client list. Find the client again before reusing an index from filtered results.
* Existing values will be updated to the input values.
* Valid dating-attribute values are listed in [Parameter list](#parameter-list). Use `g/m`, `g/w`, or
  `g/nb` to change gender; empty `g/` clears it, and omitting `g/` preserves it.
* When editing tags, all of the person's existing tags are removed; adding tags is not cumulative.
* To remove all of a person's tags, enter `t/` without a tag after it.
* Use `s/yes` or `s/no` to update smoking status. Omitting `s/` preserves the existing status. Clearing a recorded smoking status through a command is not supported yet; an empty `s/` is rejected.
* Relationship goals are recorded only as partner criteria, e.g. `{rg/long}`. See [Relationship goals](#relationship-goals).

Examples:

*  `edit 1 p/91234567 e/johndoe@example.com` Edits the phone number and email address of the 1st person to be `91234567` and `johndoe@example.com` respectively.
*  `edit 2 n/Betsy Crower t/` Edits the name of the 2nd person to be `Betsy Crower` and clears all existing tags.
*  `edit 1 r/Christianity` Sets the client's own religion.
*  `edit 1 r/` Clears the client's own religion to unknown, preserving both partner-criteria buckets.
*  `edit 3 {rg/short-open}` Records the 3rd displayed client as looking for a short-term relationship, open to long, preserving their other details.
* `edit 1 g/nb` changes the first displayed client's gender to non-binary.
* `edit 1 g/` clears that client's gender.

| Error handled | Message shown |
| --- | --- |
| No field is supplied | `At least one field to edit must be provided.` |
| The index is invalid or not displayed | `The person index provided is invalid.` |
| The edit would duplicate a client | `This person already exists in the address book.` |
| A supplied field is malformed or invalid | The relevant parameter constraint or `Invalid command format!` |

### Adding a preference or dealbreaker

Use a preference to record a value a client would like in a partner, and a dealbreaker to record a
value a partner must not have. These criteria can be supplied with `add`. Attributes
contained within the specified blocks describe attributes that the client desires their partner to have.

Format: `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS age/AGE ... [{CRITERIA}] [<CRITERIA>]`

`{...}` contains preferences and `<...>` contains excluded values (dealbreakers). Each block is optional
and may appear once. Inside a block, use each of `age/`, `g/`, `s/`, `r/`, or `rg/` at most once.
The accepted values are listed in [Parameter list](#parameter-list).


Examples:

* `add n/Religion Demo p/91234567 e/religion@example.com a/Test Lane age/25 {r/Buddhism} <r/Islam>`
records a religion preference and dealbreaker.
* `add n/Example p/91234567 e/example@example.com a/Test Lane age/25 {age/25-35 rg/long} <s/yes>`
records an age and relationship-goal preference and excludes smokers.

### Editing a preference or dealbreaker

Edits the recorded criteria of the client at `INDEX` without changing omitted criteria or profile attributes.

Format: `edit INDEX [{CRITERIA}] [<CRITERIA>]`

When editing, a non-empty block preserves omitted criteria. Age and smoking are single-valued: a new
value replaces the old value. Gender, religion, and relationship goal are multi-valued: new values are
added and values already recorded are retained with a duplicate warning. An empty attribute prefix
inside an `edit` block removes age, gender, religion, or relationship-goal criteria; empty smoking
criteria have no effect. Use `{}` to clear all preferences or `<>` to clear all dealbreakers. See
[Parameter list](#parameter-list) for each attribute's value and contradiction rules.

Examples:

* `edit 1 {r/Buddhism age/25} <s/yes>`
* `edit 1 {} <>`

| Error handled | Message shown |
| --- | --- |
| A block is nested, repeated, unbalanced, or malformed | `Dating criteria must use one non-nested {...} preference block and one non-nested <...> dealbreaker block.` |
| A block uses an unsupported prefix | `Only age/, g/, s/, r/, and rg/ are supported inside dating criteria.` |
| A value is invalid, or retained and newly supplied criteria contradict each other | The relevant attribute constraint; the command makes no change. |



### Finding clients: `find`

Search by name, age, or the client's own gender, religion or smoking status, their relationship-goal preference, or compatibility with a client. Each search examines the full client list and replaces the displayed results.

Formats:

* `find n/KEYWORD [MORE_KEYWORDS]` searches names. Matching is case-insensitive and uses whole words; a client is returned if any keyword matches. The `n/` prefix is required, like the other search modes: `find Alex` without it is rejected. A blank `n/` and repeated `n/` prefixes are also rejected.
* `find n/"PHRASE"` searches for a quoted phrase, which may contain `/`. A client is returned if the phrase's words appear together, in order, as whole words of the name, ignoring case. Use it for names such as `Ravi s/o Kumar`, since an unquoted keyword containing `/` is rejected.
* `find age/AGE` or `find age/MIN-MAX` searches an exact age or an inclusive range. See [Age](#age)
  for accepted values and range rules.
* `find g/GENDER[,GENDER]…` returns clients matching any listed gender: `m`, `w`, or `nb`, in any order, ignoring case and surrounding whitespace.
* `find g/` returns every client with a specified gender, equivalent to `find g/m,w,nb`. Unspecified genders are excluded from gender searches.
* `find r/RELIGION` searches the client's **own religion**, not partner criteria. Use one category from the
  [religion reference table](#religion), which also explains unknown values and invalid inputs.
* `find rg/GOAL[,GOAL]...` returns clients whose relationship-goal preference includes any listed goal. `GOAL` is a keyword from [Relationship goals](#relationship-goals), such as `long` or `long-open`. Clients with no goal preference are excluded.
* `find s/yes` or `find s/no` searches the client's **own smoking habit**, ignoring case and surrounding whitespace in the value. Clients with unspecified smoking status are excluded from both searches; unspecified does not mean non-smoker. Partner preferences and dealbreakers do not affect these results.
* A blank `s/`, unsupported values (such as `unknown`), repeated `s/` prefixes and lists such as `s/yes,no` are rejected.
* `find match/INDEX` lists suitable matches for one client. See [Finding matches for one client](#finding-matches-for-one-client-find-match).

Use one search mode at a time. Name keywords, age, gender, religion, relationship-goal, smoking and match criteria cannot be combined. Combined-trait searches are not supported in this increment.

Use each prefix only once. Duplicate gender values (including `m,M`), unsupported values, and empty items such as `m,` or `m,,w` are rejected. Gender lists are supported by `find` and inside partner-criteria blocks, but not by outer `add` or `edit` fields. Commands and prefixes must be lowercase; separate each prefix from preceding text with a space. Invalid commands leave records and displayed results unchanged.

Examples:

* `find n/Alex Sam` finds clients whose names contain Alex or Sam.
* `find n/"Ravi s/o"` finds `Ravi s/o Kumar`, but not `Kumar Ravi`.
* `find age/25-35` finds clients aged 25 to 35, inclusive.
* `find g/w` finds women.
* `find g/m,nb` finds men or non-binary clients.
* `find r/jAiNiSm` finds clients whose own religion is Jainism.
* `find r/Baha'i   Faith` finds clients recorded as Baha'i Faith, ignoring repeated spaces.
* `find r/No religion` finds clients who explicitly have no religion.
* `find rg/long,long-open` finds clients open to a long-term relationship, or to a long-term one open to short.
* `find s/YES` finds clients recorded as smokers.
* `find s/no` finds clients explicitly recorded as non-smokers.

A valid search with no matches displays an empty result list and a count of zero.
Use `list` to show every client again; searching does not modify the stored records.

| Error handled | Message shown |
| --- | --- |
| More than one search mode, an empty name search, or malformed syntax | `Invalid command format!` followed by the `find` usage |
| An unquoted name keyword contains `/` | `Name keywords cannot contain /. To search for a name containing /, put it in quotes, e.g. find n/"Ravi s/o".` |
| A quoted name is not closed, or text follows the closing quote | `A value that starts with " must end with a matching "...` or `Unexpected text after a quoted value...` |
| An attribute value or gender list is invalid | The relevant attribute constraint or `Gender list must contain one or more of m, w, nb without duplicates.` |

### Finding matches for one client: `find match/`

Lists the clients who suit one client, best matches first.

Format: `find match/INDEX`

* `INDEX` is the client's position in the **currently displayed list**. The search then covers every other client.
* A client is hidden if either of the two has a dealbreaker the other is known to have. For example, a client
  saved with `<s/yes>` never sees smokers.
* The rest are sorted by how many of each other's preferences they meet. Unrecorded details never hide anyone.
* `list` or any other `find` restores the normal order. Records are not changed.
* To see why one pair suits each other or not, use [`match`](#matching-two-clients-match).

Examples:

* `find match/2` lists clients compatible with the 2nd displayed client, best matches first.
* `find n/Alex` followed by `find match/1` lists matches for the first Alex found.

| Error handled | Message shown |
| --- | --- |
| The index is not a positive integer | `Index must be a positive integer.` |
| The index is outside the displayed list | `The person index provided is invalid.` |
| `match/` is combined with another search mode | `Invalid command format!` followed by the `find` usage |

### Matching two clients: `match`

Shows whether two clients suit each other, checking each client's preferences and dealbreakers against the other.

Format: `match INDEX_A INDEX_B`, `match n/NAME_A n/NAME_B`, or `match INDEX_A n/NAME_B`

* Give exactly two different clients. Each is either a positive index from the **currently displayed list** or
  `n/` followed by the client's name. Indexes come before names.
* A name is looked up among **all** clients, so you do not need to `find` a client first. An exact name is used if
  one exists; otherwise the name may differ in letter case and spacing, e.g. `n/alex   yeoh` finds `Alex Yeoh`.
* The result replaces the client list until your next command. It shows the overall result in color, then one
  table per direction listing each criterion, the other client's value, and its status.
* The displayed list and client records stay unchanged, and no pairing is saved.

Each criterion gets one status:

| Status | Meaning |
| --- | --- |
| **Met** | The other client has a preferred value, or does not have an excluded one. |
| **Unmet** | The other client does not have a preferred value. This alone never makes a pair incompatible. |
| **Violated** | The other client has an excluded value. The pair is **Incompatible**. |
| **Unknown** | The other client has not recorded that attribute. Without a violation, the pair has **Insufficient information**. |

A pair with no violated or unknown criteria is **Compatible with recorded dealbreakers**.
Criteria are recorded as described in [Adding a preference or dealbreaker](#adding-a-preference-or-dealbreaker).

Examples:

* `match 1 3` assesses the first and third displayed clients.
* `match n/Alex Yeoh n/Bernice Yu` assesses those two clients, whether or not they are displayed.
* `match 2 n/Bernice Yu` assesses the second displayed client and Bernice Yu.

| Error handled | Message shown |
| --- | --- |
| Not exactly two clients, a malformed index, or a name before an index | `Invalid command format!` followed by the `match` usage |
| An index is outside the displayed list | `The person index provided is invalid.` |
| Both refer to the same client | `Choose two different clients.` followed by the `match` usage |
| No client has that name | `No client is named NAME.` |
| Several clients have that name, ignoring case and spacing | `Several clients are named NAME. Use their list indexes instead.` |
| A name is invalid | The name constraint |

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

### Parameter list

The following attributes are shared by profile recording, searching, and preference/dealbreaker criteria.

#### Age

`age/AGE` records a client's age. A profile age must be one integer from 18 to 99, inclusive.
Partner criteria and searches may instead use an inclusive range, such as `age/25-35`; both bounds
must be from 18 to 99 and the minimum cannot exceed the maximum. Spaces around a range's hyphen are
accepted and normalized to `MIN-MAX`.

| Valid values | Bad values |
| --- | --- |
| `18`, `25`, `99`; `25-35` in criteria or `find` | `17`, `100`, `twenty`, `35-25`, `25-100` |

| Scope | Syntax | Meaning |
| ----- | ------ | ------- |
| Client characteristic | Outer `age/AGE` | The client's own age. It is compulsory for `add`. |
| Partner preference | `{age/AGE_OR_RANGE}` | An age or inclusive age range the client would like a partner to have. |
| Partner dealbreaker | `<age/AGE_OR_RANGE>` | An age or inclusive age range the partner **must not** fall within. |
| Search | `find age/AGE_OR_RANGE` | Search clients by their own exact age or inclusive age range. |

* The outer `age/` may occur once only. `edit INDEX age/AGE` replaces the client's recorded age;
  an empty outer `age/` is invalid.
* Each preference or dealbreaker block may contain `age/` once only and may contain only one age or
  range. On `edit`, a value adds the criterion when absent or replaces its entire previous value.
  Intervals are **not** combined or expanded: even a contained or intersecting new interval replaces
  the old interval. Repeating the identical value leaves it unchanged and reports a duplicate warning.
  On `edit`, `{age/}` or `<age/>` removes the age criterion; `{}` and `<>` clear their entire
  criterion buckets.
* A preferred age range cannot overlap an excluded age range, including a retained criterion. A
  conflicting command is rejected and leaves the record unchanged.

#### Smoking

`s/SMOKING` records the client's own smoking habit. Use `s/yes` for a smoker or `s/no` for a
non-smoker; values are case-insensitive and surrounding whitespace is ignored. Omit the field if the
status is unknown. Older records without this field display `Not specified`.

| Valid values | Bad values |
| --- | --- |
| `s/yes`, `s/no`, `s/YES` | `s/unknown`, `s/yes,no` |

| Scope | Syntax | Meaning |
| ----- | ------ | ------- |
| Client characteristic | Outer `s/yes` or `s/no` | The client's own smoking habit. |
| Partner preference | `{s/yes}` or `{s/no}` | A smoking habit the client would like a partner to have. |
| Partner dealbreaker | `<s/yes>` or `<s/no>` | A smoking habit the partner **must not have**. |
| Search | `find s/yes` or `find s/no` | Search the client's own smoking habit only. |

* The outer `s/` may occur once only and must have a value. `edit INDEX s/yes` or `edit INDEX s/no`
  replaces the recorded status; omitting `s/` preserves it. Clearing an outer smoking status is not
  supported.
* Each preference or dealbreaker block may contain `s/` once only and may contain only `yes` or `no`.
  On `edit`, a new value adds the criterion when absent or replaces its previous value. Repeating the
  identical value leaves it unchanged and reports a duplicate warning. An empty criterion `s/` has no
  effect; use `{}` or `<>` to clear the whole bucket.
* The same smoking status cannot be both preferred and excluded, including criteria retained from an
  earlier edit. A conflicting command is rejected and leaves the record unchanged.

#### Religion

Use one of these 21 categories: `Christianity`, `Islam`, `Hinduism`, `Buddhism`,
`Sikhism`, `Judaism`, `Jainism`, `Baha'i Faith`, `Shinto`, `Taoism`, `Confucianism`,
`Zoroastrianism`, `Rastafari`, `Wicca`, `Paganism`, `Tenrikyo`, `Cao Dai`, `Druze`,
`Atheism`, `Agnosticism`, or `No religion`. Case and repeated spaces are normalized:
`r/jAiNiSm` is saved as `Jainism`. Partial names, misspellings, free text and `Other` are not
supported. Comma-separated lists of categories are permitted only inside a partner criterion block.

| Scope | Syntax | Meaning |
| ----- | ------ | ------- |
| Client characteristic | Outer `r/RELIGION` | The client's own religion |
| Partner preference | `{r/RELIGION[,RELIGION]...}` | One or more religions the client would like a partner to have |
| Partner dealbreaker | `<r/RELIGION[,RELIGION]...>` | One or more religions the partner **must not have** |
| Search | `find r/RELIGION` | Search the client's **own** religion only |

* Unknown religion is different from the explicit answer `No religion`. Do not select
  `No religion` just because none of the listed categories applies.
* Type `{}` and `<>` literally. A command permits one non-nested environment of each type, one
  outer `r/`, and one `r/` within each environment. An outer `r/` is empty only when using `edit`,
  where it clears the client's own religion. In a criterion block, use a comma-separated list such
  as `{r/Buddhism,Jainism}` to record several partner religions.
* Own religion is independent of partner criteria. No normalized religion can be both preferred and
  excluded; this is checked against the **resulting record**, including criteria retained from a
  previous command. If even one religion conflicts, the command is rejected and leaves the record
  unchanged.
* `edit INDEX r/RELIGION` replaces the client's own religion; `edit INDEX r/` clears it. Within a
  criterion block, an edit adds each religion not already recorded and retains any duplicate religion,
  reporting it as a warning. A value repeated within the same comma-separated list is stored once.
  It does not replace existing partner-religion values. A non-empty edit environment preserves other
  criteria; `{}` or `<>` clears its entire bucket. All values persist across restarts.

Example: `add n/Religion Demo p/91234567 e/religion@example.com a/Test Lane age/25 r/Jainism {r/Buddhism} <r/Islam>`
records own Jainism, preferred Buddhism and excluded Islam; contact details and age stay outside the environments.

| Invalid input | How to correct it |
| ------------- | ----------------- |
| `add ... r/` or `find r/` | Supply a listed category; omit `r/` on `add` if unknown |
| `{r/Islam} <r/islam>` | Choose criteria that do not prefer and exclude the same religion |
| Repeated `r/` or `r/Other` | Supply one `r/` prefix and listed categories; comma-separated lists are allowed only inside a criterion block |
| Legacy `rp/`, `rr/`, or `rx/`, including empty values | Use the current scope syntax in the table above |

Rejected commands leave the stored records unchanged. See [Finding clients](#finding-clients-find)
for the single-search-mode restriction.

#### Gender

`g/GENDER` records a client's own gender: `m` (man), `w` (woman), or `nb` (non-binary). Values are
case-insensitive and surrounding whitespace is ignored. Omit `g/`, or leave it empty when adding or
editing, for unspecified gender. A gender list is accepted by `find` and inside a partner criterion
block only.

| Valid values | Bad values |
| --- | --- |
| `g/m`, `g/w`, `g/nb`, `g/` (unspecified); `{g/m,w}`; `find g/m,nb` | `g/man`, `g/m,w` outside a criterion block, `g/m,m` in `find` |

| Scope | Syntax | Meaning |
| ----- | ------ | ------- |
| Client characteristic | Outer `g/m`, `g/w`, or `g/nb` | The client's own gender. |
| Partner preference | `{g/GENDER[,GENDER]...}` | One or more genders the client would like a partner to have. |
| Partner dealbreaker | `<g/GENDER[,GENDER]...>` | One or more genders the partner **must not have**. |
| Search | `find g/GENDER[,GENDER]...` | Search the client's own gender. `find g/` matches any specified gender. |

* The outer `g/` may occur once only. `edit INDEX g/GENDER` replaces the recorded gender, while
  `edit INDEX g/` clears it. In `find`, every comma-separated gender must be distinct; lists are not
  accepted for outer `add` or `edit` fields.
* Each preference or dealbreaker block may contain `g/` once only and may use a comma-separated list.
  On `edit`, each new gender is added; a gender already recorded is retained and reported as a duplicate
  warning. A value repeated within the same comma-separated list is stored once. On `edit`, `{g/}` or
  `<g/>` removes all partner-gender values in that bucket; `{}` and `<>` clear the whole bucket.
* No gender can be both preferred and excluded, including criteria retained from an earlier edit. If
  even one gender conflicts, the command is rejected and leaves the record unchanged.

#### Relationship goals

Relationship goals are recorded only as **partner criteria**. A client's `{rg/...}` preference lists the goals
they are open to, and it also describes the client when others are matched against them. There is no separate
`rg/` field for the client's own goal. The accepted goals are adapted from Hinge. Type the keyword; the full goal
name is saved and shown on the client card:

Keyword | Relationship goal
--------|------------------
`life` | Life partner
`long` | Long-term relationship
`long-open` | Long-term relationship, open to short
`short-open` | Short-term relationship, open to long
`short` | Short-term fun
`unsure` | Figuring out my goals

| Valid values | Bad values |
| --- | --- |
| `{rg/long}`, `{rg/long,long-open}`, `<rg/short>`, `{rg/LONG}`; `find rg/long,short` | outer `rg/long`, `{rg/Long-term relationship}`, `{rg/marriage}`, `{rg/long,long}`, `{rg/long,}` |

| Scope | Syntax | Meaning |
| ----- | ------ | ------- |
| Partner preference | `{rg/GOAL[,GOAL]...}` | The goals the client is open to. These also describe the client to others. |
| Partner dealbreaker | `<rg/GOAL[,GOAL]...>` | Goals the partner **must not** be open to. |
| Search | `find rg/GOAL[,GOAL]...` | Clients whose goal preference includes any listed goal. |

* Goals match by **overlap**. `{rg/long}` is met by a partner whose own `{rg/...}` includes `long`. `<rg/short>` is
  violated by a partner whose `{rg/...}` includes `short`.
* A partner with no goal preference is unknown for goal criteria, not a match or a violation.
* `rg/unsure` is a real goal ("Figuring out my goals"), not missing information.
* Data saved by older versions with an own relationship goal is moved into the client's goal preference when loaded,
  unless they already have a goal preference or exclude that goal.

| Invalid input | How to correct it |
| ------------- | ----------------- |
| `add ... rg/long` or `edit 1 rg/long` | Put the goal in a block: `{rg/long}` |
| `{rg/Long-term relationship}` | Use the keyword: `{rg/long}` |
| `{rg/long,long}` or `{rg/long,}` | List each goal once, separated by commas |
| `{rg/long} <rg/long>` | Do not prefer and exclude the same goal |

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

CupidMaxxing automatically saves client data after every successful command except `help` and `exit`,
which do not change client records. These two commands remain available if the client data file cannot
be saved. You do not need to save manually. Gender is saved with each client; valid existing records
without a gender load as unspecified. The existing required-age rule still applies.

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
If your changes make the data file invalid, AddressBook starts with an empty address book at the next run. A successful command other than `help` or `exit` can overwrite the invalid file with the current data. Back up the file before editing it.<br>
Furthermore, certain edits can cause the AddressBook to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</div>


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
**Add** | `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS age/AGE [s/SMOKING] [g/GENDER] [t/TAG]…​ [r/RELIGION] [{CRITERIA}] [<CRITERIA>]` <br> e.g., `add n/James Ho p/22224444 e/jamesho@example.com a/123, Clementi Rd, 1234665 age/25 {rg/long} t/friend t/colleague`
**Clear** | `clear`
**Delete** | `delete INDEX`<br> e.g., `delete 3`
**Edit** | `edit INDEX [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [age/AGE] [s/SMOKING] [g/GENDER] [t/TAG]…​ [r/RELIGION] [{CRITERIA}] [<CRITERIA>]`<br> e.g., `edit 2 n/James Lee e/jameslee@example.com`
**Find** | `find n/KEYWORD [MORE_KEYWORDS]`, `find age/AGE`, `find age/MIN-MAX`, `find g/GENDER[,GENDER]…`, `find g/`, `find r/RELIGION`, `find rg/GOAL[,GOAL]...`, `find s/yes`, `find s/no`, `find n/"PHRASE"` or `find match/INDEX`<br> e.g., `find n/James Jake`, `find n/"Ravi s/o"`, `find age/25-35`, `find g/m,nb`, `find r/Jainism`, `find rg/long,long-open`, `find s/no`, `find match/2`
**List** | `list`
**Group members** | `grp NAME add INDEX [MORE_INDEXES]` or `grp NAME remove INDEX [MORE_INDEXES]`<br> e.g., `grp VIP add 1 3`, `grp VIP remove 2`
**Group matches** | `grp NAME matches INDEX`<br> e.g., `grp Candidates matches 1`
**Show group** | `show NAME`<br> e.g., `show VIP`
**Match** | `match INDEX_A INDEX_B`, `match n/NAME_A n/NAME_B`, or `match INDEX_A n/NAME_B`<br> e.g., `match 1 3`, `match n/Alex Yeoh n/Bernice Yu`
**Help** | `help`
**Exit** | `exit`
