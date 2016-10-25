package com.hardwork.fg607.relaxfinger.service;

/**
 * Created by fg607 on 15-8-20.
 */

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;
import com.hardwork.fg607.relaxfinger.MyApplication;
import com.hardwork.fg607.relaxfinger.R;
import com.hardwork.fg607.relaxfinger.SettingActivity;
import com.hardwork.fg607.relaxfinger.adapter.MenuFolderAdapter;
import com.hardwork.fg607.relaxfinger.model.ItemClickListener;
import com.hardwork.fg607.relaxfinger.model.MenuDataSugar;
import com.hardwork.fg607.relaxfinger.utils.AnimatorUtils;
import com.hardwork.fg607.relaxfinger.utils.AppUtils;
import com.hardwork.fg607.relaxfinger.utils.Config;
import com.hardwork.fg607.relaxfinger.utils.DensityUtil;
import com.hardwork.fg607.relaxfinger.utils.FloatingBallUtils;
import com.hardwork.fg607.relaxfinger.utils.ImageUtils;
import com.hardwork.fg607.relaxfinger.view.BlankActivity;
import com.hardwork.fg607.relaxfinger.view.ScreenshotActivity;
import com.ogaclejapan.arclayout.Arc;
import com.ogaclejapan.arclayout.ArcLayout;

import net.grandcentrix.tray.TrayAppPreferences;

import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;


import de.hdodenhof.circleimageview.CircleImageView;

public class FloatingBallService extends Service implements View.OnClickListener {

    private WindowManager mWindowManager = null;
    private WindowManager.LayoutParams mBallWmParams = null;
    private WindowManager.LayoutParams mMenuWmParams = null;
    private WindowManager.LayoutParams mTrackWmParams = null;
    private WindowManager.LayoutParams mPopBackWmParams = null;
    private WindowManager.LayoutParams mMenuFolderWmParams = null;
    private View mBallView;
    private View mMenuView;
    private View mMenuFolderView;
    private GridView mGridView;
    private CardView mCardView;
    private boolean mIsFolderAdded = false;
    private MenuFolderAdapter mMenuFolderAdapter;
    private int mOldOffsetX, mOldOffsetY, mNewOffsetX, mNewOffsetY;
    private Context mContext = MyApplication.getApplication();
    private FrameLayout mMenuLayout;
    private boolean mReverseMenu=false;
    private Button mFloatImage;
    private boolean mIsmoving = false;
    private boolean mCanmove = false;
    private boolean mIsSavePos = true;
    private boolean mIsToEdge = false;
    private Notification mNotification = null;
    private boolean mIsAdd;
    private boolean mIsMenuAdd;
    private boolean mIsTrackAdd;
    private int mClickCount;
    public static final long CLICK_SPACING_TIME = 100;//双击间隔时间
    private long mDoubleClickTime=CLICK_SPACING_TIME;
    public static final long LONG_PRESS_TIME = ViewConfiguration.getLongPressTimeout();
    public static final int MIN_BALL_ALPHA = 255;
    public static final int MAX_BALL_ALPHA = 10;
    public static final int MIN_BALL_SIZE = DensityUtil.dip2px(MyApplication.getApplication(),30);
    public static final int MAX_BALL_SIZE = DensityUtil.dip2px(MyApplication.getApplication(),60);
    public static final int MENU_WINDOW_WIDTH = DensityUtil.dip2px(MyApplication.getApplication(),150);
    public static final int MENU_WINDOW_HEIGHT = DensityUtil.dip2px(MyApplication.getApplication(),280);;
    private int floatBallSize;
    private Handler mHandler;
    private LongPressedThread mLongPressedThread;
    private ClickPressedThread mClickPressedThread;
    private ShowPopMenuThread mShowPopMenuThread;
    private HidePopMenuThread mHidePopMenuThread;
    private long mPreClickTime;
    public int mFloatBallAlpha;
    private Button mFab;
    private ArrayList<String> mCurrentFuncList = new ArrayList<>();
    private boolean mLongPressing;
    private View mTrackView;
    private Button mTrackImage;
    private NavAccessibilityService mAccessibilityService;
    private AccessibilityManager mAccessibilityManger;
    private List<AccessibilityServiceInfo> mList;
    private int mInitX,mInitY;
    private float mTouchX, mTouchY, mCurrentX, mCurrentY;
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
    private AnimatorSet mShowAnimator;
    private AnimatorSet mHideAnimator;
    private List<Animator> mShowAnimatorList;
    private List<Animator> mHideAnimatorList;
    private ScaleAnimation mZoomInAnima;
    private ScaleAnimation mZoomOutAnima;
    //private List<String> mRunningAppList;
    //private List<String> mSavedAppList;
   // private boolean mIsRunMenuOpen = false;
    //private boolean mIsSpeedApp = true;
    //private boolean mIsDetectRunApp=false;
    //private DetectAppThread mDetectThread;
    //private boolean mIsInitDetect=true;
    private boolean mIsNotifyOpened = true;
    private Object wmgInstnace = null;
    private Method trimMemory = null;
    private boolean mIsBallFree = false;
    private boolean mIsKeyboardShow=false;
    private boolean mIsLandscape=false;
    private boolean mIsGestureFeedback = true;
    private boolean mIsShotscreen = true;
    private boolean mIsHomePressed = false;
    private boolean mIsRecentPressed = false;

    private View mPopBackgroundView;
    private boolean mIsBackAdded= false;

    private LinearLayout mHideLayout;
    private LinearLayout.LayoutParams mHideParams;
    private TextView mHideBarView;
    private boolean mIsHideBarAdded = false;
    private boolean mCanShowHideBar = true;
    private WindowManager.LayoutParams mHideAreaParams=null;
    private boolean mIsBallHiding = false;


    private final BaseSpringSystem mSpringSystem = SpringSystem.create();
    private final ExampleSpringListener mSpringListener = new ExampleSpringListener();
    private final ExampleSpringListener1 mSpringListener1 = new ExampleSpringListener1();
    private FrameLayout mRootView;
    private Spring mScaleSpring;
    private Spring mScaleSpring1;

    private AnimationSet mShowAnimationSet;
    private AnimationSet mShowAnimationSet1;
    private AnimationSet mHideAnimationSet;
    private AnimationSet mHideAnimationSet1;
    private AnimationSet mShowFolderAnimationSet;

    private boolean mGestureActive = false;
    private boolean mHideAnimationStart = false;

    private CircleImageView mMenuA = null;
    private CircleImageView mMenuB = null;
    private CircleImageView mMenuC = null;
    private CircleImageView mMenuD = null;
    private CircleImageView mMenuE = null;

    private class ExampleSpringListener extends SimpleSpringListener {

        @Override
        public void onSpringUpdate(Spring spring) {

            float mappedValue = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 1, 0.8);

