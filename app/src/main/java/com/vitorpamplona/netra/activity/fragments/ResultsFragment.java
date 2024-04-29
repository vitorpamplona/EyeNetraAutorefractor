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


import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Property;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vitorpamplona.core.test.AddSuggestions;
import com.vitorpamplona.netra.R;
import com.vitorpamplona.netra.activity.NavActivity;
import com.vitorpamplona.netra.activity.NetraGApplication;
import com.vitorpamplona.netra.activity.components.DatePickerFragment;
import com.vitorpamplona.netra.model.RefractionType;
import com.vitorpamplona.netra.model.db.objects.DebugExam;
import com.vitorpamplona.netra.model.db.objects.Refraction;
import com.vitorpamplona.netra.utils.AcuityFormatter;
import com.vitorpamplona.netra.utils.AgeCalculator;
import com.vitorpamplona.netra.utils.ConfidenceFormatter;
import com.vitorpamplona.netra.utils.PrescriptionParser;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

public class ResultsFragment extends NavFragment implements DatePickerFragment.DateInputIO {

    private TextView mLeftSphere, mLeftCyl, mLeftAxis, mLeftAcuity, mLeftAdd;
    private TextView mRightSphere, mRightCyl, mRightAxis, mRightAcuity, mRightAdd;

    private TextView mLeftSphereOld, mLeftCylOld, mLeftAxisOld, mLeftAcuityOld, mLeftAddOld;
    private TextView mRightSphereOld, mRightCylOld, mRightAxisOld, mRightAcuityOld, mRightAddOld;

    private TextView mPD, mPDOld;

    private EditText mEdNote, mEdAge, mEdEmail;

    private TextView mTxLeft, mTxRight, mTxCyl, mTxCylChange, mTxSph, mTxSphChange, mTxSuggestedFor;
    private View mTxAdd;
    private RelativeLayout mRlCyl, mRlSph;

    DecimalFormat sphereCylFormatter = new DecimalFormat("+0.00;-0.00");
    DecimalFormat axisFormatter = new DecimalFormat("0°;0°");
    private DecimalFormat mPdFormatter = new DecimalFormat("0mm");

    private ImageView imAttentionLeft, imAttentionRight, imLousyLeft, imLousyRight;

    private IntlPhoneInput mPhoneInputView;
    private EditText mPhoneEditText;

    private final float PHONE_DISTANCE = 0.25f; //meters
    private final float BOOK_DISTANCE = 0.37f; //meters
    private final float COMPUTER_DISTANCE = 0.50f; //meters
    private final float DRIVING_DISTANCE = 0.75f; //meters
    private final float[] DISTANCES = {PHONE_DISTANCE, BOOK_DISTANCE, COMPUTER_DISTANCE, DRIVING_DISTANCE};
    private int addMode = 1;

    private TextView mInstructions1;
    private Button mNextButton;

    boolean goBackHome;

