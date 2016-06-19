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
import com.hardwork.fg607.relaxfinger.model.ToolInfo;

import java.util.ArrayList;

/**
 * Created by fg607 on 15-11-26.
 */
public class ToolAdapter extends BaseAdapter {

    private ArrayList<ToolInfo> mToolList;
    private Context mContext;
    private String mChoosedToolName;
    private ArrayList<String> mChoosedNameList;

    public ToolAdapter(Context context){
        this.mContext = context;
    }

    public void addList(ArrayList<ToolInfo> list){

        this.mToolList = list;
        notifyDataSetChanged();
    }

    public void setToolChecked(String name){

        this.mChoosedToolName = name;
        notifyDataSetChanged();

    }

    public void setToolChecked(ArrayList<String> choosedNameList){

        this.mChoosedNameList = choosedNameList;
       // notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        return mToolList.size();
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
        icon.setBackgroundResource(R.drawable.path_blue_oval);
        icon.setImageDrawable(mToolList.get(i).getToolIcon());
        name.setText(mToolList.get(i).getToolName());

        if(mChoosedNameList.contains(mToolList.get(i).getToolName())){

            checkBox.setChecked(true);
        }else {

            checkBox.setChecked(false);
        }
      /*  if(mToolList.get(i).getToolName().equals(mChoosedToolName)){

            checkBox.setChecked(true);
        }
        else {

            checkBox.setChecked(false);
        }*/

        return view1;
    }
}
