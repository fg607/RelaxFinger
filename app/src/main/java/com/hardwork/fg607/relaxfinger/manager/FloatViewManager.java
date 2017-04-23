package com.hardwork.fg607.relaxfinger.manager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.SparseArray;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.facebook.rebound.SpringConfigRegistry;
import com.hardwork.fg607.relaxfinger.R;
import com.hardwork.fg607.relaxfinger.adapter.MenuFolderAdapter;
import com.hardwork.fg607.relaxfinger.model.HideAppInfo;
import com.hardwork.fg607.relaxfinger.model.MenuDataSugar;
import com.hardwork.fg607.relaxfinger.model.Config;
import com.hardwork.fg607.relaxfinger.model.NotificationInfo;
import com.hardwork.fg607.relaxfinger.model.NotificationStack;
import com.hardwork.fg607.relaxfinger.service.NotificationService;
import com.hardwork.fg607.relaxfinger.utils.AppUtils;
import com.hardwork.fg607.relaxfinger.utils.DensityUtil;
import com.hardwork.fg607.relaxfinger.utils.FloatingBallUtils;
import com.hardwork.fg607.relaxfinger.view.BackgroundView;
import com.hardwork.fg607.relaxfinger.view.BallView;
import com.hardwork.fg607.relaxfinger.view.FolderViewProxy;
import com.hardwork.fg607.relaxfinger.view.HideAreaView;
import com.hardwork.fg607.relaxfinger.view.MenuViewProxy;

import net.grandcentrix.tray.TrayAppPreferences;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.hardwork.fg607.relaxfinger.view.BallView.MAX_BALL_ALPHA;
import static com.hardwork.fg607.relaxfinger.view.BallView.MAX_BALL_SIZE;
import static com.hardwork.fg607.relaxfinger.view.BallView.MIN_BALL_ALPHA;
import static com.hardwork.fg607.relaxfinger.view.BallView.MIN_BALL_SIZE;

/**
 * Created by fg607 on 16-11-24.
 */

