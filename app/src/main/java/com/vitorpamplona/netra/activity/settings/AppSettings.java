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
package com.vitorpamplona.netra.activity.settings;

import android.content.Context;

import java.util.Locale;

public class AppSettings extends AppSettingsBase {

    public static enum ACCURACY {REPEAT_18, REPEAT_34}

    private static String SETTING_LOGGED_IN_USER_TOKEN = "account.id";
    private static String SETTING_LOGGED_IN_USER_USERNAME = "account.username";

    private static String SETTING_DEVICE_ID = "device.id";

    private static String SETTING_SEQUENCE_NUMBER = "sequence_number";


    private final String SETTING_KEY_TELE_RX_SYNC = "account.sync";


    private final String SETTING_PATIENT_LOCALE_LANG = "patient_locale_language";
    private final String SETTING_PATIENT_LOCALE_COUNTRY = "patient_locale_country";

    private final String SETTING_APP_LOCALE_LANG = "app_locale_language";
    private final String SETTING_APP_LOCALE_COUNTRY = "app_locale_country";

    private final String SETTING_NEGATIVE_CYLS = "negative_cyls";
    private final String SETTING_IMPERIAL_SYSTEM = "imperial_system";
    private final String SETTING_SPHEROCYLINDRICAL_CORRECTIONS = "spherocylindrical";

    private final String SETTING_SHOW_NUMBERS = "show_numbers";
    private final String SETTING_TRAIN_ANGLES = "do_training_angles";
    private final String SETTING_ROUND_RESULTS = "round_results";

    private final String SETTING_REPETITIONS = "repetitions";

    private final String SETTING_FIRST_NAME = "user_first_name";
    private final String SETTING_LAST_NAME = "user_last_name";
    private final String SETTING_CAN_PRESCRIBE = "user_can_prescribe";
    private final String SETTING_ORG_NAME = "user_org_name";
    private final String SETTING_LOGO_REMOTE_PATH = "user_logo_remote_path";
    private final String SETTING_LOGO_LOCAL_PATH = "user_logo_local_path";
    private final String SETTING_LOGO_LAST_MODIFIED = "user_logo_last_modified";
    private final String SETTING_LOGO_ACTIVE = "user_logo_active";

    private final String SETTING_LAST_FLAG = "last_flag";

    public AppSettings(Context context) {
        super(context);
    }

    public String getLoggedInUserToken() {
        return getString(SETTING_LOGGED_IN_USER_TOKEN, null);
    }

    public void setLoggedInUserToken(String userId) {
        setString(SETTING_LOGGED_IN_USER_TOKEN, userId);
    }

    public String getLoggedInUsername() {
        return getString(SETTING_LOGGED_IN_USER_USERNAME, "guest");
    }

    public void setLoggedInUsername(String username) {
        setString(SETTING_LOGGED_IN_USER_USERNAME, username);
    }

    public void setDeviceId(long id) {
        // No public versions before 404. Avoid invalid calibrations by setting it to 404
        if (id < 404) {
            id = 404;
        }

        setInt(SETTING_DEVICE_ID, (int) id);
    }

    public long getDeviceId() {
        return getInt(SETTING_DEVICE_ID, 404);
    }

    public boolean isDeviceKnown() {
        return getDeviceId() > 0 && getDeviceId() != 404;
    }


    public int getSequenceNumber() {
        int i = getInt(SETTING_SEQUENCE_NUMBER, 1);
        return (i > 0) ? i : 1;
    }

    public void setSequenceNumber(int i) {
        if (i > 0) {
            setInt(SETTING_SEQUENCE_NUMBER, i);
        } else {
            setInt(SETTING_SEQUENCE_NUMBER, 1);
        }
    }

    public boolean isSync() {
        return getBoolean(SETTING_KEY_TELE_RX_SYNC, true);
    }

    public void setTeleRxSync(boolean b) {
        setBoolean(SETTING_KEY_TELE_RX_SYNC, b);
    }

