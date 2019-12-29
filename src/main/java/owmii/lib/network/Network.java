package owmii.lib.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import owmii.lib.Lollipop;
import owmii.lib.util.Server;

import static net.minecraftforge.fml.network.NetworkDirection.PLAY_TO_CLIENT;

public class Network {
    private static final ResourceLocation ID = new ResourceLocation(Lollipop.MOD_ID, "main");
    private static final SimpleChannel CHANNEL;
    private static int id;

    @SuppressWarnings("unchecked")
    public <T> void register(IPacket<T> message) {
        CHANNEL.registerMessage(id++, (Class<T>) message.getClass(), message::encode, message::decode, message::handle);
    }

    @OnlyIn(Dist.CLIENT)
    public <T> void toServer(T msg) {
        CHANNEL.sendToServer(msg);
    }

    public <T> void toAll(T msg) {
        Server.get().getPlayerList().getPlayers().forEach(serverPlayerEntity -> {
            toClient(msg, serverPlayerEntity);
        });
    }

    public <T> void toClient(T msg, PlayerEntity player) {
        if (player instanceof ServerPlayerEntity) {
            CHANNEL.sendTo(msg, ((ServerPlayerEntity) player).connection.getNetworkManager(), PLAY_TO_CLIENT);
        }
    }

    static {
        CHANNEL = NetworkRegistry.ChannelBuilder.named(ID)
                .clientAcceptedVersions("1"::equals)
                .serverAcceptedVersions("1"::equals)
                .networkProtocolVersion(() -> "1")
                .simpleChannel();
    }
}
