package xieao.lib.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.extensions.IForgeTileEntity;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

public interface IInvBase extends ISidedInventory, IForgeTileEntity {
    default TileBase getTile() {
        return (TileBase) this;
    }

    @Override
    default int[] getSlotsForFace(Direction Direction) {
        return IntStream.range(0, getSizeInventory() - 1).toArray();
    }

    @Override
    default boolean canInsertItem(int index, ItemStack itemStack, @Nullable Direction Direction) {
        return isItemValidForSlot(index, itemStack);
    }

    @Override
    default boolean canExtractItem(int index, ItemStack itemStack, Direction Direction) {
        return true;
    }

    @Override
    default int getSizeInventory() {
        return getTile().stacks.size();
    }

    default void setInvSize(int size) {
        getTile().stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    @Override
    default boolean isEmpty() {
        return getTile().stacks.isEmpty();
    }

    @Override
    default ItemStack getStackInSlot(int index) {
        return getTile().stacks.get(index);
    }

    @Override
    default ItemStack decrStackSize(int index, int count) {
        ItemStack itemstack = ItemStackHelper.getAndSplit(getTile().stacks, index, count);
        if (!itemstack.isEmpty()) {
            markDirty();
        }
        return itemstack;
    }

    @Override
    default ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(getTile().stacks, index);
    }

    @Override
    default void setInventorySlotContents(int index, ItemStack itemStack) {
        ItemStack oldStack = getStackInSlot(index);
        getTile().stacks.set(index, itemStack);
        if (itemStack.getCount() > this.getInventoryStackLimit()) {
            itemStack.setCount(this.getInventoryStackLimit());
        }
        markDirty();
        if (!oldStack.isItemEqual(itemStack)) {
            onInventoryChanged(index);
        }
    }

    default void onInventoryChanged(int index) {
    }

    @Override
    default int getInventoryStackLimit() {
        return 64;
    }

    @Override
    default boolean isUsableByPlayer(PlayerEntity PlayerEntity) {
        return true;
    }

    @Override
    default void openInventory(PlayerEntity PlayerEntity) {
    }

    @Override
    default void closeInventory(PlayerEntity PlayerEntity) {
    }

    @Override
    default boolean isItemValidForSlot(int index, ItemStack itemStack) {
        return true;
    }

    @Override
    default void clear() {
        for (int i = 0; i < getSizeInventory(); ++i) {
            ItemStack stack = getStackInSlot(i);
            if (!stack.isEmpty()) {
                setInventorySlotContents(i, ItemStack.EMPTY);
            }
        }
    }
}
