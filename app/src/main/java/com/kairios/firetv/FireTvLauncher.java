package com.kairios.firetv;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

/** Launches Amazon's stock Fire TV home screen. */
public final class FireTvLauncher {

    private FireTvLauncher() {}

    public static boolean launch(Activity activity) {
        PackageManager pm = activity.getPackageManager();

        // 1) Plain package launches, in order.
        for (String pkg : BootLogic.fireTvHomePackages()) {
            Intent i = pm.getLeanbackLaunchIntentForPackage(pkg);
            if (i == null) i = pm.getLaunchIntentForPackage(pkg);
            if (i != null) {
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    activity.startActivity(i);
                    return true;
                } catch (Exception ignored) { /* try the next candidate */ }
            }
        }

        // 2) Explicit home activities.
        for (String[] c : BootLogic.fireTvHomeComponents()) {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.setComponent(new ComponentName(c[0], c[1]));
            i.addCategory(Intent.CATEGORY_HOME);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                activity.startActivity(i);
                return true;
            } catch (Exception ignored) { /* try the next candidate */ }
        }

        Toast.makeText(activity,
                "Could not find the Fire TV home screen on this device.",
                Toast.LENGTH_LONG).show();
        return false;
    }
}
