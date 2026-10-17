package com.example.ui.screens

import kotlin.math.min

data class BubbleTarget(
    val centerX: Float,
    val centerY: Float,
    val radius: Float
)

data class BubblePopGameState(val score: Int = 0) {
    val isComplete: Boolean
        get() = score >= ROUND_LENGTH

    fun currentTarget(width: Float, height: Float): BubbleTarget {
        val point = TARGET_POINTS[score.coerceIn(0, ROUND_LENGTH - 1)]
        return BubbleTarget(
            centerX = point.first * width,
            centerY = point.second * height,
            radius = min(width, height) * TARGET_RADIUS_FRACTION
        )
    }

    fun tap(x: Float, y: Float, width: Float, height: Float): BubblePopGameState {
        if (isComplete || width <= 0f || height <= 0f) return this
        val target = currentTarget(width, height)
        val dx = x - target.centerX
        val dy = y - target.centerY
        return if (dx * dx + dy * dy <= target.radius * target.radius) {
            copy(score = score + 1)
        } else {
            this
        }
    }

    fun restart(): BubblePopGameState = BubblePopGameState()

    companion object {
        const val ROUND_LENGTH = 10
        private const val TARGET_RADIUS_FRACTION = 0.085f
        private val TARGET_POINTS = listOf(
            0.20f to 0.25f,
            0.72f to 0.20f,
            0.48f to 0.55f,
            0.82f to 0.72f,
            0.18f to 0.78f,
            0.50f to 0.34f,
            0.32f to 0.62f,
            0.70f to 0.46f,
            0.55f to 0.82f,
            0.84f to 0.30f
        )
    }
}
