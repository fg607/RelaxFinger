package com.hardwork.fg607.relaxfinger.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hardwork.fg607.relaxfinger.R;
import com.hardwork.fg607.relaxfinger.model.MenuDataSugar;
import com.hardwork.fg607.relaxfinger.utils.AppUtils;
import com.hardwork.fg607.relaxfinger.utils.FloatingBallUtils;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by fg607 on 16-6-19.
 */
public class MenuFolderAdapter extends BaseAdapter {

    private Context mContext;
    private List<MenuDataSugar> mMenuDataList;

    private OnFolderItemClickListener mItemClickListener;

    public interface OnFolderItemClickListener{

        void folderItemClick();
    }

    public MenuFolderAdapter(Context context){

        this.mContext = context;
    }

    public void setOnFolderItemClickListener(OnFolderItemClickListener listener){

        mItemClickListener = listener;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

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

        Drawable drawable = null;

        switch (type){

            case 0:
                drawable = AppUtils.getAppIcon(menuDataSugar.getAction());
                break;
            case 1:
                icon.setBackgroundResource(R.drawable.path_blue_oval);
                drawable = FloatingBallUtils.getSwitcherIcon(menuDataSugar.getName());
                break;
            case 2:
                drawable = AppUtils.getShortcutIcon(menuDataSugar.getName());
                break;
            default:
                break;
        }

        if(drawable == null){

            MenuDataSugar.executeQuery("delete from MENU_DATA_SUGAR where ACTION='" + mMenuDataList.get(position).getAction()+"'");
        }

        icon.setImageDrawable(drawable);

        name.setText(mMenuDataList.get(position).getName());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    mMenuDataList.get(position).click();

                } catch (URISyntaxException e) {

                    e.printStackTrace();

                }catch (ActivityNotFoundException e){
                    e.printStackTrace();
                    Toast.makeText(mContext,"找不到该应用程序！",Toast.LENGTH_SHORT).show();
                    MenuDataSugar.executeQuery("delete from MENU_DATA_SUGAR where ACTION='" + mMenuDataList.get(position).getAction()+"'");
                }

                if(mItemClickListener!=null){

                    mItemClickListener.folderItemClick();
                }

            }
        });

        return view;
    }
}
