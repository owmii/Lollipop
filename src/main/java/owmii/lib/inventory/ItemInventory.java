package owmii.lib.inventory;

import net.minecraft.item.ItemStack;
import owmii.lib.util.Stack;

public class ItemInventory extends Inventory {
    private final ItemStack stack;

    public ItemInventory(int size, ItemStack stack) {
        super(size);
        this.stack = stack;
        deserializeNBT(Stack.getTagOrEmptyChild(stack, "InventoryStacks"));
    }

    @Override
    protected void onContentsChanged(int slot) {
        this.stack.getOrCreateTag().put("InventoryStacks", serializeNBT());
    }

    public ItemStack getStack() {
        return this.stack;
    }
}
