package owmii.lib.util;

import net.minecraft.util.ResourceLocation;
import owmii.lib.Lollipop;

public class Empty {
    public static final ResourceLocation LOCATION = new ResourceLocation(Lollipop.MOD_ID, "empty");
    public static final String STRING = "";

    public static boolean check(ResourceLocation location) {
        return LOCATION.equals(location);
    }
}
