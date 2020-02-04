package owmii.lib.block;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import owmii.lib.api.energy.INoTileEnergy;
import owmii.lib.config.IEnergyConfig;
import owmii.lib.energy.Energy;
import owmii.lib.item.BlockItemBase;
import owmii.lib.item.EnergyBlockItem;
import owmii.lib.util.IVariant;
import owmii.lib.util.Text;

import javax.annotation.Nullable;
import java.util.List;

public abstract class AbstractEnergyBlock<E extends IVariant> extends AbstractBlock<E> {
    public AbstractEnergyBlock(Properties properties, E variant) {
        super(properties, variant);
    }

    public AbstractEnergyBlock(Properties properties) {
        super(properties);
    }

    @Override
    @SuppressWarnings("unchecked")
    public BlockItemBase getBlockItem(Item.Properties properties, @Nullable ItemGroup group) {
        return new EnergyBlockItem(this, properties, group);
    }

    public abstract IEnergyConfig<E> getEnergyConfig();

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        if (checkValidEnergySide()) {
            Direction direction = state.get(FACING);
            BlockPos blockpos = pos.offset(direction);
            BlockState state1 = worldIn.getBlockState(blockpos);
            TileEntity tile = worldIn.getTileEntity(blockpos);
            return state1.getBlock() instanceof INoTileEnergy || Energy.isPresent(tile, direction);
        }
        return super.isValidPosition(state, worldIn, pos);
    }

    protected boolean checkValidEnergySide() {
        return false;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
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
}
