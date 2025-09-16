// Vlastní proměnné
var _skore = 0;
var _casStart = Date.now();
var _pouziteNapovedy = 0;

// Začátek hry
const start = {
    onStart: () => {
        heading("Domácí poklad 🏠🔍", "Vítejte!");
        text("Vítejte v domácí hře na hledání pokladu! Vaším úkolem je splnit různé výzvy po celém domě. Každý úkol prověří vaše pozorovací schopnosti.");
        text("Za správné odpovědi získáte body, za použití nápověd body ztratíte. Uvidíme, jak dobře znáte svůj vlastní domov!");

        takePicture("Vyfotografujte se na začátku hry!");

        button("Začít hru", () => {
            _casStart = Date.now();
            showTask("kuchyneUkol");
        });
    }
}

// Kuchyňský úkol
const kuchyneUkol = {
    onStart: () => {
        heading("Kuchyňský detektiv 🍳");
        text("První výzva se odehrává v kuchyni. Čas ukázat své pozorovací schopnosti!");

        takePicture("Vyfotografujte něco červeného ve vaší kuchyni");

        multichoice("Kolik různých druhů koření najdete ve své kuchyni?", (cisloOdpovedi) => {
            if(cisloOdpovedi === 0) { // Více než 10
                _skore += 20;
                popUp("Výborně! Máte dobře zásobenou kuchyni! +20 bodů", "koupelnaUkol");
            } else if(cisloOdpovedi === 1) { // 5-10
                _skore += 10;
                popUp("Není to špatné! +10 bodů", "koupelnaUkol");
            } else { // Méně než 5
                _skore += 5;
                popUp("Čas jít nakoupit! +5 bodů", "koupelnaUkol");
            }
        }, "5-10 druhů koření", "Více než 10 druhů", "Méně než 5 druhů");

        button("Potřebuji nápovědu (-5 bodů)", () => {
            _skore -= 5;
            _pouziteNapovedy++;
            popUp("Nápověda: Podívejte se na police s kořením, do skříněk a nezapomeňte na sůl a pepř!");
        });
    }
}

// Koupelnový úkol
const koupelnaUkol = {
    onStart: () => {
        heading("Koupelnový inspektor 🚿");
        text("Čas na koupelnové pátrání! Nebojte se, nic moc trapného.");

        question("Jakou barvu má váš zubní kartáček? (jedno slovo)", (odpoved) => {
            const barvy = ["červený", "červená", "modrý", "modrá", "zelený", "zelená", "žlutý", "žlutá", "bílý", "bílá", "černý", "černá", "růžový", "růžová", "fialový", "fialová", "oranžový", "oranžová", "hnědý", "hnědá"];
            if (barvy.includes(odpoved.toLowerCase())) {
                _skore += 15;
                popUp("Výborné pozorovací schopnosti! +15 bodů", "loznice");
            } else {
                _skore += 5;
                popUp("Zajímavá barva! +5 bodů tak jako tak", "loznice");
            }
        });

        text("Aktuální skóre: " + _skore + " bodů");

        button("Nápověda prosím (-5 bodů)", () => {
            _skore -= 5;
            _pouziteNapovedy++;
            popUp("Nápověda: Podívejte se do zrcadla nad umyvadlem!");
        });
    }
}

// Ložnicový úkol
const loznice = {
    onStart: () => {
        heading("Průzkumník ložnice 🛏️");
        text("Podívejme se, jak dobře znáte svůj prostor na spaní!");

        takePicture("Najděte v ložnici něco, co začína na písmeno 'P' a vyfotografujte to");

        multichoice("Kolik polštářů máte právě teď na posteli?", (cisloOdpovedi) => {
            _skore += 10;
            if(cisloOdpovedi === 0) {
                popUp("Minimalista! +10 bodů", "obyvak");
            } else if(cisloOdpovedi === 1) {
                popUp("Klasická volba! +10 bodů", "obyvak");
            } else {
                popUp("Milovník pohodlí! +10 bodů", "obyvak");
            }
        }, "1 polštář", "2 polštáře", "3 nebo více polštářů");

        button("Přeskočit tento pokoj (-10 bodů)", () => {
            _skore -= 10;
            showTask("obyvak");
        });
    }
}

// Obývací pokoj
const obyvak = {
    onStart: () => {
        heading("Výzva v obýváku 📺");
        text("Skoro hotovo! Toto je vaše poslední místnostní výzva.");

        question("Jaký je název knihy, kterou vidíte odkud stojíte? (Pokud nevidíte žádnou, napište 'žádná')", (odpoved) => {
            if (odpoved.toLowerCase() !== "žádná") {
                _skore += 20;
                popUp("Další čtenář! Výborně! +20 bodů", "finaleUkol");
            } else {
                _skore += 5;
                popUp("Žádné knihy na dohled, ale to nevadí! +5 bodů", "finaleUkol");
            }
        });

        takePicture("Vyfotografujte svůj obývák z neobvyklého úhlu");

        text("Použité nápovědy: " + _pouziteNapovedy);
        text("Aktuální skóre: " + _skore + " bodů");
    }
}

// Finální úkol
const finaleUkol = {
    onStart: () => {
        heading("Velké finále! 🎊");
        text("Gratulujeme k dokončení domácího hledání pokladu! Čas na závěrečnou výzvu.");

        multichoice("Která místnost se vám líbila prozkoumat nejvíce?", (cisloOdpovedi) => {
            _skore += 25; // Bonusové body za dokončení
            if(cisloOdpovedi === 0) {
                popUp("Kuchyňská dobrodružství jsou nejlepší! +25 bonusových bodů", "konec");
            } else if(cisloOdpovedi === 1) {
                popUp("Koupelnové pátrání se vyplatilo! +25 bonusových bodů", "konec");
            } else if(cisloOdpovedi === 2) {
                popUp("Sladké sny a body! +25 bonusových bodů", "konec");
            } else {
                popUp("Obývákový relax vyhrává! +25 bonusových bodů", "konec");
            }
        }, "Kuchyně", "Koupelna", "Ložnice", "Obývák");

        takePicture("Vyfotografujte se jako vítěz - zasloužíte si to!");
    }
}

// Konec hry
const konec = {
    onStart: () => {
        heading("Mise splněna! 🏆", "center");
        image("trophy.png");
        text("Skvělá práce! Úspěšně jste dokončili domácí hledání pokladu. Dokázali jste, že dobrodružství se dá najít kdekoli, dokonce i ve vašem vlastním domově!");

        const ubehleMilisekundy = Date.now() - _casStart;
        const ubehleMminuty = Math.floor(ubehleMilisekundy / (60 * 1000));

        board("Konečné výsledky", "Skóre", _skore, "Čas", ubehleMminuty + " min.", "Použité nápovědy", _pouziteNapovedy);

        shareButton();
        showAllImages("Vaše fotky z hledání pokladu:");
        finishGameButton("Zpět do menu");
    }
}
