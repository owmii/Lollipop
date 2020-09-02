package owmii.lib.logistics.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.items.IItemHandler;

import java.util.function.Predicate;

public class SidedHopper {
    private final Hopper[] hoppers = new Hopper[6];
    private final IItemHandler inv;

    public SidedHopper(IItemHandler inv) {
        this.inv = inv;
        for (int i = 0; i < this.hoppers.length; i++) {
            this.hoppers[i] = new Hopper(inv);
        }
    }

    public void read(CompoundNBT nbt, String key) {
        for (int i = 0; i < this.hoppers.length; i++) {
            Direction side = Direction.values()[i];
            this.hoppers[i].read(nbt, key + "_" + side.getName2());
        }
    }

    public CompoundNBT write(CompoundNBT nbt, String key) {
        for (int i = 0; i < this.hoppers.length; i++) {
            Direction side = Direction.values()[i];
            this.hoppers[i].write(nbt, key + "_" + side.getName2());
        }
        return nbt;
    }

    public void transfer(Direction side, IItemHandler to, int max, Predicate<ItemStack> pull, Predicate<ItemStack> push, int... ex) {
        pull(side, to, max, pull, ex);
        push(side, to, max, push, ex);
    }

    public void push(Direction side, IItemHandler to, int max, Predicate<ItemStack> predicate, int... ex) {
        getHopper(side).push(to, max, predicate, ex);
    }

    public void pull(Direction side, IItemHandler from, int max, Predicate<ItemStack> predicate, int... ex) {
        getHopper(side).pull(from, max, predicate, ex);
    }

    public void switchPull(Direction side) {
        getHopper(side).switchPull();
    }

    public void switchPush(Direction side) {
        getHopper(side).switchPush();
    }

    public boolean canPull(Direction side) {
        return getHopper(side).canPull();
    }

    public boolean canPush(Direction side) {
        return getHopper(side).canPush();
    }

    public void setPull(Direction side, boolean pull) {
        getHopper(side).setPull(pull);
    }

    public void setPush(Direction side, boolean push) {
        getHopper(side).setPush(push);
    }

    public Hopper getHopper(Direction side) {
        return this.hoppers[side.getIndex()];
    }

    public Hopper[] getHoppers() {
        return this.hoppers;
    }
}
