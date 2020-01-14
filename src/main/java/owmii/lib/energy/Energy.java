package owmii.lib.energy;

import net.minecraftforge.energy.IEnergyStorage;

public class Energy implements IEnergyStorage {
    private long capacity;
    private long stored;
    private long maxExtract;
    private long maxReceive;

    public Energy(Energy energy) {
        this(energy.capacity, energy.maxExtract, energy.maxReceive);
    }

    Energy(long capacity, long maxExtract, long maxReceive) {
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

    public long grow(long amount) {
        return Math.min(this.stored + amount, this.capacity);
    }

    public long shrink(long amount) {
        return Math.max(this.stored - amount, 0);
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
        return this.stored > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) this.stored;
    }

    @Override
    public int getMaxEnergyStored() {
        return this.capacity > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) this.capacity;
    }

    @Override
    public boolean canExtract() {
        return this.maxExtract > 0;
    }

    @Override
    public boolean canReceive() {
        return this.maxReceive > 0;
    }
}
