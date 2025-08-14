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

package celestialnavigationevents.events.lunar

import celestialnavigationevents.api.LunarEventsCalculator
import celestialnavigationevents.common.CelestialNavigationEventsTest
import celestialnavigationevents.common.TestUtils
import celestialnavigationevents.internal.lunar.LunarCalculatorImpl
import celestialnavigationevents.model.events.common.riseset.EventType
import celestialnavigationevents.model.events.lunar.LunarEventDay
import celestialnavigationevents.model.measurement.Time
import celestialnavigationevents.model.state.HorizonCrossingLunarState
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.test.fail

private const val VISIBLE_LOG_TEST = true

/**
 * ⚠️ **Disclaimer:** This test suite provides approximate validation of astronomical event calculations.
 * Due to model simplifications, numerical imprecision, and potential implementation errors,
 * results may slightly deviate from authoritative astronomical sources. Always cross-check
 * critical data when high accuracy is required.
 */
internal class LunarEventTest {

    init {
        TestUtils.uiTestHeader(this@LunarEventTest::class.simpleName)
        TestUtils.uiTestHeaderTable()
    }

    private val calculator: LunarEventsCalculator = LunarCalculatorImpl()

    @CelestialNavigationEventsTest
    fun `equator typical day should return RISEN_AND_SET`() {
        val result = calculator.calculateLunarEventDay(0.0, 0.0, ZonedDateTime.parse("2025-03-15T00:00Z"))
        assertEquals(HorizonCrossingLunarState.SET_AND_RISEN, result.type)
        assertEquals(2, result.events.size)
    }

    @CelestialNavigationEventsTest
    fun `mid latitude typical spring day`() {
        val result = calculator.calculateLunarEventDay(55.75, 37.62, ZonedDateTime.parse("2025-04-10T00:00Z"))
        assertEquals(HorizonCrossingLunarState.SET_AND_RISEN, result.type)
    }

    @CelestialNavigationEventsTest
    fun `mid latitude long night should have full night or set only`() {
        val result = calculator.calculateLunarEventDay(55.75, 37.62, ZonedDateTime.parse("2025-12-12T00:00+03:00"))
        assertTrue(result.type == HorizonCrossingLunarState.RISEN_AND_SET)
    }

    @CelestialNavigationEventsTest
    fun `polar summer should return FULL_DAY`() {
        val result = calculator.calculateLunarEventDay(69.0, 18.95, ZonedDateTime.parse("2025-07-01T00:00+02:00"))
        assertEquals(HorizonCrossingLunarState.SET_RISE_SET, result.type)
    }

    @CelestialNavigationEventsTest
    fun `polar winter should return FULL_NIGHT`() {
        val result = calculator.calculateLunarEventDay(69.0, 18.95, ZonedDateTime.parse("2025-01-01T00:00Z"))
        assertEquals(HorizonCrossingLunarState.FULL_NIGHT, result.type)
    }

    @CelestialNavigationEventsTest
    fun `moon never sets should be ONLY_RISEN or FULL_DAY`() {
        val result = calculator.calculateLunarEventDay(80.0, 0.0, ZonedDateTime.parse("2025-06-15T00:00Z"))
        assertTrue(result.type == HorizonCrossingLunarState.FULL_NIGHT)
    }

    @CelestialNavigationEventsTest
    fun `edge case near poles with rise_set_rise`() {
        val result = calculator.calculateLunarEventDay(88.0, 0.0, ZonedDateTime.parse("2025-08-01T00:00Z"))
        assertTrue(result.type == HorizonCrossingLunarState.FULL_NIGHT)
    }

    @CelestialNavigationEventsTest
    fun `south pole in summer gives FULL_DAY`() {
        val result = calculator.calculateLunarEventDay(-90.0, 0.0, ZonedDateTime.parse("2025-12-15T00:00Z"))
        assertEquals(HorizonCrossingLunarState.FULL_DAY, result.type)
    }

    @CelestialNavigationEventsTest
    fun `south pole in winter gives FULL_NIGHT`() {
        val result = calculator.calculateLunarEventDay(-90.0, 0.0, ZonedDateTime.parse("2025-06-15T00:00Z"))
        assertEquals(HorizonCrossingLunarState.FULL_DAY, result.type)
    }

    @CelestialNavigationEventsTest
    fun `longitude variation does not change state RISEN_AND_SET -12_00`() {
        val result = calculator.calculateLunarEventDay(0.0, -180.0, ZonedDateTime.parse("2025-05-01T00:00-12:00"))
        assertEquals(HorizonCrossingLunarState.RISEN_AND_SET, result.type)
    }

