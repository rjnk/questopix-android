package com.rejnek.oog.data.engine

val demoGame = """
    // generic
    var _gameType = "branching";
    
    // for branching
    var _currentTask = "start";
    
    // for open
    var _secondaryTask = "";
    var _visibleTasks = [];
    
    // custom
    var _score = 0;
    
    // startovní úkol / first task
    const start = {
        name: "Hra v divočině",
        type: "task",
        onStart: function() {
            heading("Dlouhá hra");
            text("Vítejte v dlouhé hře.");
            button("Začít hru", function() {                
                showTask("task1");
            });
        }
    }

    // 1. úkol
    const task1 = {
        name: "První úkol",
        type: "task",
        coordinates: {
            lat: 50.0,
            lng: 14.0,
            radius: 50.0
        },
        onStart: function() {
            _score += 10;
            heading("První úkol");
            text("_score: " + _score);
            button("Další úkol", function() {
                showTask("task2");
            });
        }
    }
    
    // 2. úkol
    const task2 = {
        name: "Druhý úkol",
        type: "task",
        coordinates: {
            lat: 50.0,
            lng: 14.0,
            radius: 50.0
        },
        onStart: function() {
            _score += 10;
            heading("Druhý úkol");
            text("_score: " + _score);
            save();
            button("Další úkol", function() {
                showTask("task3");
            });
        }
    }
    
    // 3. úkol
    const task3 = {
        name: "První úkol",
        type: "task",
        coordinates: {
            lat: 50.0,
            lng: 14.0,
            radius: 50.0
        },
        onStart: function() {
            _score += 10;
            heading("Třetí úkol");
            text("_score: " + _score);
            button("Další úkol", function() {
                showTask("finish");
            });
        }
    }

    // ukončení hry
    const finish = {
        name: "Konec hry",
        type: "finish",
        description: "Gratulujeme! Dokončili jste dlouhou hru!",
    }
""".trimIndent()


val demoGameOrg = """
    // generic
    let _gameType = "branching";
    
    // for branching
    let _currentTask = "start";
    
    // for open
    let _secondaryTask = "task1";
    let _visibleTasks = ["start"];
    
    // custom
    let _score = 20;
    
    // startovní úkol / first task
    const start = {
        name: "Hra v divočině",
        type: "task",
        onStart: function() {
            heading("Hra v divočině!!!");
            text("Vítejte v naší hře! Tato hra je o dobrodružství v divočině.");
            text("Vaším úkolem je projít různými úkoly a odpovědět na otázky.");
            text("Připravte se na zábavu!");
            button("Začít hru", function() {
                _gameType = "open";
                
                // showTask("task1");
                // OR
                _currentTask = "task1";
                if (!_visibleTasks.includes("task1")) { _visibleTasks.push("task1"); }
                _visibleTasks = _visibleTasks.filter(task => task !== "start");
                refresh();
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
            _score += 10;
            heading("Pod mostem");
            distance();
            text("_score: " + _score);
            text("Projdi se pod mostem, pak můžeš pokračovat");
            button("Pokračovat", function() {
                showTask("openQuestion1");
                _visibleTasks = _visibleTasks.filter(task => task !== "map1");
            });
            text("...nebo zkratka");
            button("Rovnou do cíle", function() {
                showTask("finish");
            });
            text("nebo mapa");
            button("Zobrazit mapu", function() {
                // todo this doesn't work as expected
                _visibleTasks = _visibleTasks.filter(task => task !== "task1");
                if (!_visibleTasks.includes("map1")) { _visibleTasks.push("map1"); }
                refresh();
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