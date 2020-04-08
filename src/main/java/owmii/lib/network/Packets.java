package owmii.lib.network;

import owmii.lib.network.packets.SNextEnergyConfigPacket;
import owmii.lib.network.packets.SNextRedstoneModePacket;
import owmii.lib.network.packets.SNextTransferConfigPacket;

import static owmii.lib.Lollipop.NET;

public class Packets {
    public static void register() {
        NET.register(new SNextEnergyConfigPacket());
        NET.register(new SNextRedstoneModePacket());
        NET.register(new SNextTransferConfigPacket());
    }
}
