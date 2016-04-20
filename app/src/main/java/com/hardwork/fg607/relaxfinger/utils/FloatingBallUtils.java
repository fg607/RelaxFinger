package com.hardwork.fg607.relaxfinger.utils;

/**
 * Created by fg607 on 15-8-20.
 *
 */

import android.accessibilityservice.AccessibilityService;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.hardwork.fg607.relaxfinger.MyApplication;
import com.hardwork.fg607.relaxfinger.receiver.ScreenOffAdminReceiver;

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
import java.util.List;
import java.util.Set;


public class FloatingBallUtils {

    public static OutputStream os;
    public static Bitmap bitmap;
    public static Context context = MyApplication.getApplication();

    public static final TrayAppPreferences multiProcessPreferences = new TrayAppPreferences(context);
    public static SharedPreferences sp = getSharedPreferences();
    public static AudioManager mAudioManager=null;
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

    /**
     * 根据图标信息获取图标
     * @param iconName
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

    /**
     * 保存图标到sd卡
     * @param bitmap
     * @param fileName
     * @throws IOException
     */
    public static String saveBitmap(Bitmap bitmap,String fileName) throws IOException {

        String rootdir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/RelaxFinger";

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



}

