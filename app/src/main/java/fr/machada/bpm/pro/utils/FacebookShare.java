package fr.machada.bpm.pro.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import fr.machada.bpm.pro.R;

/**
 * Created by macha on 15/03/16.
 */
public class FacebookShare {

    private final Activity mContext;
    private CallbackManager callbackManager;
    private ShareDialog mShareDialog;

    public FacebookShare(Activity context) {
        mContext = context;
        initFB();
    }

    private void initFB() {
        FacebookSdk.sdkInitialize(mContext);
        callbackManager = CallbackManager.Factory.create();
        mShareDialog = new ShareDialog(mContext);
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

    public void openFbDialog(int v) {
        boolean installed = appInstalledOrNot("com.facebook.katana");
        if (installed) {
            Bitmap image = getPersonalHeartRateBitmap(v);
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(image)
                    .build();
            SharePhotoContent photoContent = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build();
            mShareDialog.show(photoContent);

        } else {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(String.format(mContext.getString(R.string.share_message_format), v))
                    .setContentDescription(
                            String.format(mContext.getString(R.string.share_message_format), v))
                    .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=fr.machada.bpm"))
                    .build();
            mShareDialog.show(linkContent);
        }
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = mContext.getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public Bitmap getPersonalHeartRateBitmap(int value) {
        Resources resources = mContext.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.heart_scale);

        android.graphics.Bitmap.Config bitmapConfig =
                bitmap.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        if (value < 90)
            paint.setColor(mContext.getResources().getColor(R.color.green));
        else if (value < 120)
            paint.setColor(mContext.getResources().getColor(R.color.yellow));
        else if (value < 150)
            paint.setColor(mContext.getResources().getColor(R.color.orange));
        else
            paint.setColor(mContext.getResources().getColor(R.color.reed));
        // text size in pixels
        paint.setTextSize((int) (60 * scale));
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        // draw text to the Canvas center
        Rect bounds = new Rect();
        String gText = String.format("%d %s", value, mContext.getString(R.string.bpm_text));
        paint.getTextBounds(gText, 0, gText.length(), bounds);
        int x = (bitmap.getWidth() - bounds.width()) / 2;
        int y = (bitmap.getHeight() + bounds.height()) / 2;

        canvas.drawText(gText, x, y, paint);

        return bitmap;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
