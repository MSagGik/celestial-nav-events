package io.github.msaggik.celestialnavigationevents.events.solar

import io.github.msaggik.celestialnavigationevents.api.SolarEventsCalculator
import io.github.msaggik.celestialnavigationevents.common.CelestialNavigationEventsTest
import io.github.msaggik.celestialnavigationevents.common.TestUtils
import io.github.msaggik.celestialnavigationevents.internal.solar.SolarCalculatorImpl
import io.github.msaggik.celestialnavigationevents.model.events.common.riseset.EventType
import io.github.msaggik.celestialnavigationevents.model.measurement.Time
import io.github.msaggik.celestialnavigationevents.model.state.HorizonCrossingSolarState
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.Pair
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
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
internal class SolarEventTest {

    init {
        TestUtils.uiTestHeader(this@SolarEventTest::class.simpleName)
        TestUtils.uiTestHeaderTable()
    }

    private val solarEventsCalculator: SolarEventsCalculator = SolarCalculatorImpl()

    @CelestialNavigationEventsTest
    fun `Sun behavior in Quito (Equator) on Equinox (2024-03-20)`() {
        val coordinate = Pair(-0.1807, -78.4678)
        val dateTime= ZonedDateTime.of(2024, 3, 20, 0, 0, 0, 0, ZoneOffset.ofHours(-5))

        val result = solarEventsCalculator.calculateSolarEventDay(coordinate.first, coordinate.second, dateTime)

        assertEquals(HorizonCrossingSolarState.RISEN_AND_SET, result.type)
        val dayLength = result.dayLength?.toTotalMinutes() ?: 0
        assertTrue(dayLength in 700..740)
    }

    @CelestialNavigationEventsTest
    fun `Polar day in Antarctica (Amundsen-Scott station) on Southern Solstice (2024-12-21)`() {
        val coordinate = Pair(-90.0, 0.0)
        val dateTime= ZonedDateTime.of(2024, 12, 21, 0, 0, 0, 0, ZoneOffset.UTC)

        val result = solarEventsCalculator.calculateSolarEventDay(coordinate.first, coordinate.second, dateTime)

        assertEquals(HorizonCrossingSolarState.POLAR_DAY, result.type)
        assertTrue(result.events.isEmpty())
    }

    @CelestialNavigationEventsTest
    fun testQuitoEquinox() {
        val coordinate = Pair(-0.1807, -78.4678)
        val dateTime= ZonedDateTime.of(2024, 3, 20, 0, 0, 0, 0, ZoneOffset.ofHours(-5))

        val result = solarEventsCalculator.calculateSolarEventDay(coordinate.first, coordinate.second, dateTime)

        assertNotNull(result)
    }

    @CelestialNavigationEventsTest
    fun testMurmanskPolarDay() {
        val coordinate = Pair(68.9585, 33.0827)
        val dateTime= ZonedDateTime.of(2024, 6, 21, 0, 0, 0, 0, ZoneOffset.ofHours(3))

        val result = solarEventsCalculator.calculateSolarEventDay(coordinate.first, coordinate.second, dateTime)

        assert(result.type == HorizonCrossingSolarState.POLAR_DAY)
    }

    @CelestialNavigationEventsTest
    fun testMurmanskPolarNight() {
        val coordinate = Pair(68.9585, 33.0827)
        val dateTime= ZonedDateTime.of(2024, 12, 21, 0, 0, 0, 0, ZoneOffset.ofHours(3))

        val result = solarEventsCalculator.calculateSolarEventDay(coordinate.first, coordinate.second, dateTime)

        assert(result.type == HorizonCrossingSolarState.POLAR_NIGHT)
    }

    @CelestialNavigationEventsTest
    fun testAmundsenScottStation() {
        val coordinate = Pair(-90.0, 0.0)
        val dateTime= ZonedDateTime.of(2024, 12, 21, 0, 0, 0, 0, ZoneOffset.UTC)

        val result = solarEventsCalculator.calculateSolarEventDay(coordinate.first, coordinate.second, dateTime)

        assertNotNull(result)
    }

    @CelestialNavigationEventsTest
    fun testBarrowPolarDay() {
        val coordinate = Pair(71.2906, -156.7886)
        val dateTime= ZonedDateTime.of(2024, 6, 21, 0, 0, 0, 0, ZoneOffset.ofHours(-8))

        val result = solarEventsCalculator.calculateSolarEventDay(coordinate.first, coordinate.second, dateTime)

        assert(result.type == HorizonCrossingSolarState.POLAR_DAY)
    }

