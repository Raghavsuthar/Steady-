# Phase 0 — Stabilize Steady

> **For agentic workers:** Follow this plan task by task using test-driven development. Do not merge the pull request.

**Goal:** Preserve patient data while correcting Phase 0 integrity bugs and introducing a tested Room v3 schema.

**Architecture:** Keep the existing Kotlin/Compose, AndroidViewModel/StateFlow, and Room architecture. Make incremental changes at current DAO/repository/ViewModel boundaries. Defer broad refactors. A behavior not safely implemented and verified must be reported as unverified, not called fixed.

**Tech Stack:** Kotlin, Jetpack Compose, Room 2.7, KSP, JUnit, Robolectric/instrumentation tests, Gradle.

**Spec:** User-provided Steady master prompt in this conversation, especially §§0–5, 7–8.

## Global constraints

- Work only on feat/phase-0-stabilize; never commit to main; do not merge the PR.
- Do not change signingConfigs, signingConfig assignments, Secrets plugin configuration, or applicationId = "com.aistudio.steady.wknzpx" in app/build.gradle.kts.
- Preserve existing patient data. No destructive Room migration.
- ERP success is willingness/response prevention and learning, not a required fall in SUDS.
- No reassurance, promises about anxiety reduction, shame, streaks, or new compulsory techniques.
- Do not claim a command passed unless it was run and its output inspected.
- Required verification commands: ./gradlew testDebugUnitTest, ./gradlew lintDebug, ./gradlew assembleDebug, plus connected tests if an emulator is available.
- Keep touched files below ~400 lines where practical; split responsibly rather than expanding scope.

## Review focus

1. Populated v2 databases survive v2→v3 with rows and values preserved.
2. v1→v2→v3 migration preserves existing values and legacy check-in text.
3. Multiple ERP sessions on one local calendar day count as one practice day.
4. High-distress prompts do not reopen every timer tick in one episode.
5. Early-ended sessions store wasCompleted=false; AI suggestions populate the editable field and are not auto-saved.
6. Timer restoration uses persisted timestamps, not assumptions that a coroutine survives process death.
7. Sample rows remain distinguishable; historic practice dates are never invented.

## Files to map before implementation

- AGENTS.md — working rules and clinical guardrails, under ~150 lines.
- docs/plans/phase-0.md — this reviewed plan.
- app/src/main/java/com/example/data/local/SteadyDatabase.kt — Room version, migrations, schema export.
- app/src/main/java/com/example/data/model/Models.kt — Room entities.
- app/src/main/java/com/example/data/local/SteadyDao.kt — queries and inserts.
- app/src/main/java/com/example/data/repository/SteadyRepository.kt — transactions and practice-day queries.
- app/src/main/java/com/example/ui/viewmodel/SteadyViewModel.kt — safety alert, completion and timers.
- app/src/main/java/com/example/ui/navigation/SteadyApp.kt and app/src/main/java/com/example/ui/screens/AiCoachDialog.kt — AI suggestion handoff if owned here.
- app/src/test/java/com/example/ExampleUnitTest.kt and ExampleRobolectricTest.kt — weak/obsolete tests.
- app/src/androidTest/java/com/example/ExampleInstrumentedTest.kt and new app/src/androidTest/java/com/example/data/local/SteadyMigrationTest.kt — migration coverage.
- app/schemas/ — generated Room schema JSON, with location matching KSP configuration.
- Gradle configuration files only if schema/test setup requires them; preserve protected app/build.gradle.kts settings.

Before coding, inspect the complete DAO, repository, ViewModel and session UI. Update paths if actual ownership differs.

## Task 1 — Repository guardrails

- [ ] Add AGENTS.md with working rules, clinical guardrails, banned-copy list, and verification commands from the user brief; keep under ~150 lines.
- [ ] Check protected Gradle lines remain unchanged.
- [ ] Commit: docs: add Steady engineering and clinical guardrails.
- Check: inspect the documentation and diff; no code test required for this documentation-only task.

## Task 2 — Capture the actual Room v2 schema

- [ ] Inspect plugin/KSP versions and configure Room schema export to app/schemas using the supported argument.
- [ ] Generate/commit the real v2 schema; do not fabricate it from Kotlin declarations.
- [ ] Create a populated v2 migration fixture.
- [ ] Commit: test: capture Room v2 schema and migration fixtures.
- Check: run the focused migration/build command if Gradle is available; otherwise record the missing tooling exactly.

## Task 3 — Test and implement Room v2→v3

- [ ] Write a failing migration test for a populated v2 database and run it to confirm the expected failure.
- [ ] Implement entity updates and SQL, register MIGRATION_2_3, and retain MIGRATION_1_2.
- [ ] Test v2→v3 and chained v1→v2→v3, asserting representative existing rows/values survive.
- [ ] Commit: feat(db): add data-preserving Room v3 migration.
- Check: migration tests and Room generated-schema validation.

