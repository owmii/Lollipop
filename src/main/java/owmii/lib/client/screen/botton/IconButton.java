package owmii.lib.client.screen.botton;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public class IconButton extends Button {
    private final List<String> tooltip = new ArrayList<>();
    private final Screen screen;
    private final ResourceLocation resourceLocation;
    private int xTexStart;
    private int yTexStart;
    private int yDiffText;
    private final int field_212935_e;
    private final int field_212936_f;

    public IconButton(int x, int y, int w, int h, int xTexStart, int yTexStart, int yDiffText, ResourceLocation icon, IPressable iPressable, Screen screen) {
        this(x, y, w, h, xTexStart, yTexStart, yDiffText, icon, 256, 256, iPressable, screen);
    }

    public IconButton(int p_i51135_1_, int p_i51135_2_, int p_i51135_3_, int p_i51135_4_, int p_i51135_5_, int p_i51135_6_, int p_i51135_7_, ResourceLocation p_i51135_8_, int p_i51135_9_, int p_i51135_10_, IPressable p_i51135_11_, Screen screen) {
        this(p_i51135_1_, p_i51135_2_, p_i51135_3_, p_i51135_4_, p_i51135_5_, p_i51135_6_, p_i51135_7_, p_i51135_8_, p_i51135_9_, p_i51135_10_, p_i51135_11_, "", screen);
    }

    public IconButton(int p_i51136_1_, int p_i51136_2_, int p_i51136_3_, int p_i51136_4_, int p_i51136_5_, int p_i51136_6_, int p_i51136_7_, ResourceLocation p_i51136_8_, int p_i51136_9_, int p_i51136_10_, IPressable p_i51136_11_, String p_i51136_12_, Screen screen) {
        super(p_i51136_1_, p_i51136_2_, p_i51136_3_, p_i51136_4_, p_i51136_12_, p_i51136_11_);
        this.field_212935_e = p_i51136_9_;
        this.field_212936_f = p_i51136_10_;
        this.xTexStart = p_i51136_5_;
        this.yTexStart = p_i51136_6_;
        this.yDiffText = p_i51136_7_;
        this.resourceLocation = p_i51136_8_;
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

    public void setPosition(int p_191746_1_, int p_191746_2_) {
        this.x = p_191746_1_;
        this.y = p_191746_2_;
    }

    public void setIconDiff(int xTexStart) {
        this.xTexStart = xTexStart;
    }

    public List<String> getTooltip() {
        return this.tooltip;
    }

    public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        Minecraft lvt_4_1_ = Minecraft.getInstance();
        lvt_4_1_.getTextureManager().bindTexture(this.resourceLocation);
        RenderSystem.disableDepthTest();
        int lvt_5_1_ = this.yTexStart;
        if (this.isHovered()) {
            lvt_5_1_ += this.yDiffText;
        }

        blit(this.x, this.y, (float) this.xTexStart, (float) lvt_5_1_, this.width, this.height, this.field_212935_e, this.field_212936_f);
        RenderSystem.enableDepthTest();
    }

    public static final IconButton EMPTY = new IconButton(0, 0, 0, 0, 0, 0, 0, new ResourceLocation(""), button -> {
    }, new ChatScreen(""));
}
