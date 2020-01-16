package owmii.lib.util;

import net.minecraftforge.fml.ModLoadingContext;

public class FML {
    public static String activeID() {
        return ModLoadingContext.get().getActiveNamespace();
    }
}
