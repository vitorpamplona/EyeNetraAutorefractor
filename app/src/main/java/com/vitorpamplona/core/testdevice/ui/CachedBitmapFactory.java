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

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

public class CachedBitmapFactory {

    private static CachedBitmapFactory factory;
    private static Resources resources;

    private LruCache<Integer, Bitmap> bitmaps;

    // Get max available VM memory, exceeding this amount will throw an
    // OutOfMemory exception. Stored in kilobytes as LruCache takes an
    // int in its constructor.
    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

    // Use 2/8th of the available memory for this memory cache.
    final int cacheSize = maxMemory / 8;

    private CachedBitmapFactory() {
        bitmaps = new LruCache<Integer, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(Integer key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static CachedBitmapFactory getInstance() {
        if (factory == null) {
            factory = new CachedBitmapFactory();
        }
        return factory;
    }

    public Bitmap decodeResource(int name) {
        return decodeResource(resources, name);
    }

    public Bitmap decodeResource(Resources r, int name) {
        Bitmap ret = bitmaps.get(name);
        if (ret == null) {
            ret = BitmapFactory.decodeResource(r, name);
            bitmaps.put(name, ret);
        }
        return ret;
    }

    public static void setResources(Resources r) {
        resources = r;
    }

}
