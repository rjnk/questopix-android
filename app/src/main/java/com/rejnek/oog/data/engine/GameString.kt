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
            showElement("openQuestion1");
        }
    }

    // navigace na 1. úkol
    const nav1 = {
        name: "Dostaň se pod most",
        type: "navigation",
        coordinates: {
            lat: 50.1815,
            lng: 14.4980,
            radius: 25
        },
        description: "Jdi po cestě až se dostaneš k malému mostku.",
        onContinue: function() {
            if(20 > 50) {
                showElement("finish");
            }
            else{
                showElement("task1");
            }
        }
    }

    // 1. úkol
    const task1 = {
        name: "Mostní úkol",
        type: "task",
        description: "Projdi se pod mostem, pak můžeš pokračovat",
        onContinue: function() {
            showElement("nav2");
        }
    }
    
    // open question
    const openQuestion1 = {
        name: "Otázka o stromech",
        type: "task2",
        description: "Odpověz na otázku o stromech",
        onStart: async function() {
            debugPrint("Open question shown");
            
            // Use await with the question function
            const answer = await question("Odpověz na otázku: Jaký je tvůj oblíbený strom?");
            
            if (answer === "buk") {
                debugPrint("Správně!");
            }
            else {
                debugPrint("Špatně.");
            }
            
            debugPrint("Pokračuji po odpovědi");
            
            button(
                "Pokračovat", 
                function() {
                    showElement("nav1");
                });
        }
    }

    // navigace na 2. úkol
    const nav2 = {
        name: "Vydej se k rybníku",
        type: "navigation",
        coordinates: {
            lat: 50.0900,
            lng: 14.4100,
            radius: 30
        },
        description: "Následuj značky až k rybníku.",
        onContinue: function() {
            showElement("finish");
        }
    }

    // 2. úkol
    const task2 = {
        name: "Rybníkový úkol",
        type: "task",
        description: "Pozoruj přírodu kolem rybníka a pak pokračuj. TUUUUU!",
        onContinue: function() {
            showElement("finish");
        }
    }

    // ukončení hry
    const finish = {
        name: "Konec hry",
        type: "finish",
        description: "Gratulujeme! Dokončili jste hru v divočině!",
    }
""".trimIndent()