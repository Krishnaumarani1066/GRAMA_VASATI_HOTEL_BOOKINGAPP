package com.yuriikonovalov.helia.data.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@InternalSerializationApi
@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>
    // ✅ REMOVED: systemInstruction — not supported by gemini-pro / v1
)

@InternalSerializationApi
@Serializable
data class GeminiContent(
    val role: String,
    val parts: List<GeminiPart>
)

@InternalSerializationApi
@Serializable
data class GeminiPart(
    val text: String
)

@InternalSerializationApi
@Serializable
data class GeminiResponse(
    val candidates: List<GeminiCandidate> = emptyList()
)

@InternalSerializationApi
@Serializable
data class GeminiCandidate(
    val content: GeminiContent
)

@InternalSerializationApi
@Serializable
data class AnthropicMessage(
    val role: String,
    val content: String
)

@Singleton
class AnthropicApiService @Inject constructor(
    private val client: OkHttpClient
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val apiKey = "AIzaSyAEeZPdGEthjx4tfjeWgkPETHD55n_H6Qs"

    private val systemPrompt = """
        You are a helpful hotel assistant for the Helia hotel booking app.
        Help users find hotels, make recommendations based on their preferences,
        answer questions about amenities, locations, pricing, and bookings.
        Be concise, friendly, and professional.
        When recommending hotels, mention key details like location, price range,
        and standout features. Keep responses under 150 words.
    """.trimIndent()

    @OptIn(InternalSerializationApi::class)
    suspend fun sendMessage(messages: List<AnthropicMessage>): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                // ✅ System prompt injected as first user+model exchange
                val contents = mutableListOf<GeminiContent>()

                contents.add(
                    GeminiContent(
                        role = "user",
                        parts = listOf(GeminiPart(text = systemPrompt))
                    )
                )
                contents.add(
                    GeminiContent(
                        role = "model",
                        parts = listOf(GeminiPart(text = "Understood! I'm ready to help with hotel recommendations."))
                    )
                )

                // ✅ Append actual chat history
                messages.forEach { msg ->
                    contents.add(
                        GeminiContent(
                            role = if (msg.role == "assistant") "model" else "user",
                            parts = listOf(GeminiPart(text = msg.content))
                        )
                    )
                }

                val requestBody = GeminiRequest(contents = contents)

                val body = json.encodeToString(requestBody)
                    .toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey")
                    .addHeader("content-type", "application/json")
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                    ?: return@withContext Result.failure(Exception("Empty response"))

                if (!response.isSuccessful) {
                    return@withContext Result.failure(
                        Exception("API error: ${response.code} - $responseBody")
                    )
                }

                val parsed = json.decodeFromString<GeminiResponse>(responseBody)
                val text = parsed.candidates
                    .firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: return@withContext Result.failure(Exception("No text in response"))

                Result.success(text)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}