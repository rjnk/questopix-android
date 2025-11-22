// custom
var _score = 0;
var _timerStart = Date.now();

// first task
const start = {
    onStart: () => {
        // setup
        disable("uhotelu");
        disable("kauflandQuestion");

        heading("The Game Begins", "Hooray!");
        text("Welcome to the demonstration demo game for the Open Outdoor Games system. Your task is to visit important checkpoints around Dejvice and complete tasks along the way. Keep in mind that score is being tracked, and I wish you lots of fun.");
        text("As you probably know, the game starts at the Hradcanska tram stop. You should be standing there. Thank you.");
        button("Start the game", () => {
            _timerStart = Date.now();
            showTask("vietnamec");
        });
        takePicture("Bonus photo of excitement at the start.");
    }
}

// 1st task
const vietnamec = {
    onStart: () => {
        heading("Vietnamese Shop");
        text("Your first task is to find the nearest Vietnamese convenience store and buy some exotic drink. There's one really within sight of Hradcanska, so it should be chill.\nOnce you have it, continue on.");
        image("piti.png");
        button("I bought a drink", () => {
            _score += 10;
            showTask("cestaNaZelenou");
        });
        button("I don't have money or it's closed or something", () => {
            _score -= 10;
            showTask("cestaNaZelenou");
        });
    }
}

// 2nd task
const cestaNaZelenou = {
    onStart: () => {
        heading("The Big Move");
        text("There's nothing interesting left at Hradcanska, so you need to move to Zelena. Use bus 131 for that, it normally departs from Hradcanska. Count the stops along the way, I'll ask how many there were at Zelena.");
        image("bus.png");
        button("I'm at Zelena now!", () => {
            _score -= 10;
            showTask("netrpelivostPoCeste");
        });
        text("PS: Your score just changed to " + _score + ".");
    }
}

// 3rd task
const netrpelivostPoCeste = {
    onStart: () => {
        text("It will change automatically when you arrive. I'm deducting 10 points for impatience.");
        takePicture("You can take a photo of your silly face from this information.");
        button("Back", () => {
            showTask("cestaNaZelenou");
        });
        button("Skip to the riddle", () => {
            showTask("kauflandQuestion");
        });
    }
}

const zelena = {
    loc: [
        [50.106943, 14.394933],
        [50.107247, 14.394844],
        [50.107293, 14.395915],
        [50.106949, 14.396097]
    ],
    onStartFirst: () => {
        _score += 5;
    },
    onStart: () => {
        heading("You're at Zelena. Good job!");
        text("Now here's the question. Answer from memory!!");

        question("What was the name of the first stop after Hradcanska?", (answer) => {
            if (answer === "Ronalda Reagana") {
                _score += 20;
                popUp("Good job, that's correct! Keep it up.", "internacional");
            }
            else {
                debugPrint("Wrong.");
                popUp("Wrong, 0 points added.", "internacional");
            }
        });

        text("PS: For your courage on the bus, I'm adding 5 points and you now have " + _score);
    }
}

const internacional = {
    onStart: () => {
        heading("Journey to the Hotel");
        text("Now you need to get to Hotel Internacional. It's tall so it should be visible. Just in case, here's a hint.");
        image("hotel.png");
        text("Of course, when you arrive, a new task will appear automatically...");
        distance(50.1094158, 14.3933839);
        text("PS: Your score is " + _score + ".");
        enable("uhotelu");
    }
}

const uhotelu = {
    loc: [
        [50.1099739, 14.3939067],
        [50.1087081, 14.3934669],
        [50.1093600, 14.3949394]
    ],
    onFirstStart: () => {
        _score += 5;
    },
    onStart: () => {
        heading("Journey to Goodness");
        takePicture("Take a photo with the hotel.");
        text("You get another 5 points for not giving up. Now you need to take a photo with the hotel and then you can head to the next task, which is at Kaufland. Navigate using the map.");
        // '{"backgroundImage":"map2.png","topLeftLat":50.114903,"topLeftLng":14.390008,"bottomRightLat":50.108091,"bottomRightLng":14.397186}'
        simpleMap("map2.png", 50.114903, 14.390008, 50.108091, 14.397186);
        enable("kauflandQuestion");
    }
}

const kauflandQuestion = {
    loc: [
        [50.1120861, 14.3922456],
        [50.1109372, 14.3926747],
        [50.1117903, 14.3939944]
    ],
    onStart: () => {
        heading("Super! Welcome to Kaufland.");
        text("Now just answer the question and then you've won and can hurry to get food!");
        multichoice("How many floors does Kaufland have", (answerNumber) => {
            if(answerNumber === 1) {
                debugPrint("ok");
                _score += 15;
                popUp("Correct! + 15 pts.", "finish");
            } else {
                debugPrint("wrong");
                popUp("Wrong! + 0 pts.", "finish");
            }
        }, "One", "Two", "Three");
    }
}

const finish = {
    onStart: () => {
        // setup
        disable("uhotelu");
        disable("kauflandQuestion");

        heading("Great work!", "center");
        image("trophy.png");
        text("Congratulations on completing the game. It was tough, but you handled it perfectly.");

        const elapsedMs = Date.now() - _timerStart;
        const elapsedMinutes = Math.floor(elapsedMs / (60 * 1000));

        board("Results", "Score", _score, "Time", elapsedMinutes + " min.")

        shareButton();
        showAllImages("Photos from the game:");
        finishGameButton("To menu");
    }
}
