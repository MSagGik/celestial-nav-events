package io.github.msaggik.celestialnavigationevents.events.solar

import io.github.msaggik.celestialnavigationevents.common.CelestialNavigationEventsTest
import io.github.msaggik.celestialnavigationevents.common.TestUtils
import io.github.msaggik.celestialnavigationevents.internal.solar.SolarCalculatorImpl
import io.github.msaggik.celestialnavigationevents.model.events.common.riseset.EventType
import io.github.msaggik.celestialnavigationevents.model.measurement.Time
import io.github.msaggik.celestialnavigationevents.model.state.HorizonCrossingSolarState
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.test.assertEquals
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
internal class SolarRelativeEventDayTest {

    init {
        TestUtils.uiTestHeader(this@SolarRelativeEventDayTest::class.simpleName)
        TestUtils.uiTestHeaderTable()
    }

    private val calculator = SolarCalculatorImpl()

    @CelestialNavigationEventsTest
    fun `test mid-latitude summer before rise`() {
        val coordinate = Pair(45.0, 0.0)
        val dateTime= ZonedDateTime.of(2023, 6, 21, 5, 0, 0, 0, ZoneOffset.ofHours(2))
        val result = calculator.findUpcomingSolarRelativeEventDay(coordinate.first, coordinate.second, dateTime)
        val event = result.events.first()

        assertEquals(HorizonCrossingSolarState.RISEN_AND_SET, result.type)

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 54.75,
            expected = event.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 6, min = 13, sec = 10),
            expected = event.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 1, min = 13, sec = 10),
            expected = Time.fromTotalMilliseconds(event.timeToNearestEventMillis),
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `test mid-latitude summer after rise`() {
        val coordinate = Pair(45.0, 0.0)
        val dateTime= ZonedDateTime.of(2023, 6, 21, 14, 0, 0, 0, ZoneOffset.ofHours(2))
        val result = calculator.findUpcomingSolarRelativeEventDay(coordinate.first, coordinate.second, dateTime)
        val event = result.events.first()

        assertEquals(HorizonCrossingSolarState.RISEN_AND_SET, result.type)

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 305.24,
            expected = event.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 21, min = 50, sec = 19),
            expected = event.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 7, min = 50, sec = 19),
            expected = Time.fromTotalMilliseconds(event.timeToNearestEventMillis),
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `test mid-latitude winter`() {
        val coordinate = Pair(45.0, 0.0)
        val dateTime= ZonedDateTime.of(2025, 12, 21, 5, 0, 0, 0, ZoneOffset.ofHours(1))
        val result = calculator.findUpcomingSolarRelativeEventDay(coordinate.first, coordinate.second, dateTime)
        val event = result.events.first()

        assertEquals(HorizonCrossingSolarState.RISEN_AND_SET, result.preType)

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 123.23,
            expected = event.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 8, min = 35, sec = 11),
            expected = event.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 3, min = 35, sec = 11),
            expected = Time.fromTotalMilliseconds(event.timeToNearestEventMillis),
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `test north pole after polar night`() {
        val coordinate = Pair(90.0, 0.0)
        val dateTime= ZonedDateTime.of(2025, 3, 15, 12, 0, 0, 0, ZoneId.of("UTC"))
        val result = calculator.findUpcomingSolarRelativeEventDay(coordinate.first, coordinate.second, dateTime)
        val event = result.events.first()

        assertTrue(result.events.isNotEmpty())
        assertEquals(EventType.RISE, result.events.first().type)

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 91.76,
            expected = event.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 6, min = 15, sec = 4),
            expected = event.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(days = 2, hour = 18, min = 15, sec = 4),
            expected = Time.fromTotalMilliseconds(event.timeToNearestEventMillis),
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `test south pole before polar night ends`() {
        val coordinate = Pair(-90.0, 0.0)
        val dateTime= ZonedDateTime.of(2025, 7, 17, 12, 0, 0, 0, ZoneOffset.ofHours(12))
        val result = calculator.findUpcomingSolarRelativeEventDay(coordinate.first, coordinate.second, dateTime)
        val event = result.events.first()

        assertTrue(result.events.isNotEmpty())
        assertEquals(EventType.RISE, result.events.first().type)

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 312.97,
            expected = event.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 3, min = 1, sec = 26),
            expected = event.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(days = 65, hour = 15, min = 1, sec = 26),
            expected = Time.fromTotalMilliseconds(event.timeToNearestEventMillis),
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `test azimuth at Tokyo summer sunrise`() {
        val coordinate = Pair(35.6762, 139.6503)
        val dateTime= ZonedDateTime.of(2025, 6, 21, 3, 0, 0, 0, ZoneOffset.ofHours(9))
        val result = calculator.findUpcomingSolarRelativeEventDay(coordinate.first, coordinate.second, dateTime)

        val sunrise = result.events.first { it.type == EventType.RISE }

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 60.00,
            expected = sunrise.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 4, min = 25, sec = 52),
            expected = sunrise.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 1, min = 25, sec = 52),
            expected = Time.fromTotalMilliseconds(sunrise.timeToNearestEventMillis),
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `test Buenos Aires winter sunset`() {
        val coordinate = Pair(-34.6037, -58.3816)
        val dateTime= ZonedDateTime.of(2025, 6, 21, 17, 0, 0, 0, ZoneOffset.ofHours(-3))
        val result = calculator.findUpcomingSolarRelativeEventDay(coordinate.first, coordinate.second, dateTime)

        val sunset = result.events.first { it.type == EventType.SET }

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 298.23,
            expected = sunset.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 17, min = 50, sec = 31),
            expected = sunset.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 0, min = 50, sec = 31),
            expected = Time.fromTotalMilliseconds(sunset.timeToNearestEventMillis),
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }


    @CelestialNavigationEventsTest
    fun `test meridian edge longitude`() {
        val coordinate = Pair(0.0, 180.0)
        val dateTime= ZonedDateTime.of(2025, 3, 20, 1, 0, 0, 0, ZoneOffset.ofHours(12))
        val result = calculator.findUpcomingSolarRelativeEventDay(coordinate.first, coordinate.second, dateTime)
        val event = result.events.first()

        assertTrue(result.events.isNotEmpty())

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 90.24,
            expected = event.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 6, min = 4, sec = 13),
            expected = event.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 5, min = 4, sec = 13),
            expected = Time.fromTotalMilliseconds(event.timeToNearestEventMillis),
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `test antimeridian edge longitude`() {
        val coordinate = Pair(0.0, -180.0)
        val dateTime= ZonedDateTime.of(2025, 3, 20, 1, 0, 0, 0, ZoneOffset.ofHours(-12))
        val result = calculator.findUpcomingSolarRelativeEventDay(coordinate.first, coordinate.second, dateTime)
        val event = result.events.first()

        assertTrue(result.events.isNotEmpty())

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 89.45,
            expected = event.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 6, min = 3, sec = 38),
            expected = event.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 5, min = 3, sec = 38),
            expected = Time.fromTotalMilliseconds(event.timeToNearestEventMillis),
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `test leap year Mart 1`() {
        val coordinate = Pair(0.0, 0.0)
        val dateTime= ZonedDateTime.of(2025, 3, 1, 10, 0, 0, 0, ZoneId.of("UTC"))
        val result = calculator.findUpcomingSolarRelativeEventDay(coordinate.first, coordinate.second, dateTime)
        val event = result.events.first()

        assertTrue(result.events.isNotEmpty())

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 262.72,
            expected = event.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 18, min = 15, sec = 31),
            expected = event.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 8, min = 15, sec = 31),
            expected = Time.fromTotalMilliseconds(event.timeToNearestEventMillis),
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `test day before leap day`() {
        val coordinate = Pair(0.0, 0.0)
        val dateTime= ZonedDateTime.of(2025, 2, 28, 6, 0, 0, 0, ZoneId.of("UTC"))
        val result = calculator.findUpcomingSolarRelativeEventDay(coordinate.first, coordinate.second, dateTime)
        val event = result.events.first()

        assertTrue(result.events.isNotEmpty())

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 97.84,
            expected = event.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 6, min = 9, sec = 5),
            expected = event.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 0, min = 9, sec = 5),
            expected = Time.fromTotalMilliseconds(event.timeToNearestEventMillis),
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `sunrise after polar night in Tromsø`() {
        val coordinate = Pair(69.6496, 18.9560)
        val dateTime= ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.ofHours(1))
        val result = calculator.findUpcomingSolarRelativeEventDay(coordinate.first, coordinate.second, dateTime)
        val event = result.events.first()

        assertEquals(EventType.RISE, event.type)

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 177.97,
            expected = event.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 11, min = 44, sec = 41),
            expected = event.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(days = 14, hour = 11, min = 44, sec = 41),
            expected = Time.fromTotalMilliseconds(event.timeToNearestEventMillis),
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `sunset after polar day in Tromsø`() {
        val coordinate = Pair(69.6496, 18.9560)
        val dateTime= ZonedDateTime.of(2024, 7, 20, 12, 0, 0, 0, ZoneOffset.ofHours(2))
        val result = calculator.findUpcomingSolarRelativeEventDay(coordinate.first, coordinate.second, dateTime)
        val event = result.events.first()

        assertEquals(EventType.SET, event.type)

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 353.47,
            expected = event.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 0, min = 23, sec = 0),
            expected = event.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(days = 5, hour = 12, min = 23, sec = 0),
            expected = Time.fromTotalMilliseconds(event.timeToNearestEventMillis),
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `sunrise after polar night in Barrow Alaska`() {
        val coordinate = Pair(71.2906, -156.7886)
        val dateTime= ZonedDateTime.of(2025, 1, 5, 12, 0, 0, 0, ZoneOffset.ofHours(-9))
        val result = calculator.findUpcomingSolarRelativeEventDay(coordinate.first, coordinate.second, dateTime)
        val event = result.events.first()

        assertEquals(EventType.RISE, event.type)

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 174.40,
            expected = event.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 13, min = 15, sec = 7),
            expected = event.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(days = 17, hour = 1, min = 15, sec = 7),
            expected = Time.fromTotalMilliseconds(event.timeToNearestEventMillis),
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `sunset after polar day in Barrow Alaska`() {
        val coordinate = Pair(71.2906, -156.7886)
        val dateTime= ZonedDateTime.of(2025, 8, 1, 12, 0, 0, 0, ZoneOffset.ofHours(-8))
        val result = calculator.findUpcomingSolarRelativeEventDay(coordinate.first, coordinate.second, dateTime)
        val event = result.events.first()

        assertEquals(EventType.SET, event.type)

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 350.99,
            expected = event.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 1, min = 55, sec = 37),
            expected = event.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 13, min = 55, sec = 37),
            expected = Time.fromTotalMilliseconds(event.timeToNearestEventMillis),
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `sunrise after short polar night in Murmansk`() {
        val coordinate = Pair(68.9585, 33.0827)
        val dateTime= ZonedDateTime.of(2025, 1, 2, 12, 0, 0, 0, ZoneOffset.ofHours(3))
        val result = calculator.findUpcomingSolarRelativeEventDay(coordinate.first, coordinate.second, dateTime)
        val event = result.events.first()

        assertEquals(EventType.RISE, event.type)

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 173.49,
            expected = event.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 12, min = 27, sec = 36),
            expected = event.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(days = 9, hour = 0, min = 27, sec = 36),
            expected = Time.fromTotalMilliseconds(event.timeToNearestEventMillis),
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `first event near equator in Nairobi before first event`() {
        val coordinate = Pair(-1.2921, 36.8219)
        val dateTime= ZonedDateTime.of(2025, 3, 10, 5, 0, 0, 0, ZoneOffset.ofHours(3))
        val result = calculator.findUpcomingSolarRelativeEventDay(coordinate.first, coordinate.second, dateTime)
        val event = result.events.first()

        assertEquals(EventType.RISE, event.type)

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 94.05,
            expected = event.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 6, min = 39, sec = 17),
            expected = event.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 1, min = 39, sec = 17),
            expected = Time.fromTotalMilliseconds(event.timeToNearestEventMillis),
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `first event near equator in Nairobi after first event`() {
        val coordinate = Pair(-1.2921, 36.8219)
        val dateTime= ZonedDateTime.of(2025, 3, 10, 10, 0, 0, 0, ZoneOffset.ofHours(3))
        val result = calculator.findUpcomingSolarRelativeEventDay(coordinate.first, coordinate.second, dateTime)
        val event = result.events.first()

        assertEquals(EventType.SET, event.type)

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 266.14,
            expected = event.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 18, min = 46, sec = 34),
            expected = event.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 8, min = 46, sec = 34),
            expected = Time.fromTotalMilliseconds(event.timeToNearestEventMillis),
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `sunrise after long polar night in Svalbard`() {
        val coordinate = Pair(78.2232, 15.6469)
        val dateTime= ZonedDateTime.of(2025, 1, 20, 12, 0, 0, 0, ZoneOffset.ofHours(1))
        val result = calculator.findUpcomingSolarRelativeEventDay(coordinate.first, coordinate.second, dateTime)
        val event = result.events.first()

        assertEquals(EventType.RISE, event.type)

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 172.62,
            expected = event.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 11, min = 41, sec = 16),
            expected = event.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(days = 25, hour = 23, min = 41, sec = 16),
            expected = Time.fromTotalMilliseconds(event.timeToNearestEventMillis),
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `sunset after polar day in Longyearbyen`() {
        val coordinate = Pair(78.2232, 15.6469)
        val dateTime= ZonedDateTime.of(2025, 7, 25, 12, 0, 0, 0, ZoneOffset.ofHours(1))
        val result = calculator.findUpcomingSolarRelativeEventDay(coordinate.first, coordinate.second, dateTime)
        val event = result.events.first()

        assertEquals(EventType.SET, event.type)

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 350.0,
            expected = event.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 23, min = 19, sec = 1),
            expected = event.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(days = 30, hour = 11, min = 19, sec = 1),
            expected = Time.fromTotalMilliseconds(event.timeToNearestEventMillis),
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `sunrise in Alert Canada after long polar night`() {
        val coordinate = Pair(82.5018, -62.3481)
        val dateTime= ZonedDateTime.of(2025, 1, 10, 12, 0, 0, 0, ZoneOffset.ofHours(-5))

        val result = calculator.findUpcomingSolarRelativeEventDay(coordinate.first, coordinate.second, dateTime)
        val event = result.events.first()

        assertEquals(EventType.RISE, event.type)

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 165.0,
            expected = event.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 10, min = 22, sec = 56),
            expected = event.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(days = 47, hour = 22, min = 22, sec = 56),
            expected = Time.fromTotalMilliseconds(event.timeToNearestEventMillis),
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `returns nearest event next day if no events today`() {
        val coordinate = Pair(66.5, 25.7)
        val dateTime= ZonedDateTime.of(2025, 1, 1, 23, 50, 0, 0, ZoneOffset.ofHours(2))
        val result = calculator.findUpcomingSolarRelativeEventDay(coordinate.first, coordinate.second, dateTime)
        val event = result.events.first()

        assertEquals(EventType.RISE, event.type)

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 160.31,
            expected = event.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 10, min = 55, sec = 28),
            expected = event.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 11, min = 5, sec = 28),
            expected = Time.fromTotalMilliseconds(event.timeToNearestEventMillis),
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `sunset just after midnight UTC at equator`() {
        val coordinate = Pair(0.0, 0.0)
        val dateTime= ZonedDateTime.of(2025, 5, 15, 0, 5, 0, 0, ZoneId.of("UTC"))

        val result = calculator.findUpcomingSolarRelativeEventDay(coordinate.first, coordinate.second, dateTime)
        val event = result.events.first()

        assertEquals(EventType.RISE, event.type)

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 71.07,
            expected = event.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 5, min = 52, sec = 51),
            expected = event.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 5, min = 47, sec = 51),
            expected = Time.fromTotalMilliseconds(event.timeToNearestEventMillis),
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }
}