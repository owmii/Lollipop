package owmii.lib.client.util;

import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;

public class Text {
    public static Style color(int color) {
        return Style.EMPTY.setColor(Color.func_240743_a_(color));
    }
}
