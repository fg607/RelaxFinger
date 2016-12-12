package com.hardwork.fg607.relaxfinger.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;

import com.hardwork.fg607.relaxfinger.model.Config;
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        try {

            String title = sbn.getNotification().extras.getString("android.title");

            if (title == null) return;

            if(title.contains("选择输入法") || title.contains("更改键盘")
                    || title.contains("选择键盘")){

                if(sp.getBoolean("floatSwitch",false)){

                    sendMsg(Config.FLOAT_AUTOMOVE, "move", true);
                }

            }

        }catch (Exception e){

            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

        try {

            String title = sbn.getNotification().extras.getString("android.title");

            if (title == null) return;

            if(title.equals("选择输入法") || title.contains("更改键盘")
                    || title.contains("选择键盘")){

                if(sp.getBoolean("floatSwitch",false)){

                    sendMsg(Config.FLOAT_AUTOMOVE, "move", false);
                }
            }

        }catch (Exception e){

            e.printStackTrace();
        }


    }


    public  void sendMsg(int what, String name, boolean action) {
        Intent intent = new Intent();
        intent.putExtra("what",what);
        intent.putExtra(name, action);
        intent.setClass(this, FloatService.class);
        startService(intent);
    }


}