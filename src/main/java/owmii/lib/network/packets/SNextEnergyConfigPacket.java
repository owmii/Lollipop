package owmii.lib.network.packets;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import owmii.lib.Lollipop;
import owmii.lib.block.TileBase;
import owmii.lib.network.IPacket;
import owmii.lib.util.Packet;

import java.util.function.Supplier;

public class SNextEnergyConfigPacket implements IPacket<SNextEnergyConfigPacket> {
    private int i;
    private BlockPos pos;

    SNextEnergyConfigPacket(int i, BlockPos pos) {
        this.i = i;
        this.pos = pos;
    }

    public SNextEnergyConfigPacket() {
        this(0, BlockPos.ZERO);
    }

    public static void send(int i, BlockPos pos) {
        Lollipop.NET.toServer(new SNextEnergyConfigPacket(i, pos));
    }

    @Override
    public void encode(SNextEnergyConfigPacket msg, PacketBuffer buffer) {
        buffer.writeInt(msg.i);
        Packet.writePos(msg.pos, buffer);
    }

    @Override
    public SNextEnergyConfigPacket decode(PacketBuffer buffer) {
        return new SNextEnergyConfigPacket(buffer.readInt(), Packet.readPos(buffer));
    }

    @Override
    public void handle(SNextEnergyConfigPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player != null) {
                TileEntity tileEntity = player.world.getTileEntity(msg.pos);
                if (tileEntity instanceof TileBase.EnergyStorage) {
                    TileBase.EnergyStorage storage = ((TileBase.EnergyStorage) tileEntity);
                    if (msg.i > 5) storage.getSideConfig().nextTypeAllSides();
                    else storage.getSideConfig().nextType(Direction.byIndex(msg.i));
                    storage.markDirtyAndSync();
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
