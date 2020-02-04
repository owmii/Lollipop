package owmii.lib.client.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import owmii.lib.client.util.Draw2D;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class Gauge {
    private final List<String> tooltip = new ArrayList<>();
    public int x, y, w, h, u, v;
    public int bx, by, bw, bh, bu, bv;
    private boolean drawBg;
    private boolean horizontal;
    private Screen screen;
    private ResourceLocation texture;
    protected boolean isHovered;
    public boolean visible;

    public Gauge(int x, int y, int w, int h, int u, int v, boolean horizontal, ResourceLocation texture, Screen screen) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.u = u;
        this.v = v;
        this.horizontal = horizontal;
        this.screen = screen;
        this.texture = texture;
    }

    public Gauge setBG(int x, int y, int w, int h, int u, int v) {
        this.bx = x;
        this.by = y;
        this.bw = w;
        this.bh = h;
        this.bu = u;
        this.bv = v;
        this.drawBg = true;
        return this;
    }

    public void render(long cap, long curr, int mouseX, int mouseY) {
        if (!this.visible) return;
        this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.w && mouseY < this.y + this.h;
        Minecraft.getInstance().getTextureManager().bindTexture(this.texture);
        if (this.drawBg) {
            this.screen.blit(this.bx, this.by, this.bu, this.bv, this.bw, this.bh);
        }
        if (curr > 0 && cap >= curr) {
            Draw2D.gaugeV(this.x, this.y, this.w, this.h, this.u, this.v, cap, curr);
        }
    }

    public boolean renderToolTip(int mouseX, int mouseY) {
        if (!this.visible) return false;
        if (this.isHovered) {
            this.screen.renderTooltip(this.tooltip, mouseX, mouseY);
            return true;
        }
        return false;
    }

    public Gauge clearToolTip() {
        getTooltip().clear();
        return this;
    }

    public Gauge tooltip(String s, Object... args) {
        return tooltip(s, TextFormatting.RESET, args);
    }

    public Gauge tooltip(String s, TextFormatting formatting, Object... args) {
        this.tooltip.add(formatting + I18n.format(s, args));
        return this;
    }

    public boolean isHovered() {
        return this.isHovered;
    }

    public List<String> getTooltip() {
        return this.tooltip;
    }
}