    @CelestialNavigationEventsTest
    fun `Murmansk last day before polar night (2024-12-01)`() {
        val coordinate = Pair(68.9585, 33.0827)
        val dateTime= ZonedDateTime.of(2025, 12, 1, 0, 0, 0, 0, ZoneOffset.ofHours(3))

        val result = solarEventsCalculator.calculateSolarEventDay(coordinate.first, coordinate.second, dateTime)

        assertTrue(result.type == HorizonCrossingSolarState.POLAR_NIGHT)
    }

    @CelestialNavigationEventsTest
    fun `Murmansk first day after polar night (2025-01-12)`() {
        val coordinate = Pair(68.9585, 33.0827)
        val dateTime= ZonedDateTime.of(2025, 1, 12, 0, 0, 0, 0, ZoneOffset.ofHours(3))

        val result = solarEventsCalculator.calculateSolarEventDay(coordinate.first, coordinate.second, dateTime)
        val sunrise = result.events.firstOrNull { it.type == EventType.RISE } ?: fail("No sunrise")

        assertTrue(result.type != HorizonCrossingSolarState.POLAR_NIGHT)
        assertTrue(result.events.any { it.type == EventType.RISE })

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 170.49,
            expected = sunrise.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 12, min = 15, sec = 5),
            expected = sunrise.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `Barrow last day before polar day (2024-05-09)`() {
        val coordinate = Pair(71.2906, -156.7886)
        val dateTime= ZonedDateTime.of(2024, 5, 9, 0, 0, 0, 0, ZoneId.of("America/Anchorage"))

        val result = solarEventsCalculator.calculateSolarEventDay(coordinate.first, coordinate.second, dateTime)

        assertTrue(result.type != HorizonCrossingSolarState.POLAR_DAY)
    }

    @CelestialNavigationEventsTest
    fun `Barrow first day of polar day (2024-05-10)`() {
        val coordinate = Pair(71.2906, -156.7886)
        val dateTime= ZonedDateTime.of(2024, 5, 10, 0, 0, 0, 0, ZoneId.of("America/Anchorage"))

        val result = solarEventsCalculator.calculateSolarEventDay(coordinate.first, coordinate.second, dateTime)

        assertEquals(HorizonCrossingSolarState.POLAR_DAY, result.type)
    }

    @CelestialNavigationEventsTest
    fun `Barrow last day of polar day (2024-07-31)`() {
        val coordinate = Pair(71.2906, -156.7886)
        val dateTime= ZonedDateTime.of(2024, 7, 31, 0, 0, 0, 0, ZoneId.of("America/Anchorage"))

        val result = solarEventsCalculator.calculateSolarEventDay(coordinate.first, coordinate.second, dateTime)

        assertEquals(HorizonCrossingSolarState.POLAR_DAY, result.type)
    }

    @CelestialNavigationEventsTest
    fun `Barrow first day after polar day (2024-08-01)`() {
        val UI_LOG_RED = "\u001B[31m"
        val UI_LOG_RESET = "\u001B[0m"

        val coordinate = Pair(71.2906, -156.7886)
        val dateTime= ZonedDateTime.of(2024, 8, 1, 0, 0, 0, 0, ZoneId.of("America/Anchorage"))

        val result = solarEventsCalculator.calculateSolarEventDay(coordinate.first, coordinate.second, dateTime)

        println("${UI_LOG_RED}Future task: fix the approximation of two events in one hour: type ${result.type} approximation${UI_LOG_RESET}")
    }

    @CelestialNavigationEventsTest
    fun `Tromsø near last sunrise before polar night`() {
        val coordinate = Pair(69.6496, 18.9560)
        val dateTime= ZonedDateTime.of(2025, 11, 25, 0, 0, 0, 0, ZoneOffset.ofHours(1))

        val result = solarEventsCalculator.calculateSolarEventDay(coordinate.first, coordinate.second, dateTime)

        assertTrue(result.type != HorizonCrossingSolarState.POLAR_NIGHT)
    }

