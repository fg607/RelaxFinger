package com.hardwork.fg607.relaxfinger.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.hardwork.fg607.relaxfinger.R;
import com.hardwork.fg607.relaxfinger.utils.AppUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by fg607 on 17-3-8.
 */

public class AllowAppAdapter extends RecyclerView.Adapter<AllowAppAdapter.AppViewHolder>
        implements View.OnLongClickListener  {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<String> mAppList;
    private OnAppLongClickListener mLongClickListener;


    public AllowAppAdapter(Context context, List<String> appList) {

        mContext = context;
        mAppList = appList;
        mInflater = LayoutInflater.from(context);
    }


    public void setAppList(List<String> appList){

        mAppList = appList;
        notifyDataSetChanged();
    }

    public void setAppLongClickListener(OnAppLongClickListener listener){

        mLongClickListener = listener;
    }

    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = mInflater.inflate(R.layout.allow_app_item,parent,false);

        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AppViewHolder holder, int position) {

        String pkg = mAppList.get(position);

        String appName = AppUtils.getAppName(pkg);
        Drawable appIcon = AppUtils.getAppIcon(pkg);

        holder.imageView.setImageDrawable(appIcon);
        holder.textView.setText(appName);

        holder.itemView.setTag(position);
        holder.itemView.setOnLongClickListener(this);


    }

    @Override
    public int getItemCount() {
        return mAppList != null?mAppList.size():0;
    }


    @Override
    public boolean onLongClick(View view) {

        if(mLongClickListener != null){

            mLongClickListener.appLongClick(view);

            return true;
        }

        return false;
    }

    class AppViewHolder extends ViewHolder {

        @BindView(R.id.image)ImageView imageView;
        @BindView(R.id.text)TextView textView;

        View itemView;

        public AppViewHolder(View view) {
            super(view);
            this.itemView = view;
            ButterKnife.bind(this,view);
        }
    }

    public interface OnAppLongClickListener{

        public void appLongClick(View view);
    }
}
