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
package com.vitorpamplona.netra.activity.fragments;

import android.app.Fragment;
import android.app.Service;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.vitorpamplona.netra.activity.NavActivity;
import com.vitorpamplona.netra.activity.settings.AppSettings;

import java.util.List;
import java.util.Locale;

public abstract class NavFragment extends Fragment {

    protected View mRootView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, int layoutId) {
        mRootView = inflater.inflate(layoutId, container, false);

        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getNavActivity().pushPatientLocale();
        reloadTextsInPatientLanguage();
        getNavActivity().restorePatientLocale();
    }

    public boolean onBackPressed() {
        return false;
    }

    public boolean shouldShowActionBar() {
        return true;
    }

    protected NavActivity getNavActivity() {
        return (NavActivity) getActivity();
    }

    public abstract void reloadTextsInPatientLanguage();

    public String getPatientResource(int id) {
        Resources res = getResources();
        Configuration conf = res.getConfiguration();
        Locale savedLocale = conf.locale;
        conf.locale = getNavActivity().getApp().getSettings().getPatientLocale(); // whatever you want here
        res.updateConfiguration(conf, null); // second arg null means don't change

        // retrieve resources from desired locale
        String str = res.getString(id);

        // restore original locale
        conf.locale = savedLocale;
        res.updateConfiguration(conf, null);

        return str;
    }

    public void processVoice(List<String> said, boolean rightEye) {
        Toast.makeText(this.getNavActivity(), said.get(0), Toast.LENGTH_LONG).show();
    }

    protected AppSettings getSettings() {
        return getNavActivity().getApp().getSettings();
    }

    protected void showKeyboard(View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }

    protected void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mRootView.getWindowToken(), 0);
    }

    public void afterSync() {

    }
}