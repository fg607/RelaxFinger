package com.hardwork.fg607.relaxfinger.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.hardwork.fg607.relaxfinger.R;
import com.hardwork.fg607.relaxfinger.model.AppInfo;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fg607 on 15-11-26.
 */
public class AppAdapter extends BaseAdapter {

    private ArrayList<AppInfo> mAppList;
    private Context mContext;
    private String mChoosedAppName;
    private ArrayList<String> mChoosedNameList = new ArrayList<>();

    public AppAdapter(Context context){
        this.mContext = context;
    }

    public void addList(ArrayList<AppInfo> list){

        this.mAppList = list;
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
        return mAppList!=null?mAppList.size():0;
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


        MyHolder holder = null;

        if(view == null){

            view = View.inflate(mContext, R.layout.choosekey_item, null);

            holder = new MyHolder(view);

            view.setTag(holder);

        }else {

            holder = (MyHolder) view.getTag();
        }

        holder.icon.setImageDrawable(mAppList.get(i).getAppIcon());
        holder.name.setText(mAppList.get(i).getAppName());

        if(mChoosedNameList.contains(mAppList.get(i).getAppPackage())){

            holder.checkBox.setChecked(true);
        }else {

            holder.checkBox.setChecked(false);
        }

        return view;
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.image) ImageView icon;
        @BindView(R.id.text) TextView name;
        @BindView(R.id.checkbox) CheckBox checkBox;

        public MyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }


}
