// Globální proměnné hry
var _score = 0;
var _timerStart = Date.now();
var _timeElapsed = 0;
var _disabled = ["studna"];

// Úvodní úkol
const start = {
    onStartFirst: () => {
        _timerStart = Date.now();
    },

    onStart: () => {
        heading("👋 Vítejte v Zápudově!", "center");
        text("Čeká vás dobrodružná cesta v okolí Zápudova. Doufám, že jste se pořádně oblékli 😇.");
        text("Sbírejte body plněním úkolů a užijte si den v Českém ráji! 🌲");

        button("🎮 Začít hru", () => showTask("cestaKPristresku"));
    }
}

const cestaKPristresku = {
    onStart: () => {
      heading("🏚️ Cesta k poustevně", "center");
      text("První úkol je na kopci nad chatou. Musíte najít poustevnu, která tam je už nějakou dobu postavená. Je u skály a fotky mohou pomoci s hledáním. 🔍");
      image("pristresek.jpg");
      image("pristresek2.jpg");
      text("Až k ní dojdete, ukáže se vám co dál. ✨");
      distance(50.5099208, 15.0375650);
      text("Abyste měli jistotu, že jdete na správný kopec nad správnou chatou, tady je ukazatel zbývající vzdálenosti ☺️.");
    }
}

// Úkol 1: Oprava přístřešku
const pristreske = {
    loc: [
        [50.5103183, 15.0376722],
        [50.5095506, 15.0382731],
        [50.5094517, 15.0374792],
        [50.5101000, 15.0368514]
    ],

    onStart: () => {
        heading("🔨 Poustevna");
        text("Teď byste měli být u poustevny. Máte za úkol ji opravit. Až to bude, tak se ve skupině dohodněte, jak moc kdo pomohl a podle toho pokračujte dál.");
        text("Přiložte ruku k dílu! 🛠️");

        button("💪 Maximální nasazení", () => {
            _score += 20;
            popUp("Skvělá práce! ⭐ +20 bodů", "cestaKValecovu");
            disable("pristreske");
        });

        button("👍 Pomáhal jsem", () => {
            _score += 10;
            popUp("Dobrá práce! ✅ +10 bodů", "cestaKValecovu");
            disable("pristreske");
        });

        button("🤏 Malá pomoc", () => {
            _score += 5;
            popUp("Alespoň něco! +5 bodů", "cestaKValecovu");
            disable("pristreske");
        });

        button("😬 Nic jsem nedělal", () => {
            _score -= 5;
            popUp("Škoda... 😔 -5 bodů", "cestaKValecovu");
            disable("pristreske");
        });
    }
}

// Cesta k Valečovu
const cestaKValecovu = {
    onStart: () => {
        heading("🗺️ Cesta k Valečovu", "center");
        text("Hurá, přístřešek je opraven! 🎉 Teď pokračujte k Valečovským světničkám.");
        simpleMap("svetnicky-mapa.png", 50.5131461, 15.0264339, 50.5056544, 15.0400164);
        text("PS: tvoje aktuální skóre: " + _score + " bodů 🏆");
    }
}

// Úkol 2: Valečovské světničky - kvíz
const valecov = {
    loc: [
        [50.5097553, 15.0268844],
        [50.5089775, 15.0274853],
        [50.5095439, 15.0291161]
    ],

    onStart: () => {
        heading("🏛️ Valečovské světničky");
        text("Jste u historických skalních bytů! Odpovězte na otázky: 🤔");

        multichoice("Jak se nazývali obyvatelé těchto skalních bytů?", (choice) => {
            if (choice === 0) {
                _score += 10;
                popUp("Správně! ✅ +10 bodů", "valecovOtazka2");
            } else {
                popUp("Špatně! ❌ Správná odpověď: Skaláci", "valecovOtazka2");
            }
            disable("valecov");
        }, "Skaláci", "Horníci", "Poustevníci", "Valečáci");
    }
}

// Druhá otázka kvízu
const valecovOtazka2 = {
    onStart: () => {
        heading("📚 Valečovské světničky - kvíz pokračuje");

        multichoice("Proč obyvatelé v roce 1892 odešli?", (choice) => {
            if (choice === 1) {
                _score += 10;
                popUp("Správně! ✅ +10 bodů", "valecovOtazka3");
            } else {
                popUp("Špatně! ❌ Správná odpověď: Byli vystěhování z hygienických důvodů kvůli riziku cholery", "valecovOtazka3");
            }
        }, "Byli násilně vystěhování kvůli válce", "Byli vystěhování z hygienických důvodů kvůli riziku cholery", "Všichni odcházeli postupně, v roce 1892 odešla poslední obyvatelka", "Odešli kvůli obecním bytům zdarma v Bosni");
    }
}

