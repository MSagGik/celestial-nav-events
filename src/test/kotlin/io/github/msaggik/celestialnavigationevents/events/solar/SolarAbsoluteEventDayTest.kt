package io.github.msaggik.celestialnavigationevents.events.solar

import io.github.msaggik.celestialnavigationevents.common.CelestialNavigationEventsTest
import io.github.msaggik.celestialnavigationevents.common.TestUtils
import io.github.msaggik.celestialnavigationevents.internal.solar.SolarCalculatorImpl
import io.github.msaggik.celestialnavigationevents.model.events.common.riseset.EventType
import io.github.msaggik.celestialnavigationevents.model.measurement.Time
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

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
internal class SolarAbsoluteEventDayTest {

    init {
        TestUtils.uiTestHeader(this@SolarAbsoluteEventDayTest::class.simpleName)
        TestUtils.uiTestHeaderTable()
    }

    private val calculator = SolarCalculatorImpl()

    @CelestialNavigationEventsTest
    fun `test equator on equinox`() {
        val coordinate = Pair(0.0, 0.0)
        val dateTime = ZonedDateTime.of(2023, 3, 20, 6, 0, 0, 0, ZoneId.of("UTC"))
        val result = calculator.findUpcomingSolarAbsoluteEventDay(coordinate.first, coordinate.second, dateTime)

        assertTrue(result.events.isNotEmpty())
        assertNotNull(result.dayLength)
        assertNotNull(result.nightLength)
    }

    @CelestialNavigationEventsTest
    fun `test north pole during polar night`() {
        val coordinate = Pair(90.0, 0.0)
        val dateTime = ZonedDateTime.of(2023, 1, 1, 12, 0, 0, 0, ZoneId.of("UTC"))
        val result = calculator.findUpcomingSolarAbsoluteEventDay(coordinate.first, coordinate.second, dateTime)

        assertTrue(result.events.isNotEmpty())
        assertTrue(result.events.first().dateTime.isAfter(dateTime))
    }

    @CelestialNavigationEventsTest
    fun `test south pole during polar day`() {
        val coordinate = Pair(-90.0, 0.0)
        val dateTime = ZonedDateTime.of(2023, 12, 1, 12, 0, 0, 0, ZoneId.of("UTC"))
        val result = calculator.findUpcomingSolarAbsoluteEventDay(coordinate.first, coordinate.second, dateTime)

        assertTrue(result.events.isNotEmpty())
    }

    @CelestialNavigationEventsTest
    fun `test leap year February 29`() {
        val coordinate = Pair(0.0, 0.0)
        val dateTime = ZonedDateTime.of(2024, 2, 29, 6, 0, 0, 0, ZoneId.of("UTC"))
        val result = calculator.findUpcomingSolarAbsoluteEventDay(coordinate.first, coordinate.second, dateTime)

        assertTrue(result.events.isNotEmpty())
    }

    @CelestialNavigationEventsTest
    fun `test meridian edge longitude`() {
        val coordinate = Pair(0.0, 180.0)
        val dateTime = ZonedDateTime.of(2025, 3, 20, 6, 0, 0, 0, ZoneId.of("UTC"))
        val result = calculator.findUpcomingSolarAbsoluteEventDay(coordinate.first, coordinate.second, dateTime)

        assertTrue(result.events.isNotEmpty())
    }

    @CelestialNavigationEventsTest
    fun `sunset in Tokyo on summer solstice is around 7pm local time`() {
        val coordinate = Pair(35.6762, 139.6503)
        val dateTime = ZonedDateTime.of(2025, 6, 21, 12, 0, 0, 0, ZoneOffset.ofHours(9))
        val result = calculator.findUpcomingSolarAbsoluteEventDay(coordinate.first, coordinate.second, dateTime)

        val sunset = result.events.first { it.type == EventType.SET }

        sunset.let {
            TestUtils.assertAzimuthAroundSolar(
                coordinate = coordinate,
                dateTime = dateTime,
                actual = 300.00,
                expected = it.azimuth,
                visibleResultTest = VISIBLE_LOG_TEST
            )

            val dateResult = it.dateTime

            TestUtils.assertEqualsWithToleranceSolar(
                coordinate = coordinate,
                dateTime = dateTime,
                actual = Time(days = 21, hour = 19, min = 0, sec = 29),
                expected = Time(days = dateResult.dayOfMonth, hour = dateResult.hour, min = dateResult.minute, sec = dateResult.second),
                visibleResultTest = VISIBLE_LOG_TEST
            )
        }
    }

