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

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.view.WindowManager;

public class HardwareUtil {

    public static void turnScreenOff(Activity activity) {
        setScreenBrightness(activity, WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF);
    }

    public static void turnScreenMin(Activity activity) {
        setScreenBrightness(activity, 0.1f);
    }

    public static void turnScreenAuto(Activity activity) {
        setScreenBrightness(activity, -0.1f);
    }

    public static void turnScreenMax(Activity activity) {
        setScreenBrightness(activity, WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL);
    }

    public static void disableSleep(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        lockManager(activity).disableKeyguard();
    }

    public static void enableSleep(Activity activity) {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        lockManager(activity).reenableKeyguard();
    }

    public static KeyguardLock lockManager(Activity activity) {
        KeyguardManager keyguardManager = (KeyguardManager) activity.getSystemService(Context.KEYGUARD_SERVICE);
        return keyguardManager.newKeyguardLock(Context.KEYGUARD_SERVICE);
    }

    public static void setScreenBrightness(Activity activity, float brightness) {
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.screenBrightness = brightness;
        activity.getWindow().setAttributes(params);
    }

    public static float getDevicePixelSize(Context context) {
        //25.4mm = 1 inch
        return 25.4f / getDeviceDPI(context);
    }

    public static float getDeviceDPI(Context context) {
        return HardwareDeviceDPIUtil.getDeviceDPI(context);
    }

    public static float getBatteryPercent(Context context) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level / (float) scale;
        return batteryPct;
    }

}
