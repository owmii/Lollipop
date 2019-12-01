package zeroneye.lib.client.handler;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IHud {
    @OnlyIn(Dist.CLIENT)
    boolean renderHud(BlockState state, World world, PlayerEntity player, Hand hand, BlockRayTraceResult blockRayTraceResult);
}
