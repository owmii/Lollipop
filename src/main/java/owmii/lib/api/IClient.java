package owmii.lib.api;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public interface IClient {
    void client(FMLClientSetupEvent event);
}
