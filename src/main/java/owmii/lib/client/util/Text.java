package owmii.lib.client.util;

import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;

@OnlyIn(Dist.CLIENT)
public class Text {
    public static Style color(int color) {
        return Style.EMPTY.setColor(Color.func_240743_a_(color));
    }

    public static void drawString(ITextProperties text, float x, float y, int w, int h, int color) {
        Minecraft mc = Minecraft.getInstance();
        FontRenderer font = mc.fontRenderer;
        Matrix4f matrix4f = TransformationMatrix.identity().getMatrix();
        for (IReorderingProcessor processor : font.func_238425_b_(text, w)) {
            IRenderTypeBuffer.Impl impl = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
            font.func_238416_a_(processor, x, y, color, false, matrix4f, impl, false, 0, 15728880);
            impl.finish();
            y += h;
        }
    }

    public static String toRange(long l) {
        long l1 = (l * 2 + 1);
        return l1 + "X" + l1;
    }

    public static String toVolume(long l) {
        long l1 = (l * 2 + 1);
        return l1 + "X" + l1 + "X" + l1;
    }
}
