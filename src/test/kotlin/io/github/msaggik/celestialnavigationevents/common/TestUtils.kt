package io.github.msaggik.celestialnavigationevents.common

import io.github.msaggik.celestialnavigationevents.model.measurement.Time
import java.time.ZonedDateTime
import kotlin.math.abs
import kotlin.text.format

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


private const val TIME_TOLERANCE_MIN_SOLAR = 0.8
private const val AZIMUTH_TOLERANCE_SOLAR = 0.5

private const val TIME_TOLERANCE_MIN_LUNAR = 0.8
private const val AZIMUTH_TOLERANCE_LUNAR = 0.5

private const val UI_LOG_GREEN = "\u001B[92m"
private const val UI_LOG_CYAN_HEADER = "\u001B[36m"
private const val UI_LOG_CYAN = "\u001B[96m"
private const val UI_LOG_MAGENTA = "\u001B[95m"
private const val UI_LOG_BOLD = "\u001B[1m"
private const val UI_LOG_RESET = "\u001B[0m"
private const val UI_LOG_RED = "\u001B[91m"

/**
 * Utility class providing internal assertion methods for validating astronomical event calculations
 * related to the Sun and Moon.
 *
 * These methods are used in unit tests to ensure that computed times and azimuths for solar and
 * lunar events are within acceptable tolerances. It also provides optional formatted console output
 * for visual inspection of test results.
 *
 * ## Tolerances
 * - Solar events:
 *   - Time tolerance: 0.8 minutes (`TIME_TOLERANCE_MIN_SOLAR`)
 *   - Azimuth tolerance: 0.5° (`AZIMUTH_TOLERANCE_SOLAR`)
 * - Lunar events:
 *   - Time tolerance: 0.8 minutes (`TIME_TOLERANCE_MIN_LUNAR`)
 *   - Azimuth tolerance: 0.5° (`AZIMUTH_TOLERANCE_LUNAR`)
 *
 * ## Output
 * Includes methods for printing test headers and formatted result tables using ANSI color codes
 * for enhanced readability in the console output.
 *
 * **Note:** This class is intended for internal testing and is not part of the public API.
 */
internal object TestUtils {

    fun runClassTests(klass: Class<*>): Int {
        val instance = klass.getDeclaredConstructor().newInstance()
        var countRunTests = 0

        for (method in klass.methods) {
            if (method.isAnnotationPresent(CelestialNavigationEventsTest::class.java)) {
                countRunTests++
                try {
                    method.invoke(instance)
                } catch (e: Exception) {
                    val currentTime = ZonedDateTime.now()
                    val timeStr = "%02d:%02d:%02d:%03d".format(
                        currentTime.hour,
                        currentTime.minute,
                        currentTime.second,
                        currentTime.nano / 1_000_000L
                    )

                    val cause = e.cause ?: e
                    val errorMessage = cause.toString().replace("\n", " ")

                    println(
                        "${UI_LOG_CYAN_HEADER}%-16s${UI_LOG_RESET}".format(
                            timeStr
                        ) +
                                "${UI_LOG_CYAN}${klass.simpleName}.${method.name}${UI_LOG_RESET} " +
                                "${UI_LOG_RED}Test failed (${errorMessage})${UI_LOG_RESET}"
                    )
                }
            }
        }
        return countRunTests
    }

    internal fun assertEqualsWithToleranceSolar(
        coordinate: Pair<Double, Double>,
        dateTime: ZonedDateTime,
        expected: Time,
        actual: Time,
        visibleResultTest: Boolean
    ) {
        assertEqualsWithTolerance(
            coordinate = coordinate,
            dateTime = dateTime,
            expected = expected,
            actual = actual,
            timeTolerance = TIME_TOLERANCE_MIN_SOLAR,
            visibleResultTest = visibleResultTest
        )
    }

    internal fun assertAzimuthAroundSolar(
        coordinate: Pair<Double, Double>,
        dateTime: ZonedDateTime,
        actual: Double,
        expected: Double,
        visibleResultTest: Boolean
    ) {
        assertAzimuthAround(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = actual,
            expected = expected,
            azimuthTolerance = AZIMUTH_TOLERANCE_SOLAR,
            visibleResultTest = visibleResultTest
        )
    }

    internal fun assertEqualsWithToleranceLunar(
        coordinate: Pair<Double, Double>,
        dateTime: ZonedDateTime,
        expected: Time,
        actual: Time,
        visibleResultTest: Boolean
    ) {
        assertEqualsWithTolerance(
            coordinate = coordinate,
            dateTime = dateTime,
            expected = expected,
            actual = actual,
            timeTolerance = TIME_TOLERANCE_MIN_LUNAR,
            visibleResultTest = visibleResultTest
        )
    }

