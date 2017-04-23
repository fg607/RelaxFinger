package com.hardwork.fg607.relaxfinger.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.hardwork.fg607.relaxfinger.R;
import com.hardwork.fg607.relaxfinger.adapter.AppAdapter;
import com.hardwork.fg607.relaxfinger.model.AppInfo;
import com.hardwork.fg607.relaxfinger.model.Config;
import com.hardwork.fg607.relaxfinger.model.HideAppInfo;
import com.hardwork.fg607.relaxfinger.model.NotifyAppInfo;
import com.hardwork.fg607.relaxfinger.utils.AppUtils;

import java.util.ArrayList;

public class AppChooseActivity extends AppCompatActivity {

    private FloatingActionButton mFab;
    private ListView mAppListView;
    private AppAdapter mListAdapter;
    private ArrayList<AppInfo> mAppList;
    private ArrayList<String> mCheckedAppList;
    private ArrayList<AppInfo> mChoosedAppList;
    private int mFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_choose);

        mFab = (FloatingActionButton) findViewById(R.id.fab);

        mFab.hide();

        Intent intent = getIntent();

        if(intent != null){

            mFrom = intent.getIntExtra("from",0);

            if(mFrom == Config.FROM_NOTIFY_APP){

                setTitle("选择接收消息应用");

            }else if(mFrom == Config.FROM_HIDE_APP){

                setTitle("选择自动隐藏应用");
            }
        }


        initAppView();


    }


    private void initAppView() {

        new AsyncTask<Void, Void, Void>() {

            ProgressBar loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                mAppListView = (ListView) findViewById(R.id.app_list);

                loading = (ProgressBar) findViewById(R.id.loading);

                mListAdapter = new AppAdapter(AppChooseActivity.this);

                mChoosedAppList = new ArrayList<AppInfo>();

                mCheckedAppList = new ArrayList<String>();

                mListAdapter.setAppChecked(mCheckedAppList);


            }

            @Override
            protected Void doInBackground(Void... voids) {

                mAppList = AppUtils.getLauncherAppInfos();

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                mListAdapter.addList(mAppList);

                mAppListView.setAdapter(mListAdapter);

                mAppListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                        checkBox.setChecked(!checkBox.isChecked());

                        AppInfo choosedApp = mAppList.get(i);


                        if (checkBox.isChecked()) {


                            mCheckedAppList.add(choosedApp.getAppPackage());
                            mChoosedAppList.add(choosedApp);

                        } else {

                            mCheckedAppList.remove(choosedApp.getAppPackage());
                            mChoosedAppList.remove(choosedApp);
                        }

                        mListAdapter.setAppChecked(mCheckedAppList);

                    }
                });

                loading.setVisibility(View.GONE);
                mAppListView.setVisibility(View.VISIBLE);

                mFab.show();
                mFab.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        chooseFinished();
                    }
                });

            }
        }.execute();


    }

    private void chooseFinished() {


        if(mFrom == Config.FROM_NOTIFY_APP){

            for(AppInfo info:mChoosedAppList){

                NotifyAppInfo notifyAppInfo = new NotifyAppInfo(info.getAppName(),info.getAppPackage());

                notifyAppInfo.save();
            }

        }else if(mFrom == Config.FROM_HIDE_APP){

            for(AppInfo info:mChoosedAppList){

                HideAppInfo hideAppInfo = new HideAppInfo(info.getAppName(),info.getAppPackage());

                hideAppInfo.save();
            }
        }

        setResult(mFrom);
        finish();
    }
}
