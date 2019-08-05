package xieao.lib.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nullable;
import java.util.UUID;

public class PlayerUtil {

    public static boolean isFake(PlayerEntity player) {
        return player instanceof FakePlayer;
    }

    @Nullable
    public static ServerPlayerEntity get(UUID uuid) {
        return ServerUtil.getServer().getPlayerList().getPlayerByUUID(uuid);
    }

    @Nullable
    public static ServerPlayerEntity get(String name) {
        return ServerUtil.getServer().getPlayerList().getPlayerByUsername(name);
    }
}
