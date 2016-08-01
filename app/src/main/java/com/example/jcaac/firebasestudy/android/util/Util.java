package com.example.jcaac.firebasestudy.android.util;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.support.v7.app.NotificationCompat;

import com.example.jcaac.firebasestudy.R;

import java.util.List;

public final class Util {

    public static String getProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processList = manager.getRunningAppProcesses();
        if (processList != null) {
            for (ActivityManager.RunningAppProcessInfo processInfo : processList) {
                if (processInfo.pid == pid) {
                    return processInfo.processName;
                }
            }
        }
        return null;
    }

    public static boolean isServiceRunning(Context context, Class<? extends Service> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static Notification createNotification(Context context, int titleRes, int contentRes) {
        return createNotification(context, titleRes, contentRes, false, true);
    }

    public static Notification createNotification(Context context, int titleRes, int contentRes, boolean autoCancel, boolean onGoing) {
        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(titleRes))
                .setContentText(context.getString(contentRes))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(autoCancel)
                .setOngoing(onGoing)
                .build();

        return notification;
    }
}
