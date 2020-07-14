package owmii.lib.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import owmii.lib.block.IBlock;
import owmii.lib.block.IVariant;
import owmii.lib.client.renderer.item.TEItemRenderer;

import javax.annotation.Nullable;

public class ItemBlock<V extends IVariant, B extends Block & IBlock<V>> extends BlockItem implements IItem {
    private final B block;

    @SuppressWarnings("ConstantConditions")
    public ItemBlock(B block, Properties builder, @Nullable ItemGroup group) {
        super(block, builder.group(group).setISTER(() -> TEItemRenderer::new));
        this.block = block;
    }

    @Override
    public B getBlock() {
        return this.block;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderByItem(ItemStack stack, MatrixStack matrix, IRenderTypeBuffer rtb, int light, int ov) {
        getBlock().renderByItem(stack, matrix, rtb, light, ov);
    }
}
