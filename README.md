# Mzansi Auto Repairs — Job Card and Client Management System

## 1. Project Overview

Mzansi Auto Repairs is an Android application developed to support workshop operations by organising customer information, job cards, vehicle-related information, and available automotive services.

The application provides a central interface for workshop staff to access key operational information, navigate between management screens, and manage their account preferences.

This project was developed as part of a Work Integrated Learning (WIL) project, with an emphasis on Android application development, database management, authentication, software testing, and DevOps practices.

## 2. Project Objectives

The main objectives of the project are to:

- Provide a user-friendly Android application for workshop management.
- Support customer registration and customer information management.
- Organise and display job card information and job statuses.
- Display available workshop services and their pricing.
- Provide user registration and login functionality.
- Offer account settings and user support options.
- Apply software testing and continuous integration practices.
- Maintain source code using Git and GitHub.

## 3. Key Features

### Authentication and Account Management

- User registration and login.
- Authentication and user lookup through the application's authentication repository.
- Google Sign-In integration work using Firebase and Google authentication libraries.
- Account profile information, including name, email, phone number, and role where available.
- Settings screen with a logout confirmation dialog.

*Note: Google Sign-In availability depends on the final configuration and successful testing of the authentication flow.*

### Dashboard

The dashboard provides an overview of workshop activity, including:

- Total customer count.
- Total vehicle count.
- Active job card count.
- Technician count.
- Quick links to Customers, Job Cards, Services, and Settings.
- A recent job cards section showing customer names, vehicle registration details, descriptions, and statuses.
- Navigation to individual job card details.

### Customer Management

The customer screen supports the management of customer information. The application's customer workflow includes entering customer details and saving customer records.

### Job Card Management

The job card functionality includes:

- Displaying job cards.
- Viewing customer and vehicle-related details.
- Displaying job descriptions and statuses.
- Navigating to individual job card details.
- Identifying active, completed, and cancelled job cards.

### Workshop Services

The services screen provides access to workshop service information and pricing.

### Settings and Preferences

The settings screen is designed to provide:

- User profile details.
- Dark-mode preference.
- Notification preference.
- Help and support information.
- About the application.
- Logout functionality.

Preference persistence and the effect of the dark-mode setting on the entire application should be verified during testing.

## 4. Technology Stack

| Technology | Purpose |
|---|---|
| Kotlin | Main application programming language |
| Android Studio | Android development environment |
| Jetpack Compose | Declarative user interface |
| Material 3 | Interface components and styling |
| Navigation Compose | Navigation between application screens |
| Room | Local database functionality |
| Firebase | Authentication and Google services integration |
| Google Sign-In libraries | Google account authentication integration |
| Kotlin Coroutines | Asynchronous operations |
| Gradle | Build automation and dependency management |
| Git | Version control |
| GitHub | Source code hosting and collaboration |
| GitHub Actions | Continuous integration build and test workflow |
| Azure DevOps | Project planning and work item tracking |

## 5. Application Configuration

The Android application uses the following configuration in the current project setup:

- **Application ID:** `com.mzansiautorepairs`
- **Minimum Android SDK:** 24
- **Compile SDK:** 34
- **Target SDK:** 34
- **Android Gradle Plugin:** 8.5.2
- **Kotlin:** 2.0.21
- **Jetpack Compose BOM:** 2024.06.00
- **Room:** 2.6.1
- **Firebase BOM:** 33.1.2

These values describe the project's current configuration and may change as dependencies are updated.

## 6. Getting Started

### Prerequisites

Before running the project, ensure that you have:

- Android Studio installed.
- A compatible JDK configured through Android Studio.
- Git installed.
- An Android emulator or compatible Android device.
- Access to the project's Firebase configuration if authentication features are required.

### Clone the Repository

Open a terminal and run:

```bash
git clone https://github.com/NtokozoNolu24/WIL_SEMESTER_2.git
```

Navigate to the project directory:

