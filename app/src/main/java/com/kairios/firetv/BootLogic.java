package com.kairios.firetv;

/**
 * Pure routing logic for the boot chooser — no Android dependencies,
 * so it can be host-tested on a plain JVM (see host-test/).
 */
public final class BootLogic {

    public static final int DEST_NONE = 0;
    public static final int DEST_KAIRIOS = 1;
    public static final int DEST_FIRETV = 2;

    public static final int ROUTE_MENU = 0;
    public static final int ROUTE_KAIRIOS = 1;
    public static final int ROUTE_FIRETV = 2;

    private BootLogic() {}

    /**
     * Decide what to show when the boot menu activity starts.
     *
     * @param fromBoot          true when launched by the boot receiver or as the HOME app
     *                          right after startup; false when the user opened the app by hand
     * @param showMenuEveryBoot the user's "show this menu every time" setting
     * @param lastDest          the side the user picked last time (DEST_*), or DEST_NONE
     * @return one of ROUTE_*
     */
    public static int decideRoute(boolean fromBoot, boolean showMenuEveryBoot, int lastDest) {
        if (!fromBoot) {
            // Opened by hand: the user explicitly wants the menu.
            return ROUTE_MENU;
        }
        if (showMenuEveryBoot) {
            return ROUTE_MENU;
        }
        if (lastDest == DEST_KAIRIOS) {
            return ROUTE_KAIRIOS;
        }
        if (lastDest == DEST_FIRETV) {
            return ROUTE_FIRETV;
        }
        // Nothing remembered yet: always show the menu.
        return ROUTE_MENU;
    }

    /**
     * Known Fire TV home packages, most-specific first. Amazon has shipped
     * the home screen under these packages across Fire OS generations.
     */
    public static String[] fireTvHomePackages() {
        return new String[] {
            "com.amazon.tv.launcher",
            "com.amazon.firehomestarter",
        };
    }

    /** Explicit home activities to try if a plain package launch fails. */
    public static String[][] fireTvHomeComponents() {
        return new String[][] {
            {"com.amazon.tv.launcher", "com.amazon.tv.launcher.ui.HomeActivity"},
            {"com.amazon.tv.launcher", "com.amazon.tv.launcher.ui.HomeActivity_vNext"},
        };
    }
}
