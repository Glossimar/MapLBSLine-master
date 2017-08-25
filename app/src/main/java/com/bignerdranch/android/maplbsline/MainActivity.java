package com.bignerdranch.android.maplbsline;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.bignerdranch.android.maplbsline.Tools.ClientSocket;
import com.bignerdranch.android.maplbsline.Tools.DataBaseHelper;
import com.bignerdranch.android.maplbsline.Tools.FriendsInfo;
import com.bignerdranch.android.maplbsline.Tools.SetNameListener;

import java.util.ArrayList;
import java.util.List;

import static com.bignerdranch.android.maplbsline.SignIn.dbHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "MainActivity";
    public LocationClient mLocationClient;
    public LatLng pt;
    public List<LatLng> pts = new ArrayList<LatLng>();
    public List<LatLng> ptNew = new ArrayList<>();

    private BaiduMap baiduMap;
    private MapView mapView;
    private Toolbar toolbar;
    private TextView drawerNumber;
    private TextView drawerName;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FloatingActionButton floatingButton;
    private FloatingActionButton trackFloating;
    private Polyline mPolyline;
    private double distance;

    private boolean isFirstLocate = true;
    private boolean isFirstGetLocation = true;
    private boolean isFirstClick = true;
    private double firstLat;
    private double firstLng;
    private long firstTime = 0;
    private int i = 0;
    private String myPhoneNumber;
    private String myName;

//    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        initView();
        getPermissionAllow();
    }

    public void initView() {
        floatingButton = (FloatingActionButton) findViewById(R.id.main_floatingButton);
        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawerlayout);
        navigationView = (NavigationView) findViewById(R.id.drawer_navigation);
        trackFloating = (FloatingActionButton) findViewById(R.id.main_floatingButton_getLine);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mapView = (MapView) findViewById(R.id.main_mapview);
        drawerName = (TextView) findViewById(R.id.navigation_name);
        drawerNumber = (TextView) findViewById(R.id.navigation_phone_number);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        floatingButton.setOnClickListener(this);
        trackFloating.setOnClickListener(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        if (isFirstLocate) Toast.makeText(this, "定位校准中，一分钟以后再按右边的图标绘制路线哦", Toast.LENGTH_LONG).show();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                drawerLayout.closeDrawers();
                switch (item.getItemId()){
                    case R.id.menu_name:
                        intent = new Intent(MainActivity.this, UsersInfo.class);
                        intent.putExtra("PhoneNumber", myPhoneNumber);
                        intent.putExtra("Name", myName);
                        intent.putExtra("Client", "me");
                        startActivity(intent);
                        break;
                    case R.id.menu_phone_num:
                        intent = new Intent(MainActivity.this, UsersInfo.class);
                        intent.putExtra("PhoneNumber", myPhoneNumber);
                        intent.putExtra("Name", myName);
                        intent.putExtra("Client", "me");
                        startActivity(intent);
                        break;
                    case R.id.menu_quit:
                        intent = new Intent(MainActivity.this, SignIn.class);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put("latitude", 0d);
                        values.put("longitude", 0d);
                        db.insert("Day1", null, values);
                        values.clear();
                        ptNew.add(new LatLng(0d, 0d));
                        ClientSocket.addLocationLatitude(myPhoneNumber, "today", ptNew, "false");
                        ClientSocket.addLocationLongitude(myPhoneNumber, "today", ptNew);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.menu_friends:
                        intent = new Intent(MainActivity.this, MyFriends.class);
                        intent.putExtra("MyPhoneNumber", myPhoneNumber);
                        startActivity(intent);
                }
                return true;
            }
        });

        Intent intent = getIntent();
        myPhoneNumber = intent.getStringExtra("phoneNumber");
        Log.d(TAG, "initView: " + drawerNumber);
        drawerNumber.setText("手机号码 ： " + myPhoneNumber);
        ClientSocket.checkNameFromServer(myPhoneNumber, new SetNameListener() {
            @Override
            public void onFinish(String name) {
                myName = name;
                drawerName.setText(name);
            }

            @Override
            public void onFinish(List<FriendsInfo> friendsInfoList) {}

            @Override
            public void onFinish(Intent intent) {}

            @Override
            public void onFinish(boolean result) {}

            @Override
            public void onLocationGetFinish(List<Double> doubleList) {}
        });

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

