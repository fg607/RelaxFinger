package com.hardwork.fg607.relaxfinger.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.hardwork.fg607.relaxfinger.R;
import com.hardwork.fg607.relaxfinger.model.AppInfo;

import java.util.ArrayList;

/**
 * Created by fg607 on 15-11-26.
 */
public class AppAdapter extends BaseAdapter {

    private ArrayList<AppInfo> mAppList;
    private Context mContext;
    private String mChoosedAppName;
    private ArrayList<String> mChoosedNameList;

    public AppAdapter(Context context){
        this.mContext = context;
    }

    public void addList(ArrayList<AppInfo> list){

        this.mAppList = list;
        notifyDataSetChanged();
    }

    public void setAppChecked(String name){

        this.mChoosedAppName = name;
        notifyDataSetChanged();

    }

    public void setAppChecked(ArrayList<String> choosedNameList){

        this.mChoosedNameList = choosedNameList;
        //notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mAppList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {


        ImageView icon;
        TextView name;
        CheckBox checkBox;

        View view1 = null;

        if(view == null){

            view1 = View.inflate(mContext, R.layout.choosekey_item, null);

        }else {

            view1 = view;
        }

        icon = (ImageView) view1.findViewById(R.id.image);
        name = (TextView) view1.findViewById(R.id.text);
        checkBox = (CheckBox) view1.findViewById(R.id.checkbox);
        icon.setImageDrawable(mAppList.get(i).getAppIcon());
        name.setText(mAppList.get(i).getAppName());

        if(mChoosedNameList.contains(mAppList.get(i).getAppPackage())){

            checkBox.setChecked(true);
        }else {

            checkBox.setChecked(false);
        }
       /* if(mAppList.get(i).getAppName().equals(mChoosedAppName)){

            checkBox.setChecked(true);
        }
        else {

            checkBox.setChecked(false);
        }*/

        return view1;
    }


}
