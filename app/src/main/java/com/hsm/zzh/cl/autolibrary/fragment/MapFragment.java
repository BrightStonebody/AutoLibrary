package com.hsm.zzh.cl.autolibrary.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.hsm.zzh.cl.autolibrary.R;
import com.hsm.zzh.cl.autolibrary.activity.BookListActivity;
import com.hsm.zzh.cl.autolibrary.activity.MainActivity;
import com.hsm.zzh.cl.autolibrary.activity.SearchActivity;
import com.hsm.zzh.cl.autolibrary.info_api.Book;
import com.hsm.zzh.cl.autolibrary.info_api.BookOperation;
import com.hsm.zzh.cl.autolibrary.info_api.Machine;
import com.hsm.zzh.cl.autolibrary.info_api.MachineOperation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MapFragment extends Fragment implements View.OnClickListener {
    private MapView view_mapView = null;
    private View viewSearch;

    private AMap aMap = null;
    private Location mLocation = null;
    private Map<Marker, String> markerToId = new HashMap<>();

    private SharedPreferences sp;
    private MyOnMarkerClickListener myOnMarkerClickListener=null;
    private MineOnMyLocationChangeListener mineOnMyLocationChangeListener=null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        viewSearch = view.findViewById(R.id.search_view);
        view_mapView = (MapView) view.findViewById(R.id.map_view);

        sp = getActivity().getSharedPreferences("app", Context.MODE_PRIVATE);

        viewSearch.setOnClickListener(this);

        view_mapView.onCreate(savedInstanceState);
        if (aMap == null)
            aMap = view_mapView.getMap();

        initMap();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_view: {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    public interface MyOnMarkerClickListener{
        void onMarkerClickListener(Marker marker, String objectId);
    }


    public interface MineOnMyLocationChangeListener{
        void onMyLocationChangeListener(Location location);
    }

    public void setMyOnMarkerClickListener(MyOnMarkerClickListener onMarkerClickListener){
        this.myOnMarkerClickListener = onMarkerClickListener;
    }

    public void setMineOnMyLocationChangeListener(MineOnMyLocationChangeListener mineOnMyLocationChangeListener){
        this.mineOnMyLocationChangeListener = mineOnMyLocationChangeListener;
    }

    public void setMarks(List<Machine> machines, int image) {
        for (Machine item : machines) {
            MarkerOptions markeroptions = new MarkerOptions();
            LatLng latLng = new LatLng(item.getLocation().getLatitude(), item.getLocation().getLongitude());
            markeroptions.position(latLng);
            markeroptions.title(item.getId() + "");
            markeroptions.snippet(item.getShortdesc());
            markeroptions.draggable(false);
            markeroptions.visible(true);
            markeroptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources()
                    , image)));
            Marker marker = aMap.addMarker(markeroptions);
            markerToId.put(marker, item.getObjectId());
        }
    }

    private void initMap() {
        final MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();
//        myLocationStyle.myLocationType(MyLocationStyle.);
        // 连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(1000);
        //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.radiusFillColor(0xffffff);
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.my_locate));
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                mLocation = location;
                //在sp中存放经纬度信息
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("location_longitude", "" + location.getLongitude());
                editor.putString("location_latitude", "" + location.getLatitude());
                editor.apply();
                if(mineOnMyLocationChangeListener != null)
                    mineOnMyLocationChangeListener.onMyLocationChangeListener(location);
            }
        });

        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(myOnMarkerClickListener != null)
                    myOnMarkerClickListener.onMarkerClickListener(marker, markerToId.get(marker));
                return true;
            }
        });

        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setZoomPosition(0);
        uiSettings.setMyLocationButtonEnabled(false);
    }

    public void addFindBookLocation(List<Machine> machines) {
        viewSearch.setVisibility(View.GONE);
        if (getMyActivity() == null) {
            Log.e("错误", "MapFragment addFindBookLocation: mapFragment get activity is null: ");
            return;
        }

        setMarks(machines, R.drawable.books);
        setMarks(machines, R.drawable.locate);
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(
                new CameraPosition(
                        new LatLng(machines.get(0).getLocation().getLatitude(), machines.get(0).getLocation().getLongitude()),
                        15, 0, 0));
        aMap.animateCamera(mCameraUpdate, 1000, null);

    }

    private Activity mActivity;

    private Activity getMyActivity() {
        return mActivity;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (Activity) activity;
        if (mActivity == null)
            Log.i("test", "onAttach: ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
        Log.i("test", "onDetach: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        view_mapView.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        view_mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        view_mapView.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        view_mapView.onSaveInstanceState(outState);
    }
}
