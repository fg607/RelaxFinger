package com.hardwork.fg607.relaxfinger.model;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

/**
 * Created by fg607 on 17-3-8.
 */

public class NotifyAppInfo extends SugarRecord {

    String appName;
    @Unique
    String packageName;

    public NotifyAppInfo(){}

    public NotifyAppInfo(String appName, String packageName) {
        this.appName = appName;
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
