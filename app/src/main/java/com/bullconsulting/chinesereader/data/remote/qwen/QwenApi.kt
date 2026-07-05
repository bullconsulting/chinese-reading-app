package com.bullconsulting.chinesereader.data.remote.qwen

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * The Qwen HTTP endpoint, described declaratively. Retrofit generates the
 * real networking code from these annotations.
 */
interface QwenApi {
    @POST("chat/completions")
    suspend fun chat(
        @Header("Authorization") authorization: String,
        @Body request: ChatRequest,
    ): ChatResponse
}
