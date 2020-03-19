package owmii.lib.util;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class Recipe {
    public static <T extends IRecipe<?>> IRecipeType<T> register(ResourceLocation key) {
        return Registry.register(Registry.RECIPE_TYPE, key, new IRecipeType<T>() {
            public String toString() {
                return key.toString();
            }
        });
    }

    public static Collection<? extends IRecipe<?>> getAll(@Nullable World world, IRecipeType<?> type) {
        if (world != null) {
            RecipeManager manager = world.getRecipeManager();
            return manager.getRecipes().stream().filter(r -> r.getType() == type).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public static boolean matchTags(ItemStack stack, ItemStack other) {
        return matchTags(stack, other, true);
    }

    public static boolean matchTags(ItemStack stack, ItemStack other, boolean matchBlock) {
        Item item = stack.getItem();
        Item item1 = stack.getItem();
        if (matchBlock && item instanceof BlockItem && item1 instanceof BlockItem) {
            return matchTags(((BlockItem) item).getBlock(), ((BlockItem) item1).getBlock());
        }
        return matchTags(item, item1);
    }

    public static boolean matchTags(Block block, Block other) {
        for (ResourceLocation location : block.getTags()) {
            if (matchTags(block, location)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matchTags(Block block, ResourceLocation key) {
        for (ResourceLocation location : block.getTags()) {
            if (location.equals(key)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matchTags(Item item, Item other) {
        for (ResourceLocation location : item.getTags()) {
            if (matchTags(item, location)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matchTags(Item item, ResourceLocation key) {
        for (ResourceLocation location : item.getTags()) {
            if (location.equals(key)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matchTags(FluidStack fluid, FluidStack other) {
        return matchTags(fluid.getFluid(), other.getFluid());
    }

    public static boolean matchTags(Fluid fluid, Fluid other) {
        for (ResourceLocation location : fluid.getTags()) {
            if (matchTags(fluid, location)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matchTags(Fluid fluid, ResourceLocation key) {
        for (ResourceLocation location : fluid.getTags()) {
            if (location.equals(key)) {
                return true;
            }
        }
        return false;
    }
}