    internal fun assertAzimuthAroundLunar(
        coordinate: Pair<Double, Double>,
        dateTime: ZonedDateTime,
        actual: Double,
        expected: Double,
        visibleResultTest: Boolean
    ) {
        assertAzimuthAround(
            coordinate = coordinate,
            dateTime = dateTime,
            actual = actual,
            expected = expected,
            azimuthTolerance = AZIMUTH_TOLERANCE_LUNAR,
            visibleResultTest = visibleResultTest
        )
    }


    private fun assertEqualsWithTolerance(
        coordinate: Pair<Double, Double>,
        dateTime: ZonedDateTime,
        expected: Time,
        actual: Time,
        timeTolerance: Double,
        visibleResultTest: Boolean
    ) {
        if (visibleResultTest) {
            val diff = abs(expected.toTotalMilliseconds() - actual.toTotalMilliseconds())
            val isSuccessResult = diff <= timeTolerance * 60_000L

            uiTestInputTime(
                coordinate = coordinate,
                dateTime = dateTime,
                result = "Time event",
                actual = actual,
                expected = expected,
                tolerance = timeTolerance,
                isSuccessResult = isSuccessResult
            )
        }
    }

    private fun assertAzimuthAround(
        coordinate: Pair<Double, Double>,
        dateTime: ZonedDateTime,
        expected: Double,
        actual: Double,
        azimuthTolerance: Double,
        visibleResultTest: Boolean
    ) {
        if (visibleResultTest) {
            val diff = abs(expected - actual)
            val isSuccessResult = diff <= azimuthTolerance

            uiTestInputDouble(
                coordinate = coordinate,
                dateTime = dateTime,
                result = "Azimuth",
                actual = actual,
                expected = expected,
                tolerance = azimuthTolerance,
                isSuccessResult = isSuccessResult
            )
        }
    }

    fun uiTestHeader(nameClass: String?) {
        println("\n${UI_LOG_BOLD}${UI_LOG_MAGENTA}$nameClass${UI_LOG_RESET}")
    }

    fun uiTestHeaderTable() {
        println(
            "${UI_LOG_CYAN_HEADER}%-16s%-21s%-26s%-14s%-16s%-16s%-12s%-12s${UI_LOG_RESET}".format(
                "Time test",
                "Input coordinate",
                "Input date",
                "Result",
                "Expected",
                "Computed",
                "Δ Error",
                "ε Tolerance"
            )
        )
    }

    private fun uiTestInputTime(
        coordinate: Pair<Double, Double>,
        dateTime: ZonedDateTime,
        result: String,
        expected: Time,
        actual: Time,
        tolerance: Double,
        isSuccessResult: Boolean
    ) {
        val currentTime = ZonedDateTime.now()
        val timeStr = "%02d:%02d:%02d:%03d".format(
            currentTime.hour,
            currentTime.minute,
            currentTime.second,
            currentTime.nano / 1_000_000L
        )
        val colorResult = if (isSuccessResult) UI_LOG_GREEN else UI_LOG_RED
        println(
            "${UI_LOG_CYAN_HEADER}%-16s${UI_LOG_RESET}${UI_LOG_CYAN}%-21s%-26s%-14s%-16s${UI_LOG_RESET}${colorResult}${UI_LOG_BOLD}%-16s%-12s${UI_LOG_RESET}${UI_LOG_CYAN}%-12.5f${UI_LOG_RESET}".format(
                timeStr,
                "${coordinate.first},${coordinate.second}",
                "$dateTime",
                result,
                actual,
                expected,
                Time.fromTotalMilliseconds(abs(expected.toTotalMilliseconds() - actual.toTotalMilliseconds())),
                tolerance
            )
        )
    }

    private fun uiTestInputDouble(
        coordinate: Pair<Double, Double>,
        dateTime: ZonedDateTime,
        result: String,
        expected: Double,
        actual: Double,
        tolerance: Double,
        isSuccessResult: Boolean
    ) {
        val currentTime = ZonedDateTime.now()
        val timeStr = "%02d:%02d:%02d:%03d".format(
            currentTime.hour,
            currentTime.minute,
            currentTime.second,
            currentTime.nano / 1_000_000L
        )
        val colorResult = if (isSuccessResult) UI_LOG_GREEN else UI_LOG_RED
        println(
            "${UI_LOG_CYAN_HEADER}%-16s${UI_LOG_RESET}${UI_LOG_CYAN}%-21s%-26s%-14s%-16.5f${UI_LOG_RESET}${colorResult}${UI_LOG_BOLD}%-16.5f%-12.5f${UI_LOG_RESET}${UI_LOG_CYAN}%-12.5f${UI_LOG_RESET}".format(
                timeStr,
                "${coordinate.first},${coordinate.second}",
                "$dateTime",
                result,
                actual,
                expected,
                abs(expected - actual),
                tolerance
            )
        )
    }
}