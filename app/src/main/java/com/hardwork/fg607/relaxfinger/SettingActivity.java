package com.hardwork.fg607.relaxfinger;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.hardwork.fg607.relaxfinger.service.FloatService;
import com.hardwork.fg607.relaxfinger.utils.AppUtils;
import com.hardwork.fg607.relaxfinger.model.Config;
import com.hardwork.fg607.relaxfinger.utils.FloatingBallUtils;
import com.hardwork.fg607.relaxfinger.view.AppSettingFragment;
import com.hardwork.fg607.relaxfinger.view.GestureFragment;
import com.hardwork.fg607.relaxfinger.view.HideSettingFragment;
import com.hardwork.fg607.relaxfinger.view.NotifySettingFragment;
import com.hardwork.fg607.relaxfinger.view.SettingFragment;

import net.grandcentrix.tray.AppPreferences;
import net.grandcentrix.tray.TrayAppPreferences;

import static com.hardwork.fg607.relaxfinger.utils.AccessibilityUtil.checkAccessibility;
import static com.hardwork.fg607.relaxfinger.utils.AccessibilityUtil.isServiceRunning;

public class SettingActivity extends AppCompatActivity {

    private AlertDialog mAlertDialog;
    private SettingFragment mSettingFragment;
    private GestureFragment mGestureFragment;
    private AppSettingFragment mAppSettingFragment;
    private HideSettingFragment mHideSettingFragment;
    private NotifySettingFragment mNotifySettingFragment;
    private FragmentTransaction mTransaction;
    private AppPreferences mPreferences;
    public static Messenger sMessenger = null;
    private boolean mBound = false;
    private FloatingActionButton mFab;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            sMessenger = new Messenger(service);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            sMessenger = null;
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPreferences = FloatingBallUtils.getMultiProcessPreferences();

        setContentView(R.layout.activity_setting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFab = (FloatingActionButton) findViewById(R.id.fab);

        mFab.hide();

        initFragments();

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment, mSettingFragment).addToBackStack(null).commit();

