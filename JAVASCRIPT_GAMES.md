# JavaScript Game Integration Guide

## Overview

Your OOG Android app now supports loading and executing games written in JavaScript! This allows for dynamic game creation without recompiling the Android app.

## How It Works

The system uses Mozilla Rhino JavaScript engine to:
1. Parse JavaScript game definitions
2. Extract game elements (start, navigation, tasks)
3. Execute dynamic functions (`onEnter`, `onContinue`)
4. Manage game state and element visibility

## JavaScript Game Structure

### Basic Game Element

```javascript
const elementName = {
    name: "Display Name",
    coordinates: {
        lat: 50.0815,        // GPS latitude
        lng: 14.3980,        // GPS longitude  
        radius: 25           // Radius in meters
    },
    description: "Description text",
    onEnter: function() {
        // Executed when player enters the location
        show("nextElement");
    },
    onContinue: function() {
        // Executed when player completes the task
        show("anotherElement");
    }
}
```

### Element Types

1. **Start Element** - Has `gameType` property
2. **Navigation Element** - Has `onEnter` function
3. **Task Element** - Has `onContinue` function

### Available JavaScript Functions

- `show(elementName)` - Makes an element visible to the player

## Example Game

```javascript
// Starting point
const start = {
    name: "Adventure Begins",
    gameType: "linear",
    coordinates: {
        lat: 50.0815,
        lng: 14.3980,
        radius: 25
    },
    description: "Welcome to your adventure!"
}

// Navigation to first task
const nav1 = {
    name: "Find the Bridge",
    coordinates: {
        lat: 50.1815,
        lng: 14.4980,
        radius: 25
    },
    description: "Walk to the small bridge.",
    onEnter: function() {
        show("task1");  // Show task1 when player reaches bridge
    }
}

// First task
const task1 = {
    name: "Bridge Challenge",
    description: "Cross the bridge carefully.",
    onContinue: function() {
        show("nav2");   // Show next navigation when task completed
    }
}
```

## Loading Games

### Method 1: Inline JavaScript (GameRepository.loadDemoGame())
```kotlin
val result = gameRepository.loadDemoGame()
```

### Method 2: From Asset File
```kotlin
val result = gameRepository.loadGameFromAsset(context, "demo_game.js")
```

### Method 3: From String
```kotlin
val gameScript = "const start = { ... }"
val result = gameRepository.loadGameFromJavaScript(gameScript)
```

## Game State Management

### Check Element Status
```kotlin
// Check if element is visible
val isVisible = gameRepository.isElementVisible("task1")

// Check if element is completed  
val isCompleted = gameRepository.isElementCompleted("task1")

// Get visible elements
val visibleElements = gameRepository.getVisibleElements()
```

### Execute Game Events
```kotlin
// When player enters a location
gameRepository.setCurrentElement(element)

// When player completes a task
gameRepository.continueFromCurrentElement()
```

## File Locations

- **Data Models**: `app/src/main/java/com/rejnek/oog/data/model/GameModels.kt`
- **JavaScript Engine**: `app/src/main/java/com/rejnek/oog/data/engine/JavaScriptGameEngine.kt`  
- **Game Repository**: `app/src/main/java/com/rejnek/oog/data/repository/GameRepository.kt`
- **Demo Game**: `app/src/main/assets/demo_game.js`

## Best Practices

1. **Always include coordinates** for location-based games
2. **Use descriptive element names** for easier debugging
3. **Test JavaScript syntax** before loading into app
4. **Handle errors gracefully** when loading games
5. **Use meaningful descriptions** for better user experience

## Error Handling

The system provides detailed error messages for:
- JavaScript syntax errors
- Missing required properties
- Function execution failures
- Element not found errors

## Future Enhancements

Possible extensions to the system:
- Save/load game progress
- Multiple game file support
- Online game downloading
- Custom JavaScript functions
- Game validation tools
- Visual game editor

## Testing

Use the HomeScreen buttons to test:
1. "Load Demo Game (Inline JS)" - Tests inline JavaScript
2. "Load Demo Game (Asset File)" - Tests asset file loading

Both should successfully load and display the game information.
