package owmii.lib.util;

import net.minecraft.world.World;

import javax.annotation.Nullable;

public class Debug {
    public static void printDelayed(@Nullable World world, Object o) {
        if (world != null && world.getGameTime() % 20 == 0) {
            System.out.println(o);
        }
    }
}
