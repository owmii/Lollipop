package zeroneye.lib.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import zeroneye.lib.util.Inventory;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

public class TileBase extends TileEntity {
    protected boolean isContainerOpen;

    public TileBase(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        readSync(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        CompoundNBT nbt = super.write(compound);
        return writeSync(nbt);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return write(new CompoundNBT());
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getPos(), 3, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        readSync(pkt.getNbtCompound());
    }

    public void readSync(CompoundNBT compound) {
        readStorable(compound);
    }

    public CompoundNBT writeSync(CompoundNBT compound) {
        writeStorable(compound);
        return compound;
    }

    public void readStorable(CompoundNBT compound) {
    }

    public CompoundNBT writeStorable(CompoundNBT compound) {
        return compound;
    }

    public boolean isNBTStorable() {
        return true;
    }

    public void markDirtyAndSync() {
        if (this.world != null) {
            if (isServerWorld()) {
                BlockState state = getBlockState();
                this.world.notifyBlockUpdate(getPos(), state, state, 3);
                markDirty();
            }
        }
    }

    public boolean isServerWorld() {
        return this.world != null && !this.world.isRemote;
    }

    public Block getBlock() {
        return getBlockState().getBlock();
    }

    public void setContainerOpen(boolean b) {
        this.isContainerOpen = b;
    }

    public boolean isContainerOpen() {
        return isContainerOpen;
    }

    public static class TickableInv extends Tickable implements ISidedInventory, INamedContainerProvider {
        public NonNullList<ItemStack> stacks;
        @Nullable
        public ITextComponent customName;
        public String publicName = "";

        public TickableInv(TileEntityType<?> type) {
            super(type);
            this.stacks = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
        }

        protected void initInvStacks(int size) {
            this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        }

        @Override
        public void readSync(CompoundNBT compound) {
            super.readSync(compound);
            if (compound.contains("DefaultName", 8)) {
                this.publicName = compound.getString("DefaultName");
            }
            if (compound.contains("CustomName", 8)) {
                this.customName = ITextComponent.Serializer.fromJson(compound.getString("CustomName"));
            }
            if (dropInventoryOnBreak()) {
                readInventory(compound);
            }
        }

        @Override
        public CompoundNBT writeSync(CompoundNBT compound) {
            final CompoundNBT nbt = super.writeSync(compound);
            if (!this.publicName.isEmpty()) {
                nbt.putString("DefaultName", this.publicName);
            }
            if (this.customName != null) {
                nbt.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));
            }
            if (dropInventoryOnBreak()) {
                writeInventory(nbt);
            }
            return nbt;
        }

        public void readStorable(CompoundNBT compound) {
            if (!dropInventoryOnBreak()) {
                readInventory(compound);
            }
        }

        public CompoundNBT writeStorable(CompoundNBT compound) {
            if (!dropInventoryOnBreak()) {
                writeInventory(compound);
            }
            return compound;
        }

        public boolean dropInventoryOnBreak() {
            return true;
        }

        private void readInventory(CompoundNBT compound) {
            this.stacks = Inventory.readItems(compound, this);
            this.onInventoryChanged(0);
        }

        private CompoundNBT writeInventory(CompoundNBT compound) {
            Inventory.writeItems(compound, this);
            return compound;
        }

        @Override
        public int[] getSlotsForFace(Direction Direction) {
            return IntStream.range(0, getSizeInventory()).toArray();
        }

        @Override
        public boolean canInsertItem(int index, ItemStack itemStack, @Nullable Direction Direction) {
            return isItemValidForSlot(index, itemStack);
        }

        @Override
        public boolean canExtractItem(int index, ItemStack itemStack, Direction Direction) {
            return true;
        }

        @Override
        public int getSizeInventory() {
            return 0;
        }

        public void setInvSize(int size) {
            this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        }

        @Override
        public boolean isEmpty() {
            return this.stacks.isEmpty();
        }

        @Override
        public ItemStack getStackInSlot(int index) {
            return this.stacks.get(index);
        }

        @Override
        public ItemStack decrStackSize(int index, int count) {
            ItemStack itemstack = ItemStackHelper.getAndSplit(this.stacks, index, count);
            if (!itemstack.isEmpty()) {
                markDirty();
            }
            return itemstack;
        }

        @Override
        public ItemStack removeStackFromSlot(int index) {
            return ItemStackHelper.getAndRemove(this.stacks, index);
        }

        @Override
        public void setInventorySlotContents(int index, ItemStack itemStack) {
            ItemStack oldStack = getStackInSlot(index);
            this.stacks.set(index, itemStack);
            if (itemStack.getCount() > maxStackSize(index)) {
                itemStack.setCount(maxStackSize(index));
            }
            markDirty();
            if (!oldStack.isItemEqual(itemStack)) {
                onInventoryChanged(index);
            }
        }

        public void onInventoryChanged(int index) {
        }

        public int maxStackSize(int index) {
            return getInventoryStackLimit();
        }

        @Override
        public int getInventoryStackLimit() {
            return 64;
        }

        @Override
        public boolean isUsableByPlayer(PlayerEntity PlayerEntity) {
            return true;
        }

        @Override
        public void openInventory(PlayerEntity PlayerEntity) {
        }

        @Override
        public void closeInventory(PlayerEntity PlayerEntity) {
        }

        @Override
        public boolean isItemValidForSlot(int index, ItemStack itemStack) {
            return true;
        }

        @Override
        public void clear() {
            for (int i = 0; i < getSizeInventory(); ++i) {
                ItemStack stack = getStackInSlot(i);
                if (!stack.isEmpty()) {
                    setInventorySlotContents(i, ItemStack.EMPTY);
                }
            }
        }

        public boolean hasCustomName() {
            return this.customName != null;
        }

        @Override
        public ITextComponent getDisplayName() {
            return getName();
        }

        protected ITextComponent getDefaultName() {
            return new TranslationTextComponent(this.publicName);
        }

        public void setDefaultName(String publicName) {
            this.publicName = publicName;
        }

        @Nullable
        public ITextComponent getCustomName() {
            return customName;
        }

        public ITextComponent getName() {
            return this.customName != null ? this.customName : getDefaultName();
        }

        public void setCustomName(ITextComponent displayName) {
            this.customName = displayName;
        }

        @Nullable
        @Override
        public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
            if (this.getBlock() instanceof BlockBase) {
                return ((BlockBase) getBlock()).getContainer(i, playerInventory, this);
            }
            return null;
        }
    }

    public static class Tickable extends TileBase implements ITickableTileEntity {
        protected boolean sync;
        public int ticks;

        public Tickable(TileEntityType<?> type) {
            super(type);
        }

        @Override
        public void tick() {
            if (this.world == null) return;
            if (doTicks()) {
                if (this.ticks == 0) {
                    onFirstTick();
                }
                if (postTicks()) {
                    markDirty();
                    setReadyToSync(true);
                }
                this.ticks++;
                if (isReadyToSync() && this.ticks % getSyncTicks() == 0) {
                    markDirtyAndSync();
                    setReadyToSync(false);
                }
            }
        }

        protected boolean doTicks() {
            return true;
        }

        protected void onFirstTick() {
        }

        protected boolean postTicks() {
            return false;
        }

        public void resetTicks() {
            this.ticks = 0;
        }

        public boolean isReadyToSync() {
            return sync;
        }

        public void setReadyToSync(boolean sync) {
            this.sync = sync;
        }

        public int getSyncTicks() {
            return isContainerOpen() ? 10 : 0;
        }
    }


}
