package owmii.lib.logistics.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class Hopper {
    private final IItemHandler inv;
    private boolean push;
    private boolean pull;

    public Hopper(IItemHandler inv) {
        this.inv = inv;
    }

    public void read(CompoundNBT nbt, String key) {
        this.push = nbt.getBoolean("push_" + key);
        this.pull = nbt.getBoolean("pull_" + key);
    }

    public CompoundNBT write(CompoundNBT nbt, String key) {
        nbt.putBoolean("push_" + key, this.push);
        nbt.putBoolean("pull_" + key, this.pull);
        return nbt;
    }

    public void push(IItemHandler to, int max, Predicate<ItemStack> predicate, int... ex) {
        if (this.push) {
            transfer(this.inv, to, max, predicate, ex);
        }
    }

    public void pull(IItemHandler from, int max, Predicate<ItemStack> predicate, int... ex) {
        if (this.pull) {
            transfer(from, this.inv, max, predicate, ex);
        }
    }

    public void push(IItemHandler to, int max, Predicate<ItemStack> predicate, List<Integer> ex) {
        if (this.push) {
            transfer(this.inv, to, max, predicate, ex);
        }
    }

    public void pull(IItemHandler from, int max, Predicate<ItemStack> predicate, List<Integer> ex) {
        if (this.pull) {
            transfer(from, this.inv, max, predicate, ex);
        }
    }

    protected void transfer(IItemHandler from, IItemHandler to, int max, Predicate<ItemStack> predicate, int... ex) {
        transfer(from, to, max, predicate, Arrays.asList(Arrays.stream(ex).boxed().toArray(Integer[]::new)));
    }

    protected void transfer(IItemHandler from, IItemHandler to, int max, Predicate<ItemStack> predicate, List<Integer> ex) {
        for (int i = 0; i < from.getSlots(); i++) {
            if (ex.contains(i)) continue;
            ItemStack stack = from.extractItem(i, max, true);
            if (!stack.isEmpty() && predicate.test(stack)) {
                ItemStack insert = ItemHandlerHelper.insertItem(to, stack.copy(), false);
                if (!ItemStack.areItemStacksEqual(stack, insert)) {
                    from.extractItem(i, stack.getCount() - insert.getCount(), false);
                    break;
                }
            }
        }
    }

    public void switchPull() {
        setPull(!canPull());
    }

    public void switchPush() {
        setPush(!canPush());
    }

    public boolean canPull() {
        return this.pull;
    }

    public Hopper setPull(boolean pull) {
        this.pull = pull;
        return this;
    }

    public boolean canPush() {
        return this.push;
    }

    public Hopper setPush(boolean push) {
        this.push = push;
        return this;
    }
}
