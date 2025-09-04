package com.rejnek.oog.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GamePackage(
    val id: String,
    val gameCode: String,
    val importedAt: Long,
)
