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

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.vitorpamplona.core.testdevice.DeviceDataset.Device;
import com.vitorpamplona.core.testdevice.PrismaticEffect;
import com.vitorpamplona.netra.test.ITestActivity;
import com.vitorpamplona.netra.utils.HardwareUtil;

public abstract class BaseTestView extends View {

    public static float DEFAULT_PD = 62;

    protected float pupilaryDistance = DEFAULT_PD;
    private boolean testingRightEye = false;
    private Device device;
    PrismaticEffect prismEffect;

    public BaseTestView(Context context) {
        super(context);
    }

    public BaseTestView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseTestView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(Context context) {
        pupilaryDistance = 62;
    }

    public void setDevice(Device d) {
        device = d;

        prismEffect = new PrismaticEffect(d.tubeLength, d.lensFocalLength, d.defaultPD);
    }

    public void setPD(float pd) {
        pupilaryDistance = pd;
        ((Activity) getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        });
    }

    public float pd() {
        return pupilaryDistance;
    }

    public float getPixelSizeMM() {
        return HardwareUtil.getDevicePixelSize(getContext());
    }

    public float toPX(float mm) {
        return mm / getPixelSizeMM();
    }

    public boolean testingRightEye() {
        return testingRightEye;
    }

    public ITestActivity app() {
        return ((ITestActivity) getContext());
    }

    public Device getDevice() {
        return device;
    }

    public int testPrismShiftPX() {
        return (int) (prismEffect.testPrismShift(pd()) / getPixelSizeMM());
    }

    public float testPositionXPX() {
        // Trunks the value to avoid rounding to the nearest pixel when drawing the lines on the screen.
        float pdPX = (int) device.testPositionForPD(device.defaultPD, getPixelSizeMM(), getWidth() / 2);

        // only changes the image on the eye that is not being tested.
        if (testingRightEye())
            return pdPX - testPrismShiftPX();
        else
            return pdPX;
    }

    public float idlePositionXPX() {
        // Trunks the value to avoid rounding to the nearest pixel when drawing the lines on the screen.
        float pdPX = (int) device.filmPositionForPD(device.defaultPD, getPixelSizeMM(), getWidth() / 2);

        // only changes the image on the eye that is not being tested.
        if (!testingRightEye())
            return pdPX + testPrismShiftPX();
        else
            return pdPX;
    }

    public float positionYPX() {
        return getHeight() / 2;
    }

    public void reset() {
        pupilaryDistance = 62;
    }

    public boolean isTestingRightEye() {
        return testingRightEye;
    }

    public void setTestingRightEye(boolean testingRightEye) {
        this.testingRightEye = testingRightEye;
    }

    public boolean hasTestDevice() {
        return device != null;
    }

    public abstract void setAngle(float angle);

}
