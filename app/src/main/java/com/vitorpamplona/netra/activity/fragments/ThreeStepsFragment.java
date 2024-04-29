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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vitorpamplona.netra.R;

public class ThreeStepsFragment extends NavFragment {

    public static final int DURATION_MILLIS = 750;
    public static final int START_OFFSET = 300;
    private TextView mInstructions1, mInstructions2, mInstructions3, mInstructions4;
    private Button mStartButton;

    private ImageView imFind, imAlign, imConfirm;
    private ImageView imStep0, imStep1, imStep2, imStep3, imStep4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nav_three_steps, container, false);

        mInstructions1 = (TextView) view.findViewById(R.id.instructions_1);
        mInstructions2 = (TextView) view.findViewById(R.id.instructions_2);
        mInstructions3 = (TextView) view.findViewById(R.id.instructions_3);
        mInstructions4 = (TextView) view.findViewById(R.id.instructions_4);
        mStartButton = (Button) view.findViewById(R.id.start_button);

        imFind = (ImageView) view.findViewById(R.id.imFind);
        imAlign = (ImageView) view.findViewById(R.id.imAlign);
        imConfirm = (ImageView) view.findViewById(R.id.imComfirm);

        imStep0 = (ImageView) view.findViewById(R.id.imStep1);
        imStep1 = (ImageView) view.findViewById(R.id.imStep2);
        imStep2 = (ImageView) view.findViewById(R.id.imStep3);
        imStep3 = (ImageView) view.findViewById(R.id.imStep4);
        imStep4 = (ImageView) view.findViewById(R.id.imStep5);

        mInstructions2.setTypeface(TypeFaceProvider.getTypeFace(getActivity(), TypeFaceProvider.TYPEFACE_SOURCE_SANS_PRO_LIGHT));
        mInstructions3.setTypeface(TypeFaceProvider.getTypeFace(getActivity(), TypeFaceProvider.TYPEFACE_SOURCE_SANS_PRO_LIGHT));
        mInstructions4.setTypeface(TypeFaceProvider.getTypeFace(getActivity(), TypeFaceProvider.TYPEFACE_SOURCE_SANS_PRO_LIGHT));

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTest(v);
            }
        });

        mInstructions2.setVisibility(View.INVISIBLE);
        mInstructions3.setVisibility(View.INVISIBLE);
        mInstructions4.setVisibility(View.INVISIBLE);
        imFind.setVisibility(View.INVISIBLE);
        imAlign.setVisibility(View.INVISIBLE);
        imConfirm.setVisibility(View.INVISIBLE);
        imStep1.setVisibility(View.INVISIBLE);
        imStep2.setVisibility(View.INVISIBLE);
        imStep3.setVisibility(View.INVISIBLE);
        imStep4.setVisibility(View.INVISIBLE);

        /** INSTRUCTIONS ANIMATIONS */

        final Animation fadeInT2 = new AlphaAnimation(0, 1);
        fadeInT2.setStartOffset(START_OFFSET);
        fadeInT2.setDuration(DURATION_MILLIS);
        fadeInT2.setFillAfter(true);

        final Animation fadeInT3 = new AlphaAnimation(0, 1);
        fadeInT3.setDuration(DURATION_MILLIS);
        fadeInT3.setFillAfter(true);

        final Animation fadeInT4 = new AlphaAnimation(0, 1);
        fadeInT4.setDuration(DURATION_MILLIS);
        fadeInT4.setFillAfter(true);

        fadeInT2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mInstructions3.startAnimation(fadeInT3);
                mInstructions2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        fadeInT3.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mInstructions4.startAnimation(fadeInT4);
                mInstructions3.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        fadeInT4.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mInstructions4.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        /** STEP NUMBER ANIMATIONS */

        final Animation fadeInI2 = new AlphaAnimation(0, 1);
        fadeInI2.setStartOffset(START_OFFSET);
        fadeInI2.setDuration(DURATION_MILLIS);
        fadeInI2.setFillAfter(true);

        final Animation fadeInI3 = new AlphaAnimation(0, 1);
        fadeInI3.setDuration(DURATION_MILLIS);
        fadeInI3.setFillAfter(true);

        final Animation fadeInI4 = new AlphaAnimation(0, 1);
        fadeInI4.setDuration(DURATION_MILLIS);
        fadeInI4.setFillAfter(true);

        fadeInI2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imAlign.startAnimation(fadeInI3);
                imFind.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        fadeInI3.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imConfirm.startAnimation(fadeInI4);
                imAlign.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        fadeInI4.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imConfirm.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        /** IMAGE ANIMATIONS */

        final Animation fadeInStep1 = new AlphaAnimation(0, 1);
        fadeInStep1.setStartOffset(START_OFFSET);
        fadeInStep1.setDuration(DURATION_MILLIS);
        fadeInStep1.setFillAfter(true);

        final Animation fadeInStep2 = new AlphaAnimation(0, 1);
        fadeInStep2.setDuration(DURATION_MILLIS);
        fadeInStep2.setFillAfter(true);

        final Animation fadeInStep3 = new AlphaAnimation(0, 1);
        fadeInStep3.setDuration(DURATION_MILLIS);
        fadeInStep3.setFillAfter(true);

        final Animation fadeInStep4 = new AlphaAnimation(0, 1);
        fadeInStep4.setDuration(DURATION_MILLIS);
        fadeInStep4.setFillAfter(true);

        fadeInStep1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imStep2.startAnimation(fadeInStep2);
                imStep1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        fadeInStep2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imStep3.startAnimation(fadeInStep3);
                imStep2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        fadeInStep3.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imStep4.startAnimation(fadeInStep4);
                imStep3.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        fadeInStep4.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imStep4.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });


        mInstructions2.startAnimation(fadeInT2);
        imFind.startAnimation(fadeInI2);
        imStep1.startAnimation(fadeInStep1);

        return view;
    }

    public void reloadTextsInPatientLanguage() {
        mInstructions1.setText(R.string.repeat_8_times_per_eye);
        mInstructions2.setText(R.string.find_with_brightness);
        mInstructions3.setText(R.string.align_with_alignment);
        mInstructions4.setText(R.string.record_by_pressing_confirm);
        mStartButton.setText(R.string.got_it_let_me_try);
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
        getNavActivity().loadSimulation();
        return true;
    }

    private void startTest(View view) {
        getNavActivity().loadInsertDevice();
    }

}