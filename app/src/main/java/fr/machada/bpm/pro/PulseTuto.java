package fr.machada.bpm.pro;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by macha on 26/02/16.
 */
public class PulseTuto extends AppIntro2 {

    // Please DO NOT override onCreate. Use init.
    @Override
    public void init(Bundle savedInstanceState) {

        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(AppIntroFragment.newInstance(getString(R.string.how_take_pulse), getString(R.string.how_one), R.drawable.tuto_check_pulse, android.R.color.white));
        addSlide(AppIntroFragment.newInstance(getString(R.string.how_two), getString(R.string.how_two_details), R.drawable.tuto_start_crono, android.R.color.white));
        addSlide(AppIntroFragment.newInstance(getString(R.string.how_three), getString(R.string.how_three_details), R.drawable.tuto_enter_nb, android.R.color.white));
        addSlide(AppIntroFragment.newInstance(getString(R.string.how_four), getString(R.string.how_four_details), R.drawable.tuto_see_value, android.R.color.white));



        // Hide Skip/Done button.
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permisssion in Manifest.
        setVibrate(true);
        setVibrateIntensity(30);
    }


    @Override
    public void onDonePressed() {
        loadMainActivity();
    }

    @Override
    public void onSlideChanged() {
        // Do something when the slide changes.
    }

    @Override
    public void onNextPressed() {
        // Do something when users tap on Next button.
    }

    private void loadMainActivity() {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.first_launch), false);
        editor.commit();
        finish();
    }

}