```bash
cd WIL_SEMESTER_2
```

If your local folder has a different name, open the cloned repository folder instead.

### Open the Project

1. Launch Android Studio.
2. Select **Open**.
3. Choose the cloned repository folder.
4. Allow Gradle to sync.
5. Resolve any SDK or dependency prompts before building.

### Configure Firebase

The project uses Firebase and Google services integration.

1. Open the Firebase Console.
2. Select the Firebase project associated with the application.
3. Register the Android application using the package name `com.mzansiautorepairs` if it is not already registered.
4. Download the appropriate `google-services.json` file.
5. Place the file in the Android application's `app/` directory.
6. Ensure that the Google services Gradle plugin is configured.
7. Enable the required sign-in providers in Firebase Authentication.
8. Configure the app's SHA-1 fingerprint and OAuth settings where required for Google Sign-In.
9. Sync Gradle and build the application.

**Security:** Do not publish service account credentials, private keys, passwords, or other secrets. Follow your team's repository and assessment requirements for handling Firebase configuration. The GitHub Actions workflow may require the repository secret `GOOGLE_SERVICES_JSON`, depending on how the workflow is configured.

### Build the Application

From the project root in PowerShell:

```powershell
.\gradlew.bat assembleDebug
```

To run the unit tests:

```powershell
.\gradlew.bat testDebugUnitTest
```

To run the available verification tasks:

```powershell
.\gradlew.bat check
```

The exact tasks available depend on the project's Gradle configuration.

After a successful debug build, the APK is normally generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Install and test the APK on an emulator or compatible Android device.

## 7. Project Structure

The project is organised into packages and files that separate navigation, screens, authentication, data access, and reusable interface components.

```text
WIL_SEMESTER_2/
├── .github/
│   └── workflows/
│       └── android-ci.yml
├── app/
│   ├── build.gradle.kts
│   ├── google-services.json
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           └── java/
│               └── com/mzansiautorepairs/
│                   ├── MainActivity.kt
│                   ├── navigation/
│                   ├── screens/
│                   ├── components/
│                   ├── auth/
│                   ├── data/
│                   └── ui/
├── gradle/
│   └── libs.versions.toml
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
├── gradlew.bat
└── README.md
```

*Note: This is a logical overview of the repository. Exact folder names and file locations may differ in the checked-out version.*

### Main Components

- **MainActivity:** Starts the application and applies the app theme.
- **AppNavHost:** Defines the navigation graph and application routes.
- **DashboardScreen:** Displays workshop statistics, quick links, and recent job cards.
- **CustomerScreen:** Provides the customer management interface.
- **JobCardScreen:** Displays job card information.
- **JobCardDetailScreen:** Displays details for a selected job card.
- **ServiceScreen:** Provides access to workshop services.
- **SettingsScreen:** Displays profile details, preferences, support information, and logout controls.
- **AuthRepository:** Handles authentication-related operations.
- **Room data layer:** Provides local database functionality.
- **MockData:** Initialises sample data used by the application's screens.

## 8. Navigation

The application defines routes for the following screens:

| Route | Purpose |
|---|---|
| `login` | User login |
| `register` | User registration |
| `google_register` | Google account registration flow |
| `dashboard` | Main workshop dashboard |
| `customers` | Customer management |
| `jobcards` | Job card listing |
| `jobcard/{jobId}` | Individual job card details |
| `services` | Workshop services |
| `settings` | User settings and preferences |

The `AppNavHost` composable connects these routes to their corresponding screens.

## 9. Testing

Testing is important to verify the correctness, stability, and usability of the application.

### Functional Testing

The following areas should be checked:

