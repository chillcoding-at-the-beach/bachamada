package fr.machada.bpm.pro.view;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

import fr.machada.bpm.pro.R;

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

        int bpmMinRef = 80;

        if (mSexe == R.id.sexe_male)
            mBPM = 220 - age;
        else
            mBPM = 226 - age;

        switch (age) {
            case 0:
                bpmMinRef = 190;
                break;
            case 1:
            case 2:
                bpmMinRef = 150;
                break;
            case 3:
            case 4:
            case 5:
                bpmMinRef = 140;
                break;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
                bpmMinRef = 125;
                break;
            default:
                bpmMinRef = 80;
        }

        //if there is BPM Min and Max
        int bpmMin = 0;
        int bpmMax = 0;
        bpmMin = sharedPref.getInt(getString(R.string.value_bpm_min), bpmMin);
        bpmMax = sharedPref.getInt(getString(R.string.value_bpm_max), bpmMax);
        //for BPM at rest its depend on the age
        if (bpmMin != 0 && bpmMin < bpmMinRef)
            t0.setText(String.valueOf(bpmMin));
        else
            switch (age) {
                case 0:
                    t0.setText("90-190");
                    bpmMin = 90 + (190 - 90) / 2;
                    break;
                case 1:
                case 2:
                    t0.setText("70-150");
                    bpmMin = 70 + (150 - 70) / 2;
                    break;
                case 3:
                case 4:
                case 5:
                    t0.setText("70-140");
                    bpmMin = 70 + (140 - 70) / 2;
                    break;
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                    t0.setText("65-125");
                    bpmMin = 65 + (125 - 65) / 2;
                    break;
                default:
                    t0.setText("70-80");
                    bpmMin = 75;
            }

        if (bpmMax > mBPM) {
            t1.setText(String.format("%d-%d", (bpmMin + 65 * (bpmMax - bpmMin) / 100), (bpmMin + 75 * (bpmMax - bpmMin) / 100)));
            t2.setText(String.format("%d-%d", (bpmMin + 75 * (bpmMax - bpmMin) / 100 + 1), (bpmMin + 80 * (bpmMax - bpmMin) / 100)));
            t3.setText(String.format("%d-%d", (bpmMin + 80 * (bpmMax - bpmMin) / 100 + 1), (bpmMin + 85 * (bpmMax - bpmMin) / 100)));
            t4.setText(String.format("%d-%d", (bpmMin + 85 * (bpmMax - bpmMin) / 100 + 1), (bpmMin + 90 * (bpmMax - bpmMin) / 100)));
            t5.setText(String.format("%d-%d", (bpmMin + 90 * (bpmMax - bpmMin) / 100 + 1), (bpmMin + 95 * (bpmMax - bpmMin) / 100)));
            t6.setText(String.format("%d-%d", (bpmMin + 95 * (bpmMax - bpmMin) / 100 + 1), bpmMax));
        } else {
            t1.setText(String.format("%d-%d", 65 * mBPM / 100, 75 * mBPM / 100));
            t2.setText(String.format("%d-%d", 75 * mBPM / 100 + 1, 80 * mBPM / 100));
            t3.setText(String.format("%d-%d", 80 * mBPM / 100 + 1, 85 * mBPM / 100));
            t4.setText(String.format("%d-%d", 85 * mBPM / 100 + 1, 90 * mBPM / 100));
            t5.setText(String.format("%d-%d", 90 * mBPM / 100 + 1, 95 * mBPM / 100));
            t6.setText(String.format("%d-%d", 95 * mBPM / 100 + 1, mBPM));
        }
    }

}
