package owmii.lib.energy;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public enum Redstone {
    IGNORE(45, TextFormatting.DARK_GRAY),
    ON(60, TextFormatting.RED),
    OFF(75, TextFormatting.DARK_RED);

    private final int xuv;
    private final TextFormatting color;

    Redstone(int xuv, TextFormatting color) {
        this.xuv = xuv;
        this.color = color;
    }

    public Redstone next() {
        int i = ordinal() + 1;
        return values()[i > 2 ? 0 : i];
    }

    public int getXuv() {
        return this.xuv;
    }

    public String getDisplayName() {
        return TextFormatting.GRAY + I18n.format("info.lollipop.redstone.mode." + name().toLowerCase(), this.color);
    }
}
