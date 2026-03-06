--
#### THIS IS AN ARCHITECTURE OUTLINE GUIDE DERIVED FROM THE IMPLEMENTATION OF THE MINERVA_ANDROID_APP

## 1) What This Repository Is

`minerva-production-android` is a native Android app (Java) for mining operations workflows:

- Trip ticket dispatching and receiving
- Fuel inventory/tickets/deliveries/withdrawals
- Rental time/activity logging
- Weather logs
- Audit/error/log trails
- Device registration and app update checks

It is designed to work with local storage first (Room DB) and sync to a server through Retrofit APIs.

## 2) Tech Stack and Project Shape

- Language: Java (Android)
- UI: XML layouts + Activities + Dialogs + Adapters
- Persistence: Room (`database/entities`, `database/dao`, `repositories`)
- State layer: Android ViewModels (`viewmodels`)
- Networking: Retrofit + Gson (`api`, `api/routes`)
- Scanning/Device: ZXing, code-scanner, Nearby
- Crash reporting: Firebase Crashlytics

Core module layout:

- `app/src/main/java/app/bizshow/mining/views` -> screens, dialogs, UI modules
- `app/src/main/java/app/bizshow/mining/viewmodels` -> ViewModel wrappers and feature VMs
- `app/src/main/java/app/bizshow/mining/repositories` -> data access orchestration
- `app/src/main/java/app/bizshow/mining/database` -> Room DB, entities, DAOs, migrations
- `app/src/main/java/app/bizshow/mining/api` -> API contract
- `app/src/main/java/app/bizshow/mining/api/routes` -> feature-level API clients and sync logic
- `app/src/main/java/app/bizshow/mining/utils` -> shared helpers/constants/services

## 3) Fast Read Order (Recommended)

Use this exact order for fastest understanding.

1. `app/build.gradle`
2. `app/src/main/AndroidManifest.xml`
3. `app/src/main/java/app/bizshow/mining/MyApp.java`
4. `app/src/main/java/app/bizshow/mining/utils/Constants.java`
5. `app/src/main/java/app/bizshow/mining/database/BizshowMining.java`
6. `app/src/main/java/app/bizshow/mining/database/Migrations.java`
7. `app/src/main/java/app/bizshow/mining/viewmodels/ViewModels.java`
8. `app/src/main/java/app/bizshow/mining/views/activities/SplashScreen.java`
9. `app/src/main/java/app/bizshow/mining/views/activities/LoginForm.java`
10. `app/src/main/java/app/bizshow/mining/views/modules/Settings.java`
11. `app/src/main/java/app/bizshow/mining/api/EndPoints.java`
12. `app/src/main/java/app/bizshow/mining/api/routes/ServerDataDownloader.java`
13. `app/src/main/java/app/bizshow/mining/views/activities/TripTicketsMain.java`
14. `app/src/main/java/app/bizshow/mining/viewmodels/TripTicketDispatcherVM.java`
15. `app/src/main/java/app/bizshow/mining/repositories/TripTicketDispatcherRepo.java`
16. `app/src/main/java/app/bizshow/mining/database/dao/TripTicketDispatcherDao.java`
17. `app/src/main/java/app/bizshow/mining/database/entities/TripTicketDispatcher.java`
18. `app/src/main/java/app/bizshow/mining/api/routes/TripTickets.java`
19. `app/src/main/java/app/bizshow/mining/api/routes/Fuel.java`
20. `app/src/main/java/app/bizshow/mining/api/routes/RentalTickets.java`

Then sample one complete flow each for:

- Dispatcher: `views/activities/dispatcher/*`
- Receiver: `views/activities/receiver/*`
- Fuel: `views/activities/fuel/*`
- Rental: `views/activities/rental/*`

## 4) Runtime Flow (High-Level)

1. App launches into `SplashScreen`.
2. Required permissions are requested.
3. It checks local auth/user state and routes to login or role-specific main screen.
4. Login authenticates against backend (`ServerDataDownloader.auth()`), stores auth + server data.
5. Main screens read/write Room data through ViewModel/Repository.
6. Sync routes push unsynced local records and mark `sync_status`.

## 5) Data Architecture Pattern

Feature pattern (consistent across domains):

`Activity` -> `FeatureVM` -> `FeatureRepo` -> `FeatureDao` -> `Entity table`

Example:

- UI: `views/activities/dispatcher/TripTicketForm.java`
- VM: `viewmodels/TripTicketDispatcherVM.java`
- Repo: `repositories/TripTicketDispatcherRepo.java`
- DAO: `database/dao/TripTicketDispatcherDao.java`
- Entity: `database/entities/TripTicketDispatcher.java`

## 6) API and Sync Model

- API contract interface: `api/EndPoints.java`
- Route clients by domain in `api/routes/*`
- Server URL is built from app settings (`protocol://ip:port/api/`)
- Most sync calls:
  - Serialize local entity to map
  - Remove local-only fields
  - POST to endpoint
  - On success: set `sync_status = 1`
  - On failure: set `sync_status = 2` and store `sync_error`

## 7) Roles and Main Domains

Core roles:

- Dispatcher
- Receiver
- Fuel Tender

Core domains:

- Trip tickets (dispatcher/receiver)
- Fuel (tickets, delivery, withdrawal, refill, totalizers)
- Rental (activities, utilized/idle/down time)
- Weather and logs

Role-based UI visibility is handled in `TripTicketsMain` and related menu logic.

## 8) Key Utilities and Infra

- `CommonHelpers` -> shared UI/device/network helper methods
- `ErrorFilter` -> app-specific error capture
- `AppErrorLogs`, `AppAuditTrail`, `SendReport` -> telemetry/logging routes
- `WeatherJobService`, `AlarmService` -> scheduled/background behaviors
- `Settings` module -> hidden settings sheet and webserver config dialog

## 9) Important Operational Notes

- DB currently allows main-thread queries in Room.
- Several repos build SQL strings manually for flexible filtering.
- Network client currently trusts all SSL certificates (`CommonHelpers.createOkHttpClient()`).
- Some Android APIs used are legacy/deprecated (expected in older codebase generation).
- Tests are currently minimal (template unit/instrumentation tests only).

## 10) Suggested Onboarding Plan (First 2 Days)

Day 1:

1. Complete the Fast Read Order above up to item 13.
2. Run the app and trace startup/login with logs.
3. Inspect one end-to-end flow: create dispatcher trip ticket -> local save -> sync.

Day 2:

1. Read one fuel flow and one rental flow end-to-end.
2. Map each menu action in `TripTicketsMain` to its Activity.
3. Identify one refactor target (query safety, threading, or networking hardening).

## 11) Handy Navigation Pivots

When stuck, pivot from these files:

- App bootstrap: `MyApp.java`, `SplashScreen.java`, `LoginForm.java`
- Data core: `BizshowMining.java`, `ViewModels.java`
- Sync core: `EndPoints.java`, `ServerDataDownloader.java`
- Main navigation: `TripTicketsMain.java`
- Domain template: any `{Entity,Dao,Repo,VM,Form/List Activity}` set

## 12) Current Gaps / Risks to Keep in Mind

- Limited automated tests
- Tight coupling between Activities and many dependencies
- Custom/raw SQL construction can be brittle if not sanitized
- Security hardening needed for transport and secret handling

---

If you use only one section from this document, use **Section 3 (Fast Read Order)**.
