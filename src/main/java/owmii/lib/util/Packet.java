package owmii.lib.util;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public class Packet {
    public static String readString(PacketBuffer buffer) {
        return buffer.readString(32767);
    }

    public static BlockPos readPos(PacketBuffer buffer) {
        return new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
    }

    public static void writePos(BlockPos pos, PacketBuffer buffer) {
        buffer.writeInt(pos.getX());
        buffer.writeInt(pos.getY());
        buffer.writeInt(pos.getZ());
    }
}