public class FloatViewManager implements BallView.OnBallEventListener,
        MenuFolderAdapter.OnFolderItemClickListener {

    private Context mContext;
    private TrayAppPreferences mPreferences;
    private WindowManager mWindowManager;
    private BallView mBallView;
    private WindowManager.LayoutParams mBallParams;
    private MenuViewProxy mMenuViewProxy;
    private FolderViewProxy mFolderViewProxy;
    private BackgroundView mBackgroundView;
    private HideAreaView mHideAreaView;
    private boolean mIsBallRight;
    private boolean mIsBallToEdge;
    private boolean mIsFreeMode = false;
    private boolean mIsBallFree = false;
    private boolean mIsLandscape = false;
    private boolean mOriginMoveState;
    private boolean mIsBallHiding;
    private boolean mIsPositiveHide = false;
    private boolean mIsShowHideArea;
    private boolean mIsLandscapeHide;
    private boolean mIsHomePressed;
    private boolean mIsMoving = false;
    private boolean mIsBallShowing = false;
    private boolean mIsKeyboardShowing = false;
    private NotificationStack mNotifyStack;
    private boolean mIsHideInApp = false;
    private List<String> mHidePkgList = new ArrayList<>();
    private String mPrevNormalPkg = null;
    private String mPrevHidePkg = null;
    private boolean mJustRecovery = false;
    private long mLastCloseTime = 0;
    private boolean mIsAvoidKeyboard = false;

    public FloatViewManager(Context context) {


        mContext = context;
        mNotifyStack = new NotificationStack();

        init();
        initBallView();
        initMenuView();
        initFolderView();
        initBackgroundView();
        initHideAreaView();
        updateHideAppList();

        if(mPreferences.getBoolean("floatSwitch", false)){

            showBall();
        }
    }

    private void init() {

        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

        mPreferences = FloatingBallUtils.getMultiProcessPreferences();
        mIsBallRight = FloatingBallUtils.multiProcessPreferences.getBoolean("floatRight", true);
        mIsBallToEdge = FloatingBallUtils.multiProcessPreferences.getBoolean("toEdgeSwitch", false);
        mIsShowHideArea = FloatingBallUtils.multiProcessPreferences.getBoolean("hideAreaSwitch", true);
        mIsLandscapeHide = FloatingBallUtils.multiProcessPreferences.getBoolean("autoHideSwitch", false);
        mIsAvoidKeyboard = mPreferences.getBoolean("autoMoveSwitch", false);
    }

    private void initBallView() {

        mBallView = new BallView(mContext);

        mBallView.setOnMoveFinishListener(this);

        mBallParams = mBallView.getWindowLayoutParams();
    }

    private void initMenuView() {

        mMenuViewProxy = new MenuViewProxy(mContext);

        calculateMenuPos();

    }

    private void initBackgroundView() {

        mBackgroundView = new BackgroundView(mContext);

        mBackgroundView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                closeMenu();
            }
        });
    }

    private void initHideAreaView() {

        mHideAreaView = new HideAreaView(mContext);
    }

    private void initFolderView() {

        mFolderViewProxy = new FolderViewProxy(mContext);

        mFolderViewProxy.setOnFolderItemClickListener(this);

    }

    public void updateHideAppList() {

        List<HideAppInfo> hideAppList = HideAppInfo.listAll(HideAppInfo.class);

        mHidePkgList.clear();

        //授权界面自动隐藏悬浮球
        mHidePkgList.add("com.android.packageinstaller");

        for(HideAppInfo info:hideAppList){

            mHidePkgList.add(info.getPackageName());
        }
    }

    public void setGestureListener(BallView.OnGestureListener listener) {

        mBallView.setOnGestureListener(listener);
    }

    public void setMenuItemClickListener(MenuViewProxy.OnMenuItemClickListener listener) {

        mMenuViewProxy.setOnItemClickListener(listener);
    }

    private void showBall() {

        mBallView.show();

        mIsBallShowing = true;
    }

    public void popUpMenu() {

        if (mIsMoving) {

            return;
        }

       /* if (mIsHomePressed) {

            Toast.makeText(mContext, "按下系统自带Home键后打开应用程序会有5秒延迟！",
                    Toast.LENGTH_LONG).show();
        }
*/
        if (mMenuViewProxy.getMenuView().getParent() == null) {

            showBackground();

            mMenuViewProxy.showMenu(mIsBallRight);
        }

    }


    public void closeMenu() {

        //防止多次关闭影响关闭动画
        if((System.currentTimeMillis() - mLastCloseTime)<150){

            return;
        }

        mLastCloseTime = System.currentTimeMillis();

        mFolderViewProxy.hideFolder();
        mMenuViewProxy.closeMenu();
        hideBackground();
    }

    private void calculateMenuPos() {

        WindowManager.LayoutParams ballParams = mBallView.getWindowLayoutParams();

        int offsetX, offsetY;

        if (mIsBallRight) {

            offsetX = -(MenuViewProxy.MENU_WINDOW_WIDTH - mBallView.getBallSize());

        } else {

            offsetX = 0;
        }

        offsetY = -(MenuViewProxy.MENU_WINDOW_HEIGHT / 2 - mBallView.getBallSize() / 2);

        mMenuViewProxy.setMenuPos(ballParams.x + offsetX, ballParams.y + offsetY);

    }

    private void showBackground() {

        if (mBackgroundView.getParent() == null) {

            mWindowManager.addView(mBackgroundView, mBackgroundView.getWindowLayoutParams());

            mPreferences.put("isBkgShowing",true);
        }

    }

    private void hideBall() {

        mBallView.hide();
        mIsBallHiding = false;

    }

    private void hideBackground() {

        if (mBackgroundView.getParent() != null) {

            mWindowManager.removeViewImmediate(mBackgroundView);

            mPreferences.put("isBkgShowing",false);
        }

    }

    private void moveBallToEdge() {

        if (mIsBallRight) {

            moveBallToRight();
        } else {

            moveBallToLeft();
        }

    }

    private void moveBallToLeft() {

        final WindowManager.LayoutParams ballLayoutParams = mBallView.getWindowLayoutParams();

        ValueAnimator slideToBoundaryAnim = ValueAnimator.ofFloat(ballLayoutParams.x, 0);

        int maxDuration = 350;

        int duration = (int) (ballLayoutParams.x * maxDuration / (FloatingBallUtils
                .getScreenWidth() / 2 - ballLayoutParams.width / 2));

        if(duration < 0) duration = 0;

        slideToBoundaryAnim.setDuration(duration);
        slideToBoundaryAnim.setInterpolator(new DecelerateInterpolator());
        slideToBoundaryAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float currentValue = (Float) valueAnimator.getAnimatedValue();

                ballLayoutParams.x = (int) currentValue;

                updateBallPos();
            }
        });


        slideToBoundaryAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

                doIfSavePosition();
                calculateMenuPos();
                mIsMoving = false;

            }
        });
        slideToBoundaryAnim.start();
        mIsMoving = true;

    }

    private void moveBallToRight() {

        final WindowManager.LayoutParams ballLayoutParams = mBallView.getWindowLayoutParams();

        ValueAnimator mSlideToBoundaryAnim = ValueAnimator.ofFloat(ballLayoutParams.x, FloatingBallUtils.getScreenWidth()
                - ballLayoutParams.width);

        int maxDuration = 200;

        int duration = (int) (ballLayoutParams.x * maxDuration / (FloatingBallUtils
                .getScreenWidth() / 2 - ballLayoutParams.width / 2));

        if(duration < 0) duration = 0;

        mSlideToBoundaryAnim.setDuration(duration);
        mSlideToBoundaryAnim.setInterpolator(new DecelerateInterpolator());
        mSlideToBoundaryAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float currentValue = (Float) valueAnimator.getAnimatedValue();
                ballLayoutParams.x = (int) currentValue;
                updateBallPos();
            }

        });
        mSlideToBoundaryAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

                doIfSavePosition();
                calculateMenuPos();
                mIsMoving = false;
            }
        });
        mSlideToBoundaryAnim.start();
        mIsMoving = true;

    }

    private void updateBallPos() {

        mBallView.updatePosition();
    }


    public void hideToNotifyBar() {

        if (mIsBallShowing) {

            //mBallView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

            createShowNotification();

            hideBall();

            mIsBallHiding = true;

            mPreferences.put("hideState", mIsBallHiding);
        }

    }

    private void createShowNotification() {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("RelaxFinger")
                        .setTicker("悬浮球隐藏到通知栏啦，点击显示!")
                        .setContentText("点击显示悬浮球");

        Intent resultIntent = new Intent(Config.ACTION_SHOW_FLOATBALL);

        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(mContext,(int) SystemClock.uptimeMillis(), resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        Notification notification = mBuilder.build();

        notification.flags |= Notification.FLAG_NO_CLEAR;

        NotificationManager mNF = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);

        mNF.notify(R.string.app_name, notification);

    }

    public void recoveryFromNotifyBar() {

        if (!mIsLandscape) {

            mBallParams.x = mPreferences.getInt("ballWmParamsX", FloatingBallUtils.getScreenWidth() - mBallParams.width / 2);
            mBallParams.y = mPreferences.getInt("ballWmParamsY", FloatingBallUtils.getScreenHeight() / 2 - mBallParams.width / 2);
        } else {

            mBallParams.x = DensityUtil.getScreenWidth(mContext) - mBallParams.width;
            mBallParams.y = DensityUtil.getScreenHeight(mContext) / 2 - mBallParams.width / 2;
        }

        showBall();

        doIfBallRight();

        calculateMenuPos();

        closeNotify();

        mIsBallHiding = false;

        mPreferences.put("hideState", mIsBallHiding);

        mJustRecovery = true;

    }

    private void closeNotify() {

        NotificationManager mNF = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        mNF.cancel(R.string.app_name);
    }

    public void setBallTheme(String theme) {

        mBallView.setTheme(theme);
    }

    public void setBallSize(int ballSizePercent) {

        int ballSize = (int) (MIN_BALL_SIZE + (float) ((MAX_BALL_SIZE - MIN_BALL_SIZE) * ballSizePercent / 100));

        mBallView.setBallSize(ballSize);

        mPreferences.put("ballsize", ballSize);

        updateBallPos();

        doIfMoveToEdge();

    }

    public void setBallAlpha(int alphaPercent) {

        int ballAlpha = (int) (MIN_BALL_ALPHA + (float) ((MAX_BALL_ALPHA - MIN_BALL_ALPHA) * alphaPercent / 100));

        mBallView.setAlpha(ballAlpha);

        mPreferences.put("ballalpha", ballAlpha);


    }

    public void updateMenuIcon(String which) {

        mMenuViewProxy.updateMenuIcon(which);

    }

    public void showMenuFolder(List<MenuDataSugar> menuDataList) {

        mMenuViewProxy.closeMenu();
        mFolderViewProxy.showFolder(menuDataList);
    }

    public void setBallMove(boolean canMove) {

        mBallView.setMove(canMove);
    }

    public boolean isBallFreeMode() {

        return mIsFreeMode;
    }

    public boolean isExistMenuItem() {

        return mMenuViewProxy.isExistItem();
    }


    public boolean isShowHideArea() {

        return mIsShowHideArea;
    }

    @Override
    public void onBallMoveFinish() {

        if (!mIsFreeMode) {

            if (mIsShowHideArea) {

                if (checkIfHideToBar()) {

                    hideToNotifyBar();

                    closeHideArea();

                    return;
                }


                closeHideArea();
            }

            doIfBallRight();
            doIfMoveToEdge();//需要先checkIfBallRight才能判断往那边移动


        }


    }

    @Override
    public void onHomeKeyPressed() {

        closeMenu();

        mIsHomePressed = true;

        //按下系统Home键返回后需要延时5秒才能使用intent打开APP
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mIsHomePressed = false;

            }
        }).start();
    }

    @Override
    public void onRecentKeyPressed() {

        closeMenu();
    }

    private void doIfBallRight() {

        if (mBallParams.x + mBallView.getBallSize() / 2 >= FloatingBallUtils.getScreenWidth() / 2) {

            mIsBallRight = true;

        } else {

            mIsBallRight = false;
        }

        mMenuViewProxy.setIsBallRight(mIsBallRight);


        FloatingBallUtils.saveStates("floatRight", mIsBallRight);

    }

    private void doIfSavePosition() {

        if (!mIsFreeMode && !mIsLandscape) {

            FloatingBallUtils.getMultiProcessPreferences().put("ballWmParamsX", mBallParams.x);
            FloatingBallUtils.getMultiProcessPreferences().put("ballWmParamsY", mBallParams.y);
        }

    }

    private void doIfMoveToEdge() {

        if (mIsBallToEdge && !mIsFreeMode) {
            moveBallToEdge();
        } else {

            doIfSavePosition();
            calculateMenuPos();
        }
    }

    private boolean checkIfHideToBar() {

        return mBallParams.y + mBallParams.width >= FloatingBallUtils.getScreenHeight()
                - mHideAreaView.mWinLayoutParams.height ? true : false;
    }

    @Override
    public void folderItemClick(String name) {

        closeMenu();
    }

    public void setBallToEdge(boolean isToEdge) {

        mIsBallToEdge = isToEdge;

        if (mIsBallToEdge) {
            moveBallToEdge();
        }

    }

    public void setMove(boolean canMove) {

        mBallView.setMove(canMove);
    }

    public void setFloatState(boolean isShowBall) {

        if (isShowBall) {

            if (!mIsBallShowing && !mIsBallHiding) {

                showBall();
            }

        } else {

            if (mIsBallHiding) {

                closeNotify();
            }else if (mIsBallShowing){

                hideBall();
            }

        }

    }

    //屏幕截图隐藏显示悬浮球
    public void setBallHide(boolean isHideBall) {

        if (isHideBall) {

            mBallView.quickHide();
        } else {

            mBallView.quickShow();
        }
    }

    public void setKeyboardShowing(boolean isShowing){

        mIsKeyboardShowing = isShowing;
    }

    public boolean isKeyboardShowing(){

        return mIsKeyboardShowing;
    }

    public void setFloatAutoMove(boolean isFreeMode) {

        mIsFreeMode = isFreeMode;

        if (mIsFreeMode) {

            if (!mIsBallFree) {

                avoidKeyboard();

                changeBallToFree();
            }

        } else {

            if (mIsBallFree) {

                changeBallToOrigin();
            }
        }

    }

    private void changeBallToOrigin() {

        WindowManager.LayoutParams params = mBallView.getWindowLayoutParams();

        //横屏下处理方式
        if(mIsLandscape){

            int screenWidth = DensityUtil.getScreenWidth(mContext);
            int screenHeight = DensityUtil.getScreenHeight(mContext);

            params.x = screenWidth - mBallParams.width;
            params.y = screenHeight / 2 - mBallParams.width / 2;

        }else {

            params.x = mPreferences.getInt("ballWmParamsX", FloatingBallUtils.getScreenWidth() - params.width / 2);
            params.y = mPreferences.getInt("ballWmParamsY", FloatingBallUtils.getScreenHeight() / 2 - params.width / 2);
        }

        updateBallPos();

        mBallView.cancelFreeMode();

        mIsBallFree = false;

    }

    private void changeBallToFree() {

      /*  mOriginMoveState = mPreferences.getBoolean("moveSwitch", false);

        if (!mOriginMoveState) {

            mPreferences.put("moveSwitch", true);
        }

        mIsBallFree = true;*/


        mBallView.activateFreeMode();

        mIsBallFree = true;


    }

    private void avoidKeyboard() {

        WindowManager.LayoutParams params = mBallView.getWindowLayoutParams();

        int y = (int) (DensityUtil.getScreenHeight(mContext) / 2 - params.width * 1.5);

        if (params.y > y) {

            params.y = y;

            updateBallPos();
        }

    }

    public void configurationChanged(Configuration newConfig) {

        //关闭所有面板
        closeMenu();

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mIsLandscape = true;
        } else {
            mIsLandscape = false;
        }


        if (mIsShowHideArea) {

            mHideAreaView.configurationChanged();
        }

        mBackgroundView.configurationChanged();
        mFolderViewProxy.configurationChanged(mIsLandscape);

        //切换到横屏
        if (mIsLandscape) {

            changeToLandscape();


        } else {//切换到竖屏

            changeToPortrait();
        }

    }

    private void changeToPortrait() {
        if (mIsBallHiding) {

            if (!mIsPositiveHide) {//横屏自动隐藏

                recoveryFromNotifyBar();
            }

        } else {

            updateBallInPortrait();
        }
    }

    private void changeToLandscape() {
        if (mIsBallHiding) {

            mIsPositiveHide = true;

        } else {

            if (mIsLandscapeHide) {

                mIsPositiveHide = false;
                hideToNotifyBar();

            } else {

                updateBallInLandscape();
            }
        }
    }

    private void updateBallInPortrait() {

        mBallParams.x = mPreferences.getInt("ballWmParamsX", FloatingBallUtils.getScreenWidth() - mBallParams.width / 2);
        mBallParams.y = mPreferences.getInt("ballWmParamsY", FloatingBallUtils.getScreenHeight() / 2 - mBallParams.width / 2);

        updateBallPos();
        doIfBallRight();
        calculateMenuPos();
    }

    private void updateBallInLandscape() {

        int screenWidth = DensityUtil.getScreenWidth(mContext);
        int screenHeight = DensityUtil.getScreenHeight(mContext);

        mBallParams.x = screenWidth - mBallParams.width;
        mBallParams.y = screenHeight / 2 - mBallParams.width / 2;

        updateBallPos();
        doIfBallRight();
        calculateMenuPos();
    }

    public void performGestureVibrate() {

        mBallView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
    }

    public void performFeedback() {

        mBallView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
    }


    public void enableHideArea(boolean showHideArea) {

        mIsShowHideArea = showHideArea;
    }

    public void enableLandscapeHide(boolean isAutoHide) {

        mIsLandscapeHide = isAutoHide;
    }

    public void showHideArea() {

        mHideAreaView.show();
    }

    public void closeHideArea() {

        mHideAreaView.close();

    }

    public void newNotification(String pkg,int notifyId){

        NotificationInfo notify = new NotificationInfo(pkg,notifyId);

        mNotifyStack.push(notify);

        Drawable notifyIcon = AppUtils.getAppIcon(pkg);

        if(notifyIcon != null){

            mBallView.showNotification(notifyIcon);
        }
    }

    public void openNotification(){

        NotificationInfo notify = mNotifyStack.pop();

        Intent intent = new Intent(mContext, NotificationService.class);

        intent.putExtra("what",Config.OPEN_NOTIFICATION);

        intent.putExtra("notifyId",notify.getId());

        mContext.startService(intent);
    }


    public void showNextNotify() {

        if(hasNotification()){

            NotificationInfo notify = mNotifyStack.getTop();

            Drawable notifyIcon = AppUtils.getAppIcon(notify.getPkg());

            if(notifyIcon != null){

                mBallView.showNotification(notifyIcon);
            }

        }else {

            mBallView.clearNotification();
        }
    }

    public boolean hasNotification(){

        return !mNotifyStack.isEmpty();
    }

    public void ignoreNotification() {

        mNotifyStack.pop();
    }

    public void ignoreAllNotification() {

        mNotifyStack.clear();

        mBallView.clearNotification();
    }

    public void cancelNotification(int id){

        NotificationInfo info = mNotifyStack.getTop();

        if( info != null && info.getId() == id){

            mNotifyStack.pop();

            showNextNotify();

        }else {

            mNotifyStack.invalidNotification(id);
        }

    }

    public void checkHideInApp(String foregroundAppPkg) {

        if(foregroundAppPkg.equals("com.android.systemui")){

            mPrevNormalPkg = foregroundAppPkg;

            mJustRecovery = false;

            return;
        }



        if(mHidePkgList.contains(foregroundAppPkg)){


            if(mJustRecovery && mPrevNormalPkg.equals("com.android.systemui") &&  mPrevHidePkg.equals(foregroundAppPkg)){

                mJustRecovery = false;

                return;
            }


         if(!mIsBallHiding){

                hideToNotifyBar();

                mIsHideInApp = true;
            }

            mPrevHidePkg = foregroundAppPkg;


        }else{

            if(mIsBallHiding && mIsHideInApp){

                recoveryFromNotifyBar();

                mIsHideInApp = false;
            }

            mPrevNormalPkg = foregroundAppPkg;

        }

        mJustRecovery = false;
    }

    public void setAvoidKeyboard(boolean isAvoid) {

        mIsAvoidKeyboard = isAvoid;
    }

    public boolean isAvoidKeyboard(){

        return mIsAvoidKeyboard;
    }
}
