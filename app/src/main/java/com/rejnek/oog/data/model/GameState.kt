package com.rejnek.oog.data.model

import kotlinx.serialization.Serializable

/**
 * Represents the lifecycle state of a game.
 */
@Serializable
enum class GameState {
    /** Initial state when the game has not been started yet */
    NOT_STARTED,
    /** State when the game has been started but is not yet completed */
    IN_PROGRESS,
    /** Transient state meant to trigger UI behavior (shows game completion, navigates away) */
    FINISHED,
    /** Final persisted state for completed games (used for visual styling in library, prevents saving/location monitoring) */
    ARCHIVED
}
