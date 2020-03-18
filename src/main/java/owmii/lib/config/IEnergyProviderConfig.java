package owmii.lib.config;

import owmii.lib.util.IVariant;

public interface IEnergyProviderConfig<E extends IVariant> extends IEnergyConfig<E> {
    long getGeneration(E variant);

    static IEnergyProviderConfig<IVariant.Single> getEmpty() {
        return new IEnergyProviderConfig<IVariant.Single>() {
            @Override
            public long getGeneration(IVariant.Single variant) {
                return 0;
            }

            @Override
            public long getCapacity(IVariant.Single variant) {
                return 0;
            }

            @Override
            public long getTransfer(IVariant.Single variant) {
                return 0;
            }

            @Override
            public void reload() {

            }
        };
    }
}
