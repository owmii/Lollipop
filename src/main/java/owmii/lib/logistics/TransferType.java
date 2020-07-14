package owmii.lib.logistics;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public enum TransferType {
    ALL(true, true, TextFormatting.DARK_GRAY),
    EXTRACT(true, false, TextFormatting.DARK_GRAY),
    RECEIVE(false, true, TextFormatting.DARK_GRAY),
    NONE(false, false, TextFormatting.DARK_RED);

    public final boolean canExtract;
    public final boolean canReceive;
    private final TextFormatting color;

    TransferType(boolean canExtract, boolean canReceive, TextFormatting color) {
        this.canExtract = canExtract;
        this.canReceive = canReceive;
        this.color = color;
    }

    public TransferType next(TransferType type) {
        if (ALL.equals(type)) {
            int i = ordinal();
            if (i < 3) i++;
            else i = 0;
            return values()[i];
        } else if (EXTRACT.equals(type)) {
            return !NONE.equals(this) ? NONE : EXTRACT;
        } else if (RECEIVE.equals(type)) {
            return !NONE.equals(this) ? NONE : RECEIVE;
        }
        return NONE;
    }


    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("info.lollipop.side.config." + name().toLowerCase(), this.color).func_240699_a_(TextFormatting.GRAY);
    }
}
