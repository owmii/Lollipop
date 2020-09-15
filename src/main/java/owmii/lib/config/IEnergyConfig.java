package owmii.lib.config;

import owmii.lib.registry.IVariant;

public interface IEnergyConfig<V extends IVariant<?>> extends IConfig<V> {
    long getCapacity(V variant);

    long getTransfer(V variant);

    default long getGeneration(V variant) {
        return 0;
    }
}
