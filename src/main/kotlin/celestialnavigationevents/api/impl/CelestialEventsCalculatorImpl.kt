/*
 * Copyright 2025 Maxim Sagaciyan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package celestialnavigationevents.api.impl

import celestialnavigationevents.api.CelestialEventsCalculator
import celestialnavigationevents.api.LunarEventsCalculator
import celestialnavigationevents.api.SolarEventsCalculator
import celestialnavigationevents.internal.lunar.LunarCalculatorImpl
import celestialnavigationevents.internal.solar.SolarCalculatorImpl

/**
 * Internal implementation of the unified [CelestialEventsCalculator] interface.
 *
 * This class lazily initializes and delegates solar and lunar event calculations
 * to their respective implementations.
 *
 * This class is intended for internal use and should not be exposed directly to the library user.
 */
internal class CelestialEventsCalculatorImpl : CelestialEventsCalculator {

    /**
     * Lazily initialized instance of [SolarEventsCalculator] for computing solar-related events
     * such as sunrise, sunset, twilight phases, and magic/blue hour periods.
     *
     * The instance is created on the first call to [solar] and reused for all subsequent calls.
     */
    private val solarCalculator: SolarEventsCalculator by lazy {
        SolarCalculatorImpl()
    }

    /**
     * Lazily initialized instance of [LunarEventsCalculator] for computing lunar-related events
     * such as moonrise, moonset, moon phase, and illumination.
     *
     * The instance is created on the first call to [lunar] and reused for all subsequent calls.
     */
    private val lunarCalculator: LunarEventsCalculator by lazy {
        LunarCalculatorImpl()
    }

    /**
     * Returns the lazily created [SolarEventsCalculator] instance.
     *
     * @return An initialized calculator capable of performing solar event computations.
     */
    override fun solar(): SolarEventsCalculator = solarCalculator

    /**
     * Returns the lazily created [LunarEventsCalculator] instance.
     *
     * @return An initialized calculator capable of performing lunar event computations.
     */
    override fun lunar(): LunarEventsCalculator = lunarCalculator
}