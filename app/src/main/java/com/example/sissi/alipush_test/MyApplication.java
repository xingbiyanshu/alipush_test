package com.example.sissi.alipush_test;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.alibaba.sdk.android.push.register.GcmRegister;
import com.alibaba.sdk.android.push.register.HuaWeiRegister;
import com.alibaba.sdk.android.push.register.MiPushRegister;

/**
 * Created by Sissi on 2018/5/10.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        initCloudChannel(this);
    }


    /**
     * 初始化云推送通道
     * @param applicationContext
     */
    private static final String PUSH = "MSG_PUSH";
    private void initCloudChannel(final Context applicationContext) {
        // 创建notificaiton channel
        this.createNotificationChannel();
        PushServiceFactory.init(applicationContext);
        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                PcTrace.tp(PUSH, PcTrace.INFO, "init cloudchannel success: %s", response);

                PcTrace.tp(PUSH, PcTrace.INFO, "devId=%s, utDevId=%s", pushService.getDeviceId(), pushService.getUTDeviceId());

                pushService.checkPushChannelStatus(new CommonCallback() {
                    @Override
                    public void onSuccess(String s) {
                        PcTrace.tp(PUSH, PcTrace.INFO, "checkPushChannelStatus success: %s", s);
                    }

                    @Override
                    public void onFailed(String s, String s1) {
                        PcTrace.tp(PUSH, PcTrace.INFO, "checkPushChannelStatus failed: %s, %s", s, s1);
                    }
                });
                pushService.listAliases(new CommonCallback() {
                    @Override
                    public void onSuccess(String s) {
                        PcTrace.tp(PUSH, PcTrace.INFO, "listAliases success: %s", s);
                    }

                    @Override
                    public void onFailed(String s, String s1) {
                        PcTrace.tp(PUSH, PcTrace.INFO, "listAliases failed: %s, %s", s, s1);
                    }
                });

                pushService.listTags(CloudPushService.DEVICE_TARGET, new CommonCallback() {
                    @Override
                    public void onSuccess(String s) {
                        PcTrace.tp(PUSH, PcTrace.INFO, "listTags success: %s", s);
                    }

                    @Override
                    public void onFailed(String s, String s1) {
                        PcTrace.tp(PUSH, PcTrace.INFO, "listTags failed: %s, %s", s, s1);
                    }
                });
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                PcTrace.tp(PUSH, PcTrace.INFO, "init cloudchannel failed: errorCode=%s, errorMessage=%s", errorCode, errorMessage);
            }
        });

        MiPushRegister.register(applicationContext, "XIAOMI_ID", "XIAOMI_KEY"); // 初始化小米辅助推送
        HuaWeiRegister.register(applicationContext); // 接入华为辅助推送
        GcmRegister.register(applicationContext, "send_id", "application_id"); // 接入FCM/GCM初始化推送
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // 通知渠道的id
            String id = "1";
            // 用户可以看到的通知渠道的名字.
            CharSequence name = "notification channel";
            // 用户可以看到的通知渠道的描述
            String description = "notification description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // 配置通知渠道的属性
            mChannel.setDescription(description);
            // 设置通知出现时的闪灯（如果 android 设备支持的话）
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            // 设置通知出现时的震动（如果 android 设备支持的话）
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            //最后在notificationmanager中创建该通知渠道
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }
}
