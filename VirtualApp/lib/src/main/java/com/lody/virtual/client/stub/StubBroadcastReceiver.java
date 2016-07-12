package com.lody.virtual.client.stub;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class StubBroadcastReceiver extends BroadcastReceiver {
    BroadcastReceiver parent;
    String appPackageName;

    public StubBroadcastReceiver(String appPackageName, BroadcastReceiver parent) {
        this.parent = parent;
        this.appPackageName = appPackageName;
    }

    private Context getAppContext(Context context) {
        try {
            return context.createPackageContext(appPackageName,
                    Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
        }
        return context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (parent != null) {
            //需要把context替换成app
            parent.onReceive(getAppContext(context), intent);
        }
    }
}
