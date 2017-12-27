package com.hardwork.fg607.relaxfinger.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;


import com.hardwork.fg607.relaxfinger.model.Config;
import com.hardwork.fg607.relaxfinger.utils.FloatingBallUtils;

import net.grandcentrix.tray.AppPreferences;

/**
 * Created by fg607 on 16-1-23.
 */
public class NavAccessibilityService extends AccessibilityService {

    public  static AccessibilityService instance = null;
    private AppPreferences sp;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        instance = this;

        sp = FloatingBallUtils.getMultiProcessPreferences();

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


            String foregroundPackageName = accessibilityEvent.getPackageName().toString();

            notifyWindowChange(foregroundPackageName);

        }

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

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
    }
}