            mFloatImage.setScaleX(mappedValue);
            mFloatImage.setScaleY(mappedValue);
        }

    }

    private class ExampleSpringListener1 extends SimpleSpringListener {

        @Override
        public void onSpringUpdate(Spring spring) {

            float mappedValue = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 1, 0.8);

            mMenuA.setScaleX(mappedValue);
            mMenuA.setScaleY(mappedValue);
            mMenuB.setScaleX(mappedValue);
            mMenuB.setScaleY(mappedValue);
            mMenuC.setScaleX(mappedValue);
            mMenuC.setScaleY(mappedValue);
            mMenuD.setScaleX(mappedValue);
            mMenuD.setScaleY(mappedValue);
            mMenuE.setScaleX(mappedValue);
            mMenuE.setScaleY(mappedValue);
        }

    }


    @Override
    public void onCreate() {
        super.onCreate();

        mHandler = new Handler();

        mPreferences = FloatingBallUtils.getMultiProcessPreferences();

        mCanmove = mPreferences.getBoolean("moveSwitch", false);
        
        mIsToEdge = mPreferences.getBoolean("toEdgeSwitch", false);

        floatBallSize = mPreferences.getInt("ballsize", (MIN_BALL_SIZE + MAX_BALL_SIZE) / 2);

        mFloatBallAlpha = mPreferences.getInt("ballalpha", (MIN_BALL_ALPHA + MAX_BALL_ALPHA) / 2);
        mIsVibrate = mPreferences.getBoolean("vibratorSwitch", true);
        mIsGestureFeedback = mPreferences.getBoolean("feedbackSwitch",true);
        mIsShotscreen = mPreferences.getBoolean("shotscreenSwitch",true);
        mIsFloatRight = mPreferences.getBoolean("floatRight", true);
        mIsCanPopup = mPreferences.getBoolean("canPopup",true);
        mIsBallHiding = mPreferences.getBoolean("hideState",false);

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

        loadFunction();

        initPopBackground();

        setUpFloatMenuView();

        initMenuFolderView();


        if(mPreferences.getBoolean("notifySwitch",true)){

            createNotification();
        }


        mScaleSpring = mSpringSystem.createSpring()
        .setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(100,4));

        mScaleSpring1 = mSpringSystem.createSpring()
                .setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(100,5));
        /*
        mDetectThread = new DetectAppThread();

        if(mPreferences.getBoolean("detectApp",false)){

            startDetect();
        }

        mSavedAppList= new ArrayList<>();*/

    }

    private void initMenuFolderView() {

        LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        mMenuFolderView = li.inflate(R.layout.menu_folder, null);

        mGridView = (GridView) mMenuFolderView.findViewById(R.id.grid_view);

        mCardView = (CardView) mMenuFolderView.findViewById(R.id.card_view);

        mMenuFolderAdapter = new MenuFolderAdapter(this, new ItemClickListener() {
            @Override
            public void itemClick() {

                closeMenu();
            }
        });


        mMenuFolderWmParams = new WindowManager.LayoutParams();

        mMenuFolderWmParams.type = WindowManager.LayoutParams.TYPE_PHONE;


        mMenuFolderWmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;//| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
        mMenuFolderWmParams.gravity = Gravity.LEFT | Gravity.TOP;

        mMenuFolderWmParams.width = DensityUtil.getScreenWidth(this)*2/3;
        mMenuFolderWmParams.height = DensityUtil.getScreenHeight(this)/3;

        mMenuFolderWmParams.x =  DensityUtil.getScreenWidth(this)/6;
        mMenuFolderWmParams.y = DensityUtil.getScreenHeight(this)/2;

        mMenuFolderWmParams.format = PixelFormat.RGBA_8888;

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int orientation = newConfig.orientation;
        mHideAreaParams = null;
        if (orientation == Configuration.ORIENTATION_PORTRAIT){


            mIsLandscape = false;
            mBallWmParams.x = mPreferences.getInt("ballWmParamsX", FloatingBallUtils.getScreenWidth()-floatBallSize/2-DensityUtil.dip2px(MyApplication.getApplication(),40));
            mBallWmParams.y = mPreferences.getInt("ballWmParamsY", FloatingBallUtils.getScreenHeight()/2-floatBallSize/2);
            updateFloatBall();
            changeBallToOrigin();

            if(mPreferences.getBoolean("autoHideSwitch",false)){

                recoverFloatBall();
            }



        }
        else if (orientation == Configuration.ORIENTATION_LANDSCAPE){

            mIsLandscape = true;
           // changeBallToFree();

            if (mPreferences.getBoolean("autoHideSwitch", false)) {

                hideToNotifyBar();

            } else {

                mBallWmParams.x = DensityUtil.getScreenWidth(this) - mBallWmParams.width;
                mBallWmParams.y = DensityUtil.getScreenHeight(this) / 2 - mBallWmParams.width / 2;

                if (mIsAdd) {

                    mWindowManager.updateViewLayout(mBallView, mBallWmParams);
                }

            }


        }
    }


    public void changeBallToFree(){

        if(!mIsBallFree){

            mIsBallFree = true;
            closeMenu();
            mCanmove = true;
            mIsToEdge=false;
            mIsSavePos = false;

        }

    }

    public void changeBallToOrigin(){

        if(mIsBallFree && !mIsKeyboardShow && !mIsLandscape){

            mIsBallFree = false;
            mCanmove = mPreferences.getBoolean("moveSwitch",false);

            mIsToEdge = mPreferences.getBoolean("toEdgeSwitch",false);

            mIsSavePos = true;

            mIsFloatRight = mPreferences.getBoolean("floatRight",true);
            mIsCanPopup = mPreferences.getBoolean("canPopup",true);

            mBallWmParams.x = mPreferences.getInt("ballWmParamsX", FloatingBallUtils.getScreenWidth()-floatBallSize/2-DensityUtil.dip2px(MyApplication.getApplication(),40));
            mBallWmParams.y = mPreferences.getInt("ballWmParamsY", FloatingBallUtils.getScreenHeight()/2-floatBallSize/2);

            updateFloatBall();
        }



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

        if("无操作".equals(mPreferences.getString("doubleClick", "最近任务键"))){

            mDoubleClickTime=0;
        }else {

            mDoubleClickTime=CLICK_SPACING_TIME;
        }
    }

    private void createNotification() {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notify_logo)
                        .setContentTitle("RelaxFinger")
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
        mIsNotifyOpened = true;
    }



    private void createShowNotification(){

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notify_logo)
                        .setContentTitle("RelaxFinger")
                        .setContentText("点击显示悬浮球");

        Intent resultIntent = new Intent(Config.ACTION_SHOW_FLOATBALL);

        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(this,0,resultIntent,0);

        mBuilder.setContentIntent(resultPendingIntent);

        Notification notification = mBuilder.build();

        notification.flags|= Notification.FLAG_NO_CLEAR;

        NotificationManager mNF = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        mNF.notify(R.string.app_name, notification);

    }

    private void hideToNotifyBar(){

        mBallView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

        createShowNotification();

        closeFloatBall();


        mIsBallHiding = true;

        mPreferences.put("hideState",mIsBallHiding);
    }

    private void initFloatView() {

        LinearLayout linearLayout =  new LinearLayout(getApplicationContext()) {

            //home or recent button
            public void onCloseSystemDialogs(String reason) {

                if (reason != null && reason.equals("homekey")) {

                    disableMenu();

                }else if(reason != null && reason.equals("recentapps")){

                    if(mIsMenuAdd){

                        mIsRecentPressed = true;
                        mPreferences.put("addBackground",false);
                        closeMenu();
                    }

                }

            }
        };

        linearLayout.setFocusable(true);

        LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mBallView = li.inflate(R.layout.floatball, linearLayout);
        mFloatImage = (Button) mBallView.findViewById(R.id.float_image);
        setBallTheme(mPreferences.getString("theme", "默认"));
        mTrackView = li.inflate(R.layout.track, null);
        mTrackImage = (Button) mTrackView.findViewById(R.id.track_image);
        //mTrackImage.getBackground().setAlpha(100);
        mFloatImage.getBackground().setAlpha(mFloatBallAlpha);
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
                        loadFunction();
                    }
                    break;
                case Config.START_DETECT:
                    int detectSum = mPreferences.getInt("detectSum",0);

                    if (detectSum==0) {

                        startDetect();
                    }
                    mPreferences.put("detectSum",detectSum+1);
                    break;
                case Config.STOP_DETECT:
                    int detectSum1 = mPreferences.getInt("detectSum",0);


                    if (detectSum1-1==0) {

                        stopDetect();
                    }
                    mPreferences.put("detectSum",detectSum1-1);
                    break;
                case Config.MOVE_SWITCH:
                    setMove(intent.getBooleanExtra("canmove", false));
                    break;
                case Config.VIBRATOR_SWITCH:
                    setVibrator(intent.getBooleanExtra("isVibrate", true));
                    break;
                case Config.FEEDBACK_SWITCH:
                    setFeedback(intent.getBooleanExtra("isFeedback", true));
                    break;
                case Config.SHOTSCREEN_SWITCH:
                    setShotscreen(intent.getBooleanExtra("isShotscreen", true));
                    break;
                case Config.NOTIFY_SWITCH:
                    setNotify(intent.getBooleanExtra("isNotify", true));
                    break;
                case Config.SCREEN_ON:
                    if(mPreferences.getBoolean("floatSwitch",true)){

                        if(mIsBallHiding){

                            createShowNotification();

                        }else {

                            setFloatState(true);
                        }
                    }
                    break;
                case Config.FLOAT_SWITCH:
                    setFloatState(intent.getBooleanExtra("ballstate", false));
                    break;
                case Config.HIDE_BALL:
                    setBallHide(intent.getBooleanExtra("hide",false));
                    break;
                case Config.CLOSE_MENU:
                    closeMenu();
                    break;
                case Config.FLOAT_AUTOMOVE:
                    setFloatAutoMove(intent.getBooleanExtra("move", false));
                    break;
                case Config.HIDE_TO_NOTIFYBAR:
                    hideToNotifyBar();
                    break;
                case Config.RECOVER_FLOATBALL:
                    recoverFloatBall();
                    break;
                case Config.FLOAT_THEME:
                    setBallTheme(intent.getStringExtra("theme"));
                    break;
                case Config.BALL_SIZE:
                    setBallSize(intent.getIntExtra("ballsize", 1));
                    break;
                case Config.BALL_ALPHA:
                    setBallAlpha(intent.getIntExtra("ballalpha", 1));
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



    private void setShotscreen(boolean isShotscreen) {

        mIsShotscreen = isShotscreen;
    }

    private void setFeedback(boolean isFeedback) {

        mIsGestureFeedback = isFeedback;
    }

    private void recoverFloatBall() {

        if(!mIsLandscape){

            mBallWmParams.x = mPreferences.getInt("ballWmParamsX", FloatingBallUtils.getScreenWidth()-floatBallSize/2-DensityUtil.dip2px(MyApplication.getApplication(),40));
            mBallWmParams.y = mPreferences.getInt("ballWmParamsY", FloatingBallUtils.getScreenHeight()/2-floatBallSize/2);
        }else {

            mBallWmParams.x = DensityUtil.getScreenWidth(this)-mBallWmParams.width;
            mBallWmParams.y = DensityUtil.getScreenHeight(this)/2-mBallWmParams.width/2;
        }


        updateTrack();
        showFloatBall();

        closeRecoverNotify();

        mIsBallHiding = false;

        mPreferences.put("hideState",mIsBallHiding);


    }

    private void closeRecoverNotify() {

        NotificationManager mNF = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        mNF.cancel(R.string.app_name);
    }

    private void setBallHide(boolean hide) {

        if(hide){

                mFloatImage.getBackground().setAlpha(0);

            if(mIsTrackAdd){

                mWindowManager.removeViewImmediate(mTrackView);

                mIsTrackAdd = false;
            }

            if(mIsHideBarAdded){

                mWindowManager.removeViewImmediate(mHideLayout);
                mIsHideBarAdded = false;
            }

            mCanShowHideBar = false;

        }else {

            mFloatImage.getBackground().setAlpha(mFloatBallAlpha);
            mCanShowHideBar = true;
        }
    }

    private void setFloatAutoMove(boolean move) {

        mIsKeyboardShow = move;

        if(move){

            avoidKeyboard();

            changeBallToFree();


        } else {


            changeBallToOrigin();


        }

    }

    private void avoidKeyboard() {
        int y = (int)(DensityUtil.getScreenHeight(this)/2-mBallWmParams.height*1.5);
        if(mBallWmParams.y >y){

            mBallWmParams.y=y;
        }

        if(mIsAdd){

            mWindowManager.updateViewLayout(mBallView,mBallWmParams);

        }
    }

    private void setBallTheme(String theme) {

        switch (theme) {
            case "默认":
                mFloatImage.setBackground(getResources().getDrawable(R.drawable.nor));
                break;
            case "彩虹":
                mFloatImage.setBackground(getResources().getDrawable(R.drawable.color));
                break;
            case "红色":
                mFloatImage.setBackground(getResources().getDrawable(R.drawable.red));
                break;
            case "苹果":
                mFloatImage.setBackground(getResources().getDrawable(R.drawable.iphone));
                break;
            case "自定义":

                if(FloatingBallUtils.isFileExist("/RelaxFinger/DIY.png")){

                    String filePath= Environment.getExternalStorageDirectory().getAbsolutePath()
                            +"/RelaxFinger/DIY.png";
                    Bitmap icon = ImageUtils.scaleBitmap(filePath,floatBallSize,floatBallSize);

                    mFloatImage.setBackground(ImageUtils.bitmap2Drawable(icon));
                }

                break;
            default:
                break;
        }

        mFloatImage.getBackground().setAlpha(mFloatBallAlpha);

    }

    private void setBallAlpha(int ballalpha) {

        mFloatBallAlpha = (int)(MIN_BALL_ALPHA + (float)((MAX_BALL_ALPHA - MIN_BALL_ALPHA) * ballalpha/ 100));

        saveStates("ballalpha", mFloatBallAlpha);

        mFloatImage.getBackground().setAlpha(mFloatBallAlpha);

    }

    private void setNotify(boolean isNotify) {

        if(isNotify){

            createNotification();

        }else {

            closeNotification();
        }
    }

    private void closeNotification() {

        //销毁时停止前台
        if(mIsNotifyOpened){

            stopForeground(true);
            mIsNotifyOpened=false;
        }


    }

    private void setVibrator(boolean isVibrator) {

        mIsVibrate = isVibrator;
    }

    private void setBallSize(int ballSizePercent) {


            floatBallSize = (int)(MIN_BALL_SIZE + (float)((MAX_BALL_SIZE - MIN_BALL_SIZE) * ballSizePercent/ 100));

            saveStates("ballsize", floatBallSize);

            updateFloatBall();

            if (mIsToEdge) {

                moveToScreenEdge(mBallWmParams.x);
            }

    }

    private void setFloatState(boolean ballstate) {

        if(ballstate){

            if(!mIsBallHiding){

                showFloatBall();
            }else {

                hideToNotifyBar();
            }

        }else {
            if(mIsBallHiding){

                closeRecoverNotify();
            }
            hideTrack();
            closeFloatBall();
            closeMenu();

           /*
            if (mDetectThread!=null&& mDetectThread.isAlive()){

                mIsDetectRunApp=false;
                mDetectThread=null;
            }*/


            stopSelf();
        }
    }


    private void setMove(boolean canmove) {

        mCanmove = canmove;
    }

    private void setToEdge(boolean isToEdge) {

        mIsToEdge = isToEdge;

        if (mIsToEdge) {

            moveToScreenEdge(mBallWmParams.x);
        }
    }


