// Custom variables
var _score = 0;

// Game start - the first task that is opened when the game begins
const start = {
    onStart: () => {
        heading("Add heading");
        text("Place your introductory text here.");
        image("trophy.png");
        button("Start game", () => {
            // this function runs after the button is pressed
            showTask("task1");
        });
    }
};

// First task
const task1 = {
    onStart: () => {
        heading("Add task heading");
        text("Add instructions for the task.");

        // Question with multiple choice answers
        multichoice("Question?", (answerNumber) => {
            if(answerNumber === "0") { // First answer
                _score += 20;
                popUp("Correct", "task2");
            } else if(answerNumber === "1") { // Second answer
                popUp("Wrong", "task2");
            } else { // Third answer
                popUp("Also wrong", "task2");
            }
        }, "First answer", "Second answer", "Third answer");

        // Example how to show a hint
        button("I need a hint (-5 points)", () => {
            _score -= 5;
            popUp("Hint text!");
        });
    }
};

const task2 = {
    onStart: () => {
        // more tasks to be added here
    }
};

// Last task
const end = {
    onStart: () => {
        heading("Mission completed", "center");
        image("trophy.png");
        text("Great job! You have successfully completed the game.");

        board("Final results", "Score", _score);

        shareButton();
        showAllImages("Share the photos from game!");
        finishGameButton("Go back to menu");
    }
};
