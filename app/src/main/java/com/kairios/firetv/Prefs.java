package com.kairios.firetv;

import android.content.Context;
import android.content.SharedPreferences;

public final class Prefs {
    private static final String FILE = "kairios_prefs";
    private static final String KEY_SHOW_MENU = "show_menu_every_boot";
    private static final String KEY_LAST_DEST = "last_dest";

    private final SharedPreferences sp;

    public Prefs(Context ctx) {
        sp = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    public boolean showMenuEveryBoot() {
        return sp.getBoolean(KEY_SHOW_MENU, true);
    }

    public void setShowMenuEveryBoot(boolean v) {
        sp.edit().putBoolean(KEY_SHOW_MENU, v).apply();
    }

    public int lastDest() {
        return sp.getInt(KEY_LAST_DEST, BootLogic.DEST_NONE);
    }

    public void setLastDest(int dest) {
        sp.edit().putInt(KEY_LAST_DEST, dest).apply();
    }
}