/**
 * 绘制路线
 **/

    private void drawLine(final List<LatLng> latLngList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (latLngList.size() > 2) {
                    OverlayOptions ooPolyline = new PolylineOptions().width(10)
                            .color(0xAAFF0000).points(latLngList);
                    mPolyline = (Polyline) baiduMap.addOverlay(ooPolyline);
                    Log.d(TAG, "drawLine: " + pts.size() + pts);
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
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("latitude", 0d);
                values.put("longitude", 0d);
                db.insert("Day1", null, values);
                values.clear();

                ptNew.add(new LatLng(0d, 0d));
                while (true) {
                    if (ClientSocket.addLocationLatitude(myPhoneNumber, "today", ptNew, "false") && ClientSocket.addLocationLongitude(myPhoneNumber, "today", ptNew))
                        System.exit(0);
                }
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
        switch (v.getId()) {
            case R.id.main_floatingButton_getLine:
                if (isFirstClick) {
                    // 进入程序时第一次点击会将数据库之前的路线进行绘制
                    Cursor cursor = db.query("Day1", null, null, null, null, null, null);
                    int time = 0;
                    if (cursor.moveToFirst()) {
                        do {
                            //  开始从数据库中获取之前的坐标数据，将之前的坐标加入list,在drawline绘出当日的路线
                            double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
                            double longtitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
                            LatLng latLng = new LatLng(latitude, longtitude);
                            if (time > 5) {
                                // 为了避免前几个坐标定位偏差，从第六个开始获取坐标点
                                if (latitude != 0 && longtitude != 0) {
                                    // 在每次退出应用时，输入坐标为（0，0），作为标记，
                                    if (allPoints.size() < 2) {
                                        allPoints.add(latLng);
                                    } else {
                                        // 如果两个点之间坐标大于300，则舍弃这个坐标
                                        double distanceTest = DistanceUtil.getDistance(latLng, allPoints.get(allPoints.size() - 1));
                                        if (distanceTest < 300)
                                            allPoints.add(latLng);
                                    }
                                } else {
                                    //  如果到（0，0）就结束加点，将图线绘制出来，清空list
                                    drawLine(allPoints);
                                    allPoints.clear();
                                }
                            } else {
                                time++;
                            }
                        } while (cursor.moveToNext());
                    }
                    cursor.close();

                    // 获取当前行走的坐标点，进行此次路线的绘制
                    for (int i = 20; i < pts.size(); i++) {
                        // 从第20个开始获取坐标点，避免刚刚启动时的定位偏差
                        pointsNow.add(pts.get(i));
                    }
                    // 路线绘制
                    drawLine(pointsNow);
                    List<LatLng> firstList = new ArrayList<>();
                    for (int i = 1; i < ptNew.size(); i++) {
                        // 将这次的的点加入到数据库
                        values.put("latitude", ptNew.get(i).latitude);
                        values.put("longitude", ptNew.get(i).longitude);
                        db.insert("Day1", null, values);
                        firstList.add(ptNew.get(i));
                        values.clear();
                    }
//                    navigate(null, ptNew.get(ptNew.size() - 1));
                    // 清空坐标list
                    Log.d(TAG, "run: " + allPoints.size());
                    ClientSocket.addLocationLatitude(myPhoneNumber, "today", firstList, "false");
                    ClientSocket.addLocationLongitude(myPhoneNumber, "today", firstList);
                    allPoints.clear();
                    ptNew.clear();
                    isFirstClick = false;
                } else {
                    List<LatLng> La = new ArrayList<>();
                    // 如果是>1次点击按钮，则不进行之前路线的绘制，只进行上一次点击按钮到此次点击走过的路线的绘制
                    if (ptNew.size() > 1) {                                                           //舍弃
                        for (int i = 0; i < ptNew.size(); i++) {
                            values.put("latitude", ptNew.get(i).latitude);
                            values.put("longitude", ptNew.get(i).longitude);
                            db.insert("Day1", null, values);
                            ptNew.get(ptNew.size() - 1);
                            La.add(ptNew.get(i));
                            values.clear();
                        }
                        drawLine(La);
                        //每点一次都会将地图移到自己所在位置
//                        navigate(null, ptNew.get(ptNew.size() - 1));
                        ClientSocket.addLocationLatitude(myPhoneNumber, "today", La, "false");
                        ClientSocket.addLocationLongitude(myPhoneNumber, "today", La);
                        ptNew.clear();
                    }
                }
                break;
            case R.id.main_floatingButton:
                if (ptNew.size() != 0)
                navigate(null, pt);
           default:
               break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.toolbar_menu_search:
                Intent intent = new Intent(this, SearchClient.class);
                intent.putExtra("MyPhoneNumber", myPhoneNumber);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }

    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            if (bdLocation.getLocType() == BDLocation.TypeGpsLocation || bdLocation.getLocType()
                    == BDLocation.TypeNetWorkLocation) {

                navigateToFirst(bdLocation);
            }
            pt = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
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
                        pts.add(pt);
                        ptNew.add(pt);
                    } else if (distance > 500 || bdLocation.getLocType() > 161 || bdLocation.getLocType() < 167) {
                        Log.d(TAG, "onReceiveLocation: 两坐标点之间的距离大于500米， 距离为" + distance);
                    }
                }
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }
}
