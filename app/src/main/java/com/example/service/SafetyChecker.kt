package com.example.service

data class SafetyCheckResult(
    val isTriggered: Boolean,
    val isImminentRisk: Boolean = false,
    val isAmbiguousRisk: Boolean = false,
    val explanation: String? = null
)

object SafetyChecker {
    // Explicit, high-confidence crisis phrases
    private val explicitCrisisPhrases = listOf(
        "kill myself",
        "suicide",
        "end my life",
        "hurt myself",
        "harm myself",
        "want to die",
        "cutting myself",
        "overdose",
        "take my life",
        "better off dead",
        "hanging myself",
        "slit my wrists"
    )

    // Indirect or implicit distress phrases requiring supportive clarification
    private val indirectDistressPhrases = listOf(
        "can't go on living",
        "no reason to live",
        "give up on living",
        "wish i was never born",
        "world would be better without me",
        "can't take this life anymore",
        "hate living like this, want to end it"
    )

    // Benign contexts in OCD therapy: educational, clinical quotes, or Harm OCD obsessions
    private val benignContexts = listOf(
        "reading about",
        "prevent suicide",
        "suicide prevention",
        "intrusive thought about harm",
        "fear of harming",
        "i love my life but ocd tells me",
        "obsessed with the thought of"
    )

    fun evaluate(text: String): SafetyCheckResult {
        if (text.isBlank()) return SafetyCheckResult(isTriggered = false)

        val lower = text.lowercase().trim()

        // Check benign educational or therapeutic mention exclusions
        val hasBenignContext = benignContexts.any { lower.contains(it) }
        if (hasBenignContext && !explicitCrisisPhrases.any { lower.contains("i will $it") || lower.contains("going to $it") }) {
            return SafetyCheckResult(isTriggered = false)
        }

        // Check explicit crisis phrases
        val matchedExplicit = explicitCrisisPhrases.firstOrNull { lower.contains(it) }
        if (matchedExplicit != null) {
            return SafetyCheckResult(
                isTriggered = true,
                isImminentRisk = true,
                explanation = "Direct risk statement detected: '$matchedExplicit'"
            )
        }

        // Check indirect distress phrases
        val matchedIndirect = indirectDistressPhrases.firstOrNull { lower.contains(it) }
        if (matchedIndirect != null) {
            return SafetyCheckResult(
                isTriggered = true,
                isAmbiguousRisk = true,
                explanation = "Elevated emotional distress detected: '$matchedIndirect'"
            )
        }

        return SafetyCheckResult(isTriggered = false)
    }

    /**
     * Backward-compatible convenience function for input guards and unit tests.
     */
    fun containsSelfHarmLanguage(text: String): Boolean {
        return evaluate(text).isTriggered
    }
}
