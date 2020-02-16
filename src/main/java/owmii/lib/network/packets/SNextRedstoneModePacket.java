package owmii.lib.network.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import owmii.lib.Lollipop;
import owmii.lib.block.TileBase;
import owmii.lib.energy.IRedstoneInteract;
import owmii.lib.network.IPacket;
import owmii.lib.util.Dim;
import owmii.lib.util.Server;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

public class SNextRedstoneModePacket implements IPacket<SNextRedstoneModePacket> {
    private int dim;
    private BlockPos pos;

    SNextRedstoneModePacket(int dim, BlockPos pos) {
        this.dim = dim;
        this.pos = pos;
    }

    public SNextRedstoneModePacket() {
        this(0, BlockPos.ZERO);
    }

    public static void send(@Nullable World world, BlockPos pos) {
        if (world != null) {
            Lollipop.NET.toServer(new SNextRedstoneModePacket(Dim.id(world), pos));
        }
    }

    @Override
    public void encode(SNextRedstoneModePacket msg, PacketBuffer buffer) {
        buffer.writeInt(msg.dim);
        buffer.writeBlockPos(msg.pos);
    }

    @Override
    public SNextRedstoneModePacket decode(PacketBuffer buffer) {
        return new SNextRedstoneModePacket(buffer.readInt(), buffer.readBlockPos());
    }

    @Override
    public void handle(SNextRedstoneModePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Optional<ServerWorld> world = Server.getWorld(msg.dim);
            world.ifPresent(serverWorld -> {
                TileEntity tileEntity = serverWorld.getTileEntity(msg.pos);
                if (tileEntity instanceof TileBase) {
                    if (tileEntity instanceof IRedstoneInteract) {
                        ((IRedstoneInteract) tileEntity).nextRedstoneMode();
                        ((TileBase) tileEntity).markDirtyAndSync();
                    }
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
