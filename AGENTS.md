# Steady — Engineering and Clinical Guardrails

## Working agreement
- Work on `feat/phase-0-stabilize` only. Never push commits to `main`, merge a PR, or force-update a branch.
- Follow `docs/plans/phase-0.md` task by task. Use test-driven development: add a focused regression test first, observe the expected failure, then implement the smallest fix.
- Preserve existing user data. Never add a destructive Room migration or invent historical records.
- Keep changes incremental and reviewable. Avoid unrelated UI redesigns and broad refactors.
- Do not state that a build, test, lint, migration, or device check passed unless its actual output was inspected.
- Do not put real patient information, credentials, API keys, signing secrets, or private identifiers in source, fixtures, logs, or screenshots.
- Preserve the protected `signingConfigs`, `signingConfig` assignments, Secrets plugin configuration, and `applicationId = "com.aistudio.steady.wknzpx"` in `app/build.gradle.kts`.

## Clinical guardrails
- Steady is a self-help companion, not a diagnostic tool, emergency service, or replacement for a clinician.
- ERP progress is willingness to approach uncertainty and reduce ritual/safety behaviours; anxiety does not have to fall for a session to be useful.
- Never promise that a feared outcome will not occur, guarantee anxiety reduction, or give certainty-based reassurance.
- Do not add streak pressure, shame, coercive gamification, or a menu of compulsory calming/distraction rituals.
- Do not generate high-risk exposure assignments automatically. Exposure content involving harm themes requires clinician involvement.
- Keep crisis/help access visible and distinguish urgent risk from ambiguous distress. Do not replace crisis handling with reassurance.
- Treat AI output as a suggestion only: show it in an editable field and never auto-save it.
- Explain data handling accurately. Do not claim data is local-only unless every relevant path has been verified.
- Add `// TODO(clinician-review):` beside crisis wording/thresholds or clinical claims that need specialist review.
- Verify Indian crisis resource details against authoritative, current sources before release.

## Banned copy and claims
Do not use claims such as:
- “Nothing bad will happen.”
- “You are completely safe.”
- “Your fear is irrational, so ignore it.”
- “This will make your anxiety go away.”
- “You must finish the exercise.”
- “You failed” / “You broke your streak.”
- “AI diagnosis” or any implication that the app can diagnose OCD or determine risk with certainty.

Prefer neutral, autonomy-supportive language that acknowledges uncertainty and allows the person to stop or seek human support.

## Verification
Run and inspect output for:
- `./gradlew testDebugUnitTest`
- `./gradlew lintDebug`
- `./gradlew assembleDebug`
- Connected/instrumented tests when a suitable emulator/device is available.

If the wrapper, Gradle, Android SDK, emulator, dependencies, or required secrets are unavailable, report the exact blocker and mark affected checks **NOT RUN** or **UNVERIFIED**. Do not infer success from a workflow being created or a commit existing.