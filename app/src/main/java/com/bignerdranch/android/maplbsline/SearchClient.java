package com.bignerdranch.android.maplbsline;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.android.maplbsline.Tools.ClientSocket;
import com.bignerdranch.android.maplbsline.Tools.FriendsInfo;
import com.bignerdranch.android.maplbsline.Tools.SetNameListener;


import java.util.List;

public class SearchClient extends AppCompatActivity implements View.OnClickListener{

    private TextView goSearch;
    private TextView myPhoneNum;
    private TextView resultText;
    private TextView resultName;
    private TextView resultNumber;
    private TextView resiltDividingLine;
    private EditText searchEdit;
    private ImageView backSearch;
    private ImageView followImage;
    private RelativeLayout clientRelative;

    private String myPhoneNumber = "0";
    private String searchNumber = "0";
    private String searchName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_client);
        initData();
        initView();
    }

    private void initView() {
        goSearch = (TextView) findViewById(R.id.search_client_goSearch);
        myPhoneNum = (TextView) findViewById(R.id.search_client_myphone_num);
        searchEdit = (EditText) findViewById(R.id.search_client_edit);
        backSearch = (ImageView) findViewById(R.id.search_client_back);
        followImage = (ImageView) findViewById(R.id.search_client_follow);
        resultName = (TextView) findViewById(R.id.search_client_result_name);
        resultNumber = (TextView) findViewById(R.id.search_client_result_number);
        resultText = (TextView) findViewById(R.id.search_client_result_text);
        clientRelative = (RelativeLayout) findViewById(R.id.search_client_result_relative);
        resiltDividingLine = (TextView) findViewById(R.id.search_client_result_dividingline);

        goSearch.setOnClickListener(this);
        backSearch.setOnClickListener(this);
        clientRelative.setOnClickListener(this);
        followImage.setOnClickListener(this);

        myPhoneNum.setText("我的注册号码：" + myPhoneNumber);
    }

    private void initData() {
        Intent intent = getIntent();
        myPhoneNumber = intent.getStringExtra("MyPhoneNumber");
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.search_client_back:
                finish();
                break;
            case R.id.search_client_goSearch:
                searchNumber = searchEdit.getText().toString();
                ClientSocket.checkNumFromServer(searchNumber, new SetNameListener() {
                    @Override
                    public void onFinish(String name) {}

                    @Override
                    public void onFinish(List<FriendsInfo> friendsInfoList) {}

                    @Override
                    public void onFinish(Intent intent) {}

                    @Override
                    public void onFinish(boolean result) {
                        if (result) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    clientRelative.setVisibility(View.VISIBLE);
                                    resultText.setVisibility(View.VISIBLE);
                                    resiltDividingLine.setVisibility(View.VISIBLE);
                                    resultNumber.setText(searchNumber);
                                }
                            });

                            ClientSocket.checkNameFromServer(searchNumber, new SetNameListener() {
                                @Override
                                public void onFinish(final String name) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            searchName = name;
                                            resultName.setText(name);
                                        }
                                    });
                                }

                                @Override
                                public void onFinish(List<FriendsInfo> friendsInfoList) {
                                }

                                @Override
                                public void onFinish(Intent intent) {
                                }

                                @Override
                                public void onFinish(boolean result) {
                                }

                                @Override
                                public void onLocationGetFinish(List<Double> doubleList) {
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    resiltDividingLine.setVisibility(View.GONE);
                                    resultText.setVisibility(View.GONE);
                                    resiltDividingLine.setVisibility(View.GONE);
                                    clientRelative.setVisibility(View.GONE);
                                    Toast.makeText(SearchClient.this, "用户不存在哦", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                    @Override
                    public void onLocationGetFinish(List<Double> doubleList) {}});
                break;
            case R.id.search_client_result_relative:
                intent = new Intent(this, UsersInfo.class);
                intent.putExtra("PhoneNumber", searchNumber);
                intent.putExtra("Name", searchName);
                intent.putExtra("Client", "other");
                startActivity(intent);
                break;
            case R.id.search_client_follow:
                if (followImage.getBackground().getConstantState().equals(getResources()
                        .getDrawable(R.drawable.unfollow).getConstantState())){
                    followImage.setBackgroundResource(R.drawable.follow);
                    Toast.makeText(this, "以后就可以获取TA每天的路线咯", Toast.LENGTH_SHORT).show();
                    ClientSocket.addNewFriends(myPhoneNumber, searchNumber+searchName);
                }
        }
    }
}
