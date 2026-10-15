# Steady OCD App 2.0 — Evidence-Informed Engineering Plan

A comprehensive engineering blueprint to upgrade Steady into an evidence-informed OCD self-help, ERP practice, patient safety, and clinical progress platform.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> The following decisions were clarified and confirmed in Phase 1:

- **Confirmed Decision 1 (First-Launch Data)**: Steady 2.0 starts with a clean, authentic onboarding experience with zero prefilled fictional patient profiles or artificial dummy data. An optional "Load Sample Practice Data" action is placed in Settings for demo/clinical review purposes.
- **Confirmed Decision 2 (Personal Goals & Values Module)**: Personal Goals & Values will feature a dedicated card on the Home dashboard for everyday values alignment, with full CRUD and tracking housed within the Track tab alongside ritual reduction and reclaimed time.
- **Confirmed Decision 3 (Reminders & Notifications)**: Configurable daily practice time with explicit weekday vs. weekend scheduling, non-stigmatizing lock-screen copy ("Time for your daily Steady practice"), and full respect for Android battery optimization and notification permissions.

---

## Baseline Audit Report

An initial audit of the existing codebase identified the following foundational strengths and specific upgrade targets:

1. **Architecture & Persistence**:
   - The app has established clean separation across Room, DAO, Repository, and ViewModel.
   - *Defect to Resolve*: `SteadyDatabase.kt` currently utilizes `.fallbackToDestructiveMigration()`. This poses a severe data loss risk if the schema increments. We will implement explicit Room migration strategies (`Migration(1, 2)`) to protect patient histories.
   - *Initial Data Defect*: Initial database callback pre-populates dummy patient data (`hasCompletedOnboarding = true, userName = "Alex"`), bypassing actual onboarding. We will make onboarding fresh and clean on initial run, preserving sample seeding as an explicit opt-in function.

2. **Safety & Crisis Interventions**:
   - `SafetyChecker.kt` currently relies on simple substring matching for crisis keywords.
   - *Defect to Resolve*: Keyword matching alone can trigger false positives on educational quotes and miss indirect risk indicators. We will implement a layered safety engine with contextual clarification, nonjudgmental emergency dialer prompts, and official verified Indian resources (Tele-MANAS `14416` and National Emergency `112`), with no alarming red banners.

3. **ERP Practice & Fear Ladder**:
   - Strong foundational models exist for `LadderItem` and `ErpSession`.
   - *Defect to Resolve*: Sessions currently terminate only on an arbitrary timer, and mastery suggestions need clearer guardrails. We will enhance the ERP flow to support flexible session durations, optional non-mandatory distress check-ins, expectancy violation calculations, and structured inhibitory learning notes (*"What actually happened?"*).

4. **Security & AI Integration**:
   - AI Coach in `GeminiService.kt` uses direct calls with `BuildConfig.GEMINI_API_KEY` and robust local template fallbacks.
   - *Security Hardening*: Ensure zero client-side key leakage, treat all model responses as untrusted, prevent repetitive reassurance seeking, and enforce strict token and timeout limits.

---

## 1. Overview & Core Concept

Steady 2.0 is an evidence-informed Android digital health companion designed to assist individuals with Obsessive-Compulsive Disorder (OCD) using Exposure and Response Prevention (ERP) and Acceptance and Commitment Therapy (ACT) principles.

- **Primary Mission**: Help users break the compulsive OCD loop by fostering tolerance for uncertainty without offering reassurance or promoting avoidance.
- **Clinical Boundaries**: Steady is not a diagnostic tool or therapy replacement. It acts as an adjunct self-help companion and clinical collaboration tool.
- **Offline-First Guarantee**: Core therapeutic tracking, Fear Ladder management, active exposure sessions, psychoeducation, and safety tools function 100% offline without mandatory account creation or cloud synchronization.

---

## 2. User Experience & Visual Design

```
┌────────────────────────────────────────────────────────┐
│                        STEADY 2.0                      │
│                                                        │
│  ┌──────────────────────────────────────────────────┐  │
│  │ Today's Intention: "Uncertainty is safe today."  │  │
│  └──────────────────────────────────────────────────┘  │
│                                                        │
│  ┌──────────────────────────────────────────────────┐  │
│  │ ⚡ I'M STRUGGLING RIGHT NOW                       │  │
│  │ Urgent urge surfing • 4-6 Breathing • Delay timer │  │
│  └──────────────────────────────────────────────────┘  │
│                                                        │
│  ┌─────────────────────────┐  ┌─────────────────────┐  │
│  │ NEXT ERP PRACTICE       │  │ PERSONAL VALUES     │  │
│  │ [SUDS 65] Door lock x1  │  │ Leaving home freely │  │
│  │ [Start Practice ➔]      │  │ Progress: 3 days    │  │
│  └─────────────────────────┘  └─────────────────────┘  │
│                                                        │
│  ┌──────────────────────────────────────────────────┐  │
│  │ 4-Tab Navigation: Home • Practice • Track • Learn │  │
│  └──────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────┘
```

### Visual Identity & Theme Tokens
- **Palette**: Calm sage green (`#7FA38A`, `#4F7A5E`), dusty blue (`#8FA9C4`), sand neutral (`#E9DFCC`), warm amber (`#E0A458` for alerts and high distress), and soft cream (`#FAF7F2` light) / slate charcoal (`#14181A` dark).
- **Rule of No Red**: Red is strictly avoided to prevent triggering false alarm responses in anxious patients.
- **Typography & Accessibility**: Humanist sans-serif with 48dp+ interactive touch targets, dynamic font scaling support, TalkBack semantic descriptions, and a toggleable "Reduced Motion" mode substituting breathing animations with clear rhythmic text cues.

