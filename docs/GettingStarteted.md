# Questopix: Getting started Guide

## What is Questopix?
Questopix is a platform for creating outdoor GPS games. Players use the GPS on their phones to navigate to various locations where they complete tasks or answer questions.
You can create your own game by following this step-by-step guide. As for now, the creation of a game is little technical and you need to edit the game text files manually.

---

## What do you need?

1. **Text editor**
- Feel free to use any simple text editor:
   - Windows: Notepad
   - Mac: TextEdit
   - Or VS Code, Sublime Text, Atom, etc.
2. **Basic knowledge of JSON and JavaScript** - Don't worry, this guide will walk you through the necessary parts.
3. **(Optional) GPS coordinates of your locations** - The game can be created without specific locations. If you need to have it location based, you will need to provide GPS coordinates for each location.
4. **(Optional) Images** - Photos for the game's cover image and content.

---

## Creating the game

### Step 1: Download the template
The best starting point for creating your own game is to download the game template from the [Questopix GitHub repository](https://gitlab.fel.cvut.cz/rejneluk/questopix-releases/-/raw/master/template-game.zip?ref_type=heads). After downloading, unzip the file to a folder on your computer. It will contain the `info.json`, `game.js`, and some images.

## Step 2: Edit game informations in the info.json
Read the [GameFormatSpecification.md, Info.json section](https://gitlab.fel.cvut.cz/rejneluk/oog/-/blob/master/docs/GameFormatSpecification.md#infojson-game-metadata) to understand the file structure. After that, open the `info.json` file in your text editor and modify the fields inside to your liking.

### Step 3: Create a game tasks in the game.js
Creating your own game tasks is more tricky that editing the info.json file. First read the [GameFormatSpecification.md, Game.js section](https://gitlab.fel.cvut.cz/rejneluk/oog/-/blob/master/docs/GameFormatSpecification.md#gamejs-game-logic) to understand how to use the individual actions. After you are ready, open the `game.js` file in your text editor and edit it to your liking. It contains some basic structure of a game with few example tasks that you can modify or delete.

It's practical to start with a game that is **not tied** to a specific locations, so you can test it indoors. You can later add location-based tasks once you are familiar with the platform.

If you are unsure, you can look at the example games in the [Questopix GitHub repository](https://gitlab.fel.cvut.cz/rejneluk/oog/-/tree/master/app/src/main/assets/bundled_games) to see how they are implemented or ask the Generative AI for help (while providing the game specification file as a reference).

### Step 4: Validate your game files using Generative AI
Currently, the Questopix platform doesn't have a built-in game validator. However, you can use Generative AI tools to help validate your game files. Simply provide the AI with the `GameFormatSpecification.md` file and your modified `info.json` and `game.js` files, and ask it to check for any errors or inconsistencies.

### Step 5: Export the game as a ZIP file
Once you have finished editing the `info.json` and `game.js` files, you need to package them into a ZIP file. Make sure to include all necessary images used in the game. The ZIP file should contain:
- `info.json`
- `game.js`
- Any images used in the game

### Step 6: Installing the Questopix app
The Questopix app is avaliable for Android 10+ only, no iOS support. The app needs to be installed from apk file as it's not yet avaliable in Google Play Store.

1. Download the latest Questopix apk file from the [releases section of the GitHub repository](https://gitlab.fel.cvut.cz/rejneluk/oog/-/releases).
2. Install the apk file on your Android device, following the standard installation procedure for apk files (guide: [https://www.browserstack.com/guide/download-and-install-apk-on-android](https://www.browserstack.com/guide/download-and-install-apk-on-android))

### Step 7: Import and test the game in Questopix app
Now that you have your game packaged as a ZIP file, you can import it into the Questopix app. Open the app, navigate to the Library screen, and use the import function to add your game. Once imported, you can start playing your game to test it out and ensure everything works as expected.
