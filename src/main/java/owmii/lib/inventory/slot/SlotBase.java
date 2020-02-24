package owmii.lib.inventory.slot;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotBase extends SlotItemHandler {
    public SlotOverlay bg = SlotOverlay.SLOT;
    public SlotOverlay ov = SlotOverlay.FILTER;
    public boolean drawBg;
    public boolean drawOv;
    public boolean hide;


    public SlotBase(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    public SlotBase bg(SlotOverlay bg) {
        this.bg = bg;
        this.drawBg = true;
        return this;
    }

    public SlotBase ov(SlotOverlay ov) {
        this.ov = ov;
        this.drawOv = true;
        return this;
    }

    public boolean isHidden() {
        return this.hide;
    }

    public SlotBase hide() {
        this.hide = true;
        return this;
    }
}
