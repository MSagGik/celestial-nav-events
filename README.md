# 🌌 CelestialNavigationEvents (celestial-nav-events)

**CelestialNavigationEvents** is a lightweight library written in Kotlin for calculating common Sun and Moon events — sunrise, sunset, twilight phases, moonrise, moonset, lunar phases (in days), illumination, and more. It works offline worldwide using only geographic coordinates and time in the Java standard `ZonedDateTime` format.

[![](https://jitpack.io/v/MSagGik/celestial-nav-events.svg)](https://jitpack.io/#MSagGik/celestial-nav-events)
[![](https://img.shields.io/github/license/MSagGik/celestial-nav-events)](LICENSE)
![Top Lang](https://img.shields.io/github/languages/top/MSagGik/celestial-nav-events)
![Kotlin](https://img.shields.io/badge/kotlin-1.9.24-blue)
![Beta](https://img.shields.io/badge/status-beta-blue)
---

## 📖 Overview

CelestialNavigationEvents solves everyday needs for applications that react to or display daily astronomical events. Typical scenarios include:

- 🔆 Dynamic day/night backgrounds in apps
- 📸 Photography planning (magic hour / blue hour)
- 🌑 Night mode scheduling
- 🌊 Tide and weather forecasting tools
- 🧭 Hiking/navigation planners
- 🗓 Calendar integrations
- 🌓 Enhancing user experience with light/dark theme transitions

All calculations run locally; no internet connection is required.

---

## ✨ Key Features

### 🌞 Solar Events

- Sunrise & sunset
- Civil, nautical & astronomical twilight intervals
- Magic hour & blue hour
- Duration of stay above/below horizon
- Meridian & anti-meridian crossings
- Next upcoming solar event
- Polar day / night support (high-latitude handling)

### 🌕 Lunar Events

- Moonrise & moonset
- Lunar phase (in days) & illumination percentage
- Meridian & anti-meridian crossings
- Daylight / nighttime durations
- Next upcoming lunar event
- Polar day / night support (high-latitude handling)



### ⚙️ Technical Highlights

- Offline, worldwide support (no external data sources)
- Uses standard `java.time.ZonedDateTime` for accurate timezone handling
- Simple Kotlin-style API (DSL-like usage)
- Unit-tested
- Modular design: solar and lunar calculators are separate components

---

## 📦 Installation

### Using JitPack

1. In your project `settings.gradle` (or `settings.gradle.kts`):

   ```kotlin
   dependencyResolutionManagement {
       repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
       repositories {
           mavenCentral()
           maven { url = uri("https://jitpack.io") }
       }
   }
   ```

2. In your module `build.gradle` (or `build.gradle.kts`):

   ```kotlin
   dependencies {
       implementation("com.github.MSagGik:celestial-nav-events:1.0.0-beta.1")
   }
   ```

---

## ✨ Quick Start

```kotlin
fun main() {
    val celestial = CelestialNavigationEvents().provide()

    val solarEvents = celestial.solar().calculateSolarEventDay(
        latitude = 12.345,
        longitude = 12.345,
        dateTime = java.time.ZonedDateTime.now()
    )
    println("Sunrise & Sunset: ${solarEvents.events}")

    val lunarEvents = celestial.lunar().calculateLunarEventDay(
        latitude = 12.345,
        longitude = 12.345,
        dateTime = java.time.ZonedDateTime.now()
    )
    println("Moonrise & Moonset: ${lunarEvents.events}")
    println("Lunar illumination: ${lunarEvents.illuminationPercent}%")
}
```

---

## 🔭 API Overview

### Entry Point

```kotlin
val celestial: CelestialEventsCalculator = CelestialNavigationEvents().provide()
```

### Solar Events

- `calculateSolarEventDay(...)`
- `findUpcomingSolarAbsoluteEventDay(...)`
- `findUpcomingSolarRelativeEventDay(...)`
- `findUpcomingSolarRelativeShortEventDay(...)`
- `calculateMagicHourPeriod(...)`
- `calculateBlueHourPeriod(...)`
- `calculateCivilTwilightPeriod(...)`
- `calculateNauticalTwilightPeriod(...)`
- `calculateAstronomicalTwilightPeriod(...)`

### Lunar Events

- `calculateLunarEventDay(...)`
- `findUpcomingLunarAbsoluteEventDay(...)`
- `findUpcomingLunarRelativeEventDay(...)`
- `findUpcomingLunarRelativeShortEventDay(...)`

---

## 🧐 Use Cases

- Dynamic UI Backgrounds
- Photography Planning
- Moon Phase Widgets
- Astronomy / Weather Apps
- Navigation Tools
- Calendar Integrations

---

## 🏗️ Architecture & Design

- **Modular Structure**️\
  Factory via `CelestialNavigationEvents.provide()`\
  Interfaces: `CelestialEventsCalculator`, `SolarEventsCalculator`, `LunarEventsCalculator`

- **Patterns & Practices** \
  Factory, Facade, Data Classes\
  Separation of solar/lunar logic\
  Clear Kotlin DSL conventions

- **Testing** \
  Unit-tested

---
## 📡 Scientific Foundations and Algorithm Sources

CelestialNavigationEvents implements astronomical calculations based on well-established scientific works and formulas, balancing performance with reasonable precision. Key references include:

- van Flandern, T.C., & Pulkkinen, K.F. (1979).  
  *Low-precision formulae for planetary positions*, Astrophysical Journal Supplement Series, 41, 391–411.

- Reda, I. & Andreas, A. (2008).  
  *Solar Position Algorithm for Solar Radiation Applications*, NREL Report TP-560-34302.  
  [https://www.nrel.gov/docs/fy08osti/34302.pdf](https://www.nrel.gov/docs/fy08osti/34302.pdf)

- Meeus, J. (1998).  
  *Astronomical Algorithms* (2nd ed.).

- Espenak, F. & Meeus, J.  
  *Five Millennium Canon of Solar Eclipses: -1999 to +3000*, NASA Technical Publication TP-2006-214141.

- Espenak, F. (2014).  
  *Polynomial Expressions for Delta T.*
  [https://www.eclipsewise.com/help/deltatpoly2014.html](https://www.eclipsewise.com/help/deltatpoly2014.html)

Additional time calculations use commonly accepted astronomical methods implemented independently.

> ⚠️ The referenced scientific works are cited for transparency only; this library does not guarantee full alignment with their exact implementations or results.

---

## 🚀 Accuracy Disclaimer

This library offers **practical, non-scientific precision** for JVM-based applications. It's suitable for:

- Photography & golden hour planning
- Night mode or UI theming
- Basic navigation tools
- General-purpose mobile & desktop apps

🔬 About Accuracy
- Based on widely recognized approximations from scientific literature
- Optimized for mid-latitude regions
- Accuracy may vary due to:
  - Observer elevation
  - Atmospheric pressure & temperature
  - Nearby obstructions (horizon profile)

Not intended for:
- Scientific research or precision-demanding simulations
- Professional astronomical observatories or ephemeris services
- Mission-critical, safety-related, or real-time systems

✅ Design Highlights
- Fully offline & local computation
- Original implementation — no direct reuse of third-party code
- Sources cited for transparency (see above)

> ⚠️ This software is provided “as is” without warranties.  
It is **not a substitute for professional-grade astronomical software**.  
Use at your own risk.

---

## 🤝 Contributing

Contributions are welcome:

- Report bugs
- Improve docs
- Add features or events
- Suggest improvements

---

## 📩 Feedback & Contact

- GitHub: [celestial-nav-events](https://github.com/MSagGik/celestial-nav-events)
- Issues & discussions on GitHub
- Open to collaboration and real-world usage feedback

---

## 📄 License

This project is licensed under the Apache License 2.0.

See:
- [LICENSE](./LICENSE) for full license terms.
- [NOTICE](./NOTICE) for legal notices and project-specific disclaimers.
- [THIRD-PARTY-NOTICES](./THIRD-PARTY-NOTICES) for third-party components and license references.

Users redistributing or modifying this library, especially in academic or professional contexts, are encouraged to credit the scientific sources listed above.

> ⚠️ `CelestialNavigationEvents` is an open-source project and is not affiliated with JetBrains, Oracle, or any other entity. All trademarks and brand names used in this project (such as "Kotlin", "Java") are the property of their respective owners.
> 
> This project is provided "as is", without warranty of any kind, either express or implied, including but not limited to warranties of merchantability, fitness for a particular purpose, and noninfringement.