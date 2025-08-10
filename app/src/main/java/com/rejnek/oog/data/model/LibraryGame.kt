package com.rejnek.oog.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LibraryGame(
    val id: String,
    val name: String,
    val gameCode: String,
    val importedAt: Long
)
