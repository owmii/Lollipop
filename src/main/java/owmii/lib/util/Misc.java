package owmii.lib.util;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Misc {
    public static boolean canBlockSeeSky(World world, BlockPos pos) {
        BlockPos blockpos = new BlockPos(pos.getX(), world.getSeaLevel(), pos.getZ());
        for (BlockPos down = blockpos.down(); down.getY() > pos.getY(); down = down.down()) {
            BlockState blockstate = world.getBlockState(down);
            if (blockstate.getOpacity(world, down) > 0 && !blockstate.getMaterial().isLiquid()) {
                return false;
            }
        }
        return true;
    }

    public static BlockPos getPos(Entity entity) {
        return new BlockPos(entity.getPosX(), entity.getPosY(), entity.getPosZ());
    }
}
