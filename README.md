# Pouch ⭕

Let your little **Pouch** hold the magical world of your creative ideas and valuable notes.<br>
Capture inspiration effortlessly in the **Creative Zone** and unveil the hidden **Box of Mysteries** with a touch of enchantment.

It’s available in a Modern **Kotlin with Jetpack Compose** [version](https://github.com/MoaazAtik/Pouch) and a **Java with Android Views** [version](https://github.com/MoaazAtik/Pouch/tree/java-views-version).<br>
Supported by **The White Wings**🪽. Play Store [link](https://play.google.com/store/apps/dev?id=6456450686494659010)

<br>

<div align="center">
  <img src="https://github.com/user-attachments/assets/4d2c0536-bab9-47a9-bfc6-6ce96dc9ef85" alt="Creative Zone" width="250"/>
  <img src="https://github.com/user-attachments/assets/8f2c0fe4-309a-4882-bfc2-2ec42fe824f9" alt="Box of Mysteries" width="250"/>
</div>

<div align="center">
  Screenshots
</div>

<br>

## Navigate Your Journey of exploring Pouch 🗺️
 1. [Why Choose Pouch?](#why-choose-pouch-)
 2. [Quick Start](#quick-start-)
 3. [Usage](#usage-)
 4. [Demonstration](#demonstration-)
 5. [Utilized Techniques and Technologies](#utilized-techniques-and-technologies-)
 6. [Upcoming Enhancements](#upcoming-enhancements-)
 7. [Core Files](#core-files-)

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
 - Easily remove a note by swiping it to the left.
 - Restore a recently removed note by tapping on the **Undo** button in the **Snackbar** that is shown temporarily after removing a note.

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
Click the image below to watch the full app demo of the Java version on YouTube ⬇️

[![Full demo](https://img.youtube.com/vi/20ExnZcRBzE/maxresdefault.jpg)](https://youtu.be/TIbixpGNFwU)

<br>

## Utilized Techniques and Technologies 🔧
This app was originally developed in Java using Android Views and older libraries. Gradually, it was fully migrated to Kotlin with Jetpack Compose, adopting modern libraries and techniques along the way.
The focus was on maintainability, scalability, and aligning with Modern App Development Principles.

- **Programming Language:** Kotlin (migrated from Java)

- **UI**: Jetpack Compose, Android Views via Interoperability API, Navigation Component

- **Storage:** Room library for local storage of notes

- **Architecture Pattern:** Model View ViewModel (MVVM) with Unidirectional Data Flow (UDF)

- **Unit & UI Testing**: JUnit, Mockito

- **Development Tools:** Figma, Lottie Animations, Git, and Android Studio

- **Modern Practices:**
  - Layered architecture and abstraction
  - Dependency Injection (DI)
  - Reactive Programming with Coroutines and Flows
  - Well-documented codebase for ease of understanding and maintenance

<br>

## Upcoming Enhancements 🚀

- **Unit & UI Testing**: Strengthening code coverage and reliability. (✅Done)

- **Architecture & Code**: Improved structure and scalability. (✅Done)

- **Adaptive Layout**: Support for various screen sizes.

- **Recycle Bin Feature**: Temporarily storing deleted notes for 1 month.

- **UI/UX**: Smoother interface and user experience.

<br>

## Core Files 📁
- [NotesScreen](app/src/main/java/com/thewhitewings/pouch/feature_note/presentation/notes/NotesScreen.kt)
- [NotesViewModel](app/src/main/java/com/thewhitewings/pouch/feature_note/presentation/notes/NotesViewModel.kt)
- [BomRevealingButton](app/src/main/java/com/thewhitewings/pouch/feature_note/presentation/util/BomRevealingButton.kt)
- [AddEditNoteScreen](app/src/main/java/com/thewhitewings/pouch/feature_note/presentation/add_edit_note/AddEditNoteScreen.kt)
- [AddEditNoteViewModel](app/src/main/java/com/thewhitewings/pouch/feature_note/presentation/add_edit_note/AddEditNoteViewModel.kt)
- [PouchNavGraph](app/src/main/java/com/thewhitewings/pouch/feature_note/presentation/navigation/PouchNavGraph.kt)<br><br>
- [OfflineNotesRepositoryImpl](app/src/main/java/com/thewhitewings/pouch/feature_note/data/repository/OfflineNotesRepositoryImpl.kt)
- [NoteDao](app/src/main/java/com/thewhitewings/pouch/feature_note/data/data_source/NoteDao.kt)
- [PouchPreferencesImpl](app/src/main/java/com/thewhitewings/pouch/feature_note/data/repository/PouchPreferencesImpl.kt)
- [DatabaseMigrations](app/src/main/java/com/thewhitewings/pouch/feature_note/data/data_source/DatabaseMigrations.kt)<br><br>
- [DateTimeUtils](app/src/main/java/com/thewhitewings/pouch/feature_note/util/DateTimeUtils.kt)
- [PouchApplication](app/src/main/java/com/thewhitewings/pouch/PouchApplication.kt)<br><br>
- [NotesViewModelTest](app/src/test/java/com/thewhitewings/pouch/presentation/NotesViewModelTest.kt)
- [OfflineNotesRepositoryTest](app/src/test/java/com/thewhitewings/pouch/domain/OfflineNotesRepositoryTest.kt)
- [DateTimeUtilsTest](app/src/test/java/com/thewhitewings/pouch/util/DateTimeUtilsTest.kt)<br><br>
- [NotesScreenTest](app/src/androidTest/java/com/thewhitewings/pouch/presentation/NotesScreenTest.kt)
- [NoteDaoTest](app/src/androidTest/java/com/thewhitewings/pouch/data/NoteDaoTest.kt)

<br>

<br></br>
Carry your World of Creative Ideas and Valuable Notes in your Magical **Pouch** 🪄
