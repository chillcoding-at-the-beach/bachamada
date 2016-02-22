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
    private TextView t0, t1, t2, t3, t4, t5, t6, t0p;
    private int mYear = 1984;
    private int mSexe = R.id.sexe_male;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_layout_zone, container, false);

        t0 = (TextView) view.findViewById(R.id.fce_resting_ref);
        t0p = (TextView) view.findViewById(R.id.fce_resting_ref_p);

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

        int bpmMinRef, hr1;

        if (mSexe == R.id.sexe_male)
            mBPM = 220 - age;
        else
            mBPM = 226 - age;
        int p1, p2;
        switch (age) {
            case 0:
                hr1 = 90;
                bpmMinRef = 190;
                break;
            case 1:
            case 2:
                hr1 = 70;
                bpmMinRef = 150;
                break;
            case 3:
            case 4:
            case 5:
                hr1 = 70;
                bpmMinRef = 140;
                break;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
                hr1 = 65;
                bpmMinRef = 125;
                break;
            default:
                hr1 = 70;
                bpmMinRef = 80;
        }
        t0.setText(String.format("%d-%d", hr1, bpmMinRef));
        p1 = hr1 * 100 / mBPM;
        p2 = bpmMinRef * 100 / mBPM;
        t0p.setText(String.format("%d-%d %%", p1, p2));
        // get BPM Min and Max in DB
        int bpmMin = 0;
        int bpmMax = 0;
        bpmMin = sharedPref.getInt(getString(R.string.value_bpm_min), bpmMin);
        bpmMax = sharedPref.getInt(getString(R.string.value_bpm_max), bpmMax);
//if bpmMin is not good show default method in % of HR Max
        if (bpmMin == 0 || bpmMin > bpmMinRef) {
            t1.setText(String.format("%d-%d", 65 * mBPM / 100, 75 * mBPM / 100));
            t2.setText(String.format("%d-%d", 75 * mBPM / 100 + 1, 80 * mBPM / 100));
            t3.setText(String.format("%d-%d", 80 * mBPM / 100 + 1, 85 * mBPM / 100));
            t4.setText(String.format("%d-%d", 85 * mBPM / 100 + 1, 90 * mBPM / 100));
            t5.setText(String.format("%d-%d", 90 * mBPM / 100 + 1, 95 * mBPM / 100));
            t6.setText(String.format("%d-%d", 95 * mBPM / 100 + 1, mBPM));
        } else {
            //method more evoluate Karvonen
            if (bpmMax < mBPM)
                bpmMax = mBPM;
            //HR Max does not evoluate a lot with training so default is OK
            t1.setText(String.format("%d-%d", (bpmMin + 65 * (bpmMax - bpmMin) / 100), (bpmMin + 75 * (bpmMax - bpmMin) / 100)));
            t2.setText(String.format("%d-%d", (bpmMin + 75 * (bpmMax - bpmMin) / 100 + 1), (bpmMin + 80 * (bpmMax - bpmMin) / 100)));
            t3.setText(String.format("%d-%d", (bpmMin + 80 * (bpmMax - bpmMin) / 100 + 1), (bpmMin + 85 * (bpmMax - bpmMin) / 100)));
            t4.setText(String.format("%d-%d", (bpmMin + 85 * (bpmMax - bpmMin) / 100 + 1), (bpmMin + 90 * (bpmMax - bpmMin) / 100)));
            t5.setText(String.format("%d-%d", (bpmMin + 90 * (bpmMax - bpmMin) / 100 + 1), (bpmMin + 95 * (bpmMax - bpmMin) / 100)));
            t6.setText(String.format("%d-%d", (bpmMin + 95 * (bpmMax - bpmMin) / 100 + 1), bpmMax));
        }
    }

}
