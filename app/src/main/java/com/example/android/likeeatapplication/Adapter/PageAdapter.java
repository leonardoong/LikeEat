package com.example.android.likeeatapplication.Adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.android.likeeatapplication.TabFragment1;
import com.example.android.likeeatapplication.TabFragment2;

/**
 * Created by Leonardo on 3/30/2018.
 */

public class PageAdapter extends FragmentStatePagerAdapter {

    int mNoOfTabs;

    public PageAdapter(FragmentManager fragmentManager, int NumberOfTabs)
    {
        super(fragmentManager);
        this.mNoOfTabs = NumberOfTabs;
    }
    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        switch(position)
        {
            case 0:
                TabFragment1 tab1 = new TabFragment1();
                return tab1;
            case 1:
                TabFragment2 tab2 = new TabFragment2();
                return tab2;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return mNoOfTabs;
    }

}
