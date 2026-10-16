# Steady ERP Data-Integrity Follow-up — Design

**Date:** 2026-10-16  
**Status:** Approved by user on 2026-10-16  
**Base:** merged `main` after PR #4  
**Scope:** Focused follow-up to finish the highest-priority Phase 0 persistence guarantees. This document is design only; no application behavior changes are included in this commit.

## 1. Goal and success criteria

Make ERP session recording consistent when related records are written, and protect the distinct-practice-day count from partial updates or duplicate practice on the same local date.

Success means:
- A session and all associated ladder/goal progress updates commit together or none of those writes commit.
- A failure during any part of the write sequence cannot leave an ERP session saved while its related progress is stale, or vice versa.
- Repeated practice for the same goal on the same local calendar date counts as one distinct day.
- Early-ended sessions are retained as incomplete and are not accidentally treated as completed.
- Regression tests cover success, duplicate-day behavior, and rollback on failure.
- Existing user data and the current Room v3 migration chain remain intact.

## 2. Current context

The current repository's `SteadyRepository.recordSession` performs multiple sequential writes: insert the ERP session, update the linked ladder item, and potentially update the linked personal goal's practice log/count. Those writes are not currently wrapped in a single transaction. A failure between writes can leave persisted data inconsistent.

The Room v3 schema already includes `GoalPracticeLog` and `ActiveSessionState`; the practice log is intended to use a composite key for goal/date idempotency. The existing ERP state includes `wasCompleted`, and early-ending behavior must remain explicitly distinguishable from a completed exposure. The app uses Room, Kotlin coroutines, and an existing repository/ViewModel architecture.

## 3. Approaches considered

1. **Recommended — transaction at the repository/database boundary.** Make the database transaction available to the repository and perform session insertion plus related ladder and goal updates inside one `withTransaction` block. This keeps the business operation visible as one unit and allows focused repository tests.
2. **DAO transaction wrapper.** Put the entire operation behind a DAO transaction method. This can work, but would move orchestration/progression logic into the persistence layer and make the existing repository boundary less clear.
3. **Compensating writes.** Try to undo prior writes if a later operation fails. Rejected because rollback logic is harder to make reliable than a database transaction.

## 4. Recommended design

### Atomic ERP recording
- Use Room's transaction support for the complete `recordSession` operation.
- Include the ERP session insert, ladder-item session count/status update, and goal practice-log/count update in the same transaction.
- Preserve existing progression rules and clinical guardrails; do not redefine mastery criteria as part of this change.
- If the referenced ladder item or goal no longer exists, preserve current safe behavior without inventing records.
- Allow persistence exceptions to propagate to the existing caller/error-handling path so a failed write is not reported as success.

### Idempotent distinct practice days
- Keep the database uniqueness constraint for `(goalId, localDate)` as the source of truth.
- Ensure a duplicate practice log cannot increment the derived distinct-day count twice.
- Prefer deriving the count from persisted distinct-day rows or using the DAO's existing count query; avoid trusting a stale in-memory count.
- Keep local calendar-day semantics consistent with the current implementation and test around date boundaries where practical.

### Early-ended sessions
- Keep early-ended sessions in history with `wasCompleted = false`.
- Ensure early ending does not invoke completion-only behavior or present the session as completed.
- A normal completion remains governed by the existing completion helper and remaining-time rule.
- Do not require a reduction in SUDS for an ERP session to count as a meaningful practice.

## 5. Files and boundaries likely to change

- `app/src/main/java/com/example/data/repository/SteadyRepository.kt`: transactional session-recording orchestration and consistent goal-day count.
- `app/src/main/java/com/example/data/local/SteadyDatabase.kt` and/or the repository constructor wiring in `SteadyViewModel.kt`: expose transaction support without changing the database version unless schema inspection proves a migration is required.
- `app/src/main/java/com/example/data/local/SteadyDao.kt`: use or add the smallest query needed for distinct-day counting, respecting existing uniqueness constraints.
- `app/src/test/java/com/example/data/repository/`: focused repository regression tests using the project's established Room test strategy.
- `app/src/test/java/com/example/data/local/RoomV3MigrationContractTest.kt` and existing instrumentation migration fixture: retain and strengthen migration/data-preservation assertions only where needed.

Do not change unrelated UI styling, introduce new product features, or restructure the app.

## 6. Error handling and data safety

- A failed transaction must leave no partial session/progress updates.
- Do not delete, reset, or fabricate existing user records.
- Do not change the Room version or migration chain unless the implementation proves it necessary and the migration is separately tested.
- Never place patient-identifiable data, credentials, API keys, or signing secrets in tests or logs.
- Preserve the protected signing configuration, Secrets plugin configuration, and `applicationId = "com.aistudio.steady.wknzpx"`.

## 7. Test-first verification plan

Write focused regression tests before implementation:
1. Successful session recording persists the session and all expected linked progress changes.
2. A forced failure after the first write rolls back the session and every related update.
3. Two practice sessions for the same goal on one local date produce one distinct practice day; a session on the next local date produces a second.
4. A session ended early is persisted as incomplete and is not treated as a completed session.
5. The existing v1→v2→v3 migration preserves legacy settings, session history, and reminder data.

Run and inspect actual output for:
- `./gradlew testDebugUnitTest`
- `./gradlew lintDebug`
- `./gradlew assembleDebug`
- Android instrumentation/migration tests when a suitable emulator or device is available.

If the environment cannot run a check, report it as **NOT RUN** or **UNVERIFIED**, with the exact blocker. GitHub Actions status alone is not enough unless the relevant run and job results are inspected.

## 8. Explicitly out of scope

- Onboarding/demo-data redesign and crisis-language review (separate next phase).
- Persisting/restoring active ERP timer state after process death (separate follow-up).
- New AI behavior, clinical content, or exposure recommendations.
- UI redesign and broad architecture refactoring.

## 9. Review questions

Please review whether the proposed transaction boundary, duplicate-day semantics, and test scope match the intended behavior. Implementation planning should begin only after this written design is approved.
