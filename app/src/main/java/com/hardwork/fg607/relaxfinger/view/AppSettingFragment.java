package com.hardwork.fg607.relaxfinger.view;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hardwork.fg607.relaxfinger.MyApplication;
import com.hardwork.fg607.relaxfinger.R;
import com.hardwork.fg607.relaxfinger.adapter.AppAdapter;
import com.hardwork.fg607.relaxfinger.adapter.MyPagerAdapter;
import com.hardwork.fg607.relaxfinger.adapter.ToolAdapter;
import com.hardwork.fg607.relaxfinger.model.AppInfo;
import com.hardwork.fg607.relaxfinger.model.ToolInfo;
import com.hardwork.fg607.relaxfinger.service.FloatingBallService;
import com.hardwork.fg607.relaxfinger.utils.AppUtils;
import com.hardwork.fg607.relaxfinger.utils.Config;
import com.hardwork.fg607.relaxfinger.utils.FloatingBallUtils;
import com.hardwork.fg607.relaxfinger.utils.ImageUtils;

import net.grandcentrix.tray.TrayAppPreferences;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AppSettingFragment extends Fragment implements View.OnClickListener{

    @Bind(R.id.app1_layout) RelativeLayout mLayout1;
    @Bind(R.id.app2_layout) RelativeLayout mLayout2;
    @Bind(R.id.app3_layout) RelativeLayout mLayout3;
    @Bind(R.id.app4_layout) RelativeLayout mLayout4;
    @Bind(R.id.app5_layout) RelativeLayout mLayout5;
    @Bind(R.id.app1_name) TextView mAppTextView1;
    @Bind(R.id.app2_name) TextView mAppTextView2;
    @Bind(R.id.app3_name) TextView mAppTextView3;
    @Bind(R.id.app4_name) TextView mAppTextView4;
    @Bind(R.id.app5_name) TextView mAppTextView5;
    @Bind(R.id.icon_app1) ImageView mAppIcon1;
    @Bind(R.id.icon_app2) ImageView mAppIcon2;
    @Bind(R.id.icon_app3) ImageView mAppIcon3;
    @Bind(R.id.icon_app4) ImageView mAppIcon4;
    @Bind(R.id.icon_app5) ImageView mAppIcon5;
    private String mAppName;
    private TextView mCurrentTextView;
    private ImageView mCurrentIcon;
    private TrayAppPreferences mPreferences;
    private String mCurrentApp;
    private FunctionDialog mFuncDialog;
    private Activity mActivity;

    static ArrayList<AppInfo> appList;
    static ArrayList<ToolInfo> toolList;


    public AppSettingFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_app_setting, container, false);

        ButterKnife.bind(this,fragmentView);
        mPreferences = FloatingBallUtils.getMultiProcessPreferences();
        //initDialog();
        intView();

        new Thread(new Runnable() {
            @Override
            public void run() {
                appList =  AppUtils.getAppInfos();
                toolList = FloatingBallUtils.getToolInfos();
            }
        }).start();


        return fragmentView;
    }

    private void intView() {

        mLayout1.setOnClickListener(this);
        mLayout2.setOnClickListener(this);
        mLayout3.setOnClickListener(this);
        mLayout4.setOnClickListener(this);
        mLayout5.setOnClickListener(this);

        String name;
        int type;

        name = mPreferences.getString("app1","");
        type = mPreferences.getInt("type1",0);
        if(name != ""){

            if(type==0){
                mAppTextView1.setText(AppUtils.getAppName(name));
                mAppIcon1.setBackground(null);
                mAppIcon1.setImageDrawable(AppUtils.getAppIcon(name));
            }else if(type==1){
                mAppTextView1.setText(name);
                mAppIcon1.setBackgroundResource(R.drawable.path_blue_oval);
                mAppIcon1.setImageDrawable(FloatingBallUtils.getSwitcherIcon(name));
            }


        }

        name = mPreferences.getString("app2","");
        type = mPreferences.getInt("type2",0);

        if(name != ""){

            if(type==0){
                mAppTextView2.setText(AppUtils.getAppName(name));
                mAppIcon2.setBackground(null);
                mAppIcon2.setImageDrawable(AppUtils.getAppIcon(name));
            }else if(type==1){
                mAppTextView2.setText(name);
                mAppIcon2.setBackgroundResource(R.drawable.path_blue_oval);
                mAppIcon2.setImageDrawable(FloatingBallUtils.getSwitcherIcon(name));
            }
        }


        name = mPreferences.getString("app3","");
        type = mPreferences.getInt("type3",0);

        if(name != ""){

            if(type==0){
                mAppTextView3.setText(AppUtils.getAppName(name));
                mAppIcon3.setBackground(null);
                mAppIcon3.setImageDrawable(AppUtils.getAppIcon(name));
            }else if(type==1){
                mAppTextView3.setText(name);
                mAppIcon3.setBackgroundResource(R.drawable.path_blue_oval);
                mAppIcon3.setImageDrawable(FloatingBallUtils.getSwitcherIcon(name));
            }
        }

        name = mPreferences.getString("app4","");
        type = mPreferences.getInt("type4",0);

        if(name != ""){

            if(type==0){
                mAppTextView4.setText(AppUtils.getAppName(name));
                mAppIcon4.setBackground(null);
                mAppIcon4.setImageDrawable(AppUtils.getAppIcon(name));
            }else if(type==1){
                mAppTextView4.setText(name);
                mAppIcon4.setBackgroundResource(R.drawable.path_blue_oval);
                mAppIcon4.setImageDrawable(FloatingBallUtils.getSwitcherIcon(name));
            }
        }

        name = mPreferences.getString("app5","");
        type = mPreferences.getInt("type5",0);

        if(name != ""){

            if(type==0){
                mAppTextView5.setText(AppUtils.getAppName(name));
                mAppIcon5.setBackground(null);
                mAppIcon5.setImageDrawable(AppUtils.getAppIcon(name));
            }else if(type==1){
                mAppTextView5.setText(name);
                mAppIcon5.setBackgroundResource(R.drawable.path_blue_oval);
                mAppIcon5.setImageDrawable(FloatingBallUtils.getSwitcherIcon(name));
            }
        }


    }

    private void initDialog(String funcName) {

        mFuncDialog = FunctionDialog.newInstance(funcName);

        mFuncDialog.setDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClick(Intent intent) {

                if (intent != null) {

                    if (!intent.getStringExtra("name").equals("")) {

                        mCurrentTextView.setText(intent.getStringExtra("name"));
                        if (intent.getIntExtra("type", 0) == 1) {
                            mCurrentIcon.setBackgroundResource(R.drawable.path_blue_oval);


                        } else {
                            mCurrentIcon.setBackground(null);
                        }

                        mCurrentIcon.setImageDrawable(ImageUtils.Bytes2Drawable(intent.getByteArrayExtra("icon")));
                        mPreferences.put("app" + mCurrentApp, intent.getStringExtra("package"));


                    } else {

                        mCurrentTextView.setText(intent.getStringExtra("name"));
                        mCurrentIcon.setBackground(null);
                        mCurrentIcon.setImageDrawable(null);
                        mPreferences.put("app" + mCurrentApp, "");

                    }

                    mPreferences.put("type" + mCurrentApp, intent.getIntExtra("type", 0));

                    sendMsg(Config.UPDATE_APP, "which", mCurrentApp);

                }

            }
        });

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.app1_layout:
                mAppName = mAppTextView1.getText().toString();
                mCurrentTextView = mAppTextView1;
                mCurrentIcon = mAppIcon1;
                mCurrentApp = "1";
                break;
            case R.id.app2_layout:
                mAppName = mAppTextView2.getText().toString();
                mCurrentTextView = mAppTextView2;
                mCurrentIcon = mAppIcon2;
                mCurrentApp = "2";
                break;
            case R.id.app3_layout:
                mAppName = mAppTextView3.getText().toString();
                mCurrentTextView = mAppTextView3;
                mCurrentIcon = mAppIcon3;
                mCurrentApp = "3";
                break;
            case R.id.app4_layout:
                mAppName = mAppTextView4.getText().toString();
                mCurrentTextView = mAppTextView4;
                mCurrentIcon = mAppIcon4;
                mCurrentApp = "4";
                break;
            case R.id.app5_layout:
                mAppName = mAppTextView5.getText().toString();
                mCurrentTextView = mAppTextView5;
                mCurrentIcon = mAppIcon5;
                mCurrentApp = "5";
                break;
            default:
                break;
        }

        int type = mPreferences.getInt("type"+mCurrentApp,0);
        popupFunctionDialog(type,mAppName);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }


    public void popupFunctionDialog(int type,String funcName){

        if(mFuncDialog ==null){

            initDialog(funcName);
        }


        mFuncDialog.setCheckedFuncName(type,funcName);

        if(mFuncDialog.getDialog()!=null){

            mFuncDialog.getDialog().show();


        }else {
            mFuncDialog.show(getActivity().getFragmentManager(), "dialogFragment");
        }

    }

    public  void sendMsg(int what,String name,String msg) {
        Intent intent = new Intent();
        intent.putExtra("what", what);
        intent.putExtra(name, msg);
        intent.setClass(MyApplication.getApplication(), FloatingBallService.class);
        MyApplication.getApplication().startService(intent);

    }

    @Override
    public void onPause() {
        DialogFragment dialogFragment = (DialogFragment) getActivity().getFragmentManager().findFragmentByTag("dialogFragment");

        if(dialogFragment!=null){

            getActivity().getFragmentManager().beginTransaction().remove(dialogFragment).commit();
        }
        super.onPause();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Config.CHOOSE_APP_CODE){

            if(data != null){

                if(!data.getStringExtra("name").equals("")){

                    mCurrentTextView.setText(data.getStringExtra("name"));
                    mCurrentIcon.setImageDrawable(ImageUtils.Bytes2Drawable(data.getByteArrayExtra("icon")));
                    mPreferences.put("app" + mCurrentApp, data.getStringExtra("package"));



                }else {

                    mCurrentTextView.setText(data.getStringExtra("name"));
                    mCurrentIcon.setImageDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    mPreferences.put("app" + mCurrentApp,"");
                }

                sendMsg(Config.UPDATE_APP,"which",mCurrentApp);

            }

        }

    }




    public static class FunctionDialog extends DialogFragment{

        @Bind(R.id.viewPager)
        ViewPager mViewPager;
        @Bind(R.id.tabs)
        TabLayout mTabs;
        private View mAppView = View.inflate(MyApplication.getApplication(),R.layout.activity_choose_app,null);
        private View mButtonView = View.inflate(MyApplication.getApplication(),R.layout.activity_choose_app,null);
        private View mShotcutView;

        private MyPagerAdapter mPagerAdapter;
        private  AppAdapter adapter;
        private  ToolAdapter mToolAdapter;
        private String mCheckdedFuncName;
        private OnDialogClickListener mClickListener;
        private int mType=0;

        static FunctionDialog newInstance(String checkedName) {

            FunctionDialog f = new FunctionDialog();

            Bundle args = new Bundle();
            args.putString("checkedName", checkedName);
            f.setArguments(args);

            return f;
        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mCheckdedFuncName = getArguments().getString("checkedName");

        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.function_dialog_layout, null);

            ButterKnife.bind(this, view);

            initAppView();

            initButtonView();
            initShotcutView();

            setupViewPager();


            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.BottomDialog);

            builder.setView(view);

           Dialog  dialog = builder.create();

            dialog.setCanceledOnTouchOutside(true);

            dialog.setTitle("选择快捷功能");

            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                    if(keyCode==KeyEvent.KEYCODE_BACK){

                        getDialog().hide();

                        return true;
                    }

                    return false;
                }
            });

            // 设置宽度为屏宽、靠近屏幕底部。
            Window window = dialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.BOTTOM;
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(wlp);

            return dialog;
        }

        private void setDialogClickListener(OnDialogClickListener listener){

            mClickListener = listener;
        }
        private void setCheckedFuncName(int type,String funcName){

            mCheckdedFuncName = funcName;

            mType = type;

            if(mType==0){

                if(adapter!=null){

                    adapter.setAppChecked(funcName);
                    adapter.notifyDataSetChanged();
                }

                if(mViewPager!=null){
                    mViewPager.setCurrentItem(0);
                }


            }else if(mType==1){

                if(mToolAdapter != null){

                    mToolAdapter.setToolChecked(funcName);
                    mToolAdapter.notifyDataSetChanged();
                }

                if(mViewPager!=null){
                    mViewPager.setCurrentItem(1);
                }

            }


        }

        private void initShotcutView() {

        }

        private void initButtonView() {

            //mButtonView = View.inflate(getActivity(),R.layout.activity_choose_app,null);
            ListView mListView = (ListView) mButtonView.findViewById(R.id.lv_app);

            mToolAdapter= new ToolAdapter(getActivity());
            mToolAdapter.setToolChecked(mCheckdedFuncName);
            if(toolList==null){

                mToolAdapter.addList(FloatingBallUtils.getToolInfos());

            }else {

                mToolAdapter.addList(toolList);
            }


            mListView.setAdapter(mToolAdapter);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Intent intent = new Intent();

                    String name = toolList.get(i).getToolName();

                    if (!name.equals(mCheckdedFuncName)) {

                        if("手电筒".equals(name)&&Build.VERSION.SDK_INT>22){

                            checkPermissionGranted(Manifest.permission.CAMERA);
                        }


                        mToolAdapter.setToolChecked(name);
                        intent.putExtra("name", name);
                        intent.putExtra("package", name);
                        intent.putExtra("type",1);
                        intent.putExtra("icon", ImageUtils.Drawable2Bytes(toolList.get(i).getToolIcon()));
                    } else {
                        mToolAdapter.setToolChecked("");
                        intent.putExtra("name", "");
                    }


                    mClickListener.onDialogClick(intent);

                    getDialog().hide();


                }
            });

        }

        private void checkPermissionGranted(String permission) {


            if(Build.VERSION.SDK_INT>22){

                int grant = getActivity().checkSelfPermission(permission);

                if (grant != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    requestPermissions(new String[]{permission}, 123);
                }
            }

        }


        private void initAppView() {

            //mAppView = View.inflate(getActivity(),R.layout.activity_choose_app,null);

            ListView mListView = (ListView) mAppView.findViewById(R.id.lv_app);

            adapter= new AppAdapter(getActivity());
            adapter.setAppChecked(mCheckdedFuncName);

            if(appList==null){

                adapter.addList(AppUtils.getAppInfos());

            }else {

                adapter.addList(appList);
            }


            mListView.setAdapter(adapter);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Intent intent = new Intent();

                    String name = appList.get(i).getAppName();
                    if (!name.equals(mCheckdedFuncName)) {

                        adapter.setAppChecked(name);
                        intent.putExtra("name", name);
                        intent.putExtra("package", appList.get(i).getAppPackage());
                        intent.putExtra("type",0);
                        intent.putExtra("icon", ImageUtils.Drawable2Bytes(appList.get(i).getAppIcon()));
                    } else {
                        adapter.setAppChecked("");
                        intent.putExtra("name", "");
                    }


                    mClickListener.onDialogClick(intent);

                    getDialog().hide();


                }
            });
        }


        public void setupViewPager(){

            mPagerAdapter = new MyPagerAdapter();

            if(mAppView!=null){

                mPagerAdapter.addView(mAppView,"应用程序");
                mPagerAdapter.addView(mButtonView,"快捷开关");
            }


            mViewPager.setAdapter(mPagerAdapter);

            mTabs.setupWithViewPager(mViewPager);

            mViewPager.setOffscreenPageLimit(3);

            switch (mType){

                case 0:
                    mViewPager.setCurrentItem(0);
                    break;
                case 1:
                    mViewPager.setCurrentItem(1);
                    break;
                case 2:
                    mViewPager.setCurrentItem(2);
                    break;
                default:
                    break;
            }

            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    switch (position) {
                        case 0:
                            // mFloatingActionButton.hide();
                            break;
                        case 1:
                            // mFloatingActionButton.hide();
                            //mHistoryFragment.refreshHistory();
                            break;
                        case 2:
                            //mFloatingActionButton.show();
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }


    }

    public interface OnDialogClickListener{

        public void onDialogClick(Intent intent);

    }


}
