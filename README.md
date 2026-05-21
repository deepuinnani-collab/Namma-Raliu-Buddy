# Namma Railu Buddy 🚆

## Overview

Namma Railu Buddy is an Android application designed to help railway passengers with train tracking, live train status, route information, station details, and travel assistance in a simple and user-friendly interface. The app aims to improve the travel experience by providing real-time railway updates and essential travel features.

---

## Features

* 🚉 Live Train Status Tracking
* 🗺️ Railway Route Information
* 📍 Nearby Railway Stations
* 🔔 Train Arrival and Departure Updates
* 👤 User Login and Authentication
* ☁️ Firebase Integration
* 📱 Modern Android UI Design
* 🌐 Internet-based Real-time Updates
* 🧭 Maps Integration
* 🔍 Search Trains by Number or Name

---

## Technologies Used

### Frontend

* Android Studio
* XML Layouts
* Java / Kotlin

### Backend & Services

* Firebase Authentication
* Firebase Realtime Database / Firestore
* Google Maps API
* Railway API Integration

---

## Project Structure

```bash
Namma-Raliu-Buddy/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   ├── res/
│   │   │   └── AndroidManifest.xml
│   │
│   ├── build.gradle
│
├── gradle/
├── build.gradle
├── settings.gradle
└── README.md
```

---

## Installation

### Prerequisites

Make sure you have the following installed:

* Android Studio
* JDK 8 or higher
* Git
* Firebase Project Setup

---

## Steps to Run the Project

1. Clone the repository:

```bash
git clone https://github.com/deepuinnani-collab/Namma-Raliu-Buddy.git
```

2. Open the project in Android Studio.

3. Sync Gradle files.

4. Add your `google-services.json` file inside the `app/` folder.

5. Enable Firebase Authentication and Database.

6. Run the app on:

   * Android Emulator
   * Physical Android Device

---

## Firebase Setup

1. Go to Firebase Console.
2. Create a new Firebase project.
3. Add Android app package name.
4. Download `google-services.json`.
5. Place it in:

```bash
app/google-services.json
```

6. Enable:

   * Authentication
   * Realtime Database / Firestore
   * Analytics

---

## Permissions Required

Add the following permissions in `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

---

## Future Enhancements

* 🎫 Ticket Booking Support
* 🤖 AI-based Travel Assistant
* 🌍 Multi-language Support
* 📶 Offline Mode
* 🔔 Smart Notifications
* 📊 Train Delay Prediction

---

## Screenshots

Add your application screenshots here.

```bash
screenshots/home.png
screenshots/live_status.png
screenshots/maps.png
```

---

## Contributing

Contributions are welcome.

1. Fork the repository
2. Create a new branch
3. Commit your changes
4. Push to your branch
5. Create a Pull Request

---

## License

This project is licensed under the MIT License.

---

## Author

Developed by Deepali Innani and contributors.

GitHub Repository:

[https://github.com/deepuinnani-collab/Namma-Raliu-Buddy](https://github.com/deepuinnani-collab/Namma-Raliu-Buddy)

---

## Support

If you like this project, give it a ⭐ on GitHub.

