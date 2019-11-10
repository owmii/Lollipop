package zeroneye.lib.item;

import net.minecraft.item.Item;
import zeroneye.lib.client.renderer.item.TEItemRenderer;

public class ItemBase extends Item implements IItemBase {
    public ItemBase(Properties properties) {
        super(properties.setTEISR(() -> TEItemRenderer::new));
    }
}
