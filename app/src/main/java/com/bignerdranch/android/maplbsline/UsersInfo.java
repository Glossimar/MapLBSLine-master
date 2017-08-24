package com.bignerdranch.android.maplbsline;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.android.maplbsline.Tools.ClientSocket;
import com.bignerdranch.android.maplbsline.Tools.ExpandAdapter;
import com.bignerdranch.android.maplbsline.Tools.FriendsInfo;
import com.bignerdranch.android.maplbsline.Tools.SetNameListener;

import java.util.List;

public class UsersInfo extends AppCompatActivity implements View.OnClickListener{

    private String phoneNumber;
    private String name;
    private String client;

    private TextView phoneNumberView;
    private TextView nameText;
    private ImageView back;
    private ExpandableListView expandableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_info);
        initData();
        initView();
    }

    private void initData() {
        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("PhoneNumber");
        name = intent.getStringExtra("Name");
        client = intent.getStringExtra("Client");
        Log.d("UserInfo", "initData: " + name);
    }
    private void initView() {
        phoneNumberView = (TextView) findViewById(R.id.users_info_phone_num);
        nameText = (TextView) findViewById(R.id.users_info_name);
        back = (ImageView) findViewById(R.id.users_info_back);
        expandableListView = (ExpandableListView) findViewById(R.id.users_info_expand);

        phoneNumberView.setText(phoneNumber);
        nameText.setText(name);

        back.setOnClickListener(this);
        expandableListView.setGroupIndicator(null);
        expandableListView.setAdapter(new ExpandAdapter(this, new SetNameListener() {
            @Override
            public void onFinish(String name) {}

            @Override
            public void onFinish(List<FriendsInfo> friendsInfoList) {}

            @Override
            public void onFinish(Intent intent) {
                intent.putExtra("Client", "me");
                intent.putExtra("PhoneNumber", phoneNumber);
            }

            @Override
            public void onFinish(boolean result) {}

            @Override
            public void onLocationGetFinish(List<Double> doubleList) {}
        }));

    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
//            case R.id.users_info_track:
//                intent = new Intent(this, TrackMap.class);
//                intent.putExtra("PhoneNumber", phoneNumber);
//                intent.putExtra("Date", "today");
//                intent.putExtra("Client", "me");
//                startActivity(intent);
//                break;
//            case R.id.users_info_toMap:
//                intent = new Intent(this, TrackMap.class);
//                intent.putExtra("PhoneNumber", phoneNumber);
//                intent.putExtra("Date", "today");
//                intent.putExtra("Client", "me");
//                startActivity(intent);
//                break;
            case R.id.users_info_back:
                finish();
            default:
                break;
        }
    }
}
