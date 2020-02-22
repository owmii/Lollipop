package owmii.lib.network;

import owmii.lib.Lollipop;
import owmii.lib.network.packets.SNextEnergyConfigPacket;
import owmii.lib.network.packets.SNextRedstoneModePacket;
import owmii.lib.network.packets.SNextTransferConfigPacket;

public class Packets {
    public static void register() {
        Lollipop.NET.register(new SNextEnergyConfigPacket());
        Lollipop.NET.register(new SNextRedstoneModePacket());
        Lollipop.NET.register(new SNextTransferConfigPacket());
    }
}