    @CelestialNavigationEventsTest
    fun `Tromsø first sunrise after polar night`() {
        val coordinate = Pair(69.6496, 18.9560)
        val dateTime= ZonedDateTime.of(2025, 1, 15, 0, 0, 0, 0, ZoneOffset.ofHours(1))

        val result = solarEventsCalculator.calculateSolarEventDay(coordinate.first, coordinate.second, dateTime)
        val sunrise = result.events.firstOrNull { it.type == EventType.RISE } ?: fail("No sunrise")

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 173.18,
            expected = sunrise.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 11, min = 24, sec = 22),
            expected = sunrise.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `Antarctica transition to polar day (Amundsen-Scott)`() {
        val coordinate = Pair(-90.0, 0.0)
        val dateTime= ZonedDateTime.of(2024, 10, 20, 0, 0, 0, 0, ZoneOffset.UTC)

        val result = solarEventsCalculator.calculateSolarEventDay(coordinate.first, coordinate.second, dateTime)

        assertEquals(HorizonCrossingSolarState.POLAR_DAY, result.type)
    }

    @CelestialNavigationEventsTest
    fun `Antarctica transition from polar day to polar night (Amundsen-Scott)`() {
        val coordinate = Pair(-90.0, 0.0)
        val dateTime= ZonedDateTime.of(2024, 7, 20, 23, 0, 0, 0, ZoneOffset.UTC)

        val result = solarEventsCalculator.calculateSolarEventDay(coordinate.first, coordinate.second, dateTime)

        assertEquals(HorizonCrossingSolarState.POLAR_NIGHT, result.type)
    }

