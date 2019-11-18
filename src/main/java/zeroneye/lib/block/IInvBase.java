package zeroneye.lib.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.extensions.IForgeTileEntity;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.IntStream;

public interface IInvBase extends ISidedInventory, IForgeTileEntity, INamedContainerProvider {
    default TileBase getTile() {
        return (TileBase) this;
    }

    @Override
    default int[] getSlotsForFace(Direction Direction) {
        return IntStream.range(0, getSizeInventory()).toArray();
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
    int getSizeInventory();

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
        if (itemStack.getCount() > maxStackSize(index)) {
            itemStack.setCount(maxStackSize(index));
        }
        markDirty();
        if (!oldStack.isItemEqual(itemStack)) {
            onInventoryChanged(index);
        }
    }

    default void onInventoryChanged(int index) {
    }

    default int maxStackSize(int index) {
        return getInventoryStackLimit();
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

    @Override
    default ITextComponent getDisplayName() {
        return getTile().getName();
    }

    @Nullable
    @Override
    default Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        if (getTile().getBlock() instanceof BlockBase) {
            return ((BlockBase) getTile().getBlock()).getContainer(i, playerInventory, this);
        }
        return null;
    }

    IInvBase EMPTY = new IInvBase() {
        @Override
        public int getSizeInventory() {
            return 0;
        }

        @Override
        public void markDirty() {
        }

        @Override
        public CompoundNBT getTileData() {
            return new CompoundNBT();
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return LazyOptional.empty();
        }

        @Nullable
        @Override
        public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
            return null;
        }

        @Override
        public ITextComponent getDisplayName() {
            return new StringTextComponent("");
        }
    };
}
