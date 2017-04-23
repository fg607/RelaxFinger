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
import com.hardwork.fg607.relaxfinger.model.NotifyAppInfo;
import com.hardwork.fg607.relaxfinger.service.NotificationService;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class NotifySettingFragment extends Fragment {

    private Context mContext;
    private SettingActivity mActivity;
    private RecyclerView mListView;
    private AllowAppAdapter mAdapter;
    private List<String> mPkgList;
    private List<NotifyAppInfo> mNotifyAppList;

    public NotifySettingFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notify_setting, container, false);

        initNotifyAppList(view);

        return view;
    }

    private void initNotifyAppList(View view) {

        mListView = (RecyclerView) view.findViewById(R.id.lv_notify);

        mListView.setLayoutManager(
                new LinearLayoutManager(mListView.getContext()));


        mListView.setItemAnimator(new DefaultItemAnimator());


        mNotifyAppList = NotifyAppInfo.listAll(NotifyAppInfo.class);

        mPkgList = new ArrayList<>();

        for (NotifyAppInfo info : mNotifyAppList) {

            mPkgList.add(info.getPackageName());
        }

        mAdapter = new AllowAppAdapter(mContext, mPkgList);

        mAdapter.setAppLongClickListener(new AllowAppAdapter.OnAppLongClickListener() {

            @Override
            public void appLongClick(View view) {

                removeNotifyApp((int) (view.getTag()));
            }
        });

        mListView.setAdapter(mAdapter);
    }

    private void removeNotifyApp(int position) {

        final NotifyAppInfo notifyAppInfo = mNotifyAppList.get(position);

        SweetAlertDialog confirmDlg = new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE);

        confirmDlg.setCanceledOnTouchOutside(true);

        confirmDlg.setTitleText("取消接收'" + notifyAppInfo.getAppName() + "'消息通知!")
                .setConfirmText(getString(R.string.delete_ok))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();


                        mNotifyAppList.remove(notifyAppInfo);

                        mPkgList.remove(notifyAppInfo.getPackageName());

                        mAdapter.setAppList(mPkgList);

                        notifyAppInfo.delete();

                        notifyNotificationService();

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

                addNotifyApp();
            }
        });
    }

    private void addNotifyApp() {

        Intent intent = new Intent(mContext, AppChooseActivity.class);

        intent.putExtra("from", Config.FROM_NOTIFY_APP);

        startActivityForResult(intent, Config.FROM_NOTIFY_APP);
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

        if (resultCode == Config.FROM_NOTIFY_APP) {

            mNotifyAppList = NotifyAppInfo.listAll(NotifyAppInfo.class);

            mPkgList.clear();

            for (NotifyAppInfo info : mNotifyAppList) {

                mPkgList.add(info.getPackageName());
            }

            mAdapter.setAppList(mPkgList);

            notifyNotificationService();

        }
    }

    private void notifyNotificationService() {

        Intent intent = new Intent(mContext, NotificationService.class);

        intent.putExtra("what",Config.NOTIFY_APP_CHANGE);

        mContext.startService(intent);
    }

    public void clearMemory() {

    }
}
