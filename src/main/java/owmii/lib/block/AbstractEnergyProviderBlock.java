package owmii.lib.block;

import owmii.lib.config.IEnergyProviderConfig;
import owmii.lib.util.IVariant;

public abstract class AbstractEnergyProviderBlock<E extends IVariant> extends AbstractEnergyBlock<E> {
    public AbstractEnergyProviderBlock(Properties properties, E variant) {
        super(properties, variant);
    }

    public AbstractEnergyProviderBlock(Properties properties) {
        super(properties);
    }

    public abstract IEnergyProviderConfig<E> getEnergyConfig();
}
