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
package com.vitorpamplona.core.testdevice;


import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21)
public class PrismaticEffectTest {

    @Test
    public void testPrism() {
        PrismaticEffect effect = new PrismaticEffect(125, 75, 62);
        Assert.assertEquals(-8.6, effect.testPrismShift(70.1f), 0.1);


        effect = new PrismaticEffect(125, 75, 62);
        Assert.assertEquals(12.8, effect.testPrismShift(50.0f), 0.1);
    }

    @Test
    public void testLocalFocalLength() {
        PrismaticEffect effect = new PrismaticEffect(125, 71, 67, 62);

        Assert.assertEquals(71, effect.localFocalLength(62f), 0.1);
        Assert.assertEquals(67, effect.localFocalLength(62 - 10f), 0.1);
        Assert.assertEquals(67, effect.localFocalLength(62 + 10f), 0.1);

        Assert.assertEquals(69, effect.localFocalLength(62 + 5f), 0.1);
        Assert.assertEquals(69, effect.localFocalLength(62 - 5f), 0.1);

        effect = new PrismaticEffect(125, 75, 62);

        Assert.assertEquals(75, effect.localFocalLength(62f), 0.1);
        Assert.assertEquals(75, effect.localFocalLength(62 - 10f), 0.1);
        Assert.assertEquals(75, effect.localFocalLength(62 + 10f), 0.1);

        Assert.assertEquals(75, effect.localFocalLength(62 + 5f), 0.1);
        Assert.assertEquals(75, effect.localFocalLength(62 - 5f), 0.1);
    }
	
	/*
	@Test
	public void testPrismActualLensMeasurements() {
		PrismaticEffect effect = new PrismaticEffect(125, 75f, 67f, 62);
		
		Assert.assertEquals(0, effect.prismActual75mmLens(62f), 0.1);
		
		Assert.assertEquals(-12, effect.prismActual75mmLens(62+7.3f), 0.1);
		Assert.assertEquals(-7, effect.prismActual75mmLens(62+4.3f), 0.1);
		
		Assert.assertEquals(12, effect.prismActual75mmLens(62-7.3f), 0.1);
		Assert.assertEquals( 7, effect.prismActual75mmLens(62-4.3f), 0.1);
		
		for (int pd = 50; pd < 70; pd++) {
			System.out.println(effect.testPrismShiftActualLens(pd) + " \t " + effect.testPrismShift(pd));
		}
	}*/

}
