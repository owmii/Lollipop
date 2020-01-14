package owmii.lib.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import owmii.lib.energy.Energy;
import owmii.lib.inventory.Inventory;

import javax.annotation.Nullable;

public class TileBase extends TileEntity implements INamedContainerProvider {
    protected final Inventory inv = Inventory.createBlank(this);
    private final LazyOptional<Inventory> holder = LazyOptional.of(() -> this.inv);
    protected boolean isContainerOpen;
    public boolean isNew = true;

    @Nullable
    public ITextComponent customName;
    public String publicName = "";

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
        this.isNew = compound.getBoolean("IsNew");
        if (compound.contains("DefaultName", 8))
            this.publicName = compound.getString("DefaultName");
        if (compound.contains("CustomName", 8))
            this.customName = ITextComponent.Serializer.fromJson(compound.getString("CustomName"));
        if (!keepInventory()) this.inv.deserializeNBT(compound);
        readStorable(compound);
    }

    public CompoundNBT writeSync(CompoundNBT compound) {
        compound.putBoolean("IsNew", this.isNew);
        if (!this.publicName.isEmpty())
            compound.putString("DefaultName", this.publicName);
        if (this.customName != null)
            compound.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));
        if (!keepInventory()) compound.merge(this.inv.serializeNBT());
        writeStorable(compound);
        return compound;
    }

    public void readStorable(CompoundNBT compound) {
        if (keepInventory())
            this.inv.deserializeNBT(compound);
    }

    public CompoundNBT writeStorable(CompoundNBT compound) {
        if (keepInventory())
            compound.merge(this.inv.serializeNBT());
        return compound;
    }

    public boolean isNBTStorable() {
        return true;
    }

    public void markDirtyAndSync() {
        if (this.world != null && !this.world.isRemote) {
            BlockState state = getBlockState();
            this.world.notifyBlockUpdate(this.pos, state, state, 3);
            markDirty();
        }
    }

    public boolean isRemote() {
        return this.world != null && this.world.isRemote;
    }

    public AbstractBlock getBlock() {
        return (AbstractBlock) getBlockState().getBlock();
    }

    public void setContainerOpen(boolean b) {
        this.isContainerOpen = b;
    }

    public boolean isContainerOpen() {
        return this.isContainerOpen;
    }

    public void onSlotChanged(int index) {
    }

    public boolean canInsert(int index, ItemStack stack) {
        return true;
    }

    public boolean canExtract(int slot, ItemStack stack) {
        return true;
    }

    public int getSlotLimit(int index) {
        return 64;
    }

    public boolean keepInventory() {
        return false;
    }

    public Inventory getInventory() {
        return this.inv;
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
        return this.customName;
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
        return getBlock().getContainer(i, playerInventory, this);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (!this.removed && !this.inv.isBlank() && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return this.holder.cast();
        }
        return super.getCapability(cap, side);
    }

    public abstract static class Machine extends Tickable {
        private final Energy energy;

        public Machine(TileEntityType<?> type, long capacity, long maxExtract, long maxReceive) {
            super(type);
            this.energy = Energy.create(capacity, maxExtract, maxReceive);
        }

        public int receiveEnergy(int maxReceive, boolean simulate, @Nullable Direction side) {
            if (canReceiveEnergy(side)) {
                return 0;
            }
            return 0;
        }

        public int extractEnergy(int maxExtract, boolean simulate, @Nullable Direction side) {
            if (canExtractEnergy(side)) {
                return 0;
            }
            return 0;
        }

        public boolean canReceiveEnergy(@Nullable Direction side) {
            return this.energy.canReceive();
        }

        public boolean canExtractEnergy(@Nullable Direction side) {
            return this.energy.canExtract();
        }

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
            if (cap == CapabilityEnergy.ENERGY && isEnergyPresent(side)) {
                return LazyOptional.of(() -> new Energy(this.energy) {
                    @Override
                    public int extractEnergy(int maxExtract, boolean simulate) {
                        return Machine.this.extractEnergy(maxExtract, simulate, side);
                    }

                    @Override
                    public int receiveEnergy(int maxReceive, boolean simulate) {
                        return Machine.this.receiveEnergy(maxReceive, simulate, side);
                    }

                    @Override
                    public boolean canReceive() {
                        return Machine.this.canReceiveEnergy(side);
                    }

                    @Override
                    public boolean canExtract() {
                        return Machine.this.canExtractEnergy(side);
                    }
                }).cast();
            }
            return super.getCapability(cap, side);
        }

        public boolean isEnergyPresent(@Nullable Direction side) {
            return true;
        }
    }

    public abstract static class Tickable extends TileBase implements ITickableTileEntity {
        protected int syncTicks;
        public int ticks;

        public Tickable(TileEntityType<?> type) {
            super(type);
        }

        @Override
        public void tick() {
            if (this.world != null) {
                if (doTicks()) {
                    if (this.ticks == 0) {
                        if (this.isNew) absFirstTick(this.world);
                        firstTick(this.world);
                    }
                    if (postTicks(this.world)) {
                        this.world.markChunkDirty(this.pos, this);
                        sync(getSyncTicks());
                    }
                    if (isRemote()) clientTicks(this.world);
                    this.ticks++;
                    if (this.syncTicks > -1) this.syncTicks--;
                    if (this.syncTicks == 0) markDirtyAndSync();
                }
                if (this.isNew) {
                    this.isNew = false;
                    markDirtyAndSync();
                }
            }
        }

        protected boolean doTicks() {
            return true;
        }

        protected void absFirstTick(World world) {
        }

        protected void firstTick(World world) {
        }

        protected boolean postTicks(World world) {
            return false;
        }

        @OnlyIn(Dist.CLIENT)
        protected void clientTicks(World world) {
        }

        public void resetTicks() {
            this.ticks = 0;
        }

        public void sync(int delay) {
            if (this.syncTicks <= 0) {
                this.syncTicks = delay;
            }
        }

        public int getSyncTicks() {
            return isContainerOpen() ? 3 : 50;
        }
    }
}
