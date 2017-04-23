package com.hardwork.fg607.relaxfinger.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;
import com.hardwork.fg607.relaxfinger.MyApplication;
import com.hardwork.fg607.relaxfinger.R;
import com.hardwork.fg607.relaxfinger.model.MenuDataSugar;
import com.hardwork.fg607.relaxfinger.utils.AppUtils;
import com.hardwork.fg607.relaxfinger.utils.DensityUtil;
import com.hardwork.fg607.relaxfinger.utils.FloatingBallUtils;
import com.hardwork.fg607.relaxfinger.utils.ImageUtils;
import com.ogaclejapan.arclayout.ArcLayout;

import net.grandcentrix.tray.TrayAppPreferences;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.hardwork.fg607.relaxfinger.utils.ImageUtils.releaseBitmap;

/**
 * Created by fg607 on 16-11-27.
 */

public class MenuViewProxy implements View.OnClickListener {

    public static final String TAG = "MenuViewProxy";

    public static final int TYPE_DEFAULT = -1;
    public static final int TYPE_APP = 0;
    public static final int TYPE_SWITCH_BUTTON = 1;
    public static final int TYPE_SHORTCUT = 2;
    public static final int TYPE_FOLDER = 3;
    public static final String MENU_A = "1";
    public static final String MENU_B = "2";
    public static final String MENU_C = "3";
    public static final String MENU_D = "4";
    public static final String MENU_E = "5";
    private static final int MAX_FOLDER_ICONS = 9;
    private static final int CLEAR_COUNT = 5;

    public static final int MENU_WINDOW_WIDTH = DensityUtil.dip2px(MyApplication.getApplication(), 150);
    public static final int MENU_WINDOW_HEIGHT = DensityUtil.dip2px(MyApplication.getApplication(), 280);
    private final WindowManager mWindowManager;

    private byte mIsExistItem = 0;
    private final byte mExistAllItem = (byte) (1 << 0 | 1 << 1 | 1 << 2 | 1 << 3 | 1 << 4);
    private int mClearCount = 0;

    private Context mContext;
    private View mMenuView;
    private WindowManager.LayoutParams mWinLayoutParams;
    private TrayAppPreferences mPreferences;
    private boolean mIsBallRight;
    private OnMenuItemClickListener mItemClickListener;
    private CircleImageView mMenuA;
    private CircleImageView mMenuB;
    private CircleImageView mMenuC;
    private CircleImageView mMenuD;
    private CircleImageView mMenuE;
    private FrameLayout mMenuLayout;
    private AnimationSet mShowAnimationSetL;
    private AnimationSet mShowAnimationSetR;
    private AnimationSet mHideAnimationSetL;
    private AnimationSet mHideAnimationSetR;

    private int mMenuType = -1; //菜单内容类型是APP,快捷开关,快捷方式,文件夹
    private Spring mScaleSpring;
    private ScaleSpringListener mSpringListener;



    public interface OnMenuItemClickListener{

        void clickeMenuA();
        void clickeMenuB();
        void clickeMenuC();
        void clickeMenuD();
        void clickeMenuE();
        void closeMenu();
    }

    private class ScaleSpringListener extends SimpleSpringListener {

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

    public MenuViewProxy(Context context){

        mContext = context;

        mPreferences = FloatingBallUtils.getMultiProcessPreferences();

        mWindowManager = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);

        mIsBallRight = mPreferences.getBoolean("floatRight", true);

        initMenuView();
        initLayoutParams();
        initAnimation();
    }

    private void initAnimation() {

        mScaleSpring = SpringSystem.create().createSpring()
                .setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(100, 8));

        mSpringListener = new ScaleSpringListener();

        mShowAnimationSetL = new AnimationSet(true);
        mShowAnimationSetR = new AnimationSet(true);

