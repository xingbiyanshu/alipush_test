package com.example.sissi.alipush_test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;

import com.sissi.droidw.settings.Checker;
import com.sissi.droidw.settings.Guider;
import com.sissi.droidw.utils.KLog;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        printPermissions(this);

        if (!Checker.isAppNotificationEnabled(this)){
            Intent intent = Guider.guide2AppNotification(this);
            startActivity(intent);
        }
        if (!Checker.isAppAutoLaunchEnabled(this)){
            Intent intent = Guider.guide2AppAutoLaunch(this);
            startActivity(intent);  // TODO 一次性完成所有设置再回到应用
        }
    }

    private void printPermissions(Context context){
        try {
            PackageManager pm = getPackageManager();
            String packName = context.getPackageName();
            PackageInfo packInfo = pm.getPackageInfo(packName, PackageManager.GET_PERMISSIONS);
            String[] permissions = packInfo.requestedPermissions;
            StringBuilder sb = new StringBuilder();
            sb.append("=========permissions=========\n");
            for (String perm:permissions){
                sb.append(perm+" ").append(pm.checkPermission(perm, packName)).append("\n");
            }
            KLog.p("%s", sb.toString());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean hasAutolaunchPermission(Context context){

        return PackageManager.PERMISSION_GRANTED  == getPackageManager().checkPermission("android.permission.RECEIVE_BOOT_COMPLETED", context.getPackageName());

//        ComponentName componentName = new ComponentName("com.example.sissi.alipush_test","com.taobao.accs.EventReceiver");
//        int a = getPackageManager().getComponentEnabledSetting(componentName);
//        KLog.p("getComponentEnabledSetting=%s", a);
//        return PackageManager.COMPONENT_ENABLED_STATE_ENABLED == a;
    }

    private void applyAutolaunchPermission(Context context) {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        context.startActivity(intent);
        return;
//
//        Intent intent = new Intent();
//        try {
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            String manufacturer = Build.MANUFACTURER;
//            KLog.p("MANUFACTURER=%s", manufacturer);
//            ComponentName componentName = null;
//            if (manufacturer.equals("Xiaomi")) { // 红米Note4测试通过
//                componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity");
//            } else if (manufacturer.equals("Letv")) { // 乐视2测试通过
//                intent.setAction("com.letv.android.permissionautoboot");
//            } else if (manufacturer.equals("samsung")) { // 三星Note5测试通过
//                componentName = new ComponentName("com.samsung.android.sm_cn", "com.samsung.android.sm.ui.ram.AutoRunActivity");
//            } else if (manufacturer.equals("HUAWEI")) { // 华为测试通过
//                componentName = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity");
//            } else if (manufacturer.equals("vivo")) { // VIVO测试通过
//                componentName = ComponentName.unflattenFromString("com.iqoo.secure/.safeguard.PurviewTabActivity");
//            } else if (manufacturer.equals("Meizu")) { //万恶的魅族
//                // 通过测试，发现魅族是真恶心，也是够了，之前版本还能查看到关于设置自启动这一界面，系统更新之后，完全找不到了，心里默默Fuck！
//                // 针对魅族，我们只能通过魅族内置手机管家去设置自启动，所以我在这里直接跳转到魅族内置手机管家界面，具体结果请看图
//                componentName = ComponentName.unflattenFromString("com.meizu.safe/.permission.PermissionMainActivity");
//            } else if (manufacturer.equals("OPPO")) { // OPPO R8205测试通过
////                componentName = ComponentName.unflattenFromString("com.oppo.safe/.permission.startup.StartupAppListActivity");
//                componentName = ComponentName.unflattenFromString("com.oppo.safe/.permission.startup.StartupAppListActivity");
//                Intent intentOppo = new Intent();
//                intentOppo.setClassName("com.oppo.safe/.permission.startup", "StartupAppListActivity");
//                if (context.getPackageManager().resolveActivity(intentOppo, 0) == null) {
//                    componentName = ComponentName.unflattenFromString("com.coloros.safecenter/.startupapp.StartupAppListActivity");
//                }
//            } else if (manufacturer.equals("ulong")) { // 360手机 未测试
//                componentName = new ComponentName("com.yulong.android.coolsafe", ".ui.activity.autorun.AutoRunListActivity");
//            } else {
//                // 以上只是市面上主流机型，由于公司你懂的，所以很不容易才凑齐以上设备
//                // 针对于其他设备，我们只能调整当前系统app查看详情界面
//                // 在此根据用户手机当前版本跳转系统设置界面
//                if (Build.VERSION.SDK_INT >= 9) {
//                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
//                    intent.setData(Uri.fromParts("package", context.getPackageName(), null));
//                } else if (Build.VERSION.SDK_INT <= 8) {
//                    intent.setAction(Intent.ACTION_VIEW);
//                    intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
//                    intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
//                }
//            }
//            intent.setComponent(componentName);
//            context.startActivity(intent);
//        } catch (Exception e) {//抛出异常就直接打开设置页面
//            intent = new Intent(Settings.ACTION_SETTINGS);
//            context.startActivity(intent);
//        }
    }
}