| Test Area | Expected Result |
|---|---|
| Application startup | The application opens without crashing |
| User registration | Valid registration details are processed correctly |
| Login | Valid credentials allow access to the application |
| Customer management | Customer records can be entered and saved |
| Job cards | Job cards display their details and statuses |
| Job card navigation | Selecting a job card opens its detail screen |
| Services | Available services and prices are displayed |
| Dashboard statistics | Counts correspond to the data available to the application |
| Settings navigation | Selecting Settings opens the settings screen |
| Profile details | Available user information is displayed |
| Preference persistence | Selected preferences remain saved after reopening the screen |
| Logout | Confirmation is displayed and logout returns the user to login |
| Google Sign-In | The configured Google authentication flow completes successfully |

Record actual results and any defects found during execution rather than marking untested cases as passed.

### Automated Testing

The project includes a GitHub Actions workflow for Android build and testing.

Run the local unit-test task using:

```powershell
.\gradlew.bat testDebugUnitTest
```

Successful compilation alone does not prove that every feature works correctly. Functional testing should also be performed on an emulator or physical Android device.

### Physical Device Testing

Where a compatible device is available:

1. Install the debug APK.
2. Launch the application.
3. Test login and registration.
4. Navigate through Customers, Job Cards, and Services.
5. Test Settings, preferences, and logout.
6. Record screenshots and any errors for the project report.

If physical-device testing is unavailable, use an Android emulator where possible and document the limitation accurately.

## 10. DevOps and Continuous Integration

The project uses Git and GitHub for source control and collaboration.

### GitHub Actions

The workflow is located at:

```text
.github/workflows/android-ci.yml
```

The workflow is intended to automate Android build and test checks when configured workflow events occur.

The project team should verify that:

- The workflow runs successfully.
- Gradle dependencies resolve correctly.
- The application builds without errors.
- Available automated tests execute.
- Required Firebase configuration is provided securely.
- Workflow failures are investigated and corrected.

### Branching and Commits

A feature branch should be used when making changes that need to be reviewed before merging.

Example commands:

```bash
git status
git checkout -b feature/settings-navigation
git add app/src/main/java
git commit -m "Add dashboard settings navigation"
git push -u origin feature/settings-navigation
```

Review the staged files before committing, and do not include generated files, local secrets, or unrelated changes.

Create a pull request when required by the team's workflow, review the changes, and merge only after the relevant checks pass.

### Azure DevOps

Azure DevOps can be used to track tasks, assign responsibilities, monitor progress, and maintain evidence of project work.

## 11. Team Collaboration

The project is a collaborative WIL project involving application development, backend and database work, authentication, and DevOps/testing.

Typical responsibilities include:

- **Mobile application development:** User interface, screen structure, navigation, and user experience.
- **Backend and database development:** Data management, authentication, and database functionality.
- **DevOps, testing, and deployment:** Source control, automated build checks, testing evidence, documentation, and deployment verification.

The actual allocation of responsibilities should follow the team's agreed task allocation.

## 12. Known Limitations and Future Improvements

Potential improvements include:

- Completing and verifying the Google Sign-In flow.
- Applying the saved dark-mode preference consistently across all screens.
- Connecting notification preferences to actual notification behaviour.
- Expanding automated unit and UI test coverage.
- Verifying database persistence and error handling.
- Improving form validation and user-facing error messages.
- Completing device or emulator testing.
- Generating a final release build after testing.
- Improving user documentation and deployment instructions.

These items should be marked complete only after implementation and verification.

## 13. Project Status

The project includes an Android application structure, navigation, workshop management screens, authentication-related code, a settings screen, and a GitHub Actions workflow.

Final readiness depends on successful builds, functional testing, verification of authentication and settings behaviour, and completion of the team's submission requirements.

## 14. Contributors

Add the names of all project team members here before submission.

-Thato Menetje— Mobile application / frontend
-Thobeka Sithole — Backend / database / authentication
-Ntokozo Mashiane — DevOps / testing / deployment

Update these descriptions to match the team's final allocation.

## 15. Repository

GitHub repository:

https://github.com/NtokozoNolu24/WIL_SEMESTER_2

---

**Mzansi Auto Repairs — Job Card and Client Management System**

Developed as part of a Work Integrated Learning project.
