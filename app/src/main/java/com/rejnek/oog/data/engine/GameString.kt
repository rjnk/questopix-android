package com.rejnek.oog.data.engine

val demoGame = """
    // startovní úkol
    const start = {
        name: "Hra v divočině",
        type: "start",
        gameType: "linear",
        coordinates: {
            lat: 50.0815,
            lng: 14.3980,
            radius: 25
        },
        description: "Tohle je jednoduchá demonstrační hra pro účely vyzkoušení načítání z javascriptu.",
        onContinue: function() {
            debugPrint("CONSOLE PRINT: game is starting!");
            showTask("task1");
        }
    }

    // 1. úkol
    const task1 = {
        name: "Mostní úkol",
        type: "task",
        onStart: function() {
            text("Projdi se pod mostem, pak můžeš pokračovat");
            button("Pokračovat", function() {
                showTask("openQuestion1");
            });
            text("...nebo zkratka");
            button("Rovnou do cíle", function() {
                showTask("finish");
            });
        }
    }
    
    // open question
    const openQuestion1 = {
        name: "Otázka o stromech",
        type: "task",
        description: "Odpověz na otázku o stromech",
        onStart: async function() {
            debugPrint("Open question shown");
            
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

    // ukončení hry
    const finish = {
        name: "Konec hry",
        type: "finish",
        description: "Gratulujeme! Dokončili jste hru v divočině!",
    }
""".trimIndent()