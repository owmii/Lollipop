package zeroneye.lib.potion;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;

public class Brew {
    public static void addMix(Potion in, Item item, Potion out) {
        ItemStack potionIn = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), in);
        ItemStack potionOut = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), out);
        ItemStack potionOut2 = PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), out);
        ItemStack potionOut3 = PotionUtils.addPotionToItemStack(new ItemStack(Items.LINGERING_POTION), out);
        BrewingRecipeRegistry.addRecipe(Ingredient.fromStacks(potionIn), Ingredient.fromItems(item), potionOut);
        BrewingRecipeRegistry.addRecipe(Ingredient.fromStacks(potionOut), Ingredient.fromItems(Items.GUNPOWDER), potionOut2);
        BrewingRecipeRegistry.addRecipe(Ingredient.fromStacks(potionOut2), Ingredient.fromItems(Items.DRAGON_BREATH), potionOut3);
    }
}
