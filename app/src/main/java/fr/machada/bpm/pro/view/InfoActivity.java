package fr.machada.bpm.pro.view;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.text.method.LinkMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

import fr.machada.bpm.pro.R;

public class InfoActivity extends Activity {

    public static final String VIEW_NAME_HEADER_IMAGE ="info:header:image" ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_layout_info);
        ImageView headerImageView = (ImageView) findViewById(R.id.info_img);
        ViewCompat.setTransitionName(headerImageView, VIEW_NAME_HEADER_IMAGE);

        TextView link = (TextView) findViewById(R.id.link_icon);
        link.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
