package com.iwen.chat.pq.http;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/*
* 用户操作相关类
* 对用户操作类
* 单例对象
* handler 的通用写法
switch (msg.what) {
    case SUCCESS:

        JSONObject obj =  (JSONObject) msg.obj;
        if (Objects.equals(obj.get("code"), 200)) {//200代表成功
            JSONObject data = (JSONObject) obj.get("data");
            //200
        }else {
            Log.e(MainHomeActivity.this.getClass().getName(), Objects.requireNonNull((String) obj.get("message")));
        }
        break;
    case FAILURE:
        Bundle data = msg.getData();
        Log.e(MainHomeActivity.this.getClass().getName(), Objects.requireNonNull(data.getString("message")));
}

*
* 可以把每次传输token的步骤优化，使用okhttp的特性
*
* */
public class UserManagement {

    private final static String BASE_URL = "http://192.168.0.15:8080/api";
    public static final String USER_VERIFY_CODE_FORGET = "/user/verify/code/forget";
    private final static int TIME_OUT = 2;
    public static final String USER_VERIFY_CODE = "/user/verify/code";
    public static final String MAIL = "mail";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String USER_LOGIN = "/user/login";
    public final static int SUCCESS = 1;
    public final static int FAILURE = 2;
    public static final String CODE = "code";
    public static final String NICK_NAME = "nickName";
    public static final String USER_REGISTER = "/user/register";
    public static final String FORGET_PASSWORD = "/user/forget/password";
    public static final String USER_FRIENDS = "/user/friends";
    public static final String AUTHORIZATION = "Authorization";
    public static final String TIME = "time";
    public static final String USER_GROUPS = "/user/groups";
    public static final String USER_HOT_MESSAGE = "/user/hot/message";

    private UserManagement() {}

    private static class UserManagementInner {
        static final UserManagement userManagement = new UserManagement();
    }

    public static UserManagement getInstance() {
        return UserManagementInner.userManagement;
    }

    private void builderRequest(Handler handler,Request request) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS).build();

        Message msg = new Message();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                msg.what = 2;//失败
                Bundle bundle = new Bundle();
                if(e instanceof SocketTimeoutException){//判断超时异常
                    bundle.putString("message", "连接超时＿|￣|●");
                }else if(e instanceof ConnectException){//判断连接异常，我这里是报Failed to connect
                    bundle.putString("message", "网络不见了o(╥﹏╥)o");
                }
                msg.setData(bundle);
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject obj = JSONUtil.parseObj(response.body().string());
                msg.what = 1;
                msg.obj = obj;
                handler.sendMessage(msg);
            }
        });

    }

    public void register(Handler handler, String code, String nickNameStr, String passwordStr) {
        FormBody body = new FormBody.Builder()
                .add(CODE, code)
                .add(PASSWORD, passwordStr)
                .add(NICK_NAME, nickNameStr)
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + USER_REGISTER)
                .post(body)
                .build();
        builderRequest(handler, request);
    }

    public void forgetPassword(Handler handler, String code, String passwordStr) {
        FormBody body = new FormBody.Builder()
                .add(CODE, code)
                .add(PASSWORD, passwordStr)
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + FORGET_PASSWORD)
                .post(body)
                .build();
        builderRequest(handler, request);
    }

    public void login(Handler handler, String email, String password)  {
        FormBody body = new FormBody.Builder().add(EMAIL, email)
                .add(PASSWORD, password).build();

        Request request = new Request.Builder()
                .url(BASE_URL + USER_LOGIN)
                .post(body)
                .build();
        builderRequest(handler, request);
    }

    public void registerSendEmail(Handler handler, String uri, String email) {

        // TODO: 2020/6/7 这里需要头部提交token和参数email
        HttpUrl url = HttpUrl.get(BASE_URL + uri).newBuilder()
                .addQueryParameter(MAIL, email)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        builderRequest(handler, request);

    }

    public void friends(Handler handler, String token, long time) {
        HttpUrl url = HttpUrl.get(BASE_URL + USER_FRIENDS).newBuilder()
                .addQueryParameter(TIME, String.valueOf(time))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader(AUTHORIZATION, token)
                .build();
        builderRequest(handler, request);
    }

    public void groups(Handler handler, String token, long time) {
        HttpUrl url = HttpUrl.get(BASE_URL + USER_GROUPS).newBuilder()
                .addQueryParameter(TIME, String.valueOf(time))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader(AUTHORIZATION, token)
                .build();
        builderRequest(handler, request);
    }

    public void hotMessage(Handler handler, String token) {
        HttpUrl url = HttpUrl.get(BASE_URL + USER_HOT_MESSAGE).newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader(AUTHORIZATION, token)
                .build();
        builderRequest(handler, request);
    }
}
