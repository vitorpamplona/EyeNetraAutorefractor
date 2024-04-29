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
package com.vitorpamplona.core.testdevice.ui.alignment;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import com.vitorpamplona.core.testdevice.DeviceDataset.Device;
import com.vitorpamplona.core.testdevice.Pair;
import com.vitorpamplona.core.testdevice.ui.MillimiterBasedPainter;
import com.vitorpamplona.core.utils.StdDevStack;

public class GreenCircle extends MillimiterBasedPainter {

    protected final Paint mPaint = new Paint();

    protected static final float ALIGNMENT_CIRCLE_THICKNESS_MM = 1.2f;
    protected static final float ALIGNMENT_CIRCLE_RADIUS_MM = 6.5f;

    protected static final int GREEN_LEVEL_MIN = 0;
    protected static final int GREEN_LEVEL_MAX = 255;
    protected static final float FADE_OUT_START = 1500;
    protected static final float FADE_DURATION = 3000;

    protected int mGreenLevel = GREEN_LEVEL_MIN;
    protected long mLastMoved = 0;

    StdDevStack posYs = new StdDevStack(40);

    public GreenCircle(float displayDPI) {
        super(displayDPI);
    }

    public void setFadingAlpha(float alpha) {
        mGreenLevel = (int) (alpha * (GREEN_LEVEL_MAX - GREEN_LEVEL_MIN)) + GREEN_LEVEL_MIN;
    }

    @Override
    public boolean paint(Canvas canvas, Device device, boolean testingRightEye, Pair workingPair, float middleX,
                         float testY, float idleY, float alpha) {

        setFadingAlpha(alpha);

        if (device.phoneOrientation == Device.RIGHT) {
            if (testingRightEye) {
                return alignmentPatterns(canvas, mPaint, workingPair, middleX, testY, ALIGNMENT_CIRCLE_THICKNESS_MM, ALIGNMENT_CIRCLE_RADIUS_MM);
            } else {
                return alignmentPatterns(canvas, mPaint, workingPair, middleX, idleY, ALIGNMENT_CIRCLE_THICKNESS_MM, ALIGNMENT_CIRCLE_RADIUS_MM);
            }
        } else {
            if (testingRightEye) {
                return alignmentPatterns(canvas, mPaint, workingPair, middleX, idleY, ALIGNMENT_CIRCLE_THICKNESS_MM, ALIGNMENT_CIRCLE_RADIUS_MM);
            } else {
                return alignmentPatterns(canvas, mPaint, workingPair, middleX, testY, ALIGNMENT_CIRCLE_THICKNESS_MM, ALIGNMENT_CIRCLE_RADIUS_MM);
            }
        }
    }

    public boolean alignmentPatterns(Canvas canvas, Paint mPaint, Pair workingPair, float xCenter, float yCenter, float lineSizeMM, float radiusMM) {
        canvas.save();
        canvas.translate(xCenter, yCenter);

        mPaint.setColor(Color.rgb(0, mGreenLevel, 0));
        mPaint.setStrokeWidth(toPX(lineSizeMM));
        mPaint.setStyle(Style.STROKE);

        canvas.drawCircle(0, 0, toPX(radiusMM), mPaint);

        canvas.restore();

        return true;
    }

    public void onAngleChange() {
        //mLastMoved = System.currentTimeMillis();
    }

}
