package com.bignerdranch.android.maplbsline;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.bignerdranch.android.maplbsline.Tools.ClientSocket;
import com.bignerdranch.android.maplbsline.Tools.DataBaseHelper;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.toggle;
import static com.baidu.location.d.j.S;
import static com.baidu.location.d.j.b;
import static com.baidu.location.d.j.v;
import static com.bignerdranch.android.maplbsline.SignIn.dbHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "MainActivity";
    public LocationClient mLocationClient;
    public List<LatLng> pts = new ArrayList<LatLng>();
    public List<LatLng> ptNew = new ArrayList<>();

    private BaiduMap baiduMap;
    private MapView mapView;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FloatingActionButton floatingButton;
    private Polyline mPolyline;
    private double distance;

    private boolean isFirstLocate = true;
    private boolean isFirstGetLocation = true;
    private boolean isFirstClick = true;
    private double firstLat;
    private double firstLng;
    private long firstTime = 0;
    private int i = 0;

//    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
//        dbHelper = new DataBaseHelper(this, "DayLine.db", null, 1);
        floatingButton = (FloatingActionButton) findViewById(R.id.main_floatingButton);
        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawerlayout);
        navigationView = (NavigationView) findViewById(R.id.drawer_navigation);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mapView = (MapView) findViewById(R.id.main_mapview);
        initLayout();
        getPermissionAllow();
//        drawLine();
    }


    public void getPermissionAllow() {
        List<String> permissionList = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        } else {
            requestLocation();
        }
    }

/**
 * navigateTo : 第一次进入地图时，在地图上移动到我的位置
 * */
    private void navigateToFirst(BDLocation location) {
        if (isFirstLocate) {
            navigate(location, null);
            if (i < 1) {
                i++;
            } else {
                isFirstLocate = false;
            }
            Log.d(TAG, "onReceiveLocation: gggggggggggggggggggggggggggggg" + isFirstLocate + "gggggggggggggg" + location.getLongitude() + "   "
                    + location.getLatitude());
        }
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);
    }

    private void navigate(BDLocation location, LatLng lastLocation) {
        if (location != null) {
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            Toast.makeText(this, ll.toString(), Toast.LENGTH_SHORT).show();
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);
        } else {
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(lastLocation);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);
        }
    }

    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }

/**
 * initLocation ： 初始化地图数据
 */
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(3000);
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
//        option.setCoorType("wgs84");
        SDKInitializer.setCoordType(CoordType.GCJ02);
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    public void initLayout() {
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        floatingButton.setOnClickListener(this);

        toolbar.setTitle("陪你走过漫长的岁月");
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
//        navigationView.setCheckedItem(R.id.menu_name);               将昵称设置为默认选项
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return true;
    }

