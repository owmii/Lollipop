package owmii.lib.util;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class Stack {
    public static String path(ItemStack provider) {
        return location(provider).getPath();
    }

    public static String modId(ItemStack provider) {
        return location(provider).getNamespace();
    }

    public static ResourceLocation location(ItemStack stack) {
        return location(stack.getItem());
    }

    public static String path(IItemProvider provider) {
        return location(provider).getPath();
    }

    public static String modId(IItemProvider provider) {
        return location(provider).getNamespace();
    }

    @SuppressWarnings("ConstantConditions")
    public static ResourceLocation location(IItemProvider provider) {
        return ForgeRegistries.ITEMS.getKey(provider.asItem());
    }

    public static boolean orEquals(ItemStack stack, ItemStack... stacks) {
        for (ItemStack stack1 : stacks) {
            if (stack.equals(stack1)) {
                return true;
            }
        }
        return false;
    }
}
