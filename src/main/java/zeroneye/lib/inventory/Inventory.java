package zeroneye.lib.inventory;

import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import zeroneye.lib.block.TileBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Inventory extends ItemStackHandler {
    @Nullable
    private final TileBase tile;

    Inventory(int size, @Nullable TileBase tile) {
        super(size);
        this.tile = tile;
    }

    public static Inventory create(int size, @Nullable TileBase tile) {
        return new Inventory(size, tile);
    }

    public static Inventory createBlank(@Nullable TileBase tile) {
        return new Inventory(0, tile);
    }

    public static Inventory create(int size) {
        return new Inventory(size, null);
    }

    public static Inventory createBlank() {
        return new Inventory(0, null);
    }

    public void deserializeNBT(CompoundNBT nbt) {
        if (isBlank()) return;
        nbt.putInt("Size", this.getSlots()); // TODO remove : 12/12/2019
        super.deserializeNBT(nbt);
    }

    @Override
    public CompoundNBT serializeNBT() {
        return isBlank() ? new CompoundNBT() : super.serializeNBT();
    }

    public Inventory set(int size) {
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        return this;
    }

    public Inventory add(int size) {
        this.stacks = NonNullList.withSize(size + this.stacks.size(), ItemStack.EMPTY);
        return this;
    }

    @Override
    public int getSlotLimit(int slot) {
        if (this.tile != null) {
            return this.tile.getSlotLimit(slot);
        }
        return super.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (this.tile != null) {
            return this.tile.canInsert(slot, stack);
        }
        return super.isItemValid(slot, stack);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return canExtract(slot, getStackInSlot(slot)) ? super.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
    }

    public boolean canExtract(int slot, ItemStack stack) {
        if (this.tile != null) {
            return this.tile.canExtract(slot, stack);
        }
        return true;
    }

    @Override
    protected void onContentsChanged(int slot) {
        if (this.tile != null) {
            this.tile.onSlotChanged(slot);
        }
    }

    @Nullable
    public TileBase getTile() {
        return tile;
    }

    public boolean isEmpty() {
        for (ItemStack stack : this.stacks) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean isSlotEmpty(int slot) {
        return this.stacks.get(slot).isEmpty();
    }

    public ItemStack setSlotEmpty(int slot) {
        ItemStack stack = this.stacks.set(slot, ItemStack.EMPTY);
        onContentsChanged(slot);
        return stack;
    }

    public ItemStack setStack(int slot, ItemStack stack) {
        ItemStack stack1 = this.stacks.set(slot, stack);
        onContentsChanged(slot);
        return stack1;
    }

    public void clear() {
        set(getSlots());
    }

    public boolean isBlank() {
        return this.stacks.size() <= 0;
    }

    public NonNullList<ItemStack> getStacks() {
        return this.stacks;
    }

    public List<ItemStack> getNonEmptyStacks() {
        List<ItemStack> stacks = new ArrayList<>(this.stacks);
        stacks.removeIf(ItemStack::isEmpty);
        return stacks;
    }

    public ItemStack addNext(ItemStack stack) {
        for (int i = 0; i < getSlots(); ++i) {
            if (isItemValid(i, stack)) {
                return insertItem(i, stack.copy(), false);
            }
        }
        return ItemStack.EMPTY;
    }

    public ItemStack removeNext() {
        for (int i = getSlots() - 1; i >= 0; --i) {
            ItemStack stack = setSlotEmpty(i);
            if (!stack.isEmpty()) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public void drop(World world, BlockPos pos) {
        this.stacks.forEach(stack -> {
            InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
        });
    }
}
