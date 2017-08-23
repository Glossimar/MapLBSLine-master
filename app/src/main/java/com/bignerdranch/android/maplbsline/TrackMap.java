package com.bignerdranch.android.maplbsline;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
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

public class TrackMap extends AppCompatActivity implements View.OnClickListener{
    private MapView mapView;
    private ImageView backImage;
    private Polyline mPolyline;
    private BaiduMap baiduMap;

    private String date;
    private String client;
    private String phoneNumber;
    private List<LatLng> trackList;
    private List<Double> latitudeList;
    private List<Double> longitudeList;

    private DataBaseHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_track_map);
        mapView = (MapView) findViewById(R.id.track_map_mapview);
        mapView.setClickable(true);
        initData();
        initView();
    }

    private void initData() {
        Intent intent = getIntent();
        date = intent.getStringExtra("Date");
        client = intent.getStringExtra("Client");
        phoneNumber = intent.getStringExtra("PhoneNumber");
        dbHelper = new DataBaseHelper(this, "DayLine.db", null, 3);
        db = dbHelper.getWritableDatabase();
        trackList = new ArrayList<>();
        baiduMap = mapView.getMap();
        Log.d("TrackMap", "initData: " + phoneNumber + client + date);
    }

    private void initView() {
        backImage = (ImageView) findViewById(R.id.track_map_back);
        backImage.setOnClickListener(this);
        if (client.equals("me")){
            if (date.equals("today")){
//                getListFromDB("Day1");

                ClientSocket.getLocationLatitudeFromServer("today", phoneNumber, new SetNameListener() {
                    @Override
                    public void onFinish(String name) {}

                    @Override
                    public void onFinish(List<FriendsInfo> friendsInfoList) {}

                    @Override
                    public void onFinish(Intent intent) {}

                    @Override
                    public void onLocationGetFinish(List<Double> doubleList) {
                        Log.d("TrackMap", "initView: jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj");
                        latitudeList = doubleList;
                    }
                });
                ClientSocket.getLocationLongitudeFromServer("today", phoneNumber, new SetNameListener() {
                    @Override
                    public void onFinish(String name) {}

                    @Override
                    public void onFinish(List<FriendsInfo> friendsInfoList) {}

                    @Override
                    public void onFinish(Intent intent) {}

                    @Override
                    public void onLocationGetFinish(final List<Double> doubleList) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                longitudeList = doubleList;
                                trackList = new ArrayList<LatLng>();
                                Log.d("TrackMap", "onLocationGetFinish: " + doubleList.size());
                                initLocationLine(latitudeList, longitudeList);
                            }
                        });
                    }
                });
            } else if (date.equals("yesterday")) {
//
                ClientSocket.getLocationLatitudeFromServer("yesterday", phoneNumber, new SetNameListener() {
                    @Override
                    public void onFinish(String name) {}

                    @Override
                    public void onFinish(List<FriendsInfo> friendsInfoList) {}

                    @Override
                    public void onFinish(Intent intent) {}

                    @Override
                    public void onLocationGetFinish(List<Double> doubleList) {
                        Log.d("TrackMap", "initView: jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj");
                        latitudeList = doubleList;
                    }
                });
                ClientSocket.getLocationLongitudeFromServer("yesterday", phoneNumber, new SetNameListener() {
                    @Override
                    public void onFinish(String name) {}

                    @Override
                    public void onFinish(List<FriendsInfo> friendsInfoList) {}

                    @Override
                    public void onFinish(Intent intent) {}

                    @Override
                    public void onLocationGetFinish(final List<Double> doubleList) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                longitudeList = doubleList;
                                trackList = new ArrayList<LatLng>();
                                Log.d("TrackMap", "onLocationGetFinish: " + doubleList.size());
                                initLocationLine(latitudeList, longitudeList);
                            }
                        });
                    }
                });
            }
        } else if (client.equals("other")) {
            if (date.equals("today")) {
            } else if (date.equals("yesterday")) {
            }
        }
    }


    private void drawLine(final List<LatLng> latLngList) {
        Log.d("TrackMap", "run: cccccccccccccccccccccccccccccc");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (latLngList.size() > 2) {
                    OverlayOptions ooPolyline = new PolylineOptions().width(10)
                            .color(0xAAFF0000).points(latLngList);
                    mPolyline = (Polyline) baiduMap.addOverlay(ooPolyline);
                    Log.d("TrackMap", "drawLine: bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb" + latLngList.size());
                    navigate(latLngList.get(latLngList.size() - 1));
                }
            }
        });
    }

    /**
     * getListFromDB: 从数据库里获取坐标点，并且在图中绘制出来；
     */
    private void initLocationLine(List<Double> latList, List<Double> lonList) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                cursor = db.query(bookName, null, null, null, null, null, null);
//                if (cursor.moveToFirst()) {
//                    int time = 0;
//                    do {
        for (int i = 0; i < lonList.size(); i++) {
                        double latitude = latList.get(i);
                        double longtitude = lonList.get(i);
                        LatLng latLng = new LatLng(latitude, longtitude);
                            // 为了避免前几个坐标定位偏差，从第六个开始获取坐标点
                            if (latitude != 0 && longtitude != 0) {
                                // 在每次退出应用时，输入坐标为（0，0），作为标记，
                                if (trackList.size() < 2) {
                                    trackList.add(latLng);
                                } else {
                                    // 如果两个点之间坐标大于300，则舍弃这个坐标
                                    double distanceTest = DistanceUtil.getDistance(latLng, trackList.get(trackList.size() - 1));
                                    if (distanceTest < 300)
                                        Log.d("TrackMap", "getListFromDB: " + latLng.longitude + latLng.latitude);
                                        trackList.add(latLng);
                                }
                            } else {
                                //  如果到（0，0）就结束加点，将图线绘制出来，清空list
                                Log.d("TrackMap", "onClick: nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
                                drawLine(trackList);
                                trackList.clear();
                            }
                        }
//                     while (cursor.moveToNext());
//                }
                drawLine(trackList);
//                cursor.close();
//            }
//        }).start();

    }

    /**
     * 在绘图结束时将地图移到最后一个点所在的位置
     */
    private void navigate(LatLng lastLocation) {
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(lastLocation);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
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
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.track_map_back:
                finish();
                break;
            default:
                break;
        }
    }
}
