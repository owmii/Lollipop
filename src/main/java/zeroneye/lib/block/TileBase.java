package zeroneye.lib.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.INameable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import zeroneye.lib.util.Inventory;

import javax.annotation.Nullable;

public abstract class TileBase extends TileEntity implements INameable {
    public NonNullList<ItemStack> stacks = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
    @Nullable
    public ITextComponent customName;
    public String defaultName = "";
    protected boolean isContainerOpen;
    protected boolean sync;

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
        if (compound.contains("DefaultName", 8)) {
            this.defaultName = compound.getString("DefaultName");
        }
        if (compound.contains("CustomName", 8)) {
            this.customName = ITextComponent.Serializer.fromJson(compound.getString("CustomName"));
        }
        if (this instanceof IInvBase) {
            this.stacks = Inventory.readItems(compound, (IInvBase) this);
            ((IInvBase) this).onInventoryChanged(0);
        }
        readStorable(compound);
    }

    public CompoundNBT writeSync(CompoundNBT compound) {
        if (!this.defaultName.isEmpty()) {
            compound.putString("DefaultName", this.defaultName);
        }
        if (this.customName != null) {
            compound.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));
        }
        if (this instanceof IInvBase) {
            Inventory.writeItems(compound, (IInventory) this);
        }
        writeStorable(compound);
        return compound;
    }

    public int getSizeInventory() {
        return 0;
    }

    public void readStorable(CompoundNBT compound) {
    }

    public CompoundNBT writeStorable(CompoundNBT compound) {
        return compound;
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

    public void markDirtyAndSync() {
        if (this.world != null) {
            markDirty();
            if (isServerWorld()) {
                BlockState state = getBlockState();
                this.world.notifyBlockUpdate(getPos(), state, state, 3);
            }
        }
    }

    public boolean isServerWorld() {
        return this.world != null && !this.world.isRemote;
    }

    public Block getBlock() {
        return getBlockState().getBlock();
    }

    @Override
    public boolean hasCustomName() {
        return this.customName != null;
    }

    @Override
    public ITextComponent getDisplayName() {
        return getName();
    }

    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent(this.defaultName);
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }

    @Nullable
    @Override
    public ITextComponent getCustomName() {
        return customName;
    }

    public ITextComponent getName() {
        return this.customName != null ? this.customName : getDefaultName();
    }

    public void setCustomName(ITextComponent displayName) {
        this.customName = displayName;
    }

    public void setContainerOpen(boolean b) {
        this.isContainerOpen = b;
    }

    public boolean isContainerOpen() {
        return isContainerOpen;
    }

    public static abstract class Tickable extends TileBase implements ITickableTileEntity {
        public int ticks;

        public Tickable(TileEntityType<?> type) {
            super(type);
        }

        @Override
        public final void tick() {
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

        protected abstract boolean postTicks();

        public void resetTicks() {
            this.ticks = 0;
        }
    }
}
