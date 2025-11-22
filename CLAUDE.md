# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Open Outdoor Games (OOG) / Questopix is an Android app for playing GPS-based outdoor games like treasure hunts and scavenger hunts. Games are written in JavaScript and packaged as ZIP files containing game logic, metadata (info.json), and assets (images).

The app is built with Kotlin and Jetpack Compose, targeting Android 10+ (minSdk 29). It uses WebView as a JavaScript engine to execute user-created games.

## Build & Development Commands

### Building the App
```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK (requires signing config in local.properties)
./gradlew assembleRelease

# Install debug build to connected device
./gradlew installDebug
```

### Testing
```bash
# Run unit tests
./gradlew test

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest
```

### Linting & Cleaning
```bash
# Run Kotlin linter
./gradlew lint

# Clean build artifacts
./gradlew clean
```

### Release Signing
The release build requires signing credentials in `local.properties`:
- `RELEASE_STORE_FILE`: Path to keystore file
- `RELEASE_STORE_PASSWORD`: Keystore password
- `RELEASE_KEY_ALIAS`: Key alias
- `RELEASE_KEY_PASSWORD`: Key password

## Code Architecture

### Layer Structure

**UI Layer** (`ui/`)
- **Screens**: Main app screens (Home, Library, GameInfo, GameTask, Settings, Onboarding)
- **Components**: Reusable UI components organized by feature (gameInfo, library, home, settings, permissions)
- **ViewModels**: Screen-specific state management and business logic
- **Navigation**: AppRouter handles navigation between screens using Jetpack Navigation Compose
- **Theme**: Material3 theming (colors, typography, shapes)

**Data Layer** (`data/`)
- **Repository**: Business logic and data coordination
  - `GameRepository`: Core game management, coordinates between JS engine, storage, location, and UI
  - `StorageRepository`: Persistent storage using shared preferences (game packages, library, saved games)
  - `LocationRepository`: GPS location tracking and geofencing for location-based tasks
  - `CommandRepository`: Registry of all game API commands (UI elements, interactions, game flow)
  - `GameUIRepository`: Manages current game UI state (composables to render)
- **Model**: Data classes (`GamePackage`, `GameState`, `Coordinates`, `Area`, `GameTask`)
- **Storage**: `GameStorage` handles file operations for game assets

**Game Engine** (`engine/`)
- **JsGameEngine**: WebView-based JavaScript runtime for executing game code
  - Initializes WebView with game API functions
  - Evaluates JavaScript code and retrieves values
  - Manages game state persistence
  - Handles task lifecycle (onStart, onStartFirst)
- **JsGameInterface**: Bridge between JavaScript and Kotlin via `@JavascriptInterface` annotations
- **Commands**: Game API implementations
  - `direct/factory/`: UI elements (heading, text, image, button, etc.)
  - `direct/simple/`: Utility commands (save, refresh, debugPrint)
  - `callback/`: Interactive elements with callbacks (button, question, multichoice)
  - Each command factory provides JavaScript function definition and Kotlin implementation

**Dependency Injection** (`di/`)
- Uses Koin for dependency injection
- `AppModule` defines all ViewModels and repositories

### Key Technical Patterns

**Game Execution Flow**:
1. User selects game from library
2. `GameRepository.startGame()` initializes JS engine with game code
3. Game state restored if resuming saved game
4. Task lifecycle managed via `executeOnStart()` which calls `onStartFirst()` once, then `onStart()` every time
5. Location monitoring activates for tasks with `loc` property (polygon coordinates)
6. JavaScript commands communicate with Kotlin via `Android` interface in WebView
7. Game state persisted automatically when `save()` called from JS

**Game API Implementation**:
- Each game function (e.g., `heading()`, `button()`, `question()`) is implemented as a `GenericCommandFactory`
- Factory provides JavaScript function definition injected into WebView HTML template
- JavaScript calls `Android.methodName()` which triggers corresponding Kotlin code
- UI updates flow through `GameUIRepository` which notifies `GameTaskViewModel`
- ViewModels observe state and recompose UI

**State Management**:
- ViewModels use StateFlow/MutableStateFlow for reactive state
- Game state persisted as JSON in SharedPreferences
- Global game variables (prefixed with `_`) automatically saved/restored
- Current task ID tracked to resume games

**Location Handling**:
- Tasks define location areas as polygons (`loc: [[lat,lng], ...]`)
- `LocationRepository` monitors user location and checks geofence intersections
- When user enters area, task's `onStart()` triggered automatically
- Tasks can be disabled/enabled to control location detection

## Game Format

Games are ZIP packages containing:
- `info.json`: Metadata (id, name, description, coverPhoto, start/finish locations, attributes)
- `game.js`: JavaScript game logic with task definitions
- Images: Assets referenced in the game

See `/docs/GameFormatSpecification.md` for complete game API documentation.

## Important Files & Locations

- `/app/src/main/assets/bundled_games/`: Example games bundled with the app
- `/app/src/main/assets/example_games/`: Game templates for users
- `/docs/GameFormatSpecification.md`: Complete game format and API reference
- `/docs/GettingStarted.md`: Guide for creating games
- `/app/build.gradle.kts`: App-level build configuration (dependencies, SDK versions, signing)
- `/gradle/libs.versions.toml`: Dependency version catalog

## Key Dependencies

- **Jetpack Compose**: Modern declarative UI framework
- **Koin**: Dependency injection
- **WebView**: JavaScript engine for game execution
- **Play Services Location**: GPS tracking and geofencing
- **Kotlinx Serialization**: JSON parsing for game metadata and state
- **Coil**: Image loading
- **Navigation Compose**: Screen navigation

## Localization

The app supports multiple languages (currently English and Czech). String resources are in:
- `/app/src/main/res/values/strings.xml` (English - default)
- `/app/src/main/res/values-cs/strings.xml` (Czech)

Add new strings to both files when adding UI text.

## Testing Games

During development, test games without location requirements first. Location-based tasks require:
1. Physical device or emulator with GPS
2. Location permissions granted
3. Being within 75m of start location to begin game
4. Being inside task polygon boundaries to trigger location tasks

Use `debugPrint()` in game JavaScript to log debug messages visible in logcat.
