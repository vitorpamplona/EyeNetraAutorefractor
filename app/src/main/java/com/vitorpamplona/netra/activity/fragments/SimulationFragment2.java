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

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.vitorpamplona.netra.R;
import com.vitorpamplona.netra.activity.components.PreTestSimulation;

import java.util.logging.Logger;

public class SimulationFragment2 extends NavFragment implements Animation.AnimationListener {

    private static final Logger log = Logger.getLogger(SimulationFragment2.class.getName());

    private View mRoot;
    private PreTestSimulation mSimulation;

    public static final int DURATION_MILLIS = 750;
    public static final int START_OFFSET = 300;

    private static final float TARGET_PD_1 = 30;
    private static final float TARGET_PD_2 = 31.5f;
    private static final float PD_TOLERANCE = 1.20f;
    private float mTargetPd = TARGET_PD_1;

    private static final float POWER_TOLERANCE = .4f;//.2f;

    private int mRemainingAngles = 3;
    private float mPower = +5;
    private float mAngle = 180;
    private float mPd = 37;


    boolean finderReady;
    boolean linesAligned;

    private ImageView mNetraDevice;
    private ImageView mHighlight;
    private Button mStartButton;
    private SeekBar mSeekBarFinderAligner;
    private boolean isFinding = true;
    private boolean isAligning = true;
    private boolean isConfirming = true;
    private boolean isSeeingAngleChange = true;
    private TextView mInstructions;
    private TextView mInstructions2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_simulation2, container, false);

        mInstructions = (TextView) mRoot.findViewById(R.id.instructions_1);
        mInstructions2 = (TextView) mRoot.findViewById(R.id.instructions_2);
        mSimulation = (PreTestSimulation) mRoot.findViewById(R.id.simulation);

        mSeekBarFinderAligner = (SeekBar) mRoot.findViewById(R.id.seekBarFinderAligner);
        mNetraDevice = (ImageView) mRoot.findViewById(R.id.imageNetra);
        mStartButton = (Button) mRoot.findViewById(R.id.start_test_button);
        mHighlight = (ImageView) mRoot.findViewById(R.id.imageHighLight);


        isFinding = true;
        isAligning = false;
        isConfirming = false;
        isSeeingAngleChange = false;

        mSimulation.setDone(false);
        mSimulation.setCenterPd(TARGET_PD_1);
        mSimulation.onPdChanged(mPd);
        mSimulation.onAngleChanged(mAngle);

        mSeekBarFinderAligner.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (isFinding) {
                    // -12 to +6.
                    float newPd = progress / 100.0f * 14 + 25;
                    onPdChanged(newPd);
                } else {
                    // -12 to +6.
                    float newPower = progress / 100.0f * 18 - 12;
                    onPowerChanged(newPower);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                manualStep();
            }
        });

        mStartButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                manualStep();
            }
        });

        mHighlight.setVisibility(View.INVISIBLE);
        mInstructions.setVisibility(View.INVISIBLE);
        mInstructions2.setVisibility(View.INVISIBLE);
        mSimulation.setVisibility(View.INVISIBLE);
        mSeekBarFinderAligner.setVisibility(View.INVISIBLE);

        fadeInComponents();

        return mRoot;
    }

    private void fadeInComponents() {
        final Animation fadeInInstructions = new AlphaAnimation(0, 1);
        fadeInInstructions.setDuration(DURATION_MILLIS);

        final Animation fadeInSimulation = new AlphaAnimation(0, 1);
        fadeInSimulation.setDuration(DURATION_MILLIS);

        fadeInInstructions.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isAligning || isFinding)
                    mSeekBarFinderAligner.startAnimation(fadeInSimulation);
                mSimulation.startAnimation(fadeInSimulation);
                mInstructions.setVisibility(View.VISIBLE);
                mInstructions2.setVisibility(View.VISIBLE);
                mHighlight.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        fadeInSimulation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isAligning || isFinding)
                    mSeekBarFinderAligner.setVisibility(View.VISIBLE);
                mSimulation.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mInstructions.startAnimation(fadeInInstructions);
        mInstructions2.startAnimation(fadeInInstructions);
        mHighlight.startAnimation(fadeInInstructions);
    }

    public void manualStep() {
        if (isFinding) {
            if (finderReady) {
                isFinding = false;
                isAligning = true;
                switchToRightKnob();
            }
        } else if (isAligning) {
            if (linesAligned) {
                isAligning = false;
                isConfirming = true;
                switchToButton();
            }
        } else if (isConfirming) {
            isConfirming = false;
            isSeeingAngleChange = true;
            changeAngle();
        } else if (isSeeingAngleChange) {
            isSeeingAngleChange = false;
            showCheckmark();
        } else {
            getNavActivity().loadThreeSteps();
        }
    }

    public void resetSeekBar() {
        if (isFinding)
            mSeekBarFinderAligner.setProgress((int) ((mPd - 25) / 14.0f * 100));
        else
            mSeekBarFinderAligner.setProgress((int) ((mPower + 12) / 18.0f * 100));
    }

    public void changeAngle() {
        mSimulation.onAngleChanged(-22.0f);
        mSimulation.onPowerChanged((float) (Math.random() * 3 + 4));
        refreshTexts();
    }

    public void showCheckmark() {
        mSimulation.setDone(true);
        refreshTexts();
    }

    protected void startSimulation() {
        resetSeekBar();
        generateNewPower();
        onPdChanged(mPd);

        mStartButton.setEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        startSimulation();
        getNavActivity().showMenu();
        getNavActivity().hidePrinterButton();
        getNavActivity().showLanguageButton();
        getNavActivity().hideNewCustomReadingButton();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void onPdChanged(Float newPd) {
        mPd = newPd;
        mSimulation.onPdChanged(newPd);
        finderReady = Math.abs(newPd - mTargetPd) < PD_TOLERANCE;
        mStartButton.setEnabled(finderReady);
    }

    public void onPowerChanged(Float newPower) {
        mPower = newPower;
        mSimulation.onPowerChanged(newPower);
        linesAligned = Math.abs(newPower) < POWER_TOLERANCE;
        mStartButton.setEnabled(linesAligned);
    }

    protected void generateNewPower() {
        mPower = (float) (Math.random() * 3 + 4);
        mSimulation.onPowerChanged(mPower);
        mSimulation.onAngleChanged(0.0f);
    }

    public boolean onBackPressed() {
        getNavActivity().loadPreSimulation();
        return true;
    }

    public void refreshTexts() {
        getNavActivity().pushPatientLocale();
        reloadTextsInPatientLanguage();
        if (isAligning) {
            resetSeekBar();
        }
        getNavActivity().restorePatientLocale();
    }

    @Override
    public void onAnimationStart(Animation animation) {
        mSimulation.setVisibility(View.INVISIBLE);
        mSeekBarFinderAligner.setVisibility(View.INVISIBLE);
        mInstructions.setVisibility(View.INVISIBLE);
        mInstructions2.setVisibility(View.INVISIBLE);
        mStartButton.setVisibility(View.INVISIBLE);
        mHighlight.setVisibility(View.INVISIBLE);

        refreshTexts();
    }

    public void reloadTextsInPatientLanguage() {
        if (isFinding) {
            mInstructions.setText(R.string.user_right_knob_to_find);
            mStartButton.setText(R.string.found);
        } else if (isAligning) {
            mInstructions.setText(R.string.use_left_knob_to_align_lines);
            mStartButton.setText(R.string.ok_they_are_aligned);
        } else if (isConfirming) {
            mHighlight.setY(mHighlight.getY() + 130);
            mInstructions.setText(R.string.press_confirm_to_record_measurement);
            mStartButton.setText(R.string.great_confirm);
        } else if (isSeeingAngleChange) {
            mInstructions.setText(R.string.the_lines_rotate);
            mStartButton.setText(R.string.got_it);
        } else {
            mInstructions.setText(R.string.when_you_see_a_checkmark_you_are_done);
            mStartButton.setText(R.string.got_it);
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        fadeInComponents();
        mStartButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    public static int dpToPixels(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
        // S4 scale == 3.
    }

    private void switchToRightKnob() {
        AnimationSet as = new AnimationSet(true);
        as.setInterpolator(new AnticipateOvershootInterpolator(1));
        as.setFillAfter(true);

        Animation a2 = new TranslateAnimation(0, dpToPixels(this.getActivity(), 450), 0, -dpToPixels(this.getActivity(), 66));
        a2.setDuration(2000);
        as.addAnimation(a2);

        as.setAnimationListener(this);

        mNetraDevice.startAnimation(as);
    }

    private void switchToButton() {
        AnimationSet as = new AnimationSet(true);
        as.setInterpolator(new AnticipateOvershootInterpolator(1));
        as.setFillAfter(true);

        Animation a2 = new TranslateAnimation(dpToPixels(this.getActivity(), 450), 0, -dpToPixels(this.getActivity(), 66), -dpToPixels(this.getActivity(), 18));
        a2.setDuration(2000);
        as.addAnimation(a2);

        as.setAnimationListener(this);

        mNetraDevice.startAnimation(as);
    }

}
