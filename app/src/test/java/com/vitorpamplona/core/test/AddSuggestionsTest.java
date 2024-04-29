/**
 * Copyright (c) 2024 Vitor Pamplona
 *
 * This program is offered under a commercial and under the AGPL license.
 * For commercial licensing, contact me at vitor@vitorpamplona.com.
 * For AGPL licensing, see below.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * This application has not been clinically tested, approved by or registered in any health agency.
 * Even though this repository grants licenses to use to any person that follow it's license,
 * any clinical or commercial use must additionally follow the laws and regulations of the
 * pertinent jurisdictions. Having a license to use the source code does not imply on having
 * regulatory approvals to use or market any part of this code.
 */
package com.vitorpamplona.core.test;


import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21)
public class AddSuggestionsTest {
    @Test
    public void suggestedAddPowerByAgeOnlyTest() {
        assertEquals("0yrs", 0, AddSuggestions.suggestedAddPowerByAgeOnly(0), 0.01);
        assertEquals("10yrs", 0, AddSuggestions.suggestedAddPowerByAgeOnly(10), 0.01);
        assertEquals("20yrs", 0, AddSuggestions.suggestedAddPowerByAgeOnly(20), 0.01);
        assertEquals("30yrs", 0, AddSuggestions.suggestedAddPowerByAgeOnly(30), 0.01);
        assertEquals("35yrs", 0, AddSuggestions.suggestedAddPowerByAgeOnly(35), 0.01);
        assertEquals("40yrs", 0.5, AddSuggestions.suggestedAddPowerByAgeOnly(40), 0.01);
        assertEquals("42yrs", 1.00, AddSuggestions.suggestedAddPowerByAgeOnly(42), 0.01);
        assertEquals("44yrs", 1.25, AddSuggestions.suggestedAddPowerByAgeOnly(44), 0.01);
        assertEquals("45yrs", 1.50, AddSuggestions.suggestedAddPowerByAgeOnly(45), 0.01);
        assertEquals("48yrs", 1.75, AddSuggestions.suggestedAddPowerByAgeOnly(48), 0.01);
        assertEquals("50yrs", 2.0, AddSuggestions.suggestedAddPowerByAgeOnly(50), 0.01);
        assertEquals("55yrs", 2.5, AddSuggestions.suggestedAddPowerByAgeOnly(55), 0.01);
        assertEquals("60yrs", 2.75, AddSuggestions.suggestedAddPowerByAgeOnly(60), 0.01);
        assertEquals("70yrs", 3.00, AddSuggestions.suggestedAddPowerByAgeOnly(70), 0.01);
        assertEquals("80yrs", 3.00, AddSuggestions.suggestedAddPowerByAgeOnly(80), 0.01);
        assertEquals("90yrs", 3.00, AddSuggestions.suggestedAddPowerByAgeOnly(90), 0.01);
        assertEquals("100yrs", 3.00, AddSuggestions.suggestedAddPowerByAgeOnly(100), 0.01);
        assertEquals("110yrs", 3.00, AddSuggestions.suggestedAddPowerByAgeOnly(110), 0.01);
    }

    @Test
    public void accommodationAmplitudeByAgeTest() {
        assertEquals("0yrs", 15, AddSuggestions.accommodationAmplitudeByAge(0), 0.01);
        assertEquals("10yrs", 13.75, AddSuggestions.accommodationAmplitudeByAge(10), 0.01);
        assertEquals("20yrs", 11, AddSuggestions.accommodationAmplitudeByAge(20), 0.01);
        assertEquals("30yrs", 8.6, AddSuggestions.accommodationAmplitudeByAge(30), 0.01);
        assertEquals("40yrs", 6, AddSuggestions.accommodationAmplitudeByAge(40), 0.01);
        assertEquals("50yrs", 1.75, AddSuggestions.accommodationAmplitudeByAge(50), 0.01);
        assertEquals("60yrs", 1.25, AddSuggestions.accommodationAmplitudeByAge(60), 0.01);
        assertEquals("70yrs", 1.00, AddSuggestions.accommodationAmplitudeByAge(70), 0.01);
        assertEquals("80yrs", 0.75, AddSuggestions.accommodationAmplitudeByAge(80), 0.01);
        assertEquals("90yrs", 0.50, AddSuggestions.accommodationAmplitudeByAge(90), 0.01);
        assertEquals("100yrs", 0.50, AddSuggestions.accommodationAmplitudeByAge(100), 0.01);
        assertEquals("110yrs", 0.50, AddSuggestions.accommodationAmplitudeByAge(110), 0.01);
    }

