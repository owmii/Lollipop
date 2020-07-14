package owmii.lib.block;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeBlock;
import owmii.lib.item.ItemBlock;

import javax.annotation.Nullable;

public interface IBlock<V extends IVariant> extends IForgeBlock {
    @SuppressWarnings("unchecked")
    default ItemBlock getBlockItem(Item.Properties properties, @Nullable ItemGroup group) {
        return new ItemBlock(getBlock(), properties, group);
    }

    @Override
    default boolean hasTileEntity(BlockState state) {
        return createTileEntity(state, null) != null;
    }

    @OnlyIn(Dist.CLIENT)
    default void renderByItem(ItemStack stack, MatrixStack matrix, IRenderTypeBuffer rtb, int light, int ov) {
    }
}
