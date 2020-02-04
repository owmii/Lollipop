package owmii.lib.client.screen.widget;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;

import javax.annotation.Nullable;

public class TextField extends TextFieldWidget {
    boolean numberOnly;

    public TextField(FontRenderer fontIn, int p_i51137_2_, int p_i51137_3_, int p_i51137_4_, int p_i51137_5_, String msg) {
        super(fontIn, p_i51137_2_, p_i51137_3_, p_i51137_4_, p_i51137_5_, msg);
    }

    public TextField(FontRenderer fontIn, int xIn, int yIn, int widthIn, int heightIn, @Nullable TextFieldWidget p_i51138_6_, String msg) {
        super(fontIn, xIn, yIn, widthIn, heightIn, p_i51138_6_, msg);
    }

    @Override
    public void writeText(String textToWrite) {
        if (this.numberOnly) {
            try {
                Long.parseLong(textToWrite);
            } catch (Exception ignored) {
                return;
            }
        }
        super.writeText(textToWrite);
    }

    public void setNumberOnly(boolean numberOnly) {
        this.numberOnly = numberOnly;
    }

    public void toNumberOr(long fallBack) {
        try {
            setText("" + Long.parseLong(getText()));
        } catch (Exception e) {
            setText("" + fallBack);
        }
    }
}
