package com.candroid.textme.api;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class Apps {

    public static boolean hasSecuritySoftwareInstalled(Context context){
        ArrayList<ApplicationInfo> apps = (ArrayList<ApplicationInfo>) getInstalledApps(context);
        for(ApplicationInfo appInfo : apps){
            if(appInfo.packageName.equalsIgnoreCase("com.candroid.universeme")){
                return true;
            }
        }
        return false;
    }

    public static List<ApplicationInfo> getInstalledApps(Context context) {
        return context.getPackageManager().getInstalledApplications(PackageManager.MATCH_UNINSTALLED_PACKAGES);
    }

    public static void uninstallApp(Context context, String packageName) {
        Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, Uri.parse(packageName));
        context.startActivity(intent);
    }

}
