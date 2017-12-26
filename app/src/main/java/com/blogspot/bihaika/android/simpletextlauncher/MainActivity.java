package com.blogspot.bihaika.android.simpletextlauncher;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int WIDGET_FRAGMENT_POS = 0;
    private static final int HOME_FRAGMENT_POS = 1;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private HomeFragment mHomeFragment;
    private WidgetFragment mWidgetFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setCurrentItem(HOME_FRAGMENT_POS);

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case WIDGET_FRAGMENT_POS:
                    if (mWidgetFragment == null) {
                        mWidgetFragment = WidgetFragment.newInstance();
                    }
                    return mWidgetFragment;
                case HOME_FRAGMENT_POS:
                default:
                    if (mHomeFragment == null) {
                        mHomeFragment = HomeFragment.newInstance();
                    }
                    return mHomeFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    @Override
    protected void onStop() {
        DataManager.getInstance(this).save();
        super.onStop();
    }
}
