package com.iwen.chat.pq.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.iwen.chat.pq.R;
import com.iwen.chat.pq.http.UserManagement;
import com.iwen.chat.pq.util.StatusBarUtil;
import com.iwen.chat.pq.util.Util;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;

import static com.iwen.chat.pq.http.UserManagement.FAILURE;
import static com.iwen.chat.pq.http.UserManagement.SUCCESS;

public class ForgetPasswordActivicy extends PQBaseActivity {

    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.btn)
    Button btn;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS:
                    JSONObject obj =  (JSONObject) msg.obj;


                    if (Objects.equals(obj.get("code"), 200)) {
                        Util.showToast(ForgetPasswordActivicy.this, (String) obj.get("data"));

                        //成功
                        new Handler().postDelayed((Runnable) () -> {
                            Intent intent = new Intent(ForgetPasswordActivicy.this, ForgetPassword2Activicy.class);
                            startActivity(intent);

                        }, 1000);

                    }else {
                        //失败
                        Util.showToast(ForgetPasswordActivicy.this, (String) obj.get("message"));

                    }

                    break;
                case FAILURE:
                    Bundle data = msg.getData();
                    Util.showToast(ForgetPasswordActivicy.this, data.getString("message"));

            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password_activity);
        ButterKnife.bind(this);

        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
//        StatusBarUtil.setTranslucentStatus(this);
        StatusBarUtil.setStatusBarDarkTheme(this, true);
        TitleBar titleBar = findViewById(R.id.forgetPasswordTitleBar);
        StatusBarUtil.setStatusBarWithTitleBarHasEqualColor(titleBar, this);

        titleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View v) {
                ForgetPasswordActivicy.this.finish();
            }

            @Override
            public void onTitleClick(View v) {

            }

            @Override
            public void onRightClick(View v) {

            }
        });
    }

    @OnClick(R.id.btn)
    public void onNextClicked() {
        UserManagement instance = UserManagement.getInstance();
        String emailStr = email.getText().toString();
        if (!StrUtil.isEmpty(emailStr)) {
            boolean isMatch = ReUtil.isMatch("^\\s*?(.+)@(.+?)\\s*$", emailStr);
            if (isMatch) {
                instance.registerSendEmail(handler,UserManagement.USER_VERIFY_CODE_FORGET,emailStr);
            }else {
                Util.showToast(ForgetPasswordActivicy.this, "邮箱地址格式错误");
            }
        }else {
            Util.showToast(ForgetPasswordActivicy.this, "请输入邮箱地址");
        }
    }
}