/**
 * 绘制路线
 **/

    private void drawLine(final List<LatLng> latLngList) {

//        final List<LatLng> ptNew = new ArrayList<>();

        Log.d(TAG, "run: cccccccccccccccccccccccccccccc");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                ptNew.add(pts.get(1));
//                for (int i = 2; i < pts.size(); i++) {
//                    ptNew.add(pts.get(i));
//                }

                if (latLngList.size() > 2) {
                    OverlayOptions ooPolyline = new PolylineOptions().width(10)
//                            .color().points(latLngList);
                            .color(0xAAFF0000).points(latLngList);
                    mPolyline = (Polyline) baiduMap.addOverlay(ooPolyline);
                    Log.d(TAG, "drawLine: bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb" + pts.size() + pts);
                }
            }
        });
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;
            } else {
//                dbHelper = new DataBaseHelper(this, "DayLine.db", null, 1);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("latitude", 0d);
                values.put("longitude", 0d);
                db.insert("Day1", null, values);
                values.clear();
                List<Double> latitudeList = new ArrayList<>();
                for (int i = 1; i < ptNew.size(); i++) {
                    latitudeList.add(ptNew.get(i).latitude);
                }
                ClientSocket.addNewTrackLatitude(latitudeList);
                List<Double> longitudeList = new ArrayList<>();
                for (int i = 1; i < ptNew.size(); i++) {
                    longitudeList.add(ptNew.get(i).longitude);
                }
                ClientSocket.addNewTrackLongitude(longitudeList);
                System.exit(0);
            }
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        dbHelper = new DataBaseHelper(this, "DayLine.db", null, 3);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        List<LatLng> allPoints = new ArrayList<>();
        List<LatLng> pointsNow = new ArrayList<>();
        LatLng ll = null;
        switch (v.getId()) {
            case R.id.main_floatingButton:
                if (isFirstClick) {
                    Cursor cursor = db.query("Day1", null, null, null, null, null, null);
                    int time = 0;
                    if (cursor.moveToFirst()) {
                        do {
                            double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
                            double longtitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
                            LatLng latLng = new LatLng(latitude, longtitude);
                            if (time > 5) {
                                if (latitude != 0 && longtitude != 0) {
                                    if (allPoints.size() < 2) {
                                        allPoints.add(latLng);
                                    } else {
                                        double distanceTest = DistanceUtil.getDistance(latLng, allPoints.get(allPoints.size() - 1));
                                        if (distanceTest < 300)
                                            allPoints.add(latLng);
                                    }
                                } else {
                                    drawLine(allPoints);
                                    allPoints.clear();
                                }
                            } else {
                                time++;
                            }
                        } while (cursor.moveToNext());
                    }
                    cursor.close();

                    for (int i = 20; i < pts.size(); i++) {
                        pointsNow.add(pts.get(i));
                    }
                    drawLine(pointsNow);
                    for (int i = 1; i < ptNew.size(); i++) {
                        values.put("latitude", ptNew.get(i).latitude);
                        values.put("longitude", ptNew.get(i).longitude);
                        db.insert("Day1", null, values);
                        values.clear();
                    }
                    allPoints.clear();
                    ptNew.clear();
                    isFirstClick = false;
                } else {
                    if (ptNew.size() > 2) {                                                           //舍弃
                        for (int i = 1; i < ptNew.size(); i++) {
                            values.put("latitude", ptNew.get(i).latitude);
                            values.put("longitude", ptNew.get(i).longitude);
                            db.insert("Day1", null, values);
                            ll =ptNew.get(ptNew.size() - 1);
                            values.clear();
                        }
                        drawLine(ptNew);
                        navigate(null, ptNew.get(ptNew.size() - 1));
                        ptNew.clear();
                    }
                }
                break;
           default:
               break;
        }
    }


    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            if (bdLocation.getLocType() == BDLocation.TypeGpsLocation || bdLocation.getLocType()
                    == BDLocation.TypeNetWorkLocation) {

                navigateToFirst(bdLocation);
            }
            LatLng pt = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            if (isFirstGetLocation) {
                firstLat = bdLocation.getLatitude();
                firstLng = bdLocation.getLongitude();
                isFirstGetLocation = false;
            }
            if (pts.size() > 0 || pts.size() == 0) {
                if (pts.size() == 0) {
                    pts.add(pt);
                } else {
                    distance = DistanceUtil.getDistance(pt, pts.get(pts.size() - 1));
                    if (distance < 500 || bdLocation.getNetworkLocationType().equals("wf")) {
                        Toast.makeText(MainActivity.this, bdLocation.getNetworkLocationType(), Toast.LENGTH_SHORT).show();
                        pts.add(pt);
                        ptNew.add(pt);
                        Toast.makeText(MainActivity.this, SDKInitializer.getCoordType()+"", Toast.LENGTH_SHORT).show();
                    } else if (distance > 500 || bdLocation.getLocType() > 161 || bdLocation.getLocType() < 167) {
                        Toast.makeText(MainActivity.this, "大于500" + pts.size(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this, "大于500" + pts.size(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            if (pts.size() > 3)
                Toast.makeText(MainActivity.this, pts.size() + "", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

}
