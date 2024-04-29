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
package com.vitorpamplona.netra.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;

import java.util.HashMap;
import java.util.Map;

public class HardwareDeviceDPIUtil {

    /***
     * Map of device manufactures
     * TODO: In the future, put this in a file or pull remotely
     */
    private static final Map<String, Map<String, Float>> DPI_MAP;

    static {
        DPI_MAP = new HashMap<String, Map<String, Float>>();

        // Samsung
        Map<String, Float> samsung = new HashMap<String, Float>();
        DPI_MAP.put("Samsung", samsung);

        samsung.put("SGH-T959", 234.0f);
        samsung.put("GT-I9100", 218.0f);
        samsung.put("SGH-T939", 180.0f);

        // Verizon
        Map<String, Float> verizon = new HashMap<String, Float>();
        DPI_MAP.put("verizon", verizon);

        verizon.put("DROIDX", 240.0f);

        // Verizon_wwe
        Map<String, Float> verizon_wwe = new HashMap<String, Float>();
        DPI_MAP.put("verizon_wwe", verizon_wwe);

        // HRC Rezound
        verizon_wwe.put("ADR6425LVW", 342.0f);

    }

    public static float getDeviceDPI(Context context) {
        if (DPI_MAP.containsKey(Build.BRAND)) {
            Map<String, Float> brand = DPI_MAP.get(Build.BRAND);
            if (brand.containsKey(Build.MODEL)) {
                return brand.get(Build.MODEL);
            }
        }
        Resources res = context.getResources();
        return (res.getDisplayMetrics().xdpi + res.getDisplayMetrics().ydpi) / 2;
    }

}
