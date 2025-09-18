// OOG Debug Game - All UI functions in one task
var _score = 100;
var _timerStart = Date.now();
var _playerName = "Test Player";
var _photoCount = 3;

const start = {
    loc: [
        [51.5118817, 17.7840464],
        [47.8202303, 17.8609508],
        [49.8660272, 11.0329478]
    ],
    onStart: () => {
        // UI Elements
        heading("Welcome to the Game!", "center");
        heading("Task title");

        text("This is an example of game instructions or story text.");
        text("You can include newline characters \nfor formatting or put multiple text() calls below each other.");

        image("trophy.png");

        // User Interactions
        button("Continue", () => {});
        button("Skip", () => {
            popUp("Task skipped");
        });

        question("What number is written on the building?", (answer) => {
            if( answer === "42") {
                popUp("Correct answer!");
            } else {
                popUp("Wrong answer, try again.");
            }
        });

        multichoice("Pick your difficulty:", (choice) => {
            if (choice === 0) {
                _difficulty = "Easy";
            } else if (choice === 1) {
                _difficulty = "Medium";
            } else {
                _difficulty = "Hard";
            }
            popUp("Difficulty set to " + _difficulty);
        }, "Easy", "Medium", "Hard");

        takePicture("Take a photo of the monument!");

        // Navigation UI
        distance(50.1094, 14.3933);

        simpleMap("map.png", 51.6620556, 13.2027478, 48.2060719, 16.3777964);

        // Utility Functions
        board("Final Results",
              "Score", "75",
              "Time", "10min",
              "Difficulty", "Hard");

        showAllImages("Memories from the Game");

        shareButton();

        finishGameButton("Return to Library");
    }
}