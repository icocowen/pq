package com.iwen.chat.pq;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;

import androidx.annotation.NonNull;

import com.iwen.chat.pq.dao.DataHelper;
import com.iwen.chat.pq.dto.Self;
import com.iwen.chat.pq.http.UserManagement;
import com.iwen.chat.pq.util.NetworkUtil;
import com.iwen.chat.pq.util.StatusBarUtil;
import com.iwen.chat.pq.util.Util;
import com.iwen.chat.pq.view.*;

import java.util.Objects;

import cn.hutool.json.JSONObject;

import static com.iwen.chat.pq.http.UserManagement.FAILURE;
import static com.iwen.chat.pq.http.UserManagement.SUCCESS;

/**
 *
 * 登录页面
 * 登录需要使用到的操作
 * 1. 检查本地是否存储有token
 *    1. 如果没有token，显示本界面
 *    2. 如果存在token，使用token，去服务器校验，校验成功，显示主界面，校验失败显示本界面
 * 2. 登录需要：账号(邮箱)，密码，密码本地加密一遍
 * 优化点：防止多地登录，登录状态，可以使用redis的位（1为登录）标记，0位未登录
 *
 */
public class MainActivity extends PQBaseActivity {


    private long exitTime;
    private ImageView logo;
    private EditText loginEmail;
    private EditText loginPassword;
    private Button btn;
    private TextView register;
    private TextView forgetPassword;




    /*
    * 处理登录事件
    * */
    @SuppressLint("HandlerLeak")
    private  Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS:
                    JSONObject obj =  (JSONObject) msg.obj;
                    if (Objects.equals(obj.get("code"), 200)) {

                        JSONObject data = (JSONObject) obj.get("data");

                        assert data != null;
                        //存储自己的基本信息
                        Self self = new Self(data.get("tokenHead").toString()
                                , data.get("nickName").toString()
                                , data.get("id").toString()
                                , data.get("user").toString()
                                , data.get("token").toString()
                        );
                        DataHelper instance = DataHelper.getInstance();
                        instance.saveSelfInfo(MainActivity.this, self);
                        //登录成功，现在跳转activity
                        toMainHomeActivity(self);
                    }else {
                        Util.showToast(MainActivity.this, (String) obj.get("message"));
                    }
                    break;
                case FAILURE:

                    Bundle data = msg.getData();
                    Util.showToast(MainActivity.this, data.getString("message"));

