package zeroneye.lib.util;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class Text {
    public static ITextComponent format(String s, Object... args) {
        ITextComponent tc = new TranslationTextComponent(s, args);
        for (TextFormatting tf : TextFormatting.values()) {
            tc = new StringTextComponent(tc.getString().replace("<" + tf.name().toLowerCase().replace("ark_", "").replace("ight_", "") + ">", "" + tf));
        }
        return tc;
    }
}
