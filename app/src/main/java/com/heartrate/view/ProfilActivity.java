package com.heartrate.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.heartrate.R;
import com.heartrate.model.BpmDbAdapter;

import java.util.Calendar;

public class ProfilActivity extends Activity implements RadioGroup.OnCheckedChangeListener {


    private int mYear = 1984;
    private int mBpmMin = 0;
    private int mBpmMax = 0;
    private int mSexe = R.id.sexe_male;
    private String user;

    private EditText mTextBpmMin;
    private EditText mTextBpmMax;
    private SharedPreferences mSharedPref;
    private int mAge;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_layout_profil);

        user = getString(R.string.text_default);

        //get Shared_pref of a user
        mSharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        mSexe = mSharedPref.getInt(getString(R.string.value_sexe), mSexe);
        mYear = mSharedPref.getInt(getString(R.string.value_year), mYear);

        //sexe
        RadioGroup radioSexeGroup = (RadioGroup) findViewById(R.id.profil_sexe);
        radioSexeGroup.check(mSexe);
        radioSexeGroup.setOnCheckedChangeListener(this);
        //age
        final EditText textYear = (EditText) findViewById(R.id.edit_year);
        textYear.setText(String.valueOf(mYear));
        Calendar calendar = Calendar.getInstance();
        int curYear = calendar.get(Calendar.YEAR);
        mAge = curYear - mYear;
        //BPMs
        mTextBpmMin = (EditText) findViewById(R.id.profil_bpm_min);
        mTextBpmMax = (EditText) findViewById(R.id.profil_bpm_max);


        updateBPM();

        //Valid button
        Button valid = (Button) findViewById(R.id.profil_valid);
        valid.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getApplicationContext(), R.string.text_profile_saved, duration);
                toast.show();
                mYear = Integer.valueOf(textYear.getText().toString());
                mBpmMin = Integer.valueOf(mTextBpmMin.getText().toString());
                mBpmMax = Integer.valueOf(mTextBpmMax.getText().toString());
                onDialogChangeProfilClick(mSexe, mYear, mBpmMin, mBpmMax);
            }
        });

    }


    public void updateBPM() {
        BpmDbAdapter dbHelper = new BpmDbAdapter(this);
        dbHelper.open();
        mBpmMax = dbHelper.getBpmMax(user);
        mBpmMin = dbHelper.getBpmMin(user);
        dbHelper.close();

        SharedPreferences.Editor editor = mSharedPref.edit();

        if (mBpmMin != 0) {
            if (mBpmMin == mBpmMax)
                if (mBpmMin < 100)
                    initBpmMax();
                else
                    initBpmMin();
        } else {
            initBpmMin();
            initBpmMax();
        }

        editor.putInt(getString(R.string.value_bpm_min), mBpmMin);
        editor.putInt(getString(R.string.value_bpm_max), mBpmMax);
        editor.commit();

        mTextBpmMin.setText(String.valueOf(mBpmMin));
        mTextBpmMax.setText(String.valueOf(mBpmMax));
    }

    private void initBpmMin() {
        switch (mAge) {
            case 0:
                mBpmMin = 90 + (190 - 90) / 2;
                break;
            case 1:
            case 2:
                mBpmMin = 70 + (150 - 70) / 2;
                break;
            case 3:
            case 4:
            case 5:
                mBpmMin = 70 + (140 - 70) / 2;
                break;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
                mBpmMin = 65 + (125 - 65) / 2;
                break;
            default:
                mBpmMin = 75;
        }
    }

    private void initBpmMax() {
        if (mSexe == R.id.sexe_male)
            mBpmMax = 220 - mAge;
        else
            mBpmMax = 226 - mAge;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        mSexe = checkedId;
    }

    public void onDialogChangeProfilClick(int sexe, int year, int bpmMin, int bpmMax) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt(getString(R.string.value_sexe), sexe);
        editor.putInt(getString(R.string.value_year), year);
        editor.putInt(getString(R.string.value_bpm_min), bpmMin);
        editor.putInt(getString(R.string.value_bpm_max), bpmMax);
        editor.commit();
        finish();
    }

    public void updateBpmWithDb(View v) {
        //BDD
        BpmDbAdapter dbHelper = new BpmDbAdapter(this);
        dbHelper.open();
        int bpmMax = dbHelper.getBpmMax(user);
        int bpmMin = dbHelper.getBpmMin(user);
        dbHelper.close();

        if (bpmMin != 0 && bpmMax != 0) {
            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.putInt(getString(R.string.value_bpm_min), bpmMin);
            editor.putInt(getString(R.string.value_bpm_max), bpmMax);
            editor.commit();

            updateBPM();
        }
    }

}