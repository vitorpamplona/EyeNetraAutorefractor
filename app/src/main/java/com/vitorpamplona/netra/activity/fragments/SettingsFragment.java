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

import static com.vitorpamplona.netra.utils.LanguageHelper.loadSelectedAppLanguageSettingsPage;
import static com.vitorpamplona.netra.utils.LanguageHelper.loadSelectedPatientLanguageSettingsPage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.vitorpamplona.netra.R;
import com.vitorpamplona.netra.activity.settings.AppSettings;
import com.vitorpamplona.netra.utils.LanguageHelper;

import java.util.Locale;

public class SettingsFragment extends NavFragment implements LanguageHelper.SettingsCallback {

    private TextView mInstructions1;

    private RadioButton rbSphCyl;
    private RadioButton rbSphEq;
    private RadioButton rbMinusCyl;
    private RadioButton rbPlusCyl;
    private RadioButton rbAcuityImperial;
    private RadioButton rbAcuityMetric;

    private RadioButton rbNumbersYes;
    private RadioButton rbNumbersNo;

    private RadioButton rbTrainingAnglesYes;
    private RadioButton rbTrainingAnglesNo;

    private RadioButton rbRounding025;
    private RadioButton rbRounding001;

    private RadioButton rb18Times;
    private RadioButton rb34Times;

    private ImageButton imAppLanguage;
    private TextView txAppLanguage;
    private ImageButton imPatientLanguage;
    private TextView txPatientLanguage;

    private LinearLayout llPatientLanguage;
    private LinearLayout llAppLanguage;

    private TextView txShowNumbers;
    private TextView txTrainingAngles;
    private TextView txRounding;
    private TextView txRepetitions;

