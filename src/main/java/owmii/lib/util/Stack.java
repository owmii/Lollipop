package owmii.lib.util;

import net.minecraft.item.ItemStack;

public class Stack {
    public static boolean orEquals(ItemStack stack, ItemStack... stacks) {
        for (ItemStack stack1 : stacks) {
            if (stack.equals(stack1)) {
                return true;
            }
        }
        return false;
    }
}
