package com.hardwork.fg607.relaxfinger.model;

import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.hardwork.fg607.relaxfinger.MyApplication;

import java.io.Serializable;

/**
 * Created by fg607 on 16-6-4.
 */
public class ShortcutInfo{

    private Drawable shortcutIcon;
    private String shortcutTitle;
    private String shortcutIntent;

    public ShortcutInfo(Drawable shortcutIcon, String shortcutTitle, String shortcutIntent) {
        this.shortcutIcon = shortcutIcon;
        this.shortcutTitle = shortcutTitle;
        this.shortcutIntent = shortcutIntent;
    }

    public Drawable getShortcutIcon() {
        return shortcutIcon;
    }

    public void setShortcutIcon(Drawable shortcutIcon) {
        this.shortcutIcon = shortcutIcon;
    }

    public String getShortcutTitle() {
        return shortcutTitle;
    }

    public void setShortcutTitle(String shortcutTitle) {
        this.shortcutTitle = shortcutTitle;
    }

    public String getShortcutIntent() {
        return shortcutIntent;
    }

    public void setShortcutIntent(String shortcutIntent) {
        this.shortcutIntent = shortcutIntent;
    }
}
