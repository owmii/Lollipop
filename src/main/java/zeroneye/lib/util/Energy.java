package zeroneye.lib.util;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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

    public static int receive(World world, BlockPos pos, Direction direction, int energy, boolean simulate) {
        TileEntity tile = world.getTileEntity(pos);
        return receive(tile, direction, energy, simulate);
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

    public static int extract(World world, BlockPos pos, Direction direction, int energy, boolean simulate) {
        TileEntity tile = world.getTileEntity(pos);
        return extract(tile, direction, energy, simulate);
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

    public static boolean hasEnergy(ItemStack stack) {
        return getForgeEnergy(stack).isPresent();
    }

    public static LazyOptional<IEnergyStorage> getForgeEnergy(ItemStack stack) {
        return stack.getCapability(CapabilityEnergy.ENERGY, null);
    }

    public static boolean hasEnergy(World world, BlockPos pos, @Nullable Direction direction) {
        return getForgeEnergy(world, pos, direction).isPresent();
    }

    public static boolean hasEnergy(@Nullable TileEntity tile, @Nullable Direction direction) {
        return getForgeEnergy(tile, direction).isPresent();
    }

    public static LazyOptional<IEnergyStorage> getForgeEnergy(World world, BlockPos pos, @Nullable Direction direction) {
        return getForgeEnergy(world.getTileEntity(pos), direction);
    }

    public static LazyOptional<IEnergyStorage> getForgeEnergy(@Nullable TileEntity tile, @Nullable Direction direction) {
        return tile == null ? LazyOptional.empty() : tile.getCapability(CapabilityEnergy.ENERGY, direction != null ? direction.getOpposite() : null);
    }
}
