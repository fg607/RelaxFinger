package com.hardwork.fg607.relaxfinger.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.GridView;

import com.hardwork.fg607.relaxfinger.MyApplication;
import com.hardwork.fg607.relaxfinger.R;
import com.hardwork.fg607.relaxfinger.adapter.MenuFolderAdapter;
import com.hardwork.fg607.relaxfinger.model.MenuDataSugar;
import com.hardwork.fg607.relaxfinger.utils.DensityUtil;
import com.hardwork.fg607.relaxfinger.utils.FloatingBallUtils;

import java.util.List;

/**
 * Created by fg607 on 16-11-28.
 */

public class FolderViewProxy {

    private View mFolderView;
    private GridView mGridView;
    private CardView mCardView;
    private MenuFolderAdapter mFolderAdapter;
    private WindowManager.LayoutParams mWinLayoutParams;
    private AnimationSet mShowFolderAnimSet;
    private AnimationSet mHideFolderAnimSet;
    private Context mContext;


    public FolderViewProxy(Context context){

        mContext = context;

        initFolderView();

        initLayoutParams();

        initAnimation();
    }

    private void initAnimation() {

        ScaleAnimation animation = new ScaleAnimation(0.5f, 1, 0.5f, 1, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        AlphaAnimation showAlphaAnimation = new AlphaAnimation(0.1f, 1.0f);

        mShowFolderAnimSet = new AnimationSet(true);
        mShowFolderAnimSet.addAnimation(animation);
        mShowFolderAnimSet.addAnimation(showAlphaAnimation);
        mShowFolderAnimSet.setDuration(100);
        mShowFolderAnimSet.setFillAfter(true);
        mShowFolderAnimSet.setFillEnabled(true);

        ScaleAnimation hideFolderanimation = new ScaleAnimation(1, 0.5f, 1, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        AlphaAnimation hideAlphaAnimation = new AlphaAnimation(1.0f, 0.1f);

        mHideFolderAnimSet = new AnimationSet(true);
        mHideFolderAnimSet.addAnimation(hideFolderanimation);
        mHideFolderAnimSet.addAnimation(hideAlphaAnimation);
        mHideFolderAnimSet.setDuration(100);
        mHideFolderAnimSet.setFillAfter(true);
        mHideFolderAnimSet.setFillEnabled(true);

        mHideFolderAnimSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                if (mFolderView.getParent() != null) {

                    MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            FloatingBallUtils.getWindowManager().removeViewImmediate(mFolderView);
                        }
                    },50);

                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void initFolderView() {

        mFolderView = LayoutInflater.from(mContext).inflate(R.layout.menu_folder, null);

        mGridView = (GridView) mFolderView.findViewById(R.id.grid_view);

        mCardView = (CardView) mFolderView.findViewById(R.id.card_view);

        mFolderAdapter = new MenuFolderAdapter(mContext);
    }

    private void initLayoutParams() {
        mWinLayoutParams = new WindowManager.LayoutParams();

        mWinLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;


        mWinLayoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;//| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
        mWinLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;

        mWinLayoutParams.width = DensityUtil.getScreenWidth(mContext) * 2 / 3;
        mWinLayoutParams.height = DensityUtil.getScreenHeight(mContext) / 3;

        mWinLayoutParams.x = DensityUtil.getScreenWidth(mContext) / 6;
        mWinLayoutParams.y = DensityUtil.getScreenHeight(mContext) / 2;

        mWinLayoutParams.format = PixelFormat.RGBA_8888;
    }

    public void setOnFolderItemClickListener(MenuFolderAdapter.OnFolderItemClickListener listener){

        mFolderAdapter.setOnFolderItemClickListener(listener);
    }

    public void showFolder(List<MenuDataSugar> menuDataList){

        if (mFolderView.getParent() == null) {

            mFolderAdapter.setMenuDataList(menuDataList);

            mGridView.setAdapter(mFolderAdapter);

            FloatingBallUtils.getWindowManager().addView(mFolderView, mWinLayoutParams);

            mCardView.startAnimation(mShowFolderAnimSet);

        }
    }

    public void hideFolder(){

        if(mFolderView.getParent() != null){

            mCardView.startAnimation(mHideFolderAnimSet);
        }
    }

    public void configurationChanged(boolean isLandscape) {

        if(isLandscape){

            mWinLayoutParams.width = DensityUtil.getScreenWidth(mContext) * 2 / 5;
            mWinLayoutParams.height = DensityUtil.getScreenHeight(mContext)* 2 / 3;

            mWinLayoutParams.x = DensityUtil.getScreenWidth(mContext) * 3 / 10;
            mWinLayoutParams.y = DensityUtil.getScreenHeight(mContext) / 6;

        }else {

            mWinLayoutParams.width = DensityUtil.getScreenWidth(mContext) * 2 / 3;
            mWinLayoutParams.height = DensityUtil.getScreenHeight(mContext) / 3;

            mWinLayoutParams.x = DensityUtil.getScreenWidth(mContext) / 6;
            mWinLayoutParams.y = DensityUtil.getScreenHeight(mContext) / 2;
        }

    }
}
