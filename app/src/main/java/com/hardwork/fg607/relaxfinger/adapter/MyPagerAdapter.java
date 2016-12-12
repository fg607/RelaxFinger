package com.hardwork.fg607.relaxfinger.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fg607 on 16-4-18.
 */
public class MyPagerAdapter extends PagerAdapter {

    private final List<View> mViews = new ArrayList<>();

    private final List<String> mTitles = new ArrayList<>();

    public void addView(View view, String title) {
        mViews.add(view);
        mTitles.add(title);
    }

    @Override
    public int getCount() {
        return mViews.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // TODO Auto-generated method stub
        return mTitles.get(position);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {

        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup view, int position, Object object) {
        view.removeView(mViews.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        view.addView(mViews.get(position));
        return mViews.get(position);
    }
}