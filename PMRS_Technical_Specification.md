# Implementation Directive: Patient Medical Record System (PMRS)

**Document type:** Engineering build order for an autonomous coding agent
**Target output:** A compilable, runnable JavaFX desktop application
**Audience:** AI coding agent (no human clarification available mid-build — resolve ambiguity using the defaults stated here)

---

## 1. Mission Statement

Build a desktop **Patient Medical Record System (PMRS)** in Java using JavaFX and the MVC pattern. The system manages patients, physicians, appointments, and medical records for a small clinic. Deliver working, compiling code — not scaffolding, not TODO stubs, not partial features. Every feature listed in Section 5 must be functional end-to-end (UI → controller → model → in-memory store) before the build is considered complete.

**Hard constraint:** GUI only. No `Scanner`/console I/O anywhere in the runtime path. No CLI fallback mode.

---

## 2. Technology Stack

| Layer | Choice |
|---|---|
| Language | Java 17+ |
| UI Toolkit | JavaFX (OpenJFX) |
| Build tool | Maven (`pom.xml` with `javafx-maven-plugin`) |
| UI definition | FXML (one `.fxml` file per screen — no UI built purely in Java code) |
| Pattern | MVC, strictly separated (see Section 3) |
| Persistence | In-memory Java collections for v1 (see Section 7 for the seam that allows swapping in file/DB persistence later) |
| Testing | JUnit 5 for model/service layer unit tests |

Do not introduce Spring, Hibernate, or any DI framework — this is a self-contained desktop app. Do not use `javafx.scene.control.Alert.AlertType.NONE` dialogs as a substitute for real validation UI.

---

## 3. Project Structure (mandatory package layout)

```
pmrs/
├── pom.xml
├── src/main/java/com/pmrs/
│   ├── Main.java                      # Application entry point
│   ├── model/
│   │   ├── Person.java                # abstract base class
│   │   ├── Patient.java               # extends Person
│   │   ├── Physician.java             # extends Person
│   │   ├── Admin.java                 # extends Person
│   │   ├── MedicalRecord.java
│   │   ├── Appointment.java
│   │   ├── Prescription.java
│   │   ├── Address.java
│   │   └── enums/
│   │       ├── Gender.java
│   │       ├── BloodType.java
│   │       ├── AppointmentStatus.java
│   │       └── Role.java
│   ├── repository/                     # in-memory data access seam
│   │   ├── Repository.java             # generic interface: add, findById, findAll, update, delete
│   │   ├── InMemoryPatientRepository.java
│   │   ├── InMemoryPhysicianRepository.java
│   │   ├── InMemoryAppointmentRepository.java
│   │   └── InMemoryMedicalRecordRepository.java
│   ├── service/                        # business logic, independent of UI
│   │   ├── PatientService.java
│   │   ├── AppointmentService.java
│   │   ├── MedicalRecordService.java
│   │   └── ValidationService.java
│   ├── controller/                     # JavaFX FXML controllers only — no business logic here
│   │   ├── MainLayoutController.java
│   │   ├── DashboardController.java
│   │   ├── PatientRegistrationController.java
│   │   ├── PatientListController.java
│   │   ├── PatientDetailController.java
│   │   ├── MedicalRecordController.java
│   │   ├── AppointmentSchedulerController.java
│   │   └── LoginController.java
│   ├── exception/
│   │   ├── PMRSException.java          # base checked exception
│   │   ├── DuplicatePatientException.java
│   │   ├── RecordNotFoundException.java
│   │   └── ValidationException.java
│   └── util/
│       ├── IdGenerator.java
│       ├── DateUtil.java
│       └── SceneNavigator.java         # centralizes FXML loading / scene switching
├── src/main/resources/com/pmrs/view/
│   ├── login.fxml
│   ├── main-layout.fxml
│   ├── dashboard.fxml
│   ├── patient-registration.fxml
│   ├── patient-list.fxml
│   ├── patient-detail.fxml
│   ├── medical-record.fxml
│   └── appointment-scheduler.fxml
├── src/main/resources/com/pmrs/css/
│   └── application.css
└── src/test/java/com/pmrs/
    ├── service/
    │   ├── PatientServiceTest.java
    │   ├── AppointmentServiceTest.java
    │   └── ValidationServiceTest.java
    └── model/
        └── PatientTest.java
```

Controllers must never talk to repositories directly — always go through a `service` object. This is the boundary that keeps the UI layer thin and testable.

---

## 4. Domain Model (OOP requirements made concrete)

Vague instructions like "demonstrate polymorphism" are not actionable on their own. Implement the pillars exactly as follows:

- **Abstraction:** `Person` is an `abstract class` with an abstract method `String getRoleDescription()` and an abstract method `String getDashboardView()` (returns the FXML path each role should land on after login). `Repository<T>` is a generic interface implemented by each in-memory store.
- **Encapsulation:** All model fields are `private`. Expose only validated setters — e.g. `Patient.setDateOfBirth()` must reject future dates by throwing `ValidationException`, not silently accept bad input. No public mutable field ever escapes a getter (return defensive copies of lists/dates where applicable).
- **Inheritance:** `Patient`, `Physician`, and `Admin` extend `Person`. `Person` holds shared fields: `id`, `firstName`, `lastName`, `dateOfBirth`, `gender`, `contactNumber`, `address` (composed `Address` object, not a flat string).
- **Polymorphism:** `Person.getRoleDescription()` is overridden differently in each subclass. `PatientService.search(String query)` is overloaded with `search(String query, BloodType filter)` and `search(LocalDate dobFrom, LocalDate dobTo)`. Use polymorphic dispatch in `SceneNavigator` when routing a logged-in `Person` to `person.getDashboardView()` rather than an if/else chain on role type.

