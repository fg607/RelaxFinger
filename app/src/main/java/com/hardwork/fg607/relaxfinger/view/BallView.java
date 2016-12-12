package com.hardwork.fg607.relaxfinger.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;

import com.hardwork.fg607.relaxfinger.MyApplication;
import com.hardwork.fg607.relaxfinger.R;
import com.hardwork.fg607.relaxfinger.utils.DensityUtil;
import com.hardwork.fg607.relaxfinger.utils.FloatingBallUtils;
import com.hardwork.fg607.relaxfinger.utils.ImageUtils;

import net.grandcentrix.tray.TrayAppPreferences;

import static android.R.attr.animation;
import static android.R.attr.breadCrumbShortTitle;

/**
 * Created by fg607 on 16-11-24.
 */

public class BallView extends View {

    public static final String TAG = "BallView";
    public static final int MIN_BALL_ALPHA = 255;
    public static final int MAX_BALL_ALPHA = 10;
    public static final int MIN_BALL_SIZE = DensityUtil.dip2px(MyApplication.getApplication(), 30);
    public static final int MAX_BALL_SIZE = DensityUtil.dip2px(MyApplication.getApplication(), 60);

    public static final int SINGLE_TAP = 0;
    public static final int DOUBLE_TAP = 1;
    public static final int LONGPRESS = 2;
    public static final int SWIPE_UP = 3;
    public static final int SWIPE_DOWN = 4;
    public static final int SWIPE_LEFT = 5;
    public static final int SWIPE_RIGHT= 6;
    public static final int MOVE = 7;
    public static final int DOWN = 8;
    public static final int QUICK_SINGLE_TAP = 9;
    public static final int MOVE_FINISH = 10;

    public static final int TAP = 11;
    public static final int LONG_PRESS_TIMEOUT = 200;

    public static final int DOUBLE_TAP_TIMEOUT = 130;
    private static final int HOME_KEY_PRESSED = 11;
    private static final int RECENT_KEY_PRESSED = 12;

    private Context mContext;
    private int mSize;
    private int mAlpha;
    private String mTheme;
    private boolean mCanMove = true;
    private boolean mHasScrolled = false;
    private boolean mHasMoved = false;
    private TrayAppPreferences mPreferences;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWinLayoutParams;
    private LinearLayout mParentLayout;
    private LinearLayout.LayoutParams mLayoutParams;

    private ScaleAnimation mZoomInAnim;
    private ScaleAnimation mZoomOutAnim;
    private ScaleAnimation mFeedbackAnim;

    private OnGestureListener mGestureListener;
    private OnBallEventListener mBallEventListener;

    private int mTouchSlop;
    private int mTouchSlopSquare;
    private int mDownX;
    private int mDownY;
    private boolean mIsDoubleTapping;
    private boolean mGestureActive = false;

