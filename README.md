# 🌤 TempSphere — Weather Forecast App

> A modern Android weather application built with Clean Architecture, MVVM, and Jetpack Compose.

---

## 📌 Project Overview

TempSphere is an Android application that provides real-time weather forecasts based on the user's current GPS location or a manually selected map location. Users can browse hourly and 5-day forecasts, manage favourite cities, set weather alerts, and switch between Arabic and English with full RTL support.

---

## 🏗 Architecture

The project follows **Clean Architecture** with a clear separation between layers:

- **Data Layer** — Remote API, local Room database, SharedPreferences
- **Presentation Layer** — MVVM with `ViewModel` + `StateFlow` + Jetpack Compose UI

```
com.darkzoom.tempsphere/
│
├── data/
│   ├── contract/               # Interfaces (Repository, Datasource)
│   ├── local/
│   │   ├── dao/                # Room DAOs
│   │   ├── datasource/         # Local datasource implementations
│   │   ├── db/                 # WeatherDatabase
│   │   └── model/              # Local entities & domain models
│   ├── remote/
│   │   ├── datasource/         # Retrofit datasource
│   │   ├── model/              # API response models
│   │   └── network/            # RetrofitClient, WeatherAPIService
│   └── repository/             # Repository implementations
│
├── ui/
│   ├── core/                   # App, MainActivity, Theme, BottomNavBar
│   ├── home/                   # Home screen + ViewModel
│   ├── places/                 # Places, PlaceDetail, MapPicker
│   ├── alert/                  # Weather alerts screen
│   ├── settings/               # Settings screen + ViewModel
│   ├── common/                 # Shared UI components
│   └── worker/                 # NotificationWorker (WorkManager)
│
└── utils/                      # LocationUtil, LocaleHelper, Mapping, AlertManager
```

---

## 🚀 Getting Started

### Prerequisites

| Tool | Version |
|------|---------|
| Android Studio | Hedgehog or later |
| JDK | 11 |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 35 (Android 15) |
| Kotlin | 2.1.0 |

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/TempSphere.git
   cd TempSphere
   ```

2. **Add your API keys** to `local.properties`
   ```properties
   WEATHER_API_KEY=your_openweathermap_api_key
   MAPS_API_KEY=your_google_maps_api_key
   ```

3. **Open in Android Studio** and let Gradle sync complete.

4. **Run the app**
   ```bash
   ./gradlew assembleDebug
   ```
   Or press **Run ▶** in Android Studio.

---

## ✨ Features

- 🌡 **Current Weather** — Temperature, feels-like, high/low, humidity, wind, pressure, cloudiness
- 🕐 **Hourly Forecast** — Next 10 hours with precipitation chance
- 📅 **7-Day Forecast** — Daily high/low with weather type icons
- 📍 **GPS & Map Location** — Auto-detect via GPS or pin a custom location on Google Maps
- ⭐ **Favourite Places** — Save cities, swipe to delete, pull to refresh
- 🔔 **Weather Alerts** — Schedule notifications or alarm-sound alerts with repeat options
- 🌙 **Time-based Themes** — Morning / Afternoon / Night backgrounds that change live
- 🌐 **Bilingual** — Full Arabic (RTL) and English support, switchable at runtime
- ⚖️ **Unit Conversion** — Celsius / Fahrenheit / Kelvin and m/s / km/h / mph
- 📶 **Offline Support** — Cached data served from Room when network is unavailable

---

## 🌍 API

| Property | Value |
|----------|-------|
| Provider | [OpenWeatherMap](https://openweathermap.org/api) |
| Current Weather | `GET /data/2.5/weather` |
| 5-Day Forecast | `GET /data/2.5/forecast` |
| Base URL | `https://api.openweathermap.org/data/2.5/` |
| Auth | API Key via query parameter `appid` |

---



## 📱 Screens

| Screen | Description |
|--------|-------------|
| **Home** | Current weather for GPS or pinned location. Hourly + daily forecast, pull-to-refresh |
| **Places** | Saved favourite cities with live weather cards. Swipe-to-delete |
| **Place Detail** | Full forecast detail for a saved city |
| **Map Picker** | Google Maps screen to pin a location or search by city name |
| **Alerts** | Create, toggle, and delete scheduled weather notifications or alarms |
| **Settings** | Language, temperature unit, wind unit, location mode, refresh rate |

---

## 🧪 Testing

Unit tests are located in `src/test/` and cover ViewModels and repository logic.

```bash
./gradlew test
```

Key tools: **JUnit 4**, **MockK**, **Turbine** (Flow testing), **kotlinx-coroutines-test**


---

## 👥 Team

| Name | Role |
|------|------|
| Hazem | Android Developer |

---

## 📋 Project Info

| Property | Value |
|----------|-------|
| Min SDK | 24 |
| Target SDK | 35 |
| Compile SDK | 35 |
| Kotlin | 2.1.0 |
| Architecture | Clean Architecture + MVVM |
| UI Toolkit | Jetpack Compose |



