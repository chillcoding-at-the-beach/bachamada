package com.heartrate.view;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.heartrate.R;

import java.util.Calendar;

public class BPMZoneFragment extends Fragment {


    private int mBPM;
    private TextView t0, t1, t2, t3, t4, t5, t6;
    private int mYear = 1984;
    private int mSexe = R.id.sexe_male;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_layout_zone, container, false);

        t0 = (TextView) view.findViewById(R.id.fce_resting);

        t1 = (TextView) view.findViewById(R.id.fce_footing);

        t2 = (TextView) view.findViewById(R.id.fce_marathon);

        t3 = (TextView) view.findViewById(R.id.fce_semi);

        t4 = (TextView) view.findViewById(R.id.fce_allure);

        t5 = (TextView) view.findViewById(R.id.fce_vmal);

        t6 = (TextView) view.findViewById(R.id.fce_vmac);

        updateZone();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateZone();
    }


    public void updateZone() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        mSexe = sharedPref.getInt(getString(R.string.value_sexe), mSexe);
        mYear = sharedPref.getInt(getString(R.string.value_year), mYear);

        Calendar calendar = Calendar.getInstance();
        int curYear = calendar.get(Calendar.YEAR);
        int age = curYear - mYear;

        if (mSexe == R.id.sexe_male)
            mBPM = 220 - age;
        else
            mBPM = 226 - age;

        //for BPM at rest its depend on the age
        switch (age) {
            case 0:
                t0.setText("90-190");
                break;
            case 1:
            case 2:
                t0.setText("70-150");
                break;
            case 3:
            case 4:
            case 5:
                t0.setText("70-140");
                break;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
                t0.setText("65-125");
                break;
            default:
                t0.setText("70-80");
        }
        //if there is BPM Min and Max

        int bpmMin = 0;
        int bpmMax = 0;
        int diff;
        bpmMin = sharedPref.getInt(getString(R.string.value_bpm_min), bpmMin);
        bpmMax = sharedPref.getInt(getString(R.string.value_bpm_max), bpmMax);
        diff = bpmMax - bpmMin;

        if (bpmMin != 0 && bpmMax != 0 && diff > 50) {
            t1.setText("" + (bpmMin + 65 * (bpmMax - bpmMin) / 100) + "-" + (bpmMin + 75 * (bpmMax - bpmMin) / 100));
            t2.setText("" + (bpmMin + 75 * (bpmMax - bpmMin) / 100) + "-" + (bpmMin + 80 * (bpmMax - bpmMin) / 100));
            t3.setText("" + (bpmMin + 80 * (bpmMax - bpmMin) / 100) + "-" + (bpmMin + 85 * (bpmMax - bpmMin) / 100));
            t4.setText("" + (bpmMin + 85 * (bpmMax - bpmMin) / 100) + "-" + (bpmMin + 90 * (bpmMax - bpmMin) / 100));
            t5.setText("" + (bpmMin + 90 * (bpmMax - bpmMin) / 100) + "-" + (bpmMin + 95 * (bpmMax - bpmMin) / 100));
            t6.setText("" + (bpmMin + 95 * (bpmMax - bpmMin) / 100) + "-" + bpmMax);
        } else {
            t1.setText("" + 65 * mBPM / 100 + "-" + 75 * mBPM / 100);
            t2.setText("" + 75 * mBPM / 100 + "-" + 80 * mBPM / 100);
            t3.setText("" + 80 * mBPM / 100 + "-" + 85 * mBPM / 100);
            t4.setText("" + 85 * mBPM / 100 + "-" + 90 * mBPM / 100);
            t5.setText("" + 90 * mBPM / 100 + "-" + 95 * mBPM / 100);
            t6.setText("" + 95 * mBPM / 100 + "-" + mBPM);
        }
    }

}
