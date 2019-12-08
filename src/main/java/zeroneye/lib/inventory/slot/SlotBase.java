package zeroneye.lib.inventory.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import zeroneye.lib.block.TileBase;

public class SlotBase extends Slot {
    public SlotBase(IInventory inventory, int id, int x, int y) {
        super(inventory, id, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return this.inventory.isItemValidForSlot(getSlotIndex(), stack);
    }

    @Override
    public int getSlotStackLimit() {
        return this.inventory instanceof TileBase.TickableInv
                ? ((TileBase.TickableInv) this.inventory).maxStackSize(getSlotIndex())
                : super.getSlotStackLimit();
    }
}
