package owmii.lib.util;

import net.minecraft.network.PacketBuffer;

public class Packet {
    public static String readString(PacketBuffer buffer) {
        return buffer.readString(32767);
    }
}