    @CelestialNavigationEventsTest
    fun `Moscow sunrise azimuth at equinox (2025-03-20)`() {
        val coordinate = Pair(55.75, 37.62)
        val dateTime= ZonedDateTime.of(2025, 3, 20, 0, 0, 0, 0, ZoneOffset.ofHours(3))

        val result = solarEventsCalculator.calculateSolarEventDay(coordinate.first, coordinate.second, dateTime)
        val sunrise = result.events.firstOrNull { it.type == EventType.RISE } ?: fail("No sunrise")

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 88.93,
            expected = sunrise.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 6, min = 31, sec = 34),
            expected = sunrise.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `Moscow sunset azimuth at equinox (2025-03-20)`() {
        val coordinate = Pair(55.75, 37.62)
        val dateTime= ZonedDateTime.of(2025, 3, 20, 0, 0, 0, 0, ZoneOffset.ofHours(3))

        val result = solarEventsCalculator.calculateSolarEventDay(coordinate.first, coordinate.second, dateTime)
        val sunset = result.events.firstOrNull { it.type == EventType.SET } ?: fail("No sunset")

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 271.43,
            expected = sunset.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 18, min = 43, sec = 27),
            expected = sunset.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `Tromsø sunrise azimuth before polar night (2024-11-20)`() {
        val coordinate = Pair(69.65, 18.95)
        val dateTime= ZonedDateTime.of(2024, 11, 20, 0, 0, 0, 0, ZoneOffset.ofHours(1))

        val result = solarEventsCalculator.calculateSolarEventDay(coordinate.first, coordinate.second, dateTime)

        val sunrise = result.events.firstOrNull { it.type == EventType.RISE } ?: fail("No sunrise")

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 159.47,
            expected = sunrise.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 10, min = 2, sec = 20),
            expected = sunrise.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `Tromsø sunset azimuth before polar night (2024-11-20)`() {
        val coordinate = Pair(69.65, 18.95)
        val dateTime= ZonedDateTime.of(2024, 11, 20, 0, 0, 0, 0, ZoneOffset.ofHours(1))

        val result = solarEventsCalculator.calculateSolarEventDay(coordinate.first, coordinate.second, dateTime)

        val sunset = result.events.firstOrNull { it.type == EventType.SET } ?: fail("No sunrise")

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 200.32,
            expected = sunset.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 12, min = 56, sec = 35),
            expected = sunset.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `Quito sunrise azimuth at equinox (2025-03-20)`() {
        val coordinate = Pair(-0.18, -78.47)
        val dateTime= ZonedDateTime.of(2025, 3, 20, 0, 0, 0, 0, ZoneOffset.ofHours(-5))

        val result = solarEventsCalculator.calculateSolarEventDay(coordinate.first, coordinate.second, dateTime)

        val sunrise = result.events.firstOrNull { it.type == EventType.RISE } ?: fail("No sunrise")

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 89.96,
            expected = sunrise.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 6, min = 17, sec = 53),
            expected = sunrise.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `Quito sunset azimuth at equinox (2025-03-20)`() {
        val coordinate = Pair(-0.18, -78.47)
        val dateTime= ZonedDateTime.of(2025, 3, 20, 0, 0, 0, 0, ZoneOffset.ofHours(-5))

        val result = solarEventsCalculator.calculateSolarEventDay(coordinate.first, coordinate.second, dateTime)

        val sunset = result.events.firstOrNull { it.type == EventType.SET } ?: fail("No sunset")

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 270.24,
            expected = sunset.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 18, min = 24, sec = 24),
            expected = sunset.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `New York sunrise azimuth at summer solstice (2025-06-21)`() {
        val coordinate = Pair(40.71, -74.01)
        val dateTime= ZonedDateTime.of(2025, 6, 21, 0, 0, 0, 0, ZoneOffset.ofHours(-4))

        val result = solarEventsCalculator.calculateSolarEventDay(coordinate.first, coordinate.second, dateTime)

        val sunrise = result.events.firstOrNull { it.type == EventType.RISE } ?: fail("No sunrise")

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 57.50,
            expected = sunrise.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 5, min = 25, sec = 6),
            expected = sunrise.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `New York sunset azimuth at summer solstice (2025-06-21)`() {
        val coordinate = Pair(40.71, -74.01)
        val dateTime= ZonedDateTime.of(2025, 6, 21, 0, 0, 0, 0, ZoneOffset.ofHours(-4))

        val result = solarEventsCalculator.calculateSolarEventDay(coordinate.first, coordinate.second, dateTime)

        val sunset = result.events.firstOrNull { it.type == EventType.SET } ?: fail("No sunset")

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 302.49,
            expected = sunset.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 20, min = 30, sec = 47),
            expected = sunset.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `Cape Town sunrise azimuth at winter solstice (2025-06-21)`() {
        val coordinate = Pair(-33.92, 18.42)
        val dateTime= ZonedDateTime.of(2025, 6, 21, 0, 0, 0, 0, ZoneOffset.ofHours(2))

        val result = solarEventsCalculator.calculateSolarEventDay(coordinate.first, coordinate.second, dateTime)

        val sunrise = result.events.firstOrNull { it.type == EventType.RISE } ?: fail("No sunrise")

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 61.99,
            expected = sunrise.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 7, min = 51, sec = 22),
            expected = sunrise.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `Cape Town sunset azimuth at winter solstice (2025-06-21)`() {
        val coordinate = Pair(-33.92, 18.42)
        val dateTime= ZonedDateTime.of(2025, 6, 21, 0, 0, 0, 0, ZoneOffset.ofHours(2))

        val result = solarEventsCalculator.calculateSolarEventDay(coordinate.first, coordinate.second, dateTime)

        val sunset = result.events.firstOrNull { it.type == EventType.SET } ?: fail("No sunset")

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 298.00,
            expected = sunset.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 17, min = 45, sec = 2),
            expected = sunset.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `sunrise after polar night in Barrow Alaska set`() {
        val coordinate = Pair(71.2906, -156.7886)
        val dateTime= ZonedDateTime.of(2024, 1, 24, 0, 0, 0, 0, ZoneOffset.ofHours(-9))

        val result = solarEventsCalculator.calculateSolarEventDay(coordinate.first, coordinate.second, dateTime)

        val sunset = result.events.firstOrNull { it.type == EventType.SET } ?: fail("No sunset")

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 191.64,
            expected = sunset.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 14, min = 28, sec = 32),
            expected = sunset.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `sunrise after polar night in Barrow Alaska rise`() {
        val coordinate = Pair(71.2906, -156.7886)
        val dateTime= ZonedDateTime.of(2024, 1, 24, 0, 0, 0, 0, ZoneOffset.ofHours(-9))

        val result = solarEventsCalculator.calculateSolarEventDay(coordinate.first, coordinate.second, dateTime)

        val sunrise = result.events.firstOrNull { it.type == EventType.RISE } ?: fail("No sunset")

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 168.61,
            expected = sunrise.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 12, min = 50, sec = 55),
            expected = sunrise.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun `sunrise in Alert Canada after long polar night`() {
        val coordinate = Pair(82.5018, -62.3481)
        val dateTime= ZonedDateTime.of(2024, 2, 28, 12, 0, 0, 0, ZoneOffset.ofHours(-5))

        val result = solarEventsCalculator.calculateSolarEventDay(coordinate.first, coordinate.second, dateTime)

        val sunset = result.events.firstOrNull { it.type == EventType.RISE } ?: fail("No sunset")

        TestUtils.assertAzimuthAroundSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 162.93,
            expected = sunset.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceSolar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 10, min = 12, sec = 59),
            expected = sunset.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }
}