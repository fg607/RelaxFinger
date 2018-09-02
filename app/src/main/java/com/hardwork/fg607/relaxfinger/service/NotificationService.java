package com.hardwork.fg607.relaxfinger.service;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.inputmethodservice.InputMethodService;
import android.os.Build;
import android.os.Parcelable;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.SparseArray;
import android.view.inputmethod.InputMethodManager;

import com.hardwork.fg607.relaxfinger.model.Config;
import com.hardwork.fg607.relaxfinger.model.NotifyAppInfo;
import com.hardwork.fg607.relaxfinger.utils.FloatingBallUtils;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotificationService extends NotificationListenerService {

    private Context context;
    private SharedPreferences sp;
    private SparseArray<StatusBarNotification> mNotificationArray;
    private List<String> mNotifyPkgList = new ArrayList<>();
    public static List<String> sKeyboardTitleList = new ArrayList<>(Arrays.asList(new String[] {
            "选择输入法","更改键盘","选择键盘","更改输入法"}));

    @Override
    public void onCreate() {
        super.onCreate();

        SugarRecord.executeQuery("CREATE TABLE IF NOT EXISTS NOTIFY_APP_INFO (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, APP_NAME TEXT, PACKAGE_NAME TEXT UNIQUE)");

        context = getApplicationContext();
        sp = FloatingBallUtils.getSharedPreferences();
        mNotificationArray = new SparseArray<>();
        updateNotifyAppList();

    }

    private void updateNotifyAppList() {

        List<NotifyAppInfo> notifyAppList = NotifyAppInfo.listAll(NotifyAppInfo.class);

        mNotifyPkgList.clear();

        for(NotifyAppInfo info:notifyAppList){

            mNotifyPkgList.add(info.getPackageName());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        StatusBarNotification[] notifies = getActiveNotifications();

        StatusBarNotification validSbn = null;

        for(int i = 0;i<notifies.length;i++){

            if(notifies[i].isClearable()){

                if(notifies[i].getPackageName().equals(sbn.getPackageName()) &&
                        notifies[i].getId()==sbn.getId()){

                    validSbn = notifies[i];
                    break;
                }
            }
        }

        if(validSbn != null && mNotifyPkgList.contains(validSbn.getPackageName()) && !validSbn.getPackageName().equals("android")
                && validSbn.getNotification().contentIntent != null){

            StatusBarNotification notify = mNotificationArray.get(validSbn.getId());

            if(notify == null){

                mNotificationArray.put(validSbn.getId(),validSbn);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

                    Icon icon = validSbn.getNotification().getLargeIcon();

                    notifyNewNotify(validSbn,icon);

                }else {

                    notifyNewNotify(validSbn,null);
                }

            }

        }

        try {

            String title = sbn.getNotification().extras.getString("android.title");

            if (title == null) return;

            if(sKeyboardTitleList.contains(title)){

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

        if(mNotificationArray.indexOfKey(sbn.getId()) != -1){

            mNotificationArray.remove(sbn.getId());

            notifyCancelNotify(sbn.getId());

        }


        try {

            String title = sbn.getNotification().extras.getString("android.title");

            if (title == null) return;

            if(sKeyboardTitleList.contains(title)){

                if(sp.getBoolean("floatSwitch",false)){

                    sendMsg(Config.FLOAT_AUTOMOVE, "move", false);
                }
            }

        }catch (Exception e){

            e.printStackTrace();
        }


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {

            switch (intent.getIntExtra("what", -1)) {

                case Config.OPEN_NOTIFICATION:
                    int id = intent.getIntExtra("notifyId",-1);
                    if(id != -1){

                        openNotification(id);
                    }
                    break;
                case Config.NOTIFY_APP_CHANGE:
                    updateNotifyAppList();
                    break;
                default:
                    break;
            }
        }


        return super.onStartCommand(intent, flags, startId);
    }

    private void openNotification(int id) {

        StatusBarNotification sbn = mNotificationArray.get(id);

        if(sbn != null){

            try {

                sbn.getNotification().contentIntent.send();


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cancelNotification(sbn.getKey());
                }

                cancelNotification(sbn.getPackageName(), sbn.getTag(), sbn.getId());

            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }

        mNotificationArray.remove(id);
    }

    public  void sendMsg(int what, String name, boolean action) {
        Intent intent = new Intent();
        intent.putExtra("what",what);
        intent.putExtra(name, action);
        intent.setClass(this, FloatService.class);
        startService(intent);
    }

    public  void notifyNewNotify(StatusBarNotification sbn, Parcelable icon) {
        Intent intent = new Intent();
        intent.putExtra("what",Config.NEW_NOTIFICATION);
        intent.putExtra("notifyId",sbn.getId());
        intent.putExtra("pkg",sbn.getPackageName());
        if(icon != null)intent.putExtra("icon",icon);
        intent.setClass(this, FloatService.class);
        startService(intent);
    }

    public  void notifyCancelNotify(int id) {
        Intent intent = new Intent();
        intent.putExtra("what",Config.CANCEL_NOTIFICATION);
        intent.putExtra("notifyId",id);
        intent.setClass(this, FloatService.class);
        startService(intent);
    }


}