    @CelestialNavigationEventsTest
    fun `sunset in New York on winter solstice is before 5pm local time`() {
        val coordinate = Pair(40.7128, -74.0060)
        val dateTime = ZonedDateTime.of(2025, 12, 21, 0, 0, 0, 0, ZoneOffset.ofHours(-5))
        val result = calculator.findUpcomingSolarAbsoluteEventDay(coordinate.first, coordinate.second, dateTime)

        val sunset = result.events.first { it.type == EventType.SET }
        val sunrise = result.events.first { it.type == EventType.RISE }

        sunset.let {
            TestUtils.assertAzimuthAroundSolar(
                coordinate = coordinate,
                dateTime = dateTime,
                actual = 239.19,
                expected = it.azimuth,
                visibleResultTest = VISIBLE_LOG_TEST
            )

            val dateResult = it.dateTime

            TestUtils.assertEqualsWithToleranceSolar(
                coordinate = coordinate,
                dateTime = dateTime,
                actual = Time(days = 21, hour = 16, min = 31, sec = 57),
                expected = Time(days = dateResult.dayOfMonth, hour = dateResult.hour, min = dateResult.minute, sec = dateResult.second),
                visibleResultTest = VISIBLE_LOG_TEST
            )
        }

        sunrise.let {
            TestUtils.assertAzimuthAroundSolar(
                coordinate = coordinate,
                dateTime = dateTime,
                actual = 120.81,
                expected = it.azimuth,
                visibleResultTest = VISIBLE_LOG_TEST
            )

            val dateResult = it.dateTime

            TestUtils.assertEqualsWithToleranceSolar(
                coordinate = coordinate,
                dateTime = dateTime,
                actual = Time(days = 21, hour = 7, min = 16, sec = 39),
                expected = Time(days = dateResult.dayOfMonth, hour = dateResult.hour, min = dateResult.minute, sec = dateResult.second),
                visibleResultTest = VISIBLE_LOG_TEST
            )
        }
    }

    @CelestialNavigationEventsTest
    fun `sunrise in London during DST`() {
        val coordinate = Pair(51.5074, -0.1278)
        val dateTime = ZonedDateTime.of(2025, 6, 1, 0, 0, 0, 0, ZoneOffset.ofHours(1))
        val result = calculator.findUpcomingSolarAbsoluteEventDay(coordinate.first, coordinate.second, dateTime)

        val sunrise = result.events.firstOrNull() { it.type == EventType.RISE }

        sunrise?.let {
            TestUtils.assertAzimuthAroundSolar(
                coordinate = coordinate,
                dateTime = dateTime,
                actual = 51.53,
                expected = it.azimuth,
                visibleResultTest = VISIBLE_LOG_TEST
            )

            val dateResult = it.dateTime

            TestUtils.assertEqualsWithToleranceSolar(
                coordinate = coordinate,
                dateTime = dateTime,
                actual = Time(days = 1, hour = 4, min = 48, sec = 56),
                expected = Time(days = dateResult.dayOfMonth, hour = dateResult.hour, min = dateResult.minute, sec = dateResult.second),
                visibleResultTest = VISIBLE_LOG_TEST
            )
        }
    }

