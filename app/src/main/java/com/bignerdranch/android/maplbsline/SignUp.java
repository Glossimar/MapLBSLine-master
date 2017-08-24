package com.bignerdranch.android.maplbsline;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bignerdranch.android.maplbsline.Tools.ClientSocket;

public class SignUp extends AppCompatActivity implements View.OnClickListener{

    private EditText phoneNumberEdit;
    private EditText passwordEdit;
    private EditText nameEdit;
    private Button signUp;
    private ImageView backImage;
    private String phoneNumber;
    private String password;
    private String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initView();
    }

    private void initView() {
        phoneNumberEdit = (EditText) findViewById(R.id.signup_phoneNumber);
        passwordEdit = (EditText) findViewById(R.id.signup_password);
        nameEdit = (EditText) findViewById(R.id.signup_name);
        backImage = (ImageView) findViewById(R.id.signup_image_back);
        signUp = (Button) findViewById(R.id.signup_button_add_new_client);
        signUp.setOnClickListener(this);
        backImage.setOnClickListener(this);
    }
/**
 * iniData : 进行注册数据的获取和发送给服务器
 **/
    private void initData() {
        phoneNumber = phoneNumberEdit.getText().toString();
        password = passwordEdit.getText().toString();
        name = nameEdit.getText().toString();
        if (phoneNumber.equals("")) {
            Toast.makeText(this, "号码不能为空", Toast.LENGTH_SHORT).show();
        } else if (password.equals(""))  {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
        } else if (name.equals("")) {
            Toast.makeText(this, "昵称不能为空", Toast.LENGTH_SHORT).show();
        } else if (!phoneNumber.equals("") && !password.equals("")) {
            if (isIntNumber(phoneNumber) && isTrueCount(phoneNumber)) {
                Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
                ClientSocket.addNewClient(phoneNumber, password, name);
            } else {
                Toast.makeText(this, "请输入有效的电话号码", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.signup_button_add_new_client:
                initData();
                // 如果服务器返回"true"，表示注册成功，跳转至登陆界面进行自动登录 ClientSocket.getAddCResFromServer()
                intent = new Intent(this, SignIn.class);
                intent.putExtra("phoneNumber", phoneNumber);
                intent.putExtra("Password", password);
                intent.putExtra("Name", name);
                intent.putExtra("path","SignUp");
                startActivity(intent);
                finish();
                break;
            case R.id.signup_image_back:
                intent = new Intent(this, SignIn.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * isIntNumber :判断电话号码是否只是数字
     */
    private boolean isIntNumber(String phoneNum) {
        try {
            Long.parseLong(phoneNum);
            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(this, "请输入有效电话号", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * isTrueCount ：判断用户是否输入了正确的号码位数
     */
    private boolean isTrueCount(String phoneNum) {
        try{
            int len = phoneNum.length();
            if (len == 11) {
                return true;
            } else {
                Toast.makeText(this, "请输入有效电话号", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "请输入有效电话号", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
