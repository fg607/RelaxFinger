package com.hardwork.fg607.relaxfinger;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

import com.hardwork.fg607.relaxfinger.receiver.ScreenOffAdminReceiver;
import com.hardwork.fg607.relaxfinger.service.FloatingBallService;
import com.hardwork.fg607.relaxfinger.utils.Config;
import com.hardwork.fg607.relaxfinger.utils.FloatingBallUtils;
import com.hardwork.fg607.relaxfinger.view.AppSettingFragment;
import com.hardwork.fg607.relaxfinger.view.GestureFragment;
import com.hardwork.fg607.relaxfinger.view.SettingFragment;

import net.grandcentrix.tray.TrayAppPreferences;

import java.util.List;

public class SettingActivity extends AppCompatActivity{

    private boolean mIsAccessibilityEnable;
    private boolean mIsAdmin;
    private DevicePolicyManager mDeviceManager;
    private ComponentName mComponentName;
    private AccessibilityManager mManager;
    private List< AccessibilityServiceInfo> mList;
    private AlertDialog mAlertDialog;
    private SettingFragment mSettingFragment = new SettingFragment();
    private GestureFragment mGestureFragment;
    private Fragment mAppSettingFragment;
    private boolean mIsShowTeaching;
    private TrayAppPreferences mPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPreferences = FloatingBallUtils.getMultiProcessPreferences();
        mIsShowTeaching = mPreferences.getBoolean("showTeaching",true);

        mSettingFragment.setGestureSettingClickListener(new SettingFragment.OnSettingClickListener() {
            @Override
            public void onGestureSettingClick() {

                FragmentTransaction transaction = getFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.animator.fragment_left_enter,
                                R.animator.fragment_left_exit);


                transaction.hide(mSettingFragment);

                mGestureFragment = (GestureFragment) getFragmentManager().findFragmentByTag("gesture");

                if(mGestureFragment == null){


                    mGestureFragment = new GestureFragment();
                    transaction.add(R.id.fragment, mGestureFragment, "gesture");
                }else {

                    transaction.show(mGestureFragment);
                }

                transaction.commit();

                SettingActivity.this.setTitle("手势功能设置");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            }

            @Override
            public void onAppSettingClick() {

                FragmentTransaction transaction = getFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.animator.fragment_left_enter,
                                R.animator.fragment_left_exit);


                transaction.hide(mSettingFragment);

                mAppSettingFragment =  getFragmentManager().findFragmentByTag("app");

                if(mAppSettingFragment == null){


                    mAppSettingFragment = new AppSettingFragment();
                    transaction.add(R.id.fragment, mAppSettingFragment, "app");
                }else {

                    transaction.show(mAppSettingFragment);
                }

                transaction.commit();

                SettingActivity.this.setTitle("快捷应用设置");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);


            }
        });


        getFragmentManager().beginTransaction().add(R.id.fragment, mSettingFragment,"setting").commit();


        initAccessibility();
    }

    @Override
    public void onBackPressed() {

        Fragment fragmentGesture = getFragmentManager().findFragmentByTag("gesture");
        Fragment fragmentApp = getFragmentManager().findFragmentByTag("app");
        FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(
                R.animator.fragment_pop_left_enter,
                R.animator.fragment_pop_left_exit);

        if(fragmentGesture != null && !fragmentGesture.isHidden()){

            transaction.hide(mGestureFragment);

            transaction.show(mSettingFragment).commit();

            SettingActivity.this.setTitle("RelaxFinger");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        }else if(fragmentApp != null && !fragmentApp.isHidden()) {

            transaction.hide(mAppSettingFragment);

            transaction.show(mSettingFragment).commit();

            SettingActivity.this.setTitle("RelaxFinger");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        }else {
            super.onBackPressed();
        }
    }

    private void initAccessibility() {
        mManager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
        mDeviceManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        mComponentName = new ComponentName(SettingActivity.this,ScreenOffAdminReceiver.class);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(mAlertDialog != null && mAlertDialog.isShowing()){

            mAlertDialog.dismiss();
        }
        checkAccessibility();


    }

    private void checkAccessibility() {

        mIsAccessibilityEnable = false;

        mList = mManager.getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);

        for (int i = 0; i < mList.size(); i++) {
            if ("com.hardwork.fg607.relaxfinger/.service.NavAccessibilityService".equals(mList.get(i).getId())) {
                mIsAccessibilityEnable = true;
                break;
            }
        }
        if (!mIsAccessibilityEnable) {

            openAlertDialog();

        }else if(mIsShowTeaching){

                sendMsg(Config.SHOW_TEACHING,"showTeaching",0);
                mIsShowTeaching = false;
                mPreferences.put("showTeaching", false);
        }

        mIsAdmin = mDeviceManager.isAdminActive(mComponentName);

        if(mIsAdmin){

            mSettingFragment.getLockScreenSwitch().setChecked(true);
        }else {

            mSettingFragment.getLockScreenSwitch().setChecked(false);
        }
    }

    public void openAlertDialog(){

        mAlertDialog = new AlertDialog.Builder(this).create();
        mAlertDialog.setTitle("激活导航服务");
        mAlertDialog.setMessage("您还没有激活导航服务。" + "在设置中：系统 → 辅助功能 → 服务 中激活" + getResources().getString(R.string.app_name)
                + "后，便可进行快捷导航");
        mAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "去激活", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                startActivity(new Intent("android.settings.ACCESSIBILITY_SETTINGS"));

            }
        });
        mAlertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                mSettingFragment.getFloatSwitch().setChecked(false);
                stopFloatService();
                SettingActivity.this.finish();
            }
        });

        mAlertDialog.setCancelable(false);
        mAlertDialog.show();

    }

    private void stopFloatService() {

        Intent intent = new Intent(this, FloatingBallService.class);

        stopService(intent);
    }

    public  void sendMsg(int what,String name,int msg) {
        Intent intent = new Intent();
        intent.putExtra("what",what);
        intent.putExtra(name, msg);
        intent.setClass(this, FloatingBallService.class);
        startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {

            developerInfo();
            return true;

        }

        if(id == android.R.id.home){

            Fragment fragment = getFragmentManager().findFragmentByTag("gesture");
            FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(
                    R.animator.fragment_pop_left_enter,
                    R.animator.fragment_pop_left_exit);

            if(fragment != null && !fragment.isHidden()){
                transaction.hide(mGestureFragment);
            }else {

                transaction.hide(mAppSettingFragment);
            }


            transaction.show(mSettingFragment).commit();

            SettingActivity.this.setTitle("RelaxFinger");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        return super.onOptionsItemSelected(item);
    }

    public void developerInfo(){

        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("关于RelaxFinger");
        dialog.setMessage("版本：v1.1\r\n作者：fg607\r\n邮箱：fg607@sina.com");
        dialog.show();
    }

}
