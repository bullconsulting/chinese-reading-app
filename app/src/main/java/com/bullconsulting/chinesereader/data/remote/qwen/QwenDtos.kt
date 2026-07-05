package com.bullconsulting.chinesereader.data.remote.qwen

import kotlinx.serialization.Serializable

/**
 * DTOs = Data Transfer Objects: the exact JSON shapes the Qwen API expects and returns.
 * These live in the data layer; they never leak into the domain.
 */
@Serializable
data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
)

@Serializable
data class ChatMessage(
    val role: String,
    val content: String,
)

@Serializable
data class ChatResponse(
    val choices: List<ChatChoice> = emptyList(),
)

@Serializable
data class ChatChoice(
    val message: ChatMessage,
)
