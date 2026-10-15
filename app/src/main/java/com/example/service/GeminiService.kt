package com.example.service

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * AI Exposure Writing Assistant Service
 *
 * Implements strict clinical ERP guardrails:
 * - Never offers reassurance or certitude.
 * - Reframes feared situations into observable exposures in the present tense.
 * - Supports offline fallback templates so the app remains 100% usable without internet.
 * - Designed for server-side proxy integration in production to avoid packaging API keys in APK.
 */
object GeminiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private const val MODEL = "gemini-3.5-flash"

    // Optional server proxy endpoint. When deployed with a backend proxy, set this URL.
    var customBackendProxyUrl: String? = null

    private const val SYSTEM_INSTRUCTION =
        "You are an evidence-informed ERP writing assistant for someone with OCD, not a clinician. " +
        "CRITICAL CLINICAL RULES: " +
        "1. Never offer reassurance. Never say a feared outcome is unlikely, safe, harmless, or untrue. " +
        "2. Never confirm or deny feared outcomes. Do not provide certainty. " +
        "3. Never suggest checking, avoidance, mental rituals, or physical safety rituals. " +
        "4. Use acceptance-based, uncertainty-tolerant phrasing (e.g., 'I am choosing to sit with the possibility', 'I cannot be 100% certain, and I choose to proceed anyway'). " +
        "5. If the user mentions suicide, self-harm, or crisis, immediately output only: CRISIS_DETECTED. " +
        "6. Keep responses under 120 words, strictly in clear, observable, present-tense language."

    suspend fun phraseExposureItem(
        userPrompt: String,
        itemType: String // "ladder_item", "imaginal_script", "clinician_question", or "reflection"
    ): Result<String> = withContext(Dispatchers.IO) {
        val safetyCheck = SafetyChecker.evaluate(userPrompt)
        if (safetyCheck.isTriggered) {
            return@withContext Result.failure(SecurityException("CRISIS_DETECTED"))
        }

        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        // If no API key or default placeholder, provide high-quality offline clinical template
        if ((apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") && customBackendProxyUrl.isNullOrBlank()) {
            return@withContext Result.success(getErpTemplateResponse(userPrompt, itemType))
        }

        try {
            val url = customBackendProxyUrl ?: "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent?key=$apiKey"

            val prompt = when (itemType) {
                "ladder_item" -> "Help me phrase this OCD exposure situation into an observable behavior that sits with uncertainty: \"$userPrompt\""
                "imaginal_script" -> "Help me write an imaginal exposure script (under 90 words) in the present tense focusing on accepting uncertainty for: \"$userPrompt\""
                "clinician_question" -> "Help me formulate 2 brief, non-reassuring questions to discuss with my therapist regarding: \"$userPrompt\""
                else -> "Give me a neutral 1-sentence reflection prompt about tolerating uncertainty after: \"$userPrompt\""
            }

            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", SYSTEM_INSTRUCTION)
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.3)
                    put("maxOutputTokens", 250)
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext Result.success(getErpTemplateResponse(userPrompt, itemType))
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text", "") ?: ""

            if (text.contains("CRISIS_DETECTED", ignoreCase = true)) {
                Result.failure(SecurityException("CRISIS_DETECTED"))
            } else if (text.isNotBlank()) {
                Result.success(text.trim())
            } else {
                Result.success(getErpTemplateResponse(userPrompt, itemType))
            }
        } catch (e: Exception) {
            Result.success(getErpTemplateResponse(userPrompt, itemType))
        }
    }

    private fun getErpTemplateResponse(prompt: String, itemType: String): String {
        return when (itemType) {
            "ladder_item" -> {
                "Exposure situation: \"$prompt without engaging in checking, washing, or mental rationalizing. Notice the uncertainty and allow the physical feeling to run its natural course.\""
            }
            "imaginal_script" -> {
                "Imaginal script: \"I am sitting right now with the thought: '$prompt'. I cannot be 100% certain of what will happen. My mind demands that I seek certainty, but I choose to stay in this room, feel the tightness in my chest, and take no action to neutralize it.\""
            }
            "clinician_question" -> {
                "Discussion questions: \"1. When facing the trigger '$prompt', how can I ensure I am not substituting subtle mental rituals? 2. What behavioral indicators show I am building tolerance for uncertainty rather than just gritting my teeth?\""
            }
            else -> {
                "Reflection: \"You noticed an urge around '$prompt' and chose to sit with the discomfort. What did this show you about your capacity to tolerate uncertainty?\""
            }
        }
    }
}
