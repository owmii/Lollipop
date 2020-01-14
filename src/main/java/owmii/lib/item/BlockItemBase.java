package owmii.lib.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import owmii.lib.block.IBlock;
import owmii.lib.client.renderer.item.TEItemRenderer;

import javax.annotation.Nullable;

public class BlockItemBase extends BlockItem implements IItemBase {
    @SuppressWarnings("ConstantConditions")
    public BlockItemBase(Block block, Properties properties, @Nullable ItemGroup group) {
        super(block, properties.group(block instanceof IBlock && ((IBlock) block).hideGroup() ? null : group)
                .setTEISR(() -> TEItemRenderer::new).maxStackSize(((IBlock) block).stackSize()));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderByItem(ItemStack stack) {
        if (getBlock() instanceof IBlock) {
            ((IBlock) getBlock()).renderByItem(stack);
        }
    }
}
