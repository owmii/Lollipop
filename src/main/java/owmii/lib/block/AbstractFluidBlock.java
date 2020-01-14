package owmii.lib.block;

import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;

import java.util.function.Supplier;

public class AbstractFluidBlock extends FlowingFluidBlock implements IBlock {
    public AbstractFluidBlock(Supplier<? extends FlowingFluid> supplier, Properties properties) {
        super(supplier, properties);
    }

    @Override
    public boolean hideGroup() {
        return true;
    }
}
