package com.hardwork.fg607.relaxfinger.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
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

        mWinLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;

        mWinLayoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        mWinLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;

        mWinLayoutParams.x = 0;
        mWinLayoutParams.y = 0;

        mWinLayoutParams.width = DensityUtil.getScreenWidth(mContext);
        mWinLayoutParams.height = DensityUtil.getScreenHeight(mContext);
        mWinLayoutParams.format = PixelFormat.RGBA_8888;

        setBackgroundColor(mContext.getResources().getColor(R.color.popbackground));

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
