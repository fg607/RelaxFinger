package com.hardwork.fg607.relaxfinger.service;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

import com.hardwork.fg607.relaxfinger.SettingActivity;
import com.hardwork.fg607.relaxfinger.action.GestureImpl;
import com.hardwork.fg607.relaxfinger.manager.FloatViewManager;
import com.hardwork.fg607.relaxfinger.model.Config;

import java.util.List;

/**
 * Created by fg607 on 16-11-24.
 */

public class FloatService extends Service{

    private FloatViewManager mFloatManager;
    private GestureImpl mGestureImpl;
    private AccessibilityManager mAccessibilityManger;
    private Bundle mBundle;

    private Messenger mMessenger = new Messenger(new MyHandler());

    class MyHandler extends Handler{

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            mBundle = msg.getData();

            if(mBundle == null){

                return;
            }

            switch (msg.what) {

                case Config.TO_EDGE_SWITCH:
                    mFloatManager.setBallToEdge(mBundle.getBoolean("isToEdge",false));
                    break;
                case Config.GESTURE_FUNCTION:
                    mGestureImpl.loadFunction();
                    break;
                case Config.MOVE_SWITCH:
                    mFloatManager.setMove(mBundle.getBoolean("canmove", false));
                    break;
                case Config.VIBRATOR_SWITCH:
                    mGestureImpl.setVibrator(mBundle.getBoolean("isVibrate", true));
                    break;
                case Config.FEEDBACK_SWITCH:
                    mGestureImpl.setFeedback(mBundle.getBoolean("isFeedback", true));
                    break;
                case Config.HIDE_AREA_SWITCH:
                    mFloatManager.enableHideArea(mBundle.getBoolean("showHideArea",true));
                    break;
                case Config.AUTO_HIDE_SWITCH:
                    mFloatManager.enableLandscapeHide(mBundle.getBoolean("isAutoHide",false));
                    break;
                case Config.CLOSE_MENU:
                    mFloatManager.closeMenu();
                    break;
                case Config.FLOAT_THEME:
                    mFloatManager.setBallTheme(mBundle.getString("theme"));
                    break;
                case Config.BALL_SIZE:
                    mFloatManager.setBallSize(mBundle.getInt("ballsize", 1));
                    break;
                case Config.BALL_ALPHA:
                    mFloatManager.setBallAlpha(mBundle.getInt("ballalpha", 1));
                    break;
                case Config.UPDATE_APP:
                    String which = mBundle.getString("which");
                    if (which != null) {
                        mFloatManager.updateMenuIcon(which);
                    }
                    break;
                case Config.SCREEN_SHOT:
                    mGestureImpl.saveScreenShot(mBundle.getParcelable("screenShot"));
                    break;
                default:
                    break;

            }
        }
    }



    @Override
    public void onCreate() {
        super.onCreate();

        mFloatManager = new FloatViewManager(this);

        mGestureImpl = new GestureImpl(mFloatManager);

        Log.i("floatservice","create");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {

            switch (intent.getIntExtra("what", -1)) {

                case Config.FLOAT_SWITCH:
                    mFloatManager.setFloatState(intent.getBooleanExtra("ballstate", false));
                    if(!intent.getBooleanExtra("ballstate", false)){

                        stopSelf();

                    }else {

                        if (!checkAccessibility()) {

                            openSettingActivity();
                        }
                    }
                    break;
                case Config.HIDE_BALL://屏幕截图隐藏悬浮球
                    mFloatManager.setBallHide(intent.getBooleanExtra("hide", false));
                    break;
                case Config.FLOAT_AUTOMOVE://避让软键盘进入自由移动模式
                    mFloatManager.setFloatAutoMove(intent.getBooleanExtra("move", false));
                    break;
                case Config.HIDE_TO_NOTIFYBAR:
                    mFloatManager.hideToNotifyBar();
                    break;
                case Config.RECOVER_FLOATBALL:
                    mFloatManager.recoveryFromNotifyBar();
                    break;
                default:
                    break;
            }

        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {

        return mMessenger.getBinder();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mFloatManager.configurationChanged(newConfig);

    }

    private void openSettingActivity() {

        Intent intent = new Intent(this, SettingActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private boolean checkAccessibility() {

        mAccessibilityManger = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> mList = mAccessibilityManger.getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);

        for (int i = 0; i < mList.size(); i++) {
            if ("com.hardwork.fg607.relaxfinger/.service.NavAccessibilityService".equals(mList.get(i).getId())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("floatservice","destory");
    }
}