    @CelestialNavigationEventsTest
    fun `sunrise and sunset in Reykjavik during polar twilight`() {
        val coordinate = Pair(64.1265, -21.8174)
        val dateTime= ZonedDateTime.of(2025, 1, 10, 0, 0, 0, 0, ZoneOffset.ofHours(0))
        val result = calculator.findUpcomingSolarAbsoluteEventDay(coordinate.first, coordinate.second, dateTime)

        val sunrise = result.events.firstOrNull { it.type == EventType.RISE }
        val sunset = result.events.firstOrNull { it.type == EventType.SET }

        assertNotNull(sunrise)
        assertNotNull(sunset)

        sunrise?.let {
            TestUtils.assertAzimuthAroundSolar(
                coordinate = coordinate,
                dateTime = dateTime,
                actual = 145.48,
                expected = it.azimuth,
                visibleResultTest = VISIBLE_LOG_TEST
            )

            val dateResult = it.dateTime

            TestUtils.assertEqualsWithToleranceSolar(
                coordinate = coordinate,
                dateTime = dateTime,
                actual = Time(days = 10, hour = 11, min = 4, sec = 20),
                expected = Time(days = dateResult.dayOfMonth, hour = dateResult.hour, min = dateResult.minute, sec = dateResult.second),
                visibleResultTest = VISIBLE_LOG_TEST
            )
        }

        sunset?.let {
            TestUtils.assertAzimuthAroundSolar(
                coordinate = coordinate,
                dateTime = dateTime,
                actual = 214.63,
                expected = it.azimuth,
                visibleResultTest = VISIBLE_LOG_TEST
            )

            val dateResult = it.dateTime

            TestUtils.assertEqualsWithToleranceSolar(
                coordinate = coordinate,
                dateTime = dateTime,
                actual = Time(days = 10, hour = 16, min = 5, sec = 55),
                expected = Time(days = dateResult.dayOfMonth, hour = dateResult.hour, min = dateResult.minute, sec = dateResult.second),
                visibleResultTest = VISIBLE_LOG_TEST
            )
        }
    }

    @CelestialNavigationEventsTest
    fun `sunset in Sydney on summer solstice is around 8pm local time`() {
        val coordinate = Pair(-33.8688, 151.2093)
        val dateTime= ZonedDateTime.of(2025, 12, 21, 12, 0, 0, 0, ZoneOffset.ofHours(11))
        val result = calculator.findUpcomingSolarAbsoluteEventDay(coordinate.first, coordinate.second, dateTime)

        val sunset = result.events.first { it.type == EventType.SET }

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 240.74,
            expected = sunset.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )

        val dateResult = sunset.dateTime

        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(days = 21, hour = 20, min = 5, sec = 30),
            expected = Time(days = dateResult.dayOfMonth, hour = dateResult.hour, min = dateResult.minute, sec = dateResult.second),
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `sunrise in Moscow in October after DST ends`() {
        val coordinate = Pair(55.7558, 37.6173)
        val dateTime= ZonedDateTime.of(2025, 10, 30, 0, 0, 0, 0, ZoneOffset.ofHours(3))
        val result = calculator.findUpcomingSolarAbsoluteEventDay(coordinate.first, coordinate.second, dateTime)

        val sunrise = result.events.first { it.type == EventType.RISE }

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 113.84,
            expected = sunrise.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )

        val dateResult = sunrise.dateTime

        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(days = 30, hour = 7, min = 31, sec = 39),
            expected = Time(days = dateResult.dayOfMonth, hour = dateResult.hour, min = dateResult.minute, sec = dateResult.second),
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `sunrise in Cape Town is earlier in summer than winter`() {
        val summerDate = ZonedDateTime.of(2025, 12, 21, 0, 0, 0, 0, ZoneId.of("Africa/Johannesburg"))
        val winterDate = ZonedDateTime.of(2025, 6, 21, 0, 0, 0, 0, ZoneId.of("Africa/Johannesburg"))

        val summerResult = calculator.findUpcomingSolarAbsoluteEventDay(-33.9249, 18.4241, summerDate)
        val winterResult = calculator.findUpcomingSolarAbsoluteEventDay(-33.9249, 18.4241, winterDate)

        val summerSunrise = summerResult.events.first { it.type == EventType.RISE }
        val winterSunrise = winterResult.events.first { it.type == EventType.RISE }

        assertTrue(summerSunrise.dateTime.toLocalTime().isBefore(winterSunrise.dateTime.toLocalTime()))
    }
}