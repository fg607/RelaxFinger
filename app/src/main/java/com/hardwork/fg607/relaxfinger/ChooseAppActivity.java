package com.hardwork.fg607.relaxfinger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hardwork.fg607.relaxfinger.adapter.AppAdapter;
import com.hardwork.fg607.relaxfinger.model.AppInfo;
import com.hardwork.fg607.relaxfinger.utils.AppUtils;
import com.hardwork.fg607.relaxfinger.utils.Config;
import com.hardwork.fg607.relaxfinger.utils.ImageUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ChooseAppActivity extends Activity {

    @Bind(R.id.lv_app)
    ListView mListView;
    private ArrayList<AppInfo> mAppList;
    private AppAdapter mAppAdapter;
    private String mCheckedAppName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_app);
        ButterKnife.bind(this);
        init();
    }

    public void init(){

        mAppList = AppUtils.getAppInfos();
        mAppAdapter = new AppAdapter(this);
        mAppAdapter.addList(mAppList);

        Intent intent = getIntent();

        if(intent != null){

            mCheckedAppName = intent.getStringExtra("app_name");

            mAppAdapter.setAppChecked(mCheckedAppName);
        }

        mListView.setAdapter(mAppAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent();

                if(!mAppList.get(i).getAppName().equals(mCheckedAppName)){

                    mAppAdapter.setAppChecked(mAppList.get(i).getAppName());
                    intent.putExtra("name", mAppList.get(i).getAppName());
                    intent.putExtra("package", mAppList.get(i).getAppPackage());
                    intent.putExtra("icon", ImageUtils.Drawable2Bytes(mAppList.get(i).getAppIcon()));
                }else {
                    mAppAdapter.setAppChecked("");
                    intent.putExtra("name","");
                }





                ChooseAppActivity.this.setResult(Config.CHOOSE_APP_CODE, intent);
                ChooseAppActivity.this.finish();

            }
        });

    }

}