    public boolean isImperialSystem() {
        return getBoolean(SETTING_IMPERIAL_SYSTEM, true);
    }

    public void setImperialSystem(boolean b) {
        setBoolean(SETTING_IMPERIAL_SYSTEM, b);
    }

    public void toggleImperialSystem() {
        setImperialSystem(!isImperialSystem());
    }

    public boolean isShowNumbers() {
        return getBoolean(SETTING_SHOW_NUMBERS, false);
    }

    public void setShowNumbers(boolean b) {
        setBoolean(SETTING_SHOW_NUMBERS, b);
    }

    public boolean isTrainingAngles() {
        return getBoolean(SETTING_TRAIN_ANGLES, false);
    }

    public void setTrainingAngles(boolean b) {
        setBoolean(SETTING_TRAIN_ANGLES, b);
    }

    public boolean is18Repetitions() {
        return getInt(SETTING_REPETITIONS, ACCURACY.REPEAT_18.ordinal()) == ACCURACY.REPEAT_18.ordinal();
    }

    public boolean is34Repetitions() {
        return getInt(SETTING_REPETITIONS, ACCURACY.REPEAT_18.ordinal()) == ACCURACY.REPEAT_34.ordinal();
    }

    public void setRepetitions(ACCURACY b) {
        setInt(SETTING_REPETITIONS, b.ordinal());
    }


    public boolean isRoundResults() {
        return getBoolean(SETTING_ROUND_RESULTS, true);
    }

    public void setRoundResults(boolean b) {
        setBoolean(SETTING_ROUND_RESULTS, b);
    }

    public boolean isNegativeCylModel() {
        return getBoolean(SETTING_NEGATIVE_CYLS, true);
    }

    public void setNegativeCylModel(boolean b) {
        setBoolean(SETTING_NEGATIVE_CYLS, b);
    }

    public void toggleCylModel() {
        setNegativeCylModel(!isNegativeCylModel());
    }

    public boolean isCanPrescribe() {
        return getBoolean(SETTING_CAN_PRESCRIBE, false);
    }

    public void setCanPrescribe(boolean b) {
        setBoolean(SETTING_CAN_PRESCRIBE, b);
    }

    public boolean isSpherocylindricalMode() {
        return getBoolean(SETTING_SPHEROCYLINDRICAL_CORRECTIONS, true);
    }

    public void setSpherocylindricalMode(boolean b) {
        setBoolean(SETTING_SPHEROCYLINDRICAL_CORRECTIONS, b);
    }

    public void toggleSpherocylindricalMode() {
        setSpherocylindricalMode(!isSpherocylindricalMode());
    }

    public void setPatientLocale(String lang, String country) {
        setString(SETTING_PATIENT_LOCALE_LANG, lang);
        setString(SETTING_PATIENT_LOCALE_COUNTRY, country);
    }

    public Locale getPatientLocale() {
        return new Locale(
                getString(SETTING_PATIENT_LOCALE_LANG, Locale.getDefault().getLanguage()),
                getString(SETTING_PATIENT_LOCALE_COUNTRY, Locale.getDefault().getCountry()));
    }

    public void setAppLocale(String lang, String country) {
        setString(SETTING_APP_LOCALE_LANG, lang);
        setString(SETTING_APP_LOCALE_COUNTRY, country);
    }

    public Locale getAppLocale() {
        return new Locale(
                getString(SETTING_APP_LOCALE_LANG, Locale.getDefault().getLanguage()),
                getString(SETTING_APP_LOCALE_COUNTRY, Locale.getDefault().getCountry()));
    }

    public String getLastFlag() {
        return getString(SETTING_LAST_FLAG, null);
    }

    public void setLastFlag(String s) {
        setString(SETTING_LAST_FLAG, s);
    }

    public String getOrgName() {
        return getString(SETTING_ORG_NAME, "");
    }

    public void setOrgName(String s) {
        setString(SETTING_ORG_NAME, s);
    }
}
