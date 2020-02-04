package owmii.lib.config;

import owmii.lib.util.IVariant;

public interface IEnergyConfig<E extends IVariant> extends IConfig {
    long getCapacity(E variant);

    long getTransfer(E variant);

    static IEnergyConfig getEmpty() {
        return new IEnergyConfig() {
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
