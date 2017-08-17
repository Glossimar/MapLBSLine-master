package com.bignerdranch.android.maplbsline;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bignerdranch.android.imageloadingwan.CircleImageViewWan;
import com.bignerdranch.android.maplbsline.Tools.ClientSocket;
import com.bignerdranch.android.maplbsline.Tools.DataBaseHelper;
import com.bignerdranch.android.maplbsline.Tools.ImageLoad;

import java.sql.ClientInfoStatus;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.bignerdranch.android.maplbsline.Tools.ClientSocket.checkNumFromServer;
import static com.bignerdranch.android.maplbsline.Tools.ClientSocket.checkPasswordFromServer;
import static com.bignerdranch.android.maplbsline.Tools.ClientSocket.updateDay1AndDay2;

public class SignIn extends AppCompatActivity implements View.OnClickListener{

    private String url = "http://p3.zhimg.com/69/d0/69d0ab1bde1988bd475bc7e0a25b713e.jpg";
    private final String TAG = "SginIn_Activity";
    private String password = null;
    private String phoneNum = null;

    private CircleImageViewWan circleImage;
    private EditText phoneNumEdit;
    private EditText passwordEdit;
    private Button signIn;
    private Button signUp;

    public static DataBaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initLayout();
        Intent intent = getIntent();
        String path = intent.getStringExtra("path");

        Log.d(TAG, "loginFromSingnUp: 如果是从注册界面（sinup)跳转回来，进入这个方法，直接进行登陆如果是从注册界面（sinup)跳转回来，进入这个方法，直接进行登陆如果是从注册界面（sinup)跳转回来，进入这个方法，直接进行登陆如果是从注册界面（sinup)跳转回来，进入这个方法，直接进行登陆如果是从注册界面（sinup)跳转回来，进入这个方法，直接进行登陆");
//        接受从公signup返回的数据，如果pat 路径是signup 则直接进行登陆
        if (path == null) {
            Log.d(TAG, "loginFromSingnUp: 如果是从注册界面（sinup)跳转回来，进入这个方法 null null null " );
        } else {
            Log.d(TAG, "onCreate: " + path);
        }
        if (path!= null && path.equals("SignUp")) {
            String phoneNumfromSignUp = intent.getStringExtra("phoneNumber");
            String passwordFromSignUp = intent.getStringExtra("Password");
            Log.d(TAG, "onCreate: " + passwordFromSignUp + phoneNumfromSignUp);
            loginFromSingnUp(phoneNumfromSignUp, passwordFromSignUp);
        }
    }

    private void initLayout() {
        circleImage = (CircleImageViewWan) findViewById(R.id.signin_circle_avatar);
        phoneNumEdit = (EditText) findViewById(R.id.signin_phoneNumber);
        passwordEdit = (EditText) findViewById(R.id.signin_password);
        signIn = (Button) findViewById(R.id.signin_button_login);
        signUp = (Button) findViewById(R.id.signin_button_signUp);

        signIn.setOnClickListener(this);
        signUp.setOnClickListener(this);
//        ImageLoad.getCircleImage(this, url, circleImage);

        updateSQLite();
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.signin_button_login:
                login();
                break;
            case R.id.signin_button_signUp:
                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * checkData : 获取今日日期，来检查是否需要更新数据库
     */
    public long checkDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String  dateSumString = Integer.toString(year) + Integer.toString(month) + Integer.toString(day);
        long dateSum = (long) Integer.parseInt(dateSumString);

        Log.d(TAG, "checkDate: cccccccccccccccccccccccccccccccccccccccc" + dateSum);
        return dateSum;
    }

    private void updateSQLite() {
        dbHelper = new DataBaseHelper(this, "DayLine.db", null, 3);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        Cursor cursor = db.query("date", null, null, null, null, null, null);
        if (cursor.getCount() == 0) {
            values.put("everyday", checkDate());
            db.insert("date", null, values);
            values.clear();
            Log.d(TAG, "updateSQLite: tttttttttttttttttttttttttttttttttttttttt");
        } else if (cursor.moveToLast()) {
            Log.d(TAG, "updateSQLite: vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv" + cursor.getLong(cursor.getColumnIndex("everyday")));
            if (checkDate() > cursor.getLong(cursor.getColumnIndex("everyday"))) {
                Log.d(TAG, "updateSQLite: llllllllllllllllllllllllllllllllllllllllllllll");
                values.put("everyday", checkDate());
                db.insert("date", null, values);
                values.clear();

                db.delete("Day2", null, null);

                do {
                    double lat = cursor.getDouble(cursor.getColumnIndex("latitude"));
                    double log = cursor.getDouble(cursor.getColumnIndex("longitude"));

                    values.put("latitude", lat);
                    values.put("longitude", log);
                    db.insert("Day2", null, values);
                    values.clear();
                } while (cursor.moveToNext());

                db.delete("Day1", null, null);
                ClientSocket.updateDay1AndDay2();

            } else {
                Log.d(TAG, "updateSQLite: uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu");
            }
        }
    }

/**
 *login : 点击进行登陆，从服务器上获取数据并核对
 **/
    public void login() {
        Intent intent;
        password = passwordEdit.getText().toString();
        phoneNum = phoneNumEdit.getText().toString();

        if (password.equals("") || phoneNum.equals("")) {
            if (phoneNum.equals("")) {
                Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d(TAG, "onClick: aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+phoneNum +"aaaaa" +passwordEdit.getText().toString() +phoneNum);
            if (ClientSocket.checkNumFromServer(phoneNum) &&
                    ClientSocket.checkPasswordFromServer(phoneNum, password)) {
                intent = new Intent(SignIn.this, MainActivity.class);
                intent.putExtra("phoneNumber", phoneNum);
                startActivity(intent);
                finish();
            } else if (!ClientSocket.checkNumFromServer(phoneNum)) {
                Log.d(TAG, "onClick: " + ClientSocket.checkNumFromServer(phoneNum) );
                Toast.makeText(this, "用户名不存在", Toast.LENGTH_SHORT).show();
            } else if (!ClientSocket.checkPasswordFromServer(phoneNum, password)) {
                Toast.makeText(this, "密码不正确", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * loginFromSignUp : 如果是从注册界面（sinup)跳转回来，进入这个方法，直接进行登陆
     */
    private void loginFromSingnUp (String phoneNumeFromSignUp, String passworFromSignUp) {
        Log.d(TAG, "loginFromSingnUp: 如果是从注册界面（sinup)跳转回来，进入这个方法，直接进行登陆如果是从注册界面（sinup)跳转回来，进入这个方法，直接进行登陆如果是从注册界面（sinup)跳转回来，进入这个方法，直接进行登陆如果是从注册界面（sinup)跳转回来，进入这个方法，直接进行登陆如果是从注册界面（sinup)跳转回来，进入这个方法，直接进行登陆");
        phoneNumEdit.setText(phoneNumeFromSignUp);
        passwordEdit.setText(passworFromSignUp);
    }
}
