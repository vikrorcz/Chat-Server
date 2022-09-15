package com.example.models.websocket

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val username: String,
    val receivingUsername: String,
    val message: String
)