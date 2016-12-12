package com.hardwork.fg607.relaxfinger.view;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.RemoteException;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hardwork.fg607.relaxfinger.MyApplication;
import com.hardwork.fg607.relaxfinger.R;
import com.hardwork.fg607.relaxfinger.SettingActivity;
import com.hardwork.fg607.relaxfinger.receiver.ScreenOffAdminReceiver;
import com.hardwork.fg607.relaxfinger.model.Config;
import com.hardwork.fg607.relaxfinger.utils.DensityUtil;
import com.hardwork.fg607.relaxfinger.utils.FloatingBallUtils;
import com.hardwork.fg607.relaxfinger.utils.ImageUtils;
import com.jenzz.materialpreference.SwitchPreference;
import com.orm.SugarRecord;

import net.grandcentrix.tray.TrayAppPreferences;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.hardwork.fg607.relaxfinger.utils.AccessibilityUtil.checkAccessibility;
import static com.hardwork.fg607.relaxfinger.utils.AccessibilityUtil.isServiceRunning;

public class SettingFragment extends PreferenceFragment implements OnPreferenceChangeListener,View.OnClickListener {

    private SwitchPreference mFloatSwitch;
    private SwitchPreference mMoveSwitch;
    private SwitchPreference mToEdgeSwitch;
    private SwitchPreference mLockScreenSwitch;
    private SwitchPreference mAutoMoveSwitch;
    private SwitchPreference mAutoStartSwitch;
    private SwitchPreference mVibratorSwitch;
    private SwitchPreference mFeedbackSwitch;
    private SwitchPreference mAutoHideSwitch;
    private SwitchPreference mHideAreaSwitch;
    private com.jenzz.materialpreference.Preference mGestureSetting;
    private com.jenzz.materialpreference.Preference mAppSetting;
    private com.jenzz.materialpreference.Preference mFloatBallTheme;
    private com.jenzz.materialpreference.Preference mFloatBallSize;
    private com.jenzz.materialpreference.Preference mFloatBallAlpha;

    private boolean mIsAdmin;
    private DevicePolicyManager mDeviceManager;
    private ComponentName mComponentName;
    private OnSettingClickListener mClickListener;
    private TrayAppPreferences mPreferences;
    private SharedPreferences mSharePreferences;

    private Context mContext;
    private SettingActivity mActivity;

    private View mThemeView;
    @BindView(R.id.img1) RelativeLayout mImg1;
    @BindView(R.id.img2) RelativeLayout mImg2;
    @BindView(R.id.img3) RelativeLayout mImg3;
    @BindView(R.id.img4) RelativeLayout mImg4;
    @BindView(R.id.img5) RelativeLayout mImg5;
    @BindView(R.id.img6) ImageView mImg6;
    @BindView(R.id.check1) ImageView mCheck1;
    @BindView(R.id.check2) ImageView mCheck2;
    @BindView(R.id.check3) ImageView mCheck3;
    @BindView(R.id.check4) ImageView mCheck4;
    @BindView(R.id.check5) ImageView mCheck5;
    private ImageView mViewChoosed;
    private String mThemeChoosed;

    private AlertDialog mThemeDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SugarRecord.executeQuery("CREATE TABLE IF NOT EXISTS MENU_DATA_SUGAR (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, WHICH_MENU TEXT, NAME TEXT, TYPE TEXT, ACTION TEXT)");

        addPreferencesFromResource(R.xml.preferences_setting);

        mSharePreferences = FloatingBallUtils.getSharedPreferences();

        mPreferences = FloatingBallUtils.getMultiProcessPreferences();

        mDeviceManager = (DevicePolicyManager) MyApplication.getApplication().getSystemService(Context.DEVICE_POLICY_SERVICE);
        mComponentName = new ComponentName(MyApplication.getApplication(), ScreenOffAdminReceiver.class);

