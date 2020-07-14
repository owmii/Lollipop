package owmii.lib.block;

import net.minecraft.tileentity.TileEntityType;
import owmii.lib.config.IEnergyConfig;
import owmii.lib.logistics.TransferType;

public class AbstractEnergyProvider<V extends IVariant<?>, C extends IEnergyConfig<V>, B extends AbstractEnergyBlock<V, C>> extends AbstractEnergyStorage<V, C, B> {
    public AbstractEnergyProvider(TileEntityType<?> type) {
        super(type);
    }

    public AbstractEnergyProvider(TileEntityType<?> type, V variant) {
        super(type, variant);
    }

    public long getGeneration() {
        return getBlock().getConfig().getGeneration(getVariant());
    }

    @Override
    public TransferType getTransferType() {
        return TransferType.EXTRACT;
    }
}