---

## 3. Key Product Decisions & Trade-Offs

1. **Local Room Database with Explicit Migrations**:
   - *Choice*: Room database upgraded from version 1 to 2 with an explicit `Migration(1, 2)` script.
   - *Rationale*: Preserves existing user practice data while introducing `PersonalGoal` and structured reminder entities without resorting to destructive table drops.

2. **Values-Based ERP (Inhibitory Learning vs. Habituation)**:
   - *Choice*: Session success is measured by *tolerating uncertainty and resisting rituals*, not whether SUDS dropped to zero.
   - *Rationale*: Modern ERP neuroscience (Craske et al.) demonstrates that anxiety reduction is secondary to inhibitory learning—teaching the brain that distress is tolerable and catastrophic beliefs can be disconfirmed.

3. **Layered Safety & Crisis Architecture**:
   - *Choice*: Multi-tiered evaluation combining keyword heuristics, distress persistence ($\text{SUDS} \ge 90$ for 5+ min), and supportive ambiguity resolution, leading to a calm Tele-MANAS (14416) / Emergency (112) dialer and 5-4-3-2-1 grounding exercise.
   - *Rationale*: Ensures patients in severe distress receive immediate human crisis support without experiencing robotic or frightening lockouts.

---

## 4. Technical Architecture & System Layout

```
┌──────────────────────────────────────────────────────────────────┐
│                         PRESENTATION                             │
│  SteadyApp (BottomNav + Floating Struggle Pill + BackHandler)   │
│   ├── HomeDashboard2 ── (Daily intention, next step, goals)      │
│   ├── PracticeScreen ── (Ladder, In Vivo, Imaginal scripts)      │
│   ├── TrackScreen    ── (Rituals, Progress, Personal Goals)      │
│   ├── LearnScreen    ── (10 Psychoeducation modules + Bookmarks) │
│   └── SettingsScreen ── (Reminders, Theme, Data Export, Safety)  │
└─────────────────────────────────┬────────────────────────────────┘
                                  │ StateFlow & Events
┌─────────────────────────────────▼────────────────────────────────┐
│                         STEADY VIEWMODEL                         │
│  - State management (ERP Active, Struggle Flow, Navigation)      │
│  - Inhibitory learning calculations & SUDS distress arcs         │
│  - Reminder scheduling coordination & Notification permission    │
└──────────────────┬───────────────────────────────┬───────────────┘
                   │ Repository Calls              │ Offline Fallback
┌──────────────────▼─────────────┐   ┌─────────────▼───────────────┐
│       STEADY REPOSITORY        │   │        GEMINI SERVICE       │
│  - Single source of truth      │   │  - ERP writing prompts      │
│  - Safe coroutine dispatchers  │   │  - Anti-reassurance rules   │
└──────────────────┬─────────────┘   │  - Strict 120-word limit    │
                   │                 └─────────────────────────────┘
┌──────────────────▼───────────────────────────────────────────────┐
│                        DATA PERSISTENCE                          │
│  SteadyDatabase (Room v2 with Safe Migrations)                   │
│   ├── SettingsEntity        ├── RitualLog                        │
│   ├── LadderItem (v2)       ├── StruggleEvent                    │
│   ├── ErpSession (v2)       ├── PersonalGoal (NEW)               │
│   └── ImaginalScript        └── ReminderSchedule (NEW)           │
└──────────────────────────────────────────────────────────────────┘
```

---

## 5. Phased Implementation Roadmap

- **Phase 0 — Baseline & Checkpoint Verification**:
  - Verify current compilation state and test baseline.
  - Document all current classes and ensure zero unintended regressions.
- **Phase 1 — Critical Reliability & Security**:
  - Implement Room Database Migration v1 $\rightarrow$ v2; eliminate `fallbackToDestructiveMigration()`.
  - Add `PersonalGoal` and `ReminderSchedule` entities.
  - Fix initialization to ensure fresh onboarding for new users while retaining sample data loading in Settings.
  - Upgrade `SafetyChecker` to support layered heuristics, benign quote filtering, and unambiguous Tele-MANAS/112 pathways.
- **Phase 2 — Core ERP Experience Upgrade**:
  - Enhance Fear Ladder with goal associations and status tracking.
  - Implement revised 3-stage ERP session flow: Expectancy Prediction $\rightarrow$ Flexible Active Timer (with optional check-ins) $\rightarrow$ Inhibitory Learning Reflection.
  - Add resume capability for interrupted exposure sessions.
- **Phase 3 — Home, Tracking & Learning 2.0**:
  - Upgrade Home Dashboard with values intention, next ERP recommendation, and Goal summary card.
  - Expand Track tab with Personal Goals management and reclaimed time metrics.
  - Expand Learn tab with 10 structured psychoeducation modules, takeaway cards, and bookmarking.
  - Refine Clinician Report with goal progress and customizable date ranges.
- **Phase 4 — Reminders, AI Coach & Accessibility**:
  - Implement reliable local notification reminders for daily practice using `AlarmManager`/WorkManager APIs.
  - Enforce strict AI guardrails with client-side secret decoupling and local template fallback.
  - Validate TalkBack content descriptions, 48dp touch targets, and dynamic font scaling.
- **Phase 5 — Testing, QA & Documentation**:
  - Add comprehensive unit tests covering safety heuristics, SUDS bands, expectancy violation math, and migration scripts.
  - Run `gradle :app:testDebugUnitTest` and `compile_applet`.
  - Deliver final release documentation and user guide.
