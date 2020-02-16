package owmii.lib.block;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import owmii.lib.config.IEnergyProviderConfig;
import owmii.lib.energy.Energy;
import owmii.lib.energy.SideConfig;
import owmii.lib.util.IVariant;
import owmii.lib.util.Text;

import java.util.List;

public abstract class AbstractEnergyProviderBlock<E extends IVariant> extends AbstractEnergyBlock<E> {
    public AbstractEnergyProviderBlock(Properties properties, E variant) {
        super(properties, variant);
    }

    public AbstractEnergyProviderBlock(Properties properties) {
        super(properties);
    }

    public abstract IEnergyProviderConfig<E> getEnergyConfig();

    @Override
    public SideConfig.Type getTransferType() {
        return SideConfig.Type.OUT;
    }

    @Override
    public void additionalEnergyInfo(ItemStack stack, Energy.Item energy, List<ITextComponent> tooltip) {
        tooltip.add(new TranslationTextComponent("info.lollipop.max.generates", TextFormatting.DARK_GRAY + Text.numFormat(getEnergyConfig().getGeneration(this.variant))).applyTextStyle(TextFormatting.GRAY));
    }
}
