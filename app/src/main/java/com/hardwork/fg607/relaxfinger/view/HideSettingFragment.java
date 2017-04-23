package com.hardwork.fg607.relaxfinger.view;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.hardwork.fg607.relaxfinger.R;
import com.hardwork.fg607.relaxfinger.SettingActivity;
import com.hardwork.fg607.relaxfinger.adapter.AllowAppAdapter;
import com.hardwork.fg607.relaxfinger.model.Config;
import com.hardwork.fg607.relaxfinger.model.HideAppInfo;
import com.hardwork.fg607.relaxfinger.service.FloatService;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class HideSettingFragment extends Fragment {

    private Context mContext;
    private SettingActivity mActivity;
    private RecyclerView mListView;
    private AllowAppAdapter mAdapter;
    private List<String> mPkgList;
    private List<HideAppInfo> mHideAppList;

    public HideSettingFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_hide_setting, container, false);

        initHideAppList(view);

        return view;
    }

    private void initHideAppList(View view) {

        mListView = (RecyclerView) view.findViewById(R.id.lv_hide);

        mListView.setLayoutManager(
                new LinearLayoutManager(mListView.getContext()));


        mListView.setItemAnimator(new DefaultItemAnimator());


        mHideAppList = HideAppInfo.listAll(HideAppInfo.class);

        mPkgList = new ArrayList<>();

        for (HideAppInfo info : mHideAppList) {

            mPkgList.add(info.getPackageName());
        }

        mAdapter = new AllowAppAdapter(mContext, mPkgList);

        mAdapter.setAppLongClickListener(new AllowAppAdapter.OnAppLongClickListener() {

            @Override
            public void appLongClick(View view) {

                removeHideApp((int) (view.getTag()));
            }
        });

        mListView.setAdapter(mAdapter);
    }

    private void removeHideApp(int position) {

        final HideAppInfo hideAppInfo = mHideAppList.get(position);

        SweetAlertDialog confirmDlg = new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE);

        confirmDlg.setCanceledOnTouchOutside(true);

        confirmDlg.setTitleText("取消'" + hideAppInfo.getAppName() + "'界面自动隐藏!")
                .setConfirmText(getString(R.string.delete_ok))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();


                        mHideAppList.remove(hideAppInfo);

                        mPkgList.remove(hideAppInfo.getPackageName());

                        mAdapter.setAppList(mPkgList);

                        hideAppInfo.delete();

                        notifyFloatService();

                    }
                })
                .show();

    }

    @Override
    public void onResume() {
        super.onResume();

        mActivity.setFabClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                addHideApp();
            }
        });
    }

    private void addHideApp() {

        Intent intent = new Intent(mContext,AppChooseActivity.class);

        intent.putExtra("from", Config.FROM_HIDE_APP);

        startActivityForResult(intent,Config.FROM_HIDE_APP);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Config.FROM_HIDE_APP) {

            mHideAppList = HideAppInfo.listAll(HideAppInfo.class);

            mPkgList.clear();

            for (HideAppInfo info : mHideAppList) {

                mPkgList.add(info.getPackageName());
            }

            mAdapter.setAppList(mPkgList);

            notifyFloatService();

        }
    }

    private void notifyFloatService() {

        Intent intent = new Intent(mContext, FloatService.class);

        intent.putExtra("what",Config.HIDE_APP_CHANGE);

        mContext.startService(intent);
    }

    public void clearMemory() {

    }
}
