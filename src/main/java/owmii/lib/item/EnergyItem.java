package owmii.lib.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import owmii.lib.config.IEnergyConfig;
import owmii.lib.energy.Energy;
import owmii.lib.util.IVariant;
import owmii.lib.util.Text;

import javax.annotation.Nullable;
import java.util.List;

public abstract class EnergyItem<E extends IVariant> extends ItemBase {
    private final E variant;

    public EnergyItem(Properties properties, E variant) {
        super(properties);
        this.variant = variant;
    }

    public EnergyItem(Properties properties) {
        this(properties, IVariant.getEmpty());
    }

    public abstract IEnergyConfig<E> getEnergyConfig();

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        Energy.ifPresent(stack, storage -> {
            if (storage instanceof Energy.Item) {
                Energy.Item energy = (Energy.Item) storage;
                addEnergyInfo(energy, tooltip);
                addEnergyTransferInfo(energy, tooltip);
                tooltip.add(new StringTextComponent(""));
            }
        });
    }

    public void addEnergyInfo(Energy.Item storage, List<ITextComponent> tooltip) {
        if (storage.getCapacity() > 0)
            tooltip.add(new TranslationTextComponent("info.lollipop.stored.energy.fe", TextFormatting.DARK_GRAY + Text.addCommas(storage.getStored()), Text.numFormat(storage.getCapacity())).applyTextStyle(TextFormatting.GRAY));
    }

    public void addEnergyTransferInfo(Energy.Item storage, List<ITextComponent> tooltip) {
        long ext = storage.getMaxExtract();
        long re = storage.getMaxReceive();
        if (ext + re > 0) {
            if (ext == re) {
                tooltip.add(new TranslationTextComponent("info.lollipop.max.transfer.fe", TextFormatting.DARK_GRAY + Text.numFormat(ext)).applyTextStyle(TextFormatting.GRAY));
            } else {
                if (ext > 0)
                    tooltip.add(new TranslationTextComponent("info.lollipop.max.extract.fe", TextFormatting.DARK_GRAY + Text.numFormat(ext)).applyTextStyle(TextFormatting.GRAY));
                if (re > 0)
                    tooltip.add(new TranslationTextComponent("info.lollipop.max.receive.fe", TextFormatting.DARK_GRAY + Text.numFormat(re)).applyTextStyle(TextFormatting.GRAY));
            }
        }
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        IEnergyConfig config = getEnergyConfig();
        return new Energy.Item.Provider(stack, config.getCapacity(getVariant()), config.getTransfer(getVariant()), config.getTransfer(getVariant()));
    }

    public E getVariant() {
        return this.variant;
    }
}
