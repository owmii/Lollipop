package owmii.lib.network.packets;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import owmii.lib.Lollipop;
import owmii.lib.block.TileBase;
import owmii.lib.energy.IRedstoneInteract;
import owmii.lib.network.IPacket;
import owmii.lib.util.Packet;

import java.util.function.Supplier;

public class SNextRedstoneModePacket implements IPacket<SNextRedstoneModePacket> {
    private BlockPos pos;

    SNextRedstoneModePacket(BlockPos pos) {
        this.pos = pos;
    }

    public SNextRedstoneModePacket() {
        this(BlockPos.ZERO);
    }

    public static void send(BlockPos pos) {
        Lollipop.NET.toServer(new SNextRedstoneModePacket(pos));
    }

    @Override
    public void encode(SNextRedstoneModePacket msg, PacketBuffer buffer) {
        Packet.writePos(msg.pos, buffer);
    }

    @Override
    public SNextRedstoneModePacket decode(PacketBuffer buffer) {
        return new SNextRedstoneModePacket(Packet.readPos(buffer));
    }

    @Override
    public void handle(SNextRedstoneModePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player != null) {
                TileEntity tileEntity = player.world.getTileEntity(msg.pos);
                if (tileEntity instanceof TileBase) {
                    if (tileEntity instanceof IRedstoneInteract) {
                        ((IRedstoneInteract) tileEntity).nextRedstoneMode();
                        ((TileBase) tileEntity).markDirtyAndSync();
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
