package com.hardwork.fg607.relaxfinger.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.hardwork.fg607.relaxfinger.service.FloatingBallService;
import com.hardwork.fg607.relaxfinger.utils.Config;
import com.hardwork.fg607.relaxfinger.utils.DensityUtil;
import com.hardwork.fg607.relaxfinger.utils.FloatingBallUtils;
import com.hardwork.fg607.relaxfinger.utils.ScreenshotCallback;
import com.hardwork.fg607.relaxfinger.utils.Screenshotter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.RunnableFuture;

public class ScreenshotActivity extends Activity {

    private static final int REQUEST_MEDIA_PROJECTION = 1;

    private static final int REQUEST_EXTERNAL_STORAGE = 2;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private AudioManager mAudioManager=null;
    private int mOriginVolume=0;
    private boolean mIsExist =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        verifyStoragePermissions(ScreenshotActivity.this);

        takeScreenshot();

    }

    public void takeScreenshot() {
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager)
                getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        sendMsg(Config.HIDE_BALL, "hide", true);

        startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent(),
                REQUEST_MEDIA_PROJECTION);

        shootSound();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode == RESULT_OK) {
                
                    Screenshotter.getInstance()
                            .takeScreenshot(this, resultCode, data, new ScreenshotCallback() {
                                @Override
                                public void onScreenshot(Bitmap bitmap) {

                                    if(!mIsExist){

                                        sendMsg(Config.HIDE_BALL, "hide", false);
                                        saveScreenshot(bitmap);

                                        mIsExist = true;
                                    }

                                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mOriginVolume, 0);
                                    finish();
                                }
                            });

                }

            } else {
                Toast.makeText(this, "You denied the permission.", Toast.LENGTH_SHORT).show();
            }

    }

    private void saveScreenshot(Bitmap bitmap) {


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");

        Date date = new Date();

        String strDate = dateFormat.format(date);

        try {
            FloatingBallUtils.saveBitmap(bitmap,strDate+".png");

            Toast.makeText(this, "截图成功！", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();

            Toast.makeText(this,"截图失败！",Toast.LENGTH_SHORT).show();
        }

    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public void onBackPressed() {

        moveTaskToBack(false);

    }

    /**
     *   播放截图声音
     */
    public void shootSound()
    {

        if(mAudioManager ==null){
            mAudioManager= (AudioManager)getSystemService(Context.AUDIO_SERVICE);
            mOriginVolume = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC);
        }



        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int)(maxVolume/3), 0);

        if (mOriginVolume != 0)
        {

             MediaPlayer   shootMP = MediaPlayer.create(this, Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
            if (shootMP != null){

                shootMP.start();
            }

        }
    }

    public  void sendMsg(int what,String name,boolean action) {
        Intent intent = new Intent();
        intent.putExtra("what",what);
        intent.putExtra(name, action);
        intent.setClass(this, FloatingBallService.class);
        startService(intent);
    }
}
