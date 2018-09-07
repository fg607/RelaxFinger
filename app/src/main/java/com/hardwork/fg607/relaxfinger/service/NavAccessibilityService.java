package com.hardwork.fg607.relaxfinger.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.inputmethod.InputMethodManager;


import com.hardwork.fg607.relaxfinger.SettingActivity;
import com.hardwork.fg607.relaxfinger.model.Config;
import com.hardwork.fg607.relaxfinger.utils.FloatingBallUtils;

import net.grandcentrix.tray.AppPreferences;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by fg607 on 16-1-23.
 */
public class NavAccessibilityService extends AccessibilityService {

    public  static AccessibilityService instance = null;
    private AppPreferences sp;
    private InputMethodManager mIMM = null;
    private Method getIMHeightMethod = null;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        instance = this;

        sp = FloatingBallUtils.getMultiProcessPreferences();

         mIMM = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        Class<InputMethodManager> iMSClass = InputMethodManager.class;
        try {
            getIMHeightMethod= iMSClass.getMethod("getInputMethodWindowVisibleHeight");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }



      /*  //动态配置
        AccessibilityServiceInfo config = new AccessibilityServiceInfo();
        config.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED;
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;

        if (Build.VERSION.SDK_INT >= 16){

            config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
        }

        setServiceInfo(config);*/


    }


    @Override
    public void onAccessibilityEvent(final AccessibilityEvent accessibilityEvent) {

        if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {


            if(accessibilityEvent.getPackageName()!=null) {
                String foregroundPackageName = accessibilityEvent.getPackageName().toString();

                notifyWindowChange(foregroundPackageName);
            }


            if("android.inputmethodservice.SoftInputWindow".equals(accessibilityEvent.getClassName())){

                notifyInputWindowShow(true);

                if(getIMHeightMethod != null && mIMM != null){

                    new Thread(() -> {

                        while (true){

                            try {
                                Thread.sleep(255);

                                //反射判断键盘高度，为0则表明键盘关闭！
                                int imeHeight = (int) getIMHeightMethod.invoke(mIMM);
                                if(imeHeight == 0){

                                    notifyInputWindowShow(false);

                                    break;
                                }

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }


                    }).start();
                }

            }



        }

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {

        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK && isBallMenuShowing()){

            sendMsg(Config.KEY_BACK_PRESSED);

            return true;
        }


        return super.onKeyEvent(event);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent != null){

            switch (intent.getIntExtra("what", -1)){

                case Config.STOP_SELF:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        disableSelf();
                    }
                    break;
                default:
                    break;
            }

        }

        return super.onStartCommand(intent, flags, startId);
    }

    private boolean isBallMenuShowing() {

        return  sp.getBoolean("isBkgShowing",false);
    }

    public  void sendMsg(int what, String name, boolean action) {
        Intent intent = new Intent();
        intent.putExtra("what",what);
        intent.putExtra(name, action);
        intent.setClass(this, FloatService.class);
        startService(intent);
    }

    public  void sendMsg(int what) {
        Intent intent = new Intent();
        intent.putExtra("what",what);
        intent.setClass(this, FloatService.class);
        startService(intent);
    }

    private void notifyWindowChange(String foregroundPackageName) {
        Intent intent = new Intent();
        intent.putExtra("what", Config.FOREGROUND_APP_CHANGE);
        intent.putExtra("pkg",foregroundPackageName);
        intent.setClass(this, FloatService.class);
        startService(intent);
    }

    private void notifyInputWindowShow(boolean isShowing) {
        Intent intent = new Intent();
        intent.putExtra("what", Config.FLOAT_AUTOMOVE);
        intent.putExtra("move",isShowing);
        intent.setClass(this, FloatService.class);
        startService(intent);
    }
}
