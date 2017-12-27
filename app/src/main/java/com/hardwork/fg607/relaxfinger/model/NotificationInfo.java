package com.hardwork.fg607.relaxfinger.model;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;

/**
 * Created by fg607 on 17-1-17.
 */

public class NotificationInfo {

    String pkg;
    int id;
    Drawable icon;

    public NotificationInfo(String pkg, int id,Drawable icon) {

        this.pkg = pkg;
        this.id = id;
        this.icon = icon;
    }

    public String getPkg() {
        return pkg;
    }

    public int getId() {
        return id;
    }

    public Drawable getIcon() {
        return icon;
    }
}
