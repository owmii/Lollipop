package zeroneye.lib.client.util;

import com.mojang.blaze3d.platform.GlStateManager;

public class GL {
    public static void color(int color) {
        float r = (float) (color >> 16) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        GlStateManager.color3f(r, b, g);
    }
}
