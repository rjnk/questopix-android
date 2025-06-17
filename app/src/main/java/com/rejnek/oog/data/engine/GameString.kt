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
            Android.debugPrint("CONSOLE PRINT: game is starting!");
            Android.showTask("task1");
        }
    }

    // 1. úkol
    const task1 = {
        name: "Mostní úkol",
        type: "task",
        description: "Projdi se pod mostem, pak můžeš pokračovat",
        onStart: function() {
            button("Pokračovat", function() {
                Android.showTask("openQuestion1");
            });
            button("Rovnou do cíle", function() {
                Android.showTask("finish");
            });
        },
        onContinue: function() {
            Android.showTask("openQuestion1");
        }
    }
    
    // open question
    const openQuestion1 = {
        name: "Otázka o stromech",
        type: "task2",
        description: "Odpověz na otázku o stromech",
        onStart: async function() {
            Android.debugPrint("Open question shown");
            
            // Use await with the question function
            const answer = await question("Odpověz na otázku: Jaký je tvůj oblíbený strom?");
            
            if (answer === "buk") {
                Android.debugPrint("Správně!");
                Android.showTask("finish");
            }
            else {
                Android.debugPrint("Špatně.");
                Android.showTask("task1");
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