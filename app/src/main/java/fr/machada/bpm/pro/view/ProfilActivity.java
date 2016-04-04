package fr.machada.bpm.pro.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import fr.machada.bpm.pro.R;

public class ProfilActivity extends Activity implements RadioGroup.OnCheckedChangeListener {


    public static final String VIEW_NAME_HEADER_IMAGE = "profile:header:image";
    private int mYear = 1984;
    private int mBpmMin = 0;
    private int mBpmMax = 0;
    private int mSexe = R.id.sexe_male;
    private String user;

    private EditText mTextBpmMin;
    private EditText mTextBpmMax;
    private SharedPreferences mSharedPref;
    private int mAge;
    private TextView mTextBpmMinDef;
    private TextView mTextBpmMaxDef;
    private int mBpmMinRef;
    private int mBpmMaxRef;
    private boolean mBpmMinIsDefault;
    private boolean mBpmMaxIsDefault;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_layout_profil);

        //transition
        ImageView headerImageView = (ImageView) findViewById(R.id.profile_img);
        ViewCompat.setTransitionName(headerImageView, VIEW_NAME_HEADER_IMAGE);

        user = getString(R.string.text_default);
        Calendar calendar = Calendar.getInstance();
        final int curYear = calendar.get(Calendar.YEAR);

        //get Shared_pref of a user
        mSharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        mSexe = mSharedPref.getInt(getString(R.string.value_sexe), mSexe);
        mYear = mSharedPref.getInt(getString(R.string.value_year), mYear);
        mBpmMin = mSharedPref.getInt(getString(R.string.value_bpm_min), mBpmMin);
        mBpmMax = mSharedPref.getInt(getString(R.string.value_bpm_max), mBpmMax);

        //sexe
        RadioGroup radioSexeGroup = (RadioGroup) findViewById(R.id.profil_sexe);
        radioSexeGroup.check(mSexe);
        radioSexeGroup.setOnCheckedChangeListener(this);
        //age
        mAge = curYear - mYear;
        final EditText textYear = (EditText) findViewById(R.id.edit_year);
        textYear.setText(String.valueOf(mYear));
        textYear.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!textYear.getText().toString().isEmpty()) {
                    mYear = Integer.valueOf(textYear.getText().toString());
                    mAge = curYear - mYear;
                    if (0 < mAge && mAge < 150) {
                        //change HRMin
                        if (mBpmMinIsDefault) {
                            initBpmMinRef();
                            initBpmMin();
                        }
                        //change HRMax
                        if (mBpmMaxIsDefault) {
                            initBpmMaxRef();
                            initBpmMax();
                        }
                    }
                }
            }
        });

        //BPMs
        mTextBpmMin = (EditText) findViewById(R.id.profil_bpm_min);
        mTextBpmMax = (EditText) findViewById(R.id.profil_bpm_max);
        mTextBpmMinDef = (TextView) findViewById(R.id.profil_bpm_min_is_default);
        mTextBpmMaxDef = (TextView) findViewById(R.id.profil_bpm_max_is_default);

        initBpmMinRef();
        initBpmMaxRef();
        updateBPM();

        //
        mTextBpmMin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mTextBpmMinDef.setText("");
            }
        });
        mTextBpmMax.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mTextBpmMaxDef.setText("");
            }
        });

        //Valid button
        Button valid = (Button) findViewById(R.id.profil_valid);
        valid.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getApplicationContext(), R.string.text_profile_saved, duration);
                toast.show();
                if (!textYear.getText().toString().isEmpty())
                    mYear = Integer.valueOf(textYear.getText().toString());
                mBpmMin = Integer.valueOf(mTextBpmMin.getText().toString());
                mBpmMax = Integer.valueOf(mTextBpmMax.getText().toString());
                onDialogChangeProfilClick(mSexe, mYear, mBpmMin, mBpmMax, mBpmMinRef, mBpmMaxRef);
            }
        });

    }

    public boolean isUpdateOnBpmMin() {
        if (mBpmMin != 0) {
            if (mBpmMin >= mBpmMinRef)
                return true;
        } else
            return true;
        return false;
    }

    public boolean isUpdateOnBpmMax() {
        if (mBpmMin != 0) {
            if (mBpmMax <= mBpmMaxRef)
                return true;
        } else
            return true;
        return false;
    }

    private void initBpmMinRef() {
        switch (mAge) {
            case 0:
                mBpmMinRef = 190;
                break;
            case 1:
            case 2:
                mBpmMinRef = 150;
                break;
            case 3:
            case 4:
            case 5:
                mBpmMinRef = 140;
                break;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
                mBpmMinRef = 125;
                break;
            default:
                mBpmMinRef = 80;
        }
    }

    private void initBpmMaxRef() {
        if (mSexe == R.id.sexe_male)
            mBpmMaxRef = 220 - mAge;
        else
            mBpmMaxRef = 226 - mAge;
    }

    public void updateBPM() {
        if (isUpdateOnBpmMin())
            initBpmMin();
        else
            mTextBpmMin.setText(String.valueOf(mBpmMin));
        if (isUpdateOnBpmMax())
            initBpmMax();
        else
            mTextBpmMax.setText(String.valueOf(mBpmMax));
    }

    private void initBpmMin() {
        mBpmMin = mBpmMinRef;
        mTextBpmMin.setText(String.valueOf(mBpmMin));
        mTextBpmMinDef.setText(getString(R.string.text_default));
        mBpmMinIsDefault = true;
    }

    private void initBpmMax() {
        mBpmMax = mBpmMaxRef;
        mTextBpmMax.setText(String.valueOf(mBpmMax));
        mTextBpmMaxDef.setText(getString(R.string.text_default));
        mBpmMaxIsDefault = true;

    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        mSexe = checkedId;
        // change HRMax default
        if (mBpmMaxIsDefault) {
            initBpmMaxRef();
            initBpmMax();
        }
    }

    public void onDialogChangeProfilClick(int sexe, int year, int bpmMin, int bpmMax, int bpmMinRef, int bpmMaxRef) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt(getString(R.string.value_sexe), sexe);
        editor.putInt(getString(R.string.value_year), year);
        editor.putInt(getString(R.string.value_bpm_min), bpmMin);
        editor.putInt(getString(R.string.value_bpm_max), bpmMax);
        editor.putInt(getString(R.string.value_bpm_min_ref), bpmMinRef);
        editor.putInt(getString(R.string.value_bpm_max_ref), bpmMaxRef);
        editor.commit();
        finish();
    }

}