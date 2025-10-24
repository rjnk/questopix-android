var _score = 0;
var _timerStart = null;
var _timerEnd = null;

const start = {
    onStartFirst: () => {
        _timerStart = Date.now();
    },
    onStart: () => {
        heading("Vítej v Malé Černošické hře!", "center");
        text("Hra obsahuje různé úkoly, za jejichž splnění získáváš body.\n");
        text("Kromě důvtipu bude někdy potřeba i odvaha, šikovnost a spolupráce.");

        takePicture("Úvodní selfie s týmem");

        button("Začít hru", () => {
            showTask("task1");
        });
    }
}

const task1 = {
    onStart: () => {
        heading("Úkol 1: Černošická trivia");
        multichoice("Jaký je původ názvu Černošice?", (choice) => {
            if (choice === "0") {
                popUp("Správně! Přidávám 10 bodů.", "task2");
                _score += 10;
            } else {
                popUp("Špatně! Správná odpověď je: Název města vychází z příslušnosti vsi k rodu Černochových.", "task2");
            }
        }, "Název města vychází z příslušnosti vsi k rodu Černochových", "Černošice byly pojmenovány podle černých ptáků, kteří zde hnízdili", "Název pochází z tmavé barvy půdy v okolí řeky", "Jméno města je odvozeno od staročeského slova pro stín");
    }
}

const task2 = {
    onStart: () => {
        heading("Další otázka:");
        question("Kolik obyvatel mají Černošice? Čím blíže budeš, tím více bodů dostaneš.", (answer) => {
            if(isNaN(answer)) {
                popUp("To není číslo! Zkus to prosím znovu.");
                return;
            }
            let population = 7712;
            let diff = Math.abs(population - parseInt(answer));
            if(diff < 500) {
                popUp("Výborně! Počet je 7712 a tak přidávám 25 bodů.", "task3");
                _score += 25;
            } else if(diff < 1000) {
                popUp("Dobře! Počet je 7712 a tak přidávám 20 bodů.", "task3");
                _score += 20;
            } else if(diff < 2000) {
                popUp("Ujde to! Počet je 7712 a tak přidávám 10 bodů.", "task3");
                _score += 10;
            } else if (diff < 4000) {
                popUp("Nic moc. Počet je 7712 a přidávám 5 bodů za snahu.", "task3");
                _score += 5;
            } else {
                popUp("Bohužel špatně. Počet je 7712 a ty získáváš 0 bodů.", "task3");
            }
        });
        text("PS: Tvoje skóre je zatím: " + _score + " bodů.");
    }
}

const task3 = {
    onStart: () => {
        heading("Poslední otázka z trivie:");
        text("Opět můžeš získat body podle přesnosti své odpovědi. Maximum je 25 bodů.");
        question("Kdy byla první písemná zmínka o (Horních) Černošicích? (uveď rok)", (answer) => {
            if(isNaN(answer)) {
                popUp("To není číslo! Zkus to prosím znovu.");
                return;
            }
            const explanation = "Horní Černošice jsou poprvé zmíněny v listině Kladrubského kláštera z roku 1115.";
            let year = 1115;
            let diff = Math.abs(year - parseInt(answer));
            if(diff < 75) {
                popUp("Výborně! " + explanation + " Přidávám 25 bodů.", "task4");
                _score += 25;
            } else if (diff < 150) {
                popUp("Dobře! " + explanation + " Získáváš 15 bodů.", "task4");
                _score += 15;
            } else if (diff < 250) {
                popUp(explanation + " Přidávám 5 bodů za snahu.", "task4");
                _score += 5;
            } else {
                popUp(explanation + " Jsi moc daleko a nezískáváš nic.", "task4");
            }
        });
        text("PS: Tvoje skóre je zatím: " + _score + " bodů.");
    }
}

const task4 = {
    onStart: () => {
        heading("Jde se do akce!");
        text("Kvízů už je dost a teď je potřeba se začít hýbat. Pokračuj po Karlštejnské ulici dál směrem od řeky.\n");
        text("Jakmile dorazíš ke křížení s ulicí V Dolících zobrazí se ti další instrukce.");
        distance(49.9576319, 14.3160736);
        text("PS: Tvoje skóre je zatím: " + _score + " bodů.");
    }
}

const task5 = {
    loc: [
        [49.9577078, 14.3155306],
        [49.9572936, 14.3158711],
        [49.9574406, 14.3166303],
        [49.9578631, 14.3163056]
    ],
    onStart: () => {
        heading("Čas na spolupráci!");
        text("Nyní je potřeba se rozhodnout, jestli pokračovat po hlavní ulici nebo jít \"zkratkou\" přes les. Domluvte se s ostatními hráči, ideálně se nerozdělujte.");
        simpleMap("map-rozhodnuti.png", 49.9595664, 14.3081878, 49.9539097, 14.3178492);
        button("Pokračovat po hlavní ulici", () => {
            _score -= 10;
            popUp("Za zbabělost -10 bodů.", "task6ulice");
        });
        button("Jít delší cestou přes les", () => {
            _score += 10;
            popUp("Za odvahu +10 bodů.", "task6les");
        });
        text("PS: Tvoje skóre je zatím: " + _score + " bodů.");
    }
}

