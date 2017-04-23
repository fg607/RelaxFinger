package com.hardwork.fg607.relaxfinger.model;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Created by fg607 on 16-5-14.
 */
public class ToolInfo{

    private Drawable toolIcon;
    private String toolName;

    public ToolInfo(Drawable toolIcon, String toolName) {
        this.toolIcon = toolIcon;
        this.toolName = toolName;
    }

    public Drawable getToolIcon() {
        return toolIcon;
    }

    public void setToolIcon(Drawable toolIcon) {
        this.toolIcon = toolIcon;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }
}
