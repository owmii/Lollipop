package owmii.lib.client.util;

import com.mojang.blaze3d.systems.RenderSystem;

public class GL {
    public static void color(int color) {
        float r = (color >> 16 & 0xFF) / 255.0F;
        float g = (color >> 8 & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        RenderSystem.color3f(r, g, b);
    }
}