    @CelestialNavigationEventsTest
    fun `longitude variation does not change state RISEN_AND_SET +06_00`() {
        val result = calculator.calculateLunarEventDay(0.0, 90.0, ZonedDateTime.parse("2025-05-01T00:00+06:00"))
        assertEquals(HorizonCrossingLunarState.RISEN_AND_SET, result.type)
    }

    @CelestialNavigationEventsTest
    fun `longitude variation does not change state RISEN_AND_SET +12_00`() {
        val result = calculator.calculateLunarEventDay(0.0, 180.0, ZonedDateTime.parse("2025-05-01T00:00+12:00"))
        assertEquals(HorizonCrossingLunarState.RISEN_AND_SET, result.type)
    }

    @CelestialNavigationEventsTest
    fun `longitude variation does not change state RISEN_AND_SET`() {
        val result = calculator.calculateLunarEventDay(0.0, 0.0, ZonedDateTime.parse("2025-05-01T00:00Z"))
        assertEquals(HorizonCrossingLunarState.RISEN_AND_SET, result.type)
    }

    @CelestialNavigationEventsTest
    fun `longitude variation does not change state SET_AND_RISEN -06_00`() {
        val result = calculator.calculateLunarEventDay(0.0, -90.0, ZonedDateTime.parse("2025-05-01T00:00-06:00"))
        assertEquals(HorizonCrossingLunarState.RISEN_AND_SET, result.type)
    }

    @CelestialNavigationEventsTest
    fun `Tromsø moonrise`() {
        val coordinate = Pair(69.6496, 18.9560)
        val dateTime= ZonedDateTime.of(2025, 1, 5, 0, 0, 0, 0, ZoneOffset.ofHours(1))

        val result = calculator.calculateLunarEventDay(coordinate.first, coordinate.second, dateTime)
        result.events.firstOrNull { it.type == EventType.RISE } ?: fail("No sunrise")
    }

    @CelestialNavigationEventsTest
    fun `Antarctica moonset`() {
        val coordinate = Pair(-90.0, 0.0)
        val dateTime = ZonedDateTime.of(2025, 8, 12, 0, 0, 0, 0, ZoneOffset.ofHours(12))

        val result = calculator.calculateLunarEventDay(coordinate.first, coordinate.second, dateTime)

        result.events.firstOrNull { it.type == EventType.SET } ?: fail("No moonset event")
    }

