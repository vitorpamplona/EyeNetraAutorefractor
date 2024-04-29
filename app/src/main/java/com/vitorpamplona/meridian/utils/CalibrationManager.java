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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONObject;

public class CalibrationManager {

    public static final String TAG = "CalibrationManager";

    public static final String KEY_PREFIX = "cablibration_";

    public static final String KEY_SUCCESS_RATE = "success_rate";
    public static final String KEY_DEVICE_ID = "device_id";
    public static final String KEY_SLIDER_P1_X = "slider_p1_x";
    public static final String KEY_SLIDER_P1_Y = "slider_p1_y";
    public static final String KEY_SLIDER_P2_X = "slider_p2_x";
    public static final String KEY_SLIDER_P2_Y = "slider_p2_y";
    public static final String KEY_CALIBRATION_P1_X = "calibration_p1_x";
    public static final String KEY_CALIBRATION_P1_Y = "calibration_p1_y";
    public static final String KEY_CALIBRATION_P2_X = "calibration_p2_x";
    public static final String KEY_CALIBRATION_P2_Y = "calibration_p2_y";
    public static final String KEY_RATCHET_X = "ratchet_x";
    public static final String KEY_RATCHET_Y = "ratchet_y";
    public static final String KEY_RATCHET_X_NORMALIZED = "ratchet_x_normalized";
    public static final String KEY_RATCHET_Y_NORMALIZED = "ratchet_y_normalized";
    public static final String KEY_RATCHET_R = "ratchet_r";
    public static final String KEY_SCROLLY_X = "scrolly_x";
    public static final String KEY_SCROLLY_Y = "scrolly_y";
    public static final String KEY_SCROLLY_R1 = "scrolly_r1";


    protected SharedPreferences mPreferences;
    protected SharedPreferences.Editor mEditor;

    public CalibrationManager(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    // test if settings exist for device
    public boolean exist(long id) {

        String data = mPreferences.getString(KEY_PREFIX + id, "");

        if (data.length() > 0) {
            return true;
        } else {
            return false;
        }

    }

    public DeviceCalibration get(long id) {
        DeviceCalibration c = new DeviceCalibration();
        c.deviceId = id;

        String data = mPreferences.getString(KEY_PREFIX + id, "");
        Logr.d(TAG, "get data: " + data);
        if (data.length() > 0) {
            try {
                JSONObject obj = new JSONObject(data);

                //c.deviceId = obj.getLong(KEY_DEVICE_ID);

                c.success_rate = (float) obj.getDouble(KEY_SUCCESS_RATE);
                c.sliderP1X = (float) obj.getDouble(KEY_SLIDER_P1_X);
                c.sliderP1Y = (float) obj.getDouble(KEY_SLIDER_P1_Y);
                c.sliderP2X = (float) obj.getDouble(KEY_SLIDER_P2_X);
                c.sliderP2Y = (float) obj.getDouble(KEY_SLIDER_P2_Y);

                c.calibrationP1X = (float) obj.getDouble(KEY_CALIBRATION_P1_X);
                c.calibrationP1Y = (float) obj.getDouble(KEY_CALIBRATION_P1_Y);
                c.calibrationP2X = (float) obj.getDouble(KEY_CALIBRATION_P2_X);
                c.calibrationP2Y = (float) obj.getDouble(KEY_CALIBRATION_P2_Y);

                c.ratchetX = (float) obj.getDouble(KEY_RATCHET_X);
                c.ratchetY = (float) obj.getDouble(KEY_RATCHET_Y);
                c.ratchetR = (float) obj.getDouble(KEY_RATCHET_R);
                c.ratchetX_normalized = (float) obj.getDouble(KEY_RATCHET_X_NORMALIZED);
                c.ratchetY_normalized = (float) obj.getDouble(KEY_RATCHET_Y_NORMALIZED);

                c.scrollyX = (float) obj.getDouble(KEY_SCROLLY_X);
                c.scrollyY = (float) obj.getDouble(KEY_SCROLLY_Y);
                c.scrollyR1 = (float) obj.getDouble(KEY_SCROLLY_R1);

            } catch (Exception e) {
                Logr.d(TAG, "exception: " + e.getMessage());
                return c;
            }
        }

        return c;
    }

    public void put(DeviceCalibration c) {

        try {
            JSONObject obj = new JSONObject();

            obj.put(KEY_SUCCESS_RATE, c.success_rate);
            obj.put(KEY_DEVICE_ID, c.deviceId);

            obj.put(KEY_SLIDER_P1_X, c.sliderP1X);
            obj.put(KEY_SLIDER_P1_Y, c.sliderP1Y);
            obj.put(KEY_SLIDER_P2_X, c.sliderP2X);
            obj.put(KEY_SLIDER_P2_Y, c.sliderP2Y);

            obj.put(KEY_CALIBRATION_P1_X, c.calibrationP1X);
            obj.put(KEY_CALIBRATION_P1_Y, c.calibrationP1Y);
            obj.put(KEY_CALIBRATION_P2_X, c.calibrationP2X);
            obj.put(KEY_CALIBRATION_P2_Y, c.calibrationP2Y);

            obj.put(KEY_RATCHET_X, c.ratchetX);
            obj.put(KEY_RATCHET_Y, c.ratchetY);
            obj.put(KEY_RATCHET_R, c.ratchetR);
            obj.put(KEY_RATCHET_X_NORMALIZED, c.ratchetX_normalized);
            obj.put(KEY_RATCHET_Y_NORMALIZED, c.ratchetY_normalized);

            obj.put(KEY_SCROLLY_X, c.scrollyX);
            obj.put(KEY_SCROLLY_Y, c.scrollyY);
            obj.put(KEY_SCROLLY_R1, c.scrollyR1);


            String data = obj.toString();
            Logr.d(TAG, "put data: " + data);

            mEditor = mPreferences.edit();
            mEditor.putString(KEY_PREFIX + c.deviceId, data);
            mEditor.commit();
        } catch (Exception e) {
            Logr.d(TAG, "exception: " + e.getMessage());
        }
    }

    public static class DeviceCalibration {

        public float success_rate;
        public long deviceId;

        public float sliderP1X;
        public float sliderP1Y;
        public float sliderP2X;
        public float sliderP2Y;

        public float calibrationP1X;
        public float calibrationP1Y;
        public float calibrationP2X;
        public float calibrationP2Y;

        public float ratchetX;
        public float ratchetY;
        public float ratchetR;
        public float ratchetX_normalized;
        public float ratchetY_normalized;

        public float scrollyX;
        public float scrollyY;
        public float scrollyR1;
    }
}
