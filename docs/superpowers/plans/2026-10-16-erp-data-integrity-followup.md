# ERP Data-Integrity Follow-up Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [x]`) syntax for tracking.

**Goal:** Make ERP session recording atomic, keep distinct goal practice-day counts idempotent, and preserve the incomplete state of early-ended sessions.

**Architecture:** Keep the existing Kotlin/Compose, Room, repository, and ViewModel boundaries. Wrap the repository's session insert and linked ladder/goal updates in one Room transaction, using persisted goal/date rows as the source of truth. Add focused regression tests before changing production code.

**Tech Stack:** Kotlin, Room 2.7, coroutines, JUnit, existing Android unit/instrumentation test setup.

**Spec:** `docs/superpowers/specs/2026-10-16-erp-data-integrity-design.md`

## Global Constraints

- Preserve existing user data; do not add destructive migrations.
- Do not change the Room version or migration chain unless implementation proves it necessary and the migration is separately tested.
- ERP progress means willingness, response prevention, and learning; anxiety does not need to fall for a session to be useful.
- Do not add reassurance, shame, streak pressure, coercive gamification, or compulsory calming techniques.
- Do not put real patient information, credentials, API keys, signing secrets, or private identifiers in source, fixtures, logs, or screenshots.
- Preserve `signingConfigs`, `signingConfig` assignments, Secrets plugin configuration, and `applicationId = "com.aistudio.steady.wknzpx"` in `app/build.gradle.kts`.
- Never claim a command passed unless its actual output was inspected.
- Work on a new feature branch based on merged `main`; do not push to `main`, merge a PR, or force-update a branch.

## Review Focus

- Failure after inserting an ERP session but before updating linked progress must roll back all writes.
- A goal practice log duplicate for the same local date must not inflate the distinct-day count.
- A session on a different local calendar date must count as a new distinct practice day.
- A missing/deleted ladder item or goal must not create phantom records or crash an otherwise valid unlinked session.
- Early-ended sessions must remain in history with `wasCompleted = false`, and must not be treated as completed.
- Existing v1→v2→v3 migration must preserve legacy settings, session history, and reminder data.

---

### Task 1: Add Room-backed repository regression tests

**Files:**
- Create: `app/src/test/java/com/example/data/repository/SteadyRepositoryTransactionTest.kt`
- Inspect: `app/src/main/java/com/example/data/local/SteadyDatabase.kt`
- Inspect: `app/src/main/java/com/example/data/local/SteadyDao.kt`
- Inspect: `app/src/main/java/com/example/data/model/Models.kt`

**Interfaces:**
- Tests use an in-memory Room `SteadyDatabase`, its `steadyDao()`, and `SteadyRepository`.
- Reuse the project's existing test dependencies and conventions; do not add a new test framework without need.

- [x] **Step 1: Write test for successful linked session recording.** Assert that a linked session is stored, the ladder item's session count/status and last SUDS follow existing rules, and the associated goal has one practice log for that date.
- [x] **Step 2: Write test for duplicate same-day practice.** Record two sessions linked to the same goal with timestamps on the same local calendar date; assert exactly one goal practice-log row and a distinct-day count of 1.
- [x] **Step 3: Write test for next-day practice.** Record a session on a second local calendar date; assert the distinct-day count becomes 2.
- [x] **Step 4: Write test for early-ended session preservation.** Record an `ErpSession(wasCompleted = false)`; assert it remains false in stored history and is not rewritten as completed.
- [x] **Step 5: Run the focused test before implementation.** Run: `./gradlew testDebugUnitTest --tests com.example.data.repository.SteadyRepositoryTransactionTest`. Expected: new transaction-related test coverage fails where it demonstrates current gaps; if the test infrastructure or command cannot run, record the exact blocker rather than guessing.
- [x] **Step 6: Commit tests.** Commit message: `test: cover ERP session persistence integrity`.

### Task 2: Make session and progress writes atomic

**Files:**
- Modify: `app/src/main/java/com/example/data/repository/SteadyRepository.kt`
- Modify: `app/src/main/java/com/example/ui/viewmodel/SteadyViewModel.kt` (repository construction only, if required)
- Modify: `app/src/test/java/com/example/data/repository/SteadyRepositoryTransactionTest.kt`

**Interfaces:**
- Keep public method signature: `suspend fun recordSession(session: ErpSession)`.
- Preferred implementation: inject `SteadyDatabase` into `SteadyRepository` and use `database.withTransaction { ... }`; keep DAO access through the database's existing `steadyDao()`. Update repository construction in the ViewModel and tests accordingly.
- Do not change the existing ladder mastery rule or create missing ladder/goal records.

