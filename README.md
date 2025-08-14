# 🌌 celestial-nav-events (CelestialNavigationEvents)

**CelestialNavigationEvents** is a lightweight library written in Kotlin for calculating common Sun and Moon events — including sunrise, sunset, twilight phases, moonrise, moonset, lunar phases (in days), illumination, and more. It works offline worldwide using only geographic coordinates and time in the Java standard `ZonedDateTime` format.

[![](https://jitpack.io/v/MSagGik/celestial-nav-events.svg)](https://jitpack.io/#MSagGik/celestial-nav-events)
[![](https://img.shields.io/github/license/MSagGik/celestial-nav-events)](LICENSE.txt)
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
       implementation("com.github.MSagGik:celestial-nav-events:1.0.0-beta2")
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

> ⚠️ Note: Calculations are approximate and intended for general-purpose use.

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

CelestialNavigationEvents implements custom astronomical calculations inspired by well-established scientific formulas, balancing performance with reasonable precision. The library does not claim exact alignment with the referenced works. Key references include:

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
> The algorithms are original implementations based on widely accepted scientific formulas and publicly available astronomical data. While inspired by standard methods, the code is independently developed and does not include direct copies of source code from the referenced works.

---

## 🚀 Accuracy Disclaimer

This library offers **practical, non-scientific precision** intended for general-purpose JVM-based applications such as:

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

Not intended or recommended for:
- Scientific research or precision-demanding simulations
- Professional astronomical observatories or ephemeris services
- Mission-critical, safety-related, or real-time systems

✅ Design Highlights
- Fully offline & local computation
- Original implementation — no direct reuse of third-party code
- Sources cited for transparency (see above)

> ⚠️ The algorithms offer the highest precision in mid- and low-latitude regions. In polar areas, edge cases such as short twilight durations or low-angle transitions may involve approximations.
> While precision is sufficient for general-purpose applications, this library is not intended for scientific research or mission-critical systems.
>
> This software is provided “as is” without warranties.  
It is **not a substitute for professional-grade astronomical software**.  
Use at your own risk.

---

## 🛡️ Legal & Privacy Disclaimer

- **Privacy & Location Compliance:**  
  Use of this library may result in the processing of sensitive user data, including geolocation. It is the sole responsibility of the developer or organization integrating this library to ensure full compliance with applicable local, regional, and international laws and regulations (such as GDPR, CCPA, etc.) regarding user privacy, consent, and data protection. The original author and contributors of this project accept no liability or responsibility for any legal or regulatory issues arising from such use.

- **Not for Safety-Critical Use:**  
  This library is not designed or intended for use in safety-critical applications (such as emergency services, autonomous systems, or life-support environments). It provides no guarantees for accuracy, reliability, or fault tolerance. Use at your own risk.

- **Liability Disclaimer:**  
  This software is provided "AS IS", without warranties of any kind. The original author and contributors disclaims any liability for damages, losses, or consequences arising from the use, misuse, or inability to use this library in any form or context.

___

## 🤝 Contributing

Contributions are welcome:

- Report bugs
- Improve docs
- Add features or events
- Suggest improvements

By submitting a pull request, you agree to license your contribution under the Apache License 2.0  
and confirm that you have the legal right to do so.

---

## 📩 Feedback & Contact

- GitHub: [celestial-nav-events](https://github.com/MSagGik/celestial-nav-events)
- Issues & discussions on GitHub
- Open to collaboration and real-world usage feedback

---

## 📬 Support Policy

This library is provided as-is, without active support or maintenance obligations.  
The maintainers of this project do not offer legal or technical support, and have no obligation to respond to issues, feature requests, or pull requests.

---

## 🏢 Commercial or Enterprise Use

If you plan to use this library as part of a commercial, enterprise, or distributed SDK product, you are solely responsible for ensuring full legal compliance, adequate testing, and proper integration practices.  
No warranties, guarantees, or obligations of support or fitness are expressed or implied by the original author and contributors. Use in production environments is at your own risk.

---

## 📄 License

This project is licensed under the Apache License 2.0.

See:
- [LICENSE](./LICENSE.txt) (LICENSE.txt) for full license terms.
- [NOTICE](./NOTICE.txt) (NOTICE.txt) for legal notices and project-specific disclaimers.
- [THIRD-PARTY-NOTICES](./THIRD-PARTY-NOTICES.txt) (THIRD-PARTY-NOTICES.txt) for third-party components and license references.

Users redistributing or modifying this library, especially in academic or professional contexts, are encouraged to credit the scientific sources listed above.

Under Section 4(d) of the Apache License 2.0, if you redistribute this library or any derivative works, you **must** include the complete contents of the `NOTICE` file with your distributions.  
This `NOTICE` file **includes** the third-party attributions and license references as documented in its `THIRD-PARTY-NOTICES` section, which is an integral part of the `NOTICE`.  
Failure to include the `NOTICE` file in its entirety constitutes a breach of the license terms and may lead to legal consequences.

By redistributing this project or derivative works without including the `NOTICE` file, you assume full responsibility for any resulting legal or compliance issues.  
The original author and contributors **expressly disclaim** any liability or responsibility arising from your failure to comply with these requirements.

> ⚠️ `CelestialNavigationEvents` is an open-source project and is not affiliated with JetBrains, Oracle, Apache Software Foundation, or any other entity. All trademarks and brand names used in this project (such as "Kotlin", "Java", "Apache") are the property of their respective owners.
> Use of third-party trademarks is for identification purposes only and does not imply any endorsement by or affiliation with the respective trademark holders.
>
> This project is provided "as is", without warranty of any kind, either express or implied, including but not limited to warranties of merchantability, fitness for a particular purpose, and noninfringement.