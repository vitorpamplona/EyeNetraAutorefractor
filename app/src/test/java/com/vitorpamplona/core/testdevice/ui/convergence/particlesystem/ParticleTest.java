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
package com.vitorpamplona.core.testdevice.ui.convergence.particlesystem;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.vitorpamplona.core.testdevice.Particle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21)
public class ParticleTest {
    @Test
    public void testValidAngles() {
        Particle p = new Particle(0, 0, 15);
        assertFalse(p.isLegalDirection(Math.toRadians(0)));
        assertTrue(p.isLegalDirection(Math.toRadians(26)));
        assertFalse(p.isLegalDirection(Math.toRadians(45)));
        assertTrue(p.isLegalDirection(Math.toRadians(65)));
        assertTrue(p.isLegalDirection(Math.toRadians(90)));
        assertTrue(p.isLegalDirection(Math.toRadians(120)));
        assertFalse(p.isLegalDirection(Math.toRadians(135)));
        assertTrue(p.isLegalDirection(Math.toRadians(160)));
        assertFalse(p.isLegalDirection(Math.toRadians(179)));
    }
}
