package zeroneye.lib.client.renderer.item;

import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zeroneye.lib.item.IItemBase;

import java.util.HashSet;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class TEItemRenderer extends ItemStackTileEntityRenderer {
    private static final Set<IItemBase> SET = new HashSet<>();

    @Override
    public void renderByItem(ItemStack stack) {
        final Item item = stack.getItem();
        if (item instanceof IItemBase) {
            IItemBase base = (IItemBase) item;
            if (SET.contains(base)) {
                base.renderByItem(stack);
            }
        }
    }

    public static void register(IItemBase base) {
        SET.add(base);
    }
}
