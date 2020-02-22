package owmii.lib.network.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import owmii.lib.Lollipop;
import owmii.lib.block.TileBase;
import owmii.lib.network.IPacket;
import owmii.lib.util.Dim;
import owmii.lib.util.Server;
import owmii.lib.util.logic.ILogicHandler;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

public class SNextTransferConfigPacket implements IPacket<SNextTransferConfigPacket> {
    private int i;
    private int dim;
    private BlockPos pos;

    SNextTransferConfigPacket(int i, int dim, BlockPos pos) {
        this.i = i;
        this.dim = dim;
        this.pos = pos;
    }

    public SNextTransferConfigPacket() {
        this(0, 0, BlockPos.ZERO);
    }

    public static void send(int i, @Nullable World world, BlockPos pos) {
        if (world != null) {
            Lollipop.NET.toServer(new SNextTransferConfigPacket(i, Dim.id(world), pos));
        }
    }

    @Override
    public void encode(SNextTransferConfigPacket msg, PacketBuffer buffer) {
        buffer.writeInt(msg.i);
        buffer.writeInt(msg.dim);
        buffer.writeBlockPos(msg.pos);
    }

    @Override
    public SNextTransferConfigPacket decode(PacketBuffer buffer) {
        return new SNextTransferConfigPacket(buffer.readInt(), buffer.readInt(), buffer.readBlockPos());
    }

    @Override
    public void handle(SNextTransferConfigPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Optional<ServerWorld> world = Server.getWorld(msg.dim);
            world.ifPresent(serverWorld -> {
                TileEntity te = serverWorld.getTileEntity(msg.pos);
                if (te instanceof ILogicHandler && te instanceof TileBase) {
                    ILogicHandler handler = ((ILogicHandler) te);
                    if (msg.i > 5) handler.getTransferConfig().nextTypeAllSides();
                    else handler.getTransferConfig().nextType(Direction.byIndex(msg.i));
                    ((TileBase) te).markDirtyAndSync();
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
