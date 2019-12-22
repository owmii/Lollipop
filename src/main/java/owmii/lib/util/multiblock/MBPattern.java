package owmii.lib.util.multiblock;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class MBPattern {
    private final List<List<BlockPos>> shapes = new ArrayList<>();

    MBPattern(String rgx) {
        if (rgx.isEmpty()) {
            return;
        }
        String[] ps = rgx.split("||");
        for (String p : ps) {
            String[] in = p.split(":");
            if (in.length >= 2) {
                List<BlockPos> list = new ArrayList<>();
                String cb = in[0];
                String[] bx = cb.split("x");
                if (bx.length != 3) {
                    break;
                } else {
                    int cx = Integer.parseInt(bx[0]);
                    int cy = Integer.parseInt(bx[1]);
                    int cz = Integer.parseInt(bx[2]);
                    for (int i1 = 1; i1 < in.length; i1++) {
                        String of = in[i1];
                        String[] crs = of.split("x");
                        if (crs.length != 3) {
                            break;
                        } else {
                            int ox = Integer.parseInt(crs[0]);
                            int oy = Integer.parseInt(crs[1]);
                            int oz = Integer.parseInt(crs[2]);
                            for (int x = ox; x <= cx + ox; x++) {
                                for (int y = oy; y <= cy + oy; y++) {
                                    for (int z = oz; z <= cz + oz; z++) {
                                        if (x != 0 && y != 0 && z != 0) {
                                            list.add(new BlockPos(x, y, z));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (!list.isEmpty()) {
                        this.shapes.add(list);
                    }
                }
            }
        }
    }

    public List<BlockPos> get(int id) {
        return this.shapes.get(id);
    }

    public static MBPattern compile(String rgx) {
        return new MBPattern(rgx);
    }

    public List<List<BlockPos>> getShapes() {
        return this.shapes;
    }
}
