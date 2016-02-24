package com.hardwork.fg607.relaxfinger.view;


import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hardwork.fg607.relaxfinger.MyApplication;
import com.hardwork.fg607.relaxfinger.R;
import com.hardwork.fg607.relaxfinger.receiver.ScreenOffAdminReceiver;
import com.hardwork.fg607.relaxfinger.service.FloatingBallService;
import com.hardwork.fg607.relaxfinger.utils.Config;
import com.hardwork.fg607.relaxfinger.utils.FloatingBallUtils;
import com.jenzz.materialpreference.SwitchPreference;

import net.grandcentrix.tray.TrayAppPreferences;

public class SettingFragment extends PreferenceFragment implements OnPreferenceChangeListener {

    private SwitchPreference mFloatSwitch;
    private SwitchPreference mMoveSwitch;
    private SwitchPreference mToEdgeSwitch;
    private SwitchPreference mLockScreenSwitch;
    private SwitchPreference mAutoStartSwitch;
    private SwitchPreference mVibratorSwitch;
    private com.jenzz.materialpreference.Preference mGestureSetting;
    private com.jenzz.materialpreference.Preference mAppSetting;
    private com.jenzz.materialpreference.Preference mFloatBallSize;

    private boolean mIsAdmin;
    private DevicePolicyManager mDeviceManager;
    private ComponentName mComponentName;
    private OnSettingClickListener mClickListener;
    private TrayAppPreferences mPreferences;
    private SharedPreferences mSharePreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences_setting);

        mSharePreferences = FloatingBallUtils.getSharedPreferences();

        mPreferences = FloatingBallUtils.getMultiProcessPreferences();

        initPreferences();

        mDeviceManager = (DevicePolicyManager) MyApplication.getApplication().getSystemService(Context.DEVICE_POLICY_SERVICE);
        mComponentName = new ComponentName(MyApplication.getApplication(), ScreenOffAdminReceiver.class);

        checkSetting();


    }

    public void setGestureSettingClickListener(OnSettingClickListener listener) {

        mClickListener = listener;
    }


    private void checkSetting() {

        sendMsg(Config.FLOAT_SWITCH, "ballstate", mFloatSwitch.isChecked());

        if (!mDeviceManager.isAdminActive(mComponentName)) {

            mLockScreenSwitch.setChecked(false);
        }

        boolean canMove = mPreferences.getBoolean("moveSwitch", false);

        sendMsg(Config.MOVE_SWITCH, "canmove", canMove);

        mMoveSwitch.setChecked(canMove);

        int position = mSharePreferences.getInt("seekbar_position", 50);

        mFloatBallSize.setSummary(position + "");

    }


    private void initPreferences() {


        mFloatSwitch = (SwitchPreference) findPreference("floatSwitch");
        mFloatSwitch.setOnPreferenceChangeListener(this);
        mMoveSwitch = (SwitchPreference) findPreference("moveSwitch");
        mMoveSwitch.setOnPreferenceChangeListener(this);
        mVibratorSwitch = (SwitchPreference) findPreference("vibratorSwitch");
        mVibratorSwitch.setOnPreferenceChangeListener(this);
        mToEdgeSwitch = (SwitchPreference) findPreference("toEdgeSwitch");
        mToEdgeSwitch.setOnPreferenceChangeListener(this);
        mLockScreenSwitch = (SwitchPreference) findPreference("lockScreenSwitch");
        mLockScreenSwitch.setOnPreferenceChangeListener(this);
        mAutoStartSwitch = (SwitchPreference) findPreference("autoStartSwitch");
        mAutoStartSwitch.setOnPreferenceChangeListener(this);
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

                showSeekBarDialog();

                return true;
            }
        });
    }

    private void showSeekBarDialog() {

        //获取seekbar位置
        int position = mSharePreferences.getInt("seekbar_position", 50);

        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        builder.setTitle("悬浮球大小");
        View layout = getActivity().getLayoutInflater().inflate(R.layout.ballsize_dialog_layout, null);
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

      /*  new MaterialDialog.Builder(getActivity())
                .title("悬浮球大小")
                .customView(R.layout.ballsize_dialog_layout, false)
                .negativeText("取消")
                .show();*/
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
            case "toEdgeSwitch":
                toEdgeChange((boolean) newValue);
                break;
            case "lockScreenSwitch":
                lockScreenChange((boolean) newValue);
                break;
            case "autoStartSwitch":
                autoStartChange((boolean) newValue);
                break;
            default:
                break;

        }


        return true;
    }

    private void vibratorChange(boolean newValue) {

        mPreferences.put("vibratorSwitch", newValue);

        sendMsg(Config.VIBRATOR_SWITCH, "isVibrate", newValue);
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
                getActivity().startActivity(intent);
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

    private void FloatChange(boolean newValue) {

        mPreferences.put("floatSwitch", newValue);

        sendMsg(Config.FLOAT_SWITCH, "ballstate", newValue);

    }


    /**
     * 向Ｓervice发送消息
     *
     * @param action
     */
    public void sendMsg(String name, String action) {
        Intent intent = new Intent();
        intent.putExtra(name, action);
        intent.setClass(getActivity(), FloatingBallService.class);
        getActivity().startService(intent);
    }

    public void sendMsg(int what, String name, int msg) {
        Intent intent = new Intent();
        intent.putExtra("what", what);
        intent.putExtra(name, msg);
        intent.setClass(getActivity(), FloatingBallService.class);
        getActivity().startService(intent);
    }

    public void sendMsg(int what, String name, boolean action) {
        Intent intent = new Intent();
        intent.putExtra("what", what);
        intent.putExtra(name, action);
        intent.setClass(getActivity(), FloatingBallService.class);
        getActivity().startService(intent);
    }


    public SwitchPreference getFloatSwitch() {
        return mFloatSwitch;
    }

    public SwitchPreference getMoveSwitch() {
        return mMoveSwitch;
    }

    public SwitchPreference getLockScreenSwitch() {
        return mLockScreenSwitch;
    }

    public SwitchPreference getAutoStartSwitch() {
        return mAutoStartSwitch;
    }

    /* @Override
     public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
         Animator animator = null;

         if (nextAnim == R.animator.fragment_pop_left_enter) {
             animator = AnimatorInflater.loadAnimator(getActivity(), nextAnim);
             if (animator != null && enter) {

                 animator.addListener(new Animator.AnimatorListener() {
                     @Override
                     public void onAnimationStart(Animator animation) {
                         if(mCallBack != null){

                             mCallBack.enterAnimationEnd();

                         }
                     }

                     @Override
                     public void onAnimationEnd(Animator animation) {
                         *//*if(mCallBack != null){

                            mCallBack.enterAnimationEnd();

                        }*//*
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
            }
        }
        return animator;
    }
*/
    public interface OnSettingClickListener {

        public void onGestureSettingClick();

        public void onAppSettingClick();
    }

    @Override
    public void onResume() {
        super.onResume();

        boolean canMove = mPreferences.getBoolean("moveSwitch", false);

        mMoveSwitch.setChecked(canMove);
    }
}
