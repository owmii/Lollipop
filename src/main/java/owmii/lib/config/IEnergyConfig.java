package owmii.lib.config;

import owmii.lib.util.IVariant;

public interface IEnergyConfig<E extends IVariant> extends IConfig {
    long getCapacity(E variant);

    long getTransfer(E variant);

    static IEnergyConfig<IVariant.Single> getEmpty() {
        return new IEnergyConfig<IVariant.Single>() {
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
