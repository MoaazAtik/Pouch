# Pouch ⭕

Meet **Pouch**: Your compact creative haven. With its intuitive design, your little Pouch holds the magical world of your creative ideas and valuable notes.<br>
Capture inspiration effortlessly in the **Creative Zone** and unveil the hidden **Box of Mysteries** with a touch of enchantment.

Supported by **The White Wings**🪽. Play Store [link](https://play.google.com/store/apps/dev?id=6456450686494659010)

<br>

## Navigate Your Journey of exploring Pouch 🗺️
 1. [Why Choose Pouch?](#why-choose-pouch-)
 2. [Quick Start](#quick-start-)
 3. [Usage](#usage-)
 4. [Demonstration](#demonstration-)
 5. [Utilized Technologies](#utilized-technologies-)
 6. [Main Files](#main-files-)

<br>

## Why Choose Pouch? 💡
 - **Seamless Capture**: Pouch offers an intuitive interface that won't overwhelm you, allowing you to swiftly store your ideas and notes seamlessly in the **Creative Zone**, where inspiration thrives.
 - **Hidden Box of Mysteries**: Discover the hidden Box of Mysteries, where your most valuable ideas are safeguarded away from prying eyes.

<br>

## Quick Start 🚀
**Clone** the Repository and **Run** the app on your device or emulator.

Upon opening Pouch, you'll find yourself in the Creative Zone.

**Creating a New Note:**
 1. To **create** a new note, simply tap on the *floating plus button* at the bottom right corner of the screen.
 2. After entering your note, tap *back*, and your note will automatically be **saved**.

<br>

**Revealing Your Box of Mysteries:** 🪄
 1. Find the **Magical Block** located in the Creative Zone.<br>
It is positioned in the middle of the screen, beneath the search field.
 2. **Knock** (tap) five times on the Magical Block.
 3. Remember to act **swiftly** after the first knock, as you have a seven-second window to complete the sequence.<br>
If you delay beyond seven seconds, the gate will close, and you'll need to start the process again.

<br>

## Usage 📱
Upon opening Pouch, you'll enter the **Creative Zone**, where you can effortlessly manage your notes.

Here's how to make the most of this space:
 - Utilize the **Search** Field at the top to swiftly locate specific notes by title or content.
 - Tap on the **Sorting** Button at the right of the search field to open a pop-up menu for sorting notes alphabetically or by creating/editing time in ascending or descending order.
 - Tap on any note to **view** its details or make **edits**.
 - Easily **remove** a note by swiping it to the left.

<br>

 - **Adding or Editing a Note:**
   - To **create** a new note, tap the *floating plus button* at the bottom right corner. Enter your note's title and content, then tap *back* to **save** it automatically.
   - Tap on a *note card* to **open** it. Make your changes and tap *back* to **save** them, or tap on the **Recycle Bin** at the top right corner to **remove** the note.
   - Keep track of note **Timestamps** with the updating text at the bottom of the screen.
 
<br>

 - **Exploring the Box of Mysteries:**
   - Navigate to the Box of Mysteries by following the steps outlined in the [Quick Start](#quick-start-) section.
   - This hidden space offers the same functionality of the Creative Zone, offering a secure repository for your most valuable notes.

<br>

Rest assured, your notes are securely stored locally on your device using SQLite Database, with distinct storage compartments for notes in both your Creative Zone and your Box of Mysteries.

<br>

## Demonstration 📸
Click the image below to watch the full app demo on YouTube ⬇️

[![Full demo](https://img.youtube.com/vi/20ExnZcRBzE/maxresdefault.jpg)](https://youtu.be/TIbixpGNFwU)

<br>

## Utilized Technologies 🔧
 - **Programming Language:** Java

 - **Storage:** SQLite Database for local storage of notes.

 - **Architecture Pattern:** Model View ViewModel (MVVM).

 - **Development Tools:** Figma, Lottie Animations, Git, and Android Studio.

<br>

## Main Files 📁
 - [MainActivity.java](app/src/main/java/com/thewhitewings/pouch/ui/MainActivity.java)
 - [MainViewModel.java](app/src/main/java/com/thewhitewings/pouch/ui/MainViewModel.java)
 - [activity_main.xml](app/src/main/res/layout/activity_main.xml)<br><br>
 - [NoteFragment.java](app/src/main/java/com/thewhitewings/pouch/ui/NoteFragment.java)
 - [NoteViewModel.java](app/src/main/java/com/thewhitewings/pouch/ui/NoteViewModel.java)
 - [fragment_note.xml](app/src/main/res/layout/fragment_note.xml)<br><br>
 - [OfflineNotesRepository.java](app/src/main/java/com/thewhitewings/pouch/data/OfflineNotesRepository.java)
 - [DatabaseHelper.java](app/src/main/java/com/thewhitewings/pouch/data/DatabaseHelper.java)
 - [PouchApplication.java](app/src/main/java/com/thewhitewings/pouch/PouchApplication.java)<br><br>
 - [DateTimeUtils.java](app/src/main/java/com/thewhitewings/pouch/utils/DateTimeUtils.java)

<br></br>
Carry your World of Creative Ideas and Valuable Notes in your Magical **Pouch** 🪄
