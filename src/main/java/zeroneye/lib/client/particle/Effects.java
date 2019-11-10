package zeroneye.lib.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import zeroneye.lib.util.math.V3d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(Dist.CLIENT)
public final class Effects {
    public static final Effects INSTANCE = new Effects();
    private final List<Effect> effects = new ArrayList<>();
    private final Minecraft mc = Minecraft.getInstance();

    public static Effect create(Effect.Texture texture, World world, V3d origin) {
        return new Effect(texture, world, origin);
    }

    public void spawn(Effect effect) {
        if (this.mc.currentScreen == null || !this.mc.currentScreen.isPauseScreen()) {
            this.effects.add(effect);
        }
    }

    @SubscribeEvent
    public static void tickParticles(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.currentScreen == null || !mc.currentScreen.isPauseScreen()) {
                Iterator<Effect> iterator = INSTANCE.effects.iterator();
                while (iterator.hasNext()) {
                    Effect particle = iterator.next();
                    particle.tick();
                    if (!particle.isAlive()) {
                        iterator.remove();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void renderParticles(RenderWorldLastEvent event) {
        ActiveRenderInfo renderInfo = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
        float f0 = MathHelper.cos(renderInfo.getYaw() * ((float) Math.PI / 180F));
        float f1 = MathHelper.sin(renderInfo.getYaw() * ((float) Math.PI / 180F));
        float f2 = -f1 * MathHelper.sin(renderInfo.getPitch() * ((float) Math.PI / 180F));
        float f3 = f0 * MathHelper.sin(renderInfo.getPitch() * ((float) Math.PI / 180F));
        float f4 = MathHelper.cos(renderInfo.getPitch() * ((float) Math.PI / 180F));
        Particle.interpPosX = renderInfo.getProjectedView().x;
        Particle.interpPosY = renderInfo.getProjectedView().y;
        Particle.interpPosZ = renderInfo.getProjectedView().z;
        for (int i = 0; i < INSTANCE.effects.size(); i++) {
            Effect particle = INSTANCE.effects.get(i);
            GlStateManager.depthMask(!particle.shouldDisableDepth());
            GlStateManager.enableBlend();
            particle.render(event.getPartialTicks(), f0, f4, f1, f2, f3);
            GlStateManager.disableBlend();
        }
        GlStateManager.depthMask(true);
        GlStateManager.alphaFunc(516, 0.1F);
    }
}