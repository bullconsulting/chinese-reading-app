package com.bullconsulting.chinesereader.di

import com.bullconsulting.chinesereader.data.remote.qwen.QwenApi
import com.bullconsulting.chinesereader.data.remote.sheets.SheetsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

/**
 * Tells Hilt how to build the networking stack (JSON parser, Retrofit, Qwen API).
 * BASE_URL is the one place to change the Qwen endpoint (e.g. mainland vs international).
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // DashScope US-region endpoint. Other regions:
    //   mainland China : https://dashscope.aliyuncs.com/compatible-mode/v1/
    //   Singapore/intl : https://dashscope-intl.aliyuncs.com/compatible-mode/v1/
    private const val BASE_URL = "https://dashscope-us.aliyuncs.com/compatible-mode/v1/"

    @Provides
    @Singleton
    fun provideJson(): Json = Json { ignoreUnknownKeys = true }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        // Logs request/response for debugging. The Authorization header (the API
        // key) is REDACTED so the secret never appears in logs.
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
            redactHeader("Authorization")
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(json: Json, client: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideQwenApi(retrofit: Retrofit): QwenApi = retrofit.create(QwenApi::class.java)

    @Provides
    @Singleton
    fun provideSheetsApi(json: Json, client: OkHttpClient): SheetsApi {
        val contentType = "application/json".toMediaType()
        // Sheets uses a different host, so it gets its own Retrofit instance.
        return Retrofit.Builder()
            .baseUrl("https://sheets.googleapis.com/")
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(SheetsApi::class.java)
    }
}
