package com.bignerdranch.android.maplbsline;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.bignerdranch.android.maplbsline.Tools.ClientSocket;
import com.bignerdranch.android.maplbsline.Tools.FriendsAdapter;
import com.bignerdranch.android.maplbsline.Tools.FriendsInfo;
import com.bignerdranch.android.maplbsline.Tools.SetNameListener;

import java.util.List;

public class MyFriends extends AppCompatActivity implements View.OnClickListener{
    private RecyclerView recycler;
    private Toolbar toolbar;
    private ImageView backImage;

    private String myPhoneNumber;
    private FriendsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        myPhoneNumber = intent.getStringExtra("MyPhoneNumber");
        initView();
        ClientSocket.checkFriendsFromServer(myPhoneNumber, new SetNameListener() {
            @Override
            public void onFinish(String name) {}

            @Override
            public void onFinish(final List<FriendsInfo> friendsInfoList) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new FriendsAdapter(MyFriends.this,friendsInfoList);
                        LinearLayoutManager manager = new LinearLayoutManager(MyFriends.this);
                        recycler.setAdapter(adapter);
                        recycler.setLayoutManager(manager);
                    }
                });
            }

            @Override
            public void onFinish(Intent intent) {}

            @Override
            public void onFinish(boolean result) {}

            @Override
            public void onLocationGetFinish(List<Double> doubleList) {}
        });
    }

    private void initView() {
        recycler = (RecyclerView) findViewById(R.id.my_friends_recycler);
        toolbar = (Toolbar) findViewById(R.id.my_friends_toolbar);
        backImage = (ImageView) findViewById(R.id.my_friends_back);

        backImage.setOnClickListener(this);
        toolbar.setTitle("");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_friends_back:
                finish();
                break;
        }
    }
}
