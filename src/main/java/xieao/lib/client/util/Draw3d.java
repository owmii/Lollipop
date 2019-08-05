package xieao.lib.client.util;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public class Draw3d {
    public static void cube(BufferBuilder bb) {
        for (Direction facing : Direction.values()) {
            quad(bb, facing, 1.0D, 1.0D);
        }
    }

    public static void quad(BufferBuilder bb, Direction facing, double deep, double scale) {
        double[][] pos = new double[4][3];
        double[][] uv = new double[4][2];
        switch (facing) { // TODO finish all facing quads
            case DOWN:
                break;
            case UP:
                pos[0][0] = pos[3][0] = pos[2][2] = pos[3][2] = -scale * 0.5D;
                pos[0][1] = pos[1][1] = pos[2][1] = pos[3][1] = deep;
                pos[1][0] = pos[2][0] = pos[0][2] = pos[1][2] = scale * 0.5D;
                uv[0][0] = uv[0][1] = uv[1][0] = uv[3][1];
                uv[1][1] = uv[2][0] = uv[2][1] = uv[3][0] = 1.0D;
                break;
            case NORTH:
            case SOUTH:
            case WEST:
            case EAST:
        }
        quad(bb, pos, uv);
    }

    public static void quad(BufferBuilder bb, double[][] pos, double[][] uv) {
        bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bb.pos(pos[0][0], pos[0][1], pos[0][2]).tex(uv[0][0], uv[0][1]).endVertex();
        bb.pos(pos[1][0], pos[1][1], pos[1][2]).tex(uv[1][0], uv[1][1]).endVertex();
        bb.pos(pos[2][0], pos[2][1], pos[2][2]).tex(uv[2][0], uv[2][1]).endVertex();
        bb.pos(pos[3][0], pos[3][1], pos[3][2]).tex(uv[3][0], uv[3][1]).endVertex();
    }
}
