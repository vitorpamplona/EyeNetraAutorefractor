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

import android.graphics.Bitmap;

public final class YuvConverter {
    // super secret constructor
    private YuvConverter() {
    }


    public static Bitmap toBitmapGrayscaleMini(byte[] data, int width, int height, int scale) {
        int p;
        int Color;
        int pos = 0;
        int S = scale;

        Bitmap bmp = Bitmap.createBitmap(width / S, height / S, Bitmap.Config.ARGB_8888);

        for (int h = 0; h < height; h += S) {
            for (int w = 0; w < width; w += S) {
                p = data[pos] & 0xFF;
                Color = 0xff000000 | p << 16 | p << 8 | p;  // put grayscale data into red/green/blue channels
                bmp.setPixel(w, h, Color);
                pos++;
            }
        }
        return bmp;
    }

    public static Bitmap toBitmapGrayscale(byte[] data, int width, int height) {

        int p;
        int Color;
        int pos = 0;

        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        toBitmapGrayscale(data, bmp, width, height, 1);

        return bmp;
    }

    public static void toBitmapGrayscale(byte[] data, Bitmap bmp, int width, int height, int intensityMultiplier) {

        int p;
        int Color;
        int pos = 0;

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                p = (data[pos] & 0xFF) * intensityMultiplier;
                Color = 0xff000000 | p << 16 | p << 8 | p;  // put grayscale data into red/green/blue channels
                bmp.setPixel(w, h, Color);
                pos++;
            }
        }
    }

    public static Bitmap toBitmapRGB(byte[] yuv, int width, int height) {
        return toBitmapRGB(yuv, width, height, 1);
    }

    public static Bitmap toBitmapRGB(byte[] yuv, int width, int height, float intensityMultiplier) {
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        toBitmapRGB(yuv, bmp, width, height, intensityMultiplier);
        return bmp;
    }

    public static void toBitmapRGB(byte[] yuv, Bitmap bmp, int width, int height) {
        toBitmapRGB(yuv, bmp, width, height, 1);
    }

    public static void toBitmapRGB(byte[] yuv, Bitmap bmp, int width, int height, float intensityMultiplier) {

        int Color;

        final int frameSize = width * height;
        final int ii = 0;
        final int ij = 0;
        final int di = +1;
        final int dj = +1;

        for (int i = 0, ci = ii; i < height; ++i, ci += di) {
            for (int j = 0, cj = ij; j < width; ++j, cj += dj) {
                int y = (int) ((0xff & ((int) yuv[ci * width + cj])) * intensityMultiplier);
                int v = (0xff & ((int) yuv[frameSize + (ci >> 1) * width + (cj & ~1) + 0]));
                int u = (0xff & ((int) yuv[frameSize + (ci >> 1) * width + (cj & ~1) + 1]));
                y = y < 16 ? 16 : y;

                int r = (int) Math.round((1.164f * (y - 16) + 1.596f * (v - 128)));
                int g = (int) Math.round((1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128)));
                int b = (int) Math.round((1.164f * (y - 16) + 2.018f * (u - 128)));

                r = r < 0 ? 0 : (r > 255 ? 255 : r);
                g = g < 0 ? 0 : (g > 255 ? 255 : g);
                b = b < 0 ? 0 : (b > 255 ? 255 : b);

                Color = 0xff000000 | (r << 16) | (g << 8) | b;

                bmp.setPixel(j, i, Color);
            }
        }
    }

    public static void toByteRGBArrays(byte[] yuv, byte[] R, byte[] G, byte[] B, int width, int height) {

        final int frameSize = width * height;
        final int ii = 0;
        final int ij = 0;
        final int di = +1;
        final int dj = +1;

        int ctr = 0;
        for (int i = 0, ci = ii; i < height; ++i, ci += di) {
            for (int j = 0, cj = ij; j < width; ++j, cj += dj) {
                int y = (int) ((0xff & ((int) yuv[ci * width + cj])));
                int v = (0xff & ((int) yuv[frameSize + (ci >> 1) * width + (cj & ~1) + 0]));
                int u = (0xff & ((int) yuv[frameSize + (ci >> 1) * width + (cj & ~1) + 1]));
                y = y < 16 ? 16 : y;

                int r = (int) Math.round((1.164f * (y - 16) + 1.596f * (v - 128)));
                int g = (int) Math.round((1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128)));
                int b = (int) Math.round((1.164f * (y - 16) + 2.018f * (u - 128)));

                r = r < 0 ? 0 : (r > 255 ? 255 : r);
                g = g < 0 ? 0 : (g > 255 ? 255 : g);
                b = b < 0 ? 0 : (b > 255 ? 255 : b);

                R[ctr] = (byte) r;
                G[ctr] = (byte) g;
                B[ctr] = (byte) b;

                ctr++;
            }
        }
    }


    public static void toByteArrays(byte[] yuv, byte[] y, byte[] u, byte[] v, int width, int height) {

        final int frameSize = width * height;
        final int ii = 0;
        final int ij = 0;
        final int di = +1;
        final int dj = +1;

        int cnt = 0;
        for (int i = 0, ci = ii; i < height; ++i, ci += di) {
            for (int j = 0, cj = ij; j < width; ++j, cj += dj) {
                y[cnt] = (byte) (0xff & ((int) yuv[ci * width + cj]));
                v[cnt] = (byte) (0xff & ((int) yuv[frameSize + (ci >> 1) * width + (cj & ~1) + 0]));
                u[cnt] = (byte) (0xff & ((int) yuv[frameSize + (ci >> 1) * width + (cj & ~1) + 1]));
//	            y[cnt] = y[cnt] < 16 ? 16 : y[cnt];  // depends on convention (2's complement)
                cnt++;
            }
        }
    }
}
