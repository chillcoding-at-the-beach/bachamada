package fr.machada.bpm.pro;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.ArrayList;

import fr.machada.bpm.pro.model.BpmDbAdapter;
import fr.machada.bpm.pro.model.Effort;
import fr.machada.bpm.pro.model.How;
import fr.machada.bpm.pro.model.RegisteredBpm;
import fr.machada.bpm.pro.utils.SlidingTabLayout;
import fr.machada.bpm.pro.utils.SomeKeys;
import fr.machada.bpm.pro.view.BPMZoneFragment;
import fr.machada.bpm.pro.view.HistoryFragment;
import fr.machada.bpm.pro.view.InfoActivity;
import fr.machada.bpm.pro.view.MeasurementFragment;
import fr.machada.bpm.pro.view.ProfilActivity;
import fr.machada.bpm.pro.view.element.PulseNumberDialogFragment;
import fr.machada.bpm.pro.view.element.ShowAndRegisterBPMDialogFragment;
import fr.machada.bpm.pro.view.element.TimerNumberDialogFragment;

public class MainActivity extends ActionBarActivity implements
        MeasurementFragment.OnTimerListener,
        PulseNumberDialogFragment.NoticeDialogListener,
        ShowAndRegisterBPMDialogFragment.NoticeDialogListener,
        HistoryFragment.ItemSelectedListener,
        TimerNumberDialogFragment.NoticeDialogListener {

    private final String NOTIFICATION_ID = "notification_id";
    /**
     * The {@link ViewPager} that will display the three primary sections of the app, one at a
     * time.
     */
    ViewPager mViewPager;

    private String user;
    /**
     * The TabListener that manage Tabs
     */
    private SlidingTabLayout mSlidingTabLayout;
    /**
     * Fragments related to ViewPager
     */
    private MeasurementFragment mMeasurementFrag;
    private HistoryFragment mHistoryFrag;
    private BPMZoneFragment mZoneBPMFrag;
    /**
     * The Helper to manage BDD
     */
    private BpmDbAdapter mDbHelper;
    private ArrayList<RegisteredBpm> mListBpm;

    private int mTime;
    private int mPosition;
    //for notification
    private int notification_id = 1;
    /* These are the classes you use to start the notification */
    private NotificationCompat.Builder mNotificationBuilder;
    private NotificationManagerCompat mNotificationManager;
    private CallbackManager callbackManager;
    private ShareDialog mShareDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initFragment();
        initTabs();
        initNotification();
        initFB();

        Toast.makeText(this, R.string.message_ready_to_count, Toast.LENGTH_LONG).show();

    }

    private void initFB() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        mShareDialog = new ShareDialog(this);
        // this part is optional
        mShareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    private void initData() {
        user = getString(R.string.text_default);

        //BDD
        mDbHelper = new BpmDbAdapter(this);
        mDbHelper.open();

        mListBpm = mDbHelper.getAllBpmUser(user);
        updateBpmMinMax();
    }

    private void initFragment() {

        mMeasurementFrag = new MeasurementFragment();

        mZoneBPMFrag = new BPMZoneFragment();

        initHistoryFragment();

    }

    private void initHistoryFragment() {
        mHistoryFrag = new HistoryFragment();

        Bundle bundle = new Bundle();
        mListBpm = mDbHelper.getAllBpmUser(user);
        bundle.putSerializable(SomeKeys.BUNDLE_BPM_LIST, mListBpm);

        mHistoryFrag.setArguments(bundle);
    }

    public void initTabs() {
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new AppSectionsPagerAdapter(getSupportFragmentManager()));

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs_main);
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {

            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.red);
            }

            @Override
            public int getDividerColor(int position) {
                return getResources().getColor(R.color.gray);
            }
        });

        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mPosition = position;
                if (mMeasurementFrag != null)
                    mMeasurementFrag.stopTimer();
                invalidateOptionsMenu();
            }
        });

    }

    private void initNotification() {
        Intent open_activity_intent = new Intent(this, NotificationActivity.class);
        open_activity_intent.putExtra(NOTIFICATION_ID, notification_id);
        android.app.PendingIntent pending_intent = android.app.PendingIntent.getActivity(this, 0, open_activity_intent, android.app.PendingIntent.FLAG_CANCEL_CURRENT);

        mNotificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_heartrate)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_text))
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pending_intent);

        mNotificationManager = NotificationManagerCompat.from(this);
        mNotificationManager.notify(notification_id, mNotificationBuilder.build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Only show items in the action bar relevant to this screen
        // if the drawer is not showing. Otherwise, let the drawer
        // decide what to show in the action bar.
        switch (mPosition) {
            case 0:
                getMenuInflater().inflate(R.menu.main, menu);
                break;
            case 1:
                getMenuInflater().inflate(R.menu.zone, menu);
                break;
            default:
                getMenuInflater().inflate(R.menu.history, menu);
        }

        return true;

    }

    @Override
    public void onTimerEnd(int time) {
        Vibrator mVibrator = (Vibrator) getSystemService(Activity.VIBRATOR_SERVICE);
        if (mVibrator.hasVibrator())
            mVibrator.vibrate(1000);
        mTime = time;
        Bundle b = new Bundle();
        b.putInt(getString(R.string.value_timer), mTime);
        PulseNumberDialogFragment dialog = new PulseNumberDialogFragment();
        dialog.setArguments(b);
        dialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onDialogPositiveClick(int value) {
        Bundle b = new Bundle();
        b.putInt(getString(R.string.value_timer), mTime);
        b.putInt(getString(R.string.value_bpa), value);
        ShowAndRegisterBPMDialogFragment dialogBpm = new ShowAndRegisterBPMDialogFragment();
        dialogBpm.setArguments(b);
        dialogBpm.show(getSupportFragmentManager(), null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                if (mMeasurementFrag != null)
                    mMeasurementFrag.stopTimer();
                new TimerNumberDialogFragment().show(getSupportFragmentManager(), null);
                return true;
            case R.id.action_profile:
                intent = new Intent(this, ProfilActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_info:
                intent = new Intent(this, InfoActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateBpmMinMax() {
        int bpmMin = 0;
        int bpmMax = 0;

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        bpmMin = sharedPref.getInt(getString(R.string.value_bpm_min), bpmMin);
        bpmMax = sharedPref.getInt(getString(R.string.value_bpm_max), bpmMax);

        if (bpmMin == 0 || bpmMax == 0) {
            SharedPreferences.Editor editor = sharedPref.edit();

            if (bpmMin == 0) {
                bpmMin = mDbHelper.getBpmMin(user);
                editor.putInt(getString(R.string.value_bpm_min), bpmMin);
            }
            if (bpmMax == 0) {
                bpmMax = mDbHelper.getBpmMax(user);
                editor.putInt(getString(R.string.value_bpm_max), bpmMax);
            }
            editor.commit();
        }
    }

    @Override
    public void onDialogSaveClick(int v, int effort, int how) {

        Effort e;
        How h;

        switch (effort) {
            case R.id.guru:
                e = Effort.GURU;
                break;
            case R.id.walking:
                e = Effort.WALKING;
                break;
            case R.id.interval:
                e = Effort.INTERVAL;
                break;
            case R.id.exercise:
                e = Effort.EXERCISE;
                break;
            default:
                e = Effort.GURU;
        }
        switch (how) {
            case R.id.happy:
                h = How.HAPPY;
                break;
            case R.id.neutral:
                h = How.NEUTRAL;
                break;
            case R.id.sad:
                h = How.SAD;
                break;
            default:
                h = How.HAPPY;
        }
        RegisteredBpm bpm = new RegisteredBpm();
        bpm.setDate(System.currentTimeMillis());
        bpm.setValue(v);
        bpm.setHow(h.getInt());
        bpm.setEffort(e.getInt());
        bpm.setId((int) mDbHelper.insertBpm(user, bpm));
        //fixme for optimization
        initHistoryFragment();
        mHistoryFrag.addData(bpm);
        mHistoryFrag.refresh();
    }

    @Override
    public void onDialogShareClick(int v, int effort, int how) {
        onDialogSaveClick(v, effort, how);
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle(String.format(getString(R.string.share_message_format), v))
                .setContentDescription(
                        String.format(getString(R.string.share_message_format), v))
                .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=fr.machada.bpm"))
                .build();

        mShareDialog.show(linkContent);
    }


    @Override
    public void onDeleteBpmClick(int idi, int grp, int cip) {
        final int iid = idi;
        final int gp = grp;
        final int cp = cip;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_delete)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mDbHelper.deleteBpm(iid);
                        mHistoryFrag.removeData(gp, cp);
                        mHistoryFrag.refresh();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.create().show();
    }

    @Override
    public void onDialogSetTimer(int value) {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.value_timer), value);
        editor.commit();
        mMeasurementFrag.onTimeChanged();
    }


    @Override
    public void onDestroy() {
        if (mDbHelper != null)
            mDbHelper.close();
        super.onDestroy();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public class AppSectionsPagerAdapter extends FragmentStatePagerAdapter {


        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return mMeasurementFrag;
                case 1:
                    return mZoneBPMFrag;
                default:
                    return mHistoryFrag;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.title_check_bpm);
                case 1:
                    return getResources().getString(R.string.title_bpm_zone);
                case 2:
                    return getResources().getString(R.string.title_history);
                default:
                    return getResources().getString(R.string.text_default);
            }
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
