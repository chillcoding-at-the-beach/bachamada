package fr.machada.bpm.pro.view;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import fr.machada.bpm.pro.PulseTuto;
import fr.machada.bpm.pro.R;

public class InfoActivity extends Activity {

    public static final String VIEW_NAME_HEADER_IMAGE = "info:header:image";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    static final Uri APP_URI = Uri.parse("android-app://fr.machada.bpm/https/www.chillcoding.com/bachamada/");
    static final Uri WEB_URL = Uri.parse("https://www.chillcoding.com/bachamada/");
    static final String APP_SCREEN_TITLE = "Info Screen on Healthy Heart Rate";
    private GoogleApiClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_layout_info);
        TextView headerImageView = (TextView) findViewById(R.id.info_title);
        ViewCompat.setTransitionName(headerImageView, VIEW_NAME_HEADER_IMAGE);

        TextView link = (TextView) findViewById(R.id.link_icon);
        link.setMovementMethod(LinkMovementMethod.getInstance());
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    public void showTuto(View view) {
        Intent intent = new Intent(this, PulseTuto.class);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                APP_SCREEN_TITLE,
                WEB_URL,
                APP_URI
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                APP_SCREEN_TITLE,
                WEB_URL,
               APP_URI
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
