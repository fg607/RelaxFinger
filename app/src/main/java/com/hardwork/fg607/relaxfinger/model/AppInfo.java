package com.hardwork.fg607.relaxfinger.model;

import android.graphics.drawable.Drawable;

/**
 * Created by fg607 on 15-11-26.
 */
public class AppInfo {
    private Drawable appIcon;
    private String appName;
    private String appActivity;
    private String appPackage;


    public AppInfo(Drawable appIcon, String appName, String appActivity, String appPackage) {
        this.appIcon = appIcon;
        this.appName = appName;
        this.appActivity = appActivity;
        this.appPackage = appPackage;
    }

    public String getAppActivity() {
        return appActivity;
    }

    public void setAppActivity(String appActivity) {
        this.appActivity = appActivity;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }
}