### Intended SQL contract

The statements below define the intended v3 columns/tables. Before implementation, compare against the actual generated v2 schema; if a name/type differs, stop and amend this plan before writing migration code. Every added column must match a Kotlin property and its default/nullability.

Settings columns:
- appLockEnabled INTEGER NOT NULL DEFAULT 0
- appLockTimeoutMinutes INTEGER NOT NULL DEFAULT 5
- secureScreenEnabled INTEGER NOT NULL DEFAULT 0
- discreetMode INTEGER NOT NULL DEFAULT 0
- notificationsEnabled INTEGER NOT NULL DEFAULT 0
- reminderTimes TEXT NOT NULL DEFAULT ''
- quietHoursStart TEXT NOT NULL DEFAULT ''
- quietHoursEnd TEXT NOT NULL DEFAULT ''
- crisisRegion TEXT NOT NULL DEFAULT 'IN'
- themeMode TEXT NOT NULL DEFAULT 'SYSTEM'
- myWhy TEXT NOT NULL DEFAULT ''
- sampleDataActive INTEGER NOT NULL DEFAULT 0

Ladder columns:
- responsePreventionRule TEXT NOT NULL DEFAULT ''
- safetyBehaviorsToDrop TEXT NOT NULL DEFAULT '[]'
- avoidanceNotes TEXT NOT NULL DEFAULT ''
- themeTag TEXT NOT NULL DEFAULT 'OTHER'
- clinicianApproved INTEGER NOT NULL DEFAULT 0
- isSample INTEGER NOT NULL DEFAULT 0
- archivedAt INTEGER DEFAULT NULL

ERP session columns:
- willingnessStart INTEGER DEFAULT NULL
- willingnessEnd INTEGER DEFAULT NULL
- tolerabilityBeliefStart INTEGER DEFAULT NULL
- tolerabilityBeliefEnd INTEGER DEFAULT NULL
- ritualFree INTEGER DEFAULT NULL
- safetyBehaviorsUsed TEXT NOT NULL DEFAULT '[]'
- contextTags TEXT NOT NULL DEFAULT '[]'
- plannedSessionId INTEGER DEFAULT NULL
- uncertaintyTarget INTEGER NOT NULL DEFAULT 0
- isSample INTEGER NOT NULL DEFAULT 0

Reminder migration:
- Add daysOfWeekMask INTEGER NOT NULL DEFAULT 127 and lastScheduledAt INTEGER DEFAULT NULL.
- Preserve old weekday/weekend columns in v3 to avoid an unnecessary table rebuild.
- Update daysOfWeekMask from the legacy flags only after tests define weekday-only, all-days, and neither-flag semantics. Monday is bit 0 through Sunday bit 6. Both old flags true means all days.

Ritual log columns:
- categories TEXT NOT NULL DEFAULT '["OTHER"]'
- isMental INTEGER NOT NULL DEFAULT 0
- askedOthersTimes INTEGER NOT NULL DEFAULT 0
- isSample INTEGER NOT NULL DEFAULT 0

Struggle event columns:
- didDelay INTEGER NOT NULL DEFAULT 0
- finalChoice TEXT NOT NULL DEFAULT 'STILL_DECIDING'
- categories TEXT NOT NULL DEFAULT '[]'
- isSample INTEGER NOT NULL DEFAULT 0

Imaginal script columns:
- audioFileName TEXT DEFAULT NULL
- durationSeconds INTEGER DEFAULT NULL
- themeTag TEXT NOT NULL DEFAULT 'OTHER'
- clinicianApproved INTEGER NOT NULL DEFAULT 0
- Keep audioPath in v3 for non-destructive compatibility; do not delete stored files during migration.

New goal practice log:
- Create goal_practice_log(goalId INTEGER NOT NULL, localDate TEXT NOT NULL, createdAt INTEGER NOT NULL, PRIMARY KEY(goalId, localDate), FOREIGN KEY(goalId) REFERENCES personal_goals(id) ON DELETE CASCADE).
- Add an index on localDate.
- Keep the old counter for compatibility but stop treating it as authoritative. Never invent historical dates.

New imaginal_sessions:
- id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL
- scriptId INTEGER DEFAULT NULL
- startedAt INTEGER NOT NULL
- durationSeconds INTEGER NOT NULL DEFAULT 0
- loops INTEGER NOT NULL DEFAULT 1
- startSuds INTEGER DEFAULT NULL
- endSuds INTEGER DEFAULT NULL
- beliefStart INTEGER DEFAULT NULL
- beliefEnd INTEGER DEFAULT NULL
- note TEXT NOT NULL DEFAULT ''
- createdAt INTEGER NOT NULL
- Foreign key scriptId → imaginal_scripts(id) ON DELETE SET NULL; index startedAt.

