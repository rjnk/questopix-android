# Open Outdoor Games
This is a mobile app that lets you play GPS based outdoor games (like a treasure hunt or a scavenger hunt).
Outdoor game consist of a set of tasks such as answering a question, taking a photo or visiting a location.

## App screens
**Onboarding slides**
- shows only on the first app start
- explains the app purpose and basic usage

**Home screen**:  
This screen is accessible from the bottom menu as the first tab.
- the main screen of the app, the buttons forward the user to the library

**Library screen**:  
This screen is accessible from the bottom menu as a second tab.
- shows the list of games that are available to play
- shows a list of games that have been played
- lets you delete downloaded games
- lets you import a new game from a zip file (with a confirmation if the game is already present)

**Game info screen**:  
This screen opens when the users clicks on a game in the library or after a game is imported.
- shows the game info from the info.json file (name, description, start & finish location, cover photo, game stats - such as expected duration, distance, number of tasks)
- checks if you are close enough to the starting location
- lets you start the game

**Game task screen**:  
This is the screen where the actual game is played. The ui is generated from the game javascript code. It can contain text, images, buttons, questions, photo tasks, location tasks, maps, score board etc.
The last task in a game is generally showing the final score and a button to go to the library. But it's still a task so any content can be shown there.
- some tasks shows only when you are at a given location

**Settings screen**:  
- lets you pause or quit the current game
- lets you change the app language via Android app language (currently English and Czech are supported)
- shows info about the project

The actual game is written in JavaScript and you simply import the game as part of a game zip file into the app. The zip file contains also the game info (info.json) and any images used in the game.
The app then runs the javascript code and lets you interact with the in game events.

## Game zip file
A game zip file contains all information about the game. It gets imported to the library and then shows as an item in the listing.
It contains:
- info.json - the game info file
- game.js - the actual game code
- images - images used in the game (such as cover-photo.jpg, trophy.png, etc.)

The specification of the game format is avaliable in the /docs/GameFormatSpecification.md file.
