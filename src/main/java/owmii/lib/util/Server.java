package owmii.lib.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@Mod.EventBusSubscriber
public class Server {
    public static long ticks;

    @SubscribeEvent
    public static void tick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ticks++;
        }
    }

    public static void chatToAll(BiConsumer<PlayerEntity, List<ITextComponent>> biConsumer) {
        List<ITextComponent> textComponents = new ArrayList<>();
        get().getPlayerList().getPlayers().forEach(player -> {
            biConsumer.accept(player, textComponents);
            for (ITextComponent textComponent : textComponents) {
                player.sendMessage(textComponent);
            }
        });
    }

    public static <T extends WorldSavedData> T getData(Supplier<T> supplier) {
        return getData(supplier, 0);
    }

    public static <T extends WorldSavedData> T getData(Supplier<T> supplier, Dimension dim) {
        return getData(supplier, dim.getType());
    }

    public static <T extends WorldSavedData> T getData(Supplier<T> supplier, DimensionType dim) {
        return getData(supplier, dim.getId());
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

    public static boolean isSinglePlayer() {
        return !isMultiPlayer();
    }

    public static boolean isMultiPlayer() {
        return get().isDedicatedServer();
    }

    public static MinecraftServer get() {
        return LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
    }
}