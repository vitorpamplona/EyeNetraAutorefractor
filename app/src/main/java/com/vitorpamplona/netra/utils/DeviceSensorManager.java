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
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

import com.vitorpamplona.core.testdevice.DeviceModelSettings;
import com.vitorpamplona.core.utils.DeviceModelParser;

public class DeviceSensorManager {
    public float lowLightThreshold = 1;
    public float highLightThreshold = 10;

    private SensorManager mSensorManager;
    private Sensor mLightSensor;
    private Float mLastLightSensorValue;

    PhoneInserted phoneInsertedListener;
    PhoneInserted phoneRemovedListener;

    private boolean isSensorAvailable;

    public DeviceSensorManager(Context ctx, PhoneInserted phoneInsertedListener, PhoneInserted phoneRemovedListener) {
        mSensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
        mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        String nameOfDevice = DeviceModelParser.getDeviceName();
        DeviceModelSettings lightLevels = new DeviceModelSettings(nameOfDevice);

        if (lightLevels != null) {
            lowLightThreshold = lightLevels.getLowLightThreshold();
            highLightThreshold = lightLevels.getHighLightThreshold();
        }

        isSensorAvailable = (mLightSensor != null);
        if (!isSensorAvailable)
            Toast.makeText(ctx, "Light Sensor Not Found", Toast.LENGTH_LONG).show();

        this.phoneInsertedListener = phoneInsertedListener;
        this.phoneRemovedListener = phoneRemovedListener;
    }

    private SensorEventListener lightSensorEventListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (isLightSensor(event)) {

                if (isCompletelyDark(event.values[0]) && !wasAlreadyDark()) {
                    mLastLightSensorValue = event.values[0];

                    if (phoneInsertedListener != null)
                        phoneInsertedListener.run();
                } else if (isCompletelyBright(event.values[0]) && wasAlreadyDark()) {
                    mLastLightSensorValue = event.values[0];

                    if (phoneRemovedListener != null)
                        phoneRemovedListener.run();
                }
            }
        }
    };

    private boolean isLightSensor(SensorEvent event) {
        return event.sensor.getType() == Sensor.TYPE_LIGHT;
    }

    private boolean isCompletelyDark(float sensorValue) {
        return sensorValue < lowLightThreshold;
    }

    private boolean isCompletelyBright(float sensorValue) {
        return sensorValue > highLightThreshold;
    }

    private boolean wasAlreadyDark() {
        return mLastLightSensorValue != null && isCompletelyDark(mLastLightSensorValue);
    }

    public void activate() {
        mSensorManager.registerListener(lightSensorEventListener, mLightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void deactivate() {
        mSensorManager.unregisterListener(lightSensorEventListener);
    }

    public interface PhoneInserted {
        public void run();
    }
}