    private Button mHome;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nav_settings, container, false);

        rbSphCyl = (RadioButton) view.findViewById(R.id.rbSphCyl);
        rbSphEq = (RadioButton) view.findViewById(R.id.rbSphEq);
        ;
        rbMinusCyl = (RadioButton) view.findViewById(R.id.rbMinusCyl);
        ;
        rbPlusCyl = (RadioButton) view.findViewById(R.id.rbPlusCyl);
        rbAcuityImperial = (RadioButton) view.findViewById(R.id.rbAcuityImperial);
        rbAcuityMetric = (RadioButton) view.findViewById(R.id.rbAcuityMetric);

        imAppLanguage = (ImageButton) view.findViewById(R.id.imAppLocale);
        txAppLanguage = (TextView) view.findViewById(R.id.txAppLanguageText);
        imPatientLanguage = (ImageButton) view.findViewById(R.id.imPatientLocale);
        txPatientLanguage = (TextView) view.findViewById(R.id.txPatientLanguageText);

        llPatientLanguage = (LinearLayout) view.findViewById(R.id.llPatientLanguage);
        llAppLanguage = (LinearLayout) view.findViewById(R.id.llAppLanguage);

        rbNumbersYes = (RadioButton) view.findViewById(R.id.rbNumbersYes);
        rbNumbersNo = (RadioButton) view.findViewById(R.id.rbNumbersNo);

        rbTrainingAnglesYes = (RadioButton) view.findViewById(R.id.rbTrainingAnglesYes);
        rbTrainingAnglesNo = (RadioButton) view.findViewById(R.id.rbTrainingAnglesNo);

        rbRounding025 = (RadioButton) view.findViewById(R.id.rbRounding025);
        rbRounding001 = (RadioButton) view.findViewById(R.id.rbRounding001);

        rb18Times = (RadioButton) view.findViewById(R.id.rbRepetitions18);
        rb34Times = (RadioButton) view.findViewById(R.id.rbRepetitions34);

        txShowNumbers = (TextView) view.findViewById(R.id.txShowNumbers);
        txTrainingAngles = (TextView) view.findViewById(R.id.txTrainingAngles);
        txRounding = (TextView) view.findViewById(R.id.txRaouding);
        txRepetitions = (TextView) view.findViewById(R.id.txRepetitions);

        mHome = (Button) view.findViewById(R.id.back_button);
        mHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNavActivity().loadHomeFragment();
            }
        });

        rbSphCyl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSettings().setSpherocylindricalMode(true);
            }
        });
        rbSphEq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSettings().setSpherocylindricalMode(false);
            }
        });
        rbMinusCyl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSettings().setNegativeCylModel(true);
            }
        });
        rbPlusCyl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSettings().setNegativeCylModel(false);
            }
        });
        rbAcuityImperial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSettings().setImperialSystem(true);
            }
        });
        rbAcuityMetric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSettings().setImperialSystem(false);
            }
        });
        llPatientLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LanguageHelper.callPatientLanguagePopUp(SettingsFragment.this.getActivity(), view, SettingsFragment.this);
            }
        });
        llAppLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LanguageHelper.callAppLanguagePopUp(SettingsFragment.this.getActivity(), view, SettingsFragment.this);
            }
        });

        rbNumbersYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSettings().setShowNumbers(true);
            }
        });

        rbNumbersNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSettings().setShowNumbers(false);
            }
        });

        rbTrainingAnglesYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSettings().setTrainingAngles(true);
            }
        });

        rbTrainingAnglesNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSettings().setTrainingAngles(false);
            }
        });

        rbRounding025.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSettings().setRoundResults(true);
            }
        });

        rbRounding001.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSettings().setRoundResults(false);
            }
        });


        rb18Times.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSettings().setRepetitions(AppSettings.ACCURACY.REPEAT_18);
            }
        });

        rb34Times.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSettings().setRepetitions(AppSettings.ACCURACY.REPEAT_34);
            }
        });

        txShowNumbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNumbersInfo();
            }
        });
        txTrainingAngles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trainingAnglesInfo();
            }
        });
        txRounding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roundingPowersInfo();
            }
        });
        txRepetitions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRepetitionsInfo();
            }
        });

        return view;
    }

    public void trainingAnglesInfo() {
        AlertDialog.Builder authDialog = new AlertDialog.Builder(this.getNavActivity());
        authDialog.setTitle(R.string.training_angles_title);
        authDialog.setMessage(R.string.training_angles_desc);
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

    public void roundingPowersInfo() {
        AlertDialog.Builder authDialog = new AlertDialog.Builder(this.getNavActivity());
        authDialog.setTitle(R.string.rounding_powers_title);
        authDialog.setMessage(R.string.rounding_powers_desc);
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

    public void showNumbersInfo() {
        AlertDialog.Builder authDialog = new AlertDialog.Builder(this.getNavActivity());
        authDialog.setTitle(R.string.show_numbers_title);
        authDialog.setMessage(R.string.show_numbers_desc);
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


    public void showRepetitionsInfo() {
        AlertDialog.Builder authDialog = new AlertDialog.Builder(this.getNavActivity());
        authDialog.setTitle(R.string.show_repetitions_title);
        authDialog.setMessage(R.string.show_repetitions_desc);
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

    @Override
    public void onResume() {
        getNavActivity().showMenu();
        getNavActivity().hideLanguageButton();
        getNavActivity().hideNewCustomReadingButton();
        getNavActivity().hidePrinterButton();

        rbSphCyl.setChecked(getSettings().isSpherocylindricalMode());
        rbSphEq.setChecked(!getSettings().isSpherocylindricalMode());

        rbMinusCyl.setChecked(getSettings().isNegativeCylModel());
        rbPlusCyl.setChecked(!getSettings().isNegativeCylModel());

        rbAcuityImperial.setChecked(getSettings().isImperialSystem());
        rbAcuityMetric.setChecked(!getSettings().isImperialSystem());

        rbNumbersYes.setChecked(getSettings().isShowNumbers());
        rbNumbersNo.setChecked(!getSettings().isShowNumbers());

        rbTrainingAnglesYes.setChecked(getSettings().isTrainingAngles());
        rbTrainingAnglesNo.setChecked(!getSettings().isTrainingAngles());

        rbRounding025.setChecked(getSettings().isRoundResults());
        rbRounding001.setChecked(!getSettings().isRoundResults());

        rb18Times.setChecked(getSettings().is18Repetitions());
        rb34Times.setChecked(getSettings().is34Repetitions());

        loadSelectedPatientLanguageSettingsPage(this);
        loadSelectedAppLanguageSettingsPage(this);

        super.onResume();
    }

    public void setPatientLanguage(int chekingImg, int checkingTxt) {
        imPatientLanguage.setImageResource(chekingImg);
        txPatientLanguage.setText(checkingTxt);

    }

    public void setAppLanguage(int chekingImg, int checkingTxt) {
        imAppLanguage.setImageResource(chekingImg);
        txAppLanguage.setText(checkingTxt);
    }

    public void setPatientLocale(Locale myLocale) {
        getSettings().setPatientLocale(myLocale.getLanguage(), myLocale.getCountry());
        loadSelectedPatientLanguageSettingsPage(this);
    }

    //This changes the language of the whole app. Should be done on the settings instead.
    public void setAppLocale(Locale myLocale) {
        getSettings().setAppLocale(myLocale.getLanguage(), myLocale.getCountry());

        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        getNavActivity().recreate();
        loadSelectedAppLanguageSettingsPage(this);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public boolean onBackPressed() {
        getNavActivity().loadHomeFragment();
        return true;
    }

    public void reloadTextsInPatientLanguage() {
    }

    @Override
    public boolean shouldShowActionBar() {
        return false;
    }
}