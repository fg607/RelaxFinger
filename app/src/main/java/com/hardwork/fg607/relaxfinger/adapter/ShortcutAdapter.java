package com.hardwork.fg607.relaxfinger.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.hardwork.fg607.relaxfinger.R;
import com.hardwork.fg607.relaxfinger.model.AppInfo;
import com.hardwork.fg607.relaxfinger.model.ShortcutInfo;

import java.util.ArrayList;

/**
 * Created by fg607 on 15-11-26.
 */
public class ShortcutAdapter extends BaseAdapter {

    private ArrayList<ShortcutInfo> mShortcutList;
    private Context mContext;
    private String mChoosedShortcutName;

    public ShortcutAdapter(Context context){
        this.mContext = context;
    }

    public void addList(ArrayList<ShortcutInfo> list){

        this.mShortcutList = list;
        notifyDataSetChanged();
    }

    public void setShortcutChecked(String name){

        this.mChoosedShortcutName = name;
        notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        return mShortcutList.size();
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
        icon.setImageDrawable(mShortcutList.get(i).getShortcutIcon());
        name.setText(mShortcutList.get(i).getShortcutTitle());
        if(mShortcutList.get(i).getShortcutTitle().equals(mChoosedShortcutName)){

            checkBox.setChecked(true);
        }
        else {

            checkBox.setChecked(false);
        }

        return view1;
    }
}
