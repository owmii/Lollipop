package owmii.lib.util;

public class Debug {
    public static void printDelayed(Object o) {
        if (Server.ticks % 20 == 0) {
            System.out.println(o);
        }
    }
}
