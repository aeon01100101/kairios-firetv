package com.kairios.firetv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String a = intent != null ? intent.getAction() : null;
        if (a == null) return;
        if (Intent.ACTION_BOOT_COMPLETED.equals(a)
                || Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(a)
                || "android.intent.action.QUICKBOOT_POWERON".equals(a)) {
            Intent menu = new Intent(context, BootMenuActivity.class);
            menu.putExtra(BootMenuActivity.EXTRA_FROM_BOOT, true);
            menu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                context.startActivity(menu);
            } catch (Exception ignored) {
                // If Fire OS blocks the direct start, the HOME registration
                // still brings the menu up when the home screen is requested.
            }
        }
    }
}
