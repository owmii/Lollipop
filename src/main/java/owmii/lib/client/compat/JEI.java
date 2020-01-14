//package owmii.lib.client.compat;
//
//import mezz.jei.api.IModPlugin;
//import mezz.jei.api.JeiPlugin;
//import mezz.jei.api.recipe.IFocus;
//import mezz.jei.api.recipe.IRecipeManager;
//import mezz.jei.api.runtime.IJeiRuntime;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.ResourceLocation;
//import net.minecraftforge.fml.ModList;
//import owmii.lib.Lollipop;
//
//import javax.annotation.Nullable;
//
//@JeiPlugin
//public class JEI implements IModPlugin {
//    public static final String ID = "jei";
//    private static int loaded;
//
//    @Nullable
//    private static IJeiRuntime runtime = null;
//
//    public static boolean isLoaded() {
//        if (loaded == 0) loaded = ModList.get().isLoaded(ID) ? 1 : -1;
//        return loaded == 1;
//    }
//
//    @Override
//    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
//        JEI.runtime = jeiRuntime;
//    }
//
//    public static void showRecipes(ItemStack stack) {
//        if (JEI.runtime != null) {
//            IRecipeManager register = JEI.runtime.getRecipeManager();
//            IFocus<?> focus = register.createFocus(IFocus.Mode.OUTPUT, stack);
//            JEI.runtime.getRecipesGui().show(focus);
//        }
//    }
//
//    public static void showUsage(ItemStack stack) {
//        if (JEI.runtime != null) {
//            IRecipeManager register = JEI.runtime.getRecipeManager();
//            IFocus<?> focus = register.createFocus(IFocus.Mode.INPUT, stack);
//            JEI.runtime.getRecipesGui().show(focus);
//        }
//    }
//
//    @Override
//    public ResourceLocation getPluginUid() {
//        return new ResourceLocation(Lollipop.MOD_ID, "main");
//    }
//}
