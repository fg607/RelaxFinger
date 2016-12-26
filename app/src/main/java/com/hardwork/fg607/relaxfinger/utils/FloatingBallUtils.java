package com.hardwork.fg607.relaxfinger.utils;

/**
 * Created by fg607 on 15-8-20.
 *
 */

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.hardwork.fg607.relaxfinger.MyApplication;
import com.hardwork.fg607.relaxfinger.R;
import com.hardwork.fg607.relaxfinger.model.Config;
import com.hardwork.fg607.relaxfinger.model.ToolInfo;
import com.hardwork.fg607.relaxfinger.receiver.ScreenOffAdminReceiver;
import com.hardwork.fg607.relaxfinger.service.FloatService;
import com.hardwork.fg607.relaxfinger.service.NavAccessibilityService;
import com.hardwork.fg607.relaxfinger.view.CombinationImageView;
import com.hardwork.fg607.relaxfinger.view.ScreenshotActivity;

import net.grandcentrix.tray.TrayAppPreferences;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

import static android.content.ComponentCallbacks2.TRIM_MEMORY_COMPLETE;


public class FloatingBallUtils {

    public static OutputStream os;
    public static Bitmap bitmap;
    public static Context context = MyApplication.getApplication();

    public static final TrayAppPreferences multiProcessPreferences = new TrayAppPreferences(context);
    public static SharedPreferences sp = getSharedPreferences();
    public static AudioManager mAudioManager=null;
    public static WindowManager sWindowManager = null;
    public static  WifiManager mWifiManager = null;
    public static ConnectivityManager mConnectivityManager = null;
    public static NotificationManager notificationManager =
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    public static PowerManager mPowerManager = null;
    public static PowerManager.WakeLock mWakeLock = null;
    public static Method msetDataMethod = null;
    public static Method mgetDataMethod = null;
    public static  Camera mCamera = null;
    public static AudioManager.OnAudioFocusChangeListener listener= null;
    public static  boolean mIsFlashOpened = false;

    public static Object wmgInstnace = null;
    public static Method trimMemory = null;

    /**
     * 获取MainActivity的SharedPreferences共享数据
     * @return
     */
    public static SharedPreferences getSharedPreferences() {

        return PreferenceManager.getDefaultSharedPreferences(context);

    }
    public static TrayAppPreferences getMultiProcessPreferences(){

        return multiProcessPreferences;
    }

