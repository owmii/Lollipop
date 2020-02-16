package owmii.lib.util;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class FML {
    public static String getActiveID() {
        return ModLoadingContext.get().getActiveNamespace();
    }

    public static boolean isServer() {
        return !isClient();
    }

    public static boolean isClient() {
        return FMLEnvironment.dist.isClient();
    }
}
