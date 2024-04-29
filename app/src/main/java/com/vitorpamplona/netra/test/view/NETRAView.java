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
package com.vitorpamplona.netra.test.view;

import com.vitorpamplona.core.testdevice.DeviceDataset.Device;

public interface NETRAView {
    public enum Screen {
        LOAD_DEVICE,
        CALIBRATION,
        BLUE_SCREEN,
        ALIEN_TEST,
        TRAINING_TEST,
        PLAY_ICON,
        CHECK_MARK,
        STRING;
    }

    public void setSliderDisplacement(float pd);

    public void setAngle(float angle);

    public float setPowerClosestTo(float power);

    public float resetPowerClosestTo(float power);

    public float decreasePitch();

    public float increasePitch();

    public void setScreen(Screen s);

    public void setPD(float pd);

    public void setTestingRightEye(boolean rightEye);

    public void setDevice(Device d);

    public Device getDevice();
}
