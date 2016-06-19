package com.hardwork.fg607.relaxfinger.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.hardwork.fg607.relaxfinger.R;
import com.hardwork.fg607.relaxfinger.model.MenuDataSugar;
import com.hardwork.fg607.relaxfinger.utils.AppUtils;
import com.hardwork.fg607.relaxfinger.utils.FloatingBallUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fg607 on 16-6-19.
 */
public class MenuFolderAdapter extends BaseAdapter {

    private Context mContext;
    private List<MenuDataSugar> mMenuDataList;

    public MenuFolderAdapter(Context context){

        this.mContext = context;
    }

    public void setMenuDataList(List<MenuDataSugar> list){

        mMenuDataList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mMenuDataList==null?0:mMenuDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView icon;
        TextView name;

        View view = null;

        if(convertView == null){

            view = View.inflate(mContext, R.layout.menu_folder_item, null);

        }else {

            view = convertView;
        }

        icon = (ImageView) view.findViewById(R.id.image);
        name = (TextView) view.findViewById(R.id.text);

        name.setTextColor(Color.WHITE);
        name.setTextSize(12);
        name.setSingleLine();

        MenuDataSugar menuDataSugar = mMenuDataList.get(position);
        int type = menuDataSugar.getType();

        switch (type){

            case 0:
                icon.setImageDrawable(AppUtils.getAppIcon(menuDataSugar.getAction()));
                break;
            case 1:
                icon.setBackgroundResource(R.drawable.path_blue_oval);
                icon.setImageDrawable(FloatingBallUtils.getSwitcherIcon(menuDataSugar.getName()));
                break;
            case 2:
                icon.setImageDrawable(AppUtils.getShortcutIcon(menuDataSugar.getAction()));
                break;
            default:
                break;
        }

        name.setText(mMenuDataList.get(position).getName());

        return view;
    }
}
