package zeroneye.lib.block;

import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;

import java.util.function.Supplier;

public class FluidBlockBase extends FlowingFluidBlock implements IBlockBase {
    public FluidBlockBase(Supplier<? extends FlowingFluid> supplier, Properties properties) {
        super(supplier, properties);
    }

    @Override
    public boolean hideGroup() {
        return true;
    }
}
