package com.lifesense.ta_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.lifesense.lshybird.LifesenseAgent;
import com.lifesense.lshybird.ui.YouzanActivity;
import com.lifesense.lshybird.utils.URLface;
import com.lifesense.lshybird.ui.BaseHyFragment;
import com.lifesense.rpm.doctor.dev.R;
import com.lifesense.ta_android.utils.BottomNavigationViewHelper;
import com.lifesense.ta_android.wiget.NoPreloadViewPager;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private NoScrollViewPager mViewPager;
    private List<Fragment> mFragments = new ArrayList<>();
    private FragmentStatePagerAdapter mAdapter;
    private BottomNavigationView mNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        LifesenseAgent.syncStepToService();

    }

    private long lastTime;

    private void initView() {
        mNavigation = findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(mNavigation);
        mNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_normal:
                        mViewPager.setCurrentItem(0, true);
                        return true;
                    case R.id.navigation_vip:
                        mViewPager.setCurrentItem(1, true);

//                        if (System.currentTimeMillis() - lastTime < 200) {
//                            startActivity(new Intent(MainActivity.this, DebugActivity.class));
//                            finish();
//                        }
//                        lastTime = System.currentTimeMillis();
                        return true;
                    case R.id.navigation_mine:
                        mViewPager.setCurrentItem(3, true);

                        lastTime = System.currentTimeMillis();
                        return true;
                    case R.id.navigation_bussic:
                        mViewPager.setCurrentItem(2, true);

                        lastTime = System.currentTimeMillis();

////                        if (System.currentTimeMillis() - lastTime < 200) {
////                            mViewPager.setCurrentItem(1, true);
////                        }
////                        lastTime = System.currentTimeMillis();
//                        Intent intent = new Intent(MainActivity.this, YouzanActivity.class);
//                        startActivity(intent);
                        return true;

                }
                return false;
            }
        });
        mViewPager = findViewById(R.id.viewpager);
        // 使用H5 url构建
        mFragments.add(LifesenseAgent.buildLsFragment(MyApp.h5normal));
        mFragments.add(LifesenseAgent.buildLsFragment(MyApp.h5vip));
        mFragments.add(LifesenseAgent.buildLsFragment(MyApp.h5busiss));
        mFragments.add(LifesenseAgent.buildLsFragment(MyApp.h5mine));
        // init view pager
        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mFragments);

        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(4);
        // register listener
        mViewPager.setOnPageChangeListener(mPageChangeListener);
        mViewPager.setCurrentItem(0);


    }

    private NoPreloadViewPager.OnPageChangeListener mPageChangeListener = new NoPreloadViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mNavigation.getMenu().getItem(position).setChecked(true);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> mList;

        public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.mList = list;
        }

        @Override
        public Fragment getItem(int position) {
            return this.mList == null ? null : this.mList.get(position);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return this.mList == null ? 0 : this.mList.size();
        }

    }

    @Override
    public void onBackPressed() {
        try {
            int position = mViewPager.getCurrentItem();
            if (mFragments.get(position) instanceof BaseHyFragment) {
                BaseHyFragment currentFragment = (BaseHyFragment) mFragments.get(position);
                if (currentFragment.onBackPressed()) {
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onBackPressed();
    }

}
