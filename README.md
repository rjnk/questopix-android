# Open Outdoor Games
This is a mobile app that lets you play GPS based outdoor games (like a treasure hunt or a scavenger hunt).
Outdoor game consist of a set of tasks such as answering a question, taking a photo or visiting a location.

## App screens
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

### Example of info.json
```json
{
  "id": "com.rejnek.dejvice.alpha",
  "name": "Dejvická hra",
  "description": "Hra vás provede po zajímavých místech v Dejvicích a okolí.",
  "coverPhoto": "cover.jpeg",
  "startLocation": {
    "text": "zastávka tramvaje Hradčanská, Praha 6",
    "coordinates": {
      "lat": 50.0971869,
      "lng": 14.4038831,
      "radius": 70.0
    }
  },
  "finishLocation": {
    "text": "Kaufland Dejvice, Praha 6",
    "coordinates": {
      "lat": 50.1118728,
      "lng": 14.3926511,
      "radius": 70.0
    }
  },
  "attributes": {
    "Očekávaná délka": "1 hodina",
    "Mentální náročnost": "2/5",
    "Fyzická náročnost": "2/5",
    "Trapnost": "1/5"
  }
}
```

### Example of javascript game code (game.js)
```javascript
// custom
var _score = 0;
var _timerStart = Date.now();

// startovní úkol / first task
const start = {
    onStart: () => {
        // setup - disable location tasks that are not unlocked yet
        // note: there is no point to disable non-location tasks - the command has no effect on those
        disable("uhotelu");
        disable("kauflandQuestion");
    
        heading("Hra začíná 🎉🎉", "Hurá!");
        text("Vítejte v demonstrační demo hře pro systém Open Outdoor Games. Vaším úkolem je projít významná stanoviště v okolí Dejvic a plnit po cestě úkoly. Myslete na to, že se počítá skóre a přeji plno zábavy.");
        text("Jak asi víte, tak se začíná na tramvajové zastávce Hradčanská. Měli byste tam stát. Děkuji.");
        button("Začít hru", () => {
            _timerStart = Date.now();
            showTask("vietnamec");
        });
        takePicture("Bonusová fotka nadšení na začátku.");
    }
}

// 1. úkol
const vietnamec = {
    onStart: () => {
        heading("Vietnamec");
        text("Váš první úkol je najít nejbližší Vietnamskou večerku a koupit si nějaké exotické pití. Jedna je fakt na dohled od Hrančanský, takže by to mělo být chill.\nAž to budete mít, tak pokračujte dál");
        image("piti.png");
        button("Koupil jsem pití", () => {
            _score += 10;
            showTask("cestaNaZelenou");
        });
        button("Nemám prachy nebo je zavřeno nebo něco", () => {
            _score -= 10;
            showTask("cestaNaZelenou");
        });
    }
}

// 2. úkol
const cestaNaZelenou = {
    onStart: () => {
        heading("Velký přesun");
        text("U Hradčanské už nic zajímavého není, takže je potřeba se přesunout na Zelenou. K tomu použij bus 131, odjíždí normálně z Hrančasnký. Počítej po cestě zastávky, na zelený se zeptám kolik jich bylo 😉😉.");
        image("bus.png");
        button("Už jsem na Zelený!", () => {
            _score -= 10;
            showTask("netrpelivostPoCeste");
        });
        text("PS: Tvoje skóre se právě změnilo na " + _score + ".");
    }
}

// 3. úkol
const netrpelivostPoCeste = {
    onStart: () => {
        text("Přece se to změní automaticky, když dojedeš. Za netrpělivost odečítám 10 bodů.");
        takePicture("Můžeš vyfotit svůj hloupý výraz z týhle informace 🤗.");
        button("Zpátky", () => {
            showTask("cestaNaZelenou");
        });
        button("Přeskočit na hádanku", () => {
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
    // Note: onFirstStart is called only once, when the user arrives to the location for the first time
    // onStart is called every time the user arrives to the location (including the first time) - so the score would be increased multiple times
    onStartFirst: () => {
        _score += 5;
    },
    onStart: () => {
        heading("Jsi na Zelené. GJ ☺️.");
        text("Teď je tady ta otázka. Odpovídej z hlavy!!");
        
        question("Jak se jmenovala první zastávka po Hradčasnké 😜?", (answer) => {
            if (answer === "Ronalda Reagana") {
                _score += 20;
                popUp("Dobrá práce, to je správně! Jen tak dál.", "internacional");
            }
            else {
                debugPrint("Špatně.");
                popUp("Špatně, 0 bodů přidáno 😭.", "internacional");
            }
        });
        
        text("PS: Za tvoji odvahu v buse přidávám 5 bodů a tak máš celkem " + _score);
    }
}

const internacional = {
    onStart: () => {
        heading("Cesta k hotelu");
        text("Teď je potřeba dojít k hotelu Internacional. Měl bý být vidět, protože je vysoký. Pro jistotu dávám nápovědu.");
        image("hotel.png");
        text("Jinak samozřejmě až tam dojdeš, tak se objeví nový úkol...");
        distance(50.1094158, 14.3933839);
        text("PS: můžeš se vzdát jestli na to nemáš");
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
        heading("Cesta za dobrotou 😋");
        takePicture("Vyfoť se s hotelem.");
        text("Dostáváš dalších 5 bodů za nevzdání. Teď je potřeba se vyfotit s hotelem a pak se můžeš vydat za dalším úkolem, který je u kauflandu. Naviguj se podle mapy.");
        // the simple map works by showing an image with given coordinates - '{"backgroundImage":"map2.png","topLeftLat":50.114903,"topLeftLng":14.390008,"bottomRightLat":50.108091,"bottomRightLng":14.397186}'
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
        heading("Super! Vítej u Kauflandu.");
        text("Teď stačí odpovědět na otázku a pak jsi vyhrál a můžeš hurá pro jídlo!");
        multichoice("Kolik pater má Kauland", (answerNumber) => {
            if(answerNumber === 1) {
                debugPrint("ok");
                _score += 15;
                popUp("Dobře! + 15b 🤖.", "finish");
            } else {
                debugPrint("wrong");
                popUp("Špatně! + 0b 😭.", "finish");
            }
        }, "Jedno", "Dvě", "Tři");
    }
}

const finish = {
    onStart: () => {
        // disable all location tasks so that these don't show up any more
        disable("uhotelu");
        disable("kauflandQuestion");
    
        heading("Skvělá práce!", "center");
        image("trophy.png");
        text("Blahopřeji k dokončení hry. Byla to fuška, ale zvládnul jsi to fakt perfektně.");
        
        const elapsedMs = Date.now() - _timerStart;
        const elapsedMinutes = Math.floor(elapsedMs / (60 * 1000));
        
        board("Výsledky", "Scóre", _score, "Čas", elapsedMinutes + "min.")
            
        shareButton();
        showAllImages("Fotky ze hry:");
        finishGameButton("Do menu");
    }
}
```
