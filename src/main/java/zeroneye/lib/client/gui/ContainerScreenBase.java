package zeroneye.lib.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import zeroneye.lib.inventory.ContainerBase;

import javax.annotation.Nullable;

public class ContainerScreenBase<T extends ContainerBase> extends ContainerScreen<T> {
    public int x, y;

    public ContainerScreenBase(T container, PlayerInventory playerInventory, ITextComponent name) {
        super(container, playerInventory, name);
    }

    @Override
    protected void init() {
        super.init();
        this.x = (this.width - this.xSize) / 2;
        this.y = (this.height - this.ySize) / 2;
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
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (getBackGroundImage() != null) {
            bindTexture(getBackGroundImage());
            this.blit(this.x, this.y, 0, 0, this.xSize, this.ySize);
        }
    }

    @Override
    protected final void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawForeground(mouseX, mouseY);
    }

    protected void drawForeground(int mouseX, int mouseY) {
    }

    @Override
    public void drawSlot(Slot slotIn) {
        if (hideSlot(slotIn))
            return;
        super.drawSlot(slotIn);
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
        return false;
    }

    @Nullable
    protected ResourceLocation getBackGroundImage() {
        return null;
    }
}
