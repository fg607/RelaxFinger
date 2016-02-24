package com.hardwork.fg607.relaxfinger.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by fg607 on 16-1-23.
 */
public class NavAccessibilityService extends AccessibilityService {

    public  static AccessibilityService instance;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }
}
