package com.example.ui.screens

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ErpClinicalCopyTest {
    @Test
    fun onboardingExplainsLearningWithoutPromisingAnxietyReduction() {
        val copy = ErpClinicalCopy.onboardingExplanation

        assertTrue(copy.contains("uncertainty"))
        assertTrue(copy.contains("learning"))
        assertFalse(copy.contains("anxiety falls on its own"))
        assertFalse(copy.contains("anxiety will fall"))
    }

    @Test
    fun exposureInstructionsDoNotRequireDistressToDecreaseOrTimerToFinish() {
        val copy = ErpClinicalCopy.prepInstruction + " " + ErpClinicalCopy.activeReminder

        assertTrue(copy.contains("choose not to do the compulsion"))
        assertTrue(copy.contains("at your own pace"))
        assertFalse(copy.contains("stay until anxiety comes down"))
        assertFalse(copy.contains("Let anxiety peak and drop"))
    }

    @Test
    fun completionCopyRecognizesEffortWithoutAssumingAnxietyFell() {
        val copy = ErpClinicalCopy.completionSummary

        assertTrue(copy.contains("willingness"))
        assertTrue(copy.contains("learning"))
        assertFalse(copy.contains("Anxiety rose and fell on its own"))
    }

    @Test
    fun learningNotePromptDoesNotAssumeTheFearedOutcomeDidNotOccur() {
        val copy = ErpClinicalCopy.learningNotePlaceholder

        assertTrue(copy.contains("What did you notice"))
        assertTrue(copy.contains("even if distress stayed high"))
        assertFalse(copy.contains("nothing catastrophic occurred"))
    }

    @Test
    fun supportCopyDoesNotPromiseThatEveryDistressSpikeIsSafe() {
        val copy = ErpClinicalCopy.supportBanner + " " + ErpClinicalCopy.crisisGuidance

        assertTrue(copy.contains("immediate danger"))
        assertTrue(copy.contains("contact emergency services"))
        assertFalse(copy.contains("completely safe to pause"))
        assertFalse(copy.contains("every distress spike is harmless"))
    }
}
