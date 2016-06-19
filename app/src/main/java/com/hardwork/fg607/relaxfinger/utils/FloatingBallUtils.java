package com.hardwork.fg607.relaxfinger.utils;

/**
 * Created by fg607 on 15-8-20.
 *
 */

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.hardwork.fg607.relaxfinger.MyApplication;
import com.hardwork.fg607.relaxfinger.R;
import com.hardwork.fg607.relaxfinger.SettingActivity;
import com.hardwork.fg607.relaxfinger.model.AppInfo;
import com.hardwork.fg607.relaxfinger.model.ToolInfo;
import com.hardwork.fg607.relaxfinger.receiver.ScreenOffAdminReceiver;
import com.hardwork.fg607.relaxfinger.service.FloatingBallService;
import com.hardwork.fg607.relaxfinger.view.BlankActivity;
import com.hardwork.fg607.relaxfinger.view.ScreenshotActivity;

import net.grandcentrix.tray.TrayAppPreferences;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class FloatingBallUtils {

    public static OutputStream os;
    public static Bitmap bitmap;
    public static Context context = MyApplication.getApplication();

    public static final TrayAppPreferences multiProcessPreferences = new TrayAppPreferences(context);
    public static SharedPreferences sp = getSharedPreferences();
    public static AudioManager mAudioManager=null;
    public static ActivityManager mActivitymanager = null;
    public static  WifiManager mWifiManager = null;
    public static TelephonyManager mTelephonyManager = null;
    public static ConnectivityManager mConnectivityManager = null;
    public static PowerManager mPowerManager = null;
    public static PowerManager.WakeLock mWakeLock = null;
   // public static boolean mIsKeepScreenOn = false;
    public static Method mMethod = null;
    public static  Camera mCamera = null;
    public static boolean iRotationOpen = false;
    public static AudioManager.OnAudioFocusChangeListener listener= null;
    public static  boolean mIsFlashOpened = false;
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

    /**
     * 以超级用户权限运行adb命令
     * @param cmd
     */
    public static void runCmd(String cmd) {

        try {
            if(os == null) {
                //os = Runtime.getRuntime().exec("su").getOutputStream();
                os = Runtime.getRuntime().exec("sh").getOutputStream();
            }

            cmd = cmd +" "+"\n";
            os.write(cmd.getBytes());
            os.flush();

        } catch (IOException e) {

            e.printStackTrace();
        }

    }


    /**
     * 无延时模拟全局按键
     *
     * @param keyCode
     *            键值
     */
    public static void simulateKey(int keyCode) {

        //使用KeyEvent模拟按键按下与弹起
        long l = SystemClock.uptimeMillis();
        KeyEvent localKeyEvent = new KeyEvent(l,l, KeyEvent.ACTION_DOWN,keyCode,0);
        KeyEvent localKeyEvent1 = new KeyEvent(l,l, KeyEvent.ACTION_UP,keyCode,0);


        try {


            Class ipmClass = Class.forName("android.hardware.input.InputManager");
            Object  ipmInstnace = ipmClass.getMethod("getInstance").invoke(null, (Object[]) null);
            Method trimMemory = ipmClass.getMethod("injectInputEvent",InputEvent.class,int.class);


            trimMemory.invoke(ipmInstnace,localKeyEvent,0);

            trimMemory.invoke(ipmInstnace,localKeyEvent1,0);

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("CLEAR","failed");
        }

    }

    /**
     * 按下back
     */

    public static  void  keyBack(){

        //runCmd("input keyevent KEYCODE_BACK");
        //simulateKey(KeyEvent.KEYCODE_BACK);
    }

    public static void keyBack(AccessibilityService service){

        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }


    /**
     * 按下menu
     */
    public static void  keyMenu(){
        //runCmd("input keyevent KEYCODE_MENU");
       simulateKey(KeyEvent.KEYCODE_BACK);
    }


    public static void keyHome(Context context){

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

    /**
     *按下电源键
     */

    public static void  pressPower(Context context){

        //runCmd("input keyevent KEYCODE_POWER");
       //simulateKey(KeyEvent.KEYCODE_POWER);
        //AlertWakeLock.turnScreenOff(context);

    }
    public static void lockScreen(Context context){

        DevicePolicyManager policyManager = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminReceiver = new ComponentName(context,
                ScreenOffAdminReceiver.class);
        boolean admin = policyManager.isAdminActive(adminReceiver);
        if (admin) {
            policyManager.lockNow();
        }
    }

    /**
     * 打开任务面板
     */

    public static void  openRecnetTask(){

        //runCmd("input keyevent KEYCODE_APP_SWITCH");
       // simulateKey(KeyEvent.KEYCODE_APP_SWITCH);
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

       // runCmd("input keyevent KEYCODE_VOLUME_UP");
       // simulateKey(KeyEvent.KEYCODE_VOLUME_UP);

    }

    /**
     * 音量下键
     */
    public static void vloumeDown() {

        if(mAudioManager ==null){
            mAudioManager= (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        }

        if(mAudioManager.isMusicActive()){

            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
        }else {

            mAudioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI|AudioManager.FLAG_PLAY_SOUND);

        }

        //runCmd("input keyevent KEYCODE_VOLUME_DOWN");
       //simulateKey(KeyEvent.KEYCODE_VOLUME_DOWN);

    }

    /**
     * 重启
     */
    public static void reboot() {

        runCmd("reboot");

    }

    /**
     * 关机
     */
    public static void shutdown() {

       runCmd("poweroff");

    }

    public static void previousApp() throws Exception {

        String prePackageName = null;

        List<String> packageList = AppUtils.getTasks();
        Log.i("tasks:",packageList.toString());
        if(packageList.size()>0){
            prePackageName = packageList.get(0);
        }else {

            Toast.makeText(MyApplication.getApplication(),"没有更早的应用了！",Toast.LENGTH_SHORT).show();
        }

        if(prePackageName!=null){

            AppUtils.startApplication(prePackageName);
        }


    }

    public static void nextApp(){


    }

    public static void killCurrentApp(){

        if(mActivitymanager==null){

           mActivitymanager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);

        }

        if(mActivitymanager!=null){

            ActivityManager.RunningAppProcessInfo info=AppUtils.getCurrentAppInfo();

            keyHome(context);

            if(info!=null){

                android.os.Process.killProcess(info.pid);
                android.os.Process.sendSignal(info.pid, android.os.Process.SIGNAL_KILL);
                mActivitymanager.killBackgroundProcesses(AppUtils.getApplicationInfoByProcessName(info.processName).packageName);
            }

        }

    }


    /**
     * 根据图标信息获取图标
     * @param
     * @return
     */
  /*  public static Bitmap getBitmap(String iconName) {
        Bitmap bitmapicon = null;
        switch (iconName) {
            case "nor":
                bitmapicon =   ((BitmapDrawable) (MyApplication.getApplication().getResources().getDrawable(R.drawable.nor))).getBitmap();
                break;
            case "iphone":
                bitmapicon =   ((BitmapDrawable) (MyApplication.getApplication().getResources().getDrawable(R.drawable.iphone))).getBitmap();
                break;
            case "windows":
                bitmapicon =   ((BitmapDrawable) (MyApplication.getApplication().getResources().getDrawable(R.drawable.windows))).getBitmap();
                break;
            case "babble":
                bitmapicon =   ((BitmapDrawable) (MyApplication.getApplication().getResources().getDrawable(R.drawable.babble))).getBitmap();
                break;
            case "clover":
                bitmapicon =   ((BitmapDrawable) (MyApplication.getApplication().getResources().getDrawable(R.drawable.clover))).getBitmap();
                break;
            default:

                bitmapicon = scaleBitmap(iconName,100);
                break;

        }

        return bitmapicon;


    }*/

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
        ToolInfo music = new ToolInfo(context.getResources().getDrawable(R.drawable.switch_15_music),"音乐开关");
        ToolInfo musicNext = new ToolInfo(context.getResources().getDrawable(R.drawable.switch_16_music_next),"音乐下一曲");
        ToolInfo musicPrev = new ToolInfo(context.getResources().getDrawable(R.drawable.switch_17_music_prev),"音乐上一曲");
        ToolInfo screenShot = new ToolInfo(context.getResources().getDrawable(R.drawable.screen_shot),"屏幕截图");
        ToolInfo screenOn = new ToolInfo(context.getResources().getDrawable(R.drawable.screen_on),"屏幕常亮");

        if(Build.VERSION.SDK_INT<23){

            toolList.add(wifi);
            // toolList.add(data);
            toolList.add(bluetooth);
            toolList.add(rotation);
        }

        toolList.add(screenOn);
        toolList.add(flash);
        toolList.add(vibration);
        toolList.add(screenShot);
        toolList.add(mute);
        toolList.add(music);
        toolList.add(musicNext);
        toolList.add(musicPrev);

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
            case "音乐上一曲":
                icon = context.getResources().getDrawable(R.drawable.switch_17_music_prev);
                break;
            case "音乐下一曲":
                icon = context.getResources().getDrawable(R.drawable.switch_16_music_next);
                break;
            case "屏幕截图":
                icon = context.getResources().getDrawable(R.drawable.screen_shot);
            case "屏幕常亮":
                icon = context.getResources().getDrawable(R.drawable.screen_on);
            default:
                break;

        }

        return icon;
    }

    public static void switchButton(String name){

        switch (name){

            case "WIFI":
                switchWifi();
                break;
            case "移动数据":
                switchMoblieData();
                break;
            case "蓝牙":
                switchBluetooth();
                break;
            case "手电筒":
                switchFlashlight();
                break;
            case "震动/声音":
                vibrationMode();
                break;
            case "静音/声音":
                muteMode();
                break;
            case "屏幕旋转":
                switchRotation();
                break;
            case "音乐开关":
                switchMusic();
                break;
            case "音乐上一曲":
                prevMusic();
                break;
            case "音乐下一曲":
                nextMusic();
                break;
            case "屏幕截图":
                if(Build.VERSION.SDK_INT > 20){

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                Thread.sleep(600);
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
                switchKeepScreenOn();
                break;
            default:
                break;
        }

    }

    private static void prevMusic() {

        simulateKey(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
        /*final Intent intent = new Intent(context, BlankActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                    simulateKey(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
                    intent.putExtra("finish",true);
                    context.startActivity(intent);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();*/

        /*long eventtime = SystemClock.uptimeMillis();
        Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
        KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_MEDIA_NEXT, 0);
        downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
        context.sendOrderedBroadcast(downIntent, null);*/
    }

    private static void nextMusic() {

        simulateKey(KeyEvent.KEYCODE_MEDIA_NEXT);
       /* long eventtime = SystemClock.uptimeMillis();
        Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
        KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_MEDIA_NEXT, 0);
        downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
        context.sendOrderedBroadcast(downIntent, null);*/
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


        if(Build.VERSION.SDK_INT < 23) {
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
                Toast.makeText(context,"屏幕旋转已启用",Toast.LENGTH_SHORT).show();

            } else if (gravity == 1) {

                //关闭
                Settings.System.putInt(resolver, Settings.System.ACCELEROMETER_ROTATION, 0);
                Toast.makeText(context,"屏幕旋转已关闭",Toast.LENGTH_SHORT).show();
            }
        }else {

            Toast.makeText(context,"6.0不支持该功能",Toast.LENGTH_SHORT).show();

        }

    }

    private static void muteMode() {

        if(mAudioManager ==null){
            mAudioManager= (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        }

        if(mAudioManager.getRingerMode()!=AudioManager.RINGER_MODE_SILENT){

            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            Toast.makeText(context,"静音模式已启用",Toast.LENGTH_SHORT).show();
        }else {

            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            Toast.makeText(context,"普通模式已启用",Toast.LENGTH_SHORT).show();
        }




    }

    private static void vibrationMode() {

        if(mAudioManager ==null){
            mAudioManager= (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        }


        if(mAudioManager.getRingerMode()!=AudioManager.RINGER_MODE_VIBRATE){

            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            Toast.makeText(context,"震动模式已启用",Toast.LENGTH_SHORT).show();
        }else {

            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            Toast.makeText(context,"普通模式已启用",Toast.LENGTH_SHORT).show();
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

            } else {
                parameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameter);
                mCamera.release();
                mCamera = null;
            }
        }else {

            CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);

            String[] list={};

            if(!mIsFlashOpened){

                try {
                    list = manager.getCameraIdList();
                    manager.setTorchMode(list[0], true);
                    mIsFlashOpened = true;
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }


            }else {


                try {
                    list = manager.getCameraIdList();
                    manager.setTorchMode(list[0], false);
                    mIsFlashOpened = false;
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    private static void switchBluetooth() {

        if(Build.VERSION.SDK_INT < 23){

            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

            if(adapter.isEnabled()){

                adapter.disable();

            }else {

                adapter.enable();
            }

        }else {

            Toast.makeText(context,"6.0不支持该功能",Toast.LENGTH_SHORT).show();
        }


    }

    private static void switchMoblieData() {


      /*  boolean isMobileDataEnabled;

        if(mTelephonyManager==null){

            mTelephonyManager=(TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        }

        if(mConnectivityManager==null){

            mConnectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        //判断当前手机是否在使用MobileData(移动数据)
        if (mTelephonyManager.getDataState()== TelephonyManager.DATA_CONNECTED) {
            isMobileDataEnabled=true;
        }else {

            isMobileDataEnabled = false;
        }

        try {
            if(mMethod == null){
                mMethod=mConnectivityManager.getClass().getDeclaredMethod("setMobileDataEnabled", boolean.class);
            }

            mMethod.setAccessible(true);
            mMethod.invoke(mConnectivityManager, !isMobileDataEnabled);

        } catch (Exception e) {

            e.printStackTrace();
            Log.e("RelaxFinger","切换移动数据失败！");
        }*/


    }

    private static void switchWifi() {

        if(Build.VERSION.SDK_INT < 23) {
            if (mWifiManager == null) {
                mWifiManager = (WifiManager) context
                        .getSystemService(Context.WIFI_SERVICE);
            }

            mWifiManager.setWifiEnabled(!mWifiManager.isWifiEnabled());

        }else {

            Toast.makeText(context,"6.0不支持该功能",Toast.LENGTH_SHORT).show();

        }

    }

    public static void checkPermissionGranted(Activity activity, String permission) {


        if(Build.VERSION.SDK_INT>22){

            int grant = activity.checkSelfPermission(permission);

            if (grant != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                activity.requestPermissions(new String[]{permission}, 123);
            }
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

            Toast.makeText(context,"屏幕常亮已开启！",Toast.LENGTH_SHORT).show();

        }else {

            mWakeLock.release();

            Toast.makeText(context,"屏幕常亮已关闭！",Toast.LENGTH_SHORT).show();
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

    public static byte[] serialize(Object object){
        try {
            ByteArrayOutputStream mem_out = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(mem_out);

            out.writeObject(object);

            out.close();
            mem_out.close();

            byte[] bytes =  mem_out.toByteArray();
            return bytes;
        } catch (IOException e) {
            return null;
        }
    }

    public static Object deserialize(byte[] bytes){
        try {
            ByteArrayInputStream mem_in = new ByteArrayInputStream(bytes);
            ObjectInputStream in = new ObjectInputStream(mem_in);

            Object object = in.readObject();

            in.close();
            mem_in.close();

            return object;
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }   catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }





}