const task6ulice = {
    onStart: () => {
        text("Jsem zklamaný, že jsi si nevybral dobrodružství. Nevadí. Pokračuj rovně po ulici než se zobrazí další instrukce.");
        distance(49.9563911, 14.3100736);
        text("PS: Tvoje skóre je zatím: " + _score + " bodů.");
    }
}

const task6les = {
    onStart: () => {
        text("Výborně! Dobrodružství čeká. Pokračuj podle mapy než dorazíš k dalšímu úkolu.");
        simpleMap("map-les.png", 49.9595664, 14.3081878, 49.9539097, 14.3178492);
        text("PS: Tvoje skóre je zatím: " + _score + " bodů.");
    }
}

const task7most = {
    loc: [
        [49.9562628, 14.3096444],
        [49.9562031, 14.3100586],
        [49.9564317, 14.3103403],
        [49.9565406, 14.3099447]
    ],
    onStart: () => {
        heading("Úkol na/pod mostem");

        text("Dorazil jsi k mostu přes potok Švarcava. Máš za úkol pod mostem podlézt.");
        takePicture("Fotka mostem jako důkaz splnění úkolu");
        text("Úkol můžeš buď splnit nebo přeskočit - podle toho klikni na odpovídající tlačítko 🤗.");

        button("Splněno, podlezl jsem pod mostem", () => {
            _score += 15;
            popUp("Výborně! Získáváš 15 bodů za odvahu a nasazení.", "task8cestaNaHrbitov");
        });
        button("Přeskočit úkol, nejde to.", () => {
            _score -= 15;
            popUp("Úkol přeskočen, -15 bodů.", "task8cestaNaHrbitov");
        });
    }
}

const task8cestaNaHrbitov = {
    onStart: () => {
        heading("Cesta na hřbitov");
        text("Nyní pokračuj podle ukazatele vzdálenosti na hřbitov na Vráži.\neká tě tam další dobrodružství 💀.");
        distance(49.9538839, 14.3028222);
        text("PS: Tvoje skóre je zatím: " + _score + " bodů.");
    }
}

const task9hrbitov = {
    loc: [
        [49.9538406, 14.3022697],
        [49.9534436, 14.3032514],
        [49.9540994, 14.3032378]
    ],
    onStart: () => {
        heading("Vítej na hřbitově!");
        text("Tvým úkolem je najít na hřbitově nejstarší náhrobek a vyfotit ho.");
        takePicture("Vyfoť nejstarší náhrobek na hřbitově.");
        button("Mám to!", () => {
            _score += 10;
            popUp("Výborně! Získáváš 10 bodů za splnění úkolu.", "task10cestaKeStolu");
        });
    }
}

const task10cestaKeStolu = {
    onStart: () => {
        heading("Blížíme se ke konci");
        text("Nyní pokračuj znovu podle ukazatele vzdálenosti, zpátky do lesa. Čeká tam na tebe poslední úkol.");
        distance(49.9535658, 14.2954517);
        text("PS: Tvoje skóre je zatím: " + _score + " bodů.");
    }
}

const task11stul = {
    loc: [
        [49.9538078, 14.2952314],
        [49.9536525, 14.2957733],
        [49.9534411, 14.2952958]
    ],
    onStart: () => {
        heading("Poslední úkol");
        text("Na rozcestí je kamenný stůl. Tvým úkolem je na něj vylézt BEZ použití rukou a vyfotit vítěznou selfie.");
        takePicture("Vítězná selfie");

        button("Mám to!", () => {
            _score += 10;
            popUp("Výborně! Získáváš 10 bodů za splnění úkolu.", "end");
        });

        button("Použiji ruce...", () => {
            popUp("Škoda, ale nevadí, nejsou za to záporné body.", "end");
        });

        text("PS: Tvoje skóre je zatím: " + _score + " bodů.");
    }
}

const end = {
    onStartFirst: () => {
        _timerEnd = Date.now();
    },
    onStart: () => {
        let duration = Math.floor((_timerEnd - _timerStart) / 60000); // in minutes

        heading("Gratuluji k dokončení hry!", "center");
        image("trophy.png");
        board("Výsledky",
              "Skóre", _score + " bodů",
              "Trvání", duration + " minut");

        showAllImages("Fotky ze hry");
        shareButton();
        finishGameButton("Konec");
    }
}
