package owmii.lib.config;

import owmii.lib.registry.IVariant;

public interface IConfigHolder<V extends IVariant<?>, C extends IConfig<V>> {
    C getConfig();
}
