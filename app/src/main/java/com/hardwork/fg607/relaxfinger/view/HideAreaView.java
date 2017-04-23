package com.hardwork.fg607.relaxfinger.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hardwork.fg607.relaxfinger.MyApplication;
import com.hardwork.fg607.relaxfinger.utils.DensityUtil;
import com.hardwork.fg607.relaxfinger.utils.FloatingBallUtils;

/**
 * Created by fg607 on 16-12-3.
 */

public class HideAreaView extends TextView {

    public static final int HIDE_AREA_HEIGHT = 50;

    private Context mContext;
    public WindowManager.LayoutParams mWinLayoutParams;
    private LinearLayout mParentLayout;
    private LinearLayout.LayoutParams mLayoutParams;
    private TranslateAnimation mShowaAnim;
    private TranslateAnimation mHideaAnim;

    public HideAreaView(Context context) {
        super(context);

        mContext = context;

        initTheme();
        initAnimation();
        initParentLayout();
        initLayoutParams();
    }

    private void initParentLayout() {

        mParentLayout = new LinearLayout(mContext);
        mLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }


    private void initAnimation() {

        mShowaAnim = new TranslateAnimation(0, 0, DensityUtil.dip2px(mContext, HIDE_AREA_HEIGHT), 0);
        mShowaAnim.setDuration(150);

        mHideaAnim = new TranslateAnimation(0, 0, 0, DensityUtil.dip2px(mContext, HIDE_AREA_HEIGHT));
        mHideaAnim.setDuration(150);
        mHideaAnim.setFillAfter(true);

        mHideaAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {


                MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        FloatingBallUtils.getWindowManager().removeViewImmediate(mParentLayout);
                        mParentLayout.removeView(HideAreaView.this);

                    }

                },50);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void initTheme() {

        setTextColor(Color.WHITE);
        setTextSize(18);
        setText("拖入此区域隐藏悬浮球");
        setBackgroundColor(Color.BLACK);
        getBackground().setAlpha(180);
        setGravity(Gravity.CENTER);
    }

    private void initLayoutParams() {
        mWinLayoutParams = new WindowManager.LayoutParams();

        mWinLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;

        mWinLayoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        mWinLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;

        mWinLayoutParams.width = DensityUtil.getScreenWidth(mContext);
        mWinLayoutParams.height = DensityUtil.dip2px(mContext, HIDE_AREA_HEIGHT);

        mWinLayoutParams.x = 0;
        mWinLayoutParams.y = FloatingBallUtils.getScreenHeight() - mWinLayoutParams.height;

        mWinLayoutParams.format = PixelFormat.RGBA_8888;
    }

    public void show(){

        if(getParent() == null){

            mParentLayout.addView(this, mLayoutParams);
            FloatingBallUtils.getWindowManager().addView(mParentLayout,mWinLayoutParams);
            startAnimation(mShowaAnim);
        }
    }

    public void close(){

        if(getParent() != null){

            startAnimation(mHideaAnim);
        }

    }


    public void configurationChanged() {

        mWinLayoutParams.width = DensityUtil.getScreenWidth(mContext);
        mWinLayoutParams.y = FloatingBallUtils.getScreenHeight() - mWinLayoutParams.height;

    }
}
