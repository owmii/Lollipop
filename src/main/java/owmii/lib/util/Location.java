package owmii.lib.util;

import net.minecraft.util.math.BlockPos;

public class Location {
    public final int dim;
    public final BlockPos pos;

    public Location(int dim, BlockPos pos) {
        this.dim = dim;
        this.pos = pos;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Location) {
            Location location = (Location) obj;
            return this.dim == location.dim && this.pos.equals(location.pos);
        }
        return false;
    }
}
