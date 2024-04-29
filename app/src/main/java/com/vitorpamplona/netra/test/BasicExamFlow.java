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
package com.vitorpamplona.netra.test;

import android.content.pm.PackageManager;

import com.vitorpamplona.core.testdevice.DeviceDataset;
import com.vitorpamplona.core.testdevice.DeviceDataset.Device;
import com.vitorpamplona.netra.activity.NetraGApplication;
import com.vitorpamplona.netra.model.ExamResults;

/**
 * Class to manage the connectivity and basic steps of the flow. 
 *
 * Subclasses implement given test flows
 */
public abstract class BasicExamFlow implements IExamFlow {

    private Device mDev;
    private TestActivity mContext;

    private boolean mHasExamStarted = false;
    private boolean mIsExamRunning = false;

    public BasicExamFlow(TestActivity examContext) {
        mContext = examContext;
        mDev = DeviceDataset.get(getSettingsDeviceID());
    }

    public abstract void startExam();

    public abstract void resumeExam();

    public void onCalibrationDone(float angle, float pd, final long barcodeID) {
        if (!mIsExamRunning) {
            mIsExamRunning = true;
            startExam();
        } else {
            resumeExam();
        }

        validateBarcodeId(barcodeID);
    }

    public abstract boolean wasPDMeasured();

    public abstract void forceFinishExamWithPDResults();

    public boolean hasExamStarted() {
        return mHasExamStarted;
    }

    public boolean isExamRunning() {
        return mIsExamRunning;
    }

    private void validateBarcodeId(long barcodeId) {
        if (barcodeId <= 0) {
            getResultsBuilder().logFailedBarcodeIDReading(barcodeId);
            // Unable to confirm the Serial ID, but calibrated correctly. Continue the test with the previous one.
			/*
            AlertDialog alert = new AlertDialog.Builder(mContext)
                    .setTitle("Device serial number not recognized. NETRA-G unit may need service.")
                    .setPositiveButton("Finish Test", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        	// TODO
                            //mContext.cancelExam();
                        }
                    })
                    .show();
			*/
        } else if (barcodeId != mDev.id) {
            NetraGApplication.get().getSettings().setDeviceId(barcodeId);
            mDev = DeviceDataset.get(getSettingsDeviceID());
            getResultsBuilder().setDeviceId(mDev);

            try {
                getResultsBuilder().logAppVersions(getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        } else {
            try {
                getResultsBuilder().logAppVersions(getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public long getSettingsDeviceID() {
        return NetraGApplication.get().getSettings().getDeviceId();
    }

    public TestActivity getContext() {
        return mContext;
    }

    public Device getDevice() {
        return mDev;
    }

    public abstract ExamResultBuilder getResultsBuilder();

    public ExamResults getExamResults() {
        return getResultsBuilder().getExamResults();
    }

    public void setLatLng(Double lat, Double lng) {
        getResultsBuilder().setLocation(lat, lng);
    }

}
