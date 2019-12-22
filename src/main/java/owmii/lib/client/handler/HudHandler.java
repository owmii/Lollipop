package owmii.lib.client.handler;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(Dist.CLIENT)
public class HudHandler {

    @SubscribeEvent
    public static void renderHud(RenderGameOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL && mc.currentScreen == null) {
            PlayerEntity player = mc.player;
            World world = mc.world;
            RayTraceResult hit = mc.objectMouseOver;
            if (hit instanceof BlockRayTraceResult) {
                BlockRayTraceResult result = (BlockRayTraceResult) hit;
                BlockPos pos = result.getPos();
                Vec3d sHit = result.getHitVec();
                BlockState state = world.getBlockState(pos);
                if (state.getBlock() instanceof IHud) {
                    for (Hand hand : Hand.values()) {
                        if (((IHud) state.getBlock()).renderHud(state, world, player, hand, result)) {
                            break;
                        }
                    }
                }
            }
        }
    }
}
