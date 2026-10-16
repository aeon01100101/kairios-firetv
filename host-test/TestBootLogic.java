import com.kairios.firetv.BootLogic;

/** Host-side tests for the boot routing rules. Plain JVM, no Android. */
public class TestBootLogic {
    static int passed = 0, failed = 0;

    static void check(String name, int actual, int expected) {
        if (actual == expected) { passed++; System.out.println("PASS " + name); }
        else { failed++; System.out.println("FAIL " + name + " expected=" + expected + " actual=" + actual); }
    }

    static void checkTrue(String name, boolean cond) {
        if (cond) { passed++; System.out.println("PASS " + name); }
        else { failed++; System.out.println("FAIL " + name); }
    }

    public static void main(String[] args) {
        // Manual open always shows the menu, whatever the settings say.
        check("manual open, menu-on",  BootLogic.decideRoute(false, true,  BootLogic.DEST_KAIRIOS), BootLogic.ROUTE_MENU);
        check("manual open, menu-off", BootLogic.decideRoute(false, false, BootLogic.DEST_FIRETV),  BootLogic.ROUTE_MENU);
        check("manual open, no dest",  BootLogic.decideRoute(false, false, BootLogic.DEST_NONE),    BootLogic.ROUTE_MENU);

        // Boot with "show every time" on always shows the menu.
        check("boot, menu-on, kairios", BootLogic.decideRoute(true, true, BootLogic.DEST_KAIRIOS), BootLogic.ROUTE_MENU);
        check("boot, menu-on, firetv",  BootLogic.decideRoute(true, true, BootLogic.DEST_FIRETV),  BootLogic.ROUTE_MENU);
        check("boot, menu-on, none",    BootLogic.decideRoute(true, true, BootLogic.DEST_NONE),    BootLogic.ROUTE_MENU);

        // Boot with menu off goes straight to the remembered side.
        check("boot, menu-off, kairios", BootLogic.decideRoute(true, false, BootLogic.DEST_KAIRIOS), BootLogic.ROUTE_KAIRIOS);
        check("boot, menu-off, firetv",  BootLogic.decideRoute(true, false, BootLogic.DEST_FIRETV),  BootLogic.ROUTE_FIRETV);

        // Boot with menu off but nothing ever picked: menu (never strand the user).
        check("boot, menu-off, none", BootLogic.decideRoute(true, false, BootLogic.DEST_NONE), BootLogic.ROUTE_MENU);

        // Fire TV home candidates are sane and ordered.
        String[] pkgs = BootLogic.fireTvHomePackages();
        checkTrue("firetv pkgs non-empty", pkgs.length >= 1);
        checkTrue("amazon launcher first", pkgs[0].equals("com.amazon.tv.launcher"));
        String[][] comps = BootLogic.fireTvHomeComponents();
        checkTrue("firetv comps non-empty", comps.length >= 1);
        checkTrue("comp pkg matches", comps[0][0].equals("com.amazon.tv.launcher"));

        System.out.println("----");
        System.out.println(passed + " passed, " + failed + " failed");
        if (failed > 0) System.exit(1);
    }
}
