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

package celestialnavigationevents.events.solar

import celestialnavigationevents.common.CelestialNavigationEventsTest
import celestialnavigationevents.common.TestUtils
import celestialnavigationevents.internal.solar.SolarCalculatorImpl
import celestialnavigationevents.model.measurement.Time
import java.time.Duration
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

private const val VISIBLE_LOG_TEST = true

/**
 * ⚠️ **Disclaimer:** This test suite provides approximate validation of astronomical event calculations.
 * Due to model simplifications, numerical imprecision, and potential implementation errors,
 * results may slightly deviate from authoritative astronomical sources. Always cross-check
 * critical data when high accuracy is required.
 */
internal class SolarRelativeShortEventTest {

    init {
        TestUtils.uiTestHeader(this@SolarRelativeShortEventTest::class.simpleName)
        TestUtils.uiTestHeaderTable()
    }

    private val calculator = SolarCalculatorImpl()

    @CelestialNavigationEventsTest
    fun `returns sunrise or sunset shortly after given time in equatorial zone before rise`() {
        val coordinate = Pair(0.0, 0.0)
        val dateTime = ZonedDateTime.of(2025, 6, 1, 5, 0, 0, 0, ZoneOffset.ofHours(0))

        val upcomingRelativeShortEvent = calculator.findUpcomingSolarRelativeShortEventDay(coordinate.first, coordinate.second, dateTime)

        assertNotNull(upcomingRelativeShortEvent)

        upcomingRelativeShortEvent?.let { event ->
            TestUtils.assertEqualsWithToleranceSolar(
                coordinate = coordinate,
                dateTime = dateTime,
                actual = Time(hour = 0, min = 54, sec = 17),
                expected = Time.fromTotalMilliseconds(event.timestampMillis),
                visibleResultTest = VISIBLE_LOG_TEST
            )
        }
    }

    @CelestialNavigationEventsTest
    fun `returns sunrise or sunset shortly after given time in equatorial zone after set`() {
        val coordinate = Pair(0.0, 0.0)
        val dateTime = ZonedDateTime.of(2025, 6, 1, 19, 0, 0, 0, ZoneOffset.ofHours(0))

        val upcomingRelativeShortEvent = calculator.findUpcomingSolarRelativeShortEventDay(coordinate.first, coordinate.second, dateTime)

        assertNotNull(upcomingRelativeShortEvent)

        upcomingRelativeShortEvent?.let { event ->
            TestUtils.assertEqualsWithToleranceSolar(
                coordinate = coordinate,
                dateTime = dateTime,
                actual = Time(hour = 10, min = 54, sec = 26),
                expected = Time.fromTotalMilliseconds(event.timestampMillis),
                visibleResultTest = VISIBLE_LOG_TEST
            )
        }
    }

    @CelestialNavigationEventsTest
    fun `returns sunrise or sunset shortly after given time in equatorial zone after rise`() {
        val coordinate = Pair(89.9, 0.0)
        val dateTime = ZonedDateTime.of(2025, 6, 1, 7, 0, 0, 0, ZoneOffset.ofHours(0))

        val upcomingRelativeShortEvent = calculator.findUpcomingSolarRelativeShortEventDay(coordinate.first, coordinate.second, dateTime)

        assertNotNull(upcomingRelativeShortEvent)

        upcomingRelativeShortEvent?.let { event ->
            TestUtils.assertEqualsWithToleranceSolar(
                coordinate = coordinate,
                dateTime = dateTime,
                actual = Time(days = 115, hour = 12, min = 22, sec = 52),
                expected = Time.fromTotalMilliseconds(event.timestampMillis),
                visibleResultTest = VISIBLE_LOG_TEST
            )
        }
    }

    @CelestialNavigationEventsTest
    fun `returns null when no events exist for up to a year (polar night)`() {
        val coordinate = Pair(89.9, 0.0)
        val dateTime = ZonedDateTime.of(2025, 1, 1, 12, 0, 0, 0, ZoneOffset.ofHours(0))

        val upcomingRelativeShortEvent = calculator.findUpcomingSolarRelativeShortEventDay(coordinate.first, coordinate.second, dateTime)

        assertNotNull(upcomingRelativeShortEvent)

        upcomingRelativeShortEvent?.let { event ->
            TestUtils.assertEqualsWithToleranceSolar(
                coordinate = coordinate,
                dateTime = dateTime,
                actual = Time(days = 75, hour = 18, min = 10, sec = 43),
                expected = Time.fromTotalMilliseconds(event.timestampMillis),
                visibleResultTest = VISIBLE_LOG_TEST
            )
        }
    }

    @CelestialNavigationEventsTest
    fun `returns nearest event next day if no events today`() {
        val coordinate = Pair(66.5, 25.7)
        val dateTime = ZonedDateTime.of(2025, 1, 1, 23, 50, 0, 0, ZoneOffset.ofHours(2))

        val upcomingRelativeShortEvent = calculator.findUpcomingSolarRelativeShortEventDay(coordinate.first, coordinate.second, dateTime)

        assertNotNull(upcomingRelativeShortEvent)

        upcomingRelativeShortEvent?.let { event ->
            TestUtils.assertEqualsWithToleranceSolar(
                coordinate = coordinate,
                dateTime = dateTime,
                actual = Time(hour = 11, min = 5, sec = 28),
                expected = Time.fromTotalMilliseconds(event.timestampMillis),
                visibleResultTest = VISIBLE_LOG_TEST
            )
        }
    }

