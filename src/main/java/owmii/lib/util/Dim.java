package owmii.lib.util;

import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;

public class Dim {
    public static int id(World world) {
        return id(world.dimension);
    }

    public static int id(Dimension dimension) {
        return id(dimension.getType());
    }

    public static int id(DimensionType type) {
        return type.getId();
    }
}
