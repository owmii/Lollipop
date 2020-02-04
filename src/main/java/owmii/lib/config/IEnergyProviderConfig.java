package owmii.lib.config;

import owmii.lib.util.IVariant;

public interface IEnergyProviderConfig<E extends IVariant> extends IEnergyConfig<E> {
    long getGeneration(E variant);

    static IEnergyProviderConfig getEmpty() {
        return new IEnergyProviderConfig() {
            @Override
            public long getGeneration(IVariant variant) {
                return 0;
            }

            @Override
            public long getCapacity(IVariant variant) {
                return 0;
            }

            @Override
            public long getTransfer(IVariant variant) {
                return 0;
            }

            @Override
            public void reload() {

            }
        };
    }
}
