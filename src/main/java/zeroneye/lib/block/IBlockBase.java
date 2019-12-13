package zeroneye.lib.block;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeBlock;
import zeroneye.lib.item.BlockItemBase;

import javax.annotation.Nullable;

public interface IBlockBase extends IForgeBlock {
    default BlockItemBase getBlockItem(Item.Properties properties, @Nullable ItemGroup group) {
        return new BlockItemBase(getBlock(), properties, group);
    }

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
    default void renderByItem(ItemStack stack) {
    }
}
