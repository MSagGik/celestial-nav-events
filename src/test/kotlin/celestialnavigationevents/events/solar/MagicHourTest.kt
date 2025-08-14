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

import celestialnavigationevents.api.SolarEventsCalculator
import celestialnavigationevents.common.TestUtils
import celestialnavigationevents.internal.solar.SolarCalculatorImpl

/**
 * ⚠️ **Disclaimer:** This test suite provides approximate validation of astronomical event calculations.
 * Due to model simplifications, numerical imprecision, and potential implementation errors,
 * results may slightly deviate from authoritative astronomical sources. Always cross-check
 * critical data when high accuracy is required.
 */
internal class MagicHourTest {

    init {
        TestUtils.uiTestHeader(this@MagicHourTest::class.simpleName)
    }

    private val calculator: SolarEventsCalculator = SolarCalculatorImpl()
}