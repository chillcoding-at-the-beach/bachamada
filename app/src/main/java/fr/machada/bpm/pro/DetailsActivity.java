package fr.machada.bpm.pro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Date;
import java.text.SimpleDateFormat;

import fr.machada.bpm.pro.model.BpmDbAdapter;
import fr.machada.bpm.pro.model.Effort;
import fr.machada.bpm.pro.model.How;
import fr.machada.bpm.pro.model.RegisteredFC;
import fr.machada.bpm.pro.utils.FCIndicator;
import fr.machada.bpm.pro.utils.FacebookShare;
import fr.machada.bpm.pro.utils.SomeKeys;

/**
 * Created by macha on 10/03/16.
 */
public class DetailsActivity extends AppCompatActivity {
    public static final String VIEW_NAME_FC = "details:main:fc";
    private FacebookShare mFacebookShare;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RegisteredFC fc = null;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(SomeKeys.BUNDLE_FC)) {
            fc = (RegisteredFC) bundle.get(SomeKeys.BUNDLE_FC);
        }
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        getSupportActionBar().setTitle(getString(R.string.fc_value_title));

        setContentView(R.layout.fragment_layout_details);

        TextView textDate = (TextView) findViewById(R.id.details_date);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM y,  HH:mm");
        Date resultDate = new Date(fc.getDate());
        textDate.setText(sdf.format(resultDate));

        TextView textPercent = (TextView) findViewById(R.id.details_percent);
        ImageView imgEffort = (ImageView) findViewById(R.id.details_ind_effort);
        int percent = fc.getPercent();
        textPercent.setText(String.format("%d %%", percent));
        if (percent < FCIndicator.mPercentGuru) {
            textPercent.setTextColor(ContextCompat.getColor(this, R.color.green));
            imgEffort.setImageResource(R.drawable.ic_guru);
        } else if (percent < FCIndicator.mPercentSpeed) {
            textPercent.setTextColor(ContextCompat.getColor(this, R.color.yellow));
            if (percent < FCIndicator.mPercentWalking)
                imgEffort.setImageResource(R.drawable.ic_walking);
            else
                imgEffort.setImageResource(R.drawable.ic_running);
        } else if (percent < FCIndicator.mPercentSpeedMore) {
            textPercent.setTextColor(ContextCompat.getColor(this, R.color.orange));
            imgEffort.setImageResource(R.drawable.ic_interval);
        } else {
            textPercent.setTextColor(ContextCompat.getColor(this, R.color.reed));
            imgEffort.setImageResource(R.drawable.ic_exercise);
        }

        TextView viewFC = (TextView) findViewById(R.id.details_show_fc);
        viewFC.setText(String.format("%d", fc.getValue()));
        ViewCompat.setTransitionName(viewFC, VIEW_NAME_FC);

        ImageView imE = (ImageView) findViewById(R.id.details_hiseffort);
        ImageView imH = (ImageView) findViewById(R.id.details_hishow);


        Effort ef = Effort.values()[fc.getEffort()];
        switch (ef) {
            case GURU:
                imE.setImageResource(R.drawable.ic_guru);
                break;
            case WALKING:
                imE.setImageResource(R.drawable.ic_walking);
                break;
            case INTERVAL:
                imE.setImageResource(R.drawable.ic_interval);
                break;
            case EXERCISE:
                imE.setImageResource(R.drawable.ic_exercise);
                break;
            default:
                imE.setImageResource(R.drawable.ic_guru);
        }

        How ho = How.values()[fc.getHow()];
        switch (ho) {
            case HAPPY:
                imH.setImageResource(R.drawable.ic_happy);
                break;
            case NEUTRAL:
                imH.setImageResource(R.drawable.ic_neutral);
                break;
            case SAD:
                imH.setImageResource(R.drawable.ic_sad);
                break;
            default:
                imH.setImageResource(R.drawable.ic_happy);
        }

        View fbButton = findViewById(R.id.details_fb_icon);
        final RegisteredFC finalFc = fc;
        fbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFacebookShare = new FacebookShare(DetailsActivity.this);
                mFacebookShare.openFbDialog(finalFc.getValue());
                //  EventBus.getDefault().post(new OnFBShareFCEvent(fc.getValue()));
            }
        });

        View deleteButton = findViewById(R.id.details_delete_icon);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BpmDbAdapter dbHelper = new BpmDbAdapter(DetailsActivity.this);
                dbHelper.open();
                dbHelper.deleteFC(finalFc.getId());
                dbHelper.close();
                Intent returnIntent = new Intent();
                returnIntent.putExtra(SomeKeys.DELETE_FC, true);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                this.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFacebookShare.onActivityResult(requestCode, resultCode, data);
    }

}
