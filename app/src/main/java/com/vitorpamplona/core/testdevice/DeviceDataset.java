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

import com.vitorpamplona.core.test.ComputePD;

import java.io.Serializable;
import java.util.Arrays;
import java.util.NavigableMap;
import java.util.TreeMap;

public class DeviceDataset implements Serializable {

    public enum CalibrationType {
        HARDCODED, // manually hardcoded
        STATIC,    // autocalibration (at beginning of test)
        DYNAMIC    // autocalibration (during test)
    }

    public enum PreviewFrameSize { // valid resolutions for Galaxy S4
        R1920x1080(1920, 1080),
        R1440x1080(1440, 1080),
        R1280x720(1280, 720),
        R1056x864(1056, 864),
        R960x720(960, 720),
        R720x480(720, 480),
        R640x480(640, 480),
        R320x240(320, 240),
        R176x144(176, 144);

        public final int WIDTH;
        public final int HEIGHT;

        PreviewFrameSize(int width, int height) {
            this.WIDTH = width;
            this.HEIGHT = height;
        }
    }

    private static final long serialVersionUID = 1L;

    private static NavigableMap<Long, Device> DEVICES = new TreeMap<Long, Device>() {{
        // 201 - 3D printed MVP with APD
        put(1l, new Device(1l, "3D Print          ", "MVP", "LaunchPad", 102.5f, 100f, 15f, 1.2f, 10f, 60f, 1.0f, 1.0f, -12.0f, Device.RIGHT, Device.PD_IN_KNOB, Device.ACCURACY_MOVING_AB, Device.EYES_ONE_AT_A_TIME, 0, 3, true, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 202 - Nice 3D printed MVP with APD
        put(2l, new Device(2l, "3D Print          ", "MVP", "LaunchPad", 103.0f, 100f, 15f, 1.2f, 10f, 60f, 1.0f, 1.0f, -12.0f, Device.RIGHT, Device.PD_IN_KNOB, Device.ACCURACY_MOVING_AB, Device.EYES_ONE_AT_A_TIME, 0, 3, true, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 203 - SLA MVP with APD
        put(203l, new Device(203l, "Nice SLA          ", "MVP", "LaunchPad", 102.5f, 100f, 15f, 1.2f, 10f, 60f, 1.0f, 1.0f, -12.0f, Device.RIGHT, Device.PD_IN_KNOB, Device.ACCURACY_MOVING_AB, Device.EYES_ONE_AT_A_TIME, 0, 3, true, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 204 - Alien with Ruler on top for PD.
        put(204l, new Device(204l, "3D Print          ", "Alien", "LaunchPad", 100.8f, 100f, 15f, 1.2f, 10f, 62f, 1.0f, 1.0f, -12.0f, Device.RIGHT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_ONE_AT_A_TIME, 20, 3, true, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 205 - SLA MVP with APD
        put(5l, new Device(5l, "Nice SLA          ", "MVP", "LaunchPad", 103.5f, 100f, 15f, 1.2f, 10f, 60f, 1.0f, 1.0f, -12.0f, Device.RIGHT, Device.PD_IN_KNOB, Device.ACCURACY_MOVING_AB, Device.EYES_ONE_AT_A_TIME, 0, 3, true, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 207 - Nice 3D printed MVP with APD
        put(207l, new Device(207l, "3D Print          ", "Alien", "LaunchPad", 101.5f, 100f, 15f, 1.2f, 10f, 62f, 1.0f, 1.0f, -12.0f, Device.RIGHT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_ONE_AT_A_TIME, 20, 3, true, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 208 - SLA MVP with APD
        put(208l, new Device(208l, "Nice SLA          ", "MVP", "LaunchPad", 103.5f, 100f, 15f, 1.2f, 10f, 60f, 1.0f, 1.0f, -12.0f, Device.RIGHT, Device.PD_IN_KNOB, Device.ACCURACY_MOVING_AB, Device.EYES_ONE_AT_A_TIME, 0, 3, true, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 209 - Nice 3D printed MVP with APD
        put(209l, new Device(209l, "3D Print White", "MVP", "LaunchPad", 103.5f, 100f, 15f, 1.2f, 10f, 60f, 1.0f, 1.0f, -12.0f, Device.RIGHT, Device.PD_IN_KNOB, Device.ACCURACY_MOVING_AB, Device.EYES_ONE_AT_A_TIME, 0, 3, true, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 210 - Nice 3D printed MVP with APD
        put(210l, new Device(210l, "3D Print White", "MVP", "LaunchPad", 102.5f, 100f, 15f, 1.2f, 10f, 60f, 1.0f, 1.0f, -12.0f, Device.RIGHT, Device.PD_IN_KNOB, Device.ACCURACY_MOVING_AB, Device.EYES_ONE_AT_A_TIME, 0, 3, true, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 211 - First Motor-based design
        put(211l, new Device(211l, "3D Print Motor", "Alien", "LaunchPad", 999.9f, 999f, 15f, 1.2f, 10f, 62f, 1.0f, 1.0f, -12.0f, Device.RIGHT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_ONE_AT_A_TIME, 20, 3, true, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 212 - Nice 3D printed MVP with APD
        put(212l, new Device(212l, "3D Print          ", "Alien", "LaunchPad", 101.5f, 100f, 15f, 1.2f, 10f, 62f, 1.0f, 1.0f, -12.0f, Device.RIGHT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_ONE_AT_A_TIME, 20, 3, true, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 213 - First Hyperopia Try.
        put(213l, new Device(213l, "3D Print Hyper", "Alien", "LaunchPad", 124.0f, 80f, 25f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.RIGHT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_ONE_AT_A_TIME, 20, 3, true, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 214 - 70mm APD alien device
        put(214l, new Device(214l, "3D Print          ", "Alien", "LaunchPad", 99.0f, 100f, 15f, 1.2f, 10f, 70f, 1.0f, 1.0f, -12.0f, Device.RIGHT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_ONE_AT_A_TIME, 20, 3, true, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 215 - of Hyperopia 151mm .
        put(215l, new Device(215l, "3D Print Hyper", "Alien", "LaunchPad", 151.0f, 100f, 15f, 1.2f, 10f, 70f, 4.5f, 5.5f, -12.0f, Device.RIGHT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_ONE_AT_A_TIME, 20, 3, true, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 216 - First Hyperopia with APD.
        put(216l, new Device(216l, "3D Print Hyper", "MVP", "LaunchPad", 151.0f, 100f, 15f, 1.2f, 10f, 60f, 4.5f, 5.5f, -12.0f, Device.RIGHT, Device.PD_IN_KNOB, Device.ACCURACY_MOVING_AB, Device.EYES_ONE_AT_A_TIME, 0, 3, true, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 217 - Stronger lens for Hyperopia with APD and Knob.
        put(217l, new Device(217l, "3D Print White", "MVP", "LaunchPad", 105.0f, 75f, 15f, 1.2f, 10f, 60f, 4.5f, 5.5f, -12.0f, Device.RIGHT, Device.PD_IN_KNOB, Device.ACCURACY_MOVING_AB, Device.EYES_ONE_AT_A_TIME, 0, 3, true, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 218 - First 123mm Alien and 80mm lens for hyperopia.
        put(218l, new Device(218l, "3D Print White", "Alien", "LaunchPad", 123.0f, 80f, 15f, 1.2f, 10f, 70f, 4.5f, 5.5f, -12.0f, Device.RIGHT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_ONE_AT_A_TIME, 20, 3, true, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 219 - MVP through all angles to test
        put(219l, new Device(219l, "3D Print         ", "MVPAll", "LaunchPad", 103.0f, 100f, 15f, 1.2f, 10f, 60f, 1.0f, 1.0f, -12.0f, Device.RIGHT, Device.PD_IN_KNOB, Device.ACCURACY_MOVING_AB, Device.EYES_ONE_AT_A_TIME, 0, 3, true, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 220 - Alien through all angles to test
        put(220l, new Device(220l, "3D Print Hyper", "AlienAll", "LaunchPad", 124.0f, 80f, 15f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.RIGHT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_ONE_AT_A_TIME, 20, 3, true, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 221 - First Hyperopia design
        put(221l, new Device(221l, "3D Print Hyper", "Alien", "LaunchPad", 121.0f, 80f, 25f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.RIGHT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_ONE_AT_A_TIME, 20, 3, true, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 222 - Myopia only alpha design
        put(222l, new Device(222l, "3D Print", "Alien", "LaunchPad", 101.0f, 100f, 25f, 1.2f, 10f, 62f, 1.0f, 1.0f, -12.0f, Device.RIGHT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_ONE_AT_A_TIME, 20, 3, true, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 223 - No PD Measurement Alpha design
        put(223l, new Device(223l, "3D Print", "Alien", "LaunchPad", 101.5f, 100f, 25f, 1.2f, 10f, 62f, 1.0f, 1.0f, -12.0f, Device.RIGHT, Device.PD_NO_MEASUREMENT, Device.ACCURACY_MOVING_AB, Device.EYES_ONE_AT_A_TIME, 20, 3, true, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 224 - Monocular device tentative.
        put(224l, new Device(224l, "3D Print", "Monoc", "LaunchPad", 101.5f, 100f, 10f, 1.2f, 10f, 62f, 1.0f, 1.0f, -12.0f, Device.RIGHT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_ONE_AT_A_TIME, 20, 3, true, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 225 - First alien using magnets for the rotational
        put(225l, new Device(225l, "3D Print/Magnet", "Alien", "LaunchPad", 118.0f, 80f, 25f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.RIGHT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_ONE_AT_A_TIME, 20, 3, true, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 226 - First Joe's Alpha Design.
        put(226l, new Device(226l, "Designy BE", "Alpha", "LaunchPad", 121.0f, 80f, 30f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_ONE_AT_A_TIME, 20, 3, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 227 - Second Joe's Alpha Design
        put(227l, new Device(227l, "Designy BE", "Alpha", "LaunchPad", 121.8f, 80f, 30f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_ONE_AT_A_TIME, 20, 3, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 228 - First Both Eyes At the same time.
        put(228l, new Device(228l, "Designy BE", "Alpha", "LaunchPad", 121.8f, 80f, 30f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_BOTH_SAME_TIME, 20, 1, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // First Alpha Device with Urethane Design - Just one angle and A&B to the rest.
        put(229l, new Device(229l, "Urethane BE", "Alpha", "LaunchPad", 122.5f, 80f, 30f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_BOTH_SAME_TIME, 20, 1, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 230 - A little eyeshield improvement.
        put(230l, new Device(230l, "3D Print BE", "Alpha", "LaunchPad", 124.0f, 80f, 25f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.RIGHT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_BOTH_SAME_TIME, 20, 1, true, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 10 Alpha Devices.
        put(231l, new Device(231l, "Urethane BE", "Alpha", "LaunchPad", 122.5f, 80f, 30f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_BOTH_SAME_TIME, 20, 1, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        put(232l, new Device(232l, "Urethane SE", "Alpha", "LaunchPad", 122.5f, 80f, 30f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_ONE_AT_A_TIME, 20, 3, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        put(233l, new Device(233l, "Urethane BE", "Alpha", "LaunchPad", 122.5f, 80f, 30f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_BOTH_SAME_TIME, 20, 1, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        put(234l, new Device(234l, "Urethane BE", "Alpha", "LaunchPad", 122.5f, 80f, 30f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_BOTH_SAME_TIME, 20, 1, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        put(235l, new Device(235l, "Urethane BE", "Alpha", "LaunchPad", 122.5f, 80f, 30f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_BOTH_SAME_TIME, 20, 1, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        put(236l, new Device(236l, "Urethane BE", "Alpha", "LaunchPad", 122.5f, 80f, 30f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_BOTH_SAME_TIME, 20, 1, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        put(237l, new Device(237l, "Urethane BE", "Alpha", "LaunchPad", 122.5f, 80f, 30f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_BOTH_SAME_TIME, 20, 1, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        put(238l, new Device(238l, "Urethane BE", "Alpha", "LaunchPad", 122.5f, 80f, 30f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_BOTH_SAME_TIME, 20, 1, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        put(239l, new Device(239l, "Urethane BE", "Alpha", "LaunchPad", 122.5f, 80f, 30f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_BOTH_SAME_TIME, 20, 1, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        put(240l, new Device(240l, "Urethane BE", "Alpha", "LaunchPad", 122.5f, 80f, 30f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_BOTH_SAME_TIME, 20, 8, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 242 - Alpha with Audio Buttons to control the lines
        put(241l, new Device(241l, "3D Print", "Alpha-Buttons", "LaunchPad", 122.5f, 80f, 30f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_BOTH_SAME_TIME, 20, 8, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 242 - Alpha with Audio Potentiometer to control the lines
        put(242l, new Device(242l, "3D Print", "Alpha-AudPot", "LaunchPad", 122.5f, 80f, 30f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_BOTH_SAME_TIME, 20, 8, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 243 - First functional Mouse Wheel device
        put(243l, new Device(243l, "3D Print", "Alpha-Mouse", "LaunchPad", 122.8f, 80f, 20f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_BOTH_SAME_TIME, 20, 1, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 244 - First kids design.
        put(244l, new Device(244l, "3D Print", "Alpha-Kids", "LaunchPad", 125.8f, 75f, 20f, 1.2f, 10f, 45f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_BOTH_SAME_TIME, 20, 8, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 245 - same as but 233 with just moving
        put(245l, new Device(245l, "Urethane BE", "Alpha", "LaunchPad", 122.5f, 80f, 30f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_BOTH_SAME_TIME, 20, 8, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 10 Alpha Devices.
        put(246l, new Device(246l, "Urethane BE", "Alpha", "LaunchPad", 122.5f, 80f, 24f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_BOTH_SAME_TIME, 20, 1, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        put(247l, new Device(247l, "Urethane BE", "Alpha", "LaunchPad", 122.5f, 80f, 30f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_BOTH_SAME_TIME, 20, 1, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        put(248l, new Device(248l, "Urethane BE", "Alpha", "LaunchPad", 122.5f, 80f, 30f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_BOTH_SAME_TIME, 20, 1, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        put(249l, new Device(249l, "Urethane BE", "Alpha", "LaunchPad", 122.5f, 80f, 30f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_BOTH_SAME_TIME, 20, 1, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        put(250l, new Device(250l, "Urethane BE", "Alpha", "LaunchPad", 122.5f, 80f, 30f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_BOTH_SAME_TIME, 20, 1, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        put(251l, new Device(251l, "Urethane BE", "Alpha", "LaunchPad", 122.5f, 80f, 30f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_BOTH_SAME_TIME, 20, 1, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        put(252l, new Device(252l, "Urethane BE", "Alpha", "LaunchPad", 122.5f, 80f, 30f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_BOTH_SAME_TIME, 20, 1, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        put(253l, new Device(253l, "Urethane BE", "Alpha", "LaunchPad", 122.5f, 80f, 30f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_BOTH_SAME_TIME, 20, 1, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        put(254l, new Device(254l, "Urethane BE", "Alpha", "LaunchPad", 122.5f, 80f, 30f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_BOTH_SAME_TIME, 20, 1, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        put(255l, new Device(255l, "Urethane BE", "Alpha", "LaunchPad", 122.5f, 80f, 30f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_BOTH_SAME_TIME, 20, 1, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 256 - First Mouse Wheel
        put(256l, new Device(256l, "3D Print", "Alpha-Mouse", "LaunchPad", 123.0f, 80f, 20f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_BOTH_SAME_TIME, 20, 8, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 257 - Same as 236 but with just moving
        put(257l, new Device(257l, "Urethane BE", "Alpha", "LaunchPad", 122.5f, 80f, 30f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_BOTH_SAME_TIME, 20, 8, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 258 - Same as 249 with just moving.
        put(258l, new Device(258l, "Urethane BE", "Alpha", "LaunchPad", 122.5f, 80f, 30f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_BOTH_SAME_TIME, 20, 8, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 259 - Beautiful Aplha Mouse Wheel
        put(259l, new Device(259l, "3D Print", "Alpha-Mouse", "LaunchPad", 122.8f, 80f, 30f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_BOTH_SAME_TIME, 20, 1, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 260 - 440 Display design
        put(260l, new Device(260l, "3D Print", "Alpha-Xperia", "LaunchPad", 122.8f, 80f, 30f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_BOTH_SAME_TIME, 20, 1, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 261 - 35mm kids version
        put(261l, new Device(261l, "3D Print", "Alpha-35mmlens", "LaunchPad", 125.8f, 75f, 20f, 1.2f, 10f, 62f, 5.0f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_BOTH_SAME_TIME, 20, 1, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 263 - VVDN Device
        // 264 - Electrical Sensors via Audio
        put(264l, new Device(264l, "3D Print", "Alpha-35mmlens", "LaunchPad", 122.8f, 75f, 20f, 1.2f, 10f, 62f, 5.0f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_MOVING_AB, Device.EYES_BOTH_SAME_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_AUDIO, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 265 - Motor-based design
        // 266 - 440 Display design
        put(267l, new Device(267l, "3D Print", "Alpha-Mouse-Button", "LaunchPad", 122.8f, 80f, 20f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.RIGHT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_BOTH_SAME_TIME, 20, 1, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // Closer slits
        put(268l, new Device(268l, "3D Print", "Alpha-Xperia", "LaunchPad", 121.0f, 80f, 30f, 0.85f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_BOTH_SAME_TIME, 20, 1, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        put(269l, new Device(269l, "3D Print", "Alpha-Xperia", "LaunchPad", 121.8f, 80f, 30f, 0.85f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_BOTH_SAME_TIME, 20, 1, false, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // Ratchet Design with Front Face Camera Tracking
        put(270l, new Device(270l, "3D Print", "Ratchet", "LaunchPad", 125.8f, 80f, 15f, 1.2f, 51.4285f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_OPENCV, Device.MASK_ROTATE_AUTO));
        // 271 - 3 mirror design
        // 272 - 2 mirror periscope design
        // 273 - 2 mirror penta design 135mm
        // 274 - MVP for 5 Angles
        put(274l, new Device(274l, "3D Print         ", "MVPAll", "LaunchPad", 103.0f, 100f, 15f, 1.2f, 10f, 60f, 1.0f, 5.5f, -12.0f, Device.RIGHT, Device.PD_IN_KNOB, Device.ACCURACY_MOVING_AB, Device.EYES_ONE_AT_A_TIME, 0, 3, true, null, Device.DEVICE_TYPE_DEFAULT, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // 275 - New Peta design 125mm
        // 276 - Ratchet Design with Front Face Camera Tracking
        put(276l, new Device(276l, "3D Print", "Ratchet", "LaunchPad", 129.0f, 75f, 15f, 0.85f, 51.42f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_OPENCV, Device.MASK_ROTATE_AUTO));
        // 277 - Ratchet Design with Calibration points and 8 angles
        put(277l, new Device(277l, "3D Print", "Ratchet", "LaunchPad", 128.0f, 75f, 15f, 0.85f, 22.5f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_OPENCV, Device.MASK_ROTATE_AUTO));
        // 278 - Ratchet Design with Calibration points and 8 angles
        put(278l, new Device(278l, "3D Print", "Ratchet", "LaunchPad", 128.0f, 75f, 15f, 0.85f, 22.5f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_OPENCV, Device.MASK_ROTATE_AUTO, 16.50, new Point2D(1.50, -21.50), 3.0, 8.7, new Point2D(1.45, -47.20), 4.3, new Point2D(-24.5, -43.00), new Point2D(-13, -39.00), new Point2D(15 * 2, 225 * 2), new Point2D(150 * 2, 240 * 2), 10, 5.0, CalibrationType.HARDCODED, 0, PreviewFrameSize.R640x480, 0));
        // 279 - Ratchet Design with calibration points in two eyes and 8 angles.
        // put(279l, new Device(279l,  "3D Print",     "Ratchet",      "LaunchPad",            128.0f,  75f, 15f, 0.85f, 22.5f, 62f, 4.5f,  Device.LEFT,  Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_OPENCV, Device.MASK_ROTATE_AUTO, 16.50, ,new Point2D(13.03,-20.56), 3.0, 10.00, new Point2D(13.00,-45.70), 8.2, new Point2D(-13.5,-43.00), new Point2D(1,-39.00), new Point2D(15,225), new Point2D(150,240), 8, 5.0));
        put(279l, new Device(279l, "3D Print", "Ratchet", "LaunchPad", 128.0f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_OPENCV, Device.MASK_ROTATE_AUTO, 16.60, new Point2D(14.50, -21.0), 3.5, 10.00, new Point2D(13.5, -46.50), 8.2, new Point2D(-14.5, -45.00), new Point2D(4, -41.00), new Point2D(15 * 2, 215 * 2), new Point2D(75 * 2, 240 * 2), 1, 5.0, CalibrationType.HARDCODED, 0, PreviewFrameSize.R640x480, 0));
        // 278 reprinted with small changes.
        put(280l, new Device(280l, "3D Print", "Ratchet", "LaunchPad", 128.0f, 75f, 15f, 0.85f, 22.5f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_OPENCV, Device.MASK_ROTATE_AUTO, 16.60, new Point2D(13.03, -21.50), 3.5, 10.00, new Point2D(13.00, -46.50), 8.2, new Point2D(-13.5, -44.00), new Point2D(1, -40.00), new Point2D(15 * 2, 225 * 2), new Point2D(150 * 2, 240 * 2), 10, 5.0, CalibrationType.HARDCODED, 0, PreviewFrameSize.R640x480, 0));
        // 279 reprinted with small changes.
        put(281l, new Device(281l, "3D Print", "Ratchet", "LaunchPad", 128.0f, 75f, 15f, 0.85f, 22.5f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_OPENCV, Device.MASK_ROTATE_AUTO, 16.60, new Point2D(13.53, -22.00), 3.0, 10.00, new Point2D(13.50, -49.00), 8.2, new Point2D(-14.0, -46.00), new Point2D(0.5, -42.00), new Point2D(15 * 2, 225 * 2), new Point2D(150 * 2, 240 * 2), 10, 5.0, CalibrationType.HARDCODED, 0, PreviewFrameSize.R640x480, 0));
        // 282 - 227 with mouse wheel.
        put(282l, new Device(282l, "Designy BE", "Alpha", "LaunchPad", 121.8f, 80f, 30f, 1.2f, 10f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 3, false, null, Device.DEVICE_TYPE_SCROLL_BLUETOOTH, Device.MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN));
        // reprint of the 281.
        put(283l, new Device(283l, "3D Print", "Ratchet", "LaunchPad", 128.0f, 75f, 15f, 0.85f, 22.5f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_OPENCV, Device.MASK_ROTATE_AUTO, 16.60, new Point2D(13.53, -22.00), 3.0, 10.00, new Point2D(13.50, -49.00), 8.2, new Point2D(-14.0, -46.00), new Point2D(0.5, -42.00), new Point2D(15 * 2, 225 * 2), new Point2D(150 * 2, 240 * 2), 10, 5.0, CalibrationType.HARDCODED, 0, PreviewFrameSize.R640x480, 0));
        // reprint of the 281.
        put(284l, new Device(284l, "3D Print", "Ratchet", "LaunchPad", 130.0f, 75f, 15f, 0.85f, 22.5f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_OPENCV, Device.MASK_ROTATE_AUTO, 16.90, new Point2D(13.93, -22.50), 3.0, 10.00, new Point2D(14.50, -49.70), 8.2, new Point2D(-13.0, -47.00), new Point2D(0.5, -43.00), new Point2D(15 * 2, 220 * 2), new Point2D(150 * 2, 240 * 2), 5, 5.0, CalibrationType.HARDCODED, 0, PreviewFrameSize.R640x480, 0));
        // Inverting the phone
        put(285l, new Device(285l, "3D Print", "Ratchet", "LaunchPad", 130.0f, 75f, 15f, 0.85f, 22.5f, 62f, 4.5f, 5.5f, -12.0f, Device.RIGHT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_OPENCV, Device.MASK_ROTATE_AUTO, 16.30, new Point2D(13.20, 17.80), 3.0, 10.00, new Point2D(14.00, 47.00), 8.2, new Point2D(-13.0, 39.00), new Point2D(0.5, 43.00), new Point2D(0 * 2, 115 * 2), new Point2D(150 * 2, 125 * 2), 10, 3.0, CalibrationType.HARDCODED, 0, PreviewFrameSize.R640x480, 0));

        // Reprint 284

        // Joe
        put(286l, new Device(286l, "3D Print", "Ratchet", "LaunchPad", 129.0f, 75f, 15f, 0.85f, 22.5f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_OPENCV, Device.MASK_ROTATE_AUTO, 16.50, new Point2D(8.00, -21.50), 3.0, 10.00, new Point2D(8.50, -47.70), 8.2, new Point2D(-18.0, -45.00), new Point2D(-5, -43.00), new Point2D(0 * 2, 215 * 2), new Point2D(130 * 2, 230 * 2), 6, 12.35, CalibrationType.HARDCODED, 0, PreviewFrameSize.R640x480, 0));

        // Ze
        put(287l, new Device(287l, "3D Print", "Ratchet", "LaunchPad", 129.0f, 75f, 15f, 0.85f, 22.5f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_OPENCV, Device.MASK_ROTATE_AUTO, 16.50, new Point2D(8.00, -21.50), 3.0, 10.00, new Point2D(7.50, -47.70), 8.2, new Point2D(-18.0, -46.00), new Point2D(-5, -42.00), new Point2D(0 * 2, 230 * 2), new Point2D(130 * 2, 280 * 2), 10, 12.35, CalibrationType.HARDCODED, 0, PreviewFrameSize.R640x480, 0));

        // Francio & Dennis
        put(288l, new Device(288l, "3D Print", "Ratchet", "LaunchPad", 128.0f, 75f, 15f, 0.85f, 22.5f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_OPENCV, Device.MASK_ROTATE_AUTO, 16.00, new Point2D(8.00, -21.00), 3.0, 10.00, new Point2D(8.50, -46.70), 8.2, new Point2D(-18.0, -46.00), new Point2D(-5, -42.00), new Point2D(0 * 2, 225 * 2), new Point2D(130 * 2, 280 * 2), 7, 12.35, CalibrationType.HARDCODED, 0, PreviewFrameSize.R640x480, 0));

        // Alice
        put(289l, new Device(289l, "3D Print", "Ratchet", "LaunchPad", 128.0f, 75f, 15f, 0.85f, 22.5f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_OPENCV, Device.MASK_ROTATE_AUTO, 16.50, new Point2D(8.00, -22.00), 3.0, 10.00, new Point2D(8.50, -48.00), 8.2, new Point2D(-18.0, -46.00), new Point2D(-5, -42.00), new Point2D(0 * 2, 223 * 2), new Point2D(130 * 2, 280 * 2), 10, 12.35, CalibrationType.HARDCODED, 0, PreviewFrameSize.R640x480, 0));

        // Francio (testing new in-house image processing code)
        put(290l, new Device(290l, "3D Print", "Ratchet", "LaunchPad", 128.0f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_OPENCV, Device.MASK_ROTATE_AUTO, 16.60, new Point2D(14.50, -21.0), 3.5, 10.00, new Point2D(13.5, -46.50), 8.2, new Point2D(-14.5, -45.00), new Point2D(4, -41.00), new Point2D(5 * 2, 215 * 2), new Point2D(75 * 2, 240 * 2), 0, 5.0, CalibrationType.HARDCODED, 0, PreviewFrameSize.R640x480, 0));

        // Vitor's S4: Eyeshield slits - lateral calibration points.
        put(291l, new Device(291l, "3D Print", "Ratchet", "LaunchPad", 129.0f, 75f, 15f, 0.85f, 22.5f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_OPENCV, Device.MASK_ROTATE_AUTO, 15.50, new Point2D(-26.50, -1.30), 3.0, 10.00, new Point2D(-26.50, -26.50), 8.2, new Point2D(-52.0, -25.00), new Point2D(-39.5, -20.00), new Point2D(140 * 2, 0 * 2), new Point2D(180 * 2, 600 * 2), 50, 12.35, CalibrationType.HARDCODED, 0, PreviewFrameSize.R640x480, 0));
        // Vitor's S4
        put(292l, new Device(292l, "3D Print", "Ratchet", "LaunchPad", 129.0f, 75f, 15f, 0.85f, 22.5f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_OPENCV, Device.MASK_ROTATE_AUTO, 16.00, new Point2D(-26.50, -1.30), 3.0, 10.00, new Point2D(-26.50, -26.50), 8.2, new Point2D(-52.0, -25.00), new Point2D(-39.5, -20.00), new Point2D(140 * 2, 0 * 2), new Point2D(180 * 2, 600 * 2), 50, 12.35, CalibrationType.HARDCODED, 0, PreviewFrameSize.R640x480, 0));
        // Vitor's S4
        put(293l, new Device(293l, "3D Print", "Ratchet", "LaunchPad", 129.0f, 75f, 15f, 0.85f, 22.5f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_OPENCV, Device.MASK_ROTATE_AUTO, 16.20, new Point2D(-26.80, -1.30), 3.0, 10.00, new Point2D(-27.10, -27.50), 8.2, new Point2D(-53.0, -25.00), new Point2D(-40.0, -20.00), new Point2D(140 * 2, 0 * 2), new Point2D(180 * 2, 600 * 2), 40, 12.35, CalibrationType.HARDCODED, 0, PreviewFrameSize.R640x480, 0));

        // Former 286 - Paired with Joe (testing new in-house image processing code)
        put(294l, new Device(294l, "3D Print", "Ratchet", "LaunchPad", 127.0f, 75f, 15f, 0.85f, 22.5f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE, Device.MASK_ROTATE_AUTO, 80, new Point2D(145, 335), 8, 40, new Point2D(145, 198), 4, new Point2D(1, 205), new Point2D(71, 225), new Point2D(1, 440), new Point2D(251, 458), 0, 12.35, CalibrationType.HARDCODED, 0, PreviewFrameSize.R640x480, 0));

        // Former 288 - Paired with Dennis (testing new in-house image processing code)
        put(295l, new Device(295l, "3D Print", "Ratchet", "LaunchPad", 128.0f, 75f, 15f, 0.85f, 22.5f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE, Device.MASK_ROTATE_AUTO, 79, new Point2D(170, 358), 10, 40, new Point2D(172, 217), 4, new Point2D(31, 225), new Point2D(101, 245), new Point2D(1, 460), new Point2D(251, 478), 0, 12.35, CalibrationType.HARDCODED, 0, PreviewFrameSize.R640x480, 0));

        // Former 289 - Paired with Alice (testing new in-house image processing code)
        put(296l, new Device(296l, "3D Print", "Ratchet", "LaunchPad", 128.0f, 75f, 15f, 0.85f, 22.5f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE, Device.MASK_ROTATE_AUTO, 79, new Point2D(175, 350), 10, 40, new Point2D(177, 209), 4, new Point2D(33, 220), new Point2D(103, 240), new Point2D(1, 455), new Point2D(251, 473), 0, 12.35, CalibrationType.HARDCODED, 0, PreviewFrameSize.R640x480, 0));

        // 297 STATIONARY NEXT BUTTON with Vitor's IP on Noa.
        put(297l, new Device(297l, "3D Print", "Ratchet", "LaunchPad", 127.0f, 75f, 15f, 0.85f, 22.5f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_OPENCV, Device.MASK_ROTATE_AUTO, 16.20, new Point2D(-26.80, -1.00), 3.0, 10.00, new Point2D(-27.10, -27.00), 8.2, new Point2D(-53.0, -25.00), new Point2D(-40.4, -20.00), new Point2D(140 * 2, 0 * 2), new Point2D(180 * 2, 600 * 2), 50, 12.35, CalibrationType.HARDCODED, 0, PreviewFrameSize.R640x480, 0));

        // Former 291 - Paired with Judah (testing new in-house image processing code)
        put(298l, new Device(298l, "3D Print", "Ratchet", "LaunchPad", 129.0f, 75f, 15f, 0.85f, 22.5f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE, Device.MASK_ROTATE_AUTO, 85, new Point2D(125, 335), 8, 40, new Point2D(145, 198), 4, new Point2D(1, 205), new Point2D(81, 225), new Point2D(1, 440), new Point2D(251, 458), 0, 12.35, CalibrationType.HARDCODED, 0, PreviewFrameSize.R640x480, 0));

        // 299 STATIONARY NEXT BUTTON with Vitor's IP on Vitor's S4.
        put(299l, new Device(299l, "3D Print", "Ratchet", "LaunchPad", 127.0f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_OPENCV, Device.MASK_ROTATE_AUTO, 16.60, new Point2D(-27.50, -1.00), 3.0, 10.00, new Point2D(-27.50, -27.50), 8.2, new Point2D(-52.5, -26.00), new Point2D(-41.4, -20.00), new Point2D(140 * 2, 0 * 2), new Point2D(180 * 2, 600 * 2), 40, 12.35, CalibrationType.HARDCODED, 0, PreviewFrameSize.R640x480, 0));

        // Former 299 - Paired with Amy (testing new in-house image processing code)
        put(300l, new Device(300l, "3D Print", "Ratchet", "LaunchPad", 127.0f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE, Device.MASK_ROTATE_AUTO, 80, new Point2D(153, 364), 8, 24, new Point2D(159, 225), 4, new Point2D(15, 236), new Point2D(92, 254), new Point2D(307, 220), new Point2D(287, 470), 0, 12.35, CalibrationType.HARDCODED, 0, PreviewFrameSize.R640x480, 0));

        // Paired with Disco
        put(301l, new Device(301l, "3D Print", "Ratchet", "LaunchPad", 127.0f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE, Device.MASK_ROTATE_AUTO, 105, new Point2D(133, 378), 8, 56, new Point2D(190, 200), 4, new Point2D(20, 230), new Point2D(100, 248), new Point2D(360, 229), new Point2D(340, 479), 0, 12.35, CalibrationType.HARDCODED, 0, PreviewFrameSize.R640x480, 0));

        // Former 298 - Testing static autocalibration
        put(302l, new Device(302l, "3D Print", "Ratchet", "LaunchPad", 128.0f, 75f, 15f, 0.85f, 22.5f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE, Device.MASK_ROTATE_AUTO, 85, new Point2D(125, 335), 8, 24, new Point2D(145, 198), 4, new Point2D(1, 205), new Point2D(81, 225), new Point2D(9, 0), new Point2D(0, 9), 0, 12.35, CalibrationType.STATIC, 20, PreviewFrameSize.R640x480, 0));

        // Testing static autocalibration (Bizarro)
        put(303l, new Device(303l, "3D Print", "Ratchet", "LaunchPad", 129.0f, 75f, 15f, 0.85f, 22.5f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE, Device.MASK_ROTATE_AUTO, 85, new Point2D(125, 335), 8, 24, new Point2D(145, 198), 4, new Point2D(1, 205), new Point2D(81, 225), new Point2D(9, 0), new Point2D(0, 9), 0, 12.35, CalibrationType.STATIC, 15, PreviewFrameSize.R640x480, 0));

        // Testing static autocalibration (Smurfette)
        put(304l, new Device(304l, "3D Print", "Ratchet", "LaunchPad", 127.0f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE, Device.MASK_ROTATE_AUTO, 105, new Point2D(133, 378), 8, 32, new Point2D(190, 200), 4, new Point2D(20, 230), new Point2D(100, 248), new Point2D(9, 0), new Point2D(0, 9), 0, 12.35, CalibrationType.STATIC, 16, PreviewFrameSize.R640x480, 0));

        // Testing static autocalibration (Storm Trooper)
        put(305l, new Device(305l, "3D Print", "Ratchet", "LaunchPad", 127.0f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE, Device.MASK_ROTATE_AUTO, 105, new Point2D(133, 378), 8, 32, new Point2D(190, 200), 4, new Point2D(20, 230), new Point2D(100, 248), new Point2D(9, 0), new Point2D(0, 9), 0, 12.35, CalibrationType.STATIC, 16, PreviewFrameSize.R640x480, 0));

        // Testing static autocalibration (Argentina)
        put(306l, new Device(306l, "3D Print", "MVP-1", "LaunchPad", 127.0f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE, Device.MASK_ROTATE_AUTO, 105, new Point2D(133, 378), 8, 32, new Point2D(190, 200), 4, new Point2D(20, 230), new Point2D(100, 248), new Point2D(9, 0), new Point2D(0, 9), 0, 13.40, CalibrationType.STATIC, 16, PreviewFrameSize.R720x480, 0));

        // Testing static autocalibration (The Shining)
        put(307l, new Device(307l, "3D Print", "MVP-3", "LaunchPad", 126.0f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE, Device.MASK_ROTATE_AUTO, 105, new Point2D(133, 378), 8, 32, new Point2D(190, 200), 4, new Point2D(20, 230), new Point2D(100, 248), new Point2D(9, 0), new Point2D(0, 9), 0, 12.35, CalibrationType.STATIC, 0, PreviewFrameSize.R720x480, 0));

        // 283 testing in house camera
        put(308l, new Device(308l, "3D Print", "Ratchet", "LaunchPad", 128.0f, 75f, 15f, 0.85f, 22.5f, 62f, 4.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE, Device.MASK_ROTATE_AUTO, 80, new Point2D(224, 355), 8, 40, new Point2D(224, 215), 4, new Point2D(85, 230), new Point2D(155, 250), new Point2D(1, 460), new Point2D(251, 478), 0, 5.0, CalibrationType.HARDCODED, 0, PreviewFrameSize.R720x480, 0));

        // Testing static autocalibration (Panda)
        put(309l, new Device(309l, "3D Print", "MVP-2", "LaunchPad", 127.0f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE_MVP, Device.MASK_ROTATE_AUTO, 105, new Point2D(133, 378), 8, 32, new Point2D(190, 200), 4, new Point2D(20, 230), new Point2D(100, 248), new Point2D(9, 0), new Point2D(0, 9), 0, 12.35, CalibrationType.STATIC, 0, PreviewFrameSize.R720x480, 0));

        // Testing static autocalibration
        put(310l, new Device(310l, "3D Print", "MVP-4", "LaunchPad", 127.0f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE_MVP, Device.MASK_ROTATE_AUTO, 105, new Point2D(133, 378), 8, 32, new Point2D(190, 200), 4, new Point2D(20, 230), new Point2D(100, 248), new Point2D(9, 0), new Point2D(0, 9), 0, 12.35, CalibrationType.STATIC, 0, PreviewFrameSize.R720x480, 0));

        // Getting 279 to run with in-house optical recognition
        put(311l, new Device(311l, "3D Print", "Ratchet", "LaunchPad", 128.0f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE, Device.MASK_ROTATE_AUTO, 80, new Point2D(224, 355), 8, 40, new Point2D(224, 215), 4, new Point2D(80, 230), new Point2D(150, 250), new Point2D(1, 460), new Point2D(251, 478), 0, 0.25, CalibrationType.HARDCODED, 0, PreviewFrameSize.R720x480, 0));

        // Testing static autocalibration
        put(312l, new Device(312l, "3D Print", "MVP-5", "LaunchPad", 128.5f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE_MVP, Device.MASK_ROTATE_AUTO, 105, new Point2D(133, 378), 8, 32, new Point2D(190, 200), 4, new Point2D(20, 230), new Point2D(100, 248), new Point2D(9, 0), new Point2D(0, 9), 0, 12.35, CalibrationType.STATIC, 0, PreviewFrameSize.R720x480, 0));

        // Testing static autocalibration
        put(313l, new Device(313l, "3D Print", "MVP-6", "LaunchPad", 128.5f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE_MVP, Device.MASK_ROTATE_AUTO, 105, new Point2D(133, 378), 8, 32, new Point2D(190, 200), 4, new Point2D(20, 230), new Point2D(100, 248), new Point2D(9, 0), new Point2D(0, 9), 0, 12.35, CalibrationType.STATIC, 0, PreviewFrameSize.R720x480, 0));

        // Inverted the sliding and the hatchet positions
        put(314l, new Device(314l, "3D Print", "MVP", "LaunchPad", 127.0f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE_MVP, Device.MASK_ROTATE_AUTO, 105, new Point2D(133, 378), 8, 32, new Point2D(190, 200), 4, new Point2D(20, 230), new Point2D(100, 248), new Point2D(9, 0), new Point2D(0, 9), 0, 12.35, CalibrationType.STATIC, 0, PreviewFrameSize.R720x480, 0));

        // Testing static autocalibration
        //		  id,      what,         model,            where,               tube, fl, lensEye, slit, angle,  pd,  def, +lim,  -lim,  screenX,screenY, phoneOri,  pdMeasurement,      accuracyTechnique,           eyesAtATimeTechnique, pdRuler, m, clear, viewToRun,  deviceType,                         rotationalInteraction,  ratRad, deltaCalib,     Thick, scrRad, deltaCalib,  scrThick, topLeftFromCalib,   bottomRigthFromCalib, calibTopLeft,  calibBottomRight, intThres, distCalibMarks, calibrationType, sliderOffset, previewFrameSize
        put(315l, new Device(315l, "3D Print", "MVP-7", "LaunchPad", 128.5f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE_MVP, Device.MASK_ROTATE_AUTO, 105, new Point2D(133, 378), 8, 32, new Point2D(190, 200), 4, new Point2D(20, 230), new Point2D(100, 248), new Point2D(9, 0), new Point2D(0, 9), 0, 13.40, CalibrationType.STATIC, 0, PreviewFrameSize.R720x480, 0));

        //		  id,      what,         model,            where,               tube, fl, lensEye, slit, angle,  pd,  def, +lim,  -lim,  screenX,screenY, phoneOri,  pdMeasurement,      accuracyTechnique,           eyesAtATimeTechnique, pdRuler, m, clear, viewToRun,  deviceType,                         rotationalInteraction,  ratRad, deltaCalib,     Thick, scrRad, deltaCalib,  scrThick, topLeftFromCalib,   bottomRigthFromCalib, calibTopLeft,  calibBottomRight, intThres, distCalibMarks, calibrationType, sliderOffset, previewFrameSize
        put(316l, new Device(316l, "3D Print", "MVP-8", "LaunchPad", 128.5f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE_MVP, Device.MASK_ROTATE_AUTO, 105, new Point2D(133, 378), 8, 32, new Point2D(190, 200), 4, new Point2D(20, 230), new Point2D(100, 248), new Point2D(9, 0), new Point2D(0, 9), 0, 13.40, CalibrationType.STATIC, 0, PreviewFrameSize.R720x480, 0));

        //		  id,      what,         model,            where,               tube, fl, lensEye, slit, angle,  pd,  def, +lim,  -lim,  screenX,screenY, phoneOri,  pdMeasurement,      accuracyTechnique,           eyesAtATimeTechnique, pdRuler, m, clear, viewToRun,  deviceType,                         rotationalInteraction,  ratRad, deltaCalib,     Thick, scrRad, deltaCalib,  scrThick, topLeftFromCalib,   bottomRigthFromCalib, calibTopLeft,  calibBottomRight, intThres, distCalibMarks, calibrationType, sliderOffset, previewFrameSize
        put(317l, new Device(317l, "3D Print", "Beta-1", "LaunchPad", 128.5f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE_MANTA, Device.MASK_ROTATE_AUTO, 105, new Point2D(133, 378), 8, 32, new Point2D(190, 200), 4, new Point2D(20, 230), new Point2D(100, 248), new Point2D(9, 0), new Point2D(0, 9), 10, 36.0, CalibrationType.STATIC, 0, PreviewFrameSize.R720x480, 0));

        //		  id,      what,         model,            where,               tube, fl, lensEye, slit, angle,  pd,  def, +lim,  -lim,  screenX,screenY, phoneOri,  pdMeasurement,      accuracyTechnique,           eyesAtATimeTechnique, pdRuler, m, clear, viewToRun,  deviceType,                         rotationalInteraction,  ratRad, deltaCalib,     Thick, scrRad, deltaCalib,  scrThick, topLeftFromCalib,   bottomRigthFromCalib, calibTopLeft,  calibBottomRight, intThres, distCalibMarks, calibrationType, sliderOffset, previewFrameSize
        put(318l, new Device(318l, "3D Print", "Beta-2", "LaunchPad", 128.5f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE_MANTA, Device.MASK_ROTATE_AUTO, 105, new Point2D(133, 378), 8, 32, new Point2D(190, 200), 4, new Point2D(20, 230), new Point2D(100, 248), new Point2D(9, 0), new Point2D(0, 9), 10, 36.0, CalibrationType.STATIC, 0, PreviewFrameSize.R720x480, -4));

        //		  id,      what,         model,            where,               tube, fl, lensEye, slit, angle,  pd,  def, +lim,  -lim,  screenX,screenY, phoneOri,  pdMeasurement,      accuracyTechnique,           eyesAtATimeTechnique, pdRuler, m, clear, viewToRun,  deviceType,                         rotationalInteraction,  ratRad, deltaCalib,     Thick, scrRad, deltaCalib,  scrThick, topLeftFromCalib,   bottomRigthFromCalib, calibTopLeft,  calibBottomRight, intThres, distCalibMarks, calibrationType, sliderOffset, previewFrameSize
        put(319l, new Device(319l, "3D Print", "MVP-Ind-1", "LaunchPad", 128.5f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE_MVP, Device.MASK_ROTATE_AUTO, 105, new Point2D(133, 378), 8, 32, new Point2D(190, 200), 4, new Point2D(20, 230), new Point2D(100, 248), new Point2D(9, 0), new Point2D(0, 9), 10, 13.40, CalibrationType.STATIC, 0, PreviewFrameSize.R720x480, 0));

        //		  id,      what,         model,            where,               tube, fl, lensEye, slit, angle,  pd,  def, +lim,  -lim,  screenX,screenY, phoneOri,  pdMeasurement,      accuracyTechnique,           eyesAtATimeTechnique, pdRuler, m, clear, viewToRun,  deviceType,                         rotationalInteraction,  ratRad, deltaCalib,     Thick, scrRad, deltaCalib,  scrThick, topLeftFromCalib,   bottomRigthFromCalib, calibTopLeft,  calibBottomRight, intThres, distCalibMarks, calibrationType, sliderOffset, previewFrameSize
        put(320l, new Device(320l, "3D Print", "Beta-3", "LaunchPad", 128.5f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE_MANTA, Device.MASK_ROTATE_AUTO, 105, new Point2D(133, 378), 8, 32, new Point2D(190, 200), 4, new Point2D(20, 230), new Point2D(100, 248), new Point2D(9, 0), new Point2D(0, 9), 10, 36.0, CalibrationType.STATIC, 0, PreviewFrameSize.R720x480, -4));   // 320  (?)

        //		  id,      what,         model,            where,               tube, fl, lensEye, slit, angle,  pd,  def, +lim,  -lim,  screenX,screenY, phoneOri,  pdMeasurement,      accuracyTechnique,           eyesAtATimeTechnique, pdRuler, m, clear, viewToRun,  deviceType,                         rotationalInteraction,  ratRad, deltaCalib,     Thick, scrRad, deltaCalib,  scrThick, topLeftFromCalib,   bottomRigthFromCalib, calibTopLeft,  calibBottomRight, intThres, distCalibMarks, calibrationType, sliderOffset, previewFrameSize
        put(321l, new Device(321l, "3D Print", "Beta-4", "LaunchPad", 128.5f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE_MANTA, Device.MASK_ROTATE_AUTO, 105, new Point2D(133, 378), 8, 32, new Point2D(190, 200), 4, new Point2D(20, 230), new Point2D(100, 248), new Point2D(9, 0), new Point2D(0, 9), 10, 6.5, CalibrationType.STATIC, 0, PreviewFrameSize.R720x480, 12));   // 321

        //		  id,      what,         model,            where,               tube, fl, lensEye, slit, angle,  pd,  def, +lim,  -lim,  screenX,screenY, phoneOri,  pdMeasurement,      accuracyTechnique,           eyesAtATimeTechnique, pdRuler, m, clear, viewToRun,  deviceType,                         rotationalInteraction,  ratRad, deltaCalib,     Thick, scrRad, deltaCalib,  scrThick, topLeftFromCalib,   bottomRigthFromCalib, calibTopLeft,  calibBottomRight, intThres, distCalibMarks, calibrationType, sliderOffset, previewFrameSize
        put(322l, new Device(322l, "3D Print", "Beta-5", "LaunchPad", 128.5f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE_MANTA, Device.MASK_ROTATE_AUTO, 105, new Point2D(133, 378), 8, 32, new Point2D(190, 200), 4, new Point2D(20, 230), new Point2D(100, 248), new Point2D(9, 0), new Point2D(0, 9), 10, 6.5, CalibrationType.STATIC, 0, PreviewFrameSize.R720x480, 16));   // 322

        //		  id,      what,         model,            where,               tube, fl, lensEye, slit, angle,  pd,  def, +lim,  -lim,  screenX,screenY, phoneOri,  pdMeasurement,      accuracyTechnique,           eyesAtATimeTechnique, pdRuler, m, clear, viewToRun,  deviceType,                         rotationalInteraction,  ratRad, deltaCalib,     Thick, scrRad, deltaCalib,  scrThick, topLeftFromCalib,   bottomRigthFromCalib, calibTopLeft,  calibBottomRight, intThres, distCalibMarks, calibrationType, sliderOffset, previewFrameSize
        put(323l, new Device(323l, "3D Print", "Beta-6", "LaunchPad", 128.5f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE_MANTA, Device.MASK_ROTATE_AUTO, 105, new Point2D(133, 378), 8, 32, new Point2D(190, 200), 4, new Point2D(20, 230), new Point2D(100, 248), new Point2D(9, 0), new Point2D(0, 9), 10, 6.5, CalibrationType.STATIC, 0, PreviewFrameSize.R720x480, 12));  // 323

        //		  id,      what,         model,            where,               tube, fl, lensEye, slit, angle,  pd,  def, +lim,  -lim,  screenX,screenY, phoneOri,  pdMeasurement,      accuracyTechnique,           eyesAtATimeTechnique, pdRuler, m, clear, viewToRun,  deviceType,                         rotationalInteraction,  ratRad, deltaCalib,     Thick, scrRad, deltaCalib,  scrThick, topLeftFromCalib,   bottomRigthFromCalib, calibTopLeft,  calibBottomRight, intThres, distCalibMarks, calibrationType, sliderOffset, previewFrameSize
        put(324l, new Device(324l, "3D Print", "Beta-7", "LaunchPad", 128.5f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE_MANTA, Device.MASK_ROTATE_AUTO, 105, new Point2D(133, 378), 8, 32, new Point2D(190, 200), 4, new Point2D(20, 230), new Point2D(100, 248), new Point2D(9, 0), new Point2D(0, 9), 10, 6.5, CalibrationType.STATIC, 0, PreviewFrameSize.R720x480, 16));   // 324

        //		  id,      what,         model,            where,               tube, fl, lensEye, slit, angle,  pd,  def, +lim,  -lim,  screenX,screenY, phoneOri,  pdMeasurement,      accuracyTechnique,           eyesAtATimeTechnique, pdRuler, m, clear, viewToRun,  deviceType,                         rotationalInteraction,  ratRad, deltaCalib,     Thick, scrRad, deltaCalib,  scrThick, topLeftFromCalib,   bottomRigthFromCalib, calibTopLeft,  calibBottomRight, intThres, distCalibMarks, calibrationType, sliderOffset, previewFrameSize
        put(325l, new Device(325l, "3D Print", "Beta-8", "LaunchPad", 128.5f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INJECTION, Device.MASK_ROTATE_AUTO, 105, new Point2D(133, 378), 8, 32, new Point2D(190, 200), 4, new Point2D(20, 230), new Point2D(100, 248), new Point2D(9, 0), new Point2D(0, 9), 10, 6.5, CalibrationType.STATIC, 0, PreviewFrameSize.R720x480, 16));   // 325

        //		  id,      what,         model,            where,               tube, fl, lensEye, slit, angle,  pd,  def, +lim,  -lim,  screenX,screenY, phoneOri,  pdMeasurement,      accuracyTechnique,           eyesAtATimeTechnique, pdRuler, m, clear, viewToRun,  deviceType,                         rotationalInteraction,  ratRad, deltaCalib,     Thick, scrRad, deltaCalib,  scrThick, topLeftFromCalib,   bottomRigthFromCalib, calibTopLeft,  calibBottomRight, intThres, distCalibMarks, calibrationType, sliderOffset, previewFrameSize
        put(326l, new Device(326l, "3D Print", "Beta-9", "LaunchPad", 128.5f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INJECTION, Device.MASK_ROTATE_AUTO, 105, new Point2D(133, 378), 8, 32, new Point2D(190, 200), 4, new Point2D(20, 230), new Point2D(100, 248), new Point2D(9, 0), new Point2D(0, 9), 10, 6.5, CalibrationType.STATIC, 0, PreviewFrameSize.R720x480, 16));   // 326

        //		  id,      what,         model,            where,               tube, fl, lensEye, slit, angle,  pd,  def, +lim,  -lim,  screenX,screenY, phoneOri,  pdMeasurement,      accuracyTechnique,           eyesAtATimeTechnique, pdRuler, m, clear, viewToRun,  deviceType,                         rotationalInteraction,  ratRad, deltaCalib,     Thick, scrRad, deltaCalib,  scrThick, topLeftFromCalib,   bottomRigthFromCalib, calibTopLeft,  calibBottomRight, intThres, distCalibMarks, calibrationType, sliderOffset, previewFrameSize
        put(327l, new Device(327l, "3D Print", "Beta-Ind-1", "LaunchPad", 128.5f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INJECTION, Device.MASK_ROTATE_AUTO, 105, new Point2D(133, 378), 8, 32, new Point2D(190, 200), 4, new Point2D(20, 230), new Point2D(100, 248), new Point2D(9, 0), new Point2D(0, 9), 10, 6.5, CalibrationType.STATIC, 0, PreviewFrameSize.R720x480, 16));   // 327

        //		  id,      what,         model,            where,               tube, fl, lensEye, slit, angle,  pd,  def, +lim,  -lim,  screenX,screenY, phoneOri,  pdMeasurement,      accuracyTechnique,           eyesAtATimeTechnique, pdRuler, m, clear, viewToRun,  deviceType,                         rotationalInteraction,  ratRad, deltaCalib,     Thick, scrRad, deltaCalib,  scrThick, topLeftFromCalib,   bottomRigthFromCalib, calibTopLeft,  calibBottomRight, intThres, distCalibMarks, calibrationType, sliderOffset, previewFrameSize
        // 328 unused

        //		  id,      what,         model,            where,               tube, fl, lensEye, slit, angle,  pd,  def, +lim,  -lim,  screenX,screenY, phoneOri,  pdMeasurement,      accuracyTechnique,           eyesAtATimeTechnique, pdRuler, m, clear, viewToRun,  deviceType,                         rotationalInteraction,  ratRad, deltaCalib,     Thick, scrRad, deltaCalib,  scrThick, topLeftFromCalib,   bottomRigthFromCalib, calibTopLeft,  calibBottomRight, intThres, distCalibMarks, calibrationType, sliderOffset, previewFrameSize
        // 329 unused

        //		  id,      what,         model,            where,               tube, fl, lensEye, slit, angle,  pd,  def, +lim,  -lim,  screenX,screenY, phoneOri,  pdMeasurement,      accuracyTechnique,           eyesAtATimeTechnique, pdRuler, m, clear, viewToRun,  deviceType,                         rotationalInteraction,  ratRad, deltaCalib,     Thick, scrRad, deltaCalib,  scrThick, topLeftFromCalib,   bottomRigthFromCalib, calibTopLeft,  calibBottomRight, intThres, distCalibMarks, calibrationType, sliderOffset, previewFrameSize
        put(330l, new Device(330l, "3D Print", "Beta-10", "LaunchPad", 128.5f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INJECTION, Device.MASK_ROTATE_AUTO, 105, new Point2D(133, 378), 8, 32, new Point2D(190, 200), 4, new Point2D(20, 230), new Point2D(100, 248), new Point2D(9, 0), new Point2D(0, 9), 10, 6.5, CalibrationType.STATIC, 0, PreviewFrameSize.R720x480, 16));   // 330

        //		  id,      what,         model,            where,               tube, fl, lensEye, slit, angle,  pd,  def, +lim,  -lim,  screenX,screenY, phoneOri,  pdMeasurement,      accuracyTechnique,           eyesAtATimeTechnique, pdRuler, m, clear, viewToRun,  deviceType,                         rotationalInteraction,  ratRad, deltaCalib,     Thick, scrRad, deltaCalib,  scrThick, topLeftFromCalib,   bottomRigthFromCalib, calibTopLeft,  calibBottomRight, intThres, distCalibMarks, calibrationType, sliderOffset, previewFrameSize
        put(331l, new Device(331l, "3D Print", "Beta-11", "LaunchPad", 128.5f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INJECTION, Device.MASK_ROTATE_AUTO, 105, new Point2D(133, 378), 8, 32, new Point2D(190, 200), 4, new Point2D(20, 230), new Point2D(100, 248), new Point2D(9, 0), new Point2D(0, 9), 10, 6.5, CalibrationType.STATIC, 0, PreviewFrameSize.R720x480, 16));   // 331


        ///// EAGLES START HERE /////


        //		  id,      what,         model,            where,               tube, fl, lensEye, slit, angle,  pd,  def, +lim,  -lim,  screenX,screenY, phoneOri,  pdMeasurement,      accuracyTechnique,           eyesAtATimeTechnique, pdRuler, m, clear, viewToRun,  deviceType,                         rotationalInteraction,  ratRad, deltaCalib,     Thick, scrRad, deltaCalib,  scrThick, topLeftFromCalib,   bottomRigthFromCalib, calibTopLeft,  calibBottomRight, intThres, distCalibMarks, calibrationType, sliderOffset, previewFrameSize
        put(332l, new Device(332l, "Injection", "Eagle-v1.00-#1", "LaunchPad", 128.5f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INJECTION, Device.MASK_ROTATE_AUTO, 105, new Point2D(133, 378), 8, 32, new Point2D(190, 200), 4, new Point2D(20, 230), new Point2D(100, 248), new Point2D(9, 0), new Point2D(0, 9), 10, 6.5, CalibrationType.STATIC, 0, PreviewFrameSize.R720x480, 12));   // 332


        // Enabling Myopia DEMO MODE Only
        //		  id,      what,         model,            where,               tube, fl, lensEye, slit, angle,  pd,  def, +lim,  -lim,  screenX,screenY, phoneOri,  pdMeasurement,      accuracyTechnique,           eyesAtATimeTechnique, pdRuler, m, clear, viewToRun,  deviceType,                         rotationalInteraction,  ratRad, deltaCalib,     Thick, scrRad, deltaCalib,  scrThick, topLeftFromCalib,   bottomRigthFromCalib, calibTopLeft,  calibBottomRight, intThres, distCalibMarks, calibrationType, sliderOffset, previewFrameSize
        put(380l, new Device(380l, "Injection", "Eagle-v1.00-#49", "LaunchPad", 128.5f, 120f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INJECTION, Device.MASK_ROTATE_AUTO, 105, new Point2D(133, 378), 8, 32, new Point2D(190, 200), 4, new Point2D(20, 230), new Point2D(100, 248), new Point2D(9, 0), new Point2D(0, 9), 10, 6.5, CalibrationType.STATIC, 0, PreviewFrameSize.R720x480, 12));   // 342

        //		  id,      what,         model,            where,               tube, fl, lensEye, slit, angle,  pd,  def, +lim,  -lim,  screenX,screenY, phoneOri,  pdMeasurement,      accuracyTechnique,           eyesAtATimeTechnique, pdRuler, m, clear, viewToRun,  deviceType,                         rotationalInteraction,  ratRad, deltaCalib,     Thick, scrRad, deltaCalib,  scrThick, topLeftFromCalib,   bottomRigthFromCalib, calibTopLeft,  calibBottomRight, intThres, distCalibMarks, calibrationType, sliderOffset, previewFrameSize
        put(381l, new Device(381l, "Injection", "Eagle-v1.00-#50", "LaunchPad", 128.5f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INJECTION, Device.MASK_ROTATE_AUTO, 105, new Point2D(133, 378), 8, 32, new Point2D(190, 200), 4, new Point2D(20, 230), new Point2D(100, 248), new Point2D(9, 0), new Point2D(0, 9), 10, 6.5, CalibrationType.STATIC, 0, PreviewFrameSize.R720x480, 12));   // 342

        //		  id,      what,         model,            where,               tube, fl, lensEye, slit, angle,  pd,  def, +lim,  -lim,  screenX,screenY, phoneOri,  pdMeasurement,      accuracyTechnique,           eyesAtATimeTechnique, pdRuler, m, clear, viewToRun,  deviceType,                         rotationalInteraction,  ratRad, deltaCalib,     Thick, scrRad, deltaCalib,  scrThick, topLeftFromCalib,   bottomRigthFromCalib, calibTopLeft,  calibBottomRight, intThres, distCalibMarks, calibrationType, sliderOffset, previewFrameSize
        put(398l, new Device(398l, "Injection", "Eagle-v1.00-#67", "LaunchPad", 128.5f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INJECTION, Device.MASK_ROTATE_AUTO, 105, new Point2D(133, 378), 8, 32, new Point2D(190, 200), 4, new Point2D(20, 230), new Point2D(100, 248), new Point2D(9, 0), new Point2D(0, 9), 10, 6.5, CalibrationType.STATIC, 0, PreviewFrameSize.R720x480, 16));   // 342

        //		  id,      what,         model,            where,               tube, fl, lensEye, slit, angle,  pd,  def, +lim,  -lim,  screenX,screenY, phoneOri,  pdMeasurement,      accuracyTechnique,           eyesAtATimeTechnique, pdRuler, m, clear, viewToRun,  deviceType,                         rotationalInteraction,  ratRad, deltaCalib,     Thick, scrRad, deltaCalib,  scrThick, topLeftFromCalib,   bottomRigthFromCalib, calibTopLeft,  calibBottomRight, intThres, distCalibMarks, calibrationType, sliderOffset, previewFrameSize
        put(399l, new Device(399l, "Injection", "Eagle-v1.00-#68", "LaunchPad", 128.5f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 5.5f, -12.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INJECTION, Device.MASK_ROTATE_AUTO, 105, new Point2D(133, 378), 8, 32, new Point2D(190, 200), 4, new Point2D(20, 230), new Point2D(100, 248), new Point2D(9, 0), new Point2D(0, 9), 10, 6.5, CalibrationType.STATIC, 0, PreviewFrameSize.R720x480, 12));   // 342
        //		  id,      what,         model,            where,               tube, fl, lensEye, slit, angle,  pd,  def, +lim,  -lim,  screenX,screenY, phoneOri,  pdMeasurement,      accuracyTechnique,           eyesAtATimeTechnique, pdRuler, m, clear, viewToRun,  deviceType,                         rotationalInteraction,  ratRad, deltaCalib,     Thick, scrRad, deltaCalib,  scrThick, topLeftFromCalib,   bottomRigthFromCalib, calibTopLeft,  calibBottomRight, intThres, distCalibMarks, calibrationType, sliderOffset, previewFrameSize
        // Start of Pre-Sales Batch.

        put(403l, new Device(403l, "Injection", "Eagle-v1.01", "LaunchPad", 128.5f, 75f, 15f, 0.85f, 22.5f, 62f, 5.5f, 7.5f, -15.0f, Device.LEFT, Device.PD_IN_SLIDE, Device.ACCURACY_JUST_MOVING, Device.EYES_ONE_AT_A_TIME, 20, 1, false, null, Device.DEVICE_TYPE_SCROLL_CAMERA_INJECTION, Device.MASK_ROTATE_AUTO, 105, new Point2D(133, 378), 8, 32, new Point2D(190, 200), 4, new Point2D(20, 230), new Point2D(100, 248), new Point2D(9, 0), new Point2D(0, 9), 10, 6.5, CalibrationType.STATIC, 0, PreviewFrameSize.R720x480, 12));

    }};

    public static Device get(long id) {
        // DEVICE ranges are not uniform and there are 'holes'. In such cases, return the device of
        // the last existing ID before the requested ID.
        if (DEVICES.floorEntry(id) == null) return null;
        Device baseModel = DEVICES.floorEntry(id).getValue();
        if (baseModel == null) return null;
        Device dynamicInstance = baseModel.clone();
        dynamicInstance.id = id;
        return dynamicInstance;
    }

    public static class Device {
        public static final int RIGHT = 1;
        public static final int LEFT = 0;

        public static final int PD_IN_KNOB = 1;
        public static final int PD_IN_SLIDE = 0;
        public static final int PD_NO_MEASUREMENT = 3;

        public static final int ACCURACY_JUST_MOVING = 0;
        public static final int ACCURACY_MOVING_AB = 1;

        public static final int EYES_ONE_AT_A_TIME = 0;
        public static final int EYES_BOTH_SAME_TIME = 1;

        public static final int DEVICE_TYPE_DEFAULT = 0;
        public static final int DEVICE_TYPE_SCROLL_BLUETOOTH = 1;
        public static final int DEVICE_TYPE_SCROLL_AUDIO = 2;
        public static final int DEVICE_TYPE_SCROLL_CAMERA_OPENCV = 3;
        public static final int DEVICE_TYPE_SCROLL_CAMERA_INHOUSE = 4;
        public static final int DEVICE_TYPE_SCROLL_CAMERA_INHOUSE_MVP = 5;
        public static final int DEVICE_TYPE_SCROLL_CAMERA_INHOUSE_MANTA = 6;
        public static final int DEVICE_TYPE_SCROLL_CAMERA_INJECTION = 7;

        public static final int MASK_ROTATE_MANUAL_REQUIRE_ASK_SCREEN = 0;
        public static final int MASK_ROTATE_AUTO = 1;

        public long id;
        public String what;
        public String model;
        public String where;
        public float tubeLength;
        public float lensFocalLength;
        public float lensEyeDistance;
        public float angularSteps;
        public float defaultPD;
        public float defaultStartingPower;
        public float highestPower;
        public float lowestPower;
        public Class viewToRun;
        public int phoneOrientation;
        public int pdTechnique;
        public int accuracyTechnique;
        public int eyesAtATimeTechnique;
        public int rotationalInteraction;
        public int pdRulerCenter;
        public int movingAngles;
        public boolean clearScreenBetweenAngles;
        public float slitDistance;
        public int deviceType;


        public double meridianRadius;
        public Point2D deltaMeridianFromCalibration;
        public double meridianThickness;

        public double scrollyRadius;
        public Point2D deltaScrollyFromCalibration;
        public double scrollyThickness;

        public Point2D deltaPDRectTopLeftFromCalibration;
        public Point2D deltaPDRectBottomRigthFromCalibration;

        public Point2D calibrationRectTopLeft;
        public Point2D calibrationRectBottomRight;

        public int intensityThreshold;
        public double distanceBetweenCalibrationMarks;

        public double sliderCenterPerspectiveOffset;

        public PreviewFrameSize previewFrameSize;

        public CalibrationType calibrationType;

        public float slitsLinesAlignmentDelta;

        public Device(long id, String what, String model, String where,
                      float tubeLength, float lensFocalLength, float lensEyeDistance,
                      float slitDistance, float angularSteps, float defaultPD, float defaultStartingPower, float highestPower, float lowestPower,
                      int phoneOrientation, int pdMeasurement, int accuracyTechnique, int eyesAtATimeTechnique,
                      int pdRulerCenter, int movingAngles, boolean clearScreenBetweenAngles,
                      Class viewToRun, int deviceType, int rotationalInteraction, double meridianRadius,
                      Point2D deltaMeridianFromCalibration, double meridianThickness, double scrollyRadius,
                      Point2D deltaScrollyFromCalibration, double scrollyThickness,
                      Point2D deltaPDRectTopLeftFromCalibration, Point2D deltaPDRectBottomRigthFromCalibration,
                      Point2D calibrationRectTopLeft, Point2D calibrationRectBottomRight, int intensityThreshold,
                      double distanceBetweenCalibrationMarks, CalibrationType calibrationType,
                      double sliderCenterPerspectiveOffset, PreviewFrameSize previewFrameSize, float slitsLinesAngleDelta) {
            super();
            this.id = id;
            this.what = what;
            this.model = model;
            this.where = where;
            this.tubeLength = tubeLength;
            this.lensFocalLength = lensFocalLength;
            this.lensEyeDistance = lensEyeDistance;
            this.angularSteps = angularSteps;
            this.defaultPD = defaultPD;
            this.defaultStartingPower = defaultStartingPower;
            this.viewToRun = viewToRun;
            this.phoneOrientation = phoneOrientation;
            this.pdTechnique = pdMeasurement;
            this.pdRulerCenter = pdRulerCenter;
            this.movingAngles = movingAngles;
            this.clearScreenBetweenAngles = clearScreenBetweenAngles;
            this.slitDistance = slitDistance;
            this.deviceType = deviceType;
            this.accuracyTechnique = accuracyTechnique;
            this.eyesAtATimeTechnique = eyesAtATimeTechnique;
            this.rotationalInteraction = rotationalInteraction;
            this.meridianRadius = meridianRadius;
            this.deltaMeridianFromCalibration = deltaMeridianFromCalibration;
            this.meridianThickness = meridianThickness;
            this.scrollyRadius = scrollyRadius;
            this.deltaScrollyFromCalibration = deltaScrollyFromCalibration;
            this.scrollyThickness = scrollyThickness;
            this.deltaPDRectTopLeftFromCalibration = deltaPDRectTopLeftFromCalibration;
            this.deltaPDRectBottomRigthFromCalibration = deltaPDRectBottomRigthFromCalibration;
            this.calibrationRectTopLeft = calibrationRectTopLeft;
            this.calibrationRectBottomRight = calibrationRectBottomRight;
            this.intensityThreshold = intensityThreshold;
            this.distanceBetweenCalibrationMarks = distanceBetweenCalibrationMarks;
            this.lowestPower = lowestPower;
            this.highestPower = highestPower;
            this.calibrationType = calibrationType;
            this.sliderCenterPerspectiveOffset = sliderCenterPerspectiveOffset;
            this.previewFrameSize = previewFrameSize;
            this.slitsLinesAlignmentDelta = slitsLinesAngleDelta;
        }

        public Device(long id, String what, String model, String where,
                      float tubeLength, float lensFocalLength, float lensEyeDistance,
                      float slitDistance, float angularSteps, float defaultPD, float defaultStartingPower, float highestPower, float lowestPower,
                      int phoneOrientation, int pdMeasurement, int accuracyTechnique, int eyesAtATimeTechnique,
                      int pdRulerCenter, int movingAngles, boolean clearScreenBetweenAngles,
                      Class viewToRun, int deviceType, int rotationalInteraction) {
            super();
            this.id = id;
            this.what = what;
            this.model = model;
            this.where = where;
            this.tubeLength = tubeLength;
            this.lensFocalLength = lensFocalLength;
            this.lensEyeDistance = lensEyeDistance;
            this.angularSteps = angularSteps;
            this.defaultPD = defaultPD;
            this.defaultStartingPower = defaultStartingPower;
            this.viewToRun = viewToRun;
            this.phoneOrientation = phoneOrientation;
            this.pdTechnique = pdMeasurement;
            this.pdRulerCenter = pdRulerCenter;
            this.movingAngles = movingAngles;
            this.clearScreenBetweenAngles = clearScreenBetweenAngles;
            this.slitDistance = slitDistance;
            this.deviceType = deviceType;
            this.accuracyTechnique = accuracyTechnique;
            this.eyesAtATimeTechnique = eyesAtATimeTechnique;
            this.rotationalInteraction = rotationalInteraction;
            this.lowestPower = lowestPower;
            this.highestPower = highestPower;
        }

        public Device clone() {
            Device d = new Device();
            d.id = id;
            d.what = what;
            d.model = model;
            d.where = where;
            d.tubeLength = tubeLength;
            d.lensFocalLength = lensFocalLength;
            d.lensEyeDistance = lensEyeDistance;
            d.angularSteps = angularSteps;
            d.defaultPD = defaultPD;
            d.defaultStartingPower = defaultStartingPower;
            d.viewToRun = viewToRun;
            d.phoneOrientation = phoneOrientation;
            d.pdTechnique = pdTechnique;
            d.pdRulerCenter = pdRulerCenter;
            d.movingAngles = movingAngles;
            d.clearScreenBetweenAngles = clearScreenBetweenAngles;
            d.slitDistance = slitDistance;
            d.deviceType = deviceType;
            d.accuracyTechnique = accuracyTechnique;
            d.eyesAtATimeTechnique = eyesAtATimeTechnique;
            d.rotationalInteraction = rotationalInteraction;
            d.meridianRadius = meridianRadius;
            d.deltaMeridianFromCalibration = deltaMeridianFromCalibration;
            d.meridianThickness = meridianThickness;
            d.scrollyRadius = scrollyRadius;
            d.deltaScrollyFromCalibration = deltaScrollyFromCalibration;
            d.scrollyThickness = scrollyThickness;
            d.deltaPDRectTopLeftFromCalibration = deltaPDRectTopLeftFromCalibration;
            d.deltaPDRectBottomRigthFromCalibration = deltaPDRectBottomRigthFromCalibration;
            d.calibrationRectTopLeft = calibrationRectTopLeft;
            d.calibrationRectBottomRight = calibrationRectBottomRight;
            d.intensityThreshold = intensityThreshold;
            d.distanceBetweenCalibrationMarks = distanceBetweenCalibrationMarks;
            d.lowestPower = lowestPower;
            d.highestPower = highestPower;
            d.calibrationType = calibrationType;
            d.sliderCenterPerspectiveOffset = sliderCenterPerspectiveOffset;
            d.previewFrameSize = previewFrameSize;
            d.slitsLinesAlignmentDelta = slitsLinesAlignmentDelta;
            return d;
        }

        private Device() {

        }

        public String toString() {
            return id + "\t" +
                    what + "\t" +
                    model + "\t" +
                    where + "\t" +
                    tubeLength + "\t" +
                    lensFocalLength + "\t" +
                    lensEyeDistance + "\t" +
                    angularSteps + "\t" +
                    defaultPD + "\t" +
                    defaultStartingPower + "\t" +
                    (phoneOrientation == RIGHT ? "Right" : "Left") + "\t" +
                    (pdTechnique == PD_IN_KNOB ? "Knob" : "Slide") + "\t" +
                    (pdRulerCenter) + "\t" +
                    movingAngles + "\t" +
                    clearScreenBetweenAngles + "\t" +
                    (viewToRun != null ? viewToRun.getSimpleName() + "\t" : "\t" +
                            deviceType);
        }


        public float testPositionForPD(float devicePD, float pixelSize, float centerY) {
            float pupilaryDistancePX = devicePD / pixelSize;

            return centerY - pupilaryDistancePX / 2;
        }

        public float filmPositionForPD(float devicePD, float pixelSize, float centerY) {
            float pupilaryDistancePX = devicePD / pixelSize;

            return centerY + pupilaryDistancePX / 2;
        }

        public float measurePD(float leftPDCode, float rightPDCode) {
            return new ComputePD(this.defaultPD).run(rightPDCode, leftPDCode);
        }
    }

    public static String toDebugString() {
        StringBuilder builder = new StringBuilder();

        builder.append("ID" + "\t" +
                "What         " + "\t" + "\t" +
                "Model    " + "\t" +
                "Where    " + "\t" +
                "TubeL" + "\t" +
                "FocalL" + "\t" +
                "L-E  " + "\t" +
                "AngSt" + "\t" +
                "PD    " + "\t" +
                "Pw" + "\t" +
                "ScX" + "\t" +
                "ScY" + "\t" +
                "DOri" + "\t" +
                "PDtech" + "\t" +
                "PDRuler" + "\t" +
                "QtdStep" + "\t" +
                "ClearSc" + "\t" +
                "Class" + "\t" +
                "DeviceType" + "\n");

        Long[] deviceIds = DEVICES.keySet().toArray(new Long[0]);
        Arrays.sort(deviceIds);

        for (Long id : deviceIds) {
            builder.append(DEVICES.get(id).toString() + "\n");
        }

        return builder.toString();
    }
}
