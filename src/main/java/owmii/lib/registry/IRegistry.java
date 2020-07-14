package owmii.lib.registry;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface IRegistry<T extends IForgeRegistryEntry<T>> {
    void register(RegistryEvent.Register<T> event);
}
