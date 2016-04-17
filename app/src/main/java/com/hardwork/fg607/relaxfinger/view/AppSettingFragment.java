package com.hardwork.fg607.relaxfinger.view;


import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hardwork.fg607.relaxfinger.ChooseAppActivity;
import com.hardwork.fg607.relaxfinger.R;
import com.hardwork.fg607.relaxfinger.service.FloatingBallService;
import com.hardwork.fg607.relaxfinger.utils.AppUtils;
import com.hardwork.fg607.relaxfinger.utils.Config;
import com.hardwork.fg607.relaxfinger.utils.FloatingBallUtils;
import com.hardwork.fg607.relaxfinger.utils.ImageUtils;

import net.grandcentrix.tray.TrayAppPreferences;

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

        openChooseAppDialog();

    }

    public void openChooseAppDialog(){

        Intent intent = new Intent();
        intent.putExtra("app_name",mAppName);
        intent.setClass(getActivity(), ChooseAppActivity.class);
        startActivityForResult(intent, Config.CHOOSE_APP_CODE);
    }

    public void popupFunctionDialog(){


    }

    public  void sendMsg(int what,String name,String msg) {
        Intent intent = new Intent();
        intent.putExtra("what",what);
        intent.putExtra(name, msg);
        intent.setClass(getActivity(), FloatingBallService.class);
        getActivity().startService(intent);
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
}
