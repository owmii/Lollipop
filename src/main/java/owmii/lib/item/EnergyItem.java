package owmii.lib.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import owmii.lib.block.IVariant;
import owmii.lib.config.IEnergyConfig;
import owmii.lib.logistics.energy.Energy;

import javax.annotation.Nullable;

public abstract class EnergyItem<V extends IVariant<?>, C extends IEnergyConfig<V>> extends ItemBase {
    private final V variant;

    public EnergyItem(Properties properties, V variant) {
        super(properties);
        this.variant = variant;
    }

    public EnergyItem(Properties properties) {
        this(properties, IVariant.getEmpty());
    }

    public abstract IEnergyConfig<V> getConfig();

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        IEnergyConfig config = getConfig();
        return new Energy.Item.Provider(stack, config.getCapacity(getVariant()), config.getTransfer(getVariant()), config.getTransfer(getVariant()));
    }

    public V getVariant() {
        return this.variant;
    }
}