        ScaleAnimation showScaleAimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.5f);

        ScaleAnimation showScaleAimation1 = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF,
                0f, Animation.RELATIVE_TO_SELF, 0.5f);

        AlphaAnimation showAlphaAnimation = new AlphaAnimation(0, 1.0f);

        mShowAnimationSetR.addAnimation(showAlphaAnimation);
        mShowAnimationSetR.addAnimation(showScaleAimation);
        mShowAnimationSetR.setDuration(130);

        mShowAnimationSetL.addAnimation(showAlphaAnimation);
        mShowAnimationSetL.addAnimation(showScaleAimation1);
        mShowAnimationSetL.setDuration(130);


        mShowAnimationSetL.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                mScaleSpring.setEndValue(1);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                mScaleSpring.setEndValue(0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mShowAnimationSetR.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                mScaleSpring.setEndValue(1);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

               mScaleSpring.setEndValue(0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mHideAnimationSetL = new AnimationSet(true);
        mHideAnimationSetR = new AnimationSet(true);

        ScaleAnimation hideScaleAnimation = new ScaleAnimation(1, 0, 1, 0, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.5f);

        ScaleAnimation hideScaleAnimation1 = new ScaleAnimation(1, 0, 1, 0, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.5f);

        AlphaAnimation hideAlphaAnimation = new AlphaAnimation(1.0f, 0);


        mHideAnimationSetR.addAnimation(hideAlphaAnimation);
        mHideAnimationSetR.addAnimation(hideScaleAnimation);
        mHideAnimationSetR.setFillAfter(true);
        mHideAnimationSetR.setDuration(130);


        mHideAnimationSetL.addAnimation(hideAlphaAnimation);
        mHideAnimationSetL.addAnimation(hideScaleAnimation1);
        mHideAnimationSetL.setFillAfter(true);
        mHideAnimationSetL.setDuration(130);

        mHideAnimationSetL.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {


                mScaleSpring.removeListener(mSpringListener);

                if (mMenuView.getParent() != null) {

                    MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            mWindowManager.removeViewImmediate(mMenuView);
                            mClearCount++;
                            if(mClearCount >= CLEAR_COUNT){

                                mClearCount = -1;

                                Drawable drawable;

                                drawable = mMenuA.getBackground();

                                releaseBitmap(drawable);

                                drawable = mMenuB.getBackground();

                                releaseBitmap(drawable);

                                drawable = mMenuC.getBackground();

                                releaseBitmap(drawable);

                                drawable = mMenuD.getBackground();

                                releaseBitmap(drawable);

                                drawable = mMenuE.getBackground();

                                releaseBitmap(drawable);

                                System.gc();
                                System.runFinalization();
                            }

                        }
                    },50);

                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mHideAnimationSetR.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                mScaleSpring.removeListener(mSpringListener);

                if (mMenuView.getParent() != null) {

                    MyApplication.getMainThreadHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            mWindowManager.removeViewImmediate(mMenuView);
                            mClearCount++;
                            if(mClearCount >= CLEAR_COUNT){

                                mClearCount = -1;

                                Drawable drawable;

                                drawable = mMenuA.getBackground();

                                releaseBitmap(drawable);

                                drawable = mMenuB.getBackground();

                                releaseBitmap(drawable);

                                drawable = mMenuC.getBackground();

                                releaseBitmap(drawable);

                                drawable = mMenuD.getBackground();

                                releaseBitmap(drawable);

                                drawable = mMenuE.getBackground();

                                releaseBitmap(drawable);

                                System.gc();
                                System.runFinalization();
                            }
                        }
                    },50);
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void setOnItemClickListener(OnMenuItemClickListener listener){

        mItemClickListener = listener;

    }

    private void initLayoutParams() {

        mWinLayoutParams = new WindowManager.LayoutParams();

        mWinLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;

        mWinLayoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        mWinLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;

        mWinLayoutParams.width = MENU_WINDOW_WIDTH;
        mWinLayoutParams.height = MENU_WINDOW_HEIGHT;

        mWinLayoutParams.format = PixelFormat.TRANSLUCENT;
    }

    private void initMenuView() {

        if (mIsBallRight) {
            mMenuView = LayoutInflater.from(mContext).inflate(R.layout.popup, null);
        } else {
            mMenuView = LayoutInflater.from(mContext).inflate(R.layout.popup_left, null);
        }

        mMenuA = (CircleImageView) mMenuView.findViewById(R.id.menuA);
        mMenuB = (CircleImageView) mMenuView.findViewById(R.id.menuB);
        mMenuC = (CircleImageView) mMenuView.findViewById(R.id.menuC);
        mMenuD = (CircleImageView) mMenuView.findViewById(R.id.menuD);
        mMenuE = (CircleImageView) mMenuView.findViewById(R.id.menuE);

        mMenuA.setOnClickListener(this);
        mMenuB.setOnClickListener(this);
        mMenuC.setOnClickListener(this);
        mMenuD.setOnClickListener(this);
        mMenuE.setOnClickListener(this);

        updateMenuIcons();

        mMenuLayout = (FrameLayout) mMenuView.findViewById(R.id.menu_layout);
        mMenuLayout.setOnClickListener(this);

    }

    public void setIsBallRight(boolean isBallRight){

        mIsBallRight = isBallRight;

        if (mIsBallRight != mPreferences.getBoolean("floatRight", true)) {

            reverseMenu();
        }
    }

    private void reverseMenu() {

        initMenuView();
    }


    public void updateMenuIcon(String which) {

        CircleImageView imageView = null;

        Drawable drawable = null;

        switch (which) {

            case MENU_A:
                imageView = mMenuA;
                break;
            case MENU_B:
                imageView = mMenuB;
                break;
            case MENU_C:
                imageView = mMenuC;
                break;
            case MENU_D:
                imageView = mMenuD;
                break;
            case MENU_E:
                imageView = mMenuE;
                break;
            default:
                break;
        }

        if (imageView != null) {

            drawable = getMenuDrawable(which);

            if (drawable != null) {

                if (mMenuType == TYPE_APP || mMenuType == TYPE_SHORTCUT) {

                    imageView.setBackgroundResource(R.drawable.path_white_oval);

                } else if (mMenuType == TYPE_SWITCH_BUTTON) {

                    imageView.setBackgroundResource(R.drawable.path_blue_oval);

                } else if (mMenuType == TYPE_FOLDER) {

                    imageView.setBackgroundResource(R.drawable.path_folder_oval);
                }

                imageView.setImageDrawable(drawable);
                imageView.setClickable(true);

                mIsExistItem |= 1 << (Integer.parseInt(which) - 1);

            } else {

                imageView.setBackground(null);
                imageView.setImageDrawable(null);
                imageView.setClickable(false);

                mIsExistItem &= mExistAllItem - (1 << (Integer.parseInt(which) - 1));
            }
        }

    }

    /**
     * 从数据库解析生成快捷菜单图标
     * @param which
     * @return
     */
    private Drawable getMenuDrawable(String which){

        Drawable drawable = null;

        List<MenuDataSugar> menuDatalist = MenuDataSugar.findWithQuery(MenuDataSugar.class, "select * from MENU_DATA_SUGAR" +
                " where WHICH_MENU='" + which + "'");

        //快捷菜单中的内容个数
        int size = menuDatalist.size();

        if (size == 1) { //快捷菜单只有一个内容

            MenuDataSugar dataSugar = menuDatalist.get(0);

            mMenuType = dataSugar.getType();

            drawable = getTypeDrawable(dataSugar);


        } else if (size > 1) {//快捷菜单是文件夹

            mMenuType = TYPE_FOLDER;

            ArrayList<Bitmap> list = new ArrayList<Bitmap>();

            for (MenuDataSugar dataSugar : menuDatalist) {

                drawable = getTypeDrawable(dataSugar);

                if (drawable != null) {

                    list.add(ImageUtils.drawable2Bitmap(drawable));

                } else {
                    //若对应内容没有图标则从文件夹中删除
                    MenuDataSugar.executeQuery("delete from MENU_DATA_SUGAR where ACTION='" + dataSugar.getAction() + "'");
                }

                if (list.size() >= MAX_FOLDER_ICONS) {

                    break;
                }
            }

            //由多个内容合成一个文件夹图标
            Bitmap bi = FloatingBallUtils.createCombinationImage(list);

            drawable = ImageUtils.bitmap2Drawable(bi);
        }

        return drawable;
    }

    private Drawable getTypeDrawable(MenuDataSugar dataSugar) {

        Drawable drawable = null;

        switch (dataSugar.getType()) {
            case TYPE_APP:
                drawable = AppUtils.getAppIcon(dataSugar.getAction());
                break;
            case TYPE_SWITCH_BUTTON:
                drawable = FloatingBallUtils.getSwitcherIcon(dataSugar.getName());
                break;
            case TYPE_SHORTCUT:
                drawable = AppUtils.getShortcutIcon(dataSugar.getName());
                break;
            default:
                break;
        }

        return drawable;
    }

    @Override
    public void onClick(View view) {

        if (mItemClickListener == null) {

            return;
        }

        switch (view.getId()) {

            case R.id.menu_layout:
                mItemClickListener.closeMenu();
                break;
            case R.id.menuA:
                mItemClickListener.clickeMenuA();
                break;
            case R.id.menuB:
                mItemClickListener.clickeMenuB();
                break;
            case R.id.menuC:
                mItemClickListener.clickeMenuC();
                break;
            case R.id.menuD:
                mItemClickListener.clickeMenuD();
                break;
            case R.id.menuE:
                mItemClickListener.clickeMenuE();
                break;
            default:
                closeMenu();
                break;
        }
    }

    public boolean isExistItem(){

        return mIsExistItem == 0?false:true;
    }

    public void setMenuPos(int x, int y) {

        mWinLayoutParams.x = x;
        mWinLayoutParams.y = y;
    }

    public View getMenuView(){

        return mMenuView;
    }

    public WindowManager.LayoutParams getWindowLayoutParams(){

        return mWinLayoutParams;
    }

    /**
     * 播放弹出菜单动画
     */
    public void showMenu(boolean isBallRight) {

        if(mMenuView.getParent() == null){

            if(mClearCount == -1){

                updateMenuIcons();
            }

            mWindowManager.addView(mMenuView, mWinLayoutParams);

            mScaleSpring.addListener(mSpringListener);

            if (isBallRight) {

                mMenuLayout.startAnimation(mShowAnimationSetR);

            } else {

                mMenuLayout.startAnimation(mShowAnimationSetL);

            }

            mMenuLayout.setClickable(true);

        }


    }

    public void updateMenuIcons() {

        updateMenuIcon(MENU_A);
        updateMenuIcon(MENU_B);
        updateMenuIcon(MENU_C);
        updateMenuIcon(MENU_D);
        updateMenuIcon(MENU_E);
    }

    public void closeMenu() {

        if(mMenuView.getParent() != null){

            mMenuLayout.setClickable(false);

            if (mIsBallRight) {


                mMenuLayout.startAnimation(mHideAnimationSetR);

            } else {

                mMenuLayout.startAnimation(mHideAnimationSetL);

            }
        }

    }

}
