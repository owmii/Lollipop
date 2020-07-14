package owmii.lib.block;

import net.minecraft.item.ItemStack;
import owmii.lib.logistics.inventory.Inventory;

public interface IInventoryHolder {
    int getSlotLimit(int slot);

    boolean canInsert(int slot, ItemStack stack);

    boolean canExtract(int slot, ItemStack stack);

    default void onSlotChanged(int slot) {}

    Inventory getInventory();
}
