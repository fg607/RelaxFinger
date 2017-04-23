package com.hardwork.fg607.relaxfinger.utils;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by xuyisheng on 15/11/6.
 * version 1.0
 */
public final class ShortcutSuperUtils {

    private ShortcutSuperUtils() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }

    public static Uri getUriFromLauncher(Context context) {
        StringBuilder uriStr = new StringBuilder();
       /* String authority = LauncherUtil.getAuthorityFromPermissionDefault(context);

        if (authority == null || authority.trim().equals("")) {
            authority = LauncherUtil.getAuthorityFromPermission(context, LauncherUtil.getCurrentLauncherPackageName(context) + ".permission.READ_SETTINGS");
        }*/

        String authority = LauncherUtil.getAuthorityFromPermission(context, LauncherUtil.getCurrentLauncherPackageName(context) + ".permission.READ_SETTINGS");

        uriStr.append("content://");
        if (TextUtils.isEmpty(authority)) {
            int sdkInt = android.os.Build.VERSION.SDK_INT;
            if (sdkInt < 8) { // Android 2.1.x(API 7)以及以下的
                uriStr.append("com.android.launcher.settings");
            } else if (sdkInt < 19) {// Android 4.4以下
                uriStr.append("com.android.launcher2.settings");
            } else {// 4.4以及以上
                uriStr.append("com.android.launcher3.settings");
            }
        } else {
            uriStr.append(authority);
        }
        uriStr.append("/favorites?notify=true");
        return Uri.parse(uriStr.toString());
    }
}
