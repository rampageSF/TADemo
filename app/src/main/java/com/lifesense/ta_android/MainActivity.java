package com.lifesense.ta_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.lifesense.lshybird.LifesenseAgent;
import com.lifesense.lshybird.ui.YouzanActivity;
import com.lifesense.lshybird.utils.URLface;
import com.lifesense.lshybird.ui.BaseHyFragment;
import com.lifesense.rpm.doctor.dev.R;
import com.lifesense.ta_android.utils.BottomNavigationViewHelper;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private List<Fragment> mFragments = new ArrayList<>();
    private FragmentPagerAdapter mAdapter;
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
                    case R.id.navigation_home:
                        mViewPager.setCurrentItem(2, true);
                        return true;
                    case R.id.navigation_dashboard:
                        if (System.currentTimeMillis() - lastTime < 200) {
                            startActivity(new Intent(MainActivity.this, DebugActivity.class));
                            finish();
                        }
                        lastTime = System.currentTimeMillis();
                        return true;
                    case R.id.navigation_mine:
                        if (System.currentTimeMillis() - lastTime < 200) {
                            mViewPager.setCurrentItem(3, true);
                        }
                        lastTime = System.currentTimeMillis();
                        return true;
                    case R.id.navigation_activity:
//                        if (System.currentTimeMillis() - lastTime < 200) {
//                            mViewPager.setCurrentItem(1, true);
//                        }
//                        lastTime = System.currentTimeMillis();
                        Intent intent = new Intent(MainActivity.this, YouzanActivity.class);
                        startActivity(intent);
                        return true;

                }
                return false;
            }
        });
        mViewPager = findViewById(R.id.viewpager);
        // 使用H5 url构建
        mFragments.add(new StepFragment());
        mFragments.add(new StepFragment());
        mFragments.add(LifesenseAgent.buildLsFragment(MyApp.getH5Url()));
        mFragments.add(new MeFramgnet());
        // init view pager
        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(mAdapter);
        // register listener
        mViewPager.addOnPageChangeListener(mPageChangeListener);
        mViewPager.setCurrentItem(2);


    }

    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
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

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

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
