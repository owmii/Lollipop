package owmii.lib.energy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import owmii.lib.compat.curios.CuriosCompat;
import owmii.lib.util.Data;
import owmii.lib.util.Player;
import owmii.lib.util.Safe;
import owmii.lib.util.Stack;
import owmii.lib.util.lambda.Checker;

import javax.annotation.Nullable;

public class Energy implements IEnergyStorage {
    public static final Energy EMPTY = Energy.create(0);
    public static final Long MAX = 9_000_000_000_000_000_000L;
    public static final Long MIN = 1L;

    private long capacity;
    private long stored;
    private long maxExtract;
    private long maxReceive;

    public Energy(Energy energy) {
        this(energy.capacity, energy.maxExtract, energy.maxReceive);
    }

    public Energy(long capacity, long maxExtract, long maxReceive) {
        this.capacity = capacity;
        this.maxExtract = maxExtract;
        this.maxReceive = maxReceive;
    }

    public static Energy create(long capacity) {
        return create(capacity, capacity, capacity);
    }

    public static Energy create(long capacity, long transfer) {
        return create(capacity, transfer, transfer);
    }

    public static Energy from(Energy energy) {
        return create(energy.capacity, energy.maxExtract, energy.maxReceive);
    }

    public static Energy create(long capacity, long maxExtract, long maxReceive) {
        return new Energy(capacity, maxExtract, maxReceive);
    }

    public void readStored(CompoundNBT nbt) {
        this.stored = nbt.getLong("StoredFEnergy");
    }

    public CompoundNBT writeStored(CompoundNBT nbt) {
        nbt.putLong("StoredFEnergy", this.stored);
        return nbt;
    }

    public void readTransfer(CompoundNBT nbt) {
        this.maxExtract = nbt.getLong("MaxEnergyExtract");
        this.maxReceive = nbt.getLong("MaxEnergyReceive");
    }

