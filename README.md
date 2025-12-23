# FitLife - Mobile Workout Planner

This is an Android application developed as part of my mobile application development project. The goal of the app is to help users manage their daily fitness routines, schedule their training week, and save their favorite workout locations using Google Maps.

## Project Overview

FitLife provides a centralized hub for fitness tracking. The project focused on implementing a local database (Room), integrating external APIs (Google Maps), and creating a dynamic user interface using Material Design components.

## Core Functionalities

### 1. User Account Management
*   Local sign-up and login system.
*   Data is stored securely using Room Database.
*   The dashboard greets the user by their name after a successful login.

### 2. Workout Routine Builder
*   Users can create custom routines.
*   **Dynamic Inputs:** I implemented a way to add multiple exercises to one routine, where each exercise has its own fields for sets, reps, and notes.
*   **Image Support:** Users can attach photos to their routines using either the phone's camera or the gallery.
*   **Equipment Integration:** A quick-select dialog helps users list common equipment like dumbbells or yoga mats without typing every time.

### 3. Weekly Planner
*   A horizontal calendar view that allows users to assign specific routines to different days of the week.
*   Includes a progress summary on the dashboard to show how many days are planned.

### 4. Geotagging (Google Maps)
*   Users can open a map and save locations (gyms or parks) by long-pressing.
*   These saved locations are linked to workout routines.
*   **Navigation:** Added an "Open in Maps" feature that launches the official Google Maps app for turn-by-turn navigation to the saved spot.

### 5. Sharing via SMS
*   A custom in-app dialog that allows users to send their workout checklist to a friend via SMS without leaving the app.

## Technical Details
*   **Language:** Java
*   **Database:** SQLite via Room Persistence Library.
*   **API:** Google Maps SDK.
*   **Architecture:** Follows the Repository pattern to handle data operations cleanly.

## How to Run the App
1.  Open the project in Android Studio.
2.  **API Key Requirement:** Since this app uses Google Maps, you will need to add a valid API key. 
    *   Create a `local.properties` file in the root folder.
    *   Add the line: `MAPS_API_KEY=your_key_here`.
3.  Sync Gradle and run on an emulator or physical device.

## Key Challenges & Learning
During this project, I learned how to handle complex data relationships in Room (linking locations to routines) and how to manage runtime permissions for sensitive features like GPS and SMS. I also focused on improving user experience through custom card designs and a modular dashboard layout.
