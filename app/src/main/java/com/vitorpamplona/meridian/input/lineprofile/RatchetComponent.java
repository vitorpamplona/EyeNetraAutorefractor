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
package com.vitorpamplona.meridian.input.lineprofile;

import com.vitorpamplona.core.testdevice.DeviceDataset.Device;
import com.vitorpamplona.core.utils.AngleChange360Dispatcher;
import com.vitorpamplona.core.utils.NoiseRemovalStack;
import com.vitorpamplona.core.utils.NumberChangeDispatcher.NumberChangedListener;
import com.vitorpamplona.meridian.imgproc.lineprofile.ErrorCode;
import com.vitorpamplona.meridian.imgproc.lineprofile.Rotational90DegFinder;
import com.vitorpamplona.meridian.lineprofile.FrameDebugData;
import com.vitorpamplona.meridian.utils.Logr;
import com.vitorpamplona.meridian.utils.SignalNormalizer;
import com.vitorpamplona.meridian.utils.YuvFilter;

import java.util.ArrayList;
import java.util.List;

public class RatchetComponent extends AngleComponent {

    private static final String TAG = RatchetComponent.class.getSimpleName();

    private static final int ANGLE_STACK_REQUIRED_STD_DEV = 2; // degrees
    private static final int ANGLE_STACK_SIZE = 4;
    private static final int ANGLE_STEPS_IN = 1; // degrees

    private List<RatchetListener> listeners = new ArrayList<RatchetListener>();


    public RatchetComponent(Device device, FrameDebugData debugInfo, YuvFilter colorFilter) {
        super(new Rotational90DegFinder(device, debugInfo, colorFilter));
        reset();
    }

    @Override
    public void reset() {
        NoiseRemovalStack angleStack = new NoiseRemovalStack(ANGLE_STACK_REQUIRED_STD_DEV, ANGLE_STACK_SIZE);
        AngleChange360Dispatcher angleChangeTrigger = new AngleChange360Dispatcher(ANGLE_STEPS_IN);
        angleChangeTrigger.add(new NumberChangedListener() {
            @Override
            public void changed(double from, double to, int steps) {
                Logr.d(TAG, "New Meridian to " + to + " from " + from + " which is equivalent of " + steps + " steps");
                refreshListeners(to);
            }
        });

        reset(angleStack, angleChangeTrigger);
    }

    public interface RatchetListener {
        public void meridianChange(double to);
    }

    public void refreshListeners() {
        refreshListeners(getAngle());
    }

    private void refreshListeners(double to) {
        for (RatchetListener l : listeners) {
            l.meridianChange(to);
        }
    }

    public void add(RatchetListener listener) {
        this.listeners.add(listener);
    }

    public void remove(RatchetListener listener) {
        this.listeners.remove(listener);
    }

    ErrorCode intermediateErrorCode;

    public ErrorCode process(byte[] grayscale, SignalNormalizer signalNormalizer) {

        intermediateErrorCode = processWheel(grayscale, signalNormalizer);

        if (intermediateErrorCode.errorCode <= -70) {
            switch (intermediateErrorCode) {
                case C_NOT_READY:
                    return ErrorCode.RAC_NOT_READY;
                case C_NULL_NAN:
                    return ErrorCode.RAC_NULL_NAN;
                default:
                    return ErrorCode.UNDEFINED;
            }
        } else {
            return intermediateErrorCode;
        }

    }

}