    @Test
    public void wantToReadAtTest() {
        // From Suko, customer.
        float distance = 0.30f;

        //System.out.println("Want to Read 40: +1.00 = " + AddSuggestions.wantToReadAt(40, distance));
        //System.out.println("Want to Read 42: +1.25 = " + AddSuggestions.wantToReadAt(42, distance));
        //System.out.println("Want to Read 45: +1.50 = " + AddSuggestions.wantToReadAt(45, distance));
        //System.out.println("Want to Read 47: +1.75 = " + AddSuggestions.wantToReadAt(47, distance));
        //System.out.println("Want to Read 50: +2.00 = " + AddSuggestions.wantToReadAt(50, distance));
        //System.out.println("Want to Read 52: +2.25 = " + AddSuggestions.wantToReadAt(52, distance));
        //System.out.println("Want to Read 55: +2.50 = " + AddSuggestions.wantToReadAt(55, distance));
        //System.out.println("Want to Read 57: +2.75 = " + AddSuggestions.wantToReadAt(57, distance));
        //System.out.println("Want to Read 60: +3.00 = " + AddSuggestions.wantToReadAt(60, distance));

        assertEquals("Want to Read 42", +1.25, AddSuggestions.wantToReadAt(42, distance), 0.01);
        assertEquals("Want to Read 45", +1.50, AddSuggestions.wantToReadAt(45, distance), 0.01);
        assertEquals("Want to Read 47", +1.75, AddSuggestions.wantToReadAt(47, distance), 0.01);
        assertEquals("Want to Read 50", +2.00, AddSuggestions.wantToReadAt(50, distance), 0.01);
        assertEquals("Want to Read 52", +2.25, AddSuggestions.wantToReadAt(52, distance), 0.01);
        assertEquals("Want to Read 55", +2.50, AddSuggestions.wantToReadAt(55, distance), 0.01);
        assertEquals("Want to Read 57", +2.50, AddSuggestions.wantToReadAt(57, distance), 0.01);
        assertEquals("Want to Read 60", +2.75, AddSuggestions.wantToReadAt(60, distance), 0.01);

        distance = 0.30f;

        assertEquals("Want to Read 30", 0, AddSuggestions.wantToReadAt(30, distance), 0.01);
        assertEquals("Want to Read 35", 0.00, AddSuggestions.wantToReadAt(35, distance), 0.01);
        assertEquals("Want to Read 38", 0.50, AddSuggestions.wantToReadAt(38, distance), 0.01);
        assertEquals("Want to Read 40", 0.75, AddSuggestions.wantToReadAt(40, distance), 0.01);
        assertEquals("Want to Read 41", 1.00, AddSuggestions.wantToReadAt(41, distance), 0.01);
        assertEquals("Want to Read 42", 1.25, AddSuggestions.wantToReadAt(42, distance), 0.01);
        assertEquals("Want to Read 43", 1.25, AddSuggestions.wantToReadAt(43, distance), 0.01);
        assertEquals("Want to Read 44", 1.50, AddSuggestions.wantToReadAt(44, distance), 0.01);
        assertEquals("Want to Read 45", 1.50, AddSuggestions.wantToReadAt(45, distance), 0.01);
        assertEquals("Want to Read 46", 1.50, AddSuggestions.wantToReadAt(46, distance), 0.01);
        assertEquals("Want to Read 47", 1.75, AddSuggestions.wantToReadAt(47, distance), 0.01);
        assertEquals("Want to Read 48", 2.00, AddSuggestions.wantToReadAt(48, distance), 0.01);
        assertEquals("Want to Read 49", 2.00, AddSuggestions.wantToReadAt(49, distance), 0.01);
        assertEquals("Want to Read 50", 2.00, AddSuggestions.wantToReadAt(50, distance), 0.01);
        assertEquals("Want to Read 55", 2.50, AddSuggestions.wantToReadAt(55, distance), 0.01);
        assertEquals("Want to Read 60", 2.75, AddSuggestions.wantToReadAt(60, distance), 0.01);
        assertEquals("Want to Read 70", 2.75, AddSuggestions.wantToReadAt(70, distance), 0.01);
        assertEquals("Want to Read 80", 3.00, AddSuggestions.wantToReadAt(80, distance), 0.01);

        distance = 0.17f;

        assertEquals("Want to Read 30", 0, AddSuggestions.wantToReadAt(30, distance), 0.01);
        assertEquals("Want to Read 35", 0, AddSuggestions.wantToReadAt(35, distance), 0.01);
        assertEquals("Want to Read 38", 0.50, AddSuggestions.wantToReadAt(38, distance), 0.01);
        assertEquals("Want to Read 40", 0.75, AddSuggestions.wantToReadAt(40, distance), 0.01);
        assertEquals("Want to Read 41", 1.25, AddSuggestions.wantToReadAt(41, distance), 0.01);
        assertEquals("Want to Read 42", 1.75, AddSuggestions.wantToReadAt(42, distance), 0.01);
        assertEquals("Want to Read 43", 2.50, AddSuggestions.wantToReadAt(43, distance), 0.01);
        assertEquals("Want to Read 44", 3.00, AddSuggestions.wantToReadAt(44, distance), 0.01);
        assertEquals("Want to Read 45", 3.50, AddSuggestions.wantToReadAt(45, distance), 0.01);
        assertEquals("Want to Read 46", 4.00, AddSuggestions.wantToReadAt(46, distance), 0.01);
        assertEquals("Want to Read 47", 4.50, AddSuggestions.wantToReadAt(47, distance), 0.01);
        assertEquals("Want to Read 48", 4.75, AddSuggestions.wantToReadAt(48, distance), 0.01);
        assertEquals("Want to Read 49", 4.75, AddSuggestions.wantToReadAt(49, distance), 0.01);
        assertEquals("Want to Read 50", 4.75, AddSuggestions.wantToReadAt(50, distance), 0.01);
        assertEquals("Want to Read 55", 5.00, AddSuggestions.wantToReadAt(55, distance), 0.01);
        assertEquals("Want to Read 60", 5.50, AddSuggestions.wantToReadAt(60, distance), 0.01);
        assertEquals("Want to Read 70", 5.50, AddSuggestions.wantToReadAt(70, distance), 0.01);
        assertEquals("Want to Read 80", 5.50, AddSuggestions.wantToReadAt(80, distance), 0.01);

        distance = 0.45f;

        assertEquals("Want to Read 30", 0, AddSuggestions.wantToReadAt(30, distance), 0.01);
        assertEquals("Want to Read 35", 0, AddSuggestions.wantToReadAt(35, distance), 0.01);
        assertEquals("Want to Read 38", 0.50, AddSuggestions.wantToReadAt(38, distance), 0.01);
        assertEquals("Want to Read 40", 0.75, AddSuggestions.wantToReadAt(40, distance), 0.01);
        assertEquals("Want to Read 41", 1.00, AddSuggestions.wantToReadAt(41, distance), 0.01);
        assertEquals("Want to Read 42", 1.25, AddSuggestions.wantToReadAt(42, distance), 0.01);
        assertEquals("Want to Read 43", 1.25, AddSuggestions.wantToReadAt(43, distance), 0.01);
        assertEquals("Want to Read 44", 1.50, AddSuggestions.wantToReadAt(44, distance), 0.01);
        assertEquals("Want to Read 45", 1.50, AddSuggestions.wantToReadAt(45, distance), 0.01);
        assertEquals("Want to Read 46", 1.50, AddSuggestions.wantToReadAt(46, distance), 0.01);
        assertEquals("Want to Read 47", 1.75, AddSuggestions.wantToReadAt(47, distance), 0.01);
        assertEquals("Want to Read 48", 1.50, AddSuggestions.wantToReadAt(48, distance), 0.01);
        assertEquals("Want to Read 49", 1.00, AddSuggestions.wantToReadAt(49, distance), 0.01);
        assertEquals("Want to Read 50", 1.00, AddSuggestions.wantToReadAt(50, distance), 0.01);
        assertEquals("Want to Read 55", 1.25, AddSuggestions.wantToReadAt(55, distance), 0.01);
        assertEquals("Want to Read 60", 1.75, AddSuggestions.wantToReadAt(60, distance), 0.01);
        assertEquals("Want to Read 70", 1.75, AddSuggestions.wantToReadAt(70, distance), 0.01);
        assertEquals("Want to Read 80", 1.75, AddSuggestions.wantToReadAt(80, distance), 0.01);

    }
}
