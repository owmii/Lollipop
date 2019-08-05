package xieao.lib.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

import javax.annotation.Nullable;

public class ServerUtil {

    @Nullable
    public static ServerWorld getWorld(int dimId) {
        return getWorld(DimensionType.getById(dimId));
    }

    @Nullable
    public static ServerWorld getWorld(ResourceLocation dimName) {
        return getWorld(DimensionType.byName(dimName));
    }

    @Nullable
    public static ServerWorld getWorld(@Nullable DimensionType dim) {
        return dim == null ? null : DimensionManager.getWorld(getServer(), dim, false, false);
    }

    public static MinecraftServer getServer() {
        return LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
    }
}
