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
import owmii.lib.util.logic.ILogicHandler;

import java.util.function.Supplier;

public class SNextTransferConfigPacket implements IPacket<SNextTransferConfigPacket> {
    private int i;
    private BlockPos pos;

    public SNextTransferConfigPacket(int i, BlockPos pos) {
        this.i = i;
        this.pos = pos;
    }

    public SNextTransferConfigPacket() {
        this(0, BlockPos.ZERO);
    }

    public static void send(int i, BlockPos pos) {
        Lollipop.NET.toServer(new SNextTransferConfigPacket(i, pos));
    }

    @Override
    public void encode(SNextTransferConfigPacket msg, PacketBuffer buffer) {
        buffer.writeInt(msg.i);
        buffer.writeBlockPos(this.pos);
    }

    @Override
    public SNextTransferConfigPacket decode(PacketBuffer buffer) {
        return new SNextTransferConfigPacket(buffer.readInt(), buffer.readBlockPos());
    }

    @Override
    public void handle(SNextTransferConfigPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player != null) {
                TileEntity te = player.world.getTileEntity(msg.pos);
                if (te instanceof ILogicHandler && te instanceof TileBase) {
                    ILogicHandler handler = ((ILogicHandler) te);
                    if (msg.i > 5) handler.getTransferConfig().nextTypeAllSides();
                    else handler.getTransferConfig().nextType(Direction.byIndex(msg.i));
                    ((TileBase) te).markDirtyAndSync();
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
