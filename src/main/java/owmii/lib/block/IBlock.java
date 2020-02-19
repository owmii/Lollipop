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
import owmii.lib.item.BlockItemBase;
import owmii.lib.util.IVariant;

import javax.annotation.Nullable;

public interface IBlock<E extends IVariant> extends IForgeBlock {
    @SuppressWarnings("unchecked")
    default BlockItemBase getBlockItem(Item.Properties properties, @Nullable ItemGroup group) {
        return new BlockItemBase(getBlock(), properties, group);
    }

    E getVariant();

    @Override
    default boolean hasTileEntity(BlockState state) {
        return createTileEntity(state, null) != null;
    }

    default boolean hideGroup() {
        return false;
    }

    default int stackSize() {
        return 64;
    }

    @OnlyIn(Dist.CLIENT)
    default boolean hasEffect(ItemStack stack) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    default void renderByItem(ItemStack stack, MatrixStack matrix, IRenderTypeBuffer rtb, int light, int ov) {
    }

    default ItemStack toStack() {
        return new ItemStack(getBlock());
    }

    default ItemStack toStack(int count) {
        return new ItemStack(getBlock(), count);
    }
}
