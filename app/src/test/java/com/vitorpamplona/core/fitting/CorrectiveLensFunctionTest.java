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
package com.vitorpamplona.core.fitting;


import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21)
public class CorrectiveLensFunctionTest {

    @Test
    public void testEvaluationFunction() {
        AstigmaticLensFunction func = new AstigmaticLensFunction();

        double[] presc = new double[3];
        presc[AstigmaticLensFunction.SPHERICAL] = -5;
        presc[AstigmaticLensFunction.CYLINDRICAL] = -2;
        presc[AstigmaticLensFunction.AXIS] = 20;

        double[] tests = new double[1];

        tests[0] = 10;
        assertEquals(-5.06f, func.val(tests, presc), 0.01);

        tests[0] = 20;
        assertEquals(-5f, func.val(tests, presc), 0.01);

        tests[0] = 90;
        assertEquals(-6.76f, func.val(tests, presc), 0.01);

        tests[0] = 180;
        assertEquals(-5.23f, func.val(tests, presc), 0.01);

        tests[0] = 360;
        assertEquals(-5.23f, func.val(tests, presc), 0.01);
    }

}