### Key entities

- **Patient**: extends `Person`; adds `bloodType`, `allergies (List<String>)`, `emergencyContact`, `List<MedicalRecord> records`, `List<Appointment> appointments`.
- **Physician**: extends `Person`; adds `specialization`, `licenseNumber`, `List<Appointment> schedule`.
- **Admin**: extends `Person`; adds `department`.
- **MedicalRecord**: `id`, `patientId`, `physicianId`, `visitDate`, `diagnosis`, `notes`, `List<Prescription>`.
- **Prescription**: `medicationName`, `dosage`, `frequency`, `durationDays`.
- **Appointment**: `id`, `patientId`, `physicianId`, `dateTime`, `status` (`SCHEDULED`, `COMPLETED`, `CANCELLED`, `NO_SHOW`), `reasonForVisit`.

Every ID is generated via `IdGenerator` (UUID or prefixed sequential — agent's choice, but must be collision-free and consistent, e.g. `PT-0001`, `AP-0001`).

---

## 5. Functional Requirements — build every one of these

1. **Login screen** — role selection (`Patient` view is out of scope for login; only `Physician`/`Admin` log in) with a hardcoded/seeded credential store (no real auth backend required, but the seam — a `Repository<Credentials>` — must exist so it's swappable).
2. **Dashboard** — role-specific landing view showing today's appointment count, total patients, and quick-action buttons.
3. **Patient registration** — form with client-side validation (required fields, valid date of birth, phone format, non-empty name). On submit: create `Patient`, persist via `PatientService`, show a success confirmation, clear the form.
4. **Patient list / search** — `TableView` of all patients, with a search bar wired to the overloaded `PatientService.search(...)` methods. Row double-click opens Patient Detail.
5. **Patient detail view** — read/edit patient demographic info; embedded sub-tabs or sections for Medical History and Appointments.
6. **Medical record entry** — physician-facing form to add a diagnosis/notes/prescription entry against a selected patient; appends to that patient's `records` list.
7. **Appointment scheduling** — create/cancel/reschedule appointments; must prevent double-booking a physician for the same time slot (this is a real validation rule, not decorative — reject with a clear error message on conflict).
8. **Global error handling** — uncaught exceptions in any controller action must be caught and shown to the user via a non-blocking, styled dialog (not a raw stack trace), and logged via `java.util.logging` to a rotating file, not just `System.out`.

Do not build features beyond this list for v1 (no billing, no lab integration, no reporting/export) — those are explicitly out of scope so the agent doesn't scope-creep into an unfinishable build.

---

## 6. Validation & Exception Handling Rules

- All exceptions thrown by the domain/service layer are **checked** and extend `PMRSException`. Controllers catch them at the boundary and translate to user-facing messages — never let a raw exception propagate to the JavaFX event thread unhandled.
- Validation happens in `ValidationService`, not scattered across controllers, so rules are unit-testable independent of the UI.
- Every `try` block that can fail must have a specific `catch` (no bare `catch (Exception e) {}` swallowing errors silently). Use `finally` only where a resource or UI state genuinely needs guaranteed cleanup (e.g. re-enabling a submit button).

---

## 7. Persistence Seam (for future extensibility — do not over-build now)

Implement `Repository<T>` as an interface with `InMemory*Repository` implementations backed by `HashMap<String, T>`. Do **not** wire in file or database persistence in v1 — but structure the code so a future `FileBackedPatientRepository` or `JdbcPatientRepository` could implement the same interface without touching service or controller code. This is the one piece of forward-looking design debt that's intentional; everything else in this spec should be built complete, not stubbed.

---

## 8. UI/UX Requirements

- Use `application.css` for a single consistent visual theme (clinical, clean — muted blues/greens/whites, generous whitespace, no default gray Swing-era look).
- Use a persistent left-nav or top-nav shell (`main-layout.fxml` with a `BorderPane`) that swaps center content between screens via `SceneNavigator`, rather than opening new windows per screen.
- Forms must show inline validation messages next to the offending field, not a single generic popup.
- No screen should be more than one click away from the dashboard.

---

## 9. Definition of Done

The build is complete only when all of the following are true:

1. `mvn clean javafx:run` builds and launches the app with zero errors.
2. Every workflow in Section 5 can be exercised manually start-to-finish through the GUI without exceptions surfacing to the user unhandled.
3. Double-booking an appointment is actually rejected — verified by a passing test in `AppointmentServiceTest`.
4. `mvn test` passes for all classes in Section 3's `src/test` tree.
5. Every public class and non-trivial method has a Javadoc comment describing purpose, params, and return value.
6. No `TODO`, `FIXME`, or empty method bodies remain anywhere in the shipped code.
7. Naming follows standard Java conventions throughout (PascalCase classes, camelCase methods/fields, ALL_CAPS constants).

---

## 10. Deliverables

- Full Maven project source tree per Section 3.
- A short `README.md` with build/run instructions (`mvn clean javafx:run`) and a two-paragraph architecture overview.
- No compiled binaries or `target/` directory committed.