    TextWatcher mEdNoteListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            //Log.i("Saving", editable.toString());
            if (NetraGApplication.get().getLastResult() != null) {
                NetraGApplication.get().getLastResult().setStudyName(editable.toString());
            }
        }
    };

    TextWatcher mEdEmailListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (NetraGApplication.get().getLastResult() != null) {
                NetraGApplication.get().getLastResult().setPrescriptionEmail(editable.toString());
                mEdEmail.setError(null);
            }
        }
    };

    public ResultsFragment() {
        this.goBackHome = true;
    }

    public ResultsFragment setHomeWhenDone(boolean goBackHome) {
        this.goBackHome = goBackHome;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_results, container, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getNavActivity() != null) {
                    if (getNavActivity().getCurrentFocus() != null)
                        getNavActivity().getCurrentFocus().clearFocus();
                    getNavActivity().hideKeyboard();
                }
            }
        });

        mEdNote = (EditText) view.findViewById(R.id.edPatient);
        mEdAge = (EditText) view.findViewById(R.id.edAge);

        mEdAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.setCallbacks(ResultsFragment.this);
                newFragment.show(getNavActivity().getFragmentManager(), "dobPicker");
            }
        });

        mEdEmail = (EditText) view.findViewById(R.id.edEmail);

        mPhoneInputView = (IntlPhoneInput) view.findViewById(R.id.my_phone_input);
        mPhoneInputView.setEmptyDefault(NetraGApplication.get().getSettings().getLastFlag());

        mPhoneEditText = (EditText) mPhoneInputView.findViewById(net.rimoto.intlphoneinput.R.id.intl_phone_edit__phone);
        mPhoneEditText.setGravity(Gravity.CENTER_HORIZONTAL);
        //mPhoneEditText.setTextColor(getResources().getColorStateList(R.drawable.edit_text_selector));
        mPhoneEditText.setPadding(mPhoneEditText.getPaddingLeft(), 1, mPhoneEditText.getPaddingRight(), mPhoneEditText.getPaddingBottom());
        mPhoneEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        mTxLeft = (TextView) view.findViewById(R.id.txLeft);
        mLeftSphere = (TextView) view.findViewById(R.id.left_sphere);
        mLeftCyl = (TextView) view.findViewById(R.id.left_cyl);
        mLeftAxis = (TextView) view.findViewById(R.id.left_axis);
        mLeftAdd = (TextView) view.findViewById(R.id.left_add);
        mLeftAcuity = (TextView) view.findViewById(R.id.left_acuity);

        mLeftSphereOld = (TextView) view.findViewById(R.id.left_sphere_old);
        mLeftCylOld = (TextView) view.findViewById(R.id.left_cyl_old);
        mLeftAxisOld = (TextView) view.findViewById(R.id.left_axis_old);
        mLeftAddOld = (TextView) view.findViewById(R.id.left_add_old);
        mLeftAcuityOld = (TextView) view.findViewById(R.id.left_acuity_old);

        mTxRight = (TextView) view.findViewById(R.id.txRight);
        mRightSphere = (TextView) view.findViewById(R.id.right_sphere);
        mRightCyl = (TextView) view.findViewById(R.id.right_cyl);
        mRightAxis = (TextView) view.findViewById(R.id.right_axis);
        mRightAdd = (TextView) view.findViewById(R.id.right_add);
        mRightAcuity = (TextView) view.findViewById(R.id.right_acuity);

        mRightSphereOld = (TextView) view.findViewById(R.id.right_sphere_old);
        mRightCylOld = (TextView) view.findViewById(R.id.right_cyl_old);
        mRightAxisOld = (TextView) view.findViewById(R.id.right_axis_old);
        mRightAddOld = (TextView) view.findViewById(R.id.right_add_old);
        mRightAcuityOld = (TextView) view.findViewById(R.id.right_acuity_old);

        mPD = (TextView) view.findViewById(R.id.pd);
        mPDOld = (TextView) view.findViewById(R.id.pd_old);

        imAttentionLeft = (ImageView) view.findViewById(R.id.imAttentionLeft);
        imAttentionLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAttentionPoorResultsMsg();
            }
        });
        imAttentionRight = (ImageView) view.findViewById(R.id.imAttentionRight);
        imAttentionRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAttentionPoorResultsMsg();
            }
        });
        imLousyLeft = (ImageView) view.findViewById(R.id.imLousyLeft);
        imLousyLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAttentionLousyResultsMsg();
            }
        });
        imLousyRight = (ImageView) view.findViewById(R.id.imLousyRight);
        imLousyRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAttentionLousyResultsMsg();
            }
        });

        mRightSphereOld.setPaintFlags(mRightSphereOld.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        mRightCylOld.setPaintFlags(mRightCylOld.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        mRightAxisOld.setPaintFlags(mRightAxisOld.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        mRightAddOld.setPaintFlags(mRightAddOld.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        mRightAcuityOld.setPaintFlags(mRightAcuityOld.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        mLeftSphereOld.setPaintFlags(mLeftSphereOld.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        mLeftCylOld.setPaintFlags(mLeftCylOld.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        mLeftAxisOld.setPaintFlags(mLeftAxisOld.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        mLeftAddOld.setPaintFlags(mLeftAddOld.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        mLeftAcuityOld.setPaintFlags(mLeftAcuityOld.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        mPDOld.setPaintFlags(mPDOld.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        mTxCyl = (TextView) view.findViewById(R.id.txCyl);
        mTxCyl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNavActivity().getApp().getSettings().toggleCylModel();
                loadViews();
            }
        });

        mTxCylChange = (TextView) view.findViewById(R.id.txCylChange);
        mTxCylChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNavActivity().getApp().getSettings().toggleCylModel();
                loadViews();
            }
        });

        mTxSph = (TextView) view.findViewById(R.id.txSph);
        mTxSph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNavActivity().getApp().getSettings().toggleSpherocylindricalMode();
                loadViews();
            }
        });

        mTxAdd = (View) view.findViewById(R.id.tx_add_field);
        mTxSuggestedFor = (TextView) view.findViewById(R.id.suggested_for);


        mTxSphChange = (TextView) view.findViewById(R.id.txSphChange);
        mTxSphChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNavActivity().getApp().getSettings().toggleSpherocylindricalMode();
                loadViews();
            }
        });

        mEdNote.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                int result = actionId & EditorInfo.IME_MASK_ACTION;
                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:
                        textView.clearFocus();
                        getNavActivity().hideKeyboard();
                        break;
                    case EditorInfo.IME_ACTION_NEXT:
                        textView.clearFocus();
                        getNavActivity().hideKeyboard();
                        break;
                }

                if (NetraGApplication.get().getLastResult() != null) {
                    NetraGApplication.get().getLastResult().setStudyName(mEdNote.getText().toString());
                    return true;
                }
                return false;
            }
        });
        mEdNote.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) getNavActivity().showKeyboardDontAdjust();
                else getNavActivity().hideKeyboard();
            }
        });

        mEdAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.setCallbacks(ResultsFragment.this);
                newFragment.show(getNavActivity().getFragmentManager(), "dobPicker");
            }
        });

        mEdEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                int result = actionId & EditorInfo.IME_MASK_ACTION;
                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:
                        textView.clearFocus();
                        getNavActivity().hideKeyboard();
                        break;
                    case EditorInfo.IME_ACTION_NEXT:
                        textView.clearFocus();
                        getNavActivity().hideKeyboard();
                        break;
                }

                if (NetraGApplication.get().getLastResult() != null) {
                    NetraGApplication.get().getLastResult().setPrescriptionEmail(mEdEmail.getText().toString());
                    mEdEmail.setError(null);
                    return true;
                }
                return false;
            }
        });

        mPhoneEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                int result = actionId & EditorInfo.IME_MASK_ACTION;
                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:
                        textView.clearFocus();
                        getNavActivity().hideKeyboard();
                        break;
                    case EditorInfo.IME_ACTION_NEXT:
                        textView.clearFocus();
                        getNavActivity().hideKeyboard();
                        break;
                }

                DebugExam e = NetraGApplication.get().getLastResult();

                if (e != null && mPhoneInputView.isValid()) {
                    if (e.getPrescriptionEmail() != null && e.getPrescriptionPhone() != null
                            && e.getPrescriptionEmail().contains(e.getPrescriptionPhone())
                            && !mPhoneInputView.getNumber().equals(e.getPrescriptionPhone())) {
                        e.setPrescriptionEmail(null);
                    }
                    e.setPrescriptionPhone(mPhoneInputView.getNumber());
                    NetraGApplication.get().getSettings().setLastFlag(mPhoneInputView.getSelectedCountry().getIso());
                    mPhoneEditText.setError(null);
                    return true;
                } else {
                    mPhoneEditText.setError("Invalid phone format for this country");
                }
                return false;
            }
        });

        mPhoneInputView.setOnValidityChange(new IntlPhoneInput.IntlPhoneInputListener() {
            @Override
            public void done(View view, boolean isValid) {
                DebugExam e = NetraGApplication.get().getLastResult();
                if (isValid && e != null) {
                    if (e.getPrescriptionEmail() != null && e.getPrescriptionPhone() != null
                            && e.getPrescriptionEmail().contains(e.getPrescriptionPhone())
                            && !mPhoneInputView.getNumber().equals(e.getPrescriptionPhone())) {
                        e.setPrescriptionEmail(null);
                    }
                    e.setPrescriptionPhone(mPhoneInputView.getNumber());
                    NetraGApplication.get().getSettings().setLastFlag(mPhoneInputView.getSelectedCountry().getIso());
                    mPhoneEditText.setError(null);
                } else {
                    mPhoneEditText.setError("Invalid phone format for this country");
                }
            }
        });

        mEdEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) getNavActivity().showKeyboardDontAdjust();
                else getNavActivity().hideKeyboard();
            }
        });

        mRlCyl = (RelativeLayout) view.findViewById(R.id.rlCyl);
        mRlSph = (RelativeLayout) view.findViewById(R.id.rlSph);

        mInstructions1 = (TextView) view.findViewById(R.id.instructions_1);
        mNextButton = (Button) view.findViewById(R.id.next_button);

        //mInstructions1.setTypeface(TypeFaceProvider.getTypeFace(getActivity(),TypeFaceProvider.TYPEFACE_SOURCE_SANS_PRO_LIGHT));
        mTxRight.setTypeface(TypeFaceProvider.getTypeFace(getActivity(), TypeFaceProvider.TYPEFACE_SOURCE_SANS_PRO_LIGHT));
        mTxLeft.setTypeface(TypeFaceProvider.getTypeFace(getActivity(), TypeFaceProvider.TYPEFACE_SOURCE_SANS_PRO_LIGHT));
        //mNextButton.setTypeface(TypeFaceProvider.getTypeFace(getActivity(),TypeFaceProvider.TYPEFACE_SOURCE_SANS_PRO_LIGHT), Typeface.BOLD);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextPressed();
            }
        });

        loadViews();

        return view;
    }

    public void rotateAddDistanceMode() {
        addMode = (addMode + 1) % 4;
    }

    public String[] getArrayWithSteps(float iMinValue, float iMaxValue, float iStep, NumberFormat formatter) {
        int iStepsArray = Math.abs((int) ((iMaxValue - iMinValue) / iStep + 1)); //get the lenght array that will return
        String[] arrayValues = new String[iStepsArray]; //Create array with length of iStepsArray
        for (int i = 0; i < iStepsArray; i++) {
            arrayValues[i] = String.valueOf(formatter.format(iMinValue + (i * iStep)));
        }
        return arrayValues;
    }

    public void showRefractionChangeDialog(final View view, float min, float max, float step, final NumberFormat formatter, int title) {
        final Dialog d = new Dialog(this.getNavActivity());
        d.setTitle(title);
        d.setContentView(R.layout.number_dialog);
        d.setCancelable(true);

        Button btCancel = (Button) d.findViewById(R.id.btCancel);
        Button btSave = (Button) d.findViewById(R.id.btSave);

        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker);
        prepareNumberPicker(np, min, max, step, formatter, ((TextView) view).getText().toString());

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveNewValue(view, formatter.parse(np.getDisplayedValues()[np.getValue()]).floatValue(), formatter);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                d.dismiss();
            }
        });
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss(); // dismiss the dialog
            }
        });
        d.show();
    }

    public void showAcuityDialog(final View view, AcuityFormatter.ACUITY_TYPE type, int title) {
        final Dialog d = new Dialog(this.getNavActivity());
        d.setTitle(title);
        d.setContentView(R.layout.number_dialog);
        d.setCancelable(true);

        Button btCancel = (Button) d.findViewById(R.id.btCancel);
        Button btSave = (Button) d.findViewById(R.id.btSave);

        final NumberPicker npDen = (NumberPicker) d.findViewById(R.id.numberPicker);
        prepareNumberPicker(npDen, type, ((TextView) view).getText().toString());

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AcuityFormatter formatter = new AcuityFormatter();
                saveNewAcuityValue(view, formatter.parse(npDen.getDisplayedValues()[npDen.getValue()]), null);
                d.dismiss();
            }
        });
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss(); // dismiss the dialog
            }
        });
        d.show();
    }

    public NumberPicker prepareNumberPicker(NumberPicker np, float min, float max, float step, final NumberFormat formatter, String defaultValue) {
        final String[] values = getArrayWithSteps(min, max, step, formatter);
        np.setMaxValue(values.length - 1); // max value 100
        np.setMinValue(0);   // min value 0
        np.setDisplayedValues(values);
        try {
            if ("-.--".equals(defaultValue) || "---".equals(defaultValue)) {
                int index = (int) ((0 - min) / step);
                np.setValue(index);
            } else {
                float value = formatter.parse(defaultValue).floatValue();
                int index = (int) ((value - min) / step);
                np.setValue(index);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return np;
    }

    public NumberPicker prepareNumberPicker(NumberPicker np, AcuityFormatter.ACUITY_TYPE type, String defaultValue) {
        int[] values;
        if (type == AcuityFormatter.ACUITY_TYPE.IMPERIAL) {
            values = AcuityFormatter.snellenChartImperial;
        } else {
            values = AcuityFormatter.snellenChartMetric;
        }
        AcuityFormatter formatter = new AcuityFormatter();

        String[] valuesStr = new String[values.length + 1];
        valuesStr[0] = "--/--";
        for (int i = 0; i < values.length; i++) {
            valuesStr[i + 1] = formatter.getNominator(type) + "/" + String.valueOf(values[i]);
        }
        np.setMaxValue(values.length - 1); // max value 100
        np.setMinValue(0);   // min value 0
        np.setDisplayedValues(valuesStr);

        if ("--/--".equals(defaultValue)) {
            int index = 0;
            np.setValue(index);
        } else {
            for (int i = 0; i < valuesStr.length; i++) {
                if (valuesStr[i].equals(defaultValue)) {
                    np.setValue(i);
                }
            }
        }

        return np;
    }

    public void showEyeChangeDialog(final View view) {
        final Dialog d = new Dialog(this.getNavActivity());
        d.setTitle(R.string.adjust_refraction);
        d.setContentView(R.layout.refraction_dialog);
        d.setCancelable(true);

        Button btCancel = (Button) d.findViewById(R.id.btCancel);
        Button btSave = (Button) d.findViewById(R.id.btSave);

        final NumberPicker npSph = (NumberPicker) d.findViewById(R.id.numberPickerSph);
        final NumberPicker npCyl = (NumberPicker) d.findViewById(R.id.numberPickerCyl);
        final NumberPicker npAxis = (NumberPicker) d.findViewById(R.id.numberPickerAxis);
        final NumberPicker npAdd = (NumberPicker) d.findViewById(R.id.numberPickerAdd);

        if (view.getId() == R.id.txRight) {
            prepareNumberPicker(npSph, 20, -20, -0.25f, sphereCylFormatter, mRightSphere.getText().toString());
            if (isNegativeCylModel())
                prepareNumberPicker(npCyl, 0, -15, -0.25f, sphereCylFormatter, mRightCyl.getText().toString());
            else
                prepareNumberPicker(npCyl, +15, 0, -0.25f, sphereCylFormatter, mRightCyl.getText().toString());
            prepareNumberPicker(npAxis, 5, 180, 5, axisFormatter, mRightAxis.getText().toString());
            prepareNumberPicker(npAdd, 6, 0, -0.25f, sphereCylFormatter, mRightAdd.getText().toString());

            btSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        saveNewValue(mRightSphere, sphereCylFormatter.parse(npSph.getDisplayedValues()[npSph.getValue()]).floatValue(), sphereCylFormatter);
                        saveNewValue(mRightAxis, axisFormatter.parse(npAxis.getDisplayedValues()[npAxis.getValue()]).floatValue(), axisFormatter);
                        saveNewValue(mRightCyl, sphereCylFormatter.parse(npCyl.getDisplayedValues()[npCyl.getValue()]).floatValue(), sphereCylFormatter);
                        saveNewValue(mRightAdd, sphereCylFormatter.parse(npAdd.getDisplayedValues()[npAdd.getValue()]).floatValue(), sphereCylFormatter);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    d.dismiss();
                }
            });
        } else {
            prepareNumberPicker(npSph, 20, -20, -0.25f, sphereCylFormatter, mLeftSphere.getText().toString());
            if (isNegativeCylModel())
                prepareNumberPicker(npCyl, 0, -15, -0.25f, sphereCylFormatter, mLeftCyl.getText().toString());
            else
                prepareNumberPicker(npCyl, 15, 0, -0.25f, sphereCylFormatter, mLeftCyl.getText().toString());
            prepareNumberPicker(npAxis, 5, 180, 5, axisFormatter, mLeftAxis.getText().toString());
            prepareNumberPicker(npAdd, 6, 0, -0.25f, sphereCylFormatter, mLeftAdd.getText().toString());

            btSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        saveNewValue(mLeftSphere, sphereCylFormatter.parse(npSph.getDisplayedValues()[npSph.getValue()]).floatValue(), sphereCylFormatter);
                        saveNewValue(mLeftAxis, axisFormatter.parse(npAxis.getDisplayedValues()[npAxis.getValue()]).floatValue(), axisFormatter);
                        saveNewValue(mLeftCyl, sphereCylFormatter.parse(npCyl.getDisplayedValues()[npCyl.getValue()]).floatValue(), sphereCylFormatter);
                        saveNewValue(mLeftAdd, sphereCylFormatter.parse(npAdd.getDisplayedValues()[npAdd.getValue()]).floatValue(), sphereCylFormatter);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    d.dismiss();
                }
            });
        }
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss(); // dismiss the dialog
            }
        });

        d.show();
    }

    public void spheroPopup(final View view) {
        showRefractionChangeDialog(view, 20, -20, -0.25f, sphereCylFormatter, R.string.adjust_refraction);
    }

    public void cylPopup(final View view) {
        if (NetraGApplication.get().getSettings().isNegativeCylModel())
            showRefractionChangeDialog(view, 0, -15, -0.25f, sphereCylFormatter, R.string.adjust_refraction);
        else
            showRefractionChangeDialog(view, 0, 15, 0.25f, sphereCylFormatter, R.string.adjust_refraction);
    }

    public void axisPopup(final View view) {
        showRefractionChangeDialog(view, 5, 180, 5, axisFormatter, R.string.adjust_refraction);
    }

    public void addPopup(final View view) {
        showRefractionChangeDialog(view, 6, 0, -0.25f, sphereCylFormatter, R.string.adjust_refraction);
    }

    public void pdPopup(final View view) {
        showRefractionChangeDialog(view, 50, 80, 1, mPdFormatter, R.string.adjust_pd);
    }

    public void acuityPopup(final View view) {
        showAcuityDialog(view,
                getSettings().isImperialSystem() ? AcuityFormatter.ACUITY_TYPE.IMPERIAL : AcuityFormatter.ACUITY_TYPE.METRIC,
                R.string.acuity_label);
        //RefractionChangeDialog(view, 50, 80, 1, mPdFormatter, R.string.adjust_pd);
    }

    private void saveNewSphere(Refraction ref, boolean right, Float newValue) {
        if (isSpherocylindricalMode()) {
            if (right)
                ref.setRightSphere(newValue);
            else
                ref.setLeftSphere(newValue);
        } else {
            if (right)
                ref.setRightSphere(addSphEqToSphCylReturnsNewSph(ref.getRightSphere(), ref.getRightCylinder(), newValue));
            else
                ref.setLeftSphere(addSphEqToSphCylReturnsNewSph(ref.getLeftSphere(), ref.getLeftCylinder(), newValue));
        }
    }

    private Float addSphEqToSphCylReturnsNewSph(Float originalSph, Float originalCyl, Float newSphEq) {
        if (newSphEq == null) return originalSph;

        float originalSphEq = 0;
        if (originalSph != null)
            originalSphEq = originalSph;
        if (originalCyl != null)
            originalSphEq += originalCyl / 2;

        return (originalSph == null ? 0 : originalSph) - (originalSphEq - newSphEq);
    }

    private void saveNewValue(View view, Float newValue, NumberFormat formatter) {
        DebugExam results = NetraGApplication.get().getLastResult();

        Refraction subj = results.getOrCreateFrom(RefractionType.SUBJECTIVE, RefractionType.NETRA);

        if (!NetraGApplication.get().getSettings().isNegativeCylModel()) {
            subj.putInPositiveCilinder();
        }

        switch (view.getId()) {
            case R.id.right_sphere:
                saveNewSphere(subj, true, newValue);
                break;
            case R.id.right_cyl:
                subj.setRightCylinder(newValue);
                if (Math.abs(newValue) < 0.2) {
                    subj.setRightAxis(0f);
                }
                break;
            case R.id.right_axis:
                subj.setRightAxis(newValue);
                break;
            case R.id.right_add:
                subj.setRightAdd(newValue);
                break;

            case R.id.left_sphere:
                saveNewSphere(subj, false, newValue);
                break;
            case R.id.left_cyl:
                subj.setLeftCylinder(newValue);
                if (Math.abs(newValue) < 0.2) {
                    subj.setLeftAxis(0f);
                }
                break;
            case R.id.left_axis:
                subj.setLeftAxis(newValue);
                break;
            case R.id.left_add:
                subj.setLeftAdd(newValue);
                break;


            case R.id.pd:
                subj.setLeftPd(newValue / 2.0f);
                subj.setRightPd(newValue / 2.0f);
                break;
        }

        if (!NetraGApplication.get().getSettings().isNegativeCylModel()) {
            subj.putInNegativeCilinder();
        }

        loadViews();
    }

    private void saveNewAcuityValue(View view, Float newValue, NumberFormat formatter) {
        DebugExam results = NetraGApplication.get().getLastResult();

        Refraction subj = results.getRefraction(RefractionType.SUBJECTIVE);
        Refraction netra = results.getRefraction(RefractionType.NETRA);
        if (subj != null) {
            switch (view.getId()) {
                case R.id.right_acuity:
                    subj.setRightAcuity(newValue);
                    break;
                case R.id.left_acuity:
                    subj.setLeftAcuity(newValue);
                    break;
            }

            if (netra != null) {
                switch (view.getId()) {
                    case R.id.right_acuity:
                        if (subj.equalsRight(netra)) netra.setRightAcuity(newValue);
                        break;
                    case R.id.left_acuity:
                        if (subj.equalsLeft(netra)) netra.setLeftAcuity(newValue);
                        break;
                }
            }
        } else {
            if (netra == null)
                netra = results.getOrCreateFrom(RefractionType.NETRA, RefractionType.NETRA);

            switch (view.getId()) {
                case R.id.right_acuity:
                    netra.setRightAcuity(newValue);
                    break;
                case R.id.left_acuity:
                    netra.setLeftAcuity(newValue);
                    break;
            }
        }

        loadViews();
    }

    public void processVoice(List<String> said, boolean rightEye) {
        PrescriptionParser voiceParser = new PrescriptionParser();
        Refraction ref = null;
        if (rightEye) {
            ref = voiceParser.parseRightEye(said.get(0));
        } else {
            ref = voiceParser.parseLeftEye(said.get(0));
        }

        DebugExam results = NetraGApplication.get().getLastResult();

        Refraction subj = results.getOrCreateFrom(RefractionType.SUBJECTIVE, RefractionType.NETRA);

        if (rightEye) {
            subj.setRightSphere(ref.getRightSphere());
            subj.setRightCylinder(ref.getRightCylinder());
            subj.setRightAxis(ref.getRightAxis());
        } else {
            subj.setLeftSphere(ref.getLeftSphere());
            subj.setLeftCylinder(ref.getLeftCylinder());
            subj.setLeftAxis(ref.getLeftAxis());
        }

        //Toast.makeText(this.getNavActivity(), said.get(0), Toast.LENGTH_LONG).show();

        loadViews();
    }

    private void formatTextCylToggle() {
        if (isNegativeCylModel()) {
            mTxCyl.setText(R.string.minus_cyl_label);
            mTxCylChange.setText(R.string.plus);
            mRlCyl.setBackgroundResource(R.drawable.bkg_minus_cyl_box);
            mTxCyl.setTextAppearance(this.getActivity(), R.style.MinusCylBoxText);
            mTxCylChange.setTextAppearance(this.getActivity(), R.style.MinusCylChangeBoxText);
        } else {
            mTxCyl.setText(R.string.plus_cyl_label);
            mTxCylChange.setText(R.string.minus);
            mRlCyl.setBackgroundResource(R.drawable.bkg_plus_cyl_box);
            mTxCyl.setTextAppearance(this.getActivity(), R.style.PlusCylBoxText);
            mTxCylChange.setTextAppearance(this.getActivity(), R.style.PlusCylChangeBoxText);
        }
    }

    private void formatTextSphEqToggle() {
        if (isSpherocylindricalMode()) {
            mTxSph.setText(R.string.sph);
            mTxSphChange.setText(R.string.sph_eq_label);
            mRlSph.setBackgroundResource(R.drawable.bkg_sph_cyl_box);
            mTxSph.setTextAppearance(this.getActivity(), R.style.SphCylBoxText);
            mTxSphChange.setTextAppearance(this.getActivity(), R.style.SphCylChangeBoxText);
        } else {
            mTxSph.setText(R.string.sph_eq_label);
            mTxSphChange.setText(R.string.sph);
            mRlSph.setBackgroundResource(R.drawable.bkg_sph_eq_box);
            mTxSph.setTextAppearance(this.getActivity(), R.style.SphEqBoxText);
            mTxSphChange.setTextAppearance(this.getActivity(), R.style.SphEqChangeBoxText);
        }
    }

    public void reloadTextsInPatientLanguage() {
    }

    private void loadViews() {
        DebugExam results = NetraGApplication.get().getLastResult();

        imAttentionLeft.setVisibility(View.GONE);
        imAttentionRight.setVisibility(View.GONE);
        imLousyLeft.setVisibility(View.GONE);
        imLousyRight.setVisibility(View.GONE);

        mEdNote.setError(null);
        mEdEmail.setError(null);
        mEdAge.setError(null);
        mPhoneEditText.setError(null);
        mRightSphere.setError(null);
        mLeftSphere.setError(null);
        mPD.setError(null);

        if (results == null) {
            mLeftSphere.setText("-.--");
            mLeftCyl.setText("-.--");
            mLeftAxis.setText("---");
            mLeftAdd.setText("-.--");
            mLeftAcuity.setText("---");
            mRightSphere.setText("-.--");
            mRightCyl.setText("-.--");
            mRightAxis.setText("---");
            mRightAdd.setText("-.--");
            mRightAcuity.setText("---");

            mLeftSphereOld.setText("-.--");
            mLeftCylOld.setText("-.--");
            mLeftAxisOld.setText("---");
            mLeftAddOld.setText("-.--");
            mRightSphereOld.setText("-.--");
            mRightCylOld.setText("-.--");
            mRightAxisOld.setText("---");
            mRightAddOld.setText("-.--");

            mPD.setText("---");
            mPDOld.setText("---");

            mEdAge.setText("");
            mEdNote.setText("");
            mEdEmail.setText("");
            mPhoneInputView.setDefault();
            ;

            return;
        }

        mEdAge.setEnabled(!results.isPrescribed());
        mEdNote.setEnabled(!results.isPrescribed());

        mEdEmail.setEnabled(!results.isPrescribed());
        mPhoneInputView.setEnabled(!results.isPrescribed());

        if (results.isPrescribed()) {
            mTxRight.setOnClickListener(null);
            mTxLeft.setOnClickListener(null);

            mLeftSphere.setOnClickListener(null);
            mLeftCyl.setOnClickListener(null);
            mLeftAxis.setOnClickListener(null);
            mLeftAdd.setOnClickListener(null);
            mLeftAcuity.setOnClickListener(null);

            mRightSphere.setOnClickListener(null);
            mRightCyl.setOnClickListener(null);
            mRightAxis.setOnClickListener(null);
            mRightAdd.setOnClickListener(null);
            mRightAcuity.setOnClickListener(null);

            mTxAdd.setOnClickListener(null);
            mTxSuggestedFor.setVisibility(View.INVISIBLE);

            mPD.setOnClickListener(null);
        } else {
            mTxRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResultsFragment.this.showEyeChangeDialog(view);
                }
            });
            mTxLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResultsFragment.this.showEyeChangeDialog(view);
                }
            });

            mLeftSphere.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResultsFragment.this.spheroPopup(view);
                }
            });
            mLeftCyl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResultsFragment.this.cylPopup(view);
                }
            });
            mLeftAxis.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResultsFragment.this.axisPopup(view);
                }
            });
            mLeftAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResultsFragment.this.addPopup(view);
                }
            });
            mLeftAcuity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResultsFragment.this.acuityPopup(view);
                }
            });

            mRightSphere.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResultsFragment.this.spheroPopup(view);
                }
            });
            mRightCyl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResultsFragment.this.cylPopup(view);
                }
            });
            mRightAxis.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResultsFragment.this.axisPopup(view);
                }
            });
            mRightAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResultsFragment.this.addPopup(view);
                }
            });
            mRightAcuity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResultsFragment.this.acuityPopup(view);
                }
            });

            mPD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResultsFragment.this.pdPopup(view);
                }
            });

            mTxAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rotateAddDistanceMode();
                    recomputeAdd();
                    loadViews();
                    animateAddChange();
                }
            });

            mTxSuggestedFor.setVisibility(View.VISIBLE);
            if (getSettings().isImperialSystem()) {
                switch (addMode) {
                    case 0:
                        mTxSuggestedFor.setText(getResources().getText(R.string.recommended_use_smartphone) + " (" + Math.round(DISTANCES[addMode] * 100 * 0.393701) + "in)");
                        break;
                    case 1:
                        mTxSuggestedFor.setText(getResources().getText(R.string.recommended_use_reading) + " (" + Math.round(DISTANCES[addMode] * 100 * 0.393701) + "in)");
                        break;
                    case 2:
                        mTxSuggestedFor.setText(getResources().getText(R.string.recommended_use_computer) + " (" + Math.round(DISTANCES[addMode] * 100 * 0.393701) + "in)");
                        break;
                    case 3:
                        mTxSuggestedFor.setText(getResources().getText(R.string.recommended_use_driving) + " (" + Math.round(DISTANCES[addMode] * 100 * 0.393701) + "in)");
                        break;
                }
            } else {
                switch (addMode) {
                    case 0:
                        mTxSuggestedFor.setText(getResources().getText(R.string.recommended_use_smartphone) + " (" + Math.round(DISTANCES[addMode] * 100) + "cm)");
                        break;
                    case 1:
                        mTxSuggestedFor.setText(getResources().getText(R.string.recommended_use_reading) + " (" + Math.round(DISTANCES[addMode] * 100) + "cm)");
                        break;
                    case 2:
                        mTxSuggestedFor.setText(getResources().getText(R.string.recommended_use_computer) + " (" + Math.round(DISTANCES[addMode] * 100) + "cm)");
                        break;
                    case 3:
                        mTxSuggestedFor.setText(getResources().getText(R.string.recommended_use_driving) + " (" + Math.round(DISTANCES[addMode] * 100) + "cm)");
                        break;
                }
            }
        }

        mEdNote.removeTextChangedListener(mEdNoteListener);
        mEdEmail.removeTextChangedListener(mEdEmailListener);

        mEdEmail.setText(results.getPrescriptionEmail());
        mPhoneInputView.setNumber(results.getPrescriptionPhone());
        mEdNote.setText(results.getStudyName());
        loadAge(results);

        mEdNote.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                int result = actionId & EditorInfo.IME_MASK_ACTION;
                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:
                        textView.clearFocus();
                        getNavActivity().hideKeyboard();
                        break;
                    case EditorInfo.IME_ACTION_NEXT:
                        textView.clearFocus();
                        getNavActivity().hideKeyboard();
                        break;
                }

                if (NetraGApplication.get().getLastResult() != null) {
                    NetraGApplication.get().getLastResult().setStudyName(mEdNote.getText().toString());
                    return true;
                }
                return false;
            }
        });
        mEdNote.addTextChangedListener(mEdNoteListener);
        mEdEmail.addTextChangedListener(mEdEmailListener);

        if (results.isPrescribed()) {
            if (results.getPrescriptionEmail() == null || results.getPrescriptionEmail().trim().isEmpty())
                mEdEmail.setVisibility(View.GONE);
            if (results.getPrescriptionPhone() == null || results.getPrescriptionPhone().trim().isEmpty())
                mPhoneInputView.setVisibility(View.GONE);
        }

        Refraction changed = results.getRefraction(RefractionType.SUBJECTIVE);
        Refraction original = results.getRefraction(RefractionType.NETRA);

        if (ConfidenceFormatter.isLousy(results.getFittingQualityLeft())) {
            imLousyLeft.setVisibility(View.VISIBLE);
        } else if (ConfidenceFormatter.isPoor(results.getFittingQualityLeft())) {
            imAttentionLeft.setVisibility(View.VISIBLE);
        }

        if (ConfidenceFormatter.isLousy(results.getFittingQualityRight())) {
            imLousyRight.setVisibility(View.VISIBLE);
        } else if (ConfidenceFormatter.isPoor(results.getFittingQualityRight())) {
            imAttentionRight.setVisibility(View.VISIBLE);
        }

        formatTextCylToggle();
        formatTextSphEqToggle();

        if (original != null) {
            if (isNegativeCylModel()) {
                original.putInNegativeCilinder();
            } else {
                original.putInPositiveCilinder();
            }

            if (changed != null) {
                if (isNegativeCylModel()) {
                    changed.putInNegativeCilinder();
                } else {
                    changed.putInPositiveCilinder();
                }

                setMainPowersUp(changed);

                mInstructions1.setText(R.string.subj_refraction);

                // Avoid saving with Positive
                changed.putInNegativeCilinder();
            } else {
                setMainPowersUp(original);

                mInstructions1.setText(R.string.netra_results);
            }

            // ConfidenceFormatter.format(results.getFittingQualityLeft(), getResources())
            // ConfidenceFormatter.format(results.getFittingQualityRight(), getResources())

            setOldPowersUp(original);

            // Avoid saving with Positive
            original.putInNegativeCilinder();
        } else {
            mLeftSphereOld.setText("-.--");
            mLeftCylOld.setText("-.--");
            mLeftAxisOld.setText("---");
            mLeftAddOld.setText("-.--");
            mLeftAcuityOld.setText("-.--");
            mRightSphereOld.setText("-.--");
            mRightCylOld.setText("-.--");
            mRightAxisOld.setText("---");
            mRightAddOld.setText("-.--");
            mRightAcuityOld.setText("-.--");

            if (changed != null) {
                if (isNegativeCylModel()) {
                    changed.putInNegativeCilinder();
                } else {
                    changed.putInPositiveCilinder();
                }

                setMainPowersUp(changed);

                mInstructions1.setText(R.string.subj_refraction);

                // Avoid saving with Positive
                changed.putInNegativeCilinder();
            } else {
                mLeftSphere.setText("-.--");
                mLeftCyl.setText("-.--");
                mLeftAxis.setText("---");
                mLeftAdd.setText("-.--");
                mLeftAcuity.setText("---");

                mRightSphere.setText("-.--");
                mRightCyl.setText("-.--");
                mRightAxis.setText("---");
                mRightAdd.setText("-.--");
                mRightAcuity.setText("---");
            }
        }

        setVisibleIfDifferent(mRightSphere, mRightSphereOld);
        setVisibleIfDifferent(mRightCyl, mRightCylOld);
        setVisibleIfDifferent(mRightAxis, mRightAxisOld);
        setVisibleIfDifferent(mRightAdd, mRightAddOld);
        setVisibleIfDifferent(mRightAcuity, mRightAcuityOld);
        setVisibleIfDifferent(mLeftSphere, mLeftSphereOld);
        setVisibleIfDifferent(mLeftCyl, mLeftCylOld);
        setVisibleIfDifferent(mLeftAxis, mLeftAxisOld);
        setVisibleIfDifferent(mLeftAdd, mLeftAddOld);
        setVisibleIfDifferent(mLeftAcuity, mLeftAcuityOld);
        setVisibleIfDifferent(mPD, mPDOld);

        mNextButton.setText(R.string.finish_uppercase);
    }

    private void loadAge(DebugExam results) {
        Integer age = getFormattedAge(results);
        if (age != null)
            mEdAge.setText(age.toString());
        else
            mEdAge.getText().clear();
    }

    final Property<TextView, Integer> property = new Property<TextView, Integer>(int.class, "textColor") {
        @Override
        public Integer get(TextView object) {
            return object.getCurrentTextColor();
        }

        @Override
        public void set(TextView object, Integer value) {
            object.setTextColor(value);
        }
    };

    private void animateAddChange() {
        final TextView t1;
        final TextView t2;

        DebugExam results = NetraGApplication.get().getLastResult();
        if (results.getRefraction(RefractionType.SUBJECTIVE) != null) {
            t1 = mLeftAddOld;
            t2 = mRightAddOld;
        } else {
            t1 = mLeftAdd;
            t2 = mRightAdd;
        }

        ObjectAnimator animator = ObjectAnimator.ofInt(t1, property, getResources().getColor(R.color.colorAccent));
        animator.setDuration(1200L);
        animator.setEvaluator(new ArgbEvaluator());
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                ObjectAnimator animator2 = ObjectAnimator.ofInt(t1, property, Color.BLACK);
                animator2.setDuration(1200L);
                animator2.setEvaluator(new ArgbEvaluator());
                animator2.setInterpolator(new AccelerateDecelerateInterpolator());
                animator2.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        ObjectAnimator animator3 = ObjectAnimator.ofInt(t2, property, getResources().getColor(R.color.colorAccent));
        animator3.setDuration(1200L);
        animator3.setEvaluator(new ArgbEvaluator());
        animator3.setInterpolator(new AccelerateDecelerateInterpolator());
        animator3.start();
        animator3.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                ObjectAnimator animator4 = ObjectAnimator.ofInt(t2, property, Color.BLACK);
                animator4.setDuration(1200L);
                animator4.setEvaluator(new ArgbEvaluator());
                animator4.setInterpolator(new AccelerateDecelerateInterpolator());
                animator4.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
    }

    public void recomputeAdd() {
        if (NetraGApplication.get().getLastResult().getDateOfBirth() == null || getFormattedAge(NetraGApplication.get().getLastResult()) == null) {
            Refraction netra = NetraGApplication.get().getLastResult().getOrCreateFrom(RefractionType.NETRA, RefractionType.NETRA);
            netra.setLeftAdd(null);
            netra.setRightAdd(null);
        } else {
            int age = getFormattedAge(NetraGApplication.get().getLastResult());
            float add = AddSuggestions.wantToReadAt(age, DISTANCES[addMode]);
            Refraction netra = NetraGApplication.get().getLastResult().getOrCreateFrom(RefractionType.NETRA, RefractionType.NETRA);
            netra.setLeftAdd(add);
            netra.setRightAdd(add);
        }
    }

    public Calendar getDefaultDate() {
        final Calendar c = Calendar.getInstance();
        if (NetraGApplication.get().getLastResult() != null && NetraGApplication.get().getLastResult().getDateOfBirth() != null) {
            c.setTime(NetraGApplication.get().getLastResult().getDateOfBirth());
        } else {
            c.set(Calendar.YEAR, c.get(Calendar.YEAR) - 28);
        }
        return c;
    }

    public String getDateTitle() {
        return getResources().getString(R.string.date_of_birth);
    }

    public void onDateChanged(Calendar c) {
        if (NetraGApplication.get().getLastResult() != null) {
            NetraGApplication.get().getLastResult().setDateOfBirth(c.getTime());
            recomputeAdd();
            loadViews();
            animateAddChange();
        }
    }


    private void setMainPowersUp(Refraction r) {
        setMainPowersUp(
                r.getRightSphere(), r.getRightCylinder(), r.getRightAxis(), r.getRightAdd(),
                r.getLeftSphere(), r.getLeftCylinder(), r.getLeftAxis(), r.getLeftAdd(),
                r.getRightPd(), r.getLeftPd(), r.getRightAcuity(), r.getLeftAcuity());
    }

    private void setMainPowersUp(Float rSph, Float rCyl, Float rAxis, Float rAdd,
                                 Float lSph, Float lCyl, Float lAxis, Float lAdd,
                                 Float rPD, Float lPD, Float rAcuity, Float lAcuity) {
        if (isSpherocylindricalMode()) {
            mLeftSphere.setText(formatSphere(lSph));
            mLeftCyl.setText(formatCylinder(lCyl));
            mLeftAxis.setText(formatAxis(lCyl, lAxis));
            mLeftAdd.setText(formatAdd(lAdd));
            mLeftAcuity.setText(formatAcuity(lAcuity));

            mRightSphere.setText(formatSphere(rSph));
            mRightCyl.setText(formatCylinder(rCyl));
            mRightAxis.setText(formatAxis(rCyl, rAxis));
            mRightAdd.setText(formatAdd(rAdd));
            mRightAcuity.setText(formatAcuity(rAcuity));
        } else {
            mLeftSphere.setText(formatSphereEq(lSph, lCyl));
            mLeftCyl.setText("-.--");
            mLeftAxis.setText("---");
            mLeftAdd.setText(formatAdd(lAdd));
            mLeftAcuity.setText(formatAcuity(lAcuity));

            mRightSphere.setText(formatSphereEq(rSph, rCyl));
            mRightCyl.setText("-.--");
            mRightAxis.setText("---");
            mRightAdd.setText(formatAdd(rAdd));
            mRightAcuity.setText(formatAcuity(rAcuity));
        }

        mPD.setText(formatPd(rPD, lPD));
    }

    private void setOldPowersUp(Refraction r) {
        setOldPowersUp(
                r.getRightSphere(), r.getRightCylinder(), r.getRightAxis(), r.getRightAdd(),
                r.getLeftSphere(), r.getLeftCylinder(), r.getLeftAxis(), r.getLeftAdd(),
                r.getRightPd(), r.getLeftPd(), r.getRightAcuity(), r.getLeftAcuity());
    }

    private void setOldPowersUp(Float rSph, Float rCyl, Float rAxis, Float rAdd,
                                Float lSph, Float lCyl, Float lAxis, Float lAdd,
                                Float rPD, Float lPD, Float rAcuity, Float lAcuity) {
        if (isSpherocylindricalMode()) {
            mLeftSphereOld.setText(formatSphere(lSph));
            mLeftCylOld.setText(formatCylinder(lCyl));
            mLeftAxisOld.setText(formatAxis(lCyl, lAxis));
            mLeftAddOld.setText(formatAdd(lAdd));
            mLeftAcuityOld.setText(formatAcuity(lAcuity));

            mRightSphereOld.setText(formatSphere(rSph));
            mRightCylOld.setText(formatCylinder(rCyl));
            mRightAxisOld.setText(formatAxis(rCyl, rAxis));
            mRightAddOld.setText(formatAdd(rAdd));
            mRightAcuityOld.setText(formatAcuity(rAcuity));
        } else {
            mLeftSphereOld.setText(formatSphereEq(lSph, lCyl));
            mLeftCylOld.setText("-.--");
            mLeftAxisOld.setText("---");
            mLeftAddOld.setText(formatAdd(lAdd));
            mLeftAcuityOld.setText(formatAcuity(lAcuity));

            mRightSphereOld.setText(formatSphereEq(rSph, rCyl));
            mRightCylOld.setText("-.--");
            mRightAxisOld.setText("---");
            mRightAddOld.setText(formatAdd(rAdd));
            mRightAcuityOld.setText(formatAcuity(rAcuity));
        }

        mPDOld.setText(formatPd(rPD, lPD));
    }

    private boolean isNegativeCylModel() {
        if (getNavActivity() != null)
            return getNavActivity().getApp().getSettings().isNegativeCylModel();
        else
            return true;
    }

    private boolean isSpherocylindricalMode() {
        if (getNavActivity() != null)
            return getNavActivity().getApp().getSettings().isSpherocylindricalMode();
        else
            return true;
    }

    public void setVisibleIfDifferent(TextView v1, TextView v1Old) {
        if (!v1.getText().equals(v1Old.getText()))
            v1Old.setVisibility(View.VISIBLE);
        else
            v1Old.setVisibility(View.GONE);
    }

    public boolean isRightValid(Refraction p) {
        return (p.getRightSphere() != null);
    }

    public boolean isLeftValid(Refraction p) {
        return (p.getLeftSphere() != null);
    }

    protected String formatPd(Float right, Float left) {
        if (right == null || left == null) return "-.--";
        return mPdFormatter.format((double) right + left);
    }

    protected String formatSphere(Float f) {
        if (f == null) return "-.--";

        return sphereCylFormatter.format(f);
    }

    protected String formatAdd(Float f) {
        if (f == null) return "-.--";
        if (f > 98) return "-.--";

        return sphereCylFormatter.format(f);
    }

    protected String formatSphereEq(Float f, Float cyl) {
        if (f == null && cyl == null) return "-.--";

        if (f == null) f = 0.0f;

        if (cyl == null) {
            return sphereCylFormatter.format(f);
        }

        return sphereCylFormatter.format(f + cyl / 2);
    }

    protected String formatCylinder(Float f) {
        if (f == null) return "-.--";

        return sphereCylFormatter.format(f);
    }

    protected String formatAxis(Float cyl, Float axis) {
        if (cyl == null) return "---";
        if (axis == null) return "---";

        float fl = axis;
        if (Math.abs(fl) < 0.0001)
            fl = 180;
        return axisFormatter.format(fl);
    }

    protected String formatAcuity(Float acuity) {
        if (acuity == null) return "--/--";

        AcuityFormatter formatter = new AcuityFormatter();
        return formatter.format(acuity, NetraGApplication.get().getSettings().isImperialSystem() ? AcuityFormatter.ACUITY_TYPE.IMPERIAL : AcuityFormatter.ACUITY_TYPE.METRIC);
    }

    @Override
    public void onResume() {
        super.onResume();
        getNavActivity().showMenu();
        getNavActivity().enablePrinterIfFound();
        getNavActivity().hideLanguageButton();
        getNavActivity().hideNewCustomReadingButton();
    }

    @Override
    public void onPause() {
        if (NetraGApplication.get().getLastResult() != null)
            new AllowDeletionAfterSynced().execute(NetraGApplication.get().getLastResult());

        super.onPause();
    }

    public boolean onBackPressed() {
        if (goBackHome) {
            ((NavActivity) getActivity()).loadHomeFragment();
            return true;
        } else {
            ((NavActivity) getActivity()).loadReadingsFragment(false);
            return true;
        }
    }

    public void onNextPressed() {
        if (goBackHome)
            getNavActivity().loadHomeFragment();
        else
            getNavActivity().loadReadingsFragment(false);
    }

    public void showAttentionPoorResultsMsg() {
        AlertDialog.Builder authDialog = new AlertDialog.Builder(this.getNavActivity());
        authDialog.setTitle(R.string.poor_results_title);
        authDialog.setMessage(R.string.poor_results_desc);
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

    public void showAttentionLousyResultsMsg() {
        AlertDialog.Builder authDialog = new AlertDialog.Builder(this.getNavActivity());
        authDialog.setTitle(R.string.lousy_results_title);
        authDialog.setMessage(R.string.lousy_results_desc);
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


    public Integer getFormattedAge(DebugExam table) {
        return AgeCalculator.calculateAge(table.getDateOfBirth());
    }

    public class AllowDeletionAfterSynced extends AsyncTask<DebugExam, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(DebugExam... params) {
            for (DebugExam table : params)
                synchronized (NetraGApplication.get().getSqliteHelper()) {
                    table.setStudyName(mEdNote.getText().toString());
                    NetraGApplication.get().getSqliteHelper().saveDebugExam(table);
                    NetraGApplication.get().getSqliteHelper().debugExamTable.setToSyncDebug(table);
                    NetraGApplication.get().getSqliteHelper().debugExamTable.setToSyncInsight(table);
                    NetraGApplication.get().getSqliteHelper().debugExamTable.setReadyToDeleteWhenSync(table);

                    if (!table.isPrescribed() && table.isReadyToPrescribe())
                        NetraGApplication.get().getSqliteHelper().debugExamTable.setToSyncPrescription(table);
                }
            return true;
        }
    }
}
