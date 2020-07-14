package owmii.lib.config;

import owmii.lib.block.IVariant;

public interface IConfigHolder<V extends IVariant<?>, C extends IConfig<V>> {
    C getConfig();
}
