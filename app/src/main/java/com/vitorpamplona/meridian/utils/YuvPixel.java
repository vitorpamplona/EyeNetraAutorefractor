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

/**
 * Easy access to YUV image pixels (YCbCr).  
 * Currently works with the Android YUV preview frame (NV21).
 * NV21 is 4:2:0 planar format, so there is a full frame Y (luma)
 * and a half frame interleaved V (Cr) and U (Cb).
 */
public final class YuvPixel {

    private int width, height, offset;
    private byte b;

    public YuvPixel(int width, int height) {
        this.width = width;
        this.height = height;
        this.offset = width * height;
    }

    // get
    public byte getY(byte[] byteframe, int x, int y) {
        return isBadCoord(x, y) ? 0x00 : byteframe[(y * width) + x];
    }

    public byte getU(byte[] byteframe, int x, int y) {
        //return isBadCoord(x,y) ? 0x00 : byteframe[(offset+1) + 2 * ((y * width/2) + x)];
        // Old version
        return isBadCoord(x, y) ? 0x00 : byteframe[offset + (y >> 1) * width + (x & ~1) + 1];
    }

    public byte getV(byte[] byteframe, int x, int y) {
        // Old Version: Wrong.
        //return isBadCoord(x,y) ? 0x00 : byteframe[(offset) + 2 * ((y * width/2) + x)];

        return isBadCoord(x, y) ? 0x00 : byteframe[offset + (y >> 1) * width + (x & ~1) + 0];
    }

    // get YUV filtered
    public byte getFiltered(byte[] byteframe, int x, int y, YuvFilter filter) {
        int Uval = 0xff & (int) getU(byteframe, x, y);
        int Vval = 0xff & (int) getV(byteframe, x, y);

        // returns valid Y frame pixel
        return filter.isValidPoint(Uval, Vval) ? getY(byteframe, x, y) : 0x00;
    }

    public byte getFilteredInverted(byte[] byteframe, int x, int y, YuvFilter filter) {
        int Uval = 0xff & (int) getU(byteframe, x, y);
        int Vval = 0xff & (int) getV(byteframe, x, y);

        // returns valid Y frame pixel
        return filter.isValidPoint(Uval, Vval) ? 0x00 : getY(byteframe, x, y);
    }

    // set
    public void setY(byte[] byteframe, int x, int y, byte value) {
        if (isBadCoord(x, y))
            return;
        else
            byteframe[(y * width) + x] = value;
    }

    public void setU(byte[] byteframe, int x, int y, byte value) {
        if (isBadCoord(x, y))
            return;
        else
            byteframe[offset + (y >> 1) * width + (x & ~1) + 1] = value;
    }

    public void setV(byte[] byteframe, int x, int y, byte value) {
        if (isBadCoord(x, y))
            return;
        else
            byteframe[offset + (y >> 1) * width + (x & ~1) + 0] = value;
    }

    private boolean isBadCoord(int x, int y) {
        return (x < 0 || y < 0 || x >= width || (y >= height));
    }


}