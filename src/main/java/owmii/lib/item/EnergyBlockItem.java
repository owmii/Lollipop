package owmii.lib.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import owmii.lib.block.AbstractEnergyBlock;
import owmii.lib.block.IVariant;
import owmii.lib.config.IConfigHolder;
import owmii.lib.config.IEnergyConfig;
import owmii.lib.logistics.energy.Energy;

import javax.annotation.Nullable;
import java.util.List;

public class EnergyBlockItem<V extends IVariant<?>, C extends IEnergyConfig<V>, B extends AbstractEnergyBlock<V, C>> extends ItemBlock<V, B> implements IConfigHolder<V, C> {
    public EnergyBlockItem(B block, Properties builder, @Nullable ItemGroup group) {
        super(block, builder, group);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        long transfer = getConfig().getTransfer(getVariant());
        return new Energy.Item.Provider(stack, getConfig().getCapacity(getVariant()), transfer, transfer);
    }

    @Override
    public C getConfig() {
        return getBlock().getConfig();
    }

    public V getVariant() {
        return getBlock().getVariant();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
