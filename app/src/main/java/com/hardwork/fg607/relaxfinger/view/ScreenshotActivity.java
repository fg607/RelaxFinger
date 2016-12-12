package com.hardwork.fg607.relaxfinger.view;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.hardwork.fg607.relaxfinger.service.FloatService;
import com.hardwork.fg607.relaxfinger.model.Config;
import com.hardwork.fg607.relaxfinger.utils.ScreenshotCallback;
import com.hardwork.fg607.relaxfinger.utils.Screenshotter;

public class ScreenshotActivity extends Activity {

    private static final int REQUEST_MEDIA_PROJECTION = 1;

    private static final int REQUEST_EXTERNAL_STORAGE = 2;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private AudioManager mAudioManager=null;
    private MediaPlayer mShootMP;
    private int mOriginVolume=0;
    private boolean mIsExist =false;
    private Handler mHandler = new Handler();

    private Messenger mMessenger = null;
    private boolean mBound = false;


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMessenger = new Messenger(service);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMessenger = null;
            mBound = false;
        }
    };

    public void bindFloatService(){

        if(!mBound){

            bindService(new Intent(this,FloatService.class),mServiceConnection, Context.BIND_AUTO_CREATE);
        }

    }

    public void unbindFloatService(){

        if(mBound){

            unbindService(mServiceConnection);

            mBound = false;
            mMessenger = null;
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        verifyStoragePermissions(ScreenshotActivity.this);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){

            bindFloatService();
            takeScreenshot();

        }else {

            Toast.makeText(this,"当前系统不支持快捷截屏!",Toast.LENGTH_SHORT).show();

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    finish();
                }
            },500);
        }


    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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

                                        if(mMessenger != null){

                                            Message message = Message.obtain();
                                            message.what = Config.SCREEN_SHOT;
                                            Bundle bundle = new Bundle();
                                            bundle.putParcelable("screenShot",bitmap);
                                            message.setData(bundle);

                                            try {

                                                mMessenger.send(message);

                                                Toast.makeText(ScreenshotActivity.this, "截图成功！", Toast.LENGTH_SHORT).show();

                                            } catch (RemoteException e) {
                                                e.printStackTrace();
                                                Toast.makeText(ScreenshotActivity.this, "截图失败！", Toast.LENGTH_SHORT).show();
                                            }


                                        }else {

                                            Toast.makeText(ScreenshotActivity.this, "截图失败！", Toast.LENGTH_SHORT).show();
                                        }


                                        mIsExist = true;
                                    }

                                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mOriginVolume, 0);

                                    if (mShootMP != null){

                                        mShootMP.release();
                                    }

                                    unbindFloatService();

                                    finish();

                                }
                            });

                }

            } else {
                Toast.makeText(this, "You denied the permission.", Toast.LENGTH_SHORT).show();
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

             mShootMP = MediaPlayer.create(this, Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));

            if (mShootMP != null){

                mShootMP.start();
            }

        }
    }

    public  void sendMsg(int what,String name,boolean action) {
        Intent intent = new Intent();
        intent.putExtra("what",what);
        intent.putExtra(name, action);
        intent.setClass(this, FloatService.class);
        startService(intent);
    }
}
