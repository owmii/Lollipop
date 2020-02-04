package owmii.lib.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public class IconButton extends Button {
    private final List<String> tooltip = new ArrayList<>();
    private final ResourceLocation texture;
    private final Screen screen;
    private int xTexStart;
    private int yTexStart;
    private int yDiffText;
    private final int uvw;
    private final int uvh;

    public IconButton(int x, int y, int w, int h, int xTexStart, int yTexStart, int yDiffText, ResourceLocation icon, IPressable iPressable, Screen screen) {
        this(x, y, w, h, xTexStart, yTexStart, yDiffText, icon, 256, 256, iPressable, screen);
    }

    public IconButton(int x, int y, int w, int h, int xTexStart, int yTexStart, int yDiffText, ResourceLocation location, int uvw, int uvh, IPressable pressable, Screen screen) {
        this(x, y, w, h, xTexStart, yTexStart, yDiffText, location, uvw, uvh, pressable, "", screen);
    }

    public IconButton(int x, int y, int w, int h, int xTexStart, int yTexStart, int yDiffText, ResourceLocation texture, int uvw, int uvh, IPressable p_i51136_11_, String p_i51136_12_, Screen screen) {
        super(x, y, w, h, p_i51136_12_, p_i51136_11_);
        this.uvw = uvw;
        this.uvh = uvh;
        this.xTexStart = xTexStart;
        this.yTexStart = yTexStart;
        this.yDiffText = yDiffText;
        this.texture = texture;
        this.screen = screen;
    }

    @Override
    public void renderToolTip(int x, int y) {
        super.renderToolTip(x, y);
        this.screen.renderTooltip(this.tooltip, x, y);
    }

    public IconButton tooltip(String s, Object... args) {
        return tooltip(s, TextFormatting.RESET, args);
    }

    public IconButton tooltip(String s, TextFormatting formatting, Object... args) {
        this.tooltip.add(formatting + I18n.format(s, args));
        return this;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setIconDiff(int xTexStart) {
        this.xTexStart = xTexStart;
    }

    public List<String> getTooltip() {
        return this.tooltip;
    }

    public void renderButton(int x, int y, float p_renderButton_3_) {
        Minecraft mc = Minecraft.getInstance();
        mc.getTextureManager().bindTexture(this.texture);
        RenderSystem.disableDepthTest();
        int i = this.yTexStart;
        if (this.isHovered()) {
            i += this.yDiffText;
        }
        blit(this.x, this.y, (float) this.xTexStart, (float) i, this.width, this.height, this.uvw, this.uvh);
        RenderSystem.enableDepthTest();
    }
}
