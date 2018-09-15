package com.hardwork.fg607.relaxfinger.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.hardwork.fg607.relaxfinger.R;
import com.hardwork.fg607.relaxfinger.utils.DensityUtil;

/**
 * Created by fg607 on 16-11-28.
 */

public class BackgroundView extends View {

    private Context mContext;
    private WindowManager.LayoutParams mWinLayoutParams;

    public BackgroundView(Context context) {

        super(context);
        mContext = context;

        init();

    }

    private void init() {

        mWinLayoutParams = new WindowManager.LayoutParams();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            mWinLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

        }else {

            mWinLayoutParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        }

        mWinLayoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS|
                WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        mWinLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;

        mWinLayoutParams.x = 0;
        mWinLayoutParams.y = 0;

        mWinLayoutParams.width = DensityUtil.getScreenWidth(mContext);
        mWinLayoutParams.height = DensityUtil.getScreenHeight(mContext);
        mWinLayoutParams.format = PixelFormat.RGBA_8888;
        mWinLayoutParams.dimAmount = 0.5f;

        //setBackgroundColor(mContext.getResources().getColor(R.color.popbackground));

        setClickable(true);
    }

    public WindowManager.LayoutParams getWindowLayoutParams(){

        return mWinLayoutParams;
    }


    public void configurationChanged() {

        mWinLayoutParams.width = DensityUtil.getScreenWidth(mContext);
        mWinLayoutParams.height = DensityUtil.getScreenHeight(mContext);
    }
}
