package com.hardwork.fg607.relaxfinger.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.hardwork.fg607.relaxfinger.MyApplication;
import com.hardwork.fg607.relaxfinger.model.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fg607 on 15-11-26.
 */
public class AppUtils {

    public static Context context = MyApplication.getApplication();
    public static PackageManager pm = context.getPackageManager();

    public static ArrayList<AppInfo> getAppInfos(){

        ArrayList<AppInfo> list = new ArrayList<>();

        Drawable icon;
        String name;
        String packageName;

        List<PackageInfo> packageInfos = pm.getInstalledPackages(0);

        for(PackageInfo info:packageInfos){

            //判断是否为用户应用
            if((ApplicationInfo.FLAG_SYSTEM & info.applicationInfo.flags) == 0){

                icon = info.applicationInfo.loadIcon(pm);
                name = info.applicationInfo.loadLabel(pm).toString();
                packageName = info.packageName;

                AppInfo appInfo = new AppInfo(icon,name,packageName);

                list.add(appInfo);
            }


        }

        return list;
    }

    public static String getAppName(String packageName){

        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(packageName,0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if(packageInfo!= null){

            return packageInfo.applicationInfo.loadLabel(pm).toString();
        }else {

            return null;
        }

    }

    public static Drawable getAppIcon(String packageName){

        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(packageName,0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if(packageInfo!= null){

            return packageInfo.applicationInfo.loadIcon(pm);
        }else {

            return null;
        }

    }

    public static boolean startApplication(String packageName){

        PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(packageName);
        if(intent!=null){
            context.startActivity(intent);
            return true;
        }

        return false;



    }

    public static void uninstallApplication(String packageName){

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + packageName));
        context.startActivity(intent);

    }

    public static void showAppDetailActivity(String packageName){

        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + packageName));
        context.startActivity(intent);


    }
    public static String getFilePath(String packageName){

        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageInfo = pm.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }

        if(packageInfo != null){
            applicationInfo =  packageInfo.applicationInfo;
            return applicationInfo.sourceDir;
        }
        return null;
    }
}
