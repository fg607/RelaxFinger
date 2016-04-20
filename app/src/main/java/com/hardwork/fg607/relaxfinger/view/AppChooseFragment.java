package com.hardwork.fg607.relaxfinger.view;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hardwork.fg607.relaxfinger.R;
import com.hardwork.fg607.relaxfinger.adapter.AppAdapter;
import com.hardwork.fg607.relaxfinger.model.AppInfo;
import com.hardwork.fg607.relaxfinger.utils.AppUtils;
import com.hardwork.fg607.relaxfinger.utils.ImageUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AppChooseFragment extends Fragment {

    @Bind(R.id.lv_app)
    ListView mListView;
    private ArrayList<AppInfo> mAppList;
    private AppAdapter mAppAdapter;
    private String mCheckedAppName;

    public AppChooseFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.activity_choose_app, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    public void setCheckedApp(String appName){

        mCheckedAppName = appName;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        init();
    }

    public void init(){

        mAppList = AppUtils.getAppInfos();
        mAppAdapter = new AppAdapter(getActivity());
        mAppAdapter.addList(mAppList);
        mAppAdapter.setAppChecked(mCheckedAppName);


        mListView.setAdapter(mAppAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent();

                if (!mAppList.get(i).getAppName().equals(mCheckedAppName)) {

                    mAppAdapter.setAppChecked(mAppList.get(i).getAppName());
                    intent.putExtra("name", mAppList.get(i).getAppName());
                    intent.putExtra("package", mAppList.get(i).getAppPackage());
                    intent.putExtra("icon", ImageUtils.Drawable2Bytes(mAppList.get(i).getAppIcon()));
                } else {
                    mAppAdapter.setAppChecked("");
                    intent.putExtra("name", "");
                }


                // ChooseAppActivity.this.setResult(Config.CHOOSE_APP_CODE, intent);
                // ChooseAppActivity.this.finish();

            }
        });
    }

}
