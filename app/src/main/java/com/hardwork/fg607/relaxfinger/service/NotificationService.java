package com.hardwork.fg607.relaxfinger.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.hardwork.fg607.relaxfinger.utils.Config;
import com.hardwork.fg607.relaxfinger.utils.FloatingBallUtils;

public class NotificationService extends NotificationListenerService {
    Context context;
    private SharedPreferences sp;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        sp = FloatingBallUtils.getSharedPreferences();
    }
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        String title = sbn.getNotification().extras.getString("android.title");

        Log.i("title",title);

        if(title.contains("选择输入法") || title.contains("更改键盘")
                || title.contains("选择键盘")){

            if(sp.getBoolean("floatSwitch",false)){

                sendMsg(Config.FLOAT_AUTOMOVE, "move", true);
            }

        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

        String title = sbn.getNotification().extras.getString("android.title");

        if(title.equals("选择输入法") || title.contains("更改键盘")
                || title.contains("选择键盘")){

            if(sp.getBoolean("floatSwitch",false)){

                sendMsg(Config.FLOAT_AUTOMOVE, "move", false);
            }
        }

    }


    public  void sendMsg(int what, String name, boolean action) {
        Intent intent = new Intent();
        intent.putExtra("what",what);
        intent.putExtra(name, action);
        intent.setClass(this, FloatingBallService.class);
        startService(intent);
    }


}