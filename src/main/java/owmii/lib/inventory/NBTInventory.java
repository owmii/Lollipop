package owmii.lib.inventory;

import net.minecraft.nbt.CompoundNBT;

public class NBTInventory extends Inventory {
    private final CompoundNBT nbt;

    public NBTInventory(int size, CompoundNBT nbt) {
        super(size);
        this.nbt = nbt;
        deserializeNBT(nbt.getCompound(("InventoryStacks")));
    }

    @Override
    public void onContentsChanged(int slot) {
        this.nbt.put("InventoryStacks", serializeNBT());
    }

    public CompoundNBT getNbt() {
        return this.nbt;
    }
}
