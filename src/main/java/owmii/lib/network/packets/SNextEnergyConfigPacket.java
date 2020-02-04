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

import java.util.Optional;
import java.util.function.Supplier;

public class SNextEnergyConfigPacket implements IPacket<SNextEnergyConfigPacket> {
    private int i;
    private int dim;
    private BlockPos pos;

    SNextEnergyConfigPacket(int i, int dim, BlockPos pos) {
        this.i = i;
        this.dim = dim;
        this.pos = pos;
    }

    public SNextEnergyConfigPacket() {
        this(0, 0, BlockPos.ZERO);
    }

    public static void send(int i, World world, BlockPos pos) {
        Lollipop.NET.toServer(new SNextEnergyConfigPacket(i, Dim.id(world), pos));
    }

    @Override
    public void encode(SNextEnergyConfigPacket msg, PacketBuffer buffer) {
        buffer.writeInt(msg.i);
        buffer.writeInt(msg.dim);
        buffer.writeBlockPos(msg.pos);
    }

    @Override
    public SNextEnergyConfigPacket decode(PacketBuffer buffer) {
        return new SNextEnergyConfigPacket(buffer.readInt(), buffer.readInt(), buffer.readBlockPos());
    }

    @Override
    public void handle(SNextEnergyConfigPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Optional<ServerWorld> world = Server.getWorld(msg.dim);
            world.ifPresent(serverWorld -> {
                TileEntity tileEntity = serverWorld.getTileEntity(msg.pos);
                if (tileEntity instanceof TileBase.EnergyStorage) {
                    TileBase.EnergyStorage storage = ((TileBase.EnergyStorage) tileEntity);
                    if (msg.i > 5) storage.getSideConfig().nextTypeAllSides();
                    else storage.getSideConfig().nextType(Direction.byIndex(msg.i));
                    storage.sync(1);
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
