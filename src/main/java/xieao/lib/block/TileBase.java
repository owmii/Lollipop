package xieao.lib.block;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import xieao.lib.util.Inventory;

import javax.annotation.Nullable;
import java.util.Objects;

public abstract class TileBase extends TileEntity {
    public NonNullList<ItemStack> stacks = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
    @Nullable
    public ITextComponent customName;

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
        if (compound.contains("CustomName", 8)) {
            this.customName = ITextComponent.Serializer.fromJson(compound.getString("CustomName"));
        }
        if (this instanceof IInvBase) {
            this.stacks = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
            Inventory.readAllItems(compound, (IInvBase) this);
        }
        readStorable(compound);
    }

    public CompoundNBT writeSync(CompoundNBT compound) {
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

    public ITextComponent getName() {
        return new StringTextComponent("block." + Objects.requireNonNull(getType()
                .getRegistryName()).toString().replace(':', '.'));
    }

    public static abstract class Tickable extends TileBase implements ITickableTileEntity {
        public int ticks;

        public Tickable(TileEntityType<?> type) {
            super(type);
        }

        @Override
        public void tick() {
            this.ticks++;
        }
    }
}
