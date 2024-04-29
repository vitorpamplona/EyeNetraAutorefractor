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
package com.vitorpamplona.core.testdevice.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;

import com.vitorpamplona.core.testdevice.DeviceDataset.Device;
import com.vitorpamplona.core.testdevice.Pair;

public class AnimatedStringPainter implements CanvasPainter {

    protected static float TEXT_SIZE = 1024;

    protected Paint mPaint = new Paint();
    protected Rect mRect = new Rect();
    protected String mString = "";
    private float dy = -3;
    private float dz = 1;

    public AnimatedStringPainter(String string) {
        mString = string;
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Style.FILL);
        mPaint.setTextSize(TEXT_SIZE);
        mPaint.getTextBounds(mString, 0, mString.length(), mRect);
    }

    @Override
    public boolean paint(Canvas canvas, Device device, boolean testingRightEye, Pair workingPair, float middleX, float testY, float idleY, float alpha) {
        mPaint.setColor(Color.argb((int) (alpha * 255), 0, 0, 255));

        paintString(canvas, testY, middleX, dy);
        paintString(canvas, idleY, middleX, -dy);

        dz *= 0.987;
        dy += 0.3;

        if (dy > 1) {
            dy = 1f;
        }

        return true;
    }

    public void paintString(Canvas canvas, float centerX, float centerY, float dy) {
        mPaint.setTextSize(TEXT_SIZE * dz);
        mPaint.getTextBounds(mString, 0, mString.length(), mRect);

        canvas.save();
        canvas.translate(centerX, centerY);

        // Draws 3 times to compensate for hardware bug that does not draw the letter.
        canvas.drawText(mString, -(mRect.right - mRect.left) / 2f - dy, (mRect.bottom - mRect.top) / 2f, mPaint);
        canvas.drawText(mString, -(mRect.right - mRect.left) / 2f - dy, (mRect.bottom - mRect.top) / 2f, mPaint);
        canvas.drawText(mString, -(mRect.right - mRect.left) / 2f - dy, (mRect.bottom - mRect.top) / 2f, mPaint);

        canvas.restore();
    }

}
