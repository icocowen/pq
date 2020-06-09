package com.iwen.chat.pq.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.iwen.chat.pq.MainActivity;
import com.iwen.chat.pq.R;
import com.iwen.chat.pq.http.UserManagement;
import com.iwen.chat.pq.util.StatusBarUtil;
import com.iwen.chat.pq.util.Util;
import com.longer.verifyedittext.PhoneCode;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;

import static com.iwen.chat.pq.http.UserManagement.FAILURE;
import static com.iwen.chat.pq.http.UserManagement.SUCCESS;

public class RegisterGetCode2Activity extends PQBaseActivity {


    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.nickName)
    EditText nickName;
    @BindView(R.id.phonecode)
    PhoneCode phonecode;
    @BindView(R.id.btn)
    Button btn;

    final String[] code = {""};


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS:
                    JSONObject obj =  (JSONObject) msg.obj;


//                {
//                    "code": 200,
//                        "message": "操作成功",
//                        "data": "注册成功"
//                }
                    Util.showToast(RegisterGetCode2Activity.this, (String) obj.get("data"));

                    if (Objects.equals(obj.get("code"), 200)) {
                        //成功
                        new Handler().postDelayed((Runnable) () -> {
                            Intent intent = new Intent(RegisterGetCode2Activity.this, MainActivity.class);
                            startActivity(intent);

                            finishAffinity();
                        }, 1000);

                    }else {
                        //失败

                    }

                    break;
                case FAILURE:
                    Bundle data = msg.getData();
                    Util.showToast(RegisterGetCode2Activity.this, data.getString("message"));

            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_getcode2_activity);
        ButterKnife.bind(this);

        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
//        StatusBarUtil.setTranslucentStatus(this);
        StatusBarUtil.setStatusBarDarkTheme(this, true);
        TitleBar titleBar = findViewById(R.id.registerGetCode2TitleBar);
        StatusBarUtil.setStatusBarWithTitleBarHasEqualColor(titleBar, this);

        titleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View v) {
                RegisterGetCode2Activity.this.finish();
            }

            @Override
            public void onTitleClick(View v) {

            }

            @Override
            public void onRightClick(View v) {

            }
        });

        phonecode.setOnVCodeCompleteListener(new PhoneCode.OnVCodeInputListener() {

            @Override
            public void vCodeComplete(String verificationCode) {
                code[0] = verificationCode;
            }

            @Override
            public void vCodeIncomplete(String verificationCode) {

            }
        });
    }



    @OnClick(R.id.btn)
    public void onRegisterClicked() {




        String nickNameStr = nickName.getText().toString().trim();
        String passwordStr = password.getText().toString().trim();

        if (StrUtil.isEmpty(code[0]) || StrUtil.isEmpty(nickNameStr) || StrUtil.isEmpty(passwordStr)) {
            Util.showToast(RegisterGetCode2Activity.this, "输入的信息不完整，请确认");
        }else {
            if (nickNameStr.length() > 8 || nickNameStr.length() < 2) {
                Util.showToast(RegisterGetCode2Activity.this, "昵称长度不符合，昵称长度是2到8位");
            }else if (passwordStr.length() > 10 || passwordStr.length() < 4) {
                Util.showToast(RegisterGetCode2Activity.this, "密码长度不符合，密码长度是2到8位");
            }else {
                UserManagement instance = UserManagement.getInstance();
                instance.register(handler, code[0], nickNameStr, passwordStr);
            }
        }


    }
}
