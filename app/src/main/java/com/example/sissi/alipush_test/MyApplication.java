package com.example.sissi.alipush_test;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Process;
import android.provider.Settings;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.alibaba.sdk.android.push.register.GcmRegister;
import com.alibaba.sdk.android.push.register.HuaWeiRegister;
import com.alibaba.sdk.android.push.register.MiPushRegister;
import com.sissi.droidw.utils.KLog;

import java.io.File;

/**
 * Created by Sissi on 2018/5/10.
 */

public class MyApplication extends Application {
    private static int count = 0;
    private static boolean canAutostart = false; // 应用是否能自启动
    private AppOpsManager appOpsManager;

    @Override
    public void onCreate() {
        super.onCreate();
        ++count;
        KLog.p("===============count=%s",count);
        printStartingInfo();

        if (!getPackageName().equals(getCurrentProcessName())){
            return; // 非主进程（推送进程）不需要走后续逻辑
        }

        initCloudChannel(this);

//        isNotificationEnabled();
//
//        appOpsManager = (AppOpsManager) getSystemService(APP_OPS_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            appOpsManager.startWatchingMode(AppOpsManager.OPSTR_READ_CONTACTS, getPackageName(), onOpChangedListener);
//        }
//
//        Foreground.init(this);
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // 3秒过后还没有activity启动则认为应用为自启动 XXX 拙劣的方案,待改进!
//                if (!canAutostart) {
//                    canAutostart = !Foreground.get().isActivityStarted();
//                }
//                KLog.p("===============canAutostart=%s", canAutostart);
//            }
//        }, 3000);
    }

    private String getCurrentProcessName() {
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
            if (process.pid == pid) {
                processName = process.processName;
            }
        }
        return processName;
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
                KLog.tp(PUSH, KLog.INFO, "init cloudchannel success: %s", response);

                KLog.tp(PUSH, KLog.INFO, "devId=%s, utDevId=%s", pushService.getDeviceId(), pushService.getUTDeviceId());

                pushService.checkPushChannelStatus(new CommonCallback() {
                    @Override
                    public void onSuccess(String s) {
                        KLog.tp(PUSH, KLog.INFO, "checkPushChannelStatus success: %s", s);
                    }

                    @Override
                    public void onFailed(String s, String s1) {
                        KLog.tp(PUSH, KLog.INFO, "checkPushChannelStatus failed: %s, %s", s, s1);
                    }
                });
                pushService.listAliases(new CommonCallback() {
                    @Override
                    public void onSuccess(String s) {
                        KLog.tp(PUSH, KLog.INFO, "listAliases success: %s", s);
                    }

                    @Override
                    public void onFailed(String s, String s1) {
                        KLog.tp(PUSH, KLog.INFO, "listAliases failed: %s, %s", s, s1);
                    }
                });

                pushService.listTags(CloudPushService.DEVICE_TARGET, new CommonCallback() {
                    @Override
                    public void onSuccess(String s) {
                        KLog.tp(PUSH, KLog.INFO, "listTags success: %s", s);
                    }

                    @Override
                    public void onFailed(String s, String s1) {
                        KLog.tp(PUSH, KLog.INFO, "listTags failed: %s, %s", s, s1);
                    }
                });
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                KLog.tp(PUSH, KLog.INFO, "init cloudchannel failed: errorCode=%s, errorMessage=%s", errorCode, errorMessage);
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



    private void printStartingInfo() {

        String debugOrRelease = BuildConfig.DEBUG ? "D" : "R";

        File f = Environment.getExternalStorageDirectory();
        String storagePath = f.getAbsolutePath();
        String storageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(storageState)){
            boolean readable = f.canRead();
            boolean writable = f.canWrite();
            String rw="";
            if (readable){
                rw+="r";
            }
            if (writable){
                rw+="w";
            }
            if (!rw.isEmpty()){
                storageState += "("+rw+")";
            }
        }

        StringBuffer abis = new StringBuffer();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (String abi : Build.SUPPORTED_ABIS){
                abis.append(abi).append(" ");
            }
        }else{
            abis.append(Build.CPU_ABI).append(" ").append(Build.CPU_ABI2);
        }

        System.out.println("\n=================================================================="
                + "\n===============  " + Build.BRAND + " " + Build.MODEL  + "\t Android " + Build.VERSION.RELEASE
                + "\n===============  " + "supported ABIs(The first is the most preferred):" +abis
                + "\n===============  " + getPackageName() + "(" + debugOrRelease
                + "\n===============  external storage: " + " path="+storagePath + " state=" + storageState
                + "\n=================================================================\n");
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) { // 看修改"自启动权限"及“通知”权限时会不会走该回调 （实测结果并不会走）
        super.onConfigurationChanged(newConfig);

        KLog.p("newConfig=%s", newConfig);
    }

    private AppOpsManager.OnOpChangedListener onOpChangedListener = new AppOpsManager.OnOpChangedListener() {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void onOpChanged(String op, String packageName) { // 设置项有变化时触发该回调
            KLog.p("op=%s", op);
            int uid = Process.myUid();
            KLog.p("uid=%s", uid);
            int mode = appOpsManager.noteOpNoThrow(op, uid, getPackageName()); // 判断设置项是否开启
            KLog.p("mode=%s", mode);

//            appOpsManager.stopWatchingMode(onOpChangedListener);
        }
    };

    /*
 * 判断通知权限是否打开
 */
//    private boolean isNotificationEnable(Context context) {
//        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(APP_OPS_SERVICE);
//        ApplicationInfo appInfo = context.getApplicationInfo();
//
//        String pkg = context.getApplicationContext().getPackageName();
//        int uid = appInfo.uid;
//
//        Class appOpsClass = null; /* Context.APP_OPS_MANAGER */
//
//        try{
//            appOpsClass = Class.forName(AppOpsManager.class.getName());
//            Method checkOpNoThrowMethod  = appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE, String.class);
//
//            Field opPostNotificationValue = appOpsClass.getDeclaredField("OP_POST_NOTIFICATION");
//            int value = (int)opPostNotificationValue.get(Integer.class);
//            return ((int)checkOpNoThrowMethod.invoke(mAppOps,value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        return true;
//    }

//    private boolean isNotificationEnabled(){
//        String ntfs = Settings.Secure.getString(getContentResolver(),Settings.Secure.DEFAULT_INPUT_METHOD);
//        KLog.p("ntfs=%s", ntfs);
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = Guider.guide2AppNotification(MyApplication.this);
//                startActivity(intent);
//            }
//        }, 3000);
//        return ntfs.contains(getApplicationContext().getPackageName());
////        if (ntfs.contains(getApplicationContext().getPackageName())) {
////            //service is enabled do something
////        } else {
////            //service is not enabled try to enabled by calling...
////            getApplicationContext().startActivity(new Intent(
////                    "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
////        }
//    }

}
