package zeroneye.lib.util;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public class Energy {
    public static int receive(ItemStack stack, int energy, boolean simulate) {
        final int[] i = {0};
        getForgeEnergy(stack).ifPresent(iEnergyStorage -> {
            i[0] = iEnergyStorage.receiveEnergy(energy, simulate);
        });
        return i[0];
    }

    public static int extract(ItemStack stack, int energy, boolean simulate) {
        final int[] i = {0};
        getForgeEnergy(stack).ifPresent(iEnergyStorage -> {
            i[0] = iEnergyStorage.extractEnergy(energy, simulate);
        });
        return i[0];
    }

    public static int getStored(ItemStack stack) {
        final int[] i = {0};
        getForgeEnergy(stack).ifPresent(iEnergyStorage -> {
            i[0] = iEnergyStorage.getEnergyStored();
        });
        return i[0];
    }

    public static int receive(@Nullable TileEntity tile, Direction direction, int energy, boolean simulate) {
        final int[] i = {0};
        if (tile != null) {
            getForgeEnergy(tile, direction).ifPresent(iEnergyStorage -> {
                i[0] = iEnergyStorage.receiveEnergy(energy, simulate);
            });
        }
        return i[0];
    }

    public static int extract(@Nullable TileEntity tile, Direction direction, int energy, boolean simulate) {
        final int[] i = {0};
        if (tile != null) {
            getForgeEnergy(tile, direction).ifPresent(iEnergyStorage -> {
                i[0] = iEnergyStorage.extractEnergy(energy, simulate);
            });
        }
        return i[0];
    }

    public static boolean isPresent(ItemStack stack) {
        return getForgeEnergy(stack).isPresent();
    }

    public static LazyOptional<IEnergyStorage> getForgeEnergy(ItemStack stack) {
        return stack.getCapability(CapabilityEnergy.ENERGY, null);
    }

    public static boolean isPresent(@Nullable TileEntity tile, @Nullable Direction direction) {
        return getForgeEnergy(tile, direction).isPresent();
    }

    public static boolean canExtract(@Nullable TileEntity tile, @Nullable Direction direction) {
        final boolean[] flag = {false};
        if (tile != null) {
            getForgeEnergy(tile, direction).ifPresent(storage -> {
                flag[0] = storage.canExtract() && storage.extractEnergy(Integer.MAX_VALUE, true) > 0;
            });
        }
        return flag[0];
    }

    public static boolean canReceive(@Nullable TileEntity tile, @Nullable Direction direction) {
        final boolean[] flag = {false};
        if (tile != null) {
            getForgeEnergy(tile, direction).ifPresent(storage -> {
                flag[0] = storage.canReceive() && storage.receiveEnergy(Integer.MAX_VALUE, true) > 0;
            });
        }
        return flag[0];
    }

    public static int maxExtract(@Nullable TileEntity tile, @Nullable Direction direction) {
        final int[] i = {0};
        if (tile != null) {
            getForgeEnergy(tile, direction).ifPresent(storage -> {
                i[0] = storage.extractEnergy(Integer.MAX_VALUE, true);
            });
        }
        return i[0];
    }

    public static int maxReceive(@Nullable TileEntity tile, @Nullable Direction direction) {
        final int[] i = {0};
        if (tile != null) {
            getForgeEnergy(tile, direction).ifPresent(storage -> {
                i[0] = storage.receiveEnergy(Integer.MAX_VALUE, true);
            });
        }
        return i[0];
    }

    public static LazyOptional<IEnergyStorage> getForgeEnergy(@Nullable TileEntity tile, @Nullable Direction direction) {
        return tile == null ? LazyOptional.empty() : tile.getCapability(CapabilityEnergy.ENERGY, direction != null ? direction.getOpposite() : null);
    }
}