                    break;

            }

        }
    };


    private void toMainHomeActivity(Self self) {
        Intent intent = new Intent(MainActivity.this, MainHomeActivity.class);
        intent.putExtra("self", self);
        startActivity(intent);
        finish();
    }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);


        StatusBarUtil.setRootViewFitsSystemWindows(this,true);
        StatusBarUtil.setTranslucentStatus(this);
        StatusBarUtil.setStatusBarDarkTheme(this, true);

        checkIsHasSelfInfo();
        checkNetStatus();
        initViews();
        initAnims();


        register.setOnClickListener(this::doRegister);
        forgetPassword.setOnClickListener(this::doForgetPassword);
        btn.setOnClickListener(this::login);
    }

    private void checkNetStatus() {
        boolean connected = NetworkUtil.isNetworkConnected(MainActivity.this);
        if (!connected) {
            Util.showToast(this, "网络不见了o(╥﹏╥)o");
        }
    }

    /**
     * 登录之前先校验是否已经存在self信息
     */
    private void checkIsHasSelfInfo() {
        DataHelper instance = DataHelper.getInstance();
        Self selfInfo = instance.getSelfInfo(MainActivity.this);
        if (selfInfo.getId() == null || selfInfo.getToken() == null) {
            //什么都不做，说明之前没有用户已经登录
        }else {
            toMainHomeActivity(selfInfo);
        }
    }

    private void login(View view) {
        Editable email = loginEmail.getText();
        Editable password = loginPassword.getText();

        UserManagement instance = UserManagement.getInstance();

        instance.login(handler, email.toString(), password.toString());

    }


    private void doForgetPassword(View view) {
        Intent intent = new Intent(getApplicationContext(), ForgetPasswordActivicy.class);
        startActivity(intent);

    }

    private void doRegister(View view) {
        Intent intent = new Intent(getApplicationContext(), RegisterGetCodeActivity.class);
        startActivity(intent);


    }


    /**
     * 初始化View控件
     */
    private void initViews() {
        logo = findViewById(R.id.login_logo);
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        btn = findViewById(R.id.loginBtn);
        register = findViewById(R.id.register);
        forgetPassword = findViewById(R.id.forgetPassword);
    }

    /**
     * 初始化logo图片以及底部注册、登录的按钮动画
     */
    private void initAnims() {
        //初始化底部注册、登录的按钮动画
        //以控件自身所在的位置为原点，从下方距离原点200像素的位置移动到原点
        ObjectAnimator tranLoginEmail = ObjectAnimator.ofFloat(loginEmail, "translationY", 200, 0);
        ObjectAnimator tranLoginPassword = ObjectAnimator.ofFloat(loginPassword, "translationY", 200, 0);
        ObjectAnimator tranBtn = ObjectAnimator.ofFloat(btn, "translationY", 200, 0);
        ObjectAnimator tranRegister = ObjectAnimator.ofFloat(register, "translationY", 200, 0);
        ObjectAnimator tranForgetPassword = ObjectAnimator.ofFloat(forgetPassword, "translationY", 200, 0);
        //将注册、登录的控件alpha属性从0变到1
        ObjectAnimator alphaLoginEmail = ObjectAnimator.ofFloat(loginEmail, "alpha", 0, 1);
        ObjectAnimator alphaLoginPassword = ObjectAnimator.ofFloat(loginPassword, "alpha", 0, 1);
        ObjectAnimator alphaBtn = ObjectAnimator.ofFloat(btn, "alpha", 0, 1);
        ObjectAnimator alphaRegister = ObjectAnimator.ofFloat(register, "alpha", 0, 1);
        ObjectAnimator alphaForgetPassword = ObjectAnimator.ofFloat(forgetPassword, "alpha", 0, 1);
        final AnimatorSet bottomAnim = new AnimatorSet();
        bottomAnim.setDuration(1000);
        //同时执行控件平移和alpha渐变动画
        bottomAnim.play(tranLoginEmail).with(tranLoginPassword)
                .with(tranRegister).with(tranForgetPassword).with(tranBtn)
                .with(alphaRegister).with(alphaForgetPassword)
                .with(alphaLoginEmail).with(alphaLoginPassword).with(alphaBtn);

        //获取屏幕高度
        WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        int screenHeight = metrics.heightPixels;

        //通过测量，获取ivLogo的高度
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        logo.measure(w, h);
        int logoHeight = logo.getMeasuredHeight();

        //初始化ivLogo的移动和缩放动画
        float transY = (screenHeight - logoHeight) * 0.28f;
        //ivLogo向上移动 transY 的距离
        ObjectAnimator tranLogo = ObjectAnimator.ofFloat(logo, "translationY", transY + 120, 0);
        //ivLogo在X轴和Y轴上都缩放0.75倍
        ObjectAnimator scaleXLogo = ObjectAnimator.ofFloat(logo, "scaleX", 1f, 0.75f);
        ObjectAnimator scaleYLogo = ObjectAnimator.ofFloat(logo, "scaleY", 1f, 0.75f);

        AnimatorSet logoAnim = new AnimatorSet();
        logoAnim.setDuration(1000);
        logoAnim.play(tranLogo).with(scaleXLogo).with(scaleYLogo);
        logoAnim.start();
        logoAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //待ivLogo的动画结束后,开始播放底部注册、登录按钮的动画
                bottomAnim.start();
            }
        });
    }


    /**
     * 重写返回键，实现双击退出效果
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                MainActivity.this.finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
