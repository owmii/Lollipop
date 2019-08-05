package xieao.lib.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeItem;
import xieao.lib.client.renderer.item.TEItemRenderer;

public interface IItemBase extends IForgeItem {
    @OnlyIn(Dist.CLIENT)
    default void renderByItem(ItemStack stack) {
    }

    @SuppressWarnings("unchecked")
    default <T extends Item & IItemBase> T setTEItem() {
        TEItemRenderer.register(this);
        return (T) getItem();
    }
}
