package com.hardwork.fg607.relaxfinger.utils;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import com.hardwork.fg607.relaxfinger.MyApplication;
import com.hardwork.fg607.relaxfinger.SettingActivity;

import java.util.List;

import static android.content.Context.ACCESSIBILITY_SERVICE;
import static android.content.Context.USAGE_STATS_SERVICE;

/**
 * Created by fg607 on 16-11-26.
 */

public class AccessibilityUtil {

    public static Context context = MyApplication.getApplication();
    public static AccessibilityManager mAccessibilityManger;

    public static boolean checkAccessibility() {


        if(mAccessibilityManger == null){

            mAccessibilityManger = (AccessibilityManager)context.getSystemService(ACCESSIBILITY_SERVICE);
        }


        List<AccessibilityServiceInfo> mList = mAccessibilityManger.getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);

        for (int i = 0; i < mList.size(); i++) {
            if ("com.hardwork.fg607.relaxfinger/.service.NavAccessibilityService".equals(mList.get(i).getId())) {
                return true;
            }
        }

        return false;
    }

    public static void openSettingActivity() {

        Intent intent = new Intent(context, SettingActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    //判断调用该设备中“有权查看使用权限的应用”这个选项的APP有没有打开
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static boolean isUsageAccess() {
        long ts = System.currentTimeMillis();
        UsageStatsManager usageStatsManager = (UsageStatsManager)
                context.getSystemService(USAGE_STATS_SERVICE);
        List queryUsageStats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_BEST, 0, ts);
        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return false;
        }
        return true;
    }

    public static void requestUsageAccessPermission() {

        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {

            context.startActivity(intent);

        } catch (Exception e) {

            Toast.makeText(context, "该ROM不支持切换上一应用功能!", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isServiceRunning(Context mContext,String className) {

        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(30);

        if (!(serviceList.size()>0)) {
            return false;
        }

        for (int i=0; i<serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    public static boolean isServiceRunning(){

        return isServiceRunning(context,"com.hardwork.fg607.relaxfinger.service.FloatService");
    }

}
