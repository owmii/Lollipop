package owmii.lib.config;

import owmii.lib.block.IVariant;

public interface IConfig<V extends IVariant<?>> {
    void reload();
}
