package com.hardwork.fg607.relaxfinger.model;

/**
 * Created by fg607 on 17-1-17.
 */

public class NotificationInfo {

    String pkg;
    int id;

    public NotificationInfo(String pkg, int id) {

        this.pkg = pkg;
        this.id = id;
    }

    public String getPkg() {
        return pkg;
    }

    public int getId() {
        return id;
    }
}
