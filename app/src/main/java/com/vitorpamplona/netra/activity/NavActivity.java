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
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.vitorpamplona.core.testdevice.DeviceDataset;
import com.vitorpamplona.netra.R;
import com.vitorpamplona.netra.activity.fragments.DoTheTestByYourselfFragment;
import com.vitorpamplona.netra.activity.fragments.NavFragment;
import com.vitorpamplona.netra.activity.fragments.OdHomeFragment;
import com.vitorpamplona.netra.activity.fragments.PutPhoneOnDeviceFragment;
import com.vitorpamplona.netra.activity.fragments.ReadingsFragment;
import com.vitorpamplona.netra.activity.fragments.ResultsFragment;
import com.vitorpamplona.netra.activity.fragments.SettingsFragment;
import com.vitorpamplona.netra.activity.fragments.SimulationFragment2;
import com.vitorpamplona.netra.activity.fragments.ThreeStepsFragment;
import com.vitorpamplona.netra.activity.settings.AppSettings;
import com.vitorpamplona.netra.model.ExamResults;
import com.vitorpamplona.netra.model.db.objects.DebugExam;
import com.vitorpamplona.netra.printer.AGPPrinterAPI;
import com.vitorpamplona.netra.printer.Printer;
import com.vitorpamplona.netra.test.TestActivity;
import com.vitorpamplona.netra.utils.HardwareUtil;
import com.vitorpamplona.netra.utils.LanguageHelper;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class NavActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LanguageHelper.MainActivityCallback {

    private static final int CODE_WRITE_SETTINGS_PERMISSION = 122;

    private ImageView mPrinterButton;
    private ImageView mLanguageSelectorButton;
    private ImageView mNewCustomReadingButton;

    private View mPrinter;
    private View mLanguageSelector;
    private View mNewCustomReading;


    ActionBarDrawerToggle mToggle;
    DrawerLayout mDrawer;

    private ImageView mLogo;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mLogo = (ImageView) findViewById(R.id.logovertical);

        mPrinter = (View) findViewById(R.id.printer);
        mPrinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printLastResults();
            }
        });

        mLanguageSelector = (View) findViewById(R.id.language);
        mNewCustomReading = (View) findViewById(R.id.new_reading);
        mNewCustomReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewReading();
            }
        });
        mNewCustomReading.setVisibility(View.GONE);

        mNewCustomReadingButton = (ImageView) findViewById(R.id.new_reading_button);
        mNewCustomReadingButton.getBackground().setColorFilter(getResources().getColor(R.color.buttonsColor), PorterDuff.Mode.SRC_IN);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(mToggle);
        mToggle.syncState();

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void saveAndSetPatientLocale(Locale myLocale) {
        getApp().getSettings().setPatientLocale(myLocale.getLanguage(), myLocale.getCountry());
        if (hasFragmentActive()) {
            try {
                pushPatientLocale();
                getCurrentFragment().reloadTextsInPatientLanguage();
                restorePatientLocale();
            } catch (Exception e) {
            }
        }
    }

    public void pushPatientLocale() {
        Resources res = getResources();
        Configuration conf = res.getConfiguration();
        Locale savedLocale = conf.locale;
        conf.locale = getApp().getSettings().getPatientLocale(); // whatever you want here
        res.updateConfiguration(conf, null); // second arg null means don't change
    }

    public void restorePatientLocale() {
        // restore original locale
        Resources res = getResources();
        Configuration conf = res.getConfiguration();
        conf.locale = getApp().getSettings().getAppLocale();
        ;
        res.updateConfiguration(conf, null);
    }

    public void callLanguagePopUp(View view) {
        LanguageHelper.callLanguagePopUpFromActivity(this, view, this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!getApp().isAuthorizedToGetPhoneId()) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    0);
        }

        checkSystemSettings();
    }

    @Override
    public void onResume() {
        super.onResume();

        loadHomeFragment();
    }

    private void setSettings() {
        if (getApp().isSamsungS4()) {
            // Set Screen Timeout to 2 minutes
            // Adjust time out only if it's too small.
            String timeout = Settings.System.getString(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
            if (Integer.parseInt(timeout) < 120000)
                Settings.System.putString(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, "120000"); // timeout.

            //Settings.System.putString(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, "255"); // Max Brightness
            //Settings.System.putString(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, "1"); // Auto-brightness

            Settings.System.putString(getContentResolver(), "screen_mode_setting", "1"); // Standard Mode
            Settings.System.putString(getContentResolver(), "screen_mode_automatic_setting", "0"); // Adaptive Display False.
            Settings.System.putString(getContentResolver(), "power_saving_mode", "0");  // Do not adjust screen tone automatically
            Settings.System.putString(getContentResolver(), "multi_window_enabled", "0"); // Multi-Window false
            Settings.System.putString(getContentResolver(), "auto_adjust_touch", "0");   // Do not adjust touch.
            Settings.System.putString(getContentResolver(), "psm_battery_level", "10");  // Batery level to 10% starts power saving mode .
            Settings.System.putString(getContentResolver(), "smart_pause", "0");         // Deactivate smart Pause
            Settings.System.putString(getContentResolver(), "smart_scroll", "0");        // Deactivate Smart Scrool.
            Settings.System.putString(getContentResolver(), "intelligent_sleep_mode", "0"); // deactivate Smart Stay.
            Settings.System.putString(getContentResolver(), "display_battery_percentage", "1"); // Show battery display.
            Settings.System.putString(getContentResolver(), "e_reading_display_mode", "0");
        }
    }

    private void checkSystemSettings() {
        if (!getApp().isSamsungS4()) return;

        boolean permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = Settings.System.canWrite(this);
        } else {
            permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        }

        if (permission) {
            setSettings();
        } else {
            AlertDialog.Builder authDialog = new AlertDialog.Builder(this);
            authDialog.setTitle(R.string.permission_request_title);
            authDialog.setMessage(R.string.permission_request_desc);
            authDialog.setCancelable(true);
            authDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.setData(Uri.parse("package:com.vitorpamplona.netra2"));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        NavActivity.this.startActivityForResult(intent, NavActivity.CODE_WRITE_SETTINGS_PERMISSION);
                    } else {
                        ActivityCompat.requestPermissions(NavActivity.this, new String[]{Manifest.permission.WRITE_SETTINGS}, NavActivity.CODE_WRITE_SETTINGS_PERMISSION);
                    }
                }
            });
            authDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    System.exit(0);
                }
            });
            authDialog.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NavActivity.CODE_WRITE_SETTINGS_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setSettings();
        }
    }


    public int toPX(int dp) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (dp * scale + 0.5f);
    }

    public void showMenu() {
        mToggle.setDrawerIndicatorEnabled(true);
        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        mLogo.setPadding(0, 0, toPX(70), 0);
    }

    public void hideMenu() {
        mToggle.setDrawerIndicatorEnabled(false);
        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mLogo.setPadding(0, 0, toPX(0), 0);
    }

    private boolean isHomeFragmentActive() {
        Fragment f = getFragmentManager().findFragmentById(R.id.fragment);
        return (f instanceof OdHomeFragment);
    }

    private boolean isReadingsFragmentActive() {
        Fragment f = getFragmentManager().findFragmentById(R.id.fragment);
        return (f instanceof ReadingsFragment);
    }

    private boolean isResultFragmentActive() {
        Fragment f = getFragmentManager().findFragmentById(R.id.fragment);
        return (f instanceof ResultsFragment);
    }

    private boolean hasNoFragmentActive() {
        Fragment f = getFragmentManager().findFragmentById(R.id.fragment);
        return f == null;
    }

    private boolean hasFragmentActive() {
        Fragment f = getFragmentManager().findFragmentById(R.id.fragment);
        return f != null;
    }

    public String getGoogleServicesVersion() {
        PackageInfo googleServices = null;
        try {
            googleServices = getPackageManager().getPackageInfo("com.google.android.gms", 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (googleServices != null) {
            return googleServices.versionName;
        } else {
            return "Not Installed";
        }
    }

    public void hideLanguageButton() {
        mLanguageSelector.setVisibility(View.GONE);
    }

    public void showLanguageButton() {
        mLanguageSelector.setVisibility(View.VISIBLE);
    }

    public void hideNewCustomReadingButton() {
        mNewCustomReading.setVisibility(View.GONE);
    }

    public void showNewCustomReadingButton() {
        mNewCustomReading.setVisibility(View.VISIBLE);
    }

    public void addNewReading() {
        AppSettings settings = NetraGApplication.get().getSettings();
        ExamResults e = new ExamResults();
        e.setId(UUID.randomUUID());
        e.setExamDate(new Date());
        e.setDevice(DeviceDataset.get(404));
        if (e.getSequenceNumber() == 0) {
            e.setSequenceNumber(settings.getSequenceNumber());
            settings.setSequenceNumber(settings.getSequenceNumber() + 1);
        }
        e.setEnvironment("NETRA 2");
        e.setUserToken(settings.getLoggedInUserToken());
        e.setUserName(settings.getLoggedInUsername());
        e.setAppVersion(NetraGApplication.get().getVersionName());

        DebugExam e1 = new DebugExam(e);
        NetraGApplication.get().getSqliteHelper().saveDebugExam(e1);
        NetraGApplication.get().setLastResult(e1);

        if (isReadingsFragmentActive()) {
            ((ReadingsFragment) getCurrentFragment()).refreshAddedCard();
            loadResultsFragment(false);
        }
    }

    AGPPrinterAPI api = null;
    Printer hp = null;

    public void enablePrinterIfFound() {
        if (api != null)
            api.destroy();
        if (hp != null)
            hp.destroy();

        api = new AGPPrinterAPI(this, new AGPPrinterAPI.TryConnecting() {
            @Override
            public void isConnectable() {
                checkPrinterReady();
            }

            @Override
            public void cannotConnect() {
                checkPrinterReady();
            }
        });

        hp = new Printer(this);
        if (hp.isAvailable()) {
            showPrinterButton();
        }
    }

    public void checkPrinterReady() {
        if (isPrinterReady())
            showPrinterButton();
        else
            hidePrinterButton();
    }

    public void enablePrinterIfFound(AGPPrinterAPI.TryConnecting feedback) {
        if (api != null)
            api.destroy();
        if (hp != null)
            hp.destroy();

        api = new AGPPrinterAPI(this, feedback);
        hp = new Printer(this, feedback);
    }

    public boolean isPrinterReady() {
        return (api != null && api.isPrinterAvailable())
                || (hp != null && hp.isAvailable());
    }

    public void showPrinterButton() {
        mPrinter.setVisibility(View.VISIBLE);
    }

    public void hidePrinterButton() {
        mPrinter.setVisibility(View.GONE);
        if (api != null)
            api.destroy();
        api = null;
        if (hp != null)
            hp.destroy();
        hp = null;
    }

    public void printLastResults() {
        if (this.getWindow() != null && this.getWindow().getCurrentFocus() != null) {
            this.getWindow().getCurrentFocus().clearFocus();
            hideKeyboard();
        }

        if (api != null && api.isPrinterAvailable()) {
            api.print(NetraGApplication.get().getLastResult());
        } else if (hp != null) {
            hp.print(NetraGApplication.get().getLastResult());
        }
    }

    public void printResults(DebugExam exam) {
        if (api != null && api.isPrinterAvailable()) {
            api.print(exam);
        } else if (hp != null) {
            hp.print(exam);
        }
    }


    public void hideKeyboard() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    public void showKeyboardDontAdjust() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }


    public void loadHomeFragment() {
        hideKeyboard();

        transitionFragment(new OdHomeFragment());

        System.gc();
    }

    public void loadResultsFragment(boolean goBackHome) {
        transitionFragment(new ResultsFragment().setHomeWhenDone(goBackHome));
    }

    ReadingsFragment r = new ReadingsFragment();

    public void loadReadingsFragment(boolean resetPosition) {
        if (resetPosition) r.backToTop();
        transitionFragment(r);
    }

    public void loadSettingsFragment() {
        transitionFragment(new SettingsFragment());
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            getCurrentFragment().onBackPressed();
        }
    }

    public void loadTest() {
        hidePrinterButton();
        hideLanguageButton();
        startActivityForResult(new Intent(this, TestActivity.class), 123);
    }

    public void loadSimulation() {
        transitionFragment(new SimulationFragment2());
    }

    public void loadPreSimulation() {
        transitionFragment(new DoTheTestByYourselfFragment());
    }


    public void loadThreeSteps() {
        transitionFragment(new ThreeStepsFragment());
    }

    public void loadInsertDevice() {
        transitionFragment(new PutPhoneOnDeviceFragment());
    }

    public NetraGApplication getApp() {
        return (NetraGApplication) getApplication();
    }

    public void transitionFragment(NavFragment f) {
        if (getCurrentFragment() != null && getCurrentFragment().getId() == f.getId()) return;

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);
        transaction.replace(R.id.fragment, f);
        transaction.commit();
    }

    private NavFragment getCurrentFragment() {
        return (NavFragment) getFragmentManager().findFragmentById(R.id.fragment);
    }

    public void showBatteryWarning() {
        AlertDialog.Builder authDialog = new AlertDialog.Builder(this);
        authDialog.setTitle(R.string.error_battery_below_20_title);
        authDialog.setMessage(R.string.error_battery_below_20_desc);
        authDialog.setCancelable(true);
        authDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        authDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        authDialog.show();
    }

    public boolean hasBattery() {
        return HardwareUtil.getBatteryPercent(this) > 0.2;
    }

    // ************** ************** ************** **************
    // Possible Back from the TEST, show results if yes.
    // ************** ************** ************** **************

    private boolean mReturningWithResults = false;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 123) {
            // Cannot change Fragments on the onActivityResult method
            mReturningWithResults = true;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requestCode == NavActivity.CODE_WRITE_SETTINGS_PERMISSION && Settings.System.canWrite(this)) {
                setSettings();
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mReturningWithResults) {
            // Commit your transactions here.
            loadResultsFragment(true);
        }
        // Reset the boolean flag back to false for next time.
        mReturningWithResults = false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            loadHomeFragment();
        } else if (id == R.id.nav_readings) {
            loadReadingsFragment(true);
        } else if (id == R.id.nav_settings) {
            loadSettingsFragment();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
