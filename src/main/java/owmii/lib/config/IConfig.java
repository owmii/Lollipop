package owmii.lib.config;

import owmii.lib.registry.IVariant;

public interface IConfig<V extends IVariant<?>> {
    void reload();
}