    /**
     * 将状态数据保存在sharepreferences
     * @param name
     * @param state
     */
    public static void saveState(String name ,boolean state) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(name, state);
        editor.commit();

    }

    public static void saveState(String name ,String value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(name, value);
        editor.commit();
    }

    public static void saveState(String name ,int value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(name, value);
        editor.commit();
    }

    public static void saveState(String name,Set<String> value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(name, value);
        editor.commit();
    }


    public static void keyBack(AccessibilityService service){

        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }


    public static void keyHome(){

        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 如果是服务里调用，必须加入new task标识
        i.addCategory(Intent.CATEGORY_HOME);
        context.startActivity(i);

    }

    /**
     * 使用原生home键返回桌面，存在5秒延迟问题
     * @param service
     */
    public static void keyHome(AccessibilityService service){

        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
    }

    public static void lockScreen(){

        DevicePolicyManager policyManager = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminReceiver = new ComponentName(context,
                ScreenOffAdminReceiver.class);
        boolean admin = policyManager.isAdminActive(adminReceiver);
        if (admin) {
            policyManager.lockNow();
        }
    }


    public static void openRecnetTask(AccessibilityService service){

        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
    }

    public static void openNotificationBar(AccessibilityService service){
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);

    }

    /**
     * 长按电源键
     */

    public static void openPowerDialog(AccessibilityService service){

        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_POWER_DIALOG);
    }

    /**
     * 快速设置
     * @param service
     */
    public static void openQuickSetting(AccessibilityService service){

        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS);
    }

    public static void screenShot(){

        context.startActivity(new Intent().setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).setClass(
                context, ScreenshotActivity.class));
    }

    /**
     * 音量上键
     */
    public static void volumeUp() {

        if(mAudioManager ==null){
            mAudioManager= (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        }

        if(mAudioManager.isMusicActive()){

            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
        }else {

            mAudioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI|AudioManager.FLAG_PLAY_SOUND);

        }

    }

    /**
     * 音量下键
     */
    public static void volumeDown() {

        if(mAudioManager ==null){
            mAudioManager= (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        }

        if(mAudioManager.isMusicActive()){

            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
        }else {

            mAudioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI|AudioManager.FLAG_PLAY_SOUND);

        }

    }


    public static void previousApp(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                String prePackageName = null;

                try {

                    prePackageName = AppUtils.getPreviousApp();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(prePackageName!=null){

                    try {
                        AppUtils.startApplication(prePackageName);
                    }catch (ActivityNotFoundException e){

                        e.printStackTrace();
                    }


                }else {

                    MyApplication.getMainThreadHandler().post(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(MyApplication.getApplication(),"没有更早的应用了！",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();



    }


    public static boolean isFileExist(String filePath){

        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+filePath;

        File file = new File(path);

        return file.exists();

    }

    public static String saveBitmap(Bitmap bitmap,String fileName) throws IOException {

        return  saveBitmap(bitmap,"/RelaxFinger",fileName);
    }

    /**
     * 保存图标到sd卡
     * @param bitmap
     * @param fileName
     * @throws IOException
     */
    public static String saveBitmap(Bitmap bitmap,String folder,String fileName) throws IOException {


        if(bitmap == null){

            return null;
        }
        String rootdir = Environment.getExternalStorageDirectory().getAbsolutePath()+
                folder;

        File dir = new File(rootdir);

        if (!dir.exists()) {
            dir.mkdir();
        }


        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

        InputStream isBm = new ByteArrayInputStream(baos.toByteArray());

        File file = new File(rootdir+"/"+fileName);

        if(!file.exists()) {
            file.createNewFile();
        }

        byte[] buffer = new byte[1024];

        OutputStream os = new FileOutputStream(file);

        int count = 0;
        while ((count = isBm.read(buffer) ) != -1) {
            os.write(buffer,0,count);
        }

        os.flush();
        os.close();
        isBm.close();

        return file.getAbsolutePath();
    }

    /**
     * 通知媒体库更新文件
     * @param context
     * @param filePath 文件全路径
     *
     * */
    public static void scanFile(Context context, String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        context.sendBroadcast(scanIntent);
    }

    /**
     * 通知媒体库更新文件夹
     * @param context
     * @param filePath 文件夹
     *
     * */
    public  static void scanFolder(Context context, String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        context.sendBroadcast(scanIntent);
    }



    /**
     * 缩放图标
     * @param filename
     * @param size
     * @return
     */
    public static Bitmap scaleBitmap(String filename,float size) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(filename, options);

        int be = (int)(options.outHeight / (float)size);

        if (be <= 0) {
            be = 1;
        }

        BitmapFactory.Options options1 = new BitmapFactory.Options();
        options1.inSampleSize = be;
        options1.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options1.inPurgeable = true;
        options1.inInputShareable = true;
        options1.inJustDecodeBounds = false;
        Bitmap outputbitmap = BitmapFactory.decodeFile(filename, options1);

        return outputbitmap;
    }

    /**
     * 缩放图标
     * @param bitmap
     * @param size
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap bitmap,float size) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        InputStream isBm = null;
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

        isBm = new ByteArrayInputStream(baos.toByteArray());

        BitmapFactory.decodeStream(isBm, null, options);


        if(isBm != null) {
        try {
            isBm.close();
        } catch (IOException e) {
            e.printStackTrace();
           }
        }

        int be = (int)(options.outHeight / (float)size);

        if (be <= 0) {
            be = 1;
        }

        BitmapFactory.Options options1 = new BitmapFactory.Options();
        options1.inSampleSize = be;
        options1.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options1.inPurgeable = true;
        options1.inInputShareable = true;
        options1.inJustDecodeBounds = false;

        isBm = new ByteArrayInputStream(baos.toByteArray());

        Bitmap outputbitmap = BitmapFactory.decodeStream(isBm, null, options1);

        if(isBm != null) {
            try {
                isBm.close();
                isBm = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return outputbitmap;
    }


    /**
     * 显示对话框
     * @param context
     * @param title
     * @param view
     * @return
     */
    public static AlertDialog showDlg(Context context,String title,View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(title);
        builder.setView(view);
        AlertDialog Dialog =  builder.show();

        return Dialog;
    }

    public static int getScreenWidth(){

        return MyApplication.getApplication().getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(){

        return MyApplication.getApplication().getResources().getDisplayMetrics().heightPixels;
    }


    public static int getStatusBarHeight(Context context){
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    public static ArrayList<ToolInfo> getToolInfos() {

        ArrayList<ToolInfo> toolList =  new ArrayList<>();

        ToolInfo wifi = new ToolInfo(context.getResources().getDrawable(R.drawable.switch_1_wifi),"WIFI");
        ToolInfo data = new ToolInfo(context.getResources().getDrawable(R.drawable.switch_2_data),"移动数据");
        ToolInfo bluetooth = new ToolInfo(context.getResources().getDrawable(R.drawable.switch_10_bluetooth),"蓝牙");
        ToolInfo flash = new ToolInfo(context.getResources().getDrawable(R.drawable.switch_9_flashlight),"手电筒");
        ToolInfo vibration = new ToolInfo(context.getResources().getDrawable(R.drawable.switch_6_vibration),"震动/声音");
        ToolInfo mute = new ToolInfo(context.getResources().getDrawable(R.drawable.switch_5_mute),"静音/声音");
        ToolInfo rotation = new ToolInfo(context.getResources().getDrawable(R.drawable.switch_8_rotation),"屏幕旋转");
        ToolInfo lockScreen = new ToolInfo(context.getResources().getDrawable(R.drawable.switch_lockscreen),"休眠");
        ToolInfo hideBall = new ToolInfo(context.getResources().getDrawable(R.drawable.switch_hide),"隐藏悬浮球");
        ToolInfo powerPanel = new ToolInfo(context.getResources().getDrawable(R.drawable.switch_powerpanel),"电源面板");
        ToolInfo settingPanel = new ToolInfo(context.getResources().getDrawable(R.drawable.switch_setting),"快速设置");
        ToolInfo volumeUp = new ToolInfo(context.getResources().getDrawable(R.drawable.switch_13_volume_up),"音量加");
        ToolInfo volumeDown = new ToolInfo(context.getResources().getDrawable(R.drawable.switch_14_volume_down),"音量减");
        ToolInfo music = new ToolInfo(context.getResources().getDrawable(R.drawable.switch_15_music),"音乐开关");
        ToolInfo screenShot = new ToolInfo(context.getResources().getDrawable(R.drawable.screen_shot),"屏幕截图");
        ToolInfo screenOn = new ToolInfo(context.getResources().getDrawable(R.drawable.screen_on),"屏幕常亮");

        if(Build.VERSION.SDK_INT<21){

            toolList.add(data);

        }else {

            toolList.add(powerPanel);
            toolList.add(settingPanel);
            toolList.add(screenShot);
        }

        toolList.add(rotation);
        toolList.add(bluetooth);
        toolList.add(wifi);
        toolList.add(screenOn);
        toolList.add(flash);
        toolList.add(vibration);
        toolList.add(mute);
        toolList.add(lockScreen);
        toolList.add(hideBall);
        toolList.add(volumeUp);
        toolList.add(volumeDown);
        toolList.add(music);

        return toolList;
    }


    public static Drawable getSwitcherIcon(String name){

        Drawable icon = null;

        switch (name){

            case "WIFI":
                icon = context.getResources().getDrawable(R.drawable.switch_1_wifi);
                break;
            case "移动数据":
                icon = context.getResources().getDrawable(R.drawable.switch_2_data);
                break;
            case "蓝牙":
                icon = context.getResources().getDrawable(R.drawable.switch_10_bluetooth);
                break;
            case "手电筒":
                icon = context.getResources().getDrawable(R.drawable.switch_9_flashlight);
                break;
            case "震动/声音":
                icon = context.getResources().getDrawable(R.drawable.switch_6_vibration);
                break;
            case "静音/声音":
                icon = context.getResources().getDrawable(R.drawable.switch_5_mute);
                break;
            case "屏幕旋转":
                icon = context.getResources().getDrawable(R.drawable.switch_8_rotation);
                break;
            case "音乐开关":
                icon = context.getResources().getDrawable(R.drawable.switch_15_music);
                break;
            case "屏幕截图":
                icon = context.getResources().getDrawable(R.drawable.screen_shot);
                break;
            case "屏幕常亮":
                icon = context.getResources().getDrawable(R.drawable.screen_on);
                break;
            case "休眠":
                icon = context.getResources().getDrawable(R.drawable.switch_lockscreen);
                break;
            case "隐藏悬浮球":
                icon = context.getResources().getDrawable(R.drawable.switch_hide);
                break;
            case "音量加":
                icon = context.getResources().getDrawable(R.drawable.switch_13_volume_up);
                break;
            case "音量减":
                icon = context.getResources().getDrawable(R.drawable.switch_14_volume_down);
                break;
            case "电源面板":
                icon = context.getResources().getDrawable(R.drawable.switch_powerpanel);
                break;
            case "快速设置":
                icon = context.getResources().getDrawable(R.drawable.switch_setting);
                break;
            default:
                break;

        }

        return icon;
    }

    public static void switchButton(String name){

        switch (name){

            case "WIFI":
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        switchWifi();
                    }
                }).start();

                break;
            case "移动数据":
                switchMoblieData();
                break;
            case "蓝牙":
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        switchBluetooth();
                    }
                }).start();

                break;
            case "手电筒":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        switchFlashlight();
                    }
                }).start();

                break;
            case "震动/声音":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        vibrationMode();
                    }
                }).start();

                break;
            case "静音/声音":
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        muteMode();
                    }
                }).start();

                break;
            case "屏幕旋转":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        switchRotation();
                    }
                }).start();

                break;
            case "音乐开关":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        switchMusic();
                    }
                }).start();

                break;
            case "屏幕截图":
                if(Build.VERSION.SDK_INT > 20){

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            context.startActivity(new Intent().setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).setClass(
                                    context, ScreenshotActivity.class));
                        }
                    }).start();


                }else {

                    Toast.makeText(context,"截图功能适用于5.0以上系统！",Toast.LENGTH_SHORT).show();
                }
                break;
            case "屏幕常亮":
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        switchKeepScreenOn();
                    }
                }).start();

                break;

            case "休眠":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        lockScreen();
                    }
                }).start();
                break;
            case "隐藏悬浮球":
                hideToNotifybar();
                break;
            case "音量加":
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        volumeUp();
                    }
                }).start();
                break;
            case "音量减":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        volumeDown();
                    }
                }).start();
                break;
            case "电源面板":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(NavAccessibilityService.instance!=null){

                            openPowerDialog(NavAccessibilityService.instance);
                        }

                    }
                }).start();
                break;
            case "快速设置":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(NavAccessibilityService.instance!=null){

                            openQuickSetting(NavAccessibilityService.instance);
                        }

                    }
                }).start();
                break;

            default:
                break;
        }

    }

    public static void hideToNotifybar() {
        Intent intent = new Intent();
        intent.putExtra("what", Config.HIDE_TO_NOTIFYBAR);
        intent.setClass(context, FloatService.class);
        context.startService(intent);
    }

    private static void switchMusic() {


        if(mAudioManager ==null){
            mAudioManager= (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        }

        if(mAudioManager.isMusicActive()){

            if(listener == null){

                listener = new AudioManager.OnAudioFocusChangeListener() {
                    @Override
                    public void onAudioFocusChange(int focusChange) {

                    }
                };
            }

            mAudioManager.requestAudioFocus(listener,AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

        }else {

            if(listener != null){

                mAudioManager.abandonAudioFocus(listener);

                listener = null;
            }

        }


    }

    private static void switchRotation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if(!checkPermissionWrite()){

                return;
            }
        }

        //if(Build.VERSION.SDK_INT < 23) {
            ContentResolver resolver = context.getContentResolver();

            int gravity = -1;

            try {
                gravity = Settings.System.getInt(context.getContentResolver(),
                        Settings.System.ACCELEROMETER_ROTATION);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            if (gravity == 0) {

                //打开
                Settings.System.putInt(resolver, Settings.System.ACCELEROMETER_ROTATION, 1);
                MyApplication.getMainThreadHandler().post(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(context,"屏幕旋转已启用",Toast.LENGTH_SHORT).show();
                    }
                });


            } else if (gravity == 1) {

                //关闭
                Settings.System.putInt(resolver, Settings.System.ACCELEROMETER_ROTATION, 0);
               MyApplication.getMainThreadHandler().post(new Runnable() {
                   @Override
                   public void run() {
                       Toast.makeText(context,"屏幕旋转已关闭",Toast.LENGTH_SHORT).show();
                   }
               });

            }
        }/*else {

            MyApplication.getMainThreadHandler().post(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(context,"6.0不支持该功能",Toast.LENGTH_SHORT).show();
                }
            });


        }

    }*/

    private static void muteMode() {

        if(mAudioManager ==null){
            mAudioManager= (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);

            return;
        }

        if(mAudioManager.getRingerMode()!=AudioManager.RINGER_MODE_SILENT){

            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            MyApplication.getMainThreadHandler().post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context,"静音模式已启用",Toast.LENGTH_SHORT).show();
                }
            });

        }else {

            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            MyApplication.getMainThreadHandler().post(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(context,"普通模式已启用",Toast.LENGTH_SHORT).show();
                }
            });

        }




    }

    private static void vibrationMode() {

        if(mAudioManager ==null){
            mAudioManager= (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);

            return;
        }

        if(mAudioManager.getRingerMode()==AudioManager.RINGER_MODE_NORMAL){

            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            MyApplication.getMainThreadHandler().post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context,"震动模式已启用",Toast.LENGTH_SHORT).show();
                }
            });

        }else {

            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            MyApplication.getMainThreadHandler().post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context,"普通模式已启用",Toast.LENGTH_SHORT).show();
                }
            });

        }



    }

    private static void switchFlashlight() {


        if(Build.VERSION.SDK_INT < 23) {
            if (mCamera == null) {

                mCamera = Camera.open();
            }

            Camera.Parameters parameter = mCamera.getParameters();


            if (!parameter.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {

                mCamera.startPreview();
                parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(parameter);

                MyApplication.getMainThreadHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,"手电筒已打开",Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                parameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameter);
                mCamera.release();
                mCamera = null;

                MyApplication.getMainThreadHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,"手电筒已关闭",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }else {

            CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);

            String[] list={};

            if(!mIsFlashOpened){

                try {
                    list = manager.getCameraIdList();
                    manager.setTorchMode(list[0], true);
                    mIsFlashOpened = true;
                    MyApplication.getMainThreadHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context,"手电筒已打开",Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }else {


                try {
                    list = manager.getCameraIdList();
                    manager.setTorchMode(list[0], false);
                    mIsFlashOpened = false;
                    MyApplication.getMainThreadHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context,"手电筒已关闭",Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }

    private static void switchBluetooth() {

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        if (adapter.isEnabled()) {

            adapter.disable();

            MyApplication.getMainThreadHandler().post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "蓝牙已关闭", Toast.LENGTH_SHORT).show();
                }
            });

        } else {

            adapter.enable();
            MyApplication.getMainThreadHandler().post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "蓝牙已开启", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private static void switchMoblieData() {


        boolean isMobileDataEnabled = false;
        Class[] getArgArray =null;
        Object[] getArgInvoke =null;

        if(mConnectivityManager==null){

            mConnectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        try {
            if (mgetDataMethod == null) {

                mgetDataMethod = mConnectivityManager.getClass().getDeclaredMethod("getMobileDataEnabled",getArgArray);
            }

            if (mgetDataMethod != null) {

                //判断当前手机是否在使用MobileData(移动数据)
                if ((boolean)mgetDataMethod.invoke(mConnectivityManager, getArgInvoke)) {
                    isMobileDataEnabled = true;
                } else {
                    isMobileDataEnabled = false;
                }

            }else {

                return;
            }


            if(msetDataMethod == null){
                msetDataMethod=mConnectivityManager.getClass().getDeclaredMethod("setMobileDataEnabled", boolean.class);
            }


            if(msetDataMethod!= null){

                msetDataMethod.setAccessible(true);
                msetDataMethod.invoke(mConnectivityManager, !isMobileDataEnabled);

                if(isMobileDataEnabled){

                    MyApplication.getMainThreadHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context,"数据已关闭",Toast.LENGTH_SHORT).show();
                        }
                    });


                }else {

                    MyApplication.getMainThreadHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context,"数据已开启",Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }


        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    private static void switchWifi() {

        if (mWifiManager == null) {
            mWifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
        }

        boolean isWifiEnabled = mWifiManager.isWifiEnabled();

        mWifiManager.setWifiEnabled(!mWifiManager.isWifiEnabled());

        if (isWifiEnabled) {

            MyApplication.getMainThreadHandler().post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "WIFI已关闭", Toast.LENGTH_SHORT).show();
                }
            });

        } else {

            MyApplication.getMainThreadHandler().post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "WIFI已开启", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    public static boolean checkPermissionGranted(Activity activity, String permission) {


        if(Build.VERSION.SDK_INT>22){

            int grant = activity.checkSelfPermission(permission);

            if (grant != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                activity.requestPermissions(new String[]{permission}, 123);

                return false;
            }
        }

        return true;

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean checkPermissionWrite(){

        if(!Settings.System.canWrite(context)){

            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

            MyApplication.getMainThreadHandler().post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context,"屏幕旋转需要修改系统设置权限！",Toast.LENGTH_SHORT).show();
                }
            });

            return false;

        }else {

            return true;
        }
    }

    public static void switchKeepScreenOn(){

        if(mWakeLock == null){

            if(mPowerManager == null){

                mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            }

            mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "RelaxFinger");

        }

        if(!mWakeLock.isHeld()){

            mWakeLock.acquire();

            MyApplication.getMainThreadHandler().post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context,"屏幕常亮已开启！",Toast.LENGTH_SHORT).show();
                }
            });


        }else {

            mWakeLock.release();
            MyApplication.getMainThreadHandler().post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context,"屏幕常亮已关闭！",Toast.LENGTH_SHORT).show();
                }
            });


        }

    }

    public static Bitmap createCombinationImage(ArrayList<Bitmap> bitmaps){

        Bitmap bitmap = null;

        CombinationImageView combinationImageView = new CombinationImageView(context);

        for (Bitmap bit:bitmaps) {

            combinationImageView.addImageView(bit);
        }

        bitmap = combinationImageView.getCombinationImage(100,100);

        return bitmap;
    }



    /**
     * 将状态数据保存在sharepreferences
     *
     * @param name
     * @param number
     */
    public static final void saveStates(String name, int number) {

        multiProcessPreferences.put(name, number);

    }

    public static final void saveStates(String name, boolean isChecked) {

        multiProcessPreferences.put(name, isChecked);
    }


    /**
     * 清空缓存
     */
    public static void clearCache() {
        try {
            if (wmgInstnace == null) {
                Class wmgClass = Class.forName("android.view.WindowManagerGlobal");
                wmgInstnace = wmgClass.getMethod("getInstance").invoke(null, (Object[]) null);
                trimMemory = wmgClass.getMethod("trimMemory", int.class);
            }

            trimMemory.invoke(wmgInstnace, TRIM_MEMORY_COMPLETE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static WindowManager getWindowManager(){

        if(sWindowManager == null){

            sWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        }

        return sWindowManager;
    }




}

