package com.lody.virtual.client.core;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.HashSet;
import java.util.Set;

/**
 * 权限判断
 */
public class PermissionManager {
    static PermissionManager sPermissionManager;

    public static PermissionManager getInstance() {
        if (sPermissionManager == null) {
            synchronized (PermissionManager.class) {
                if (sPermissionManager == null) {
                    sPermissionManager = new PermissionManager();
                }
            }
        }
        return sPermissionManager;
    }

    private Set<String> mHostRequestedPermission = new HashSet<String>(10);

    private void loadHostRequestedPermission() {
        try {
            mHostRequestedPermission.clear();
            PackageManager pm = VirtualCore.getCore().getContext().getPackageManager();
            PackageInfo pms = pm.getPackageInfo(VirtualCore.getCore().getContext().getPackageName(), PackageManager.GET_PERMISSIONS);
            if (pms != null && pms.requestedPermissions != null && pms.requestedPermissions.length > 0) {
                for (String requestedPermission : pms.requestedPermissions) {
                    mHostRequestedPermission.add(requestedPermission);
                }
            }
        } catch (Exception e) {
        }
    }

    public boolean checkPermission(String requestedPermission) {
        synchronized (this) {
            if (mHostRequestedPermission.size() == 0) {
                loadHostRequestedPermission();
            }
        }
        boolean b = false;
        try {
            PackageManager pm = VirtualCore.getCore().getContext().getPackageManager();
            b = pm.getPermissionInfo(requestedPermission, 0) != null;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return b;
    }
}
