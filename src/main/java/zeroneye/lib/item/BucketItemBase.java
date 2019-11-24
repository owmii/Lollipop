package zeroneye.lib.item;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;

public class BucketItemBase extends BucketItem implements IItemBase {
    public BucketItemBase(Fluid containedFluidIn, Properties builder) {
        super(containedFluidIn, builder);
    }
}