// Třetí otázka kvízu
const valecovOtazka3 = {
    onStart: () => {
        heading("🔢 Valečovské světničky - kvíz pokračuje");

        multichoice("Kolik skalních bytů bylo vytesáno do skal?", (choice) => {
            if (choice === 1) {
                _score += 10;
                popUp("Správně! ✅ +10 bodů", "valecovOtazka4");
            } else {
                popUp("Špatně! ❌ Správná odpověď: 28 bytů", "valecovOtazka4");
            }
        }, "15 bytů", "28 bytů", "42 bytů", "50 bytů");
    }
}

// Čtvrtá otázka kvízu
const valecovOtazka4 = {
    onStart: () => {
        heading("🎯 Valečovské světničky - poslední otázka");

        multichoice("Kolik lidí zde žilo koncem 19. století?", (choice) => {
            if (choice === 1) {
                _score += 10;
                popUp("Správně! ✅ +10 bodů. Teď pokračujte ke Skalce! 🌲", "cestaKeSkalce");
            } else {
                popUp("Špatně! ❌ Správná odpověď: Asi 30 osob ze 7 rodin", "cestaKeSkalce");
            }
        }, "Asi 10 osob ze 2 rodin", "Asi 30 osob ze 7 rodin", "Asi 50 osob z 12 rodin", "Asi 100 osob z 20 rodin");
    }
}

// Navigace ke Skalce
const cestaKeSkalce = {
    onStart: () => {
        heading("🌲 Cesta ke Skalce", "center");
        text("Skvěle, zvládli jste kvíz o Valečovských světničkách! 🎉");
        text("Teď se vydejte k rozcestí Skalka. 🗺️");
        simpleMap("skalka-mapa.png", 50.5174775, 15.0269358, 50.5086014, 15.0368492);
        text("PS: tvoje aktuální skóre: " + _score + " bodů 🏆");
    }
}

// Úkol 3: Vylez na strom (Skalka)
const skalka = {
    loc: [
        [50.5162136, 15.0333944],
        [50.5160158, 15.0348806],
        [50.5167253, 15.0344031]
    ],

    onStart: () => {
        heading("🌲 Skalka - výzva lezení na strom!");
        text("Vylez na strom a vyfoť se co nejvýš! 🧗");
        text("Body získáš podle toho, jak vysoko se dostaneš. 📏");
        takePicture("📸 Vyfoť se na stromě co nejvýš");

        multichoice("Jak vysoko jsi vylezl/a?", (choice) => {
            if (choice === 0) {
                _score += 20;
                popUp("Šampion lezení! 🏆 +20 bodů", "cestaKKameni");
            } else if (choice === 1) {
                _score += 10;
                popUp("Dobrá práce! 👍 +10 bodů", "cestaKKameni");
            } else {
                popUp("Příště určitě! 😊 +0 bodů", "cestaKKameni");
            }
            disable("skalka");
        }, "🥇 Byl jsem nejvýš ze všech!", "🥈 Vylezl jsem, ale ne nejvýš", "😅 Nevylezl jsem na strom");
    }
}

// Navigace k Obětnímu kameni
const cestaKKameni = {
    onStart: () => {
        heading("🗿 Cesta k Obětnímu kameni", "center");
        text("Teď se vydejte k Obětnímu kameni. Cesta z rozcestí Skalka je snadná. Je to po modré. 🔵 Modrá se u Obětního kamene rozdvojuje, tak nezapoměň odbočit. Je tam rozcestník.");
        distance(50.5161342, 15.0463153);
        text("PS: tvoje aktuální skóre: " + _score + " bodů 🏆");
    }
}

// Úkol 4: Obětní kámen - selfie v roli
const obetniKamen = {
    loc: [
        [50.5164900, 15.0461753],
        [50.5158675, 15.0461083],
        [50.5161078, 15.0470256]
    ],

    onStart: () => {
        heading("🗿 Obětní kámen");
        text("Každý udělá selfie, zatímco má určitou roli v obětním rituálu! 🎭");
        takePicture("📸 Selfie v rituálu");

        button("✅ Máme to!", () => {
            _score += 10;
            popUp("Děsivě dobré! 😈 +10 bodů", "cestaKBrane");
            disable("obetniKamen");
        });
    }
}

// Navigace ke Skalní bráně
const cestaKBrane = {
    onStart: () => {
        heading("🧭 Cesta ke Skalní bráně", "center");
        text("Teď se vydejte dál. Jelikož jsi zkušený navigátor, tak ti stačí jenom obrázek. Musíš se zorientovat a dojít na skalní bránu. Tam se ti otevře další úkol. 🗺️");
        text("⭐ Další důležitá informace! Pokud půjdeš přes Smrkovec, dostaneš navíc 15 bodů. 🎁");
        image("skalni-brana-polo-mapa.png");
        text("PS: tvoje aktuální skóre: " + _score + " bodů 🏆");
    }
}

