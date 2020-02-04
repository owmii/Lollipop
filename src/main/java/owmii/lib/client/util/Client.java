package owmii.lib.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

import java.util.Optional;

public class Client {
    public static Optional<ClientPlayerEntity> player() {
        return Optional.ofNullable(get().player);
    }

    public static Optional<ClientWorld> world() {
        return Optional.ofNullable(get().world);
    }

    public static Minecraft get() {
        return Minecraft.getInstance();
    }
}
