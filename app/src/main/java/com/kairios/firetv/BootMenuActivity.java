package com.kairios.firetv;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The boot chooser: the first screen after the TV starts.
 * Two big cards — Kairios or Fire TV — picked with the remote.
 */
public class BootMenuActivity extends Activity {

    public static final String EXTRA_FROM_BOOT = "from_boot";

    private Prefs prefs;
    private TextView toggleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new Prefs(this);

        boolean fromBoot = isFromBoot(getIntent());
        int route = BootLogic.decideRoute(fromBoot, prefs.showMenuEveryBoot(), prefs.lastDest());
        if (route == BootLogic.ROUTE_KAIRIOS) {
            goKairios();
            return;
        }
        if (route == BootLogic.ROUTE_FIRETV && FireTvLauncher.launch(this)) {
            finish();
            return;
        }
        buildUi();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        boolean fromBoot = isFromBoot(intent);
        int route = BootLogic.decideRoute(fromBoot, prefs.showMenuEveryBoot(), prefs.lastDest());
        if (route == BootLogic.ROUTE_KAIRIOS) {
            goKairios();
        } else if (route == BootLogic.ROUTE_FIRETV && FireTvLauncher.launch(this)) {
            finish();
        }
    }

    /** Boot receiver launches carry the extra; being called as the HOME app also counts. */
    private boolean isFromBoot(Intent intent) {
        if (intent == null) return false;
        if (intent.getBooleanExtra(EXTRA_FROM_BOOT, false)) return true;
        return intent.hasCategory(Intent.CATEGORY_HOME);
    }

    private void choose(int dest) {
        prefs.setLastDest(dest);
        if (dest == BootLogic.DEST_KAIRIOS) {
            goKairios();
        } else if (FireTvLauncher.launch(this)) {
            finish();
        }
    }

    private void goKairios() {
        Intent i = new Intent(this, KairiosHomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    // ---------------- UI ----------------

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    private void buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setBackgroundColor(Color.rgb(11, 16, 38));
        root.setPadding(dp(48), dp(32), dp(48), dp(32));

        TextView title = new TextView(this);
        title.setText("KAIRIOS");
        title.setTextColor(Color.WHITE);
        title.setTextSize(64);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView sub = new TextView(this);
        sub.setText("How do you want to start?");
        sub.setTextColor(Color.rgb(185, 196, 232));
        sub.setTextSize(26);
        sub.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        subLp.bottomMargin = dp(36);
        root.addView(sub, subLp);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER);

        View kairiosCard = makeCard("KAIRIOS", "Your apps, simple and fast");
        kairiosCard.setOnClickListener(v -> choose(BootLogic.DEST_KAIRIOS));
        LinearLayout.LayoutParams kLp = new LinearLayout.LayoutParams(dp(420), dp(240));
        kLp.rightMargin = dp(20);
        row.addView(kairiosCard, kLp);

        View fireCard = makeCard("FIRE TV", "Amazon's normal home screen");
        fireCard.setOnClickListener(v -> choose(BootLogic.DEST_FIRETV));
        LinearLayout.LayoutParams fLp = new LinearLayout.LayoutParams(dp(420), dp(240));
        fLp.leftMargin = dp(20);
        row.addView(fireCard, fLp);

        root.addView(row);

        toggleView = new TextView(this);
        toggleView.setFocusable(true);
        toggleView.setClickable(true);
        toggleView.setGravity(Gravity.CENTER);
        toggleView.setTextSize(22);
        toggleView.setTextColor(Color.WHITE);
        toggleView.setPadding(dp(20), dp(12), dp(20), dp(12));
        toggleView.setBackgroundResource(R.drawable.tile_bg);
        refreshToggleText();
        toggleView.setOnClickListener(v -> {
            prefs.setShowMenuEveryBoot(!prefs.showMenuEveryBoot());
            refreshToggleText();
            Toast.makeText(this,
                    prefs.showMenuEveryBoot()
                            ? "The menu will show every time the TV starts."
                            : "Next start goes straight to the side you pick.",
                    Toast.LENGTH_SHORT).show();
        });
        LinearLayout.LayoutParams tLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tLp.topMargin = dp(36);
        root.addView(toggleView, tLp);

        TextView homeBtn = new TextView(this);
        homeBtn.setText("Set Kairios as your Home button app");
        homeBtn.setFocusable(true);
        homeBtn.setClickable(true);
        homeBtn.setGravity(Gravity.CENTER);
        homeBtn.setTextSize(20);
        homeBtn.setTextColor(Color.rgb(185, 196, 232));
        homeBtn.setPadding(dp(20), dp(10), dp(20), dp(10));
        homeBtn.setBackgroundResource(R.drawable.tile_bg);
        homeBtn.setOnClickListener(v -> promptSetHomeApp());
        LinearLayout.LayoutParams hLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        hLp.topMargin = dp(16);
        root.addView(homeBtn, hLp);

        TextView version = new TextView(this);
        version.setText("Kairios for Fire TV  •  Special Edition  •  v1.0.0");
        version.setTextColor(Color.rgb(110, 122, 165));
        version.setTextSize(16);
        version.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams vLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        vLp.topMargin = dp(24);
        root.addView(version, vLp);

        setContentView(root);
        kairiosCard.requestFocus();
    }

    private void refreshToggleText() {
        if (toggleView == null) return;
        toggleView.setText(prefs.showMenuEveryBoot()
                ? "Show this menu every time the TV starts:  ON"
                : "Show this menu every time the TV starts:  OFF");
    }

    private View makeCard(String big, String small) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setFocusable(true);
        card.setClickable(true);
        card.setBackgroundResource(R.drawable.card_bg);
        card.setPadding(dp(24), dp(24), dp(24), dp(24));
        card.setOnFocusChangeListener((v, hasFocus) -> {
            v.setScaleX(hasFocus ? 1.04f : 1.0f);
            v.setScaleY(hasFocus ? 1.04f : 1.0f);
        });

        TextView t = new TextView(this);
        t.setText(big);
        t.setTextColor(Color.WHITE);
        t.setTextSize(52);
        t.setTypeface(Typeface.DEFAULT_BOLD);
        t.setGravity(Gravity.CENTER);
        card.addView(t);

        TextView s = new TextView(this);
        s.setText(small);
        s.setTextColor(Color.rgb(185, 196, 232));
        s.setTextSize(22);
        s.setGravity(Gravity.CENTER);
        card.addView(s);
        return card;
    }

    private void promptSetHomeApp() {
        // Stock Android has a "Home app" settings screen; Fire OS may hide it,
        // so fall back to firing the HOME intent, which asks the system to
        // resolve a home app.
        try {
            startActivity(new Intent(Settings.ACTION_HOME_SETTINGS));
            return;
        } catch (Exception ignored) { /* fall through */ }
        try {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.addCategory(Intent.CATEGORY_HOME);
            home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(home);
        } catch (Exception e) {
            Toast.makeText(this,
                    "Amazon blocks changing the Home app on this Fire TV version. " +
                    "Kairios will still pop up right after the TV starts.",
                    Toast.LENGTH_LONG).show();
        }
    }
}
