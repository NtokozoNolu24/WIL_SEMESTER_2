# Mzansi Auto Repairs

> A digital workshop management system (Android app + website) that replaces paper-based records for customers, vehicles, job cards, services and bookings.

**Live Website:** [Visit Mzansi Auto Repairs](https://ntokozonolu24.github.io/WIL_SEMESTER_2/MzansiAutoRepairs2345/)
**Repository:** https://github.com/NtokozoNolu24/WIL_SEMESTER_2

**Team:** Ntokozo Noluthando Mashiane (ST10455454) · Thobeka Amahle Sithole (ST10456076) · Thato Menetje (ST10221273)

---

## Table of Contents

1. [Purpose of the App](#1-purpose-of-the-app)
2. [Design Considerations](#2-design-considerations)
3. [Features](#3-features)
4. [Technologies Used](#4-technologies-used)
5. [Screenshots](#5-screenshots)
6. [Use of GitHub and GitHub Actions](#6-use-of-github-and-github-actions)
7. [Testing](#7-testing)
8. [Getting Started](#8-getting-started)
9. [Release Notes](#9-release-notes)
10. [Use of AI](#10-use-of-ai)

---

## 1. Purpose of the App

Mzansi Auto Repairs is a vehicle repair workshop in Sandton. Before this project, the workshop kept customer, vehicle and job records on paper, which made it hard to track jobs, find customer history and manage bookings.

This system solves that with two connected products that share one Firebase backend:

- **Android app (staff):** used by administrators, receptionists and technicians to manage customers, vehicles, job cards, services and staff.
- **Website (customers and admins):** lets customers view services, book, manage their vehicles, contact the workshop and leave reviews. Admins can manage the business from a web dashboard.

**Goals**
- Replace paper records with a single digital source of truth.
- Let customers book and track services online.
- Give the workshop clear visibility of active jobs, staff and revenue.
- Keep staff access secure through role-based login and admin approval.

---

## 2. Design Considerations

**Planning and design process**
- **FigJam:** website and app sitemap, project plan (scope, WBS, schedule, risks, DevOps lifecycle).
- **Canva:** wireframes for both the mobile app and the website.
- The Android app and the website were both built from these wireframes.

**User experience**
- Navigation follows the sitemap: Home → Services → About → Reviews & Ratings → Contact → Dashboard / Login.
- The website uses a consistent logo, a navy and gold colour scheme, and optimised high-quality images.
- The app supports **dark mode**, adjustable **font size**, **language** preferences and **notification reminders** for accessibility and personalisation.
- Input validation guides users, for example South African phone numbers must be 10 digits starting with 0.

**Security and access control**
- Firebase Authentication verifies email and password before granting access.
- Protected screens cannot be opened without a valid session.
- Staff accounts are **role-based** (Administrator, Receptionist, Technician) and new staff must be **approved by an admin** before they can sign in.

**Architecture**
- The app and website use the **same Firebase accounts and data**, so a booking, vehicle or review made on one appears on the other.
- The Android app keeps a **local Room database** for its data layer, and uses **SharedPreferences** to store user settings.

---

## 3. Features

**Android app**
- Staff registration and login (email/password and Google sign-in)
- Admin dashboard with business overview (customers, vehicles, active jobs, technicians)
- Customers and vehicles management (add, search, edit, delete)
- Job cards with Pending / In Progress / Completed status
- Workshop services with prices and estimated durations
- Staff approval screen (approve or reject new staff)
- Settings screen (dark mode, font size, language, reminders)

**Website**
- Home, Services, About, Contact pages
- Customer registration and login
- Customer dashboard: add vehicles, book services, track bookings, write reviews
- Live customer reviews and ratings from the database
- Contact enquiry form saved to the database
- Admin dashboard: users, vehicles, bookings, services, reviews and enquiries

---

## 4. Technologies Used

| Area | Technology |
|---|---|
| Android app | Kotlin, Gradle, Room (local database), SharedPreferences |
| Website | HTML, CSS, JavaScript |
| Backend | Firebase Authentication, Cloud Firestore |
| Version control | Git and GitHub |
| CI/CD | GitHub Actions |
| Hosting | GitHub Pages |
| Design | FigJam, Canva |

---

## 5. Screenshots

> Add your screenshots to a `docs/images/` folder in the repo and keep the file names below (or update the paths).

### Android App

| Staff Login | Staff Registration | Admin Dashboard |
|---|---|---|
| ![Staff login](docs/images/app-login.png) | ![Staff registration](docs/images/app-register.png) | ![Admin dashboard](docs/images/app-admin-dashboard.png) |

| Customers & Vehicles | Vehicle Management | Staff Approvals |
|---|---|---|
| ![Customers](docs/images/app-customers.png) | ![Vehicles](docs/images/app-vehicles.png) | ![Staff approvals](docs/images/app-staff-approval.png) |

| Services & Pricing | Job Cards |
|---|---|
| ![Services](docs/images/app-services.png) | ![Job cards](docs/images/app-job-cards.png) |

### Website

![Home page](docs/images/web-home.png)
*Home: introduces the workshop and shows live customer reviews.*

![Services page](docs/images/web-services.png)
*Services: repair and maintenance services with prices.*

![Customer dashboard](docs/images/web-customer-dashboard.png)
*Customer dashboard: vehicles, bookings and reviews.*

![Admin dashboard](docs/images/web-admin-dashboard.png)
*Admin dashboard: business analytics and management.*

### Firebase

![Firebase Authentication](docs/images/firebase-auth.png)
![Cloud Firestore](docs/images/firebase-firestore.png)

---

## 6. Use of GitHub and GitHub Actions

### GitHub
- All source code for the **app** and the **website** lives in one repository (`WIL_SEMESTER_2`).
- The website is in the `MzansiAutoRepairs2345/` folder and the Android project in `app/`.
- Team members committed their own work with clear commit messages (for example *"Connect website to app Firebase, add role-based login"*), and branches and pull requests were used to review changes before merging into `main`.
- **GitHub Pages** hosts the live website straight from the repository, so every push to `main` updates the live site.

### GitHub Actions
GitHub Actions automates the build and test process so problems are caught early.

- **Trigger:** runs on pushes and pull requests to the `main` branch.
- **What it does:** checks out the code, sets up the JDK and Android environment, builds the app with Gradle, and runs the unit tests.
- **Why it matters:** every change is built and tested automatically, which keeps `main` stable and supports a DevOps workflow.
- **Fix from Task 2 feedback:** the earlier workflow failed. We read the execution logs, identified the problems, corrected the workflow configuration, and it now runs successfully on `main`.

> Add a screenshot of a successful run: `![GitHub Actions success](docs/images/github-actions.png)`
> Workflow file location: `.github/workflows/<your-workflow-name>.yml`

---

## 7. Testing

- **Unit tests:** run for key logic and the Room database operations.
- **Instrumented tests:** run on a device to check the app behaves correctly on Android.
- **Physical device testing:** the app was installed and run on a real Android phone to test navigation, authentication and settings.
- **Automated testing:** unit tests run on every push through GitHub Actions.

---

## 8. Getting Started

### Website
```bash
git clone https://github.com/NtokozoNolu24/WIL_SEMESTER_2.git
```
Open the `MzansiAutoRepairs2345` folder and open `index.html` with a local server (for example VS Code Live Server). Or use the [live site](https://ntokozonolu24.github.io/WIL_SEMESTER_2/MzansiAutoRepairs2345/).

### Android App
1. Open the project in Android Studio.
2. Let Gradle sync.
3. Run on an emulator or a physical device.

> **Note:** The first administrator account must already exist in Firebase. New staff register in the app and wait for admin approval.

---

## 9. Release Notes

### Version 2.0 (Final): current release

This release lists everything added or changed since the Task 2 prototype.

#### Innovative features added in this version

| Innovative feature | Description |
|---|---|
| **Shared backend for app and website** | The Android app and the website now use the same Firebase accounts and data, so a booking, vehicle or review shows up on both platforms in real time. |
| **Role-based access with staff approval workflow** | Staff register as Administrator, Receptionist or Technician and cannot sign in until an admin approves them on the Staff Approvals screen. Admins can also link a technician account to an existing technician record. |
| **Personalised settings** | Dark mode, font size, language and notification reminders, saved with SharedPreferences so they persist after the app is closed. |
| **Customer self-service web portal** | Customers can register, add vehicles, book services, track bookings and leave reviews online. |
| **Live reviews and ratings** | The website home and reviews pages display customer reviews pulled live from the database. |
| **Admin web analytics dashboard** | Total users, vehicles, bookings, reviews, revenue and top services, with management tabs for each. |
| **Google sign-in** | Staff can sign in with their Google account in addition to email and password. |

#### New
- Firebase Authentication for registration, login and session handling
- Settings screen (dark mode, font size, language, reminders)
- Staff Approvals screen
- Full website: Home, Services, About, Reviews & Ratings, Contact, Login/Register, Customer Dashboard, Admin Dashboard
- Contact enquiry form saved to the database
- Website hosted on GitHub Pages
- South African phone number validation on registration

#### Improved
- **Login:** credentials are now verified against Firebase before access is granted, and protected screens require a valid session. This fixes the Task 2 issue where verification was unrelated to sign-in.
- **Website content:** rewritten copy checked for spelling and grammar, consistent wording across app and website, and optimised, higher-quality images.
- **Website structure:** pages reorganised to follow the sitemap.
- **Backend integration:** improved communication between the app and its services.

#### Fixed
- GitHub Actions workflow failure on `main`
- Missing settings functionality
- Insufficient unit, instrumented and physical device test evidence

#### Kept from the prototype
- Local Room database design
- Existing user interface (improved, not replaced)
- Database tests
- README documentation (updated)

### Version 1.0: Prototype (Task 2)
- Android prototype with local database and basic UI
- GitHub repository with an initial automated workflow
- HTML/CSS website prototype (Task 1)

---

## 10. Use of AI

Claude (Anthropic) and ChatGPT (OpenAI) were used to research concepts, clarify technical details, proofread documentation and get guidance when solving development problems. All AI suggestions were reviewed before use, and the team remained responsible for the development, testing and final decisions.

---

© Mzansi Auto Repairs · WIL Semester 2 project
