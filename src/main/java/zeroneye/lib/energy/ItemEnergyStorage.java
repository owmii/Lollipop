package zeroneye.lib.energy;

import net.minecraft.item.ItemStack;

public class ItemEnergyStorage extends EnergyStorage {
    private final ItemStack stack;

    public ItemEnergyStorage(ItemStack stack) {
        this(stack, 0, 0);
    }

    public ItemEnergyStorage(ItemStack stack, int capacity) {
        this(stack, capacity, capacity);
    }

    public ItemEnergyStorage(ItemStack stack, int capacity, int transfer) {
        super(capacity, transfer, transfer);
        this.stack = stack;
        if (stack.getTag() != null) {
            this.energy = stack.getTag().getInt(TAG_ENERGEY_STORED);
        }
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive())
            return 0;

        int energy = super.receiveEnergy(maxReceive, simulate);
        if (energy > 0 && !simulate) {
            this.stack.getOrCreateTag().putInt(TAG_ENERGEY_STORED, getEnergyStored());
        }
        return energy;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract())
            return 0;

        int energy = super.extractEnergy(maxExtract, simulate);
        if (energy > 0 && !simulate) {
            this.stack.getOrCreateTag().putInt(TAG_ENERGEY_STORED, getEnergyStored());
        }
        return energy;
    }
}
