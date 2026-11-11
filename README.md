# Mzansi Auto Repairs: Job Card and Client Management System

**Module:** XISD6329 (WIL), Part 3
**Group name:** Tech Unit
**Team members:**

| Name | Student number | Primary role |
|---|---|---|
| Ntokozo Mashiane | ST10455454 | DevOps, Testing and Deployment |
| Thobeka Sithole | ST10456076 | Backend, Database and Authentication |
| Thato Menetje | ST10221273 | Mobile App, Frontend and API |

<!-- CONFIRM: use the official spelling of every name and the full student numbers from your student cards -->

**Repository:**[ https://github.com/NtokozoNolu24/WIL_SEMESTER_2.git ]
**Website prototype:** [link to the hosted website or prototype]
**Firebase project:** screenshots are provided in Section 8 (the console is private, so no link is given)

---

## Table of Contents
 
1. [Introduction and Background](#1-introduction-and-background)
2. [Purpose of the Application and Website](#2-purpose-of-the-application-and-website)
3. [Problem Statement](#3-problem-statement)
4. [Target Audience](#4-target-audience)
5. [Key Features](#5-key-features)
6. [Design Considerations](#6-design-considerations)
7. [Technology Stack and System Architecture](#7-technology-stack-and-system-architecture)
8. [Security, Authentication and Data Management](#8-security-authentication-and-data-management)
9. [Use of GitHub](#9-use-of-github)
10. [Use of GitHub Actions](#10-use-of-github-actions)
11. [Testing Approach](#11-testing-approach)
12. [Screenshots](#12-screenshots)
13. [Release Notes](#13-release-notes)
14. [Expected Impact](#14-expected-impact)
15. [Team Contributions](#15-team-contributions)
16. [AI Tool Usage Declaration](#16-ai-tool-usage-declaration)
17. [References](#17-references)

---

## 1. Introduction and Background

Mzansi Auto Repairs is a vehicle repair workshop based in Sandton, Johannesburg. Like many small and medium-sized workshops, it has relied on paper job cards, handwritten customer records and manual payment records to run its daily operations. As the number of customers and vehicles grows, this way of working becomes harder to sustain. Information is slow to retrieve, records can be misplaced or captured incorrectly, and management has no quick way to see what work is in progress, who is responsible for it, or how much has been paid.

This project responds to that situation by delivering a digital platform made up of two connected parts:

- **A mobile application (Android)** through which customers and authorised users can register, log in, register their vehicles, view their vehicles and bookings, request services, contact the workshop and submit reviews.
- **A website** that presents the business to the public (home, about, services, contact) and provides authentication and a dashboard from which workshop information can be accessed and managed.

The project was completed in three stages. In **Task 1** the team analysed the client's requirements, updated the project plan and produced the sitemap and wireframes for the website and the app. In **Task 2** the team built a working prototype of the Android application, backed by a cloud database and authentication service, and managed it through a GitHub repository. In **Task 3**, which this report covers, the prototype was refined, tested and documented, with an emphasis on a repeatable DevOps workflow using GitHub and GitHub Actions.

This README is written as the main technical and academic report for the project. It explains why the system exists, who it is for, how it was designed and built, how the team collaborated and automated its work, and what the expected impact is.

---

## 2. Purpose of the Application and Website

The purpose of the system is to **replace the workshop's paper-based processes with a centralised, secure and easy-to-use digital platform** that improves how customer, vehicle, booking and repair information is stored, found and used.

The **mobile application** exists to give customers and workshop users convenient access to the workshop wherever they are. A customer can register an account, add the details of their vehicles, browse the services on offer, submit a service booking request, view the status of their bookings, send an enquiry and leave a review. Putting these actions on a phone removes the need to visit or call the workshop for routine information.

The **website** serves two purposes. For the public, it is the business's online presence: it communicates who Mzansi Auto Repairs is, what services it provides (servicing, repairs, diagnostics and maintenance), how to make contact, and why customers can trust the business. For authenticated users, it is the entry point to the management system and dashboard, where records for customers, vehicles, job cards, technicians, services, payments and reports are organised.

Together, the application and the website aim to:

1. Digitise customer, vehicle and service records so that they can be stored once and retrieved quickly.
2. Make the workshop more accessible to customers by allowing bookings and enquiries to be made digitally.
3. Give management better visibility of work in progress, responsibilities and payments.
4. Reduce administrative errors through validation, structured forms and role-based access.
5. Improve customer trust and service quality by keeping an accurate service history for each vehicle.

---

## 3. Problem Statement

The client's existing process is largely manual. Through the requirements analysis in Task 1, the following problems were identified:

| Problem | Effect on the business |
|---|---|
| Customer, vehicle and job information is kept on paper | Records are slow to find and can be lost, damaged or captured incorrectly |
| No single place links a customer to their vehicles | Staff cannot easily tell who owns a vehicle or what has been done to it before |
| Service history is not searchable | Repeat repairs cannot be informed by past work, and customers receive less informed advice |
| Job progress is not visible | Management cannot see the status of a repair or which technician is responsible |
| Payments are recorded manually | Financial records are harder to reconcile and report on |
| Customers must call or visit to book or ask questions | More time is spent on routine communication and customers experience delays |

These problems are typical of organisations that have not yet adopted an information system. Laudon and Laudon (2021) explain that information systems support organisations by improving the accuracy, accessibility and timeliness of the information used in decision-making and daily operations. A digital job card and client management system addresses exactly this need.

---

## 4. Target Audience

The system serves several groups of users, each with different needs.

**Customers (primary users of the mobile app).** These are vehicle owners in and around Johannesburg who use the workshop for servicing and repairs. They typically want a fast, simple way to book a service, see what is happening with their vehicle and contact the business. They may not be technical, so the application must be clear, require few steps and use familiar language.

**Workshop administrators and managers.** These users are responsible for running the workshop. They need an overview of customers, vehicles, bookings, job cards, payments and reviews, and they need to manage that information accurately. They require secure access and role-based permissions so that sensitive data is only visible to authorised people.

**Technicians.** Technicians need to see the jobs assigned to them and record the work they perform. Their interface needs to be simple enough to use quickly during a busy working day.

**Prospective customers (public website visitors).** These visitors are not yet registered. They use the website to learn about the services offered, judge whether the business is trustworthy and find contact details.

Designing for these groups influenced many decisions, including the use of plain language labels, large touch targets in the mobile app, a limited number of steps per task and a clear separation between customer features and administrative features.

---

## 5. Key Features

### 5.1 User registration and login
Users create an account with a name, email address and password and then log in with their credentials. Authentication is handled by a managed cloud service, so passwords are never stored in plain text. Access to features depends on whether the user is a customer or an administrator.

### 5.2 Vehicle management
Users can add a vehicle by entering the registration number, make, model and year, and can view the list of vehicles linked to their account. Each vehicle is linked to its owner, which means service history can be recorded against the correct vehicle and found again when the vehicle returns.

### 5.3 Services page and booking requests
The Services page presents the workshop's offerings (servicing, repairs, diagnostics and maintenance) with short descriptions. A user can select a service and submit a booking request. The request is stored in the database and can be reviewed by the workshop.

### 5.4 My Bookings
Customers can view the bookings they have made and see the status information the system provides, so they do not need to phone the workshop to ask for updates.

### 5.5 Contact page
A validated contact form allows users to send enquiries. The workshop's address, phone number and email address are displayed alongside the form.

### 5.6 Reviews
Customers can submit a written review and a rating after a service. Reviews give the business feedback and help future customers make informed choices.

### 5.7 Dashboard
After logging in, users reach a dashboard that summarises key information, such as customers, vehicles, job cards and payments, and provides quick links to the main sections.

### 5.8 Administrator section
Authorised administrators can view and manage customer information, vehicle records, bookings, services and reviews. This section is restricted to administrator accounts.

### 5.9 Form validation
Forms check that required fields are completed, that email addresses are valid and that vehicle details are entered in the expected format. Clear error messages tell the user what to correct.

### 5.10 Features planned in the system design
The Task 1 sitemap also defined job card creation, technician assignment, service tracking, payment records and management reports. These form part of the full system design, and the status of each in the current prototype is listed in the release notes (Section 13).

<!-- CONFIRM: make sure every feature above actually exists in your app. Delete or move any that do not. -->

---

## 6. Design Considerations

### 6.1 User-centred and usability-focused design
The interface was designed around the tasks users need to complete rather than around decoration. The wireframes created in Task 1 focused on navigation, forms, buttons and information areas, because usability depends on users understanding what actions are available and what the system is doing (Nielsen, 1994). The team applied several of Nielsen's usability heuristics, in particular:

- **Visibility of system status:** confirmation messages appear after registration, bookings, enquiries and reviews.
- **Match between the system and the real world:** labels such as *Book a Service*, *My Vehicles* and *My Bookings* use the language customers already use.
- **Consistency and standards:** the same page and screen names are used in the sitemap, wireframes, report and prototype, so the structure the team planned is the structure the user experiences.
- **Error prevention and recovery:** validation prevents incorrect data from being submitted, and error messages explain how to fix problems.
- **Recognition rather than recall:** dropdown lists, labelled fields and a persistent navigation menu reduce what users must remember.

### 6.2 Navigation and information architecture
The sitemap separates the system into three areas: the **public website** (Home, About Us, Services, Contact Us), **authentication** (Register, Login) and the **management system** (Dashboard and its sections). This separation keeps public content simple while protecting management functions behind a login. Services are grouped under Vehicle Servicing, Repairs, Diagnostics and Maintenance, so visitors can quickly find the type of work they need.

### 6.3 Responsive and mobile-first thinking
Most customers will use the application on a phone, so layouts were designed for small screens first. The mobile wireframes use a top bar with a menu, stacked content blocks, full-width buttons and a bottom navigation bar for the most common destinations (Home, Cards and Profile). The website wireframes use the same structure and terminology so that users moving between the two platforms recognise the layout.

### 6.4 Visual design
The design uses a consistent logo placement, header, footer and typography across all screens. Footers carry contact details, links and copyright information. Colour and spacing choices were made to keep text readable and to make important actions, such as *Book a Service*, easy to find.

<!-- CONFIRM: add your actual colour palette, font choices and logo details here -->

### 6.5 Accessibility
Labels are written in plain language, buttons are large enough for touch input, and important information is not communicated by colour alone. These choices help users with different abilities and in different conditions, such as using a phone outdoors.

### 6.6 Data design
Data is organised around a small number of linked entities: **users**, **vehicles**, **bookings/job cards**, **services**, **reviews** and **payments**. A vehicle belongs to one customer, and a booking or job card refers to one vehicle and one service. Designing the relationships first made it possible to retrieve a vehicle's history without duplicating data.

### 6.7 Maintainability
Code is organised into separate screens, data-access classes and helper functions, so that individual team members could work on different parts without constantly editing the same files. This reduced merge conflicts in GitHub and made testing easier.

---

## 7. Technology Stack and System Architecture

| Layer | Technology |
|---|---|
| Mobile application | Android (Kotlin) [CONFIRM] |
| Website | HTML, CSS and JavaScript [CONFIRM] |
| Authentication | Firebase Authentication |
| Database | Firebase (Cloud Firestore / Realtime Database) [CONFIRM] |
| API / integration layer | [CONFIRM: REST API or Firebase SDK calls] |
| Version control | Git and GitHub |
| Automation (CI) | GitHub Actions |
| Project tracking | Azure DevOps |
| Design tools | FigJam (sitemap), Canva (wireframes) |
| Development tools | Android Studio, Visual Studio Code |

### Architecture overview

```
 ┌─────────────────┐        ┌─────────────────┐
 │  Android app    │        │  Website         │
 │  (customers /   │        │  (public pages + │
 │   admins)       │        │   dashboard)     │
 └────────┬────────┘        └────────┬─────────┘
          │      API / SDK calls     │
          └────────────┬─────────────┘
                       ▼
          ┌──────────────────────────┐
          │  Firebase                │
          │  • Authentication        │
          │  • Database              │
          └──────────────────────────┘

 Source code ──► GitHub repository ──► GitHub Actions (build + test)
```

The application and the website both communicate with the same cloud backend. This means a booking made on a phone is stored in the same database that the administrator views, so there is a **single source of truth**. Using a managed cloud backend also addressed a limitation identified in the project plan: the team had no dedicated server infrastructure, and cloud hosting offered a practical alternative (Sommerville, 2016).

---

## 8. Security, Authentication and Data Management

Because the system stores personal and business information, security was treated as a core requirement rather than an extra.

- **Authentication.** Users must log in with valid credentials. Authentication is delegated to Firebase Authentication, which handles secure password storage and hashing, so the team does not store raw passwords.
- **Role-based access.** Customers can only reach customer features. The administrator section is limited to authorised administrator accounts.
- **Input validation.** Required fields, email format and vehicle details are validated before data is submitted, which protects data quality and reduces the risk of malformed input.
- **Session management.** Users can log out, ending their session, which is especially important on shared devices.
- **Secrets management.** API keys and configuration files that must not be public are kept out of the repository (see Section 9.4).
- **Data integrity.** Each vehicle is linked to its owner, and records are retrieved by identifiers rather than by free-text matching, reducing the chance of showing one customer's information to another.

**Screenshot evidence for the Firebase setup** (replace with your own images):

![Firebase Authentication users](docs/images/firebase-auth.png)
*Figure 1: Registered users in Firebase Authentication.*

![Firebase database collections](docs/images/firebase-database.png)
*Figure 2: Database collections containing vehicles, bookings and reviews.*

---

## 9. Use of GitHub

GitHub was used as the central place for storing code, tracking change and coordinating the team. Git and GitHub are standard tools in a DevOps approach because they keep a complete history of the project and allow several developers to work in parallel.

### 9.1 Repository structure

```
WIL_SEMESTER_2/
├── .github/
│   └── workflows/
│       └── android-ci.yml      # GitHub Actions workflow
├── app/                         # Android application source
├── website/                     # Website source
├── docs/
│   ├── images/                  # Screenshots used in this README
│   └── user-guide.pdf
├── .gitignore
└── README.md
```

<!-- CONFIRM: adjust the folder names to match your real repository -->

### 9.2 Branching strategy
The team used a simple feature-branch workflow:

- `main` holds stable, working code and is the version that is submitted.
- Each member created a branch for their own area of work (for example, a branch for the frontend, one for authentication and one for the DevOps configuration).
- Work was merged into `main` once it had been reviewed and the automated build had passed.

Keeping `main` stable meant that the team always had a working version available for demonstrations and testing.

### 9.3 Commits and collaboration
All three members contributed through their own commits. Commit messages describe the change that was made (for example, "Add vehicle registration form validation"), which makes the history readable and helps trace a bug back to the change that introduced it. Pull requests were used to merge branches so that other team members could see and comment on changes before they reached `main`. Merge conflicts were resolved by discussing the changes with the team members involved.

### 9.4 .gitignore and secrets
A `.gitignore` file prevents build output, IDE settings and local configuration from being committed. Sensitive information, such as signing keys or private configuration values, was not committed to the repository. Where the build process needs a secret, it is supplied through **GitHub Secrets** rather than being written into the code.

### 9.5 Linking GitHub to project tracking
Work items and progress were also tracked on Azure DevOps. This allowed the project manager to follow tasks from planning through to completion, in line with the Plan, Develop, Build, Test, Release, Deploy, Operate and Monitor lifecycle that the team adopted in the Task 1 project plan (IBM, 2026; Microsoft, 2026).

**Screenshot evidence** (replace with your own images):

![Commit history](docs/images/github-commits.png)
*Figure 3: Commit history showing contributions from all team members.*

![Branches](docs/images/github-branches.png)
*Figure 4: Repository branches.*

![Pull requests](docs/images/github-pull-requests.png)
*Figure 5: Pull requests merged into main.*

---

## 10. Use of GitHub Actions

### 10.1 Why GitHub Actions was used
GitHub Actions is a continuous integration (CI) service built into GitHub. It runs a series of defined steps automatically whenever specific events occur, such as a push or a pull request. The team adopted it because manual building and testing is error-prone: a developer may forget to run the tests, or code may only work on one person's machine. Automating these steps means every change is built and tested in a clean environment, problems are discovered early, and the whole team can see the result.

This directly supports the DevOps lifecycle in the project plan. The **Build** and **Test** stages are carried out by the workflow on every change, which is the main advantage of continuous integration (Microsoft, 2026).

### 10.2 The workflow
The workflow is stored in `.github/workflows/android-ci.yml`. It is triggered when code is pushed to `main` or when a pull request is opened against `main`.

```yaml
name: Android CI

on:
  push:
    branches: [ "main" ]
  pull_request:
    branches: [ "main" ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - name: Check out the repository
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: gradle

      - name: Make Gradle wrapper executable
        run: chmod +x ./gradlew

      - name: Build the debug APK
        run: ./gradlew assembleDebug

      - name: Run unit tests
        run: ./gradlew testDebugUnitTest

      - name: Upload the APK as an artifact
        uses: actions/upload-artifact@v4
        with:
          name: mzansi-auto-repairs-debug-apk
          path: app/build/outputs/apk/debug/app-debug.apk
```

<!-- CONFIRM: replace this with the exact contents of your own workflow file -->

### 10.3 What each step does

| Step | Purpose |
|---|---|
| **Trigger (`on`)** | Starts the workflow automatically on every push to `main` and on every pull request, so no one has to remember to run it |
| **Check out the repository** | Copies the latest code onto the runner so it can be built |
| **Set up JDK** | Installs the Java version that the Android build requires, giving a consistent environment |
| **Make Gradle executable** | Allows the Gradle wrapper to run on the Linux runner |
| **Build the debug APK** | Compiles the application; if the code does not compile, the workflow fails |
| **Run unit tests** | Executes the automated tests; a failing test fails the workflow |
| **Upload the APK** | Saves the built application as a downloadable artifact so it can be installed on a physical device for testing |

### 10.4 How it improved the project
- **Early error detection.** Broken builds were caught immediately after a push rather than being discovered close to the submission date.
- **Consistency.** Every build ran on the same clean environment, which removed "it works on my machine" problems.
- **Quality gate for merging.** A pull request was only merged once the workflow showed a green tick.
- **Faster testing on real devices.** The uploaded APK could be downloaded and installed on a physical phone to verify the app outside the emulator.
- **Visibility.** The Actions tab gives every team member the same view of whether the project is healthy.
- **Reduced workload.** Routine build and test tasks were automated, leaving the team more time for development and debugging.

### 10.5 Problems encountered and resolved
During setup, early workflow runs failed. [Describe the real problems you met, for example: the Gradle wrapper was not executable on the Linux runner, the wrong Java version was selected, a dependency could not be resolved, or a test failed.] Each failure was investigated through the logs in the Actions tab, corrected, and committed, and the workflow was run again until it passed. Reading the logs and fixing the cause taught the team how the build process works and how to diagnose issues systematically.

<!-- CONFIRM: replace the bracketed text above with the real problems your group faced. This is the most valuable part of this section, so keep it honest and specific. -->

**Screenshot evidence** (replace with your own images):

![GitHub Actions tab](docs/images/actions-tab.png)
*Figure 6: The Actions tab showing workflow runs.*

![Successful workflow run](docs/images/actions-success.png)
*Figure 7: A successful run with the build and test steps passing.*

![Failed run that was fixed](docs/images/actions-failed.png)
*Figure 8: An earlier failed run, which was diagnosed and corrected.*

![Uploaded APK artifact](docs/images/actions-artifact.png)
*Figure 9: The APK artifact produced by the workflow.*

---

## 11. Testing Approach

Testing combined automated and manual methods.

**Automated testing.** Unit tests are executed by GitHub Actions on each push and pull request, as described in Section 10.

**Manual functional testing.** The team walked through each user journey on an emulator and on a physical device:

| Test | Expected result | Result |
|---|---|---|
| Register with valid details | Account is created and the user can log in | [Pass / Fail] |
| Register with an invalid email | Error message is shown and nothing is submitted | [Pass / Fail] |
| Log in with correct credentials | User reaches the dashboard | [Pass / Fail] |
| Log in with wrong password | Error message is shown | [Pass / Fail] |
| Add a vehicle | Vehicle appears in My Vehicles | [Pass / Fail] |
| Submit a booking request | Confirmation shown; booking appears in My Bookings | [Pass / Fail] |
| Send a contact enquiry | Confirmation message appears | [Pass / Fail] |
| Submit a review | Review is saved and a confirmation appears | [Pass / Fail] |
| Access admin section as a customer | Access is denied | [Pass / Fail] |
| Log out | Session ends and protected pages are no longer accessible | [Pass / Fail] |

**Usability and validation testing.** Navigation, labels, layout and error messages were reviewed against the wireframes and requirements, and defects were recorded in the team's testing and defect log.

**Testing on a physical device.** The application was installed on a physical Android phone to confirm that it behaves correctly outside the emulator.

<!-- CONFIRM: fill in the real results and delete any tests you did not carry out -->

---

## 12. Screenshots

### 12.1 Mobile application

| Screen | Image |
|---|---|
| Home | ![Home](docs/images/app-home.png) |
| Register | ![Register](docs/images/app-register.png) |
| Login | ![Login](docs/images/app-login.png) |
| Services | ![Services](docs/images/app-services.png) |
| Dashboard | ![Dashboard](docs/images/app-dashboard.png) |
| Add Vehicle | ![Add Vehicle](docs/images/app-add-vehicle.png) |
| My Vehicles | ![My Vehicles](docs/images/app-my-vehicles.png) |
| My Bookings | ![My Bookings](docs/images/app-my-bookings.png) |
| Contact | ![Contact](docs/images/app-contact.png) |
| Reviews | ![Reviews](docs/images/app-reviews.png) |

### 12.2 Website

| Page | Image |
|---|---|
| Home | ![Home](docs/images/web-home.png) |
| About Us | ![About](docs/images/web-about.png) |
| Services | ![Services](docs/images/web-services.png) |
| Contact Us | ![Contact](docs/images/web-contact.png) |
| Login / Register | ![Login](docs/images/web-login.png) |
| Dashboard | ![Dashboard](docs/images/web-dashboard.png) |

*Every screenshot should have a clear label and match the page or screen names used in the sitemap and wireframes.*

---

## 13. Release Notes

### Version 1.0.0: Prototype (Task 2)
- User registration and login with Firebase Authentication.
- Home, About, Services and Contact screens.
- Vehicle registration and viewing.
- Service booking requests.
- Basic dashboard and administrator section.

### Version 2.0.0: Final release (Task 3)

**New features**
- [Innovative feature 1: what it does and why it helps users]
- [Innovative feature 2]
- [Innovative feature 3]

**Improvements**
- [For example: improved form validation and clearer error messages]
- [For example: improved layout and navigation]
- GitHub Actions workflow added to build and test automatically.
- Documentation, README and user guide completed.

**Bug fixes**
- [List each real bug that was fixed]

**Known issues**
- [List anything that is still incomplete, so the release notes are honest]

<!-- CONFIRM: the markers asked for the release notes to explain which innovative features were added in this version. Make sure that is clearly stated here. -->

---

## 14. Expected Impact

**For the business**
- Records are stored in one place and found in seconds instead of minutes, which reduces the time staff spend on administration.
- Fewer lost or incorrectly captured paper records means fewer disputes and less rework.
- Clear visibility of bookings, job status and payments supports better decisions.
- A digital service history lets staff give more informed advice and builds customer trust.

**For customers**
- Bookings and enquiries can be made at any time without calling or visiting.
- Customers can see their vehicles and booking status in one place.
- Reviews give customers a voice and help the workshop improve.

**For the team**
- The project built practical experience in mobile development, cloud services, authentication, version control and CI/CD, skills that are directly relevant to professional software development.

Together, these outcomes support the client's main aim of reducing administrative errors, saving time, improving access to information and delivering more efficient service (Laudon and Laudon, 2021).

---

## 15. Team Contributions

| Member | Contribution |
|---|---|
| **Thato Menetje** | Designed and developed the Android user interface, screens and navigation; built input controls and user settings; developed and integrated the API; contributed to UI testing and documentation. |
| **Thobeka Sithole** | Designed the database tables and populated them with test data; implemented registration, login and password security; integrated the backend and API; tested authentication and data storage. |
| **Ntokozo Mashiane** | Created and managed the GitHub repository, branches and commits; maintained Azure DevOps; configured GitHub Actions; ran automated and device tests; contributed to documentation and the demonstration video. |

All members attended team meetings, committed to the repository, tested each other's work, resolved errors together and prepared for the final demonstration.

---

## 16. AI Tool Usage Declaration

In this project, AI tools were only utilized as support tools. Members of the group made every decision about planning, design, programming, testing, and documentation. The team also assessed all AI outputs prior to their use.

There were two uses for ChatGPT (OpenAI, 2026):
**Updating website functionality:** We utilized the code recommendations as a starting point after describing the features we wished to add or modify. The team examined the recommendations, modified them to work with our current code structure, incorporated them ourselves, and evaluated the outcomes in the browser.
**Resolving Firebase Problems:** When we encountered issues with our Firebase configuration, we used ChatGPT's explanations to identify potential causes and documented the issue along with the error messages. In our own project, we tested the recommended solutions and retained only those that resolved the issue. We verified the outcome by retesting data storage, login, and registration.

Our documentation, including this README and the report, was updated and improved with the aid of Claude (Anthropic, 2026) through claude.ai. Since we didn't get instructor feedback on the previous assignments, it helped with the writing's structure, clarity, and academic tone while pointing out areas that needed improvement. After comparing all of the information to our real application, repository, and project, the team added our own supporting documentation, including screenshots, workflow details, test results, and release notes, and corrected any details that did not match.

How the output was verified: AI recommendations were not approved without first being reviewed. We used GitHub Actions to verify that the application still built and passed its tests after modifications were made, ran and tested any code on an emulator and a physical device, and compared documentation against the project requirements and our own work. Ideas that weren't appropriate for our project were modified or excluded.

Responsibilities: The group members finished all of the final work. Although the AI tools helped, they did not take the place of our own knowledge, and each team member is able to describe the portion of the project that they contributed to. The reference list that follows contains citations for the tools.

---

## 17. References

Dennis, A., Wixom, B.H. and Tegarden, D., 2020. *Systems Analysis and Design: An Object-Oriented Approach with UML*. 6th ed. Hoboken: John Wiley & Sons.

IBM, 2026. *What are the phases of the DevOps lifecycle?* [online] IBM. Available at: IBM website [Accessed 1 September 2026].

Laudon, K.C. and Laudon, J.P., 2021. *Management Information Systems: Managing the Digital Firm*. 16th ed. Harlow: Pearson.

Microsoft, 2026. *Introduction to operating reliable systems with DevOps*. [online] Microsoft Learn. Available at: Microsoft Learn website [Accessed 1 September 2026].

Nielsen, J., 1994. *10 Usability Heuristics for User Interface Design*. [online] Nielsen Norman Group.

Pressman, R.S. and Maxim, B.R., 2020. *Software Engineering: A Practitioner's Approach*. 9th ed. New York: McGraw-Hill.

Sommerville, I., 2016. *Software Engineering*. 10th ed. Harlow: Pearson.

<!-- Add GitHub Docs for GitHub Actions if you cite it, for example: GitHub, 2026. Understanding GitHub Actions. [online] GitHub Docs. -->
