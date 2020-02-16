package owmii.lib.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import owmii.lib.config.IEnergyConfig;
import owmii.lib.config.IEnergyProviderConfig;
import owmii.lib.energy.Energy;
import owmii.lib.energy.IRedstoneInteract;
import owmii.lib.energy.Redstone;
import owmii.lib.energy.SideConfig;
import owmii.lib.inventory.Inventory;
import owmii.lib.util.*;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static owmii.lib.block.AbstractBlock.LIT;

public class TileBase<E extends IVariant, B extends AbstractBlock<E>> extends TileEntity implements INamedContainerProvider {
    protected final Inventory inv = Inventory.createBlank(this);
    private final LazyOptional<Inventory> holder = LazyOptional.of(() -> this.inv);
    protected E variant;

    protected boolean isContainerOpen;
    public boolean isNew = true;

    @Nullable
    public ITextComponent customName;
    public String publicName = Empty.STRING;

    public TileBase(TileEntityType<?> tileEntityTypeIn) {
        this(tileEntityTypeIn, IVariant.getEmpty());
    }

    public TileBase(TileEntityType<?> tileEntityType, E variant) {
        super(tileEntityType);
        this.variant = variant;
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

    @SuppressWarnings("unchecked")
    public void readSync(CompoundNBT compound) {
        this.isNew = compound.getBoolean("IsNew");
        if (!this.variant.isEmpty() && compound.contains("Variant", Constants.NBT.TAG_INT)) {
            this.variant = (E) this.variant.read(compound, "Variant");
        }
        if (compound.contains("DefaultName", Constants.NBT.TAG_STRING)) {
            this.publicName = compound.getString("DefaultName");
        }
        if (compound.contains("CustomName", Constants.NBT.TAG_STRING)) {
            this.customName = ITextComponent.Serializer.fromJson(compound.getString("CustomName"));
        }
        if (!keepInventory()) {
            this.inv.deserializeNBT(compound);
        }
        readStorable(compound);
    }

    @SuppressWarnings("unchecked")
    public CompoundNBT writeSync(CompoundNBT compound) {
        compound.putBoolean("IsNew", this.isNew);
        if (!this.variant.isEmpty()) {
            this.variant.write(compound, (Enum<?>) this.variant, "Variant");
        }
        if (!this.publicName.isEmpty()) {
            compound.putString("DefaultName", this.publicName);
        }
        if (this.customName != null) {
            compound.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));
        }
        if (!keepInventory()) {
            compound.merge(this.inv.serializeNBT());
        }
        writeStorable(compound);
        return compound;
    }

    public void readStorable(CompoundNBT compound) {
        if (keepInventory()) {
            this.inv.deserializeNBT(compound);
        }
    }

    public CompoundNBT writeStorable(CompoundNBT compound) {
        if (keepInventory()) {
            compound.merge(this.inv.serializeNBT());
        }
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

    @SuppressWarnings("unchecked")
    public B getBlock() {
        return (B) getBlockState().getBlock();
    }

    @Nullable
    public TileEntity getTileEntity(BlockPos pos) {
        return this.world == null ? null : this.world.getTileEntity(pos);
    }

    public void onPlaced(World world, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (stack.hasDisplayName()) {
            setCustomName(stack.getDisplayName());
        } else {
            setDefaultName(getBlock().getTranslationKey());
        }
        CompoundNBT tag = Stack.getTagOrEmpty(stack);
        if (!tag.isEmpty()) {
            readStorable(tag.getCompound(Data.TAG_TE_STORABLE));
        }
    }

    public void onAdded(World world, BlockState state, BlockState oldState, boolean isMoving) {
    }

    public void onRemoved(World world, BlockState state, BlockState newState, boolean isMoving) {
        if (!keepInventory() || !isNBTStorable()) {
            getInventory().drop(world, this.pos);
        }
    }

    public void neighborChanged(World world, BlockState state, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
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

    public E getVariant() {
        return this.variant;
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

    protected boolean switchLitProp(boolean prev, boolean b, boolean or) {
        if (this.world != null && getBlock().hasLitProp()) {
            if (prev != b || or) {
                this.world.setBlockState(this.pos, getBlockState().with(LIT, b), 3);
                return true;
            }
        }
        return false;
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

    public static class EnergyProvider<E extends IVariant, B extends AbstractEnergyProviderBlock<E>> extends EnergyStorage<E, B> {
        protected long buffer;
        protected long nextBuff;

        public EnergyProvider(TileEntityType<?> tileEntityType, E variant) {
            super(tileEntityType, variant);
        }

        public EnergyProvider(TileEntityType<?> type, long perTick) {
            this(type, IVariant.getEmpty());
        }

        @Override
        public void readSync(CompoundNBT compound) {
            super.readSync(compound);
            if (hasEnergyBuffer()) {
                this.buffer = compound.getLong("EnergyBuffer");
                this.nextBuff = compound.getLong("NextEnergyBuff");
            }
        }

        @Override
        public CompoundNBT writeSync(CompoundNBT compound) {
            if (hasEnergyBuffer()) {
                compound.putLong("EnergyBuffer", this.buffer);
                compound.putLong("NextEnergyBuff", this.nextBuff);
            }
            return super.writeSync(compound);
        }

        public boolean hasEnergyBuffer() {
            return false;
        }

        @Override
        protected boolean postTicks(World world) {
            if (isRemote()) return false;
            boolean flag = this.nextBuff > 0;
            boolean flag1 = super.postTicks(world);
            if (defaultGeneration() > 0) {
                long toGenerate = Math.min(defaultGeneration(), getEnergyStorage().getEmpty());
                if (this.nextBuff > toGenerate) {
                    this.nextBuff -= toGenerate;
                    if (this.nextBuff <= 0) {
                        this.buffer = 0;
                    }
                } else {
                    toGenerate = this.nextBuff;
                    this.nextBuff = 0;
                    this.buffer = 0;
                }
                generate(world);
                if (toGenerate > 0) {
                    produceEnergy(toGenerate);
                    flag1 = true;
                }
            }
            if (switchLitProp(flag, this.nextBuff > 0, false)) { // TODO
                flag1 = true;
            }
            return flag1;
        }

        protected void generate(World world) {
        }

        public long getBuffer() {
            return this.buffer;
        }

        public long getNextBuff() {
            return this.nextBuff;
        }

        public long getGeneration() {
            return defaultGeneration();
        }

        public long defaultGeneration() {
            return getEnergyConfig().getGeneration(getVariant());
        }

        @Override
        protected IEnergyProviderConfig<E> getEnergyConfig() {
            return getBlock().getEnergyConfig();
        }

        @Override
        public long getMaxEnergyReceive() {
            return 0L;
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void getListedEnergyInfo(List<String> list) {
            super.getListedEnergyInfo(list);
            list.add(TextFormatting.GRAY + I18n.format("info.lollipop.max.generates", TextFormatting.DARK_GRAY + Text.numFormat(getGeneration())));
        }
    }

    public static class EnergyStorage<E extends IVariant, B extends AbstractEnergyBlock<E>> extends Tickable<E, B> implements IRedstoneInteract {
        protected final Energy energy = Energy.create(0);
        protected SideConfig sideConfig = new SideConfig(this);
        protected Redstone redstone = Redstone.IGNORE;

        public EnergyStorage(TileEntityType<?> tileEntityType, E variant) {
            super(tileEntityType, variant);
            this.inv.add(getChargingSlots());
        }

        public EnergyStorage(TileEntityType<?> type) {
            this(type, IVariant.getEmpty());
        }

        @Override
        public void readSync(CompoundNBT compound) {
            this.redstone = Redstone.values()[compound.getInt("RedstoneMode")];
            this.sideConfig.read(compound);
            if (!keepEnergy()) {
                this.energy.readStored(compound);
            }
            super.readSync(compound);
        }

        @Override
        public CompoundNBT writeSync(CompoundNBT compound) {
            compound.putInt("RedstoneMode", this.redstone.ordinal());
            this.sideConfig.write(compound);
            if (!keepEnergy()) {
                this.energy.writeStored(compound);
            }
            return super.writeSync(compound);
        }

        @Override
        public void readStorable(CompoundNBT compound) {
            if (keepEnergy()) {
                this.energy.readStored(compound);
            }
            super.readStorable(compound);
        }

        @Override
        public CompoundNBT writeStorable(CompoundNBT compound) {
            if (keepEnergy()) {
                this.energy.writeStored(compound);
            }
            return super.writeStorable(compound);
        }

        public boolean keepEnergy() {
            return false;
        }

        @Override
        protected void onFirstTick(World world) {
            super.onFirstTick(world);
            this.energy.setCapacity(defaultEnergyCapacity());
            if (!getTransferType().isOutOnly()) {
                this.energy.setMaxReceive(defaultTransfer());
            }
            if (!getTransferType().isInOnly()) {
                this.energy.setMaxExtract(defaultTransfer());
            }
            sync(1);
        }

        @Override
        protected boolean postTicks(World world) {
            return extractFromSides() + chargeItems() > 0;
        }

        @Override
        protected boolean doPostTicks(World world) {
            return checkRedstone();
        }

        protected long extractFromSides() {
            long extracted = 0;
            for (Direction side : Direction.values()) {
                if (canExtractEnergy(side)) {
                    long amount = Math.min(getMaxEnergyExtract(), getEnergyStored());
                    long toExtract = Energy.receive(getTileEntity(this.pos.offset(side)), side, Safe.integer(amount), false);
                    extracted += extractEnergy(Safe.integer(toExtract), false, side);
                }
            }
            return extracted;
        }

        protected long chargeItems() {
            long extracted = 0;
            for (ItemStack stack : getChargingInv()) {
                extracted += chargeItem(stack, getMaxEnergyExtract());
            }
            return extracted;
        }

        protected long chargeItem(ItemStack stack, long transfer) {
            if (!stack.isEmpty()) {
                long amount = Math.min(transfer, getEnergyStored());
                int received = Energy.receive(stack, amount, false);
                return extractEnergy(received, false, null);
            }
            return 0;
        }

        @Override
        public int getSlotLimit(int index) {
            if (index < builtInSlots()) {
                return 1;
            }
            return super.getSlotLimit(index);
        }

        public int builtInSlots() {
            return getChargingSlots();
        }

        public List<ItemStack> getChargingInv() {
            return IntStream.range(0, getChargingSlots())
                    .mapToObj(value -> this.inv.getStacks().get(value))
                    .collect(Collectors.toList());
        }

        public int getChargingSlots() {
            return 0;
        }

        @Override
        public boolean canInsert(int index, ItemStack stack) {
            if (index < getChargingSlots()) {
                return Energy.isPresent(stack);
            }
            return true;
        }

        public long receiveEnergy(int maxReceive, boolean simulate, @Nullable Direction side) {
            if (canReceiveEnergy(side)) {
                long energyReceived = Math.min(getEnergyStorage().getEmpty(), Math.min(getMaxEnergyReceive(), maxReceive));
                if (!simulate) {
                    produceEnergy(energyReceived);
                    if (energyReceived > 0) {
                        sync(getSyncTicks());
                    }
                }
                return energyReceived;
            }
            return 0;
        }

        public long extractEnergy(int maxExtract, boolean simulate, @Nullable Direction side) {
            if (canExtractEnergy(side)) {
                long energyExtracted = Math.min(getEnergyStored(), Math.min(getMaxEnergyExtract(), maxExtract));
                if (!simulate) {
                    consumeEnergy(energyExtracted);
                    if (energyExtracted > 0) {
                        sync(getSyncTicks());
                    }
                }
                return energyExtracted;
            }
            return 0;
        }

        public void produceEnergy(long amount) {
            getEnergyStorage().produce(amount);
        }

        public void consumeEnergy(long amount) {
            getEnergyStorage().consume(amount);
        }

        @Override
        public void onAdded(World world, BlockState state, BlockState oldState, boolean isMoving) {
            super.onAdded(world, state, oldState, isMoving);
            getSideConfig().init();
        }

        public boolean canReceiveEnergy(@Nullable Direction side) {
            return checkRedstone() && getEnergyStorage().canReceive() && (this.sideConfig.getType(side).isIn() || side == null);
        }

        public boolean canExtractEnergy(@Nullable Direction side) {
            return checkRedstone() && getEnergyStorage().canExtract() && (this.sideConfig.getType(side).isOut() || side == null);
        }

        public long getEnergyCapacity() {
            return getEnergyStorage().getCapacity();
        }

        public long getEnergyStored() {
            return getEnergyStorage().getStored();
        }

        public long getMaxEnergyExtract() {
            return Math.min(this.energy.getMaxExtract(), defaultTransfer());
        }

        public long getMaxEnergyReceive() {
            return Math.min(this.energy.getMaxReceive(), defaultTransfer());
        }

        public long defaultEnergyCapacity() {
            return getEnergyConfig().getCapacity(getVariant());
        }

        public long defaultTransfer() {
            return getEnergyConfig().getTransfer(getVariant());
        }

        public Energy getEnergyStorage() {
            return this.energy;
        }

        protected IEnergyConfig<E> getEnergyConfig() {
            return getBlock().getEnergyConfig();
        }

        public SideConfig getSideConfig() {
            return this.sideConfig;
        }

        public boolean checkRedstone() {
            boolean power = this.world != null && this.world.getRedstonePowerFromNeighbors(this.pos) > 0;
            return Redstone.IGNORE.equals(getRedstoneMode()) || power && Redstone.ON.equals(getRedstoneMode()) || !power && Redstone.OFF.equals(getRedstoneMode());
        }

        @Override
        public Redstone getRedstoneMode() {
            return this.redstone;
        }

        @Override
        public void setRedstone(Redstone redstone) {
            this.redstone = redstone;
        }

        public SideConfig.Type getTransferType() {
            return getBlock().getTransferType();
        }

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
            if (cap == CapabilityEnergy.ENERGY && isEnergyPresent(side)) {
                return LazyOptional.of(() -> new Energy(getEnergyStorage()) {
                    @Override
                    public int extractEnergy(int maxExtract, boolean simulate) {
                        return Safe.integer(EnergyStorage.this.extractEnergy(maxExtract, simulate, side));
                    }

                    @Override
                    public int receiveEnergy(int maxReceive, boolean simulate) {
                        return Safe.integer(EnergyStorage.this.receiveEnergy(maxReceive, simulate, side));
                    }

                    @Override
                    public boolean canReceive() {
                        return EnergyStorage.this.canReceiveEnergy(side);
                    }

                    @Override
                    public boolean canExtract() {
                        return EnergyStorage.this.canExtractEnergy(side);
                    }
                }).cast();
            }
            return super.getCapability(cap, side);
        }

        public boolean isEnergyPresent(@Nullable Direction side) {
            return true;
        }

        @OnlyIn(Dist.CLIENT)
        public void getListedEnergyInfo(List<String> list) {
            TextFormatting g = TextFormatting.GRAY, dg = TextFormatting.DARK_GRAY;
            list.add(g + I18n.format("info.lollipop.stored.energy.fe", dg + Text.addCommas(this.energy.getStored()), Text.numFormat(this.energy.getCapacity())));
            long ext = getMaxEnergyExtract();
            long re = getMaxEnergyReceive();
            if (ext + re > 0) {
                if (ext == re) {
                    list.add(g + I18n.format("info.lollipop.max.transfer.fe", TextFormatting.DARK_GRAY + Text.numFormat(ext)));
                } else {
                    if (ext > 0)
                        list.add(g + I18n.format("info.lollipop.max.extract.fe", dg + Text.numFormat(ext)));
                    if (re > 0)
                        list.add(g + I18n.format("info.lollipop.max.receive.fe", dg + Text.numFormat(re)));
                }
            }
        }
    }

    public static class Tickable<E extends IVariant, B extends AbstractBlock<E>> extends TileBase<E, B> implements ITickableTileEntity {
        protected int syncTicks;
        public int ticks;

        public Tickable(TileEntityType<?> tileEntityType, E variant) {
            super(tileEntityType, variant);
        }

        public Tickable(TileEntityType<?> type) {
            super(type);
        }

        @Override
        public void tick() {
            if (this.world == null) return;
            if (doTicks(this.world)) {
                if (this.ticks == 0) {
                    if (this.isNew) absFirstTick(this.world);
                    onFirstTick(this.world);
                }
                if (doPostTicks(this.world) && postTicks(this.world)) {
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

        protected boolean doTicks(World world) {
            return true;
        }

        protected void absFirstTick(World world) {
        }

        protected void onFirstTick(World world) {
        }

        protected boolean postTicks(World world) {
            return false;
        }

        protected boolean doPostTicks(World world) {
            return true;
        }

        @OnlyIn(Dist.CLIENT)
        protected void clientTicks(World world) {
        }

        public void resetTicks() {
            this.ticks = 0;
        }

        public void sync(int delay) {
            if (this.syncTicks <= 0 || delay < this.syncTicks) {
                this.syncTicks = delay;
            }
        }

        public int getSyncTicks() {
            if (isContainerOpen()) {
                if (!isRemote()) {
                    if (Server.isSinglePlayer()) {
                        return 1;
                    }
                }
                return 3;
            }
            return 100;
        }
    }
}
