package owmii.lib.client.renderer.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import owmii.lib.item.IItemBase;

import java.util.HashSet;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class TEItemRenderer extends ItemStackTileEntityRenderer {
    private static final Set<Item> ITEMS = new HashSet<>();

    @Override
    public void render(ItemStack p_228364_1_, MatrixStack p_228364_2_, IRenderTypeBuffer p_228364_3_, int p_228364_4_, int p_228364_5_) {
        super.render(p_228364_1_, p_228364_2_, p_228364_3_, p_228364_4_, p_228364_5_);
    }

    // @Override TODO
    public void renderByItem(ItemStack stack) {
        final Item item = stack.getItem();
        if (item instanceof IItemBase) {
            IItemBase base = (IItemBase) item;
            if (ITEMS.contains(base)) {
                base.renderByItem(stack);
            }
        }
    }

    public static void register(Block... blocks) {
        for (Block block : blocks) {
            Item item = block.asItem();
            if (item instanceof IItemBase) {
                ITEMS.add(item);
            }
        }
    }

    public static void register(Item... items) {
        for (Item item : items) {
            if (item instanceof IItemBase) {
                ITEMS.add(item);
            }
        }
    }
}
