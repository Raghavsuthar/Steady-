package com.example

import com.example.data.model.PersonalGoal
import com.example.data.model.RitualLog
import com.example.service.SafetyChecker
import com.example.ui.components.getSudsLabel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testSafetyChecker_detectsSelfHarmLanguage() {
        assertTrue(SafetyChecker.containsSelfHarmLanguage("I feel like I want to hurt myself"))
        assertTrue(SafetyChecker.containsSelfHarmLanguage("I think about suicide often"))
        assertTrue(SafetyChecker.containsSelfHarmLanguage("I want to die"))
        assertFalse(SafetyChecker.containsSelfHarmLanguage("I touched the door knob and feel high anxiety"))
        assertFalse(SafetyChecker.containsSelfHarmLanguage("Sitting with uncertainty"))
    }

    @Test
    fun testSafetyChecker_layeredRiskClassification() {
        val imminent = SafetyChecker.evaluate("I am thinking about suicide tonight")
        assertTrue(imminent.isTriggered)
        assertTrue(imminent.isImminentRisk)

        val indirect = SafetyChecker.evaluate("I can't go on living like this")
        assertTrue(indirect.isTriggered)
        assertTrue(indirect.isAmbiguousRisk)

        val benign = SafetyChecker.evaluate("I am reading about suicide prevention for mental health awareness")
        assertFalse(benign.isTriggered)
    }

    @Test
    fun testSudsBands() {
        assertEquals("Calm", getSudsLabel(10))
        assertEquals("Calm", getSudsLabel(20))
        assertEquals("Mild", getSudsLabel(35))
        assertEquals("Moderate", getSudsLabel(55))
        assertEquals("High", getSudsLabel(75))
        assertEquals("Intense", getSudsLabel(95))
    }

    @Test
    fun testExpectancyViolationReduction() {
        val initialBelief = 85
        val postBelief = 20
        val violationDelta = initialBelief - postBelief
        assertTrue("Expectancy belief should significantly decrease", violationDelta > 50)
    }

    @Test
    fun testPersonalGoalIncrement() {
        val goal = PersonalGoal(
            id = 1,
            title = "Leave house without double checking door",
            daysPracticedCount = 3
        )
        val updated = goal.copy(daysPracticedCount = goal.daysPracticedCount + 1)
        assertEquals(4, updated.daysPracticedCount)
    }

    @Test
    fun testRitualsResistedCounting() {
        val logs = listOf(
            RitualLog(description = "Check lock", trigger = "Leaving", durationMinutes = 5, outcome = "RESISTED"),
            RitualLog(description = "Wash hands", trigger = "Doorknob", durationMinutes = 10, outcome = "PERFORMED"),
            RitualLog(description = "Count tiles", trigger = "Anxiety", durationMinutes = 2, outcome = "DELAYED")
        )

        val resistedOrDelayed = logs.count { it.outcome == "RESISTED" || it.outcome == "DELAYED" }
        assertEquals(2, resistedOrDelayed)

        val performedMinutes = logs.filter { it.outcome == "PERFORMED" }.sumOf { it.durationMinutes }
        assertEquals(10, performedMinutes)
    }
}
