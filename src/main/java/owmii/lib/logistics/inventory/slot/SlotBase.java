package owmii.lib.logistics.inventory.slot;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import owmii.lib.logistics.inventory.Inventory;

import javax.annotation.Nonnull;

public class SlotBase extends SlotItemHandler {
    protected final Inventory<?> inv;

    public SlotBase(Inventory<?> handler, int id, int x, int y) {
        super(handler, id, x, y);
        this.inv = handler;
    }

    @Override
    public boolean canTakeStack(PlayerEntity player) {
        return !this.getItemHandler().extractItemFromSlot(getSlotIndex(), 1, true).isEmpty();
    }

    @Nonnull
    @Override
    public ItemStack decrStackSize(int amount) {
        return this.getItemHandler().extractItemFromSlot(getSlotIndex(), amount, false);
    }

    @Override
    public Inventory<?> getItemHandler() {
        return this.inv;
    }
}
