# FitLife - Personal Workout Planner & Tracker

FitLife is a comprehensive Android application designed to help users organize their fitness journey. It combines workout creation, weekly scheduling, and location-based gym management into one seamless experience.

## 🚀 Features

### 🔐 User Authentication
*   **Secure Login & Sign-up:** persistent user accounts powered by Room Database.
*   **Input Validation:** Ensures data integrity during registration.

### 🏋️ Workout Routine Management
*   **Dynamic Exercise Builder:** Add, remove, and organize exercises with specific sets, reps, and notes.
*   **Equipment Checklist:** Smart "Quick Add" dialog for common gym gear.
*   **Visual Progress:** Option to attach images (Camera/Gallery) to each routine.
*   **SMS Delegation:** Share your routine and equipment checklist with workout partners via professionally formatted SMS.

### 📅 Weekly Planner
*   **Week at a Glance:** A visual horizontal week strip to navigate your schedule.
*   **Daily Assignments:** Link routines to specific days of the week.
*   **Motivational UI:** Today's date is auto-highlighted, showing you exactly what's on the menu for today.

### 📍 Google Maps Geotagging
*   **Gym Map:** Save your favorite gyms, parks, or home workout spots by long-pressing on a Google Map.
*   **Linked Locations:** Assign specific locations to your workout routines.
*   **One-Tap Navigation:** "Open in Maps" button provides instant turn-by-turn directions to your saved gym.

---

## 🛠️ Tech Stack
*   **Language:** Java
*   **Database:** Room Persistence Library (SQLite)
*   **UI Components:** Material Design 3 (Cards, Chips, Spinners, Expandable Lists)
*   **APIs:** Google Maps SDK for Android, Google Play Services Location
*   **Architecture:** Repository Pattern for clean data management

---

## ⚙️ Setup & Installation

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/YOUR_USERNAME/FitLife.git
    ```
2.  **Google Maps API Key:**
    *   This project requires a Google Maps API key to display maps.
    *   Create a file named `local.properties` in the root directory (if it doesn't exist).
    *   Add your key: `MAPS_API_KEY=your_api_key_here`
3.  **Build & Run:** Open the project in Android Studio, sync Gradle, and run on an emulator or physical device.

---

## 👨‍💻 Developed by
Developed as part of a mobile application development project focusing on CRUD operations, external API integration, and user-centric design.
