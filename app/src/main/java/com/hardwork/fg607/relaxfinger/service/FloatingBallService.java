package com.hardwork.fg607.relaxfinger.service;

/**
 * Created by fg607 on 15-8-20.
 */

import android.accessibilityservice.AccessibilityServiceInfo;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hardwork.fg607.relaxfinger.MyApplication;
import com.hardwork.fg607.relaxfinger.R;
import com.hardwork.fg607.relaxfinger.SettingActivity;
import com.hardwork.fg607.relaxfinger.utils.AnimatorUtils;
import com.hardwork.fg607.relaxfinger.utils.AppUtils;
import com.hardwork.fg607.relaxfinger.utils.Config;
import com.hardwork.fg607.relaxfinger.utils.DensityUtil;
import com.hardwork.fg607.relaxfinger.utils.FloatingBallUtils;
import com.ogaclejapan.arclayout.Arc;
import com.ogaclejapan.arclayout.ArcLayout;

import net.grandcentrix.tray.TrayAppPreferences;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FloatingBallService extends Service implements View.OnClickListener {

    private WindowManager mWindowManager = null;
    private WindowManager.LayoutParams mBallWmParams = null;
    private WindowManager.LayoutParams mMenuWmParams = null;
    private WindowManager.LayoutParams mTrackWmParams = null;
    private View mBallView;
    private View mMenuView;
    private int mOldOffsetX, mOldOffsetY, mNewOffsetX, mNewOffsetY;
    private int mTag;
    private FrameLayout mMenuLayout;
    private Button mFloatImage;
    private boolean mIsmoving = false;
    private boolean mCanmove = false;
    private boolean mIsToEdge = false;
    private Notification mNotification = null;
    private boolean mIsAdd;
    private boolean mIsMenuAdd;
    private boolean mIsTrackAdd;
    private int mClickCount;
    public static final long CLICK_SPACING_TIME = 200;//双击间隔时间
    public static final long LONG_PRESS_TIME = 300;
    public static final int TRANSPARENT = 150;
    public static final int MIN_BALL_SIZE = FloatingBallUtils.getScreenWidth()/10;
    public static final int MAX_BALL_SIZE = FloatingBallUtils.getScreenWidth()/7;
    public static final int MENU_WINDOW_WIDTH = DensityUtil.dip2px(MyApplication.getApplication(),150);
    public static final int MENU_WINDOW_HEIGHT = DensityUtil.dip2px(MyApplication.getApplication(),280);;
    private int floatBallSize;
    private Handler mHandler;
    private LongPressedThread mLongPressedThread;
    private ClickPressedThread mClickPressedThread;
    private ShowPopMenuThread mShowPopMenuThread;
    private HidePopMenuThread mHidePopMenuThread;
    private long mPreClickTime;
    public int transparent;
    private Button mFab;
    private ArrayList<String> mCurrentFuncList = new ArrayList<>();
    private boolean mLongPressing;
    private View mTrackView;
    private Button mTrackImage;
    private NavAccessibilityService mAccessibilityService;
    private AccessibilityManager mAccessibilityManger;
    private List<AccessibilityServiceInfo> mList;
    private int mTouchX, mTouchY, mCurrentX, mCurrentY;
    private ImageView mTeachArrow;
    private ImageView mTeachHand;
    private TextView mTeachText;
    private WindowManager.LayoutParams mArrowWmParams = null;
    private WindowManager.LayoutParams mHandWmParams = null;
    private WindowManager.LayoutParams mTextWmParams = null;
    private boolean mIsShowTeaching = false;
    private boolean mStopTeaching;
    private boolean mRemoveTeachText = false;
    private TrayAppPreferences mPreferences;
    private boolean mIsVibrate = true;
    private FrameLayout mFloatLayout;
    private ArcLayout mArcLayout;
    private int mAppNumber = 0;
    private boolean[] mIsAppExist = new boolean[5];
    private boolean mIsFloatRight = true;
    private boolean mIsCanPopup = true;
    @Override
    public void onCreate() {
        super.onCreate();

        mHandler = new Handler();

        mPreferences = FloatingBallUtils.getMultiProcessPreferences();

        mCanmove = mPreferences.getBoolean("moveSwitch", false);
        
        mIsToEdge = mPreferences.getBoolean("toEdgeSwitch", false);

        floatBallSize = mPreferences.getInt("ballsize", (MIN_BALL_SIZE + MAX_BALL_SIZE) / 2);

        mIsVibrate = mPreferences.getBoolean("isVibrate", true);

        mTag = 0;
        mIsAdd = false;
        mIsMenuAdd=false;
        mIsTrackAdd = false;
        mClickCount = 0;

        mPreClickTime = 0;

        //加载悬浮球布局
        initFloatView();

        createFloatBallView();

        createTrackView();

        startAccessibility();

        createNotification();

        loadFunction();

        setUpFloatMenuView();

    }

    private void loadFunction() {

        if (mCurrentFuncList.size() == 0) {
            mCurrentFuncList.add(mPreferences.getString("click", "返回键"));
            mCurrentFuncList.add(mPreferences.getString("doubleClick", "最近任务键"));
            mCurrentFuncList.add(mPreferences.getString("longPress", "移动(固定)悬浮球"));
            mCurrentFuncList.add(mPreferences.getString("swipeUp", "通知栏"));
            mCurrentFuncList.add(mPreferences.getString("swipeDown", "Home键"));
            mCurrentFuncList.add(mPreferences.getString("swipeLeft", "快捷应用"));
            mCurrentFuncList.add(mPreferences.getString("swipeRight", "快速设置"));
        } else {
            mCurrentFuncList.set(0, mPreferences.getString("click", "返回键"));
            mCurrentFuncList.set(1, mPreferences.getString("doubleClick", "最近任务键"));
            mCurrentFuncList.set(2, mPreferences.getString("longPress", "移动(固定)悬浮球"));
            mCurrentFuncList.set(3, mPreferences.getString("swipeUp", "通知栏"));
            mCurrentFuncList.set(4, mPreferences.getString("swipeDown", "Home键"));
            mCurrentFuncList.set(5, mPreferences.getString("swipeLeft", "快捷应用"));
            mCurrentFuncList.set(6, mPreferences.getString("swipeRight", "快速设置"));
        }
    }

    private void createNotification() {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.nor)
                        .setContentTitle("Relax Finger")
                        .setOngoing(true)
                        .setContentText("点击进入设置");

        Intent resultIntent = new Intent(this, SettingActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(SettingActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        startForeground(0x112, mBuilder.build());
    }

    private void initFloatView() {
        mBallView = LayoutInflater.from(this).inflate(R.layout.floatball, null);
        mFloatImage = (Button) mBallView.findViewById(R.id.float_image);
        mTrackView = LayoutInflater.from(this).inflate(R.layout.track, null);
        mTrackImage = (Button) mTrackView.findViewById(R.id.track_image);

        transparent = TRANSPARENT;
        mFloatImage.getBackground().setAlpha(transparent);
    }

    private void startAccessibility() {

        if (mAccessibilityService.instance == null) {

            Intent intent = new Intent(this, NavAccessibilityService.class);
            startService(intent);
        }

        if(!checkAccessibility()){

            openSettingActivity();
        }


    }

    private void openSettingActivity() {

        Intent intent = new Intent(this,SettingActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private boolean checkAccessibility(){

        mAccessibilityManger = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);

        mList = mAccessibilityManger.getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);

        for (int i = 0; i < mList.size(); i++) {
            if ("com.hardwork.fg607.relaxfinger/.service.NavAccessibilityService".equals(mList.get(i).getId())) {
              return true;
            }
        }

        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) {

            showFloatBall();

        } else {

            switch (intent.getIntExtra("what", -1)) {

                case Config.TO_EDGE_SWITCH:
                    setToEdge(intent.getBooleanExtra("isToEdge", false));
                    break;
                case Config.GESTURE_FUNCTION:
                    if (intent.getBooleanExtra("loadfunction", false)) {

                        Log.i("悬浮窗","function");
                        loadFunction();
                    }
                    break;
                case Config.MOVE_SWITCH:
                    setMove(intent.getBooleanExtra("canmove",false));
                    break;
                case Config.VIBRATOR_SWITCH:
                    setVibrator(intent.getBooleanExtra("isVibrate",true));
                    break;
                case Config.FLOAT_SWITCH:
                    setFloatState(intent.getBooleanExtra("ballstate",false));
                    break;
                case Config.BALL_SIZE:
                    setBallSize(intent.getIntExtra("ballsize",1));
                    break;
                case Config.UPDATE_APP:
                    String which = intent.getStringExtra("which");
                    if(which!= null){
                        updateMenuIcons(which);
                    }

                    break;
                case Config.SHOW_TEACHING:
                    //showTeaching();
                    break;
                default:
                    break;
            }
        }


        flags = START_STICKY;

        return super.onStartCommand(intent, flags, startId);
    }

    private void setVibrator(boolean isVibrator) {

        mIsVibrate = isVibrator;
    }

    private void setBallSize(int ballSizePercent) {


            floatBallSize = (int)(MIN_BALL_SIZE + (float)((MAX_BALL_SIZE - MIN_BALL_SIZE) * ballSizePercent/ 100));

            saveStates("ballsize", floatBallSize);

            updateFloatBall();

            if (mIsToEdge) {

                moveToScreenEdge();
            }

    }

    private void setFloatState(boolean ballstate) {

        if(ballstate){
            showFloatBall();
        }else {
            closeFloatBall();
            stopSelf();
        }
    }

    private void setMove(boolean canmove) {

        mCanmove = canmove;
    }

    private void setToEdge(boolean isToEdge) {

        mIsToEdge = isToEdge;

        if (mIsToEdge) {

            moveToScreenEdge();
        }
    }


/**
 * 窗口菜单初始化
 */
    private void setUpFloatMenuView(){

        mShowPopMenuThread = new ShowPopMenuThread();
        mHidePopMenuThread = new HidePopMenuThread();

        if(mIsFloatRight){
            mMenuView = LayoutInflater.from(this).inflate(R.layout.popup, null,false);
        }else {
            mMenuView = LayoutInflater.from(this).inflate(R.layout.popup_left, null);
        }

        mMenuLayout = (FrameLayout) mMenuView.findViewById(R.id.menu_layout);
        updateMenuIcons();
        mArcLayout = (ArcLayout) mMenuView.findViewById(R.id.arc_layout);
        mFab = (Button) mMenuView.findViewById(R.id.fab);


        for (int i = 0, size = mArcLayout.getChildCount(); i < size; i++) {
            mArcLayout.getChildAt(i).setOnClickListener(this);
        }

        mFab.setOnClickListener(this);

        mMenuWmParams = new WindowManager.LayoutParams();

        if (Build.VERSION.SDK_INT < 19) {

            mMenuWmParams.type = WindowManager.LayoutParams.TYPE_PHONE;

        }else {

            mMenuWmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        }
        mMenuWmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;//| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
        mMenuWmParams.gravity = Gravity.LEFT | Gravity.TOP;



        mMenuWmParams.width = MENU_WINDOW_WIDTH;
        mMenuWmParams.height = MENU_WINDOW_HEIGHT;
        mMenuWmParams.format = PixelFormat.RGBA_8888;
    }

    private void initLeftPopup(){

        mArcLayout.setArc(Arc.LEFT);


    }

    private void initRightPopup(){



    }
    /**
     * 显示功能键面板
     */
    private void showMenu() {

        mMenuLayout.setVisibility(View.VISIBLE);
        List<Animator> animList = new ArrayList<>();

        for (int i = 0, len = mArcLayout.getChildCount(); i < len; i++) {
            animList.add(createShowItemAnimator(mArcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new OvershootInterpolator());
        animSet.playTogether(animList);
        animSet.start();
    }


    /**
     * 隐藏功能键面板
     */
    private void hideMenu() {

        List<Animator> animList = new ArrayList<>();

        for (int i = mArcLayout.getChildCount() - 1; i >= 0; i--) {
            animList.add(createHideItemAnimator(mArcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new AnticipateInterpolator());
        animSet.playTogether(animList);
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mMenuLayout.setVisibility(View.INVISIBLE);
            }
        });
        animSet.start();

    }

    /**
     * 生成显示功能键动画
     * @param item
     * @return
     */
    private Animator createShowItemAnimator(View item) {

        float dx = mFab.getX() - item.getX();
        float dy = mFab.getY() - item.getY();

        item.setRotation(0f);
        item.setTranslationX(dx);
        item.setTranslationY(dy);

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(0f, 720f),
                AnimatorUtils.translationX(dx, 0f),
                AnimatorUtils.translationY(dy, 0f)
        );

        return anim;
    }

    /**
     * 生成隐藏功能键动画
     * @param item
     * @return
     */
    private Animator createHideItemAnimator(final View item) {
        float dx = mFab.getX() - item.getX();
        float dy = mFab.getY() - item.getY();

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(720f, 0f),
                AnimatorUtils.translationX(0f, dx),
                AnimatorUtils.translationY(0f, dy)
        );

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                item.setTranslationX(0f);
                item.setTranslationY(0f);
            }
        });

        return anim;
    }
    /**
     * 弹出popmenu动画线程
     */
    public class ShowPopMenuThread implements Runnable {

        @Override
        public void run() {
            showMenu();
        }
    }
    /**
     * 隐藏popmenu动画线程
     */
    public class HidePopMenuThread implements Runnable {
        @Override
        public void run() {

            if(mIsMenuAdd){

                mWindowManager.removeView(mMenuView);
            }


            mIsMenuAdd= false;
        }
    }

    /**
     * 长按线程
     */
    public class LongPressedThread implements Runnable {

        @Override

        public void run() {

            if(mIsVibrate){

                mBallView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            }

            //长按悬浮球事件
            onFloatBallLongPressed();

            mClickCount = 0;

        }

    }

    /**
     * 点击线程
     */
    public class ClickPressedThread implements Runnable {

        @Override

        public void run() {


            if (mClickCount == 1) {
                //单击悬浮球
                onFloatBallClick();
            } else if (mClickCount == 2) {

                //双击悬浮球
                onFloatBallDoubleClick();
            }
            mClickCount = 0;


        }

    }

    /**
     * 创建ＦloatBallView，并初始化显示参数
     */
    private void createFloatBallView() {


        //设置悬浮窗口参数
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        mBallWmParams = new WindowManager.LayoutParams();

        if (Build.VERSION.SDK_INT < 19) {

            mBallWmParams.type = WindowManager.LayoutParams.TYPE_PHONE;

        }else {

            mBallWmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        }
        mBallWmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;//| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
        mBallWmParams.gravity = Gravity.LEFT | Gravity.TOP;

        mBallWmParams.x = mPreferences.getInt("ballWmParamsX", FloatingBallUtils.getScreenWidth()/2-floatBallSize/2);
        mBallWmParams.y = mPreferences.getInt("ballWmParamsY", FloatingBallUtils.getScreenHeight()/2-floatBallSize/2);

        mBallWmParams.width = floatBallSize;
        mBallWmParams.height = floatBallSize;
        mBallWmParams.format = PixelFormat.RGBA_8888;

        //注册触摸事件监听器
        mFloatImage.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                if (mTag == 0) {
                    mOldOffsetX = mBallWmParams.x;
                    mOldOffsetY = mBallWmParams.y;
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mFloatImage.setPressed(true);
                        mFloatImage.getBackground().setAlpha(255);
                        mTouchX = (int) event.getRawX();
                        mTouchY = (int) event.getRawY();
                        mClickCount++;
                        mPreClickTime = System.currentTimeMillis();

                        //移除双击检测线程
                        if (mClickPressedThread != null) {
                            mHandler.removeCallbacks(mClickPressedThread);
                        }

                            mLongPressedThread = new LongPressedThread();
                            mHandler.postDelayed(mLongPressedThread, LONG_PRESS_TIME);
                            mLongPressing = true;

                        mIsmoving = false;
                            showTrack();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        mCurrentX = (int) event.getRawX();
                        mCurrentY = (int) event.getRawY();
                        mIsmoving = true;
                        mTag = 1;
                        mBallWmParams.x += (int) (mCurrentX - mTouchX);
                        mBallWmParams.y += (int) (mCurrentY - mTouchY);

                            //滑动量大于50像素取消长按事件
                            if (Math.abs(mOldOffsetX - mBallWmParams.x) > 50 || Math.abs(mOldOffsetY - mBallWmParams.y) > 50) {
                                //取消注册的长按事件
                                mHandler.removeCallbacks(mLongPressedThread);
                                mLongPressing = false;
                            }

                        //更新悬浮球位置，保存位置
                        if (mCanmove) {
                            if (mBallWmParams.x > FloatingBallUtils.getScreenWidth() - mBallWmParams.width) {
                                mBallWmParams.x = FloatingBallUtils.getScreenWidth() - mBallWmParams.width;
                            }
                            if (mBallWmParams.y > FloatingBallUtils.getScreenHeight() - mBallWmParams.height) {

                                mBallWmParams.y = FloatingBallUtils.getScreenHeight() - mBallWmParams.height;
                            }
                            updateViewPosition();

                        } else {

                            mTrackWmParams.x += (int) (mCurrentX - mTouchX) / 2;
                            mTrackWmParams.y += (int) (mCurrentY - mTouchY) / 2;

                            if (Math.abs(mTrackWmParams.x - (mOldOffsetX + (mBallWmParams.width / 2 - floatBallSize * 3 / 5 / 2))) < 200
                                    && Math.abs(mTrackWmParams.y - (mOldOffsetY + (mBallWmParams.height / 2 - floatBallSize * 3 / 5 / 2))) < 200) {

                                updateTrackPositon();
                            }


                        }
                        mTouchX = mCurrentX;
                        mTouchY = mCurrentY;
                        break;
                    case MotionEvent.ACTION_UP:
                        mFloatImage.setPressed(false);
                        mFloatImage.getBackground().setAlpha(transparent);

                        mTag = 0;

                        hideTrack();
                        mNewOffsetX = mBallWmParams.x;
                        mNewOffsetY = mBallWmParams.y;

                        // 滑动偏移量小于40像素，判定为点击悬浮球
                        if (Math.abs(mOldOffsetX - mNewOffsetX) <= 40 && Math.abs(mOldOffsetY - mNewOffsetY) <= 40) {


                            if (System.currentTimeMillis() - mPreClickTime <= LONG_PRESS_TIME) {

                                //取消注册的长按事件

                                mHandler.removeCallbacks(mLongPressedThread);

                                mClickPressedThread = new ClickPressedThread();
                                mHandler.postDelayed(mClickPressedThread, CLICK_SPACING_TIME);
                            }

                            onClearOffset();//清楚滑动偏移量
                        } else if (mCanmove) {

                            mClickCount = 0;
                        }

                        if (mCanmove) {

                            if (mIsToEdge) {

                                moveToScreenEdge();

                            } else {

                                saveStates("ballWmParamsX", mBallWmParams.x);
                                saveStates("ballWmParamsY", mBallWmParams.y);
                            }

                            if(mBallWmParams.x + mBallWmParams.width/2 >= FloatingBallUtils.getScreenWidth()/2){

                                mIsFloatRight = true;
                            }else {

                                mIsFloatRight = false;
                            }

                            if(mBallWmParams.y > FloatingBallUtils.getStatusBarHeight(FloatingBallService.this) + (MENU_WINDOW_HEIGHT/2-mBallWmParams.height/2)){

                                mIsCanPopup = true;

                            }else {

                                mIsCanPopup = false;
                            }

                            setUpFloatMenuView();

                            mCanmove = false;
                            saveStates("moveSwitch", mCanmove);


                        } else {

                          //  hideTrack();

                            mTrackWmParams.x = mOldOffsetX + (mBallWmParams.width / 2 - floatBallSize * 3 / 5 / 2);
                            mTrackWmParams.y = mOldOffsetY + (mBallWmParams.height / 2 - floatBallSize * 3 / 5 / 2);


                            if (!mLongPressing) {

                                //Y轴滑动偏移量大于40像素并且Y轴滑动偏移量比X轴偏移量多出20像素时判定为向上滑动
                                if ((mOldOffsetY - mNewOffsetY) - Math.abs(mOldOffsetX - mNewOffsetX) > 20 && (mOldOffsetY - mNewOffsetY) > 40) {

                                    mClickCount = 0;
                                    onFloatBallFlipUp();

                                }
                                //向下滑动
                                else if ((mNewOffsetY - mOldOffsetY) - Math.abs(mOldOffsetX - mNewOffsetX) > 20 && (mNewOffsetY - mOldOffsetY) > 40) {

                                    mClickCount = 0;
                                    onFloatBallFlipDown();

                                }
                                //向左滑动
                                else if ((mOldOffsetX - mNewOffsetX) - Math.abs(mOldOffsetY - mNewOffsetY) > 20 && (mOldOffsetX - mNewOffsetX) > 40) {

                                    mClickCount = 0;
                                    onFloatBallFlipLeft();

                                }
                                //向右滑动
                                else if ((mNewOffsetX - mOldOffsetX) - Math.abs(mOldOffsetY - mNewOffsetY) > 10 && (mNewOffsetX - mOldOffsetX) > 10) {

                                    mClickCount = 0;
                                    onFloatBallFlipRight();

                                }


                            }

                            onClearOffset();

                        }
                        break;
                }
                //如果拖动则返回false，否则返回true
                if (mIsmoving == false) {
                    return false;
                } else {
                    return true;
                }
            }

        });

    }

    private void moveToScreenEdge() {

        if (mBallWmParams.x + mBallWmParams.width / 2 >= FloatingBallUtils.getScreenWidth() / 2) {

            moveToScreenRight();
        } else {

            moveToScreenLeft();
        }
    }

    private void moveToScreenLeft() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (mBallWmParams.x != 0) {

                    mBallWmParams.x -= 5;

                    if (mBallWmParams.x < 0) {

                        mBallWmParams.x = 0;
                    }

                    MyApplication.getMainThreadHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            updateViewPosition();
                        }
                    });

                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                saveStates("ballWmParamsX", mBallWmParams.x);
                saveStates("ballWmParamsY", mBallWmParams.y);
            }
        }).start();
    }

    private void moveToScreenRight() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (mBallWmParams.x != FloatingBallUtils.getScreenWidth() - mBallWmParams.width) {

                    mBallWmParams.x += 5;

                    if (mBallWmParams.x > FloatingBallUtils.getScreenWidth() - mBallWmParams.width) {

                        mBallWmParams.x = FloatingBallUtils.getScreenWidth() - mBallWmParams.width;
                    }
                    MyApplication.getMainThreadHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            updateViewPosition();
                        }
                    });

                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                saveStates("ballWmParamsX", mBallWmParams.x);
                saveStates("ballWmParamsY", mBallWmParams.y);
            }
        }).start();

    }


    private void createTrackView() {

        //设置悬浮窗口参数
        mTrackWmParams = new WindowManager.LayoutParams();

        if (Build.VERSION.SDK_INT < 19) {

            mTrackWmParams.type = WindowManager.LayoutParams.TYPE_PHONE;

        }else {

            mTrackWmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        }
        mTrackWmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mTrackWmParams.gravity = Gravity.LEFT | Gravity.TOP;

        updateTrack();

        mTrackWmParams.format = PixelFormat.RGBA_8888;


    }

    /**
     * 将状态数据保存在sharepreferences
     * @param name
     * @param number
     */
    public void saveStates(String name, int number) {
        mPreferences.put(name, number);

    }

    public void saveStates(String name, boolean isChecked) {

        mPreferences.put(name,isChecked);
    }

    /**
     * 清空滑动位移量
     */
    private void onClearOffset() {
        mBallWmParams.x = mOldOffsetX;
        mBallWmParams.y = mOldOffsetY;
    }

    /**
     * 选择触发的功能
     */
    private void chooseFunction(String action) {

        if (mAccessibilityService.instance == null) {

            startAccessibility();
            return;
        }

        if(mRemoveTeachText){

            mWindowManager.removeView(mTeachText);

            mRemoveTeachText = false;
        }

        switch (action) {
            case "移动(固定)悬浮球":
                mCanmove = true;
                saveStates("moveSwitch", mCanmove);
                break;
            case "快捷应用":
                if(mAppNumber > 0){

                    if(mIsCanPopup){
                        popUpMenu();
                    }else {

                        Toast.makeText(this,"空间不足，向下移动悬浮球再试！",Toast.LENGTH_SHORT).show();
                    }

                }else {
                    showAlertDialog();
                }

                break;
            case "返回键":
                FloatingBallUtils.keyBack(mAccessibilityService.instance);
                break;
            case "Home键":
                FloatingBallUtils.keyHome(mAccessibilityService.instance);
                break;
            case "最近任务键":
                FloatingBallUtils.openRecnetTask(mAccessibilityService.instance);
                break;
            case "休眠(需要开启锁屏功能)":
                FloatingBallUtils.lockScreen(this);
                break;
            case "电源面板":
                FloatingBallUtils.openPowerDialog(mAccessibilityService.instance);
                break;
            case "快速设置":
                FloatingBallUtils.openQuickSetting(mAccessibilityService.instance);
                break;
            case "菜单键":
                FloatingBallUtils.keyMenu();
                break;
            case "通知栏":
                FloatingBallUtils.openNotificationBar(mAccessibilityService.instance);
                break;
            case "重启":
                FloatingBallUtils.reboot();
                break;
            case "关机":
                FloatingBallUtils.shutdown();
                break;
            case "音量键加":
                FloatingBallUtils.volumeUp();
                break;
            case "音量键减":
                FloatingBallUtils.vloumeDown();
                break;
            default:
                break;

        }


    }

    private void showAlertDialog() {

        Toast.makeText(this,"还没有设置快捷应用！",Toast.LENGTH_SHORT).show();
    }

    private void startSetUpActivity() {

        Intent intent = new Intent(this, SettingActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private boolean isAccessibilityEabled() {


        mList = mAccessibilityManger.getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);

        for (int i = 0; i < mList.size(); i++) {
            if ("com.hardwork.fg607.relaxfinger/.service.NavAccessibilityService".equals(mList.get(i).getId())) {
                return true;
            }
        }

        return false;
    }

    /**
     * 长按悬浮球
     */
    private void onFloatBallLongPressed() {

        chooseFunction(mCurrentFuncList.get(2));
    }

    /**
     * 点击悬浮球
     */

    private void onFloatBallClick() {

        chooseFunction(mCurrentFuncList.get(0));
    }

    /**
     * 双击悬浮球
     */
    private void onFloatBallDoubleClick() {

        chooseFunction(mCurrentFuncList.get(1));

    }

    /**
     * 向上滑动悬浮球
     */
    private void onFloatBallFlipUp() {

        chooseFunction(mCurrentFuncList.get(3));
    }

    /**
     * 向下滑动悬浮球
     */
    private void onFloatBallFlipDown() {
        chooseFunction(mCurrentFuncList.get(4));
    }

    /**
     * 向左滑动悬浮球
     */
    private void onFloatBallFlipLeft() {

        if(mIsShowTeaching){

            mStopTeaching = true;
        }
        chooseFunction(mCurrentFuncList.get(5));

    }

    /**
     * 向右滑动悬浮球
     */
    private void onFloatBallFlipRight() {

        chooseFunction(mCurrentFuncList.get(6));
    }


    /**
     * 弹出功能菜单
     */
    private  void popUpMenu() {

        mBallWmParams.x = mPreferences.getInt("ballWmParamsX", FloatingBallUtils.getScreenWidth()/2-floatBallSize/2);
        mBallWmParams.y = mPreferences.getInt("ballWmParamsY", FloatingBallUtils.getScreenHeight()/2-floatBallSize/2);

        int offsetX,offsetY;

        if(mIsFloatRight){

            offsetX = -(MENU_WINDOW_WIDTH-mBallWmParams.width);

        }else {

            offsetX = 0;
        }

        offsetY = -(MENU_WINDOW_HEIGHT/2-mBallWmParams.height/2);


        mMenuWmParams.x = mBallWmParams.x+offsetX;
        mMenuWmParams.y = mBallWmParams.y+offsetY;


        if(!mIsMenuAdd){
            mWindowManager.addView(mMenuView, mMenuWmParams);
        }

        mIsMenuAdd = true;


        //弹出面板后延迟100ms开始播放功能键显示动画
        mHandler.postDelayed(mShowPopMenuThread, 100);
    }


    /**
     * 更新悬浮球的显示位置
     */
    private void updateViewPosition() {

        if (mIsAdd) {
            mWindowManager.updateViewLayout(mBallView, mBallWmParams);
            mTrackWmParams.x = mBallWmParams.x + (mBallWmParams.width / 2 - mTrackWmParams.width / 2);
            mTrackWmParams.y = mBallWmParams.y + (mBallWmParams.height / 2 - mTrackWmParams.height / 2);

            updateTrackPositon();
        }

    }

    private void updateTrackPositon() {

        if(mIsTrackAdd){
            mWindowManager.updateViewLayout(mTrackView, mTrackWmParams);
        }

    }

    /**
     * 更新功能键图标
     */
    public void updateMenuIcons()
    {
        updateMenuIcons("1");
        updateMenuIcons("2");
        updateMenuIcons("3");
        updateMenuIcons("4");
        updateMenuIcons("5");


    }
    public void updateMenuIcons(String which){

        CircleImageView imageView = null;
        switch (which){

            case "1":
                imageView = (CircleImageView) mMenuView.findViewById(R.id.menuA);
                break;
            case "2":
                imageView = (CircleImageView) mMenuView.findViewById(R.id.menuB);
                break;
            case "3":
                imageView = (CircleImageView) mMenuView.findViewById(R.id.menuC);
                break;
            case "4":
                imageView = (CircleImageView) mMenuView.findViewById(R.id.menuD);
                break;
            case "5":
                imageView = (CircleImageView) mMenuView.findViewById(R.id.menuE);
                break;
            default:
                break;
        }

        if(imageView != null){

            updateViewIcon(imageView,which);
        }

    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 处理点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.fab:
                closeMenu();
                break;
            case R.id.menuA:
                menuClick("1");
                break;
            case R.id.menuB:
                menuClick("2");
                break;
            case R.id.menuC:
                menuClick("3");
                break;
            case R.id.menuD:
                menuClick("4");
                break;
            case R.id.menuE:
                menuClick("5");
                break;
            default:
                closeMenu();
                break;
        }

    }

    /**
     * 点击功能键
     * @param whichApp
     */
    private void menuClick(String whichApp) {

        String packageName = mPreferences.getString("app"+whichApp,"");

        if(packageName != ""){

            boolean isOpen = AppUtils.startApplication(packageName);

            if(!isOpen){

                Toast.makeText(this,"应用程序已卸载！",Toast.LENGTH_SHORT).show();
                mPreferences.put("app"+whichApp,"");
                updateMenuIcons(whichApp);
            }
        }

        closeMenu();

    }

    /**
     * 更新悬浮球图标
     */
    private void updateBallIcon() {
        String menuName = mPreferences.getString("currentfunction", null);
        updateViewIcon(mFloatImage, menuName);
        mFloatImage.getBackground().setAlpha(transparent);

    }

    /**
     * 更新功能键图标
     * @param view
     * @param which
     */
    private void updateViewIcon(View view, String which) {

        Drawable drawable = AppUtils.getAppIcon(mPreferences.getString("app"+which,""));


        if(drawable != null){

            if(view instanceof CircleImageView){

                CircleImageView circleImageView = (CircleImageView)view;
                circleImageView.setImageDrawable(drawable);

                if(!mIsAppExist[Integer.parseInt(which)-1]){

                    mAppNumber++;

                    mIsAppExist[Integer.parseInt(which)-1] = true;
                }

            }
        }else {

            if(view instanceof CircleImageView){

                CircleImageView circleImageView = (CircleImageView)view;


                circleImageView.setImageDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                if(mIsAppExist[Integer.parseInt(which)-1]){

                    mIsAppExist[Integer.parseInt(which)-1] = false;
                    mAppNumber--;
                }

            }
        }
    }

    /**
     * 关闭功能键面板
     */
    private void closeMenu() {

        if(mIsMenuAdd){

            hideMenu();
            mHandler.postDelayed(mHidePopMenuThread,500);
        }

    }

    /**
     * 关闭悬浮球
     */
    public void closeFloatBall() {
        if (mIsAdd) {
            mWindowManager.removeView(mBallView);
            mIsAdd = !mIsAdd;
        }


    }

    /**
     * 显示悬浮球
     */
    public void showFloatBall() {

        if (!mIsAdd) {
            mWindowManager.addView(mBallView, mBallWmParams);
            mIsAdd = !mIsAdd;
        }

    }

    public void showTrack() {

        if(!mIsTrackAdd){
            mWindowManager.addView(mTrackView, mTrackWmParams);
            mIsTrackAdd = !mIsTrackAdd;
        }

    }

    public void hideTrack() {

        if(mIsTrackAdd){

            mWindowManager.removeView(mTrackView);
            mIsTrackAdd = !mIsTrackAdd;
        }

    }

    /**
     * 更新悬浮球
     */
    public void updateFloatBall() {

        mBallWmParams.width = floatBallSize;
        mBallWmParams.height = floatBallSize;

        checkOutEdge();

        updateTrack();

        if (mIsAdd) {

            mWindowManager.updateViewLayout(mBallView, mBallWmParams);

        }


    }

    public void updateTrack() {

        mTrackWmParams.width = floatBallSize * 3 / 5;
        mTrackWmParams.height = floatBallSize * 3 / 5;

        mTrackWmParams.x = mBallWmParams.x + (mBallWmParams.width / 2 - mTrackWmParams.width / 2);
        mTrackWmParams.y = mBallWmParams.y + (mBallWmParams.height / 2 - mTrackWmParams.height / 2);
    }

    private void checkOutEdge() {

        if (mBallWmParams.x + mBallWmParams.width > FloatingBallUtils.getScreenWidth()) {

            mBallWmParams.x = FloatingBallUtils.getScreenWidth() - mBallWmParams.width;
        }

        if (mBallWmParams.y + mBallWmParams.height > FloatingBallUtils.getScreenHeight()) {

            mBallWmParams.y = FloatingBallUtils.getScreenHeight() - mBallWmParams.height;
        }
    }

    @Override
    public void onDestroy() {

        closeFloatBall();

        //销毁时停止前台
        stopForeground(true);

        super.onDestroy();
    }

    public void initTeaching(){

        mTeachArrow = new ImageView(this);
        mTeachHand = new ImageView(this);
        mTeachText = new TextView(this);
        mTeachText.setTextColor(Color.rgb(56, 172, 190));
        mTeachText.setTextSize(16);
        mTeachText.setText("向左滑动打开'最近任务面板'");
        mTeachText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mTeachText.setGravity(Gravity.CENTER);

        mTeachArrow.setImageResource(R.drawable.teaching_arrow);
        mTeachHand.setImageResource(R.drawable.teaching_hand);

        mArrowWmParams = new WindowManager.LayoutParams();
        mHandWmParams = new WindowManager.LayoutParams();
        mTextWmParams = new WindowManager.LayoutParams();

        if (Build.VERSION.SDK_INT < 19) {

            mArrowWmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            mHandWmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            mTextWmParams.type = WindowManager.LayoutParams.TYPE_PHONE;

        }else {

            mArrowWmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            mHandWmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            mTextWmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        }

        mArrowWmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mArrowWmParams.gravity = Gravity.LEFT | Gravity.TOP;

        mHandWmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mHandWmParams.gravity = Gravity.LEFT | Gravity.TOP;

        mTextWmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mTextWmParams.gravity = Gravity.LEFT | Gravity.TOP;


        mArrowWmParams.width = floatBallSize*3/2;
        mArrowWmParams.height = 100;

        mArrowWmParams.x = FloatingBallUtils.getScreenWidth()/2-mArrowWmParams.width;
        mArrowWmParams.y = FloatingBallUtils.getScreenHeight()/2-mArrowWmParams.height/2;
        mArrowWmParams.format = PixelFormat.RGBA_8888;


        mHandWmParams.width = floatBallSize;
        mHandWmParams.height = floatBallSize;

        mHandWmParams.x = FloatingBallUtils.getScreenWidth()/2;
        mHandWmParams.y = FloatingBallUtils.getScreenHeight()/2;
        mHandWmParams.format = PixelFormat.RGBA_8888;


        mTextWmParams.width = FloatingBallUtils.getScreenWidth();
        mTextWmParams.height = 80;

        mTextWmParams.x = 0;
        mTextWmParams.y = FloatingBallUtils.getScreenHeight()/2-floatBallSize-20;
        mTextWmParams.format = PixelFormat.RGBA_8888;



    }

    public void showTeaching(){

        initTeaching();
        mWindowManager.addView(mTeachArrow, mArrowWmParams);

        mWindowManager.addView(mTeachHand, mHandWmParams);

        mWindowManager.addView(mTeachText, mTextWmParams);

        final int originPosition= mHandWmParams.x;

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (!mStopTeaching) {
                    mHandWmParams.x -= 10;

                    if (mHandWmParams.x < originPosition - 200) {


                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mHandWmParams.x = originPosition;

                    }

                    MyApplication.getMainThreadHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            mWindowManager.updateViewLayout(mTeachHand, mHandWmParams);
                        }
                    });

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                MyApplication.getMainThreadHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        mWindowManager.removeView(mTeachArrow);
                        mWindowManager.removeView(mTeachHand);

                        mTeachText.setText("试试其它滑动方式，体验RelaxFinger带来的便捷吧!");
                        mTeachText.setTextSize(14);
                        mTeachText.setTextColor(Color.rgb(255,64,129));
                        mWindowManager.updateViewLayout(mTeachText, mTextWmParams);
                        mRemoveTeachText = true;

                    }
                });

            }
        }).start();

        mIsShowTeaching = true;

    }

    public void listenSoftKeyboard(){
        
    }
}
