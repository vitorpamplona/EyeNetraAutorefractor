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
package com.vitorpamplona.meridian.utils;

import android.graphics.Rect;

import com.vitorpamplona.core.testdevice.Point2D;

public class YuvDraw {

    private int width, height;
    private static YuvPixel frame;

    public YuvDraw(int width, int height) {
        this.width = width;
        this.height = height;
        frame = new YuvPixel(width, height);
    }

    public void drawCircle(byte[] data, Point2D center, int radius) {
        int x, y;
        for (int theta = 0; theta < 360; theta++) {
            // find x,y points in circle (nearest pixel)
            x = (int) Math.round(center.x + radius * Math.cos(Math.toRadians(theta)));
            y = (int) Math.round(center.y + radius * Math.sin(Math.toRadians(theta)));
            frame.setY(data, x, y, (byte) 0xff);
        }
    }

    public void drawBox(byte[] data, Rect box) {
        int left = box.left < 0 ? 0 : box.left;
        int right = box.right >= width ? width - 1 : box.right;
        int top = box.top < 0 ? 0 : box.top;
        int bottom = box.bottom >= height ? height - 1 : box.bottom;

        // draw horizontal lines
        for (int x = left; x < right; x++) {
            frame.setY(data, x, top, (byte) 0xff);
            frame.setY(data, x, bottom, (byte) 0xff);
        }
        // draw vertical lines
        for (int y = top; y < bottom; y++) {
            frame.setY(data, left, y, (byte) 0xff);
            frame.setY(data, right, y, (byte) 0xff);
        }
    }
}
