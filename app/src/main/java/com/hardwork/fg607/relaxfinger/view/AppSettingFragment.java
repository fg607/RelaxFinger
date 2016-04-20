package com.hardwork.fg607.relaxfinger.view;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
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
import com.hardwork.fg607.relaxfinger.model.AppInfo;
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

    public AppSettingFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_app_setting, container, false);

        ButterKnife.bind(this,fragmentView);
        mPreferences = FloatingBallUtils.getMultiProcessPreferences();
        intView();

        return fragmentView;
    }

    private void intView() {

        mLayout1.setOnClickListener(this);
        mLayout2.setOnClickListener(this);
        mLayout3.setOnClickListener(this);
        mLayout4.setOnClickListener(this);
        mLayout5.setOnClickListener(this);

        String packageName;

        packageName = mPreferences.getString("app1","");

        if(packageName != ""){

            mAppTextView1.setText(AppUtils.getAppName(packageName));
            mAppIcon1.setImageDrawable(AppUtils.getAppIcon(packageName));
        }

        packageName = mPreferences.getString("app2","");

        if(packageName != ""){

            mAppTextView2.setText(AppUtils.getAppName(packageName));
            mAppIcon2.setImageDrawable(AppUtils.getAppIcon(packageName));
        }


        packageName = mPreferences.getString("app3","");

        if(packageName != ""){

            mAppTextView3.setText(AppUtils.getAppName(packageName));
            mAppIcon3.setImageDrawable(AppUtils.getAppIcon(packageName));
        }

        packageName = mPreferences.getString("app4","");

        if(packageName != ""){

            mAppTextView4.setText(AppUtils.getAppName(packageName));
            mAppIcon4.setImageDrawable(AppUtils.getAppIcon(packageName));
        }

        packageName = mPreferences.getString("app5","");

        if(packageName != ""){

            mAppTextView5.setText(AppUtils.getAppName(packageName));
            mAppIcon5.setImageDrawable(AppUtils.getAppIcon(packageName));
        }


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

        popupFunctionDialog(mAppName);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }


    public void popupFunctionDialog(String funcName){

        if(mFuncDialog ==null){

            mFuncDialog = FunctionDialog.newInstance(funcName);

            mFuncDialog.setDialogClickListener(new OnDialogClickListener() {
                @Override
                public void onDialogClick(Intent intent) {

                    if(intent != null){

                        if(!intent.getStringExtra("name").equals("")){

                            mCurrentTextView.setText(intent.getStringExtra("name"));
                            mCurrentIcon.setImageDrawable(ImageUtils.Bytes2Drawable(intent.getByteArrayExtra("icon")));
                            mPreferences.put("app" + mCurrentApp, intent.getStringExtra("package"));



                        }else {

                            mCurrentTextView.setText(intent.getStringExtra("name"));
                            mCurrentIcon.setImageDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            mPreferences.put("app" + mCurrentApp,"");
                        }

                        sendMsg(Config.UPDATE_APP,"which",mCurrentApp);

                    }

                }
            });


        }



        if(mFuncDialog.getDialog()!=null){

            mFuncDialog.setCheckedFuncName(funcName);

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
        private View mAppView;
        private View mButtonView;
        private View mShotcutView;

        private  AppAdapter adapter;
        private String mCheckdedFuncName;
        private OnDialogClickListener mClickListener;

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
        private void setCheckedFuncName(String funcName){

            mCheckdedFuncName = funcName;

            if(adapter!=null){

                adapter.setAppChecked(funcName);
                adapter.notifyDataSetChanged();
            }

        }

        private void initShotcutView() {

        }

        private void initButtonView() {

        }

        private void initAppView() {

            mAppView = View.inflate(getActivity(),R.layout.activity_choose_app,null);

            ListView mListView = (ListView) mAppView.findViewById(R.id.lv_app);

            final ArrayList<AppInfo> appList =  AppUtils.getAppInfos();


            adapter= new AppAdapter(getActivity());
            adapter.setAppChecked(mCheckdedFuncName);
            adapter.addList(appList);

            mListView.setAdapter(adapter);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Intent intent = new Intent();

                    if (!appList.get(i).getAppName().equals(mCheckdedFuncName)) {

                        adapter.setAppChecked(appList.get(i).getAppName());
                        intent.putExtra("name", appList.get(i).getAppName());
                        intent.putExtra("package", appList.get(i).getAppPackage());
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

            MyPagerAdapter adapter = new MyPagerAdapter();

            if(mAppView!=null){

                adapter.addView(mAppView,"应用程序");
            }


            mViewPager.setAdapter(adapter);

            mTabs.setupWithViewPager(mViewPager);

            mViewPager.setOffscreenPageLimit(3);

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
