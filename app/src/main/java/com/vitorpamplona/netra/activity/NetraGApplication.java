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
package com.vitorpamplona.netra.activity;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonemetadata;
import com.google.i18n.phonenumbers.Phonenumber;
import com.vitorpamplona.core.testdevice.ui.CachedBitmapFactory;
import com.vitorpamplona.core.utils.DeviceModelParser;
import com.vitorpamplona.netra.BuildConfig;
import com.vitorpamplona.netra.activity.settings.AppSettings;
import com.vitorpamplona.netra.model.db.SQLiteHelper;
import com.vitorpamplona.netra.model.db.objects.DebugExam;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class NetraGApplication extends Application {

    public static final boolean DEVELOPER_MODE = BuildConfig.DEBUG;

    private static NetraGApplication mSingleton;
    private AppSettings mAppSettings;
    private DebugExam mLastResult;

    private SQLiteHelper mSqliteHelper, mDevSqliteHelper;

    public static interface OnSync {
        public void afterSync();
    }

    public String getStorageRoot() {
        return Environment.getExternalStorageDirectory() + "/EyeNetra/";
    }

    public String getLocalFilePath() {
        return getStorageRoot() + getSettings().getOrgName() + "/";
    }

    public String getLocalLogoPath() {
        return getLocalFilePath() + "Logos/";
    }

    public String getLocalMeasurementsPath() {
        return getLocalFilePath() + "Measurements/";
    }

    public String getLocalLastMeasurementsPath() {
        return getLocalFilePath() + "Last Measurement/";
    }

    public String getLocalToPrintPath() {
        return getLocalFilePath() + "Print/";
    }

    public String getLocalToExportPath() {
        return getLocalFilePath() + "Export/";
    }

    public String getLocalWallpaperPath() {
        return getStorageRoot() + "Wallpaper/";
    }


    @Override
    public void onCreate() {
        super.onCreate();

        getSettings();

        CachedBitmapFactory.setResources(getResources());

        mSqliteHelper = new SQLiteHelper(this, false);
        mDevSqliteHelper = new SQLiteHelper(this, true);

        resetDefaultNumbers();

        restoreAppLocale();

        mSingleton = this;
    }

    public void resetDefaultNumbers() {
        PhoneNumberUtil mPhoneUtil = PhoneNumberUtil.getInstance();
        try {
            Method getMetadata = PhoneNumberUtil.class.getDeclaredMethod("getMetadataForRegion", String.class);
            getMetadata.setAccessible(true);

            for (String region : mPhoneUtil.getSupportedRegions()) {
                Phonenumber.PhoneNumber exampleNumber = mPhoneUtil.getExampleNumberForType(region, PhoneNumberUtil.PhoneNumberType.MOBILE);
                String logExample = null;
                if (exampleNumber != null)
                    logExample = mPhoneUtil.format(exampleNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);

                Phonemetadata.PhoneMetadata desc = (Phonemetadata.PhoneMetadata) getMetadata.invoke(mPhoneUtil, region);
                String example = desc.getMobile().getExampleNumber();
                String newExample = example;
                newExample = newExample.replaceAll("123456789", "000000000");
                newExample = newExample.replaceAll("12345678", "00000000");
                newExample = newExample.replaceAll("1234567", "0000000");
                newExample = newExample.replaceAll("123456", "000000");
                newExample = newExample.replaceAll("12345", "00000");
                newExample = newExample.replaceAll("1234", "0000");
                newExample = newExample.replaceAll("123", "000");
                newExample = newExample.replaceAll("12", "00");
                //String newExample = example.replaceAll("[0-9]", "0");
                desc.getMobile().setExampleNumber(newExample);

                Phonenumber.PhoneNumber newExampleNumber = mPhoneUtil.getExampleNumberForType(region, PhoneNumberUtil.PhoneNumberType.MOBILE);
                String logNewExample = null;
                if (newExampleNumber != null)
                    logNewExample = mPhoneUtil.format(
                            newExampleNumber,
                            PhoneNumberUtil.PhoneNumberFormat.NATIONAL);

                Log.v("Default Numbers", region + ": " + logExample + " -> " + logNewExample);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void restoreAppLocale() {
        // restore original locale
        Resources res = getResources();
        Configuration conf = res.getConfiguration();
        conf.locale = getSettings().getAppLocale();
        ;
        res.updateConfiguration(conf, null);
    }

    /*
     * isOnline - Check if there is a NetworkConnection
     * @return boolean
     */
    public static boolean isReachable(String addr, int openPort, int timeOutMillis) {
        // Any Open port on other machine
        // openPort =  22 - ssh, 80 or 443 - webserver, 25 - mailserver etc.
        try {
            try (Socket soc = new Socket()) {
                soc.connect(new InetSocketAddress(addr, openPort), timeOutMillis);
                soc.close();
            }
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    public boolean shouldStartBluetooth() {
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.N;
    }

    public SQLiteHelper getSqliteHelper() {
        return getSqliteHelper(DEVELOPER_MODE);
    }

    public SQLiteHelper getSqliteHelper(boolean isDev) {
        return (isDev) ? mDevSqliteHelper : mSqliteHelper;
    }

    public static NetraGApplication get() {
        return mSingleton;
    }

    public AppSettings getSettings() {
        if (mAppSettings == null) {
            mAppSettings = new AppSettings(this);
        }

        return mAppSettings;
    }

    public String getVersionName() {
        String versionName = "";
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception e) {
            //TODO?
        }
        return versionName;
    }

    public DebugExam getLastResult() {
        return mLastResult;
    }

    public void setLastResult(DebugExam mLastResult) {
        this.mLastResult = mLastResult;
    }

    String[] s4s = new String[]{"Samsung GT-I9505G", "Samsung GT-I9505", "Samsung GT-I9500",
            "Samsung GT-I9508", "Samsung SAMSUNG-SGH-I337", "Samsung SGH-I337M", "Samsung GT-I9515L", "Samsung SCH-I545", "Samsung SGH-M919N", "Samsung SGH-M919"};

    public boolean isSamsungS4() {
        String deviceModel = DeviceModelParser.getDeviceName();

        List<String> listS4s = Arrays.asList(s4s);

        return listS4s.contains(deviceModel);
    }

    public boolean isAuthorizedToGetPhoneId() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }
}