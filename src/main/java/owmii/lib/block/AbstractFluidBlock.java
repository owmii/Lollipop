package owmii.lib.block;

import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import owmii.lib.util.IVariant;

import java.util.function.Supplier;

public abstract class AbstractFluidBlock<E extends IVariant> extends FlowingFluidBlock implements IBlock<E> {
    public AbstractFluidBlock(Supplier<? extends FlowingFluid> supplier, Properties properties) {
        super(supplier, properties);
    }

    @Override
    public E getVariant() {
        return IVariant.getEmpty();
    }

    @Override
    public boolean hideGroup() {
        return true;
    }
}
