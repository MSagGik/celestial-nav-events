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

package celestialnavigationevents

import celestialnavigationevents.common.TestUtils
import celestialnavigationevents.events.lunar.LunarEventTest
import celestialnavigationevents.events.lunar.LunarRelativeEventDayTest
import celestialnavigationevents.events.solar.MagicHourTest
import celestialnavigationevents.events.solar.SolarAbsoluteEventDayTest
import celestialnavigationevents.events.solar.SolarEventTest
import celestialnavigationevents.events.solar.SolarRelativeEventDayTest
import celestialnavigationevents.events.solar.SolarRelativeShortEventTest
import celestialnavigationevents.position.CelestialPositionTest
import celestialnavigationevents.time.TimeJDTest
import celestialnavigationevents.time.TimeTest
import celestialnavigationevents.time.UtToTtCorrection
import kotlin.jvm.java

/**
 * Aggregated test suite for CelestialNavigationEvents core astronomical calculations.
 *
 * Use this suite to run a complete validation of the solar and astronomical calculation modules.
 *
 * ⚠️ **Disclaimer:** This test suite provides approximate validation.
 * Due to model simplifications, numerical imprecision, and potential implementation bugs,
 * results may slightly deviate from authoritative astronomical sources. Always cross-check
 * critical data when high accuracy is required.
 *
 * @see SolarEventTest
 * @see SolarRelativeEventDayTest
 * @see SolarRelativeShortEventTest
 * @see SolarAbsoluteEventDayTest
 * @see MagicHourTest
 * @see LunarEventTest
 * @see LunarRelativeEventDayTest
 * @see CelestialPositionTest
 * @see TimeJDTest
 * @see TimeTest
 * @see UtToTtCorrection
 */
private object AllCelestialTests {

    fun runAllCelestialTests() {
        val testClasses = listOf(
            SolarEventTest::class.java,
            SolarRelativeEventDayTest::class.java,
            SolarRelativeShortEventTest::class.java,
            SolarAbsoluteEventDayTest::class.java,
            MagicHourTest::class.java,
            LunarEventTest::class.java,
            LunarRelativeEventDayTest::class.java,
            CelestialPositionTest::class.java,
            TimeJDTest::class.java,
            TimeTest::class.java,
            UtToTtCorrection::class.java
        )
        var totalTests = 0

        for (testClass in testClasses) {
            val result = TestUtils.runClassTests(testClass)
            totalTests += result
        }

        println("Total tests run: $totalTests")
    }
}

private fun main() {
    AllCelestialTests.runAllCelestialTests()
}