New planned_sessions:
- id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL
- ladderItemId INTEGER DEFAULT NULL
- scheduledAt INTEGER NOT NULL
- status TEXT NOT NULL DEFAULT 'PLANNED'
- reminderLeadMinutes INTEGER DEFAULT NULL
- ifThenPlan TEXT NOT NULL DEFAULT ''
- createdAt INTEGER NOT NULL
- Foreign key ladderItemId → ladder_items(id) ON DELETE SET NULL; indexes scheduledAt and ladderItemId.

New checkins:
- id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL
- date TEXT NOT NULL
- instrumentId TEXT NOT NULL
- answersJson TEXT NOT NULL DEFAULT '{}'
- totalScore INTEGER DEFAULT NULL
- note TEXT NOT NULL DEFAULT ''
- createdAt INTEGER NOT NULL
- Index date.

New active_session_state:
- id INTEGER PRIMARY KEY NOT NULL DEFAULT 1
- sessionType TEXT NOT NULL
- stateJson TEXT NOT NULL
- updatedAt INTEGER NOT NULL.

Migration constraints:
- Compare current entities/schema before adding createdAt to any existing table; do not add duplicate timestamp fields. New tables above include createdAt.
- Keep legacy checkInsJson intact in Phase 0. Any minute:suds → JSON conversion requires a separately tested parser that preserves malformed legacy content instead of silently dropping it.
- Do not add a foreign key to existing erp_sessions.ladderItemId via a simple ALTER TABLE; SQLite requires a tested table rebuild for that.
- SQLite DROP COLUMN is not portable across all supported devices. Keep deprecated reminder columns and audioPath in v3.
- Do not infer old practice dates from daysPracticedCount; historic dates are unknowable.
- Room's generated schema must match entities exactly before proceeding.

## Task 4 — Repository integrity and practice-day counting

- [ ] Write failing tests for atomic session recording and distinct local practice days; run them and verify the expected failures.
- [ ] Wrap multi-write session recording in a Room transaction.
- [ ] Derive practice-day counts from unique local dates where reliable timestamps exist; remove fabricated fallback counts.
- [ ] Commit: fix(data): record ERP sessions atomically and count unique practice days.
- Check: repository tests for multiple sessions on one day and the empty state.

## Task 5 — Completion, suggestion handoff, safety-alert latch

- [ ] Add failing tests for early completion, AI suggestion handoff, and repeated high-distress prompts; run them to verify the target failures.
- [ ] Store wasCompleted=false when a session ends early; return selected AI text to the editable field without auto-saving.
- [ ] Latch high-distress prompts once per episode and test reset/cooldown behavior.
- [ ] Preserve distinct urgent and ambiguous safety outcomes where supported. Never substitute reassurance for crisis handling.
- [ ] Commit: fix(erp): correct early completion and safety prompt behavior.
- Check: focused tests plus the existing unit suite.

## Task 6 — Timer persistence

- [ ] Write failing tests for pure timer-state calculations with an injected clock, including background and restore cases.
- [ ] Persist active-session state in Room or an existing suitable state holder; derive remaining time from timestamps, not a decrementing coroutine counter.
- [ ] Keep pause/end controls available and respect early exit.
- [ ] Commit: fix(session): restore active timers from persisted timestamps.
- Check: unit tests; true process-death/device verification requires a device or emulator.

## Task 7 — Regression suite

- [ ] Replace the tautological testExpectancyViolationReduction assertion with real behavior tests.
- [ ] Cover v2→v3 and v1→v2→v3 migration with seeded rows and value assertions.
- [ ] Cover transactions, unique practice days, alert latching, early completion, and timer restoration.
- [ ] Commit: test: cover Steady Phase 0 integrity and safety regressions.
- Check: ./gradlew testDebugUnitTest and migration/instrumented tests where Android tooling is available.

## Task 8 — Review, verification and PR

- [ ] Review the full diff against clinical guardrails and banned copy; verify protected Gradle lines.
- [ ] Run and capture output for ./gradlew testDebugUnitTest, ./gradlew lintDebug, ./gradlew assembleDebug, and connected tests if an emulator/device is available.
- [ ] If Gradle/SDK is unavailable, say so and list exactly what remains unverified. Use GitHub Actions only if an appropriate workflow can be added and its actual result observed.
- [ ] Open a PR to main with summary, files changed, tests, actual verification output, unverified items, and needs-clinician-review items.
- [ ] Do not merge.

## Clinical review flags

- Add // TODO(clinician-review): for crisis-classifier wording/thresholds that cannot be verified against an approved source.
- Verify Indian crisis resource numbers before release.
- Do not introduce self-directed harm-theme exposure examples.
- Never tell a user that a feared outcome will not happen or that anxiety will necessarily fall.

## Stop condition

After the Phase 0 PR is ready, report changed files, actual test/build output, unverified items, deliberate deferrals, and clinician-review items. Stop and wait for the user's explicit **continue** before Phase 1.
