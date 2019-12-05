package zeroneye.lib.client.util;

import com.mojang.blaze3d.platform.GlStateManager;

public class GL {
    public static void color(int color) {
        float r = (color >> 16 & 0xFF) / 255.0F;
        float g = (color >> 8 & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        GlStateManager.color3f(r, g, b);
    }
}
