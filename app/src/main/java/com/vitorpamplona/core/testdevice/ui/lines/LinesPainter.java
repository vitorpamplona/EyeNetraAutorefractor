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
package com.vitorpamplona.core.testdevice.ui.lines;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.vitorpamplona.core.testdevice.DeviceDataset.Device;
import com.vitorpamplona.core.testdevice.ui.MillimiterBasedPainter;


public abstract class LinesPainter extends MillimiterBasedPainter {

    protected Paint mPaint = new Paint();

    protected static final float LINE_X_HALF_WIDTH = 5f; // mm
    protected static final float LINE_X_OVERLAP = 2f;    // mm

    public LinesPainter(float displayDPI) {
        super(displayDPI);
    }

    /**
     * Checks if the phone is pointing to the right direction and decides which testY or idleY should be used. 
     * These two parameters are the centers of the canvas of which only one of them should be activated for tests. 
     *
     * @param canvas
     * @param device ID of the binoculars to be used. 
     * @param testingRightEye Is it testing the right eye now? 
     * @param middleX Center in X (Landscape mode). 
     * @param testY Center in Y for the test image in the Device setup
     * @param idleY Center in Y for the idle image in the Device setup
     */
    public void translateToTheTestPosition(
            Canvas canvas, Device device, boolean testingRightEye,
            float middleX, float testY, float idleY) {
        if (device.phoneOrientation == Device.RIGHT) {
            if (testingRightEye) {
                canvas.translate(testY, middleX);
            } else {
                canvas.translate(idleY, middleX);
            }
        } else {
            if (testingRightEye) {
                canvas.translate(idleY, middleX);
            } else {
                canvas.translate(testY, middleX);
            }
            canvas.rotate(180);
        }
    }
}
