package owmii.lib.inventory.slot;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import owmii.lib.client.screen.ContainerScreenBase;

public class SlotBase extends SlotItemHandler {
    public int bgX;
    public int bgY;
    private boolean drawBg;
    public int ovX;
    public int ovY;
    private boolean drawOv;


    public SlotBase(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @OnlyIn(Dist.CLIENT)
    public void drawBG(ContainerScreenBase screen) {
        if (!this.drawBg && !this.drawOv) return;
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.drawBg) screen.blit(this.xPos - 1, this.yPos - 1, this.bgX, this.bgY, 18, 18);
        if (this.drawOv) screen.blit(this.xPos - 1, this.yPos - 1, this.ovX, this.ovY, 18, 18);
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    public SlotBase bg(int bgX, int bgY) {
        this.bgX = bgX;
        this.bgY = bgY;
        this.drawBg = true;
        return this;
    }

    public SlotBase ov(int ovX, int ovY) {
        this.ovX = ovX;
        this.ovY = ovY;
        this.drawOv = true;
        return this;
    }
}