    @CelestialNavigationEventsTest
    fun `returns sunrise in Tokyo before morning time`() {
        val coordinate = Pair(35.68, 139.76)
        val dateTime = ZonedDateTime.of(2025, 6, 10, 2, 0, 0, 0, ZoneOffset.ofHours(9))

        val upcomingRelativeShortEvent = calculator.findUpcomingSolarRelativeShortEventDay(coordinate.first, coordinate.second, dateTime)

        assertNotNull(upcomingRelativeShortEvent)

        upcomingRelativeShortEvent?.let { event ->
            TestUtils.assertEqualsWithToleranceSolar(
                coordinate = coordinate,
                dateTime = dateTime,
                actual = Time(hour = 2, min = 24, sec = 40),
                expected = Time.fromTotalMilliseconds(event.timestampMillis),
                visibleResultTest = VISIBLE_LOG_TEST
            )
        }
    }

    @CelestialNavigationEventsTest
    fun `returns sunset in Buenos Aires in evening`() {
        val coordinate = Pair(-34.6, -58.4)
        val dateTime = ZonedDateTime.of(2025, 6, 10, 17, 30, 0, 0, ZoneOffset.ofHours(-3))

        val upcomingRelativeShortEvent = calculator.findUpcomingSolarRelativeShortEventDay(coordinate.first, coordinate.second, dateTime)

        assertNotNull(upcomingRelativeShortEvent)

        upcomingRelativeShortEvent?.let { event ->
            TestUtils.assertEqualsWithToleranceSolar(
                coordinate = coordinate,
                dateTime = dateTime,
                actual = Time(hour = 0, min = 19, sec = 27),
                expected = Time.fromTotalMilliseconds(event.timestampMillis),
                visibleResultTest = VISIBLE_LOG_TEST
            )
        }
    }

    @CelestialNavigationEventsTest
    fun `timestampMillis is 0 when event is exactly at dateTime`() {
        val coordinate = Pair(0.0, 0.0)
        val dateTime = ZonedDateTime.of(2025, 6, 10, 6, 0, 0, 0, ZoneOffset.ofHours(0))

        val upcomingRelativeShortEvent = calculator.findUpcomingSolarRelativeShortEventDay(coordinate.first, coordinate.second, dateTime)

        assertNotNull(upcomingRelativeShortEvent)

        upcomingRelativeShortEvent?.let { event ->
            TestUtils.assertEqualsWithToleranceSolar(
                coordinate = coordinate,
                dateTime = dateTime,
                actual = Time(hour = 12, min = 3, sec = 10),
                expected = Time.fromTotalMilliseconds(event.timestampMillis),
                visibleResultTest = VISIBLE_LOG_TEST
            )
        }
    }

    @CelestialNavigationEventsTest
    fun `timestampMillis is positive and within 24h for regular day at mid-latitudes`() {
        val coordinate = Pair(51.5, -0.1)
        val dateTime = ZonedDateTime.of(2025, 6, 8, 10, 0, 0, 0, ZoneOffset.ofHours(1))

        val upcomingRelativeShortEvent = calculator.findUpcomingSolarRelativeShortEventDay(coordinate.first, coordinate.second, dateTime)

        assertNotNull(upcomingRelativeShortEvent)

        upcomingRelativeShortEvent?.let { event ->
            TestUtils.assertEqualsWithToleranceSolar(
                coordinate = coordinate,
                dateTime = dateTime,
                actual = Time(hour = 11, min = 14, sec = 55),
                expected = Time.fromTotalMilliseconds(event.timestampMillis),
                visibleResultTest = VISIBLE_LOG_TEST
            )
        }
    }

    @CelestialNavigationEventsTest
    fun `throws exception for invalid latitude`() {
        val coordinate = Pair(-91.0, 0.0)
        val dateTime = ZonedDateTime.of(2025, 1, 1, 0, 0, 0, 0, ZoneOffset.ofHours(0))

        assertFailsWith<IllegalArgumentException> {
            calculator.findUpcomingSolarRelativeShortEventDay(coordinate.first, coordinate.second, dateTime)
        }
    }

    @CelestialNavigationEventsTest
    fun `returns correct event when first event in event list is filtered out`() {
        val coordinate = Pair(30.0, 31.0)
        val dateTime = ZonedDateTime.of(2025, 6, 10, 23, 50, 0, 0, ZoneOffset.ofHours(3))

        val upcomingRelativeShortEvent = calculator.findUpcomingSolarRelativeShortEventDay(coordinate.first, coordinate.second, dateTime)

        assertNotNull(upcomingRelativeShortEvent)

        upcomingRelativeShortEvent?.let { event ->
            TestUtils.assertEqualsWithToleranceSolar(
                coordinate = coordinate,
                dateTime = dateTime,
                actual = Time(hour = 6, min = 4, sec = 21),
                expected = Time.fromTotalMilliseconds(event.timestampMillis),
                visibleResultTest = VISIBLE_LOG_TEST
            )
        }
    }

    @CelestialNavigationEventsTest
    fun `event returned is consistent with event from absolute method`() {
        val coordinate = Pair(48.85, 2.35)
        val dateTime = ZonedDateTime.of(2025, 6, 9, 6, 0, 0, 0, ZoneOffset.ofHours(2))

        val shortEvent = calculator.findUpcomingSolarRelativeShortEventDay(coordinate.first, coordinate.second, dateTime)
        val absEvent = calculator.findUpcomingSolarAbsoluteEventDay(coordinate.first, coordinate.second, dateTime).events.first()

        val expectedMillis = Duration.between(dateTime, absEvent.dateTime).toMillis()

        shortEvent?.let { event ->
            assertTrue(event.timestampMillis == expectedMillis)

            TestUtils.assertEqualsWithToleranceSolar(
                coordinate = coordinate,
                dateTime = dateTime,
                actual = Time(hour = 15, min = 52, sec = 34),
                expected = Time.fromTotalMilliseconds(event.timestampMillis),
                visibleResultTest = VISIBLE_LOG_TEST
            )
        }
    }
}