        SettingActivity.this.setTitle(R.string.title_activity_setting);

    }


    @Override
    protected void onResume() {
        super.onResume();

        checkUpgrade();


       /* if(isServiceRunning()){

            bindFloatService();
        }*/

        if(mAppSettingFragment != null){

            mAppSettingFragment.hideFuncDialog();
        }

    }

    public void bindFloatService(){

        if(!mBound){

            bindService(new Intent(this,FloatService.class),mServiceConnection, Context.BIND_AUTO_CREATE);
        }

    }

    public void unbindFloatService(){

        if(mBound){

            unbindService(mServiceConnection);

            mBound = false;
            sMessenger = null;
        }

    }

    public void startFloatService() {

        Intent intent = new Intent();
        intent.setClass(this, FloatService.class);
        intent.putExtra("what", Config.FLOAT_SWITCH);
        intent.putExtra("ballstate", true);
        startService(intent);
    }

    public void stopFloatService() {

        Intent intent = new Intent();
        intent.setClass(this, FloatService.class);
        intent.putExtra("what", Config.FLOAT_SWITCH);
        intent.putExtra("ballstate", false);
        startService(intent);
    }


    private void initFragments() {

        mSettingFragment = new SettingFragment();

        mSettingFragment.setGestureSettingClickListener(new SettingFragment.OnSettingClickListener() {
            @Override
            public void onGestureSettingClick() {

                showGestureSetting();

            }

            @Override
            public void onAppSettingClick() {

                showAppSetting();


            }

            @Override
            public void onNotifySettingClick() {

                showNotifySetting();
            }

            @Override
            public void onHideSettingClick() {

                showHideSetting();

            }
        });

    }

    private void showAppSetting() {

        if (mAppSettingFragment == null) {


            mAppSettingFragment = new AppSettingFragment();

        }

        mTransaction = getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        mTransaction.replace(R.id.fragment, mAppSettingFragment);

        mTransaction.addToBackStack(null);

        mTransaction.commit();

        getFragmentManager().executePendingTransactions();
        SettingActivity.this.setTitle(R.string.title_menu_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void showGestureSetting() {

        if (mGestureFragment == null) {

            mGestureFragment = new GestureFragment();

        }

        mTransaction = getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        mTransaction.replace(R.id.fragment, mGestureFragment);

        mTransaction.addToBackStack(null);

        mTransaction.commitAllowingStateLoss();

        getFragmentManager().executePendingTransactions();
        SettingActivity.this.setTitle(R.string.title_gesture_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void showNotifySetting(){


        if (mNotifySettingFragment == null) {


            mNotifySettingFragment = new NotifySettingFragment();

        }

        mTransaction = getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        mTransaction.replace(R.id.fragment, mNotifySettingFragment);

        mTransaction.addToBackStack(null);

        mTransaction.commit();

        getFragmentManager().executePendingTransactions();
        SettingActivity.this.setTitle(R.string.title_notify_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        showFab();
    }

    private void showHideSetting(){

        if (mHideSettingFragment == null) {


            mHideSettingFragment = new HideSettingFragment();

        }

        mTransaction = getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        mTransaction.replace(R.id.fragment, mHideSettingFragment);

        mTransaction.addToBackStack(null);

        mTransaction.commit();

        getFragmentManager().executePendingTransactions();
        SettingActivity.this.setTitle(R.string.title_hide_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        showFab();
    }


    @Override
    public void onBackPressed() {

        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 1) {

            fm.popBackStackImmediate();
            SettingActivity.this.setTitle(R.string.title_activity_setting);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        } else {

            clearMemory();
            finish();
        }
    }

    public void clearMemory(){

        if(mAppSettingFragment != null){

            mAppSettingFragment.clearMemory();

            mAppSettingFragment = null;
        }

        if(mSettingFragment != null){

            mSettingFragment.clearMemory();

            mSettingFragment = null;
        }

        if(mGestureFragment != null){

            mGestureFragment.clearMemory();

            mGestureFragment = null;
        }

        System.gc();
        System.runFinalization();
    }

    @Override
    protected void onDestroy() {

        clearMemory();
        super.onDestroy();
    }


    private void checkUpgrade() {
        if (mPreferences.getInt("versionCode", 0) < AppUtils.getVersionCode(this)) {

            showUpdateInfo();

            mPreferences.put("versionCode", AppUtils.getVersionCode(this));
        }
    }

    public void openAlertDialog() {

        mAlertDialog = new android.app.AlertDialog.Builder(this).create();
        mAlertDialog.setTitle("开启辅助功能");
        mAlertDialog.setMessage("辅助功能未开启，悬浮助手后台服务需要使用辅助功能，是否开启？");
        mAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "前往开启", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                try {
                    startActivity(new Intent("android.settings.ACCESSIBILITY_SETTINGS"));

                } catch (ActivityNotFoundException e) {

                    e.printStackTrace();

                    Toast.makeText(SettingActivity.this, "没有找到辅助功能设置界面，请手动开启！", Toast.LENGTH_SHORT).show();
                }


            }
        });
        mAlertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                Toast.makeText(SettingActivity.this, "辅助功能未开启，悬浮助手正常退出！", Toast.LENGTH_SHORT).show();
                saveExit();
            }
        });

        mAlertDialog.setCancelable(false);
        mAlertDialog.show();

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

        if (id == android.R.id.home) {

            FragmentManager fm = getFragmentManager();
            if (fm.getBackStackEntryCount() > 1) {

                fm.popBackStackImmediate();
                SettingActivity.this.setTitle(R.string.title_activity_setting);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else {

                super.onBackPressed();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    public void developerInfo() {

        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("关于悬浮助手");
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setMessage("版本：3.0.4.3\r\n作者：fg607\r\n邮箱：fg607@sina.com");
        dialog.show();
    }

    public void questionsAnswer() {

        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("帮助说明");
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setMessage("1.不能卸载软件：在设置界面关闭“开启锁屏”选项后，即可正常卸载。\r\n" +
                "2.屏幕截图没反应：部分手机在第一次屏幕截图时需要稍等片刻，弹出授权框后，点击允许即可。\r\n" +
                "3.截图保存在哪里：截图保存在系统存储卡根目录RelaxFinger文件夹里面。\r\n" +
                "4.避让软键盘无效：安卓7.0以下系统避让软键盘功能最好安装两个及以上输入法（包含系统自带输入法）。\r\n" +
                "5.不能开机自启动：首先确保设置界面“开机启动”选项已开启，如果仍然不能启动，到系统设置->" +
                "安全->应用程序许可中找到RelaxFinger,点击进去后打开自动运行开关即可。\r\n" +
                "6.自定义主题不好看：在系统存储卡根目录找到RelaxFinger目录，将里面的DIY.png换成喜欢的图片" +
                "，确保新图片名称依然是DIY.png即可。\r\n" +
                "7.若频繁需要重新激活,系统设置->安全->应用程序许可->RelaxFinger->启用自动运行," +
                "部分国产手机->电池管理->受保护应用->启用悬浮助手,任务管理器中的一键清除也会杀掉悬浮助手," +
                "可以在任务管理界面,给悬浮助手加上锁即可,手机不同加锁方法自行百度," +
                "华为是任务管理器界面按住悬浮助手往下拉，MIUI好像是就有个锁，点一下就好了。\r\n" +
                "8.临时移动模式：悬浮球会向上移动一段距离，可自由移动，点击退出临时移动模式。打开关闭输入法会自动"+
                "进入和退出临时移动模式。\r\n"+
                "9.显示消息通知：当接收到消息时，悬浮球会变成相应的APP图标，并晃动提示，点击打开消息，上滑忽略"+
                "当前消息，下滑忽略所有消息。\r\n"+
                "10.安卓6.0及以上系统出现叠加层解决方法:在系统设置->开发者选项->停用HW叠加层即可。");
        dialog.show();
    }

    public void showUpdateInfo() {

        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("悬浮助手-3.0.4.3版本更新内容");
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setMessage("" +
                "1.全新的切换上一应用模式，速度更快，不再需要“使用情况访问权限”。\r\n"+
                "2.修复屏幕旋转权限问题。\r\n"+
                "3.优化快捷菜单半透明背景，覆盖状态栏和导航栏。\r\n"+
                "4.修复选择切换上一应用手势不能立即生效的问题。"+
                "");
        dialog.show();

    }

    @Override
    protected void onStop() {
        super.onStop();

        unbindFloatService();
    }

    private void saveExit(){

        unbindFloatService();
        stopFloatService();
        finish();
    }

    public void showFab(){

        mFab.show();
    }

    public void hideFab(){

        mFab.hide();
    }

    public void setFabClickListener(View.OnClickListener listener){

        mFab.setOnClickListener(listener);
    }
}