/**
 * 窗口菜单初始化
 */
    private void setUpFloatMenuView(){

        mShowPopMenuThread = new ShowPopMenuThread();
        mHidePopMenuThread = new HidePopMenuThread();

        LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        if(mIsFloatRight){
            mMenuView = li.inflate(R.layout.popup, null);
        }else {
            mMenuView = li.inflate(R.layout.popup_left, null);
        }

        mMenuLayout = (FrameLayout) mMenuView.findViewById(R.id.menu_layout);

        mMenuLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
            }
        });
        updateMenuIcons();
        mArcLayout = (ArcLayout) mMenuView.findViewById(R.id.arc_layout);
        mFab = (Button) mMenuView.findViewById(R.id.fab);

        mMenuA = (CircleImageView) mMenuView.findViewById(R.id.menuA);
        mMenuB = (CircleImageView) mMenuView.findViewById(R.id.menuB);
        mMenuC = (CircleImageView) mMenuView.findViewById(R.id.menuC);
        mMenuD = (CircleImageView) mMenuView.findViewById(R.id.menuD);
        mMenuE = (CircleImageView) mMenuView.findViewById(R.id.menuE);

        for (int i = 0, size = mArcLayout.getChildCount(); i < size; i++) {
            mArcLayout.getChildAt(i).setOnClickListener(this);
        }

        mFab.setOnClickListener(this);


    /*    mShowAnimatorList = new ArrayList<>();
        mShowAnimator = new AnimatorSet();
        mShowAnimator.setDuration(400);
        mShowAnimator.setInterpolator(new OvershootInterpolator());

        mHideAnimatorList= new ArrayList<>();
        mHideAnimator = new AnimatorSet();
        mHideAnimator.setDuration(400);
        mHideAnimator.setInterpolator(new AnticipateInterpolator());
        mHideAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mMenuLayout.setVisibility(View.INVISIBLE);
            }
        });*/


        mShowAnimationSet = new AnimationSet(true);
        mShowAnimationSet1 = new AnimationSet(true);

        ScaleAnimation showScaleAimation = new ScaleAnimation(0.2f,1,0.2f,1,Animation.RELATIVE_TO_SELF,
                1.0f,Animation.RELATIVE_TO_SELF,0.5f);

        ScaleAnimation showScaleAimation1 = new ScaleAnimation(0.2f,1,0.2f,1,Animation.RELATIVE_TO_SELF,
                0f,Animation.RELATIVE_TO_SELF,0.5f);

        AlphaAnimation showAlphaAnimation = new AlphaAnimation(0.1f,1.0f);


        mShowAnimationSet.addAnimation(showAlphaAnimation);
        mShowAnimationSet.addAnimation(showScaleAimation);
        mShowAnimationSet.setDuration(150);


        mShowAnimationSet1.addAnimation(showAlphaAnimation);
        mShowAnimationSet1.addAnimation(showScaleAimation1);
        mShowAnimationSet1.setDuration(150);

        mShowAnimationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                mScaleSpring1.setEndValue(1);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                mScaleSpring1.setEndValue(0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mShowAnimationSet1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                mScaleSpring1.setEndValue(1);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                mScaleSpring1.setEndValue(0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mHideAnimationSet = new AnimationSet(true);
        mHideAnimationSet1 = new AnimationSet(true);

        ScaleAnimation hideScaleAnimation = new ScaleAnimation(1,0.2f,1,0.2f,Animation.RELATIVE_TO_SELF,
                1.0f,Animation.RELATIVE_TO_SELF,0.5f);

        ScaleAnimation hideScaleAnimation1 = new ScaleAnimation(1,0.2f,1,0.2f,Animation.RELATIVE_TO_SELF,
                0.0f,Animation.RELATIVE_TO_SELF,0.5f);

        AlphaAnimation hideAlphaAnimation = new AlphaAnimation(1.0f,0.1f);


        mHideAnimationSet.addAnimation(hideAlphaAnimation);
        mHideAnimationSet.addAnimation(hideScaleAnimation);
        mHideAnimationSet.setFillAfter(true);
        mHideAnimationSet.setDuration(150);

        mHideAnimationSet1.addAnimation(hideAlphaAnimation);
        mHideAnimationSet1.addAnimation(hideScaleAnimation1);
        mHideAnimationSet1.setFillAfter(true);
        mHideAnimationSet1.setDuration(150);

        mHideAnimationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                mHideAnimationStart = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (mIsMenuAdd) {

                            mWindowManager.removeViewImmediate(mMenuView);
                            mIsMenuAdd = false;

                            mHideAnimationStart = false;

                            clearCache();
                        }
                    }
                },50);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mHideAnimationSet1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                mHideAnimationStart = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {


                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (mIsMenuAdd) {

                            mWindowManager.removeViewImmediate(mMenuView);
                            mIsMenuAdd = false;

                            mHideAnimationStart = false;

                            clearCache();
                        }
                    }
                },50);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        ScaleAnimation animation = new ScaleAnimation(0.5f,1,0.5f,1,Animation.RELATIVE_TO_SELF,
                0.5f,Animation.RELATIVE_TO_SELF,0.5f);

        mShowFolderAnimationSet = new AnimationSet(true);

        mShowFolderAnimationSet.addAnimation(animation);

        mShowFolderAnimationSet.addAnimation(showAlphaAnimation);

        mShowFolderAnimationSet.setDuration(150);
        mShowFolderAnimationSet.setFillAfter(true);
        mShowFolderAnimationSet.setFillEnabled(true);


        mMenuWmParams = new WindowManager.LayoutParams();

        if (Build.VERSION.SDK_INT < 19) {

            mMenuWmParams.type = WindowManager.LayoutParams.TYPE_PHONE;

        }else {

            mMenuWmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        }
        mMenuWmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;//| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
        mMenuWmParams.gravity = Gravity.LEFT | Gravity.TOP;


       // mMenuWmParams.dimAmount=0.5f;
        mMenuWmParams.width = MENU_WINDOW_WIDTH;
        mMenuWmParams.height = MENU_WINDOW_HEIGHT;
        mMenuWmParams.format = PixelFormat.RGBA_8888;
    }

    private  void initPopBackground(){

        mPopBackWmParams = new WindowManager.LayoutParams();

        if (Build.VERSION.SDK_INT < 19) {

            mPopBackWmParams.type = WindowManager.LayoutParams.TYPE_PHONE;

        }else {

            mPopBackWmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        }
        mPopBackWmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;//| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
        mPopBackWmParams.gravity = Gravity.LEFT | Gravity.TOP;

        //mPopBackWmParams.alpha=0.3f;
        //mPopBackWmParams.dimAmount=0.5f;


        mPopBackWmParams.x=0;
        mPopBackWmParams.y=0;

        mPopBackWmParams.width = DensityUtil.getScreenWidth(this);
        mPopBackWmParams.height = DensityUtil.getScreenHeight(this);
        mPopBackWmParams.format = PixelFormat.RGBA_8888;
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


        if(mIsFloatRight){


            mMenuLayout.startAnimation(mShowAnimationSet);

        }else {

            mMenuLayout.startAnimation(mShowAnimationSet1);

        }

        mFab.setClickable(true);
    }


    /**
     * 隐藏功能键面板
     */
    private void hideMenu() {

        mFab.setClickable(false);
/*
        ScaleAnimation animation = new ScaleAnimation(1,0,1,0,Animation.RELATIVE_TO_SELF,
                1.0f,Animation.RELATIVE_TO_SELF,0.5f);
        animation.setDuration(200);

        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f,0.1f);

        alphaAnimation.setDuration(200);


        mHideAnimationSet.addAnimation(alphaAnimation);
        mHideAnimationSet.addAnimation(animation);*/


        if(mIsFloatRight){


            mMenuLayout.startAnimation(mHideAnimationSet);

        }else {

            mMenuLayout.startAnimation(mHideAnimationSet1);

        }



     /*   mHideAnimatorList.clear();

        for (int i = mArcLayout.getChildCount() - 1; i >= 0; i--) {
            mHideAnimatorList.add(createHideItemAnimator(mArcLayout.getChildAt(i)));
        }
        mHideAnimator.playTogether(mHideAnimatorList);
        mHideAnimator.start();*/


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

            //if(mIsMenuAdd){
/*
                if(!mIsRecentPressed){

                    removePopBackground();
                }*/

            if(mIsMenuAdd){

                mWindowManager.removeViewImmediate(mMenuView);
                mIsMenuAdd= false;
                clearCache();
            }

           // }



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
    public void createFloatBallView() {


        //设置悬浮窗口参数
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        mBallWmParams = new WindowManager.LayoutParams();

        mBallWmParams.type = WindowManager.LayoutParams.TYPE_PHONE;

        mBallWmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;//| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
        mBallWmParams.gravity = Gravity.LEFT | Gravity.TOP;

        mBallWmParams.x = mPreferences.getInt("ballWmParamsX", FloatingBallUtils.getScreenWidth()-floatBallSize/2-DensityUtil.dip2px(MyApplication.getApplication(),40));
        mBallWmParams.y = mPreferences.getInt("ballWmParamsY", FloatingBallUtils.getScreenHeight()/2-floatBallSize/2);

        mBallWmParams.width = floatBallSize;
        mBallWmParams.height = floatBallSize;

        mBallWmParams.format = PixelFormat.TRANSLUCENT;

        mZoomOutAnima = new ScaleAnimation(1f,0.9f,1f,0.9f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        mZoomOutAnima.setDuration(100);
        mZoomOutAnima.setFillAfter(true);

        mZoomInAnima = new ScaleAnimation(0.9f,1f,0.9f,1f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        mZoomInAnima.setDuration(100);
        mZoomInAnima.setFillAfter(false);

        //系统能识别的最小滑动距离
        final int touchSlop = ViewConfiguration.get(FloatingBallService.this).getScaledTouchSlop();

        //注册触摸事件监听器
        mFloatImage.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                boolean isShowHideBar = mPreferences.getBoolean("hideAreaSwitch",true);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mScaleSpring.setEndValue(1);
                        mFloatImage.getBackground().setAlpha(255);
                        mTouchX = event.getRawX();
                        mTouchY = event.getRawY();
                        mInitX = mBallWmParams.x;
                        mInitY = mBallWmParams.y;
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

                        //showTrack();

                        mOldOffsetX = mBallWmParams.x;
                        mOldOffsetY = mBallWmParams.y;

                        mNewOffsetX = 0;
                        mNewOffsetY = 0;

                        break;
                    case MotionEvent.ACTION_MOVE:
                        mCurrentX =  event.getRawX();
                        mCurrentY =  event.getRawY();
                        mIsmoving = true;

                        mNewOffsetX = (int)(mCurrentX-mTouchX);
                        mNewOffsetY = (int)(mCurrentY-mTouchY);

                        //滑动量大于50像素取消长按事件
                            if (Math.abs(mNewOffsetX) > 50 || Math.abs(mNewOffsetY) > 50) {
                                //取消注册的长按事件
                                mHandler.removeCallbacks(mLongPressedThread);
                                mLongPressing = false;
                            }

                        //更新悬浮球位置，保存位置
                        if (mCanmove) {

                            mBallWmParams.x = mInitX + mNewOffsetX;
                            mBallWmParams.y = mInitY + mNewOffsetY;


                            if(isShowHideBar){

                                //mLongPressing用来判断是否为移动，而非点击
                                if(!mLongPressing&&mCanShowHideBar&&!mIsHideBarAdded){

                                    showHideBar();
                                }

                            }


                            if (mBallWmParams.x > FloatingBallUtils.getScreenWidth() - mBallWmParams.width) {
                                mBallWmParams.x = FloatingBallUtils.getScreenWidth() - mBallWmParams.width;
                            }
                            if (mBallWmParams.y > FloatingBallUtils.getScreenHeight() - mBallWmParams.height) {

                                mBallWmParams.y = FloatingBallUtils.getScreenHeight() - mBallWmParams.height;
                            }

                            updateViewPosition();


                        } else {


                            if(!mGestureActive){

                                //Y轴滑动偏移量大于40像素并且Y轴滑动偏移量比X轴偏移量多出20像素时判定为向上滑动
                                if (Math.abs(mNewOffsetY) - Math.abs(mNewOffsetX) > touchSlop/2 && (mNewOffsetY) < -touchSlop) {

                                    mClickCount = 0;
                                    onFloatBallFlipUp();

                                    mScaleSpring.setEndValue(0);
                                    mGestureActive = true;
                                    return true;

                                }
                                //向下滑动
                                else if (Math.abs(mNewOffsetY) - Math.abs(mNewOffsetX) > touchSlop/2 && (mNewOffsetY) > touchSlop) {

                                    mClickCount = 0;
                                    onFloatBallFlipDown();

                                    mScaleSpring.setEndValue(0);
                                    mGestureActive = true;
                                    return true;

                                }
                                //向左滑动
                                else if (Math.abs(mNewOffsetX) - Math.abs(mNewOffsetY) > touchSlop/2 && (mNewOffsetX) < -touchSlop) {

                                    mClickCount = 0;
                                    onFloatBallFlipLeft();

                                    mScaleSpring.setEndValue(0);
                                    mGestureActive = true;
                                    return true;

                                }
                                //向右滑动
                                else if (Math.abs(mNewOffsetX) - Math.abs(mNewOffsetY) > touchSlop/2 && (mNewOffsetX) > touchSlop) {

                                    mClickCount = 0;
                                    onFloatBallFlipRight();

                                    mScaleSpring.setEndValue(0);
                                    mGestureActive = true;
                                    return true;

                                }

                            }


                          /*  mTrackWmParams.x = mOldOffsetX + (mBallWmParams.width / 2 - floatBallSize * 3 / 5 / 2) + mNewOffsetX / 2;
                            mTrackWmParams.y = mOldOffsetY + (mBallWmParams.height / 2 - floatBallSize * 3 / 5 / 2) + mNewOffsetY / 2;

                            updateTrackPositon();*/


                        }
                        break;
                    case MotionEvent.ACTION_UP:

                        mScaleSpring.setEndValue(0);
                        mGestureActive = false;

                        if(mIsHideBarAdded){

                            closeHideBar();
                        }
                        mHandler.removeCallbacks(mLongPressedThread);
                        mFloatImage.getBackground().setAlpha(mFloatBallAlpha);


                        //hideTrack();


                        // 滑动偏移量小于40像素，判定为点击悬浮球
                        if (Math.abs(mNewOffsetX) <= touchSlop && Math.abs(mNewOffsetY) <= touchSlop) {


                            if (System.currentTimeMillis() - mPreClickTime <= LONG_PRESS_TIME) {

                                //取消注册的长按事件

                                mClickPressedThread = new ClickPressedThread();
                                mHandler.postDelayed(mClickPressedThread, mDoubleClickTime);
                            }

                        } else if (mCanmove) {

                            mClickCount = 0;
                        }

                        if (mCanmove) {


                            if(mIsHideBarAdded&&checkInHideArea()){

                                hideToNotifyBar();

                                if(!mPreferences.getBoolean("moveSwitch",false) && mIsSavePos){

                                    mCanmove = false;
                                }

                                return true;
                            }

                            if (mIsToEdge) {

                                moveToScreenEdge(event.getRawX());

                            } else if(mIsSavePos && !mIsLandscape) {

                                saveStates("ballWmParamsX", mBallWmParams.x);
                                saveStates("ballWmParamsY", mBallWmParams.y);

                                if(mBallWmParams.x + mBallWmParams.width/2 >= FloatingBallUtils.getScreenWidth()/2){

                                    mIsFloatRight = true;
                                }else {

                                    mIsFloatRight = false;
                                }

                                if(mIsFloatRight!=mPreferences.getBoolean("floatRight",true)){

                                    reverseMenu();
                                }


                                saveStates("floatRight",mIsFloatRight);


                            }

                            if(mIsSavePos){

                                int statBarHeight = FloatingBallUtils.getStatusBarHeight(FloatingBallService.this);
                                int screenHeight = DensityUtil.getScreenHeight(FloatingBallService.this);

                                if(mBallWmParams.y >= (statBarHeight + (MENU_WINDOW_HEIGHT/2-mBallWmParams.height/2))
                                        && mBallWmParams.y<= screenHeight-(MENU_WINDOW_HEIGHT/2+mBallWmParams.height/2+statBarHeight)){

                                    mIsCanPopup = true;

                                }else {

                                    mIsCanPopup = false;
                                }

                                saveStates("canPopup",mIsCanPopup);
                            }



                            mPreferences.getBoolean("moveSwitch",false);
                            if(!mPreferences.getBoolean("moveSwitch",false) && mIsSavePos){

                                mCanmove = false;
                            }



                        } else {

                            mTrackWmParams.x = mOldOffsetX + (mBallWmParams.width / 2 - floatBallSize * 3 / 5 / 2);
                            mTrackWmParams.y = mOldOffsetY + (mBallWmParams.height / 2 - floatBallSize * 3 / 5 / 2);


                            if (!mLongPressing) {

                                //Y轴滑动偏移量大于40像素并且Y轴滑动偏移量比X轴偏移量多出20像素时判定为向上滑动
                                /*if (Math.abs(mNewOffsetY) - Math.abs(mNewOffsetX) > 20 && (mNewOffsetY) < -40) {

                                    mClickCount = 0;
                                    onFloatBallFlipUp();

                                }
                                //向下滑动
                                else if (Math.abs(mNewOffsetY) - Math.abs(mNewOffsetX) > 20 && (mNewOffsetY) > 40) {

                                    mClickCount = 0;
                                    onFloatBallFlipDown();

                                }
                                //向左滑动
                                else if (Math.abs(mNewOffsetX) - Math.abs(mNewOffsetY) > 20 && (mNewOffsetX) < -40) {

                                    mClickCount = 0;
                                    onFloatBallFlipLeft();

                                }
                                //向右滑动
                                else if (Math.abs(mNewOffsetX) - Math.abs(mNewOffsetY) > 10 && (mNewOffsetX) > 10) {

                                    mClickCount = 0;
                                    onFloatBallFlipRight();

                                }*/


                            }

                            //onClearOffset();

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

    private void reverseMenu() {

        LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        if(mIsFloatRight){
            mMenuView = li.inflate(R.layout.popup, null);
        }else {
            mMenuView = li.inflate(R.layout.popup_left, null);
        }

        mMenuA = (CircleImageView) mMenuView.findViewById(R.id.menuA);
        mMenuB = (CircleImageView) mMenuView.findViewById(R.id.menuB);
        mMenuC = (CircleImageView) mMenuView.findViewById(R.id.menuC);
        mMenuD = (CircleImageView) mMenuView.findViewById(R.id.menuD);
        mMenuE = (CircleImageView) mMenuView.findViewById(R.id.menuE);

        mMenuLayout = (FrameLayout) mMenuView.findViewById(R.id.menu_layout);

        mMenuLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
            }
        });
        updateMenuIcons();
        mArcLayout = (ArcLayout) mMenuView.findViewById(R.id.arc_layout);
        mFab = (Button) mMenuView.findViewById(R.id.fab);


        for (int i = 0, size = mArcLayout.getChildCount(); i < size; i++) {
            mArcLayout.getChildAt(i).setOnClickListener(this);
        }

        mFab.setOnClickListener(this);

    }

    private void closeHideBar() {

        final TranslateAnimation animation = new TranslateAnimation(0,0,0,mHideAreaParams.height);
        animation.setDuration(150);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                mWindowManager.removeView(mHideLayout);
                mIsHideBarAdded = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mHideBarView.startAnimation(animation);
    }


    private void disableMenu() {

        if(mIsMenuAdd){

            closeMenu();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                mIsHomePressed = true;
                try {
                    Thread.sleep(5000);

                    mIsHomePressed = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    mIsHomePressed = false;
                }


            }
        }).start();
    }

    private boolean checkInHideArea(){

        return  FloatingBallUtils.getStatusBarHeight(this)+mBallWmParams.y
                +mBallWmParams.width>=FloatingBallUtils.getScreenHeight()
                -mHideAreaParams.height?true:false;
    }

    private void moveToScreenEdge(float startx) {

        if (mBallWmParams.x + mBallWmParams.width / 2 >= FloatingBallUtils.getScreenWidth() / 2) {

            moveToScreenRight(startx);
            if(!mIsLandscape){
                mIsFloatRight = true;
            }


        } else {

            if(!mIsLandscape){
                mIsFloatRight = false;
            }
            moveToScreenLeft(startx);
        }

        if(mIsFloatRight != mPreferences.getBoolean("floatRight",true)){

            mReverseMenu = true;
        }else{

            mReverseMenu = false;
        }

        saveStates("floatRight",mIsFloatRight);
    }

    private void moveToScreenLeft(float startx) {

        ValueAnimator mSlideToBoundaryAnim = ValueAnimator.ofFloat(startx, 0);

        int maxDuration = 350;

        mSlideToBoundaryAnim.setDuration((int)(startx*maxDuration/(FloatingBallUtils
                .getScreenWidth()/2-mBallWmParams.width/2)));
        mSlideToBoundaryAnim.setInterpolator(new DecelerateInterpolator());
        mSlideToBoundaryAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float currentValue = (Float) valueAnimator.getAnimatedValue();
                mBallWmParams.x = (int) currentValue;
                updateViewPosition();
            }
        });
        mSlideToBoundaryAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

                if(mIsSavePos && !mIsLandscape){

                    saveStates("ballWmParamsX", mBallWmParams.x);
                    saveStates("ballWmParamsY", mBallWmParams.y);
                }

                if(mReverseMenu && !mIsLandscape){

                    reverseMenu();

                    mReverseMenu = false;
                }

            }
        });
        mSlideToBoundaryAnim.start();

    }

    private void moveToScreenRight(float startx) {

        ValueAnimator mSlideToBoundaryAnim = ValueAnimator.ofFloat(startx, FloatingBallUtils.getScreenWidth()
        -mBallWmParams.width);

        int maxDuration = 200;

        mSlideToBoundaryAnim.setDuration((int)(startx*maxDuration/(FloatingBallUtils
                .getScreenWidth()/2-mBallWmParams.width/2)));
        mSlideToBoundaryAnim.setInterpolator(new DecelerateInterpolator());
        mSlideToBoundaryAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float currentValue = (Float) valueAnimator.getAnimatedValue();
                mBallWmParams.x = (int) currentValue;
                updateViewPosition();
            }

        });
        mSlideToBoundaryAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

                if(mIsSavePos && !mIsLandscape){

                    saveStates("ballWmParamsX", mBallWmParams.x);
                    saveStates("ballWmParamsY", mBallWmParams.y);
                }

                if(mReverseMenu  && !mIsLandscape){

                    reverseMenu();

                    mReverseMenu = false;
                }
            }
        });
        mSlideToBoundaryAnim.start();


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
                break;
            case "快捷应用":

                if(mIsLandscape){

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(mContext,"横屏不允许打开快捷菜单!",Toast.LENGTH_SHORT).show();
                        }
                    });

                    return;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        if(mAppNumber > 0){

                            if(mIsCanPopup){

                        /*if(!mIsSpeedApp){

                            updateMenuIcons();

                            mIsSpeedApp = true;
                        }*/

                                if(!mIsHomePressed){

                                    mIsRecentPressed = false;

                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            popUpMenu();
                                        }
                                    });


                                }else {

                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            Toast.makeText(mContext,"系统原因，请在按下系统自带Home键5秒后再打开快捷菜单！",Toast.LENGTH_LONG).show();
                                        }
                                    });

                                }




                            }else {

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {

                                        Toast.makeText(mContext,"空间不足，上下移动悬浮球再试！",Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }



                        }else {

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {

                                    showAlertDialog();
                                }
                            });

                        }
                    }
                }).start();

                break;
            case "返回键":
                closeMenu();
                FloatingBallUtils.keyBack(mAccessibilityService.instance);
                break;
            case "Home键":
                closeMenu();
                FloatingBallUtils.keyHome(this);
                break;
            case "最近任务键":
                FloatingBallUtils.openRecnetTask(mAccessibilityService.instance);
                break;
            case "切换上一个应用":
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            FloatingBallUtils.previousApp();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

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
            case "屏幕截图":
                if(Build.VERSION.SDK_INT > 20){

                    startActivity(new Intent().setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).setClass(
                            this, ScreenshotActivity.class));
                }else {

                    Toast.makeText(this,"截图功能适用于5.0以上系统！",Toast.LENGTH_SHORT).show();
                }

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
                FloatingBallUtils.volumeDown();
                break;
            case "后台应用":
                /*try {

                    if(mIsCanPopup){

                        if(mSavedAppList.size()>0){

                            mIsRunMenuOpen=true;
                            createRunAppMenu(mSavedAppList);

                            mIsRunMenuOpen=true;
                            mIsSpeedApp = false;

                            popUpMenu();

                        }else {

                            if(mIsInitDetect){
                                Toast.makeText(this,"正在检测，请稍后！",Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(this,"没有后台应用！",Toast.LENGTH_SHORT).show();
                            }

                        }


                    }else {

                        Toast.makeText(this,"空间不足，向下移动悬浮球再试！",Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this,"打开应用出错！",Toast.LENGTH_SHORT).show();
                }*/
                break;
            default:
                break;

        }


    }

    private void showHideBar() {


        if(mHideAreaParams==null){

            mHideAreaParams = new WindowManager.LayoutParams();

            if (Build.VERSION.SDK_INT < 19) {

                mHideAreaParams.type = WindowManager.LayoutParams.TYPE_PHONE;

            }else {

                mHideAreaParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            }

            mHideAreaParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;//| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
            mHideAreaParams.gravity = Gravity.LEFT | Gravity.TOP;

            mHideAreaParams.width = DensityUtil.getScreenWidth(this);
            mHideAreaParams.height = DensityUtil.dip2px(this,50);

            mHideAreaParams.x=0;
            mHideAreaParams.y=FloatingBallUtils.getScreenHeight()-mHideAreaParams.height-FloatingBallUtils.getStatusBarHeight(this);


            mHideAreaParams.format = PixelFormat.RGBA_8888;
        }


        if(mHideBarView == null){

            mHideBarView = new TextView(this);
            mHideBarView.setTextColor(Color.WHITE);
            mHideBarView.setTextSize(18);
            mHideBarView.setText("拖入此区域隐藏悬浮球");
            mHideBarView.setBackgroundColor(Color.BLACK);
            mHideBarView.getBackground().setAlpha(180);
            mHideBarView.setGravity(Gravity.CENTER);
        }


        if(mHideLayout == null){

            mHideLayout = new LinearLayout(this);
            mHideParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            mHideLayout.addView(mHideBarView,mHideParams);
        }



        if(!mIsHideBarAdded){

            mWindowManager.addView(mHideLayout,mHideAreaParams);

            final TranslateAnimation animation = new TranslateAnimation(0,0,mHideAreaParams.height,0);
            animation.setDuration(150);
            mHideBarView.startAnimation(animation);

            mIsHideBarAdded=true;
        }


    }

    private void createRunAppMenu(List<String> packageNameList) {

        /*CircleImageView imageView = null;

        int size = packageNameList.size();
        if(size==0){
            return;
        }

        imageView = (CircleImageView) mMenuView.findViewById(R.id.menuA);

        if(size>=1){


            imageView.setImageDrawable(AppUtils.getAppIcon(packageNameList.get(0)));
            imageView.setClickable(true);
        }else {

            imageView.setImageDrawable(null);
            imageView.setClickable(false);

        }

        imageView = (CircleImageView) mMenuView.findViewById(R.id.menuB);

        if(size>=2){


            imageView.setImageDrawable(AppUtils.getAppIcon(packageNameList.get(1)));
            imageView.setClickable(true);
        }else {

            imageView.setImageDrawable(null);
            imageView.setClickable(false);
        }

        imageView = (CircleImageView) mMenuView.findViewById(R.id.menuC);

        if(size>=3){


            imageView.setImageDrawable(AppUtils.getAppIcon(packageNameList.get(2)));
            imageView.setClickable(true);

        }else {

            imageView.setImageDrawable(null);
            imageView.setClickable(false);
        }


        imageView = (CircleImageView) mMenuView.findViewById(R.id.menuD);

        if(size>=4){


            imageView.setImageDrawable(AppUtils.getAppIcon(packageNameList.get(3)));
            imageView.setClickable(true);
        }else {

            imageView.setImageDrawable(null);
            imageView.setClickable(false);

        }

        imageView = (CircleImageView) mMenuView.findViewById(R.id.menuE);

        if(size>=5){


            imageView.setImageDrawable(AppUtils.getAppIcon(packageNameList.get(4)));
            imageView.setClickable(true);
        }else {

            imageView.setImageDrawable(null);
            imageView.setClickable(false);
        }*/


    }

    private void showAlertDialog() {

        Toast.makeText(this,"还没有设置快捷菜单！",Toast.LENGTH_SHORT).show();
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

        if(mIsBallFree){

            if(mIsShotscreen){

                chooseFunction("屏幕截图");
            }


        }else {

            chooseFunction(mCurrentFuncList.get(2));
        }

    }

    /**
     * 点击悬浮球
     */

    private void onFloatBallClick() {


        if(mIsGestureFeedback){

            mBallView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        }


        if(mIsBallFree){

            chooseFunction("返回键");

        }else {

            chooseFunction(mCurrentFuncList.get(0));

        }

    }

    /**
     * 双击悬浮球
     */
    private void onFloatBallDoubleClick() {

        if(mIsGestureFeedback){

            mBallView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        }

        if(mIsBallFree){

            chooseFunction("Home键");

        }else {

            chooseFunction(mCurrentFuncList.get(1));
        }



    }

    /**
     * 向上滑动悬浮球
     */
    private void onFloatBallFlipUp() {

        if(mIsGestureFeedback){

            mBallView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        }

        chooseFunction(mCurrentFuncList.get(3));
    }

    /**
     * 向下滑动悬浮球
     */
    private void onFloatBallFlipDown() {

        if(mIsGestureFeedback){

            mBallView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        }

        chooseFunction(mCurrentFuncList.get(4));
    }

    /**
     * 向左滑动悬浮球
     */
    private void onFloatBallFlipLeft() {

        if(mIsGestureFeedback){

            mBallView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        }

        if(mIsShowTeaching){

            mStopTeaching = true;
        }
        chooseFunction(mCurrentFuncList.get(5));

    }

    /**
     * 向右滑动悬浮球
     */
    private void onFloatBallFlipRight() {

        if(mIsGestureFeedback){

            mBallView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        }

        chooseFunction(mCurrentFuncList.get(6));
    }


    /**
     * 弹出功能菜单
     */
    private  void popUpMenu() {

        mBallWmParams.x = mPreferences.getInt("ballWmParamsX", FloatingBallUtils.getScreenWidth()-floatBallSize/2-DensityUtil.dip2px(MyApplication.getApplication(),40));
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

            addPopBackground();
            mWindowManager.addView(mMenuView, mMenuWmParams);
            mIsMenuAdd = true;

        }




        //弹出面板后延迟100ms开始播放功能键显示动画
        //mHandler.postDelayed(mShowPopMenuThread, 100);
        mHandler.post(mShowPopMenuThread);


    }

    private void addPopBackground() {

        //AppUtils.startActivity(BlankActivity.class);

        mPreferences.put("addBackground",true);
        final Intent intent = new Intent(this, BlankActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

      /* if(!mIsBackAdded){


           mIsBackAdded=true;
           mPopBackgroundView = new ImageView(this);
           mPopBackgroundView.setBackgroundColor(getResources().getColor(R.color.popbackground));

           mPopBackgroundView.setClickable(true);
           mPopBackgroundView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {

                   closeMenu();
               }
           });

           mWindowManager.addView(mPopBackgroundView,mPopBackWmParams);


       }*/

    }

    private void removePopBackground(){

        mPreferences.put("addBackground",false);

        final Intent intent = new Intent(this, BlankActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("finish",true);
        startActivity(intent);

      /*  if(mIsBackAdded){

            mWindowManager.removeViewImmediate(mPopBackgroundView);

            mPopBackgroundView=null;
            mIsBackAdded=false;
        }
*/
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
    public void updateMenuIcons() {
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

            updateViewIcon(imageView, which);
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

        List<MenuDataSugar> menuDatalist = MenuDataSugar.findWithQuery(MenuDataSugar.class,"select * from MENU_DATA_SUGAR" +
                " where WHICH_MENU='"+whichApp+"'");


        int size = menuDatalist.size();

        if(size == 0){

            Toast.makeText(mContext,"找不到该应用程序！",Toast.LENGTH_SHORT).show();
            updateMenuIcons(whichApp);

            return;
        }

        if(size==1){

            MenuDataSugar dataSugar = menuDatalist.get(0);

            try {
                dataSugar.click();
            } catch (URISyntaxException e) {
                e.printStackTrace();
                Toast.makeText(mContext,"找不到该应用程序！",Toast.LENGTH_SHORT).show();
                MenuDataSugar.executeQuery("delete from MENU_DATA_SUGAR where ACTION='" + dataSugar.getAction()+"'");

                updateMenuIcons(whichApp);
            }catch (ActivityNotFoundException e){
                e.printStackTrace();
                Toast.makeText(mContext,"找不到该应用程序！",Toast.LENGTH_SHORT).show();
                MenuDataSugar.executeQuery("delete from MENU_DATA_SUGAR where ACTION='" + dataSugar.getAction()+"'");

                updateMenuIcons(whichApp);
            }

            /*int type = dataSugar.getType();
            String action = dataSugar.getAction();
            String name = dataSugar.getName();

            if(type==0){

                boolean isOpen = AppUtils.startApplication(action);

                if(!isOpen){
                    Toast.makeText(this,"应用程序已卸载！",Toast.LENGTH_SHORT).show();
                    mPreferences.put("app"+whichApp,"");
                    updateMenuIcons(whichApp);
                }
            }else if(type==1){

                FloatingBallUtils.switchButton(name);

            }else if(type==2){

                try {

                    AppUtils.startActivity(action);

                } catch (URISyntaxException e) {
                    e.printStackTrace();

                    Toast.makeText(this,"应用程序已卸载！",Toast.LENGTH_SHORT).show();

                    mPreferences.put("app"+whichApp,"");
                    updateMenuIcons(whichApp);

                } catch (ActivityNotFoundException e){

                    e.printStackTrace();

                    Toast.makeText(this,"应用程序已卸载！",Toast.LENGTH_SHORT).show();

                    mPreferences.put("app"+whichApp,"");
                    updateMenuIcons(whichApp);

                }
            }*/

            closeMenu();


        }else if(size>1) {


            showMenuFolder(menuDatalist);


        }



        // if(mIsSpeedApp){

     /*   String name = mPreferences.getString("app"+whichApp,"");
        int type = mPreferences.getInt("type"+whichApp,0);

        if(name != ""){

            if(type==0){

                boolean isOpen = AppUtils.startApplication(name);

                if(!isOpen){
                    Toast.makeText(this,"应用程序已卸载！",Toast.LENGTH_SHORT).show();
                    mPreferences.put("app"+whichApp,"");
                    updateMenuIcons(whichApp);
                }
            }else if(type==1){

                FloatingBallUtils.switchButton(name);

            }else if(type==2){

                String intent =  mPreferences.getString("shortcutIntent"+whichApp,"");
                try {

                    AppUtils.startActivity(intent);

                } catch (URISyntaxException e) {
                    e.printStackTrace();

                    Toast.makeText(this,"应用程序已卸载！",Toast.LENGTH_SHORT).show();

                    mPreferences.put("app"+whichApp,"");
                    updateMenuIcons(whichApp);

                } catch (ActivityNotFoundException e){

                    e.printStackTrace();

                    Toast.makeText(this,"应用程序已卸载！",Toast.LENGTH_SHORT).show();

                    mPreferences.put("app"+whichApp,"");
                    updateMenuIcons(whichApp);

                }
            }

        }*/

        /*}else {

            int which = Integer.parseInt(whichApp);

            if(mSavedAppList.size()>=which){

                String packageName = mSavedAppList.get(which-1);

                if(packageName != ""){

                    boolean isOpen = AppUtils.startApplication(packageName);

                    if(!isOpen){

                        Toast.makeText(this,"后台没有该应用！",Toast.LENGTH_SHORT).show();

                    }
                }
            }else {


                Toast.makeText(this,"后台没有该应用！",Toast.LENGTH_SHORT).show();
            }


        }*/


    }

    private void showMenuFolder(List<MenuDataSugar> menuDataList) {

        if (!mIsFolderAdded) {

            mMenuFolderAdapter.setMenuDataList(menuDataList);

            mGridView.setAdapter(mMenuFolderAdapter);

            mMenuView.setVisibility(View.INVISIBLE);

            mWindowManager.addView(mMenuFolderView, mMenuFolderWmParams);

            mCardView.startAnimation(mShowFolderAnimationSet);

            mIsFolderAdded = true;
        }

    }

    /**
     * 更新悬浮球图标
     */
    private void updateBallIcon() {
        String menuName = mPreferences.getString("currentfunction", null);
        updateViewIcon(mFloatImage, menuName);
        mFloatImage.getBackground().setAlpha(mFloatBallAlpha);

    }

    /**
     * 更新功能键图标
     * @param view
     * @param which
     */
    private void updateViewIcon(View view, String which) {

        Drawable drawable = null;

        int type = -1;

        List<MenuDataSugar> menuDatalist = MenuDataSugar.findWithQuery(MenuDataSugar.class,"select * from MENU_DATA_SUGAR" +
                " where WHICH_MENU='"+which+"'");

        int size = menuDatalist.size();

        if(size==1){

            MenuDataSugar dataSugar = menuDatalist.get(0);

            type = dataSugar.getType();

            if(type == 0){

                drawable = AppUtils.getAppIcon(dataSugar.getAction());

            }else if(type == 1){

                drawable = FloatingBallUtils.getSwitcherIcon(dataSugar.getName());

            }else if( type == 2){

                drawable = AppUtils.getShortcutIcon(dataSugar.getName());
            }


        }else if(size>1) {


            ArrayList<Bitmap> list = new ArrayList<Bitmap>();

            for (MenuDataSugar dataSugar : menuDatalist) {

                switch (dataSugar.getType()) {

                    case 0:
                        drawable = AppUtils.getAppIcon(dataSugar.getAction());
                        break;
                    case 1:
                        drawable = FloatingBallUtils.getSwitcherIcon(dataSugar.getName());
                        break;
                    case 2:
                        drawable = AppUtils.getShortcutIcon(dataSugar.getName());
                        break;
                    default:
                        break;
                }

                if(drawable!=null){

                    list.add(ImageUtils.drawable2Bitmap(drawable));

                }else {

                    MenuDataSugar.executeQuery("delete from MENU_DATA_SUGAR where ACTION='" + dataSugar.getAction()+"'");
                }

                if (list.size() >= 9) {

                    break;
                }


            }

            Bitmap bi = FloatingBallUtils.createCombinationImage(list);

            drawable = ImageUtils.bitmap2Drawable(bi);
        }

      /*  int type = mPreferences.getInt("type"+which,0);

        if(type == 0){

            drawable = AppUtils.getAppIcon(mPreferences.getString("app" + which, ""));

        }else if(type == 1){

            drawable = FloatingBallUtils.getSwitcherIcon(mPreferences.getString("app" + which, ""));
        }else if( type == 2){

            drawable = AppUtils.getShortcutIcon(mPreferences.getString("app" + which, ""));
        }*/



        if(drawable != null){

            if(view instanceof CircleImageView){

                CircleImageView circleImageView = (CircleImageView)view;
                if(type==0 || type == 2){

                    circleImageView.setBackground(null);

                }else if(type==1){

                    circleImageView.setBackgroundResource(R.drawable.path_blue_oval);

                }else if(type == -1){

                    circleImageView.setBackgroundResource(R.drawable.path_folder_oval);
                }
                circleImageView.setImageDrawable(drawable);
                circleImageView.setClickable(true);

                if(!mIsAppExist[Integer.parseInt(which)-1]){

                    mAppNumber++;

                    mIsAppExist[Integer.parseInt(which)-1] = true;
                }

            }
        }else {

            if(view instanceof CircleImageView){

                CircleImageView circleImageView = (CircleImageView)view;
                circleImageView.setBackground(null);
                circleImageView.setImageDrawable(null);
                circleImageView.setClickable(false);

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


        if(mIsFolderAdded){

            ScaleAnimation animation = new ScaleAnimation(1,0.5f,1,0.5f,Animation.RELATIVE_TO_SELF,
                    0.5f,Animation.RELATIVE_TO_SELF,0.5f);


            AlphaAnimation animation1 = new AlphaAnimation(1.0f,0.1f);

            AnimationSet animationSet = new AnimationSet(true);

            animationSet.addAnimation(animation);

            animationSet.addAnimation(animation1);

            animationSet.setDuration(150);
            animationSet.setFillAfter(true);
            animationSet.setFillEnabled(true);

            animationSet.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {


                }

                @Override
                public void onAnimationEnd(Animation animation) {

                    if(mIsFolderAdded){

                        mWindowManager.removeViewImmediate(mMenuFolderView);
                        mIsFolderAdded = false;
                    }

                    mMenuView.setVisibility(View.VISIBLE);

                  /*  mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            mMenuView.setVisibility(View.VISIBLE);
                        }
                    },200);
*/


                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            mCardView.startAnimation(animationSet);


        }


        if(mIsMenuAdd){

            //mIsMenuAdd=false;

            if(mIsFolderAdded){

                if (mIsMenuAdd) {

                    mWindowManager.removeViewImmediate(mMenuView);
                    mIsMenuAdd = false;
                    clearCache();
                }


            }else {
                if(!mHideAnimationStart){
                    hideMenu();
                }

            }



           // mHandler.postDelayed(mHidePopMenuThread,500);
        }



        if(!mIsRecentPressed&&mPreferences.getBoolean("addBackground",false)){

            removePopBackground();
        }



        /*if(mIsRunMenuOpen){

            mIsRunMenuOpen=false;
        }*/

    }

    /**
     * 关闭悬浮球
     */
    public void closeFloatBall() {
        if (mIsAdd) {
            ScaleAnimation animation = new ScaleAnimation(1f,0f,1f,0f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
            animation.setDuration(200);
            animation.setFillAfter(false);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                    mWindowManager.removeView(mBallView);
                    mScaleSpring.removeListener(mSpringListener);
                    mScaleSpring1.removeListener(mSpringListener1);
                    mIsAdd = !mIsAdd;

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mFloatImage.startAnimation(animation);

        }


    }

    /**
     * 显示悬浮球
     */
    public void showFloatBall() {

        if (!mIsAdd) {
            mWindowManager.addView(mBallView, mBallWmParams);

            ScaleAnimation animation = new ScaleAnimation(0f,1f,0f,1f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
            animation.setDuration(200);
            animation.setFillAfter(false);
            mFloatImage.startAnimation(animation);

            mScaleSpring.addListener(mSpringListener);
            mScaleSpring1.addListener(mSpringListener1);

            mIsAdd = !mIsAdd;
        }

    }

    public void showTrack() {

        if (!mIsTrackAdd) {
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

        closeNotification();

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


    public void startDetect(){

        /*mIsDetectRunApp = true;

        mPreferences.put("detectApp",true);

        if(mDetectThread!= null){

            mDetectThread.start();

        }else {

            mDetectThread = new DetectAppThread();

            mDetectThread.start();
        }*/

    }


    public void stopDetect(){
        /*if(mDetectThread!= null){

            mIsDetectRunApp=false;
            mPreferences.put("detectApp",false);
            mDetectThread = null;

            mRunningAppList.clear();
            mRunningAppList=null;
            mSavedAppList.clear();
            mSavedAppList=null;
        }*/

    }

    class DetectAppThread extends Thread{

        @Override
        public void run() {

         /*   mIsInitDetect = true;
            while (mIsDetectRunApp){

                try {
                    mRunningAppList = AppUtils.getTasks();
                    if(!mIsRunMenuOpen){

                        if(mSavedAppList==null){

                            mSavedAppList=new ArrayList<>();
                        }
                        mSavedAppList.clear();
                        mSavedAppList.addAll(mRunningAppList);
                        mIsInitDetect=false;
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }*/
        }
    }


    private void clearCache() {
        try {

            if(wmgInstnace==null){

                Class wmgClass = Class.forName("android.view.WindowManagerGlobal");
                wmgInstnace = wmgClass.getMethod("getInstance").invoke(null, (Object[]) null);
                trimMemory = wmgClass.getMethod("trimMemory",int.class);
            }


            trimMemory.invoke(wmgInstnace,TRIM_MEMORY_COMPLETE);

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("CLEAR","failed");
        }
    }





}
