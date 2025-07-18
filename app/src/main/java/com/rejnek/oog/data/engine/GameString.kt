package com.rejnek.oog.data.engine

val demoGame = """
    // startovní úkol / first task
    const start = {
        name: "Hra v divočině",
        type: "task",
        onStart: function() {
            heading("Hra v divočině");
            text("Vítejte v naší hře! Tato hra je o dobrodružství v divočině.");
            text("Vaším úkolem je projít různými úkoly a odpovědět na otázky.");
            text("Připravte se na zábavu!");
            button("Začít hru", function() {
                setVisible("task1");
                setVisible("openQuestion1");
                setVisible("map1");
                setSecondary("task1");
                setHidden("start");
            });
        }
    }

    // 1. úkol
    const task1 = {
        name: "Mostní úkol",
        type: "task",
        coordinates: {
            lat: 50.0,
            lng: 14.0,
            radius: 50.0
        },
        onStart: function() {
            heading("Pod mostem");
            distance();
            text("Projdi se pod mostem, pak můžeš pokračovat");
            button("Pokračovat", function() {
                showTask("openQuestion1");
            });
            text("...nebo zkratka");
            button("Rovnou do cíle", function() {
                showTask("finish");
            });
//            text("nebo menu");
//            button("Zpět do menu", function() {
//                setHidden("task1");
//                setVisible("task1");
//            });
            text("nebo mapa");
            button("Zobrazit mapu", function() {
                showTask("map1");
            });
        },
        onEnter: function() {
            debugPrint("Vstoupil jsi do úkolu 1");
            showTask("finish");
        },
    }
    
    // open question
    const openQuestion1 = {
        name: "Otázka o stromech",
        type: "task",
        coordinates: {
            lat: 50.01,
            lng: 14.0,
            radius: 50.0
        },
        description: "Odpověz na otázku o stromech",
        onStart: async function() {
            debugPrint("Open question shown");
            heading("Otevřená otázka o stromech");
            
            // Use await with the question function
            const answer = await question("Odpověz na otázku: Jaký je tvůj oblíbený strom?");
            
            if (answer === "buk") {
                debugPrint("Správně!");
                showTask("finish");
            }
            else {
                debugPrint("Špatně.");
                showTask("task1");
            }
            
            return;
        }
    }
    
    // map task
    const map1 = {
        name: "Mapa",
        type: "task",
        description: "Koukni se na mapu a najdi další úkoly",
        onStart: async function() {
            debugPrint("Map shown");
            heading("Mega mapa úkolů");
            map();
        }
    }

    // ukončení hry
    const finish = {
        name: "Konec hry",
        type: "finish",
        description: "Gratulujeme! Dokončili jste hru v divočině!",
    }
""".trimIndent()