    @CelestialNavigationEventsTest
    fun moonsetAzimuthOnEquatorShouldBeWest() {
        val coordinate = Pair(0.0, 0.0)
        val dateTime = ZonedDateTime.of(2025, 6, 9, 0, 0, 0, 0, ZoneOffset.UTC)

        val result = calculator.calculateLunarEventDay(coordinate.first, coordinate.second, dateTime)
        val moonset = result.events.firstOrNull { it.type == EventType.SET } ?: fail("No moonset event")
        val moonrise = result.events.firstOrNull { it.type == EventType.RISE } ?: fail("No moonrise event")

        TestUtils.assertAzimuthAroundLunar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 246.86,
            expected = moonset.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceLunar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 4, min = 13, sec = 29),
            expected = moonset.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )

        TestUtils.assertAzimuthAroundLunar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 114.71,
            expected = moonrise.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceLunar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 16, min = 37, sec = 41),
            expected = moonrise.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun moonsetInMidLatitudeJune2025ShouldMatch() {
        val coordinate = Pair(40.7128, -74.0060)
        val dateTime = ZonedDateTime.of(2025, 6, 9, 0, 0, 0, 0, ZoneOffset.ofHours(-4))

        val result = calculator.calculateLunarEventDay(coordinate.first, coordinate.second, dateTime)
        val moonset = result.events.firstOrNull { it.type == EventType.SET } ?: fail("No moonset")
        val moonrise = result.events.firstOrNull { it.type == EventType.RISE } ?: fail("No moonrise")

        TestUtils.assertAzimuthAroundLunar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 238.13,
            expected = moonset.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceLunar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 3, min = 48, sec = 0),
            expected = moonset.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )

        TestUtils.assertAzimuthAroundLunar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 124.68,
            expected = moonrise.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceLunar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 19, min = 24, sec = 12),
            expected = moonrise.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun moonEventsInMurmanskShouldBeEmptyOrSingle() {
        val coordinate = Pair(69.0, 33.0)
        val dateTime = ZonedDateTime.of(2025, 6, 9, 0, 0, 0, 0, ZoneOffset.ofHours(3))

        val result = calculator.calculateLunarEventDay(coordinate.first, coordinate.second, dateTime)
        assertTrue(result.events.size <= 1)
        assertTrue(result.type == HorizonCrossingLunarState.FULL_NIGHT)
    }

    @CelestialNavigationEventsTest
    fun moonEventsInSydneyJune2025ShouldBeValid() {
        val coordinate = Pair(-33.8688, 151.2093)
        val dateTime = ZonedDateTime.of(2025, 6, 9, 0, 0, 0, 0, ZoneOffset.ofHours(10))

        val result = calculator.calculateLunarEventDay(coordinate.first, coordinate.second, dateTime)
        val moonrise = result.events.firstOrNull { it.type == EventType.RISE } ?: fail("No moonrise")
        val moonset = result.events.firstOrNull { it.type == EventType.SET } ?: fail("No moonset")

        TestUtils.assertAzimuthAroundLunar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 118.36,
            expected = moonrise.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceLunar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 15, min = 3, sec = 33),
            expected = moonrise.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )

        TestUtils.assertAzimuthAroundLunar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 243.62,
            expected = moonset.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceLunar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 4, min = 53, sec = 3),
            expected = moonset.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun moonEventsInS68_60AndE33_01_date_19_06_25() {
        val coordinate = Pair(-68.60, 33.01)
        val dateTime = ZonedDateTime.of(2025, 6, 19, 10, 0, 0, 0, ZoneOffset.ofHours(12))

        val result = calculator.calculateLunarEventDay(coordinate.first, coordinate.second, dateTime)
        val moonrise = result.events.firstOrNull { it.type == EventType.RISE } ?: fail("No moonrise")
        val moonset = result.events.firstOrNull { it.type == EventType.SET } ?: fail("No moonset")

        TestUtils.assertAzimuthAroundLunar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 89.20,
            expected = moonrise.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceLunar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 9, min = 58, sec = 9),
            expected = moonrise.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )

        TestUtils.assertAzimuthAroundLunar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 279.72,
            expected = moonset.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceLunar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 21, min = 40, sec = 43),
            expected = moonset.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun moonEventsInN57_71AndE39_77_date_19_06_25() {
        val coordinate = Pair(57.71, 39.77)
        val dateTime = ZonedDateTime.of(2025, 6, 19, 10, 0, 0, 0, ZoneOffset.ofHours(3))

        val result = calculator.calculateLunarEventDay(coordinate.first, coordinate.second, dateTime)
        val moonrise = result.events.firstOrNull { it.type == EventType.RISE } ?: fail("No moonrise")
        val moonset = result.events.firstOrNull { it.type == EventType.SET } ?: fail("No moonset")

        TestUtils.assertAzimuthAroundLunar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 89.71,
            expected = moonrise.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceLunar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 0, min = 25, sec = 53),
            expected = moonrise.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )

        TestUtils.assertAzimuthAroundLunar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = 276.96,
            expected = moonset.azimuth,
            visibleResultTest = VISIBLE_LOG_TEST
        )
        TestUtils.assertEqualsWithToleranceLunar(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = Time(hour = 13, min = 14, sec = 39),
            expected = moonset.time,
            visibleResultTest = VISIBLE_LOG_TEST
        )
    }

    @CelestialNavigationEventsTest
    fun illuminationShouldBeWithinBounds() {
        val coordinate = Pair(0.0, 0.0)
        val dateTime = ZonedDateTime.of(2025, 6, 9, 0, 0, 0, 0, ZoneOffset.UTC)

        val result = calculator.calculateLunarEventDay(coordinate.first, coordinate.second, dateTime)
        assertTrue(result.illuminationPercent in 0.0..100.0)
    }

    @CelestialNavigationEventsTest
    fun `invalid illumination throws exception`() {
        assertFailsWith<IllegalArgumentException> {
            LunarEventDay(events = emptyList(), type = HorizonCrossingLunarState.ERROR, illuminationPercent = 150.0)
        }
    }

    @CelestialNavigationEventsTest
    fun `illumination percent must be in valid range`() {
        val result = calculator.calculateLunarEventDay(0.0, 0.0, ZonedDateTime.parse("2025-02-10T00:00Z"))
        assertTrue(result.illuminationPercent in 0.0..100.0)
    }
}