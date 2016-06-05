package com.hardwork.fg607.relaxfinger;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

import com.hardwork.fg607.relaxfinger.receiver.ScreenOffAdminReceiver;
import com.hardwork.fg607.relaxfinger.service.FloatingBallService;
import com.hardwork.fg607.relaxfinger.utils.AppUtils;
import com.hardwork.fg607.relaxfinger.utils.Config;
import com.hardwork.fg607.relaxfinger.utils.FloatingBallUtils;
import com.hardwork.fg607.relaxfinger.view.AppSettingFragment;
import com.hardwork.fg607.relaxfinger.view.GestureFragment;
import com.hardwork.fg607.relaxfinger.view.SettingFragment;

import net.grandcentrix.tray.TrayAppPreferences;

import java.util.List;
import java.util.jar.Manifest;

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
        mIsShowTeaching = mPreferences.getBoolean("showTeaching", true);

        mSettingFragment.setGestureSettingClickListener(new SettingFragment.OnSettingClickListener() {
            @Override
            public void onGestureSettingClick() {

                FragmentTransaction transaction = getFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.animator.fragment_left_enter,
                                R.animator.fragment_left_exit,
                                R.animator.fragment_pop_left_enter,
                                R.animator.fragment_pop_left_exit);



                if (mGestureFragment == null) {

                    mGestureFragment = new GestureFragment();

                }

                transaction.replace(R.id.fragment, mGestureFragment);

                transaction.addToBackStack(null);

                transaction.commit();

                SettingActivity.this.setTitle("手势功能设置");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            }

            @Override
            public void onAppSettingClick() {

                FragmentTransaction transaction = getFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.animator.fragment_left_enter,
                                R.animator.fragment_left_exit,
                                R.animator.fragment_pop_left_enter,
                                R.animator.fragment_pop_left_exit);


                if (mAppSettingFragment == null) {


                    mAppSettingFragment = new AppSettingFragment();

                }

                transaction.replace(R.id.fragment, mAppSettingFragment);

                transaction.addToBackStack(null);

                transaction.commit();

                SettingActivity.this.setTitle("快捷菜单设置");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);


            }
        });

        getFragmentManager().beginTransaction().replace(R.id.fragment, mSettingFragment).addToBackStack(null).commit();

        //防止内存不足重绘重叠
       /* if(savedInstanceState == null){

            getFragmentManager().beginTransaction().add(R.id.fragment, mSettingFragment, "setting").commit();

        }else {

            Fragment fragmentGesture = getFragmentManager().findFragmentByTag("gesture");
            Fragment fragmentApp = getFragmentManager().findFragmentByTag("app");
            Fragment fragmentSetting = getFragmentManager().findFragmentByTag("setting");
            getFragmentManager().beginTransaction().show(fragmentSetting).hide(fragmentGesture).hide(fragmentApp).commit();
        }*/

        SettingActivity.this.setTitle("悬浮助手-RelaxFinger");

        initAccessibility();

    }

    @Override
    public void onBackPressed() {


        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 1) {

            fm.popBackStack();
            SettingActivity.this.setTitle("悬浮助手-RelaxFinger");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } else {

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

        if(mPreferences.getInt("versionCode",0)< AppUtils.getVersionCode(this)){

            showUpdateInfo();

            mPreferences.put("versionCode",AppUtils.getVersionCode(this));
        }

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

        try{
            if(mIsAdmin){
                mSettingFragment.getLockScreenSwitch().setChecked(true);
            }else {
                mSettingFragment.getLockScreenSwitch().setChecked(false);
            }
        }catch (Exception e){

            e.printStackTrace();
            Log.e("ERROR","resume error");
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

        if (id == R.id.action_question) {

            questionsAnswer();
            return true;

        }

        if(id == android.R.id.home){

            FragmentManager fm = getFragmentManager();
            if (fm.getBackStackEntryCount() > 1) {

                fm.popBackStack();
                SettingActivity.this.setTitle("悬浮助手-RelaxFinger");
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else {

                super.onBackPressed();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    public void developerInfo(){

        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("关于悬浮助手");
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setMessage("版本：v1.3.0\r\n作者：fg607\r\n邮箱：fg607@sina.com");
        dialog.show();
    }

    public void questionsAnswer(){

        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("常见问题解答");
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setMessage("1.什么是自由模式：当切换到横屏或者弹出软键盘时会切换到自由模式，自由模式下" +
                "悬浮球可以自由移动，点击为返回键，双击回到桌面，长按屏幕截图（可在设置界面取消长按截图）" +
                "，其它手势不可用。\r\n" +
                "2.不能卸载软件：在设置界面关闭“开启锁屏”选项后，即可正常卸载。\r\n" +
                "3.屏幕截图没反应：部分手机在第一次屏幕截图时需要稍等片刻，弹出授权框后，点击允许即可。\r\n" +
                "4.截图保存在哪里：截图保存在系统存储卡根目录RelaxFinger文件夹里面。\r\n" +
                "5.避让软键盘无效：避让软键盘功能需要安装两个及以上输入法时生效（包含系统自带输入法）。\r\n" +
                "6.不能开机自启动：首先确保设置界面“开机启动”选项已开启，如果仍然不能启动，到系统设置->" +
                "安全->应用程序许可中找到RelaxFinger,点击进去后打开自动运行开关即可。\r\n" +
                "7.自定义主题不好看：在系统存储卡根目录找到RelaxFinger目录，将里面的DIY.png换成喜欢的图片" +
                "，确保新图片名称依然是DIY.png即可。");
        dialog.show();
    }

    public void showUpdateInfo(){

        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("悬浮助手-1.3.0版本更新内容");
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setMessage("1.添加隐藏悬浮球到通知栏功能。\r\n" +
                "2.添加手势震动反馈功能。\r\n" +
                "3.添加自由模式下长按截屏开关。\r\n" +
                "4.优化整体操作体验：优化自带主题及自定义主题，增加悬浮球触摸动画。\r\n" +
                "5.双击手势为“无操作”时，可提高返回键响应速度。\r\n" +
                "6.修复截屏不能在QQ及微信显示的问题。\r\n" +
                "7.修复按下Home键后5秒内不能打开快捷应用的问题。\r\n" +
                "8.修复主题名称不随主题改变的问题。");
        dialog.show();

    }

}
