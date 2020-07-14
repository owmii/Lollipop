package owmii.lib.block;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import owmii.lib.config.IEnergyConfig;
import owmii.lib.logistics.energy.Energy;
import owmii.lib.util.Util;

import java.util.List;

public abstract class AbstractGeneratorBlock<V extends IVariant<?>, C extends IEnergyConfig<V>> extends AbstractEnergyBlock<V, C> {
    public AbstractGeneratorBlock(Properties properties) {
        super(properties);
    }

    public AbstractGeneratorBlock(Properties properties, V variant) {
        super(properties, variant);
    }

    @Override
    public void additionalEnergyInfo(ItemStack stack, Energy.Item energy, List<ITextComponent> tooltip) {
        tooltip.add(new TranslationTextComponent("info.lollipop.generates", Util.numFormat(getConfig().getGeneration(this.variant))).func_240699_a_(TextFormatting.DARK_GRAY));
    }
}