// Bonusový úkol: Smrkovec
const smrkovec = {
    loc: [
        [50.5158469, 15.0484150],
        [50.5153011, 15.0495200],
        [50.5152261, 15.0482433]
    ],

    onStartFirst: () => {
        _score += 15;
    },

    onStart: () => {
        heading("🌲 Smrkovec - bonus! ⭐");
        text("Skvěle! Našel jsi Smrkovec! 🎉");
        text("Za to, že jsi zvolil tuto cestu, dostáváš bonusových +15 bodů! 🎁");
        text("Teď pokračuj ke Skalní bráně. 🧭");
        image("skalni-brana-polo-mapa.png");
        text("PS: tvoje aktuální skóre: " + _score + " bodů 🏆");
        disable("smrkovec");
    }
}

// Úkol 5: Ohřejte se u ohně (Skalní brána)
const skalniGate = {
    loc: [
        [50.5172608, 15.0535003],
        [50.5167833, 15.0542406],
        [50.5173767, 15.0545947]
    ],

    onStart: () => {
        heading("🔥 Skalní brána - oheň");
        text("Čas na odpočinek u ohně! ☕");
        text("Body dostaneš podle toho, jak moc jsi pomohl. 💪");

        multichoice("Co jsi u ohně dělal?", (choice) => {
            if (choice === 0) {
                _score += 25;
                popUp("Skvělé! 🔥 +25 bodů", "cestaKKapli");
            } else if (choice === 1) {
                _score += 10;
                popUp("Dobrá práce s dřevem! 🪵 +10 bodů", "cestaKKapli");
            } else {
                popUp("Příště se zapoj víc! 😊 +0 bodů", "cestaKKapli");
            }
            disable("skalniGate");
        }, "🔥 Zapálil jsem ho", "🪵 Nosil jsem dřevo", "👀 Jen jsem koukal");
    }
}

// Navigace ke kapli Branžež
const cestaKKapli = {
    onStart: () => {
        heading("⛪ Cesta ke kapli Branžež", "center");
        text("Teď se vydejte ke kapli Branžež. Sejdi z kopce k potoku a jdi na jih po proudu. 💧 Podél potoka vede cyklostezka 4009. Jakmile dojdeš do vesnice, tak uvidíš náves a tam je cíl. Ukazatel vzdálenosti by tě měl uklidnit. 🧭");
        distance(50.5078286, 15.0582128);
        text("PS: tvoje aktuální skóre: " + _score + " bodů 🏆");
    }
}

// Úkol 6: Kaple Branžež - vděčnost
const kaple = {
    loc: [
        [50.5076581, 15.0578403],
        [50.5075864, 15.0584572],
        [50.5080061, 15.0582800]
    ],

    onStart: () => {
        heading("⛪ Kaple Branžež");
        text("Poděkujte Bohu za dnešní den. 🙏");
        text("Každý řekne 3-5 věcí, za které je dneska vděčný. ❤️");

        button("✅ Hotovo", () => {
            _score += 10;
            popUp("Krásné! 💝 +10 bodů", "cestaKeStudne");
            disable("kaple");
        });

        button("⏭️ Přeskočit", () => {
            showTask("cestaKeStudne");
            disable("kaple");
        });
    }
}

// Navigace ke studně
const cestaKeStudne = {
    onStartFirst: () => {
        enable("studna");
    },

    onStart: () => {
        heading("🏠 Cesta domů", "center");
        text("Teď se vydejte zpátky domů. 🚶");
        text("Nejdříve jděte po silnici na jih, až dojdete k rozcestníku Branžež a parkovišti, pak pokračujte po Zelené značce správným směrem. 💚");
        distance(50.5099081, 15.0397272);
        text("PS: tvoje aktuální skóre: " + _score + " bodů 🏆");
    }
}

// Úkol 7: Studna - výzva s vodou
const studna = {
    loc: [
        [50.5097017, 15.0392578],
        [50.5097186, 15.0403631],
        [50.5103464, 15.0395528]
    ],

    onStart: () => {
        heading("💧 Studna před chalupou");
        text("Poslední výzva: Pokud se necháš polít kýblem vody ze studny, získáš dalších 50 bodů! 🪣");
        text("Odvážíš se? 😱");
        text("PS: tvoje aktuální skóre: " + _score + " bodů 🏆");

        button("🪣 Jdu do toho! 💦", () => {
            _score += 50;
            popUp("Hrdina! 🦸 +50 bodů", "finish");
            disable("studna");
        });

        button("😅 Raději ne (Pass)", () => {
            popUp("Škoda... Pokračujeme... 🏃", "finish");
            disable("studna");
        });
    }
}

// Finální úkol
const finish = {
    onStartFirst: () => {
        _timeElapsed = Math.floor((Date.now() - _timerStart) / 60000);
    },

    onStart: () => {
        heading("🎉 Gratulujeme! 🏆", "center");
        text("Dokončili jste Zápudovskou hru! 🎊");
        image("trophy.png");
        board("📊 Výsledky",
              "Celkové skóre 🏆", _score,
              "Čas ⏱️", _timeElapsed + " minut");

        showAllImages("📸 Vaše zážitky");
        shareButton();
        finishGameButton("📚 Zpět do knihovny");
    }
}
