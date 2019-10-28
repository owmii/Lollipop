package xieao.lib.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

public class Server {
    public static <T extends WorldSavedData> T getData(Supplier<T> supplier) {
        return getData(supplier, 0);
    }

    @SuppressWarnings("unchecked")
    public static <T extends WorldSavedData> T getData(Supplier<T> supplier, int dim) {
        Optional<ServerWorld> world = Server.getWorld(dim);
        final T[] data = (T[]) new WorldSavedData[]{supplier.get()};
        world.ifPresent(serverWorld -> {
            DimensionSavedDataManager dataManager = serverWorld.getSavedData();
            data[0] = dataManager.get(supplier, supplier.get().getName());
            if (data[0] == null) {
                data[0] = supplier.get();
                data[0].markDirty();
                dataManager.set(data[0]);
            }
        });
        return data[0];
    }

    public static Optional<ServerWorld> getWorld(int dimId) {
        return getWorld(DimensionType.getById(dimId));
    }

    public static Optional<ServerWorld> getWorld(ResourceLocation dimName) {
        return getWorld(DimensionType.byName(dimName));
    }

    public static Optional<ServerWorld> getWorld(@Nullable DimensionType dim) {
        return dim == null ? Optional.empty() : Optional.ofNullable(DimensionManager.getWorld(get(), dim, false, false));
    }

    public static boolean hasPlayers() {
        return !get().getPlayerList().getPlayers().isEmpty();
    }

    public static MinecraftServer get() {
        return LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
    }
}