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

import static org.junit.Assert.assertEquals;

import com.vitorpamplona.netra.model.db.objects.Refraction;
import com.vitorpamplona.netra.utils.PrescriptionParser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, application = com.vitorpamplona.netra.activity.NetraGTestApplication.class)
public class PrescriptionParserTest {

    @Test
    public void testStandardODOS() {
        PrescriptionParser parser = new PrescriptionParser();
        Refraction r = new Refraction();
        parser.parseRightEyeInto("0.75 0.25 at 45", r);
        parser.parseLeftEyeInto("OS -0.50 -0.75 at 135", r);

        assertEquals("Right Sphere", 0.75, r.getRightSphere(), 0.01);
        assertEquals("Right Cylinder", 0.25, r.getRightCylinder(), 0.01);
        assertEquals("Right Axis", 45, r.getRightAxis(), 0.01);
        assertEquals("Left Sphere", -0.50, r.getLeftSphere(), 0.01);
        assertEquals("Left Cylinder", -0.75, r.getLeftCylinder(), 0.01);
        assertEquals("Left Axis", 135, r.getLeftAxis(), 0.01);
    }

    @Test
    public void testStandard() {
        PrescriptionParser parser = new PrescriptionParser();
        Refraction r = new Refraction();
        parser.parseRightEyeInto("0.75 0.25 at 45 ", r);
        parser.parseLeftEyeInto("-0.50 -0.75 at 135", r);

        assertEquals("Right Sphere", 0.75, r.getRightSphere(), 0.01);
        assertEquals("Right Cylinder", 0.25, r.getRightCylinder(), 0.01);
        assertEquals("Right Axis", 45, r.getRightAxis(), 0.01);
        assertEquals("Left Sphere", -0.50, r.getLeftSphere(), 0.01);
        assertEquals("Left Cylinder", -0.75, r.getLeftCylinder(), 0.01);
        assertEquals("Left Axis", 135, r.getLeftAxis(), 0.01);
    }

    @Test
    public void testStandardSpaces() {
        PrescriptionParser parser = new PrescriptionParser();
        Refraction r = new Refraction();
        parser.parseRightEyeInto("+ 0.75 + 0.25 at 45", r);
        parser.parseLeftEyeInto("- 0.50 - 0.75 at 135", r);

        assertEquals("Right Sphere", 0.75, r.getRightSphere(), 0.01);
        assertEquals("Right Cylinder", 0.25, r.getRightCylinder(), 0.01);
        assertEquals("Right Axis", 45, r.getRightAxis(), 0.01);
        assertEquals("Left Sphere", -0.50, r.getLeftSphere(), 0.01);
        assertEquals("Left Cylinder", -0.75, r.getLeftCylinder(), 0.01);
        assertEquals("Left Axis", 135, r.getLeftAxis(), 0.01);
    }

    @Test
    public void testStandardExtensive() {
        PrescriptionParser parser = new PrescriptionParser();
        Refraction r = new Refraction();
        parser.parseRightEyeInto("+ 0.75 diopters + 0.25 diopters at 45 degrees - 0.50 diopters - 0.75 diopters at 135 degrees", r);

        assertEquals("Right Sphere", 0.75, r.getRightSphere(), 0.01);
        assertEquals("Right Cylinder", 0.25, r.getRightCylinder(), 0.01);
        assertEquals("Right Axis", 45, r.getRightAxis(), 0.01);
        assertEquals("Left Sphere", -0.50, r.getLeftSphere(), 0.01);
        assertEquals("Left Cylinder", -0.75, r.getLeftCylinder(), 0.01);
        assertEquals("Left Axis", 135, r.getLeftAxis(), 0.01);
    }

    @Test
    public void testStandardForgot0() {
        PrescriptionParser parser = new PrescriptionParser();
        Refraction r = new Refraction();
        parser.parseRightEyeInto(".75 .25 at 45", r);
        parser.parseLeftEyeInto("-.50 -.75 at 135", r);

        assertEquals("Right Sphere", 0.75, r.getRightSphere(), 0.01);
        assertEquals("Right Cylinder", 0.25, r.getRightCylinder(), 0.01);
        assertEquals("Right Axis", 45, r.getRightAxis(), 0.01);
        assertEquals("Left Sphere", -0.50, r.getLeftSphere(), 0.01);
        assertEquals("Left Cylinder", -0.75, r.getLeftCylinder(), 0.01);
        assertEquals("Left Axis", 135, r.getLeftAxis(), 0.01);
    }


    @Test
    public void testStandardWrittenNumber() {
        PrescriptionParser parser = new PrescriptionParser();
        Refraction r = new Refraction();
        parser.parseRightEyeInto("point 75 point 25 at 45 minus one sphere", r);

        assertEquals("Right Sphere", 0.75, r.getRightSphere(), 0.01);
        assertEquals("Right Cylinder", 0.25, r.getRightCylinder(), 0.01);
        assertEquals("Right Axis", 45, r.getRightAxis(), 0.01);
        assertEquals("Left Sphere", -1.00, r.getLeftSphere(), 0.01);
        assertEquals("Left Cylinder", 0, r.getLeftCylinder(), 0.01);
        assertEquals("Left Axis", 0, r.getLeftAxis(), 0.01);
    }


}
