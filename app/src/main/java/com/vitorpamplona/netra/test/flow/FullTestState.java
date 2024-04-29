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
package com.vitorpamplona.netra.test.flow;

import static com.vitorpamplona.netra.test.flow.FullTestState.Stage.ANGLE_LEFT;
import static com.vitorpamplona.netra.test.flow.FullTestState.Stage.ANGLE_RIGHT;
import static com.vitorpamplona.netra.test.flow.FullTestState.Stage.PD_LEFT;
import static com.vitorpamplona.netra.test.flow.FullTestState.Stage.PD_RIGHT;
import static com.vitorpamplona.netra.test.flow.FullTestState.Stage.READY_TO_START;
import static com.vitorpamplona.netra.test.flow.FullTestState.Stage.TRAINING_LEFT;
import static com.vitorpamplona.netra.test.flow.FullTestState.Stage.TRAINING_RIGHT;

import android.util.Log;

import com.vitorpamplona.netra.R;
import com.vitorpamplona.netra.test.TestActivity;
import com.vitorpamplona.netra.test.view.NETRAView.Screen;

public class FullTestState {

    public static enum Stage {
        NONE,
        READY_TO_START,
        TRAINING_LEFT,
        TRAINING_RIGHT,
        PD_LEFT,
        PD_RIGHT,
        ANGLE_LEFT,
        ANGLE_RIGHT,
        POST_TEST,
        DONE,
        CANCELED;
    }

    protected TestActivity mActivity;

    protected Stage mPreviousStage = Stage.NONE;
    protected Stage mCurrentStage = Stage.NONE;
    protected int mPreviousStep = 0;
    protected int mCurrentStep = 0;
    protected int mTotalSteps = 0;

    public FullTestState(TestActivity activity) {
        mActivity = activity;
    }

    public void playWrong() {
        mActivity.getAudioPlayer().playSfx(R.raw.wrong);
    }

    public boolean isRightEye() {
        return mCurrentStage == PD_RIGHT || mCurrentStage == ANGLE_RIGHT || mCurrentStage == TRAINING_RIGHT;
    }

    public boolean isPDStage() {
        return mCurrentStage == PD_RIGHT || mCurrentStage == PD_LEFT;
    }

    public boolean isTrainingStage() {
        return mCurrentStage == TRAINING_RIGHT || mCurrentStage == TRAINING_LEFT;
    }

    public boolean wasPDMeasured() {
        return mCurrentStage == ANGLE_LEFT || mCurrentStage == ANGLE_RIGHT;
    }

    public void setCurrentStageAsScreen() {
        switch (mCurrentStage) {
            case NONE:
                break;
            case TRAINING_LEFT:
                mActivity.getTestView().setScreen(Screen.TRAINING_TEST);
                mActivity.getTestView().setDoingPDMeasurement(false);
                break;
            case TRAINING_RIGHT:
                mActivity.getTestView().setScreen(Screen.TRAINING_TEST);
                mActivity.getTestView().setDoingPDMeasurement(false);
                break;
            case READY_TO_START:
                mActivity.getTestView().setScreen(Screen.PLAY_ICON);
                mActivity.getTestView().setDoingPDMeasurement(false);
                break;
            case PD_RIGHT:
                mActivity.getTestView().setScreen(Screen.ALIEN_TEST);
                mActivity.getTestView().setDoingPDMeasurement(true);
                break;
            case PD_LEFT:
                mActivity.getTestView().setScreen(Screen.ALIEN_TEST);
                mActivity.getTestView().setDoingPDMeasurement(true);
                break;
            case ANGLE_LEFT:
                mActivity.getTestView().setScreen(Screen.ALIEN_TEST);
                mActivity.getTestView().setDoingPDMeasurement(false);
                break;
            case ANGLE_RIGHT:
                mActivity.getTestView().setScreen(Screen.ALIEN_TEST);
                mActivity.getTestView().setDoingPDMeasurement(false);
                break;
            case POST_TEST:
                mActivity.getTestView().setScreen(Screen.ALIEN_TEST);
                mActivity.getTestView().setScreen(Screen.CHECK_MARK);
                mActivity.destroyCamera();
                break;
            default:
                break;
        }
    }