        initPreferences();


    }

    public void setGestureSettingClickListener(OnSettingClickListener listener) {

        mClickListener = listener;
    }


    private void initPreferences() {

        mFloatSwitch = (SwitchPreference) findPreference("floatSwitch");
        mFloatSwitch.setOnPreferenceChangeListener(this);
        mMoveSwitch = (SwitchPreference) findPreference("moveSwitch");
        mMoveSwitch.setOnPreferenceChangeListener(this);
        mVibratorSwitch = (SwitchPreference) findPreference("vibratorSwitch");
        mVibratorSwitch.setOnPreferenceChangeListener(this);
        mFeedbackSwitch = (SwitchPreference) findPreference("feedbackSwitch");
        mFeedbackSwitch.setOnPreferenceChangeListener(this);
        mToEdgeSwitch = (SwitchPreference) findPreference("toEdgeSwitch");
        mToEdgeSwitch.setOnPreferenceChangeListener(this);
        mLockScreenSwitch = (SwitchPreference) findPreference("lockScreenSwitch");
        mLockScreenSwitch.setOnPreferenceChangeListener(this);
        mAutoMoveSwitch = (SwitchPreference) findPreference("autoMoveSwitch");
        mAutoMoveSwitch.setOnPreferenceChangeListener(this);
        mAutoStartSwitch = (SwitchPreference) findPreference("autoStartSwitch");
        mAutoStartSwitch.setOnPreferenceChangeListener(this);
        mAutoHideSwitch = (SwitchPreference) findPreference("autoHideSwitch");
        mAutoHideSwitch.setOnPreferenceChangeListener(this);
        mHideAreaSwitch = (SwitchPreference) findPreference("hideAreaSwitch");
        mHideAreaSwitch.setOnPreferenceChangeListener(this);
        mGestureSetting = (com.jenzz.materialpreference.Preference) findPreference("gestureSetting");
        mGestureSetting.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                if (mClickListener != null) {

                    mClickListener.onGestureSettingClick();

                }
                return true;
            }
        });

        mAppSetting = (com.jenzz.materialpreference.Preference) findPreference("appSetting");
        mAppSetting.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                if (mClickListener != null) {

                    mClickListener.onAppSettingClick();

                }
                return true;
            }
        });

        mFloatBallSize = (com.jenzz.materialpreference.Preference) findPreference("floatBallSize");
        mFloatBallSize.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                showSizeDialog();

                return true;
            }
        });

        int position = mSharePreferences.getInt("seekbar_position", 50);
        mFloatBallSize.setSummary(position + "");

        mFloatBallAlpha = (com.jenzz.materialpreference.Preference) findPreference("floatBallAlpha");
        mFloatBallAlpha.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                showAlphaDialog();

                return true;
            }
        });

        position = mSharePreferences.getInt("alpha_position", 50);
        mFloatBallAlpha.setSummary(position + "");

        mFloatBallTheme = (com.jenzz.materialpreference.Preference) findPreference("floatBallTheme");
        mFloatBallTheme.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                showThemeDialog();

                return true;
            }
        });

        mFloatBallTheme.setSummary(mPreferences.getString("theme","默认"));
    }

    private void showThemeDialog() {

        if (mThemeDialog == null) {

            if (mThemeView == null) initThemeView();

            mThemeDialog = new AlertDialog.Builder(mContext)
                    .setTitle("悬浮球主题")
                    .setView(mThemeView)
                    .create();
        }

        mThemeDialog.show();

    }

    private void initThemeView() {

        mThemeView = LayoutInflater.from(mContext).inflate(R.layout.balltheme_dialog_layout, null);

        ButterKnife.bind(this, mThemeView);

        mImg1.setOnClickListener(this);
        mImg2.setOnClickListener(this);
        mImg3.setOnClickListener(this);
        mImg4.setOnClickListener(this);

        if(FloatingBallUtils.isFileExist("/RelaxFinger/DIY.png")){

            String filePath= Environment.getExternalStorageDirectory().getAbsolutePath()
                    +"/RelaxFinger/DIY.png";

            Bitmap icon = ImageUtils.scaleBitmap(filePath, DensityUtil.dip2px(mContext,40),
                    DensityUtil.dip2px(mContext,40));

            mImg5.setBackground(ImageUtils.bitmap2Drawable(icon));
            mImg5.setClickable(true);
            mImg5.setOnClickListener(this);

        }else {

            mImg5.setClickable(false);
        }

        mImg6.setOnClickListener(this);

        mThemeChoosed = mPreferences.getString("theme","默认");

        mViewChoosed = setIconChoosed(mThemeChoosed);


    }

    public ImageView setIconChoosed(String iconName) {

        ImageView imageView = null;
        switch (iconName) {
            case "默认":
                imageView = mCheck1;
                break;
            case "彩虹":
                imageView = mCheck2;
                break;
            case "Google":
                imageView = mCheck3;
                break;
            case "苹果":
                imageView = mCheck4;
                break;
            case "自定义":
                imageView = mCheck5;
                break;
            default:
                imageView = mCheck1;
                break;
        }

        if(imageView != null) {
            imageView.setVisibility(View.VISIBLE);
        }

        return imageView;

    }

    private void showAlphaDialog() {

        int position = mSharePreferences.getInt("alpha_position", 50);

        AlertDialog.Builder builder =
                new AlertDialog.Builder(mContext);
        builder.setTitle("悬浮球透明度");
        View layout = LayoutInflater.from(mContext).inflate(R.layout.ballsize_dialog_layout, null);
        builder.setView(layout);


        final TextView textView = (TextView) layout.findViewById(R.id.textview_size);
        final SeekBar seekBar = (SeekBar) layout.findViewById(R.id.seekbar);
        textView.setText(position + "");
        seekBar.setProgress(position);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mFloatBallAlpha.setSummary(progress + "");
                textView.setText(progress + "");

                if (progress != mSharePreferences.getInt("alpha_position", -1)) {

                    FloatingBallUtils.saveState("alpha_position", progress);

                    sendMsg(Config.BALL_ALPHA, "ballalpha", progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        builder.show();

    }

    private void showSizeDialog() {

        //获取seekbar位置
        int position = mSharePreferences.getInt("seekbar_position", 50);

        AlertDialog.Builder builder =
                new AlertDialog.Builder(mContext);
        builder.setTitle("悬浮球大小");
        View layout = LayoutInflater.from(mContext).inflate(R.layout.ballsize_dialog_layout, null);
        builder.setView(layout);


        final TextView textView = (TextView) layout.findViewById(R.id.textview_size);
        final SeekBar seekBar = (SeekBar) layout.findViewById(R.id.seekbar);
        textView.setText(position + "");
        seekBar.setProgress(position);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mFloatBallSize.setSummary(progress + "");
                textView.setText(progress + "");

                if (progress != mSharePreferences.getInt("seekbar_position", -1)) {

                    FloatingBallUtils.saveState("seekbar_position", progress);

                    sendMsg(Config.BALL_SIZE, "ballsize", progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        builder.show();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        switch (preference.getKey()) {

            case "floatSwitch":
                FloatChange((boolean) newValue);
                break;
            case "moveSwitch":
                moveChange((boolean) newValue);
                break;
            case "vibratorSwitch":
                vibratorChange((boolean) newValue);
                break;
            case "feedbackSwitch":
                feedbackChange((boolean) newValue);
                break;
            case "toEdgeSwitch":
                toEdgeChange((boolean) newValue);
                break;
            case "lockScreenSwitch":
                lockScreenChange((boolean) newValue);
                break;
            case "autoMoveSwitch":
                if(Build.VERSION.SDK_INT > 18){

                    autoMoveChange((boolean) newValue);
                }else {
                    if((boolean)newValue) {

                        Toast.makeText(mContext, "避让功能适用于4.4以上系统！", Toast.LENGTH_SHORT).show();

                        return false;
                    }
                }

                break;
            case "autoStartSwitch":
                autoStartChange((boolean) newValue);
                break;
            case "autoHideSwitch":
                autoHideChange((boolean) newValue);
                break;
            case "hideAreaSwitch":
                hideAreaChange((boolean) newValue);
                break;
            default:
                break;

        }


        return true;
    }

    private void hideAreaChange(boolean newValue) {

        mPreferences.put("hideAreaSwitch",newValue);

        sendMsg(Config.HIDE_AREA_SWITCH,"showHideArea",newValue);
    }

    private void autoHideChange(boolean newValue) {

        mPreferences.put("autoHideSwitch",newValue);

        sendMsg(Config.AUTO_HIDE_SWITCH,"isAutoHide",newValue);
    }


    private void autoMoveChange(boolean newValue) {

        openNotificationAccess();

    }

    private void openNotificationAccess() {

        try{

            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));

        }catch (ActivityNotFoundException e){

            e.printStackTrace();
        }

    }

    /**
     * 检测是否拥有通知使用权
     * @return
     */
    private boolean canAvoidKeyborad() {

        if(Build.VERSION.SDK_INT < 19){

            return false;
        }

            String pkgName = mActivity.getPackageName();
        final String flat = Settings.Secure.getString(mContext.getContentResolver(),
                "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void vibratorChange(boolean newValue) {

        mPreferences.put("vibratorSwitch", newValue);

        sendMsg(Config.VIBRATOR_SWITCH, "isVibrate", newValue);
    }

    private void feedbackChange(boolean newValue) {

        mPreferences.put("feedbackSwitch", newValue);

        sendMsg(Config.FEEDBACK_SWITCH, "isFeedback", newValue);
    }

    private void toEdgeChange(boolean newValue) {

        mPreferences.put("toEdgeSwitch", newValue);

        sendMsg(Config.TO_EDGE_SWITCH, "isToEdge", newValue);

    }

    private void autoStartChange(boolean newValue) {

        mPreferences.put("autoStartSwitch", newValue);

    }

    private void lockScreenChange(boolean newValue) {

        mPreferences.put("lockScreenSwitch", newValue);
        mIsAdmin = mDeviceManager.isAdminActive(mComponentName);

        if (newValue) {

            if (!mIsAdmin) {
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
                mContext.startActivity(intent);
            }
        } else {

            if (mIsAdmin) {

                mDeviceManager.removeActiveAdmin(mComponentName);
            }
        }

    }

    private void moveChange(boolean newValue) {

        mPreferences.put("moveSwitch", newValue);
        sendMsg(Config.MOVE_SWITCH, "canmove", newValue);

    }

    public void FloatChange(boolean newValue) {

        if(!newValue){

            exitFloatService();

        }else {

            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

                requestDrawOverLays();

            }else {

                activateFloatService();
            }


        }


    }

    private void exitFloatService() {

        mActivity.unbindFloatService();

        mActivity.stopFloatService();

        mPreferences.put("floatSwitch", false);
    }

    private void activateFloatService() {

        mActivity.startFloatService();

        mActivity.bindFloatService();

        mPreferences.put("floatSwitch", true);
    }

    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;

    @TargetApi(Build.VERSION_CODES.M)
    public void requestDrawOverLays() {
        if (!Settings.canDrawOverlays(mActivity)) {
            Toast.makeText(mContext, "悬浮助手需要开启在其他应用上层显示权限!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + mActivity.getPackageName()));
            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
        }else {

            activateFloatService();
        }
    }

    public void sendMsg(int what, String name, int msg) {

        Message message = Message.obtain();

        message.what = what;

        Bundle bundle = new Bundle();

        bundle.putInt(name,msg);

        message.setData(bundle);

        try {
            if(SettingActivity.sMessenger != null){

                SettingActivity.sMessenger.send(message);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(int what, String name, String msg) {

        Message message = Message.obtain();

        message.what = what;

        Bundle bundle = new Bundle();

        bundle.putString(name,msg);

        message.setData(bundle);

        try {
            if(SettingActivity.sMessenger != null){

                SettingActivity.sMessenger.send(message);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(int what, String name, boolean action) {

        Message message = Message.obtain();

        message.what = what;

        Bundle bundle = new Bundle();

        bundle.putBoolean(name,action);

        message.setData(bundle);

        try {
            if(SettingActivity.sMessenger != null){

                SettingActivity.sMessenger.send(message);

            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        boolean isDIY = false;

        switch (v.getId())
        {
            case R.id.img1:
                mThemeChoosed="默认";
                break;
            case R.id.img2:
                mThemeChoosed="彩虹";
                break;
            case R.id.img3:
                mThemeChoosed="Google";
                break;
            case R.id.img4:
                mThemeChoosed="苹果";
                break;
            case R.id.img5:
                mThemeChoosed="自定义";
                break;
            case R.id.img6:
                isDIY = true;
               choosePicture();
                break;
            default:
                break;
        }

        if(!isDIY){

            if(mPreferences==null){

                mPreferences = FloatingBallUtils.getMultiProcessPreferences();
            }

            if(mPreferences != null){

                mPreferences.put("theme",mThemeChoosed);
            }


            if(mViewChoosed == null){

                mViewChoosed = setIconChoosed(mPreferences.getString("theme","默认"));
            }
            if(mViewChoosed != null){

                mViewChoosed.setVisibility(View.INVISIBLE);
            }

            mViewChoosed = setIconChoosed(mThemeChoosed);


            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                if(!FloatingBallUtils.checkPermissionGranted(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE)){

                    FloatingBallUtils.hideToNotifybar();
                }

            }


            sendMsg(Config.FLOAT_THEME, "theme", mThemeChoosed);

            mFloatBallTheme.setSummary(mThemeChoosed);
        }


    }

    private void choosePicture() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
        try {
            startActivityForResult(intent, Config.REQUEST_PICK);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(mActivity)) {
                    mFloatSwitch.setChecked(false);
                    Toast.makeText(mContext, "开启悬浮助手需要授权在其他应用上层显示!", Toast.LENGTH_SHORT).show();
                }else {

                    activateFloatService();
                }
            }
        }else if(requestCode == Config.REQUEST_PICK) {

            if(data != null) {

                try {

                    Uri imageUri = data.getData();

                    try {
                        //将选择的图片进行暂存
                        FloatingBallUtils.bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), imageUri);
                        Intent intent = new Intent();
                        intent.setClass(mContext, ClipImageActivity.class);
                        startActivityForResult(intent,Config.REQUEST_CLIP);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }catch (OutOfMemoryError e){

                    Toast.makeText(mContext,"图片过大，无法正常加载！",Toast.LENGTH_SHORT).show();
                }

            }

        }
        //得到裁剪后的图标
        else if (requestCode == Config.REQUEST_CLIP) {

            String iconName = null;

            if(data != null) {
                iconName = data.getStringExtra("filename");
            }


            if (iconName != null && FloatingBallUtils.bitmap!=null) {
                try {
                    FloatingBallUtils.saveBitmap(FloatingBallUtils.bitmap,iconName);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(mImg5==null){

                    FloatingBallUtils.bitmap = null;
                    return;
                }

                //显示裁剪后的图标
                mImg5.setBackground(ImageUtils.bitmap2Drawable(FloatingBallUtils.bitmap));

                mImg5.setClickable(true);

                mImg5.setOnClickListener(this);

                if("自定义".equals(mThemeChoosed)){

                    //等待绑定sevice
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(100);
                                sendMsg(Config.FLOAT_THEME, "theme", mThemeChoosed);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                    mFloatBallTheme.setSummary(mThemeChoosed);
                }

                FloatingBallUtils.bitmap = null;
            }
        }

    }

    public interface OnSettingClickListener {

        public void onGestureSettingClick();

        public void onAppSettingClick();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!mDeviceManager.isAdminActive(mComponentName)) {

            mLockScreenSwitch.setChecked(false);
        }

        boolean canMove = mPreferences.getBoolean("moveSwitch", false);

        mMoveSwitch.setChecked(canMove);


        if(!canAvoidKeyborad()){

            mAutoMoveSwitch.setChecked(false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if(!Settings.canDrawOverlays(mActivity)){

                mFloatSwitch.setChecked(false);
                if(isServiceRunning(mContext,"com.hardwork.fg607.relaxfinger.service.FloatService")){

                    mActivity.stopFloatService();
                }
            }
        }

        if(!mFloatSwitch.isChecked()){

            if(isServiceRunning()){

                mActivity.unbindFloatService();
                mActivity.stopFloatService();
            }

        }else {
            if(!mActivity.isAlertShowing()){

                mActivity.startFloatService();
                mActivity.bindFloatService();
            }

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mActivity = (SettingActivity) getActivity();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        mActivity = (SettingActivity) getActivity();
    }
}
