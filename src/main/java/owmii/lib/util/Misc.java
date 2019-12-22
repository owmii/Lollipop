package owmii.lib.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class Misc {
    public static boolean canBlockSeeSky(World world, BlockPos pos, @Nullable Block block) {
        BlockPos blockpos = new BlockPos(pos.getX(), world.getSeaLevel(), pos.getZ());
        for (BlockPos blockpos1 = blockpos.down(); blockpos1.getY() > pos.getY(); blockpos1 = blockpos1.down()) {
            BlockState blockstate = world.getBlockState(blockpos1);
            if ((block != null && block != blockstate.getBlock()) && blockstate.getOpacity(world, blockpos1) > 0 && !blockstate.getMaterial().isLiquid()) {
                return false;
            }
        }
        return true;
    }
}