    private boolean mInLongPress;
    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){

                case SINGLE_TAP:
                    if(mGestureListener != null) mGestureListener.onSingleTap();
                    break;
                case QUICK_SINGLE_TAP:
                    if(mGestureListener != null) mGestureListener.onQucikSingleTap();
                    break;
                case DOUBLE_TAP:
                    if(mGestureListener != null) mGestureListener.onDoubleTap();
                    break;
                case LONGPRESS:
                    mInLongPress = true;
                    if(mGestureListener != null) mGestureListener.onLongPress();
                    break;
                case SWIPE_UP:
                    if(mGestureListener != null) mGestureListener.onScrollUp();
                    break;
                case SWIPE_DOWN:
                    if(mGestureListener != null) mGestureListener.onScrollDown();
                    break;
                case SWIPE_LEFT:
                    if(mGestureListener != null) mGestureListener.onScrollLeft();
                    break;
                case SWIPE_RIGHT:
                    if(mGestureListener != null) mGestureListener.onScrollRight();
                    break;
                case MOVE:
                    if(mGestureListener != null) mGestureListener.onMove();
                    break;
                case DOWN:
                    //BallView.this.startAnimation(mFeedbackAnim);
                    if(mGestureListener != null) mGestureListener.onDown();
                    break;
                case MOVE_FINISH:
                    if(mBallEventListener != null) mBallEventListener.onBallMoveFinish();
                    break;
                case HOME_KEY_PRESSED:
                    if(mBallEventListener != null) mBallEventListener.onHomeKeyPressed();
                    break;
                case RECENT_KEY_PRESSED:
                    if(mBallEventListener != null) mBallEventListener.onRecentKeyPressed();
                    break;
                default:
                    break;
            }
        }
    };


    public void updatePosition() {

        if(getParent() != null){

            mWindowManager.updateViewLayout(mParentLayout,mWinLayoutParams);
        }
    }


    public interface OnGestureListener {

        void onSingleTap();

        void onQucikSingleTap();

        void onDoubleTap();

        void onLongPress();

        void onScrollUp();

        void onScrollDown();

        void onScrollLeft();

        void onScrollRight();

        void onMove();

        void onDown();

    }

    public interface OnBallEventListener{

        void onBallMoveFinish();

        void onHomeKeyPressed();

        void onRecentKeyPressed();
    }


    public BallView(Context context) {

        super(context);

        mContext = context;

        mPreferences = FloatingBallUtils.getMultiProcessPreferences();

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        //系统能识别的最小滑动距离
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mTouchSlopSquare = mTouchSlop * mTouchSlop;

        mCanMove = mPreferences.getBoolean("moveSwitch", false);
        mSize = mPreferences.getInt("ballsize", (MIN_BALL_SIZE + MAX_BALL_SIZE) / 2);
        mAlpha = mPreferences.getInt("ballalpha", (MIN_BALL_ALPHA + MAX_BALL_ALPHA) / 2);
        mTheme = mPreferences.getString("theme", "默认");
        setTheme(mTheme);

        initParentLayout();
        initLayoutParams();
        initAnimation();

        setClickable(true);
    }

    public void setTheme(String theme) {

        switch (theme) {
            case "默认":
                setBackground(getResources().getDrawable(R.drawable.nor));
                break;
            case "彩虹":
                setBackground(getResources().getDrawable(R.drawable.color));
                break;
            case "Google":
                setBackground(getResources().getDrawable(R.drawable.red));
                break;
            case "苹果":
                setBackground(getResources().getDrawable(R.drawable.iphone));
                break;
            case "自定义":

                if(FloatingBallUtils.isFileExist("/RelaxFinger/DIY.png")) {

                    String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                            + "/RelaxFinger/DIY.png";
                    Bitmap icon = ImageUtils.scaleBitmap(filePath, mSize, mSize);

                    setBackground(ImageUtils.bitmap2Drawable(icon));

                }else{

                    setBackground(getResources().getDrawable(R.drawable.nor));
                    mPreferences.put("theme", "默认");
                }

                break;
            default:
                break;
        }

        getBackground().setAlpha(mAlpha);
    }

    private void initAnimation() {

        mFeedbackAnim = new ScaleAnimation(1,0.8f,1,0.8f,
                Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        mFeedbackAnim.setFillAfter(true);

        mZoomInAnim = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mZoomInAnim.setDuration(100);

        mZoomOutAnim = new ScaleAnimation(1f, 0f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mZoomOutAnim.setDuration(100);


        mZoomOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                mWindowManager.removeView(mParentLayout);
                mParentLayout.removeView(BallView.this);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void setOnGestureListener(OnGestureListener listener) {

        mGestureListener = listener;
    }

    public void setOnMoveFinishListener(OnBallEventListener listener){

        mBallEventListener = listener;
    }

    private void initParentLayout() {

        mParentLayout = new LinearLayout(mContext){

            //获取home键和多任务键事件
            public void onCloseSystemDialogs(String reason) {

                if (reason != null && reason.equals("homekey")) {

                    mHandler.sendEmptyMessage(HOME_KEY_PRESSED);

                } else if (reason != null && reason.equals("recentapps")) {

                    mHandler.sendEmptyMessage(RECENT_KEY_PRESSED);
                }

            }
        };

        mLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private void initLayoutParams() {

        mWinLayoutParams = new WindowManager.LayoutParams();

        mWinLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;

        mWinLayoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        mWinLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;

        mWinLayoutParams.x = mPreferences.getInt("ballWmParamsX", FloatingBallUtils.getScreenWidth() - mSize / 2 );
        mWinLayoutParams.y = mPreferences.getInt("ballWmParamsY", FloatingBallUtils.getScreenHeight() / 2 - mSize / 2);

        mWinLayoutParams.width = mSize;
        mWinLayoutParams.height = mSize;

        mWinLayoutParams.format = PixelFormat.TRANSLUCENT;
    }

    public WindowManager.LayoutParams getWindowLayoutParams(){

        return mWinLayoutParams;
    }

    private void touchDownFeedback() {
        setScaleX(0.8f);
        setScaleY(0.8f);
        getBackground().setAlpha(255);
    }

    private void touchUpFeedback() {
        setScaleX(1);
        setScaleY(1);
        getBackground().setAlpha(mAlpha);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:
                touchDownFeedback();
                //判断是否双击
                boolean hadTapMessage = mHandler.hasMessages(SINGLE_TAP);
                if (hadTapMessage) mHandler.removeMessages(SINGLE_TAP);
                if (hadTapMessage) {

                    mHandler.sendEmptyMessage(DOUBLE_TAP);
                    mIsDoubleTapping = true;
                    return true;

                } else {

                    mIsDoubleTapping = false;
                }

                mInLongPress = false;
                mHandler.sendEmptyMessageDelayed(LONGPRESS,LONG_PRESS_TIMEOUT);

                mDownX = (int) event.getRawX();
                mDownY = (int) event.getRawY();

                mHasMoved = false;
                mHasScrolled = false;
                mCanMove = mPreferences.getBoolean("moveSwitch",false);
                break;
            case MotionEvent.ACTION_MOVE:

                if(mIsDoubleTapping){

                   return true;
                }

                final int deltaX = (int) (event.getRawX() - mDownX);
                final int deltaY = (int) (event.getRawY() - mDownY);

                int distance = (deltaX * deltaX) + (deltaY * deltaY);

                //判断是否为滑动
                if(distance>mTouchSlopSquare){

                    mHasScrolled = true;
                    mHandler.removeMessages(LONGPRESS);

                    //移动悬浮球
                    if(mCanMove && mGestureListener != null){

                        mWinLayoutParams.x = (int)(event.getRawX()) - mSize/2;
                        mWinLayoutParams.y = (int)(event.getRawY()) - mSize/2;

                        mWindowManager.updateViewLayout(mParentLayout,mWinLayoutParams);

                        mHandler.sendEmptyMessage(MOVE);

                        mHasMoved = true;

                        return true;
                    }

                    //解析上下左右手势
                    if(!mGestureActive){

                        handleGesture(event);

                        return true;

                    }

                }

                break;
            case MotionEvent.ACTION_UP:

                touchUpFeedback();

                if(mIsDoubleTapping){

                    return true;
                }

                if(mHandler.hasMessages(LONGPRESS)){

                    mHandler.removeMessages(LONGPRESS);
                }

                if(!mInLongPress && !mHasScrolled){

                    if(!mIsDoubleTapping){

                        mHandler.sendEmptyMessageDelayed(SINGLE_TAP,DOUBLE_TAP_TIMEOUT);
                    }

                    mHandler.sendEmptyMessage(QUICK_SINGLE_TAP);
                }


                if(mHasMoved){

                    mHandler.sendEmptyMessage(MOVE_FINISH);
                    mHasMoved = false;
                }

                mGestureActive = false;
                break;
            default:
                touchUpFeedback();
                if(mHasMoved){

                    mHandler.sendEmptyMessage(MOVE_FINISH);
                    mHasMoved = false;
                }
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * 解析上下左右手势
     * @param event
     */
    private void handleGesture(MotionEvent event) {

        float yDistance = mDownY - event.getRawY();
        float xDistance = mDownX - event.getRawX();
        float yDistanceABS = Math.abs(yDistance);
        float xDistanceABS = Math.abs(xDistance);
        double x = Math.atan2(yDistanceABS, xDistanceABS);
        double angle = x * 180 / Math.PI;

        //当这个角度大于45度时候 我们就认为他是上下方向的滑动
        if (angle > 45) {

            if (yDistance < 0) {
                mHandler.sendEmptyMessage(SWIPE_DOWN);//手指往下滑动
                mGestureActive = true;
            } else {
                mHandler.sendEmptyMessage(SWIPE_UP);//手指往上滑动
                mGestureActive = true;
            }

        } else {
            //当这个角度小于45度时候 我们就认为他是左右方向的滑动
            if (xDistance < 0) {
                mHandler.sendEmptyMessage(SWIPE_RIGHT);//手指往右滑
                mGestureActive = true;
            } else {
                mHandler.sendEmptyMessage(SWIPE_LEFT);//手指往左边滑
                mGestureActive = true;
            }
        }
    }


    public void setMove(boolean canMove){

        mCanMove = canMove;
    }

    public void setAlpha(int alpha){

        mAlpha = alpha;

        getBackground().setAlpha(mAlpha);

    }

    public void setBallSize(int size){

        mSize = size;
        mWinLayoutParams.width = mSize;
        mWinLayoutParams.height = mSize;
    }

    public int getBallSize(){

        return mSize;
    }

    public void show(){

        if(getParent() == null){

            mParentLayout.addView(this);

            mWindowManager.addView(mParentLayout,mWinLayoutParams);

            startAnimation(mZoomInAnim);
        }

    }

    public void quickShow(){

        if(getParent() == null){

            mParentLayout.addView(this);

            mWindowManager.addView(mParentLayout,mWinLayoutParams);

        }
    }

    public void hide(){

        if(getParent() != null){

            startAnimation(mZoomOutAnim);
        }

    }

    public void quickHide(){

        if(getParent() != null){

            mWindowManager.removeView(mParentLayout);
            mParentLayout.removeView(BallView.this);
        }

    }

}
