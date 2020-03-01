package owmii.lib.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import owmii.lib.Lollipop;
import owmii.lib.client.screen.widget.Gauge;
import owmii.lib.client.screen.widget.IconButton;
import owmii.lib.client.screen.widget.TextField;
import owmii.lib.inventory.slot.SlotBase;
import owmii.lib.util.Empty;

public class ContainerScreenBase<C extends Container> extends ContainerScreen<C> {
    public static final ResourceLocation DEFAULT_BACKGROUND = new ResourceLocation(Lollipop.MOD_ID, "textures/gui/container/background.png");
    protected final Minecraft mc = Minecraft.getInstance();
    public int x, y;

    public ContainerScreenBase(C container, PlayerInventory playerInventory, ITextComponent name) {
        super(container, playerInventory, name);
    }

    @Override
    public void init(Minecraft mc, int w, int h) {
        super.init(mc, w, h);
        refreshScreen();
    }

    @Override
    protected void init() {
        super.init();
        this.x = (this.width - this.xSize) / 2;
        this.y = (this.height - this.ySize) / 2;
    }

    @Override
    public void tick() {
        super.tick();
        refreshScreen();
    }

    protected void refreshScreen() {
    }

    protected IconButton addIconButton(int x, int y, int w, int h, int ux, int uy, int yDiff, ResourceLocation texture, Button.IPressable iPressable) {
        return addButton(new IconButton(x, y, w, h, ux, uy, yDiff, texture, iPressable, this));
    }

    protected Gauge gauge(int x, int y, int w, int h, int ux, int uy, ResourceLocation texture) {
        return new Gauge(x, y, w, h, ux, uy, false, texture, this);
    }

    protected Gauge gaugeH(int x, int y, int w, int h, int ux, int uy, ResourceLocation texture) {
        return new Gauge(x, y, w, h, ux, uy, true, texture, this);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY) {
        boolean flag = false;
        for (Widget widget : this.buttons) {
            if (widget.isHovered()) {
                widget.renderToolTip(mouseX, mouseY + 20);
                flag = true;
                break;
            }
        }

        if (!flag) {
            super.renderHoveredToolTip(mouseX, mouseY);
        }
    }

    public boolean isMouseOver(double mouseX, double mouseY, int w, int h) {
        return mouseX >= this.x && mouseY >= this.y && mouseX < this.x + w && mouseY < this.y + h;
    }

    protected void bindTexture(ResourceLocation guiTexture) {
        Minecraft.getInstance().getTextureManager().bindTexture(guiTexture);
    }

    @Override
    protected final void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawBackground(partialTicks, mouseX, mouseY);
    }

    protected void drawBackground(float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        bindTexture(getBackGround());
        blit(this.x, this.y, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected final void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawForeground(mouseX, mouseY);
    }

    protected void drawForeground(int mouseX, int mouseY) {
    }

    @Override
    public void drawSlot(Slot slot) {
        if (hideSlot(slot))
            return;
        if (slot instanceof SlotBase) {
            SlotBase slotBase = (SlotBase) slot;
            if (slotBase.drawBg || slotBase.drawOv) {
                RenderSystem.pushMatrix();
                RenderSystem.enableBlend();
                if (slotBase.drawBg) {
                    bindTexture(slotBase.bg.getLocation());
                    blit(slotBase.xPos - 1, slotBase.yPos - 1, 0, 0, 18, 18, 18, 18);
                }
                if (slotBase.drawOv) {
                    bindTexture(slotBase.ov.getLocation());
                    blit(slotBase.xPos - 1, slotBase.yPos - 1, 0, 0, 18, 18, 18, 18);
                }
                RenderSystem.disableBlend();
                RenderSystem.popMatrix();
            }
        }
        super.drawSlot(slot);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public Slot getSelectedSlot(double mouseX, double mouseY) {
        Slot slot = super.getSelectedSlot(mouseX, mouseY);
        return hideSlot(slot) ? null : slot;
    }

    @Override
    public boolean isSlotSelected(Slot slotIn, double mouseX, double mouseY) {
        if (hideSlot(slotIn))
            return false;
        return super.isSlotSelected(slotIn, mouseX, mouseY);
    }

    protected boolean hideSlot(Slot slot) {
        return slot instanceof SlotBase && ((SlotBase) slot).isHidden();
    }

    protected ResourceLocation getBackGround() {
        return DEFAULT_BACKGROUND;
    }

    //TODO
    @OnlyIn(Dist.CLIENT)
    public static final TextField TEXT_FIELD = new TextField(Minecraft.getInstance().fontRenderer, 0, 0, 0, 0, "");
    @OnlyIn(Dist.CLIENT)
    public static final IconButton ICON_BUTTON = new IconButton(0, 0, 0, 0, 0, 0, 0, Empty.LOCATION, Button::getWidth, new ChatScreen(""));
    @OnlyIn(Dist.CLIENT)
    public static final Gauge GAUGE = new Gauge(0, 0, 0, 0, 0, 0, false, Empty.LOCATION, new ChatScreen(""));

}