    public CompoundNBT writeTransfer(CompoundNBT nbt) {
        nbt.putLong("MaxEnergyExtract", this.maxExtract);
        nbt.putLong("MaxEnergyReceive", this.maxReceive);
        return nbt;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive())
            return 0;
        long energyReceived = Math.min(this.capacity - this.stored, Math.min(this.maxReceive, maxReceive));
        if (!simulate)
            this.stored += energyReceived;
        return (int) energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract())
            return 0;
        long energyExtracted = Math.min(this.stored, Math.min(this.maxExtract, maxExtract));
        if (!simulate)
            this.stored -= energyExtracted;
        return (int) energyExtracted;
    }

    public void produce(long amount) {
        this.stored = Math.min(this.stored + amount, this.capacity);
    }

    public void consume(long amount) {
        this.stored = Math.max(this.stored - amount, 0);
    }

    public long chargeInventory(PlayerEntity player, Checker<ItemStack> checker) {
        long l = 0L;
        for (ItemStack stack1 : Player.invStacks(player)) {
            if (stack1.isEmpty() || !isPresent(stack1) || !checker.check(stack1)) continue;
            long amount = Math.min(getMaxExtract(), getEnergyStored());
            if (amount <= 0) break;
            int received = Energy.receive(stack1, amount, false);
            l += extractEnergy(received, false);
        }
        if (CuriosCompat.isLoaded()) {
            for (ItemStack stack1 : CuriosCompat.getAllStacks(player)) {
                if (stack1.isEmpty() || !isPresent(stack1) || !checker.check(stack1)) continue;
                long amount = Math.min(getMaxExtract(), getEnergyStored());
                if (amount <= 0) break;
                int received = Energy.receive(stack1, amount, false);
                l += extractEnergy(received, false);
            }
        }
        return l;
    }

    public long getEmpty() {
        return getCapacity() - getStored();
    }

    public long getCapacity() {
        return this.capacity;
    }

    public Energy setCapacity(long capacity) {
        this.capacity = capacity;
        return this;
    }

    public long getStored() {
        return this.stored;
    }

    public Energy setStored(long stored) {
        this.stored = Math.max(0, Math.min(this.capacity, stored));
        return this;
    }

    public long getMaxExtract() {
        return this.maxExtract;
    }

    public Energy setMaxExtract(long maxExtract) {
        this.maxExtract = maxExtract;
        return this;
    }

    public long getMaxReceive() {
        return this.maxReceive;
    }

    public Energy setMaxReceive(long maxReceive) {
        this.maxReceive = maxReceive;
        return this;
    }

    @Override
    public int getEnergyStored() {
        return Safe.integer(this.stored);
    }

    @Override
    public int getMaxEnergyStored() {
        return Safe.integer(this.capacity);
    }

    @Override
    public boolean canExtract() {
        return this.maxExtract > 0 && !isEmpty();
    }

    @Override
    public boolean canReceive() {
        return this.maxReceive > 0 && !isFull();
    }

    public int toPixels(int total) {
        int i = (int) (((float) this.stored / this.capacity) * total);
        return !isEmpty() && i < 1 ? 1 : i;
    }

    public boolean hasEnergy() {
        return !isEmpty();
    }

    public boolean isEmpty() {
        return this.capacity > 0 && this.stored <= 0;
    }

    public boolean isFull() {
        return this.stored > 0 && this.stored >= this.capacity;
    }

    public static class Item extends Energy {
        private final ItemStack stack;

        public Item(ItemStack stack, Item energy) {
            super(energy);
            this.stack = stack;
        }

        public Item(ItemStack stack, long capacity, long maxExtract, long maxReceive) {
            super(capacity, maxExtract, maxReceive);
            this.stack = stack;
            readStored(Stack.getTagOrEmpty(stack).getCompound(Data.TAG_TE_STORABLE));
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int energy = super.receiveEnergy(maxReceive, simulate);
            if (!simulate) {
                writeStored(this.stack.getOrCreateChildTag(Data.TAG_TE_STORABLE));
            }
            return energy;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int energy = super.extractEnergy(maxExtract, simulate);
            if (!simulate) {
                writeStored(this.stack.getOrCreateChildTag(Data.TAG_TE_STORABLE));
            }
            return energy;
        }

        public static class Provider implements ICapabilityProvider {
            private final ItemStack stack;
            private final long capacity;
            private final long maxExtract;
            private final long maxReceive;

            public Provider(ItemStack stack, long capacity, long maxExtract, long maxReceive) {
                this.stack = stack;
                this.capacity = capacity;
                this.maxExtract = maxExtract;
                this.maxReceive = maxReceive;
            }

            @Override
            public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
                return cap == CapabilityEnergy.ENERGY ? LazyOptional.of(() -> {
                    return new Energy.Item(this.stack, this.capacity, this.maxExtract, this.maxReceive);
                }).cast() : LazyOptional.empty();
            }
        }
    }

    public static int extract(ItemStack stack, long energy, boolean simulate) {
        return get(stack).orElse(EMPTY).extractEnergy(Safe.integer(energy), simulate);
    }

    public static int receive(ItemStack stack, long energy, boolean simulate) {
        return get(stack).orElse(EMPTY).receiveEnergy(Safe.integer(energy), simulate);
    }

    public static int getStored(ItemStack stack) {
        return get(stack).orElse(EMPTY).getEnergyStored();
    }

    public static void ifPresent(ItemStack stack, NonNullConsumer<? super IEnergyStorage> consumer) {
        get(stack).ifPresent(consumer);
    }

    public static boolean isPresent(ItemStack stack) {
        return get(stack).isPresent();
    }

    public static LazyOptional<IEnergyStorage> get(ItemStack stack) {
        return stack.getCapability(CapabilityEnergy.ENERGY, null);
    }

    public static int extract(@Nullable TileEntity tile, Direction direction, long energy, boolean simulate) {
        return tile == null ? 0 : get(tile, direction).orElse(EMPTY).extractEnergy(Safe.integer(energy), simulate);
    }

    public static int receive(@Nullable TileEntity tile, Direction direction, long energy, boolean simulate) {
        return tile == null ? 0 : get(tile, direction).orElse(EMPTY).receiveEnergy(Safe.integer(energy), simulate);
    }

    public static void ifPresent(@Nullable TileEntity tile, @Nullable Direction direction, NonNullConsumer<? super IEnergyStorage> consumer) {
        get(tile, direction).ifPresent(consumer);
    }

    public static boolean isPresent(@Nullable TileEntity tile, @Nullable Direction direction) {
        return get(tile, direction).isPresent();
    }

    public static LazyOptional<IEnergyStorage> get(@Nullable TileEntity tile, @Nullable Direction direction) {
        return tile == null ? LazyOptional.empty() : tile.getCapability(CapabilityEnergy.ENERGY, direction != null ? direction.getOpposite() : null);
    }

    public static boolean canExtract(@Nullable TileEntity tile, @Nullable Direction direction) {
        return tile != null && get(tile, direction).orElse(EMPTY).canExtract();

    }

    public static boolean canReceive(@Nullable TileEntity tile, @Nullable Direction direction) {
        return tile != null && get(tile, direction).orElse(EMPTY).canReceive();
    }
}
