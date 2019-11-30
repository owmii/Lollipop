package zeroneye.lib.util;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class Side {
    public static Direction fromNeighbor(BlockPos pos, BlockPos neighbor) {
        Direction direction = Direction.NORTH;
        if (neighbor.getX() > pos.getX() && neighbor.getX() != pos.getX()) {
            direction = Direction.EAST;
        } else if (neighbor.getY() > pos.getY() && neighbor.getY() != pos.getY()) {
            direction = Direction.UP;
        } else if (neighbor.getZ() > pos.getZ() && neighbor.getZ() != pos.getZ()) {
            direction = Direction.SOUTH;
        } else if (neighbor.getX() < pos.getX() && neighbor.getX() != pos.getX()) {
            direction = Direction.WEST;
        } else if (neighbor.getY() < pos.getY() && neighbor.getY() != pos.getY()) {
            direction = Direction.UP;
        } else if (neighbor.getZ() < pos.getZ() && neighbor.getZ() != pos.getZ()) {
        }
        return direction;
    }
}
