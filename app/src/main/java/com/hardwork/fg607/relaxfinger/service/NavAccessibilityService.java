package com.hardwork.fg607.relaxfinger.service;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.hardwork.fg607.relaxfinger.utils.Config;
import com.hardwork.fg607.relaxfinger.utils.FloatingBallUtils;

/**
 * Created by fg607 on 16-1-23.
 */
public class NavAccessibilityService extends AccessibilityService {

    public  static AccessibilityService instance = null;
    private BroadcastReceiver mBatInfoReceiver;
    private SharedPreferences sp;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
        sp = FloatingBallUtils.getSharedPreferences();

        final IntentFilter filter = new IntentFilter();

        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);

        mBatInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                String action = intent.getAction();

                if(Intent.ACTION_SCREEN_ON.equals(action)){

                    sendMsg(Config.SCREEN_ON, "screenOn", true);


                }else if (Intent.ACTION_SCREEN_OFF.equals(action)) {

                    sendMsg(Config.FLOAT_SWITCH, "ballstate", false);
                }
            }
        };

        registerReceiver(mBatInfoReceiver, filter);

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {


    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mBatInfoReceiver!=null){
            unregisterReceiver(mBatInfoReceiver);
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
