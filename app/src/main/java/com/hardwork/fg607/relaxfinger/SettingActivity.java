package com.hardwork.fg607.relaxfinger;

import android.app.AlertDialog;
import android.app.Fragment;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import com.hardwork.fg607.relaxfinger.service.FloatService;
import com.hardwork.fg607.relaxfinger.utils.AppUtils;
import com.hardwork.fg607.relaxfinger.model.Config;
import com.hardwork.fg607.relaxfinger.utils.FloatingBallUtils;
import com.hardwork.fg607.relaxfinger.view.AppSettingFragment;
import com.hardwork.fg607.relaxfinger.view.GestureFragment;
import com.hardwork.fg607.relaxfinger.view.SettingFragment;

import net.grandcentrix.tray.TrayAppPreferences;

import static com.hardwork.fg607.relaxfinger.utils.AccessibilityUtil.checkAccessibility;
import static com.hardwork.fg607.relaxfinger.utils.AccessibilityUtil.isServiceRunning;

public class SettingActivity extends AppCompatActivity {

    private AccessibilityManager mManager;
    private AlertDialog mAlertDialog;
    private SettingFragment mSettingFragment;
    private GestureFragment mGestureFragment;
    private AppSettingFragment mAppSettingFragment;
    private FragmentTransaction mTransaction;
    private TrayAppPreferences mPreferences;
    public static Messenger sMessenger = null;
    private boolean mBound = false;

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

    private boolean mIsAlertShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPreferences = FloatingBallUtils.getMultiProcessPreferences();

        setContentView(R.layout.activity_setting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initFragments();

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment, mSettingFragment).addToBackStack(null).commit();

        SettingActivity.this.setTitle("悬浮助手-RelaxFinger");

        initAccessibility();

    }


    @Override
    protected void onResume() {
        super.onResume();

        hideAlertDialog();

        if(checkAccessibility()){

            mIsAlertShowing = false;
            checkUpgrade();

        }else {

            mIsAlertShowing = true;
            openAlertDialog();
        }

        if(isServiceRunning()){

            bindFloatService();
        }

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
        SettingActivity.this.setTitle("快捷菜单设置");
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
        SettingActivity.this.setTitle("手势功能设置");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public void onBackPressed() {

        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 1) {

            fm.popBackStackImmediate();
            SettingActivity.this.setTitle("悬浮助手-RelaxFinger");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        } else {

            finish();
        }
    }

    private void initAccessibility() {

        mManager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
    }


    private void hideAlertDialog() {

        if (mAlertDialog != null && mAlertDialog.isShowing()) {

            mAlertDialog.dismiss();
        }
    }

    private void checkUpgrade() {
        if (mPreferences.getInt("versionCode", 0) < AppUtils.getVersionCode(this)) {

            showUpdateInfo();

            mPreferences.put("versionCode", AppUtils.getVersionCode(this));
        }
    }

    public void openAlertDialog() {

        mAlertDialog = new android.app.AlertDialog.Builder(this).create();
        mAlertDialog.setTitle("激活导航服务");
        mAlertDialog.setMessage("您还没有激活导航服务。" + "在设置中：系统 → 辅助功能 → 服务 中激活" + getResources().getString(R.string.app_name)
                + "后，便可进行快捷导航");
        mAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "去激活", new DialogInterface.OnClickListener() {
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
        mAlertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
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
                SettingActivity.this.setTitle("悬浮助手-RelaxFinger");
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
        dialog.setMessage("版本：v1.5.0(Beta)\r\n作者：fg607\r\n邮箱：fg607@sina.com");
        dialog.show();
    }

    public void questionsAnswer() {

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
                "5.避让软键盘无效：避让软键盘功能需要安装两个及以上输入法时生效（包含系统自带输入法）。" +
                "如果仍然无效,打开输入法,把通知栏打开看一下选择输入法通知的标题,反馈给我,我加到软件里面就可以了。\r\n" +
                "6.不能开机自启动：首先确保设置界面“开机启动”选项已开启，如果仍然不能启动，到系统设置->" +
                "安全->应用程序许可中找到RelaxFinger,点击进去后打开自动运行开关即可。\r\n" +
                "7.自定义主题不好看：在系统存储卡根目录找到RelaxFinger目录，将里面的DIY.png换成喜欢的图片" +
                "，确保新图片名称依然是DIY.png即可。\r\n" +
                "8.若频繁需要重新激活,系统设置->安全->应用程序许可->RelaxFinger->启用自动运行," +
                "部分国产手机->电池管理->受保护应用->启用悬浮助手,任务管理器中的一键清除也会杀掉悬浮助手," +
                "可以在任务管理界面,给悬浮助手加上锁即可,手机不同加锁方法自行百度," +
                "华为是任务管理器界面按住悬浮助手往下拉，MIUI好像是就有个锁，点一下就好了。\r\n" +
                "9.安卓6.0及以上系统出现叠加层解决方法:在系统设置->开发者选项->停用HW叠加层即可。");
        dialog.show();
    }

    public void showUpdateInfo() {

        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("悬浮助手-1.5.0(Beta)版本更新内容");
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setMessage("(代码重构,建议重新安装!)\r\n" +
                "1.重构核心功能代码,提高稳定性和运行效率。\r\n" +
                "2.横屏下可以使用快捷菜单和其他完整功能。\r\n" +
                "3.优化截屏,截屏更迅速。\r\n" +
                "4.安卓6.0以上系统可以使用打开Wifi,蓝牙,屏幕旋转等功能。\r\n" +
                "5.恢复悬浮球不被键盘遮挡。\r\n" +
                "6.提高双击响应速度。\r\n" +
                "7.删除崩溃反馈功能。\r\n" +
                "8.优化获取系统权限机制。\r\n" +
                "9.添加热门桌面快捷方式权限。\r\n" +
                "10.切换上一应用排除桌面。\r\n" +
                "11.修复已知bug。");
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

    public boolean isAlertShowing() {

        return mIsAlertShowing;
    }
}
