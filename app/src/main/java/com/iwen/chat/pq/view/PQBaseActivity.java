package com.iwen.chat.pq.view;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Handler;
import android.os.Process;
import android.view.KeyEvent;

import com.iwen.chat.pq.util.ActivityUtil;
import com.iwen.chat.pq.util.NetBroadcastReceiver;
import com.iwen.chat.pq.util.Util;
import com.qmuiteam.qmui.arch.QMUIFragmentActivity;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

public abstract class PQBaseActivity extends QMUIFragmentActivity implements NetBroadcastReceiver.NetChangeListener {
    public static NetBroadcastReceiver.NetChangeListener netEvent;// 网络状态改变监听事件
    private NetBroadcastReceiver netBroadcastReceiver;

    private boolean networkFlag = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



//         添加到Activity工具类
//        ActivityUtil.getInstance().addActivity(this);

//         初始化netEvent
        netEvent = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //实例化IntentFilter对象
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            netBroadcastReceiver = new NetBroadcastReceiver();
            //注册广播接收
            registerReceiver(netBroadcastReceiver, filter);
        }


    }



//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Resources resources = this.getResources();
//        Configuration configuration = resources.getConfiguration();
//        configuration.fontScale = 1;
//        Context context = getBaseContext();
//        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
//    }
//
//    @Override
//    protected void onDestroy() {
//        // Activity销毁时，提示系统回收
//        // System.gc();
//        netEvent = null;
//        // 移除Activity
//        ActivityUtil.getInstance().removeActivity(this);
//        super.onDestroy();
//    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        // 点击手机上的返回键，返回上一层
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            // 移除Activity
//            ActivityUtil.getInstance().removeActivity(this);
//            this.finish();
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    /**
     * 权限检查方法，false代表没有该权限，ture代表有该权限
     */
    public boolean hasPermission(String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    /**
     * 权限请求方法
     */
    public void requestPermission(int code, String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, code);
    }

    /**
     * 处理请求权限结果事件
     *
     * @param requestCode  请求码
     * @param permissions  权限组
     * @param grantResults 结果集
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doRequestPermissionsResult(requestCode, grantResults);
    }

    /**
     * 处理请求权限结果事件
     *
     * @param requestCode  请求码
     * @param grantResults 结果集
     */
    public void doRequestPermissionsResult(int requestCode, @NonNull int[] grantResults) {
    }

    /**
     * 网络状态改变时间监听
     *
     * @param netWorkState true有网络，false无网络
     */
    @Override
    public void onNetChange(boolean netWorkState) {
        if (!netWorkState) {
            Util.showToast(this, "网络不见了o(╥﹏╥)o");
        }
    }
}
