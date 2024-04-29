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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.vitorpamplona.netra.R;
import com.vitorpamplona.netra.activity.NetraGApplication;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;

public class LanguageHelper {

    public static class LocaleMapItem {
        public Locale loc;
        public int icon;
        public int string;

        public LocaleMapItem(Locale loc, int icon, int string) {
            this.loc = loc;
            this.icon = icon;
            this.string = string;
        }
    }

    static HashMap<Integer, LocaleMapItem> langLocaleMap = new HashMap<Integer, LocaleMapItem>() {{
        put(R.id.lang_english_US, new LocaleMapItem(Locale.US, R.drawable.usa, R.string.english_us));
        put(R.id.lang_chinese_Simplified, new LocaleMapItem(new Locale("zn", "CN"), R.drawable.china, R.string.chinese_Sim));
        put(R.id.lang_japanese, new LocaleMapItem(new Locale("ja", "JP"), R.drawable.japan, R.string.japanese));
        put(R.id.lang_vietnamese, new LocaleMapItem(new Locale("vi", "VN"), R.drawable.vietnam, R.string.vietnamese));
        put(R.id.lang_french_France, new LocaleMapItem(Locale.FRANCE, R.drawable.france, R.string.french_FR));
        put(R.id.lang_spanish_Spain, new LocaleMapItem(new Locale("es", "ES"), R.drawable.spain, R.string.spanish_ES));
        put(R.id.lang_spanish_US, new LocaleMapItem(new Locale("es", "US"), R.drawable.mexico, R.string.spanish_us));
        put(R.id.lang_portuguese_Brasil, new LocaleMapItem(new Locale("pt", "BR"), R.drawable.brazil, R.string.portuguese_BR));
        put(R.id.lang_german_Germany, new LocaleMapItem(Locale.GERMANY, R.drawable.germany, R.string.german_DE));
        put(R.id.lang_italian_Italy, new LocaleMapItem(Locale.ITALY, R.drawable.italy, R.string.italy_IT));
        put(R.id.lang_nepali, new LocaleMapItem(new Locale("ne", "NP"), R.drawable.nepal, R.string.nepali));
        put(R.id.lang_bengali, new LocaleMapItem(new Locale("bn", "IN"), R.drawable.india, R.string.bengali));
        put(R.id.lang_swahili_Tanzania, new LocaleMapItem(new Locale("sw", "TZ"), R.drawable.tanzania, R.string.swahili_TZ));
    }};

    public static void loadSelectedAppLanguageSettingsPage(SettingsCallback act) {
        Locale appLocale = NetraGApplication.get().getSettings().getAppLocale();

        for (Integer i : langLocaleMap.keySet()) {
            LocaleMapItem loc = langLocaleMap.get(i);
            checkAppLanguageAndSet(act, appLocale, loc.loc, loc.icon, loc.string);
        }
    }

    public static void loadSelectedPatientLanguageSettingsPage(SettingsCallback act) {
        Locale patLocale = NetraGApplication.get().getSettings().getPatientLocale();
        for (Integer i : langLocaleMap.keySet()) {
            LocaleMapItem loc = langLocaleMap.get(i);
            checkPatientLanguageAndSet(act, patLocale, loc.loc, loc.icon, loc.string);
        }
    }

    public static void appLanguagePopUpReturnSettingsPage(int itemId, SettingsCallback act) {
        LocaleMapItem lang = langLocaleMap.get(itemId);
        if (lang != null)
            act.setAppLocale(lang.loc);
    }

    public static void patientLanguagePopUpReturnSettingsPage(int itemId, SettingsCallback act) {
        LocaleMapItem lang = langLocaleMap.get(itemId);
        if (lang != null)
            act.setPatientLocale(lang.loc);
    }

    // Settings

    public static void callPatientLanguagePopUp(Context ctx, View view, final SettingsCallback act) {
        PopupMenu popup = new PopupMenu(ctx, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.language, popup.getMenu());

        // Enabling Icons
        try {
            Field field = popup.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            Object menuPopupHelper = field.get(popup);
            Class<?> cls = Class.forName("com.android.internal.view.menu.MenuPopupHelper");
            Method method = cls.getDeclaredMethod("setForceShowIcon", new Class[]{boolean.class});
            method.setAccessible(true);
            method.invoke(menuPopupHelper, new Object[]{true});
        } catch (Exception e) {
            e.printStackTrace();
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                patientLanguagePopUpReturnSettingsPage(item.getItemId(), act);
                return true;
            }
        });
        popup.show();
    }

    public static void callAppLanguagePopUp(Context ctx, View view, final SettingsCallback act) {
        final PopupMenu popup = new PopupMenu(ctx, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.language, popup.getMenu());

        // Enabling Icons
        try {
            Field field = popup.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            Object menuPopupHelper = field.get(popup);
            Class<?> cls = Class.forName("com.android.internal.view.menu.MenuPopupHelper");
            Method method = cls.getDeclaredMethod("setForceShowIcon", new Class[]{boolean.class});
            method.setAccessible(true);
            method.invoke(menuPopupHelper, new Object[]{true});
        } catch (Exception e) {
            e.printStackTrace();
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                appLanguagePopUpReturnSettingsPage(item.getItemId(), act);
                return true;
            }
        });
        popup.show();
    }

    public static void checkAppLanguageAndSet(SettingsCallback act, Locale appLocale, Locale checking, int chekingImg, int checkingTxt) {
        if (appLocale.getLanguage().equals(checking.getLanguage()) && appLocale.getCountry().equals(checking.getCountry())) {
            act.setAppLanguage(chekingImg, checkingTxt);
        }
    }

    public static void checkPatientLanguageAndSet(SettingsCallback act, Locale appLocale, Locale checking, int chekingImg, int checkingTxt) {
        if (appLocale.getLanguage().equals(checking.getLanguage()) && appLocale.getCountry().equals(checking.getCountry())) {
            act.setPatientLanguage(chekingImg, checkingTxt);
        }
    }

    public static interface SettingsCallback {
        public void setAppLanguage(int chekingImg, int checkingTxt);

        public void setPatientLanguage(int chekingImg, int checkingTxt);

        public void setAppLocale(Locale l);

        public void setPatientLocale(Locale l);
    }


    // **
    // Activity
    // **

    public static void callLanguagePopUpFromActivity(Context ctx, View view, final MainActivityCallback act) {
        PopupMenu popup = new PopupMenu(ctx, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.language, popup.getMenu());

        // Enabling Icons
        try {
            Field field = popup.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            Object menuPopupHelper = field.get(popup);
            Class<?> cls = Class.forName("com.android.internal.view.menu.MenuPopupHelper");
            Method method = cls.getDeclaredMethod("setForceShowIcon", new Class[]{boolean.class});
            method.setAccessible(true);
            method.invoke(menuPopupHelper, new Object[]{true});
        } catch (Exception e) {
            e.printStackTrace();
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                saveAndSetPatientLocale(itemId, act);
                return true;
            }
        });
        popup.show();
    }

    private static void saveAndSetPatientLocale(int itemId, MainActivityCallback act) {
        LocaleMapItem lang = langLocaleMap.get(itemId);
        if (lang != null)
            act.saveAndSetPatientLocale(lang.loc);
    }

    public static interface MainActivityCallback {
        public void saveAndSetPatientLocale(Locale l);
    }
}


