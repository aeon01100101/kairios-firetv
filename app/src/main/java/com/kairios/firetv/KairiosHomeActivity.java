package com.kairios.firetv;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * The Kairios side: a simple home screen with big tiles for every app
 * installed on the TV, plus ways back to Fire TV and the boot menu.
 */
public class KairiosHomeActivity extends Activity {

    private TextView clockView;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable tick = new Runnable() {
        @Override public void run() {
            updateClock();
            handler.postDelayed(this, 15000);
        }
    };

    private static final class AppEntry {
        String label;
        String pkg;
        Drawable icon;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildUi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(tick);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(tick);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, BootMenuActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    private void updateClock() {
        if (clockView == null) return;
        String s = new SimpleDateFormat("EEE, MMM d   •   h:mm a", Locale.US).format(new Date());
        clockView.setText(s);
    }

    private void buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(11, 16, 38));
        root.setPadding(dp(48), dp(28), dp(48), dp(28));

        // Header: name + clock
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);

        TextView title = new TextView(this);
        title.setText("KAIRIOS");
        title.setTextColor(Color.rgb(255, 200, 61));
        title.setTextSize(38);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        header.addView(title, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        clockView = new TextView(this);
        clockView.setTextColor(Color.rgb(185, 196, 232));
        clockView.setTextSize(22);
        clockView.setGravity(Gravity.RIGHT);
        header.addView(clockView, new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        updateClock();
        root.addView(header);

        // Action row
        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams aLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        aLp.topMargin = dp(18);
        aLp.bottomMargin = dp(18);

        actions.addView(makeActionButton("Fire TV Home", v -> {
            if (FireTvLauncher.launch(this)) finish();
        }), actionLp());
        actions.addView(makeActionButton("Boot Menu", v -> onBackPressed()), actionLp());
        actions.addView(makeActionButton("TV Settings", v -> {
            try {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            } catch (Exception e) {
                Toast.makeText(this, "Settings not available.", Toast.LENGTH_SHORT).show();
            }
        }), actionLp());
        root.addView(actions, aLp);

        // App grid
        GridView grid = new GridView(this);
        grid.setNumColumns(4);
        grid.setHorizontalSpacing(dp(18));
        grid.setVerticalSpacing(dp(18));
        grid.setColumnWidth(dp(230));
        grid.setSelector(R.drawable.tile_bg);
        grid.setFocusable(true);
        final List<AppEntry> apps = loadApps();
        grid.setAdapter(new BaseAdapter() {
            @Override public int getCount() { return apps.size(); }
            @Override public Object getItem(int pos) { return apps.get(pos); }
            @Override public long getItemId(int pos) { return pos; }
            @Override public View getView(int pos, View convert, ViewGroup parent) {
                AppEntry e = apps.get(pos);
                LinearLayout tile;
                ImageView iv;
                TextView tv;
                if (convert instanceof LinearLayout) {
                    tile = (LinearLayout) convert;
                    iv = (ImageView) tile.getChildAt(0);
                    tv = (TextView) tile.getChildAt(1);
                } else {
                    tile = new LinearLayout(KairiosHomeActivity.this);
                    tile.setOrientation(LinearLayout.VERTICAL);
                    tile.setGravity(Gravity.CENTER);
                    tile.setPadding(dp(12), dp(16), dp(12), dp(16));
                    iv = new ImageView(KairiosHomeActivity.this);
                    tv = new TextView(KairiosHomeActivity.this);
                    tv.setTextColor(Color.WHITE);
                    tv.setTextSize(18);
                    tv.setGravity(Gravity.CENTER);
                    tv.setMaxLines(2);
                    tile.addView(iv, new LinearLayout.LayoutParams(dp(84), dp(84)));
                    LinearLayout.LayoutParams tLp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    tLp.topMargin = dp(10);
                    tile.addView(tv, tLp);
                }
                iv.setImageDrawable(e.icon);
                tv.setText(e.label);
                return tile;
            }
        });
        grid.setOnItemClickListener((parent, view, pos, id) -> launchApp(apps.get(pos).pkg));
        root.addView(grid, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        if (apps.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No apps found yet.");
            empty.setTextColor(Color.rgb(185, 196, 232));
            empty.setTextSize(22);
            root.addView(empty);
        }

        setContentView(root);
        grid.requestFocus();
    }

    private LinearLayout.LayoutParams actionLp() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.rightMargin = dp(16);
        return lp;
    }

    private View makeActionButton(String label, View.OnClickListener click) {
        TextView b = new TextView(this);
        b.setText(label);
        b.setFocusable(true);
        b.setClickable(true);
        b.setGravity(Gravity.CENTER);
        b.setTextSize(21);
        b.setTextColor(Color.WHITE);
        b.setPadding(dp(22), dp(12), dp(22), dp(12));
        b.setBackgroundResource(R.drawable.tile_bg);
        b.setOnClickListener(click);
        return b;
    }

    private List<AppEntry> loadApps() {
        PackageManager pm = getPackageManager();
        Set<String> seen = new HashSet<>();
        List<AppEntry> out = new ArrayList<>();

        // TV (leanback) apps first, then regular launcher apps.
        String[][] queries = {
            {Intent.ACTION_MAIN, Intent.CATEGORY_LEANBACK_LAUNCHER},
            {Intent.ACTION_MAIN, Intent.CATEGORY_LAUNCHER},
        };
        for (String[] q : queries) {
            Intent query = new Intent(q[0]);
            query.addCategory(q[1]);
            List<ResolveInfo> found = pm.queryIntentActivities(query, 0);
            for (ResolveInfo ri : found) {
                String pkg = ri.activityInfo.packageName;
                if (pkg.equals(getPackageName())) continue;                 // not ourselves
                if (pkg.equals("com.amazon.tv.launcher")) continue;         // has its own button
                if (pkg.equals("com.amazon.firehomestarter")) continue;
                if (!seen.add(pkg)) continue;
                AppEntry e = new AppEntry();
                e.pkg = pkg;
                CharSequence label = ri.loadLabel(pm);
                e.label = label != null ? label.toString() : pkg;
                try {
                    e.icon = ri.loadIcon(pm);
                } catch (Exception ex) {
                    e.icon = pm.getDefaultActivityIcon();
                }
                out.add(e);
            }
        }
        Collections.sort(out, (a, b) -> a.label.compareToIgnoreCase(b.label));
        return out;
    }

    private void launchApp(String pkg) {
        PackageManager pm = getPackageManager();
        Intent i = pm.getLeanbackLaunchIntentForPackage(pkg);
        if (i == null) i = pm.getLaunchIntentForPackage(pkg);
        if (i == null) {
            Toast.makeText(this, "That app can't be opened from here.", Toast.LENGTH_SHORT).show();
            return;
        }
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(i);
        } catch (Exception e) {
            Toast.makeText(this, "That app couldn't be opened.", Toast.LENGTH_SHORT).show();
        }
    }
}
