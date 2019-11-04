package xieao.lib.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import xieao.lib.block.IInvBase;

public class Inventory {
    public static CompoundNBT writeItems(CompoundNBT tag, IInventory inv) {
        return writeItems(tag, inv, true);
    }

    public static CompoundNBT writeItems(CompoundNBT tag, IInventory inv, boolean saveEmpty) {
        ListNBT nbttaglist = new ListNBT();
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
                CompoundNBT nbttagcompound = new CompoundNBT();
                nbttagcompound.putByte("Slot", (byte) i);
                itemstack.write(nbttagcompound);
                nbttaglist.add(nbttagcompound);
            }
        }
        if (!nbttaglist.isEmpty() || saveEmpty) {
            tag.put("Items", nbttaglist);
        }
        return tag;
    }

    public static NonNullList<ItemStack> readItems(CompoundNBT tag, IInvBase inv) {
        NonNullList<ItemStack> stacks = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        ListNBT nbttaglist = tag.getList("Items", 10);
        for (int i = 0; i < nbttaglist.size(); ++i) {
            CompoundNBT nbttagcompound = nbttaglist.getCompound(i);
            ItemStack stack = ItemStack.read(nbttagcompound);
            int j = nbttagcompound.getByte("Slot") & 255;
            if (j < inv.getSizeInventory()) {
                stacks.set(j, stack);
                if (stack.getCount() > inv.getInventoryStackLimit()) {
                    stack.setCount(inv.getInventoryStackLimit());
                }
            }
        }
        return stacks;
    }
}
