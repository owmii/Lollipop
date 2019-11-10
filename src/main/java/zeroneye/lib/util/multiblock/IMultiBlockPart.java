package zeroneye.lib.util.multiblock;

import net.minecraft.util.math.BlockPos;
import zeroneye.lib.block.TileBase;

import javax.annotation.Nullable;

public interface IMultiBlockPart<T extends TileBase & IMultiBlockBuilder> {
    boolean isLinked();

    void setLinked(boolean linked);

    @Nullable
    BlockPos getBuilderPos();

    void setBuilderPos(@Nullable BlockPos pos);

    @Nullable
    T getBuilder();
}
