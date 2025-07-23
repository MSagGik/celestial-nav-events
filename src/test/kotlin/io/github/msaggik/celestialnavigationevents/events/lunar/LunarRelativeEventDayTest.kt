package io.github.msaggik.celestialnavigationevents.events.lunar

import io.github.msaggik.celestialnavigationevents.api.LunarEventsCalculator
import io.github.msaggik.celestialnavigationevents.common.CelestialNavigationEventsTest
import io.github.msaggik.celestialnavigationevents.common.TestUtils
import io.github.msaggik.celestialnavigationevents.internal.lunar.LunarCalculatorImpl
import io.github.msaggik.celestialnavigationevents.model.events.common.riseset.EventType
import io.github.msaggik.celestialnavigationevents.model.measurement.Time
import io.github.msaggik.celestialnavigationevents.model.state.HorizonCrossingLunarState
import java.time.ZonedDateTime
import kotlin.test.assertEquals
import kotlin.test.fail

/*
 * Copyright 2025 Maxim Sagaciyang
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

private const val VISIBLE_LOG_TEST = true

/**
 * ⚠️ **Disclaimer:** This test suite provides approximate validation of astronomical event calculations.
 * Due to model simplifications, numerical imprecision, and potential implementation errors,
 * results may slightly deviate from authoritative astronomical sources. Always cross-check
 * critical data when high accuracy is required.
 */
internal class LunarRelativeEventDayTest {

    init {
        TestUtils.uiTestHeader(this@LunarRelativeEventDayTest::class.simpleName)
        TestUtils.uiTestHeaderTable()
    }

    private val calculator: LunarEventsCalculator = LunarCalculatorImpl() // заменить на реализацию

    @CelestialNavigationEventsTest
    fun greenland_2025_01_05() {
        val coordinate = Pair(69.6496, 18.9560)
        val dateTime= ZonedDateTime.parse("2025-01-02T12:00:00+01:00")

        val result = calculator.findUpcomingLunarRelativeEventDay(coordinate.first, coordinate.second, dateTime)
        result.events.firstOrNull { it.type == EventType.RISE } ?: fail("No moonset event")
    }

    @CelestialNavigationEventsTest
    fun sydney_2025_06_03() {
        val coordinate = Pair(-33.87, 151.21)
        val dateTime= ZonedDateTime.parse("2025-06-03T14:00:00+10:00")

        val result = calculator.findUpcomingLunarRelativeEventDay(coordinate.first, coordinate.second, dateTime)
        val event = result.events.firstOrNull { it.type == EventType.SET } ?: fail("No moonset event")

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 276.14,
            expected = event.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 0, min = 9, sec = 23),
            expected = event.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun perth_set_and_risen_2025_05_12() {
        val result = calculator.findUpcomingLunarRelativeEventDay(
            -31.95, 115.86, ZonedDateTime.parse("2025-05-12T12:00:00+08:00")
        )
        assertEquals(HorizonCrossingLunarState.SET_AND_RISEN, result.type)
    }
}