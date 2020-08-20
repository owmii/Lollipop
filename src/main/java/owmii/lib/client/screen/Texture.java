package owmii.lib.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import owmii.lib.Lollipop;
import owmii.lib.logistics.Redstone;
import owmii.lib.logistics.TransferType;

import java.util.HashMap;
import java.util.Map;

public class Texture extends AbstractGui {
    public static final Texture EMPTY = register("empty", 0, 0, 0, 0);

    // Misc
    public static final Texture SLOT_HIGHLIGHT_BG = register("container/misc", 16, 16, 0, 0);

    // Side config
    public static final Map<TransferType, Texture> CONFIG = new HashMap<>();
    public static final Texture CONFIG_BTN_BG = register("container/button_ov", 23, 25, 0, 0);
    public static final Texture CONFIG_BTN = register("container/button_ov", 5, 5, 23, 16);
    public static final Texture CONFIG_BTN_ALL = register("container/button_ov", 5, 5, 28, 16);
    public static final Texture CONFIG_BTN_OUT = register("container/button_ov", 5, 5, 33, 16);
    public static final Texture CONFIG_BTN_IN = register("container/button_ov", 5, 5, 38, 16);
    public static final Texture CONFIG_BTN_OFF = register("container/button_ov", 5, 5, 43, 16);

    // Redstone mode
    public static final Map<Redstone, Texture> REDSTONE = new HashMap<>();
    public static final Texture REDSTONE_BTN_BG = register("container/button_ov", 15, 16, 23, 0);
    public static final Texture REDSTONE_BTN_IGNORE = register("container/button_ov", 9, 8, 38, 0);
    public static final Texture REDSTONE_BTN_OFF = register("container/button_ov", 9, 8, 47, 0);
    public static final Texture REDSTONE_BTN_ON = register("container/button_ov", 9, 8, 38, 8);

    private final ResourceLocation location;
    private final int width, height;
    private final int u, v;

    public Texture(ResourceLocation location, int width, int height, int u, int v) {
        this.location = location;
        this.width = width;
        this.height = height;
        this.u = u;
        this.v = v;
    }

    static Texture register(String path, int width, int height, int u, int v) {
        return new Texture(new ResourceLocation(Lollipop.MOD_ID, "textures/gui/" + path + ".png"), width, height, u, v);
    }

    public void drawScalableW(MatrixStack matrix, float size, int x, int y) {
        scaleW((int) (size * this.width)).draw(matrix, x, y);
    }

    public void drawScalableH(MatrixStack matrix, float size, int x, int y) {
        int i = (int) (size * this.height);
        scaleH(i).moveV(this.height - i).draw(matrix, x, y + this.height - i);
    }

    public void draw(MatrixStack matrix, int x, int y) {
        if (!isEmpty()) {
            bindTexture(getLocation());
            blit(matrix, x, y, getU(), getV(), getWidth(), getHeight());
        }
    }

    public void bindTexture(ResourceLocation guiTexture) {
        Minecraft.getInstance().getTextureManager().bindTexture(guiTexture);
    }

    public Texture scaleW(int width) {
        return scale(width, this.height);
    }

    public Texture scaleH(int height) {
        return scale(this.width, height);
    }

    public Texture scale(int width, int height) {
        return new Texture(this.location, width, height, this.u, this.v);
    }

    public Texture moveU(int u) {
        return move(u, 0);
    }

    public Texture moveV(int v) {
        return move(0, v);
    }

    public Texture move(int u, int v) {
        return new Texture(this.location, this.width, this.height, this.u + u, this.v + v);
    }

    public ResourceLocation getLocation() {
        return this.location;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getU(int i) {
        return this.u + i;
    }

    public int getV(int i) {
        return this.v + i;
    }

    public int getU() {
        return this.u;
    }

    public int getV() {
        return this.v;
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    public boolean isMouseOver(int x, int y, double mouseX, double mouseY) {
        return mouseX >= x && mouseY >= y && mouseX < x + this.width && mouseY < y + this.height;
    }

    static {
        CONFIG.put(TransferType.ALL, CONFIG_BTN_ALL);
        CONFIG.put(TransferType.EXTRACT, CONFIG_BTN_OUT);
        CONFIG.put(TransferType.RECEIVE, CONFIG_BTN_IN);
        CONFIG.put(TransferType.NONE, CONFIG_BTN_OFF);
        REDSTONE.put(Redstone.IGNORE, REDSTONE_BTN_IGNORE);
        REDSTONE.put(Redstone.ON, REDSTONE_BTN_ON);
        REDSTONE.put(Redstone.OFF, REDSTONE_BTN_OFF);
    }
}
