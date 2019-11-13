package zeroneye.lib.energy;

import net.minecraftforge.energy.IEnergyStorage;
import zeroneye.lib.Lollipop;

public class EnergyStorage implements IEnergyStorage {
    public static final String TAG_ENERGEY_STORED = Lollipop.MOD_ID + "/EnergyStored";
    protected int energy;
    protected int capacity;
    protected int maxReceive;
    protected int maxExtract;

    public EnergyStorage() {
        this(0, 0, 0, 0);
    }

    public EnergyStorage(int capacity) {
        this(capacity, capacity, capacity, 0);
    }

    public EnergyStorage(int capacity, int maxTransfer) {
        this(capacity, maxTransfer, maxTransfer, 0);
    }

    public EnergyStorage(int capacity, int maxReceive, int maxExtract) {
        this(capacity, maxReceive, maxExtract, 0);
    }

    public EnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.energy = Math.max(0, Math.min(capacity, energy));
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive())
            return 0;

        int energyReceived = Math.min(getMaxEnergyStored() - getEnergyStored(), Math.min(getMaxReceive(), maxReceive));
        if (!simulate)
            setEnergy(getEnergyStored() + energyReceived);
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract())
            return 0;

        int energyExtracted = Math.min(getEnergyStored(), Math.min(getMaxExtract(), maxExtract));
        if (!simulate)
            setEnergy(getEnergyStored() - energyExtracted);
        return energyExtracted;
    }

    @Override
    public int getEnergyStored() {
        return energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return this.maxExtract > 0;
    }

    @Override
    public boolean canReceive() {
        return this.maxReceive > 0;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getMaxReceive() {
        return maxReceive;
    }

    public void setMaxReceive(int maxReceive) {
        this.maxReceive = maxReceive;
    }

    public int getMaxExtract() {
        return maxExtract;
    }

    public void setMaxExtract(int maxExtract) {
        this.maxExtract = maxExtract;
    }
}
