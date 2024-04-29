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
package com.vitorpamplona.netra.test;

import static junit.framework.Assert.assertEquals;

import com.vitorpamplona.netra.utils.AcuityFormatter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, application = com.vitorpamplona.netra.activity.NetraGTestApplication.class)
public class AcuityFormatterTest {

    AcuityFormatter formatter = new AcuityFormatter();

    @Test
    public void parseFormatter() {
        assertEquals("20/100", formatter.format(0.2f, AcuityFormatter.ACUITY_TYPE.IMPERIAL));
        assertEquals("20/30", formatter.format(0.666666f, AcuityFormatter.ACUITY_TYPE.IMPERIAL));
        assertEquals("20/20", formatter.format(1f, AcuityFormatter.ACUITY_TYPE.IMPERIAL));
        assertEquals("20/10", formatter.format(2f, AcuityFormatter.ACUITY_TYPE.IMPERIAL));
        assertEquals("6/30", formatter.format(0.2f, AcuityFormatter.ACUITY_TYPE.METRIC));
        assertEquals("6/9", formatter.format(0.666666f, AcuityFormatter.ACUITY_TYPE.METRIC));
        assertEquals("6/6", formatter.format(1f, AcuityFormatter.ACUITY_TYPE.METRIC));
        assertEquals("6/3", formatter.format(2f, AcuityFormatter.ACUITY_TYPE.METRIC));
    }

    @Test
    public void parseTest() {
        assertEquals(0.2, formatter.parse("20/100"), 0.001);
        assertEquals(0.5, formatter.parse("20/40"), 0.001);
        assertEquals(0.6666667, formatter.parse("20/30"), 0.001);
        assertEquals(1.3333334, formatter.parse("20/15"), 0.001);
        assertEquals(1.3561254, formatter.parse("20/15+1"), 0.001);
        assertEquals(1.3789175, formatter.parse("20/15+2"), 0.001);
        assertEquals(1.4017094, formatter.parse("20/15+3"), 0.001);
        assertEquals(1.4472935, formatter.parse("20/15+5"), 0.001);
        assertEquals(1.4700855, formatter.parse("20/15+6"), 0.001);
        assertEquals(1.4928775, formatter.parse("20/15+7"), 0.001);
        assertEquals(1.5384616, formatter.parse("20/15+9"), 0.001);
        assertEquals(1.5384616, formatter.parse("20/13"), 0.001);

        assertEquals(0.2, formatter.parse("6/30"), 0.001);
        assertEquals(0.5, formatter.parse("6/12"), 0.001);
        assertEquals(0.6666667, formatter.parse("6/9"), 0.001);
        assertEquals(1.5, formatter.parse("6/4"), 0.001);
        assertEquals(1.5555555820465088, formatter.parse("6/4+1"), 0.001);
        assertEquals(1.6111111640930176, formatter.parse("6/4+2"), 0.001);
        assertEquals(1.6666666269302368, formatter.parse("6/4+3"), 0.001);
        assertEquals(1.7777777910232544, formatter.parse("6/4+5"), 0.001);
        assertEquals(2.0, formatter.parse("6/3"), 0.001);
    }
}
