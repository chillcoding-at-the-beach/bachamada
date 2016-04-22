package fr.machada.bpm.pro.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import java.util.Calendar;

import fr.machada.bpm.pro.R;
import fr.machada.bpm.pro.model.Effort;

/**
 * Created by macha on 22/04/16.
 */
public class FCIndicator {

    public static int mPercentWalking = 65;
    private int mGreenStep;
    private int mYelloStep;
    private int mOrangStep;
    public static int mPercentGuru = 41;
    public static int mPercentSpeed = 85;
    public static int mPercentSpeedMore = 90;


    public FCIndicator(Context ctxt, Resources rsrc) {
        initSteps(ctxt, rsrc);
    }

    private void initSteps(Context ctxt, Resources rsrc) {

        SharedPreferences sharedPref = ctxt.getSharedPreferences(rsrc.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        int sexe = R.id.sexe_male;
        sexe = sharedPref.getInt(rsrc.getString(R.string.value_sexe), sexe);
        int year = 1984;
        year = sharedPref.getInt(rsrc.getString(R.string.value_year), year);

        Calendar calendar = Calendar.getInstance();
        int curYear = calendar.get(Calendar.YEAR);
        int age = curYear - year;
        int bpm;
        if (sexe == R.id.sexe_male)
            bpm = 220 - age;
        else
            bpm = 226 - age;

        int bpmMin = 0;
        int bpmMax = 0;
        bpmMin = sharedPref.getInt(rsrc.getString(R.string.value_bpm_min), bpmMin);
        bpmMax = sharedPref.getInt(rsrc.getString(R.string.value_bpm_max), bpmMax);
        int diff = bpmMax - bpmMin;

        if (bpmMin != 0 && bpmMax != 0 && diff > 50) {
            mGreenStep = bpmMin + mPercentGuru * (bpmMax - bpmMin) / 100;
            mYelloStep = bpmMin + mPercentSpeed * (bpmMax - bpmMin) / 100;
            mOrangStep = bpmMin + mPercentSpeedMore * (bpmMax - bpmMin) / 100;
        } else {
            mGreenStep = mPercentGuru * bpm / 100;
            mYelloStep = mPercentSpeed * bpm / 100;
            mOrangStep = mPercentSpeedMore * bpm / 100;
        }
    }

    public Effort getStep(int value) {
        if (value < mGreenStep)
            return Effort.GURU;
        else if (value < mYelloStep)
            return Effort.WALKING;
        else if (value < mOrangStep)
            return Effort.INTERVAL;
        else
            return Effort.EXERCISE;
    }
}
