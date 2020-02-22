package owmii.lib.inventory.slot;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotBase extends SlotItemHandler {
    public int bgX;
    public int bgY;
    public boolean drawBg;
    public int ovX;
    public int ovY;
    public boolean drawOv;
    public boolean hide;


    public SlotBase(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
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

    public boolean isHidden() {
        return this.hide;
    }

    public SlotBase hide() {
        this.hide = true;
        return this;
    }
}
