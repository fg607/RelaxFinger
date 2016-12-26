package com.hardwork.fg607.relaxfinger.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fg607 on 16-6-19.
 */
public class MenuFolderAdapter extends BaseAdapter {

    private Context mContext;
    private List<MenuDataSugar> mMenuDataList;

    private OnFolderItemClickListener mItemClickListener;

    public interface OnFolderItemClickListener{

        void folderItemClick(String name);
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

        MyHolder holder = null;

        if(convertView == null){

            convertView = View.inflate(mContext, R.layout.menu_folder_item, null);

            holder = new MyHolder(convertView);
            convertView.setTag(holder);
        }else {

            holder = (MyHolder) convertView.getTag();
        }


        MenuDataSugar menuDataSugar = mMenuDataList.get(position);
        int type = menuDataSugar.getType();

        Drawable drawable = null;

        switch (type){

            case 0:
                drawable = AppUtils.getAppIcon(menuDataSugar.getAction());
                break;
            case 1:
                holder.icon.setBackgroundResource(R.drawable.path_blue_oval);
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

        holder.icon.setImageDrawable(drawable);

        holder.name.setText(mMenuDataList.get(position).getName());

        final MyHolder finalHolder = holder;

        convertView.setOnClickListener(new View.OnClickListener() {
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

                    mItemClickListener.folderItemClick(finalHolder.name.getText().toString());
                }

            }
        });

        return convertView;
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.image) public ImageView icon;
        @BindView(R.id.text) public TextView name;

        public MyHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this,itemView);

            name.setTextColor(Color.WHITE);
            name.setTextSize(12);
            name.setSingleLine();
        }
    }
}