    public void updateProgress(Stage previousStage, Stage currentStage, int previousStep, int currentStep, int totalSteps) {

        Log.d("ProtoDeviceFlow", previousStage.name() + " => " + currentStage.name() + ", " + previousStep + " => " + currentStep + " / " + totalSteps);

        // we are either starting or resuming an exam
        if (mCurrentStage == Stage.NONE) {
            switch (currentStage) {
                case READY_TO_START:
                    mActivity.getTestView().setScreen(Screen.PLAY_ICON);
                    break;
                case TRAINING_LEFT:
                    mActivity.getTestView().setScreen(Screen.TRAINING_TEST);
                    break;
                case TRAINING_RIGHT:
                    mActivity.getTestView().setScreen(Screen.TRAINING_TEST);
                    break;
                case PD_RIGHT:
                case PD_LEFT:
                case ANGLE_LEFT:
                case ANGLE_RIGHT:
                    mActivity.getTestView().setScreen(Screen.ALIEN_TEST);
                    break;
                case POST_TEST:
                    mActivity.getTestView().setScreen(Screen.CHECK_MARK);

                    mActivity.destroyCamera();

                    break;
                default:
                    break;
            }

        } else if (previousStage != currentStage) {
            playChangeStageSongs(currentStage);

            switch (currentStage) {
                case NONE:
                    break;
                case TRAINING_LEFT:
                    mActivity.getTestView().setScreen(Screen.TRAINING_TEST);
                    break;
                case TRAINING_RIGHT:
                    mActivity.getTestView().setScreen(Screen.TRAINING_TEST);
                    break;
                case READY_TO_START:
                    mActivity.getTestView().setScreen(Screen.PLAY_ICON);
                    break;
                case PD_RIGHT:
                    mActivity.getTestView().setScreen(Screen.ALIEN_TEST);
                    mActivity.getTestView().setDoingPDMeasurement(true);
                    break;
                case PD_LEFT:
                    mActivity.getTestView().setDoingPDMeasurement(true);
                    break;
                case ANGLE_LEFT:
                    mActivity.getTestView().setDoingPDMeasurement(false);
                    break;
                case ANGLE_RIGHT:
                    mActivity.getTestView().setDoingPDMeasurement(false);
                    break;
                case POST_TEST:
                    mActivity.getTestView().setScreen(Screen.CHECK_MARK);
                    mActivity.destroyCamera();

                    break;
                default:
                    break;
            }
        } else if (currentStage == READY_TO_START) {

            switch (currentStep) {
                case 2:
                    mActivity.getAudioPlayer().playSfx(R.raw.wrong);
                    break;
                case 3:
                    mActivity.getAudioPlayer().playSfx(R.raw.next);
                    break;
                case 4:
                    mActivity.getAudioPlayer().playSfx(R.raw.next);
                    mActivity.getAudioPlayer().playSpeech(R.raw.great, 300);
                    break;
                case 5:
                    mActivity.getTestView().setScreen(Screen.PLAY_ICON);
                    break;
            }

        } else if (previousStep != currentStep) {
            playIntermidiarySongs(currentStage, currentStep);
        } else {

        }

        mPreviousStage = previousStage;
        mCurrentStage = currentStage;
        mPreviousStep = previousStep;
        mCurrentStep = currentStep;
        mTotalSteps = totalSteps;
    }

    public void playChangeStageSongs(Stage newStage) {
        switch (newStage) {
            case TRAINING_LEFT:
            case TRAINING_RIGHT:
            case PD_RIGHT:
            case PD_LEFT:
            case ANGLE_LEFT:
            case ANGLE_RIGHT:
            case POST_TEST:
                mActivity.getAudioPlayer().playSfx(R.raw.next);
                break;
            default:
                break;
        }

        switch (newStage) {
            case NONE:
                break;
            case TRAINING_LEFT:
                break;
            case TRAINING_RIGHT:
                break;
            case READY_TO_START:
                break;
            case PD_RIGHT:
                mActivity.getAudioPlayer().playSpeech(R.raw.exam_1c_all_instructions, 300);
                break;
            case PD_LEFT:
                mActivity.getAudioPlayer().playSpeech(R.raw.exam_2b_you_got_it_please_continue);
                break;
            case ANGLE_LEFT:
                mActivity.getAudioPlayer().playSpeech(R.raw.exam_3b_great_continue_the_whole_test_less_than_3);
                break;
            case ANGLE_RIGHT:
                mActivity.getAudioPlayer().playSpeech(R.raw.exam_7_good_job_half_way_done);
                break;
            case POST_TEST:
                mActivity.getAudioPlayer().playSpeech(R.raw.exam_11_your_are_finished);
                break;
            default:
                break;
        }
    }

    public void playIntermidiarySongs(Stage newStage, int newStep) {
        mActivity.getAudioPlayer().playSfx(R.raw.next);

        switch (newStage) {
            case ANGLE_LEFT:
                switch (newStep) {
                    case 2:
                        mActivity.getAudioPlayer().playSpeech(R.raw.excellent);
                        break;
                    case 3:
                        mActivity.getAudioPlayer().playSpeech(R.raw.good_job);
                        break;
                    case 4:
                        mActivity.getAudioPlayer().playSpeech(R.raw.exam_6_excellent_quarter_of_way_done);
                        break;
                    case 5:
                        mActivity.getAudioPlayer().playSpeech(R.raw.please_continue);
                        break;
                    case 6:
                        mActivity.getAudioPlayer().playSpeech(R.raw.great2);
                        break;
                    case 7:
                        mActivity.getAudioPlayer().playSpeech(R.raw.good_job);
                        break;
                    case 8:
                        mActivity.getAudioPlayer().playSpeech(R.raw.great_please_continue);
                        break;
                }
                break;
            case ANGLE_RIGHT:
                switch (newStep) {
                    case 2:
                        mActivity.getAudioPlayer().playSpeech(R.raw.great);
                        break;
                    case 3:
                        mActivity.getAudioPlayer().playSpeech(R.raw.good_job);
                        break;
                    case 4:
                        mActivity.getAudioPlayer().playSpeech(R.raw.great_job);
                        break;
                    case 5:
                        mActivity.getAudioPlayer().playSpeech(R.raw.exam_8_three_quarters);
                        break;
                    case 6:
                        mActivity.getAudioPlayer().playSpeech(R.raw.excellent);
                        break;
                    case 7:
                        mActivity.getAudioPlayer().playSpeech(R.raw.exam_9_almost_done);
                        break;
                    case 8:
                        mActivity.getAudioPlayer().playSpeech(R.raw.exam_10_last_one);
                        break;
                }
                break;
            default:
                break;
        }
    }

    public void resetProgress() {
        mPreviousStage = Stage.NONE;
        mCurrentStage = Stage.NONE;
        mPreviousStep = 0;
        mCurrentStep = 0;
        mTotalSteps = 0;
    }

    public Stage getPreviousStage() {
        return mPreviousStage;
    }

    public Stage getCurrentStage() {
        return mCurrentStage;
    }

    public int getPreviousStep() {
        return mPreviousStep;
    }

    public int getCurrentStep() {
        return mCurrentStep;
    }

    public int getTotalSteps() {
        return mTotalSteps;
    }


}
