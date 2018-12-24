package com.hsm.zzh.cl.autolibrary.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.model.Marker;
import com.hsm.zzh.cl.autolibrary.R;
import com.hsm.zzh.cl.autolibrary.adapter.MainViewPagerAdapter;
import com.hsm.zzh.cl.autolibrary.fragment.CommunityFragment;
import com.hsm.zzh.cl.autolibrary.fragment.MapFragment;
import com.hsm.zzh.cl.autolibrary.fragment.UserFragment;
import com.hsm.zzh.cl.autolibrary.info_api.Book;
import com.hsm.zzh.cl.autolibrary.info_api.Machine;
import com.hsm.zzh.cl.autolibrary.info_api.MachineOperation;
import com.hsm.zzh.cl.autolibrary.view.MainNavItem;
import com.hsm.zzh.cl.autolibrary.view.NoScrollViewPager;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private NoScrollViewPager view_viewPager;
    private int NavItemNum = 3;
    private MainNavItem[] view_NavItemList = new MainNavItem[NavItemNum];
    private MainNavItem view_ScannerItem;

    private MapFragment mapFragment;
    private UserFragment userFragment;
    private CommunityFragment communityFragment;

    private MainViewPagerAdapter mainViewPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view_viewPager = (NoScrollViewPager) findViewById(R.id.viewpager);
        view_NavItemList[0] = (MainNavItem) findViewById(R.id.item_1);
        view_NavItemList[1] = (MainNavItem) findViewById(R.id.item_2);
        view_NavItemList[2] = (MainNavItem) findViewById(R.id.item_3);
        view_ScannerItem = (MainNavItem) findViewById(R.id.item_scanner);
        view_ScannerItem.setOnClickListener(this);

        view_viewPager.setNoScroll(true);

        mapFragment = new MapFragment();
        communityFragment = new CommunityFragment();
        userFragment = new UserFragment();

        mainViewPagerAdapter = new MainViewPagerAdapter(this.getSupportFragmentManager()
                , new Fragment[]{mapFragment, communityFragment, userFragment}
                , this);
        view_viewPager.setAdapter(mainViewPagerAdapter);
        view_viewPager.setOffscreenPageLimit(3);

        view_viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                pagerSelect(position);
                Log.i("位置", "onPageSelected: " + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        for (int i = 0; i < view_NavItemList.length; i++) {
            final int finalI = i;
            view_NavItemList[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pagerSelect(finalI);
                }
            });
        }

        pagerSelect(0);

        initFragment();
    }


    private void initFragment(){
        //设置标记的点击事件
        mapFragment.setMyOnMarkerClickListener(new MapFragment.MyOnMarkerClickListener() {
            @Override
            public void onMarkerClickListener(Marker marker, String objectId) {
                Intent intent = new Intent(MainActivity.this, BookListActivity.class);
                intent.putExtra("machine_id", objectId);
                startActivity(intent);
            }
        });

        //监听位置改变，设置map的位置标记
        mapFragment.setMineOnMyLocationChangeListener(new MapFragment.MineOnMyLocationChangeListener() {
            @Override
            public void onMyLocationChangeListener(Location location) {
                BmobGeoPoint point = new BmobGeoPoint(location.getLongitude(), location.getLatitude());
                MachineOperation.get_machines_by_location(point, 10.0,
                        new FindListener<Machine>() {
                            @Override
                            public void done(final List<Machine> list, BmobException e) {
                                if (e != null) {
                                    Log.e("错误", "done: " + "获取附近机器信息失败");
                                    Toast.makeText(MainActivity.this, "获取信息失败", Toast.LENGTH_SHORT)
                                            .show();
                                    return;
                                }
                                Log.i("machine", "done: " + list.toString());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mapFragment.setMarks(list, R.drawable.books);
                                    }
                                });
                            }
                        });
            }
        });
    }


    private void pagerSelect(int i) {
        for (MainNavItem item : view_NavItemList) {
            item.do_selected(false);
        }
        view_NavItemList[i].do_selected(true);
        view_viewPager.setCurrentItem(i);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_scanner: {
                final Intent intent = new Intent(this, ScannerActivity.class);
                startActivity(intent);
                break;
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
