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

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vitorpamplona.netra.R;

public class PutPhoneOnDeviceFragment extends NavFragment {

    private TextView mInstructions1, mInstructions2;
    private Button mStartButton;

    private ImageView mMovie;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nav_put_device, container, false);

        mInstructions1 = (TextView) view.findViewById(R.id.instructions_1);
        mInstructions2 = (TextView) view.findViewById(R.id.instructions_2);
        mStartButton = (Button) view.findViewById(R.id.start_button);

        //mInstructions1.setTypeface(TypeFaceProvider.getTypeFace(getActivity(), TypeFaceProvider.TYPEFACE_SOURCE_SANS_PRO_LIGHT));
        mInstructions2.setTypeface(TypeFaceProvider.getTypeFace(getActivity(), TypeFaceProvider.TYPEFACE_SOURCE_SANS_PRO_LIGHT));
        //mStartButton.setTypeface(TypeFaceProvider.getTypeFace(getActivity(), TypeFaceProvider.TYPEFACE_SOURCE_SANS_PRO_LIGHT), Typeface.BOLD);

        getNavActivity().pushPatientLocale();
        reloadTextsInPatientLanguage();
        getNavActivity().restorePatientLocale();

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTest(v);
            }
        });

        mMovie = (ImageView) view.findViewById(R.id.imageView);
        AnimationDrawable frameAnimation = (AnimationDrawable) mMovie.getDrawable();
        frameAnimation.setEnterFadeDuration(100);
        frameAnimation.setExitFadeDuration(100);
        //frameAnimation.stop();
        mMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationDrawable frameAnimation = (AnimationDrawable) mMovie.getDrawable();
                frameAnimation.stop();
                frameAnimation.selectDrawable(0);
                frameAnimation.start();
            }
        });

        return view;
    }

    public void reloadTextsInPatientLanguage() {
        mInstructions1.setText(R.string.ready_to_begin);
        mInstructions2.setText(R.string.press_confirm_insert_and_hand);
        mStartButton.setText(R.string.insert_into_device);
    }

    @Override
    public void onResume() {
        super.onResume();
        getNavActivity().showMenu();
        getNavActivity().hidePrinterButton();
        getNavActivity().showLanguageButton();
        getNavActivity().hideNewCustomReadingButton();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean shouldShowActionBar() {
        return false;
    }

    public boolean onBackPressed() {
        getNavActivity().loadThreeSteps();
        return true;
    }

    private void startTest(View view) {
        getNavActivity().loadTest();
    }

}