- [x] **Step 1: Add a rollback regression test.** Use a test-only failing DAO/database strategy supported by the current test stack to trigger an exception after session insertion but before the linked progress updates finish. Assert the ERP session, ladder progress, and goal practice log/count all remain unchanged. If a reliable failure injection cannot be built with current Room test APIs, use a narrowly scoped injectable transaction operation or test seam and document why.
- [x] **Step 2: Run the rollback test and inspect its failure.** Use the focused Gradle test command from Task 1. Expected: current implementation leaves partial data or cannot satisfy the rollback assertion.
- [x] **Step 3: Implement the transaction.** Move session insert, linked ladder update, and goal practice recording/count update into one `database.withTransaction` block. Let exceptions propagate so callers cannot report success after a failed transaction.
- [x] **Step 4: Run the rollback and success tests.** Expected: all focused tests pass; rollback leaves no partial writes.
- [x] **Step 5: Commit atomic recording.** Commit message: `fix: record ERP session progress atomically`.

### Task 3: Verify goal practice-day count idempotence

**Files inspected/tested:**
- `app/src/main/java/com/example/data/repository/SteadyRepository.kt`
- `app/src/main/java/com/example/data/local/SteadyDao.kt`
- `app/src/test/java/com/example/data/repository/SteadyRepositoryTransactionTest.kt`

- [x] **Step 1: Assert duplicate log insertion is idempotent.** Same-day repository test verifies two sessions create two session rows but only one goal practice day.
- [x] **Step 2: Run focused regression coverage.** The initial CI run showed the intended rollback test failing before the transaction change; the same-day and next-day tests also ran in that suite.
- [x] **Step 3: Inspect existing count implementation.** The repository already inserts with conflict-ignore and sets `daysPracticedCount` from `getGoalPracticeDayCount(goalId)`. No separate count-algorithm change was necessary; the implementation was retained and now runs within the transaction.
- [x] **Step 4: Verify same-day and next-day behavior.** Passed in GitHub Actions run `38918097142`.
- [x] **Step 5: Record the scope decision.** No separate production change or separate commit was needed for count calculation because it already matched the approved design.

### Task 4: Verify early-end and migration preservation behavior

**Files:**
- Inspect: `app/src/main/java/com/example/ui/viewmodel/SteadyViewModel.kt`
- Inspect: `app/src/main/java/com/example/service/PhaseZeroIntegrity.kt` or the current source file containing `isErpSessionCompleted`
- Inspect/modify only if needed: `app/src/test/java/com/example/data/local/RoomV3MigrationContractTest.kt`
- Inspect/modify only if needed: `app/src/androidTest/java/com/example/data/local/SteadyMigrationTest.kt`
- Modify: `app/src/test/java/com/example/data/repository/SteadyRepositoryTransactionTest.kt` if a focused regression is missing.

- [x] **Step 1: Trace early-end call path.** Verify the UI's end-early callback persists `wasCompleted = false` and does not call the completion-only path. Add a targeted test if the existing behavior is not pinned by a test.
- [x] **Step 2: Run early-end test.** Expected: an early-ended record remains incomplete; normal completion still uses the existing helper/rule.
- [x] **Step 3: Inspect migration fixture assertions.** Confirm the existing chained migration test seeds and verifies a legacy reminder row and preserves settings/session fields. Add only missing assertions; do not change the migration schema solely to satisfy tests.
- [x] **Step 4: Run migration contract test.** Run: `./gradlew testDebugUnitTest --tests com.example.data.local.RoomV3MigrationContractTest`. Expected: PASS.
- [x] **Step 5: Check instrumentation migration test availability.** NOT RUN: GitHub Actions compiled instrumentation tests but does not provision or execute an Android emulator/device. The existing migration fixture was inspected; this limitation is documented in the PR.
- [x] **Step 6: Commit any necessary focused test correction.** Commit message: `test: verify ERP early-end and migration preservation`.

### Task 5: Full verification and branch handoff

**Files:**
- No additional product files unless a failure requires a focused correction.
- Review all commits and diffs on the feature branch.

- [x] **Step 1: Run unit tests.** Run: `./gradlew testDebugUnitTest`. Expected: PASS.
- [x] **Step 2: Run lint.** Run: `./gradlew lintDebug`. Expected: PASS with no new blocking issues.
- [x] **Step 3: Assemble debug APK.** Run: `./gradlew assembleDebug`. Expected: PASS.
- [x] **Step 4: Inspect Room schema/migration diff.** Expected: no schema change unless proven necessary; no destructive migration or lost legacy rows.
- [x] **Step 5: Review protected configuration.** Verify the signing configuration, Secrets plugin configuration, and application ID are unchanged.
- [x] **Step 6: Inspect CI evidence.** If local Android tooling is unavailable, inspect the exact GitHub Actions run and job logs for this branch/commit. Report each check individually as PASS, FAIL, NOT RUN, or UNVERIFIED; never infer a test passed from workflow existence alone.
- [x] **Step 7: Prepare PR, do not merge.** Open/update a PR from the feature branch to `main`, summarize behavior and evidence, and list any unverified checks. Leave merge decision to the user.

## Execution Notes

- The current repository has no Gradle wrapper confirmed in the root tree; verify actual available build tooling before attempting commands. If `./gradlew` is absent, do not fabricate results. Use CI only if it can run the required checks, and explicitly state any check that was not run.
- Do not combine the later onboarding/crisis-language review or active-session timer recovery into this persistence PR. Those are separate workstreams after this one is reviewed and merged.
