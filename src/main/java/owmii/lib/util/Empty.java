package owmii.lib.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import owmii.lib.Lollipop;
import owmii.lib.client.screen.widget.Gauge;
import owmii.lib.client.screen.widget.IconButton;
import owmii.lib.client.screen.widget.TextField;

public class Empty {
    public static final ResourceLocation LOCATION;
    public static final String STRING;

    @OnlyIn(Dist.CLIENT)
    public static final TextField TEXT_FIELD;
    @OnlyIn(Dist.CLIENT)
    public static final IconButton ICON_BUTTON;
    @OnlyIn(Dist.CLIENT)
    public static final Gauge GAUGE;

    public static boolean check(ResourceLocation location) {
        return LOCATION.equals(location);
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean check(TextField textField) {
        return TEXT_FIELD.equals(textField);
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean check(IconButton iconButton) {
        return ICON_BUTTON.equals(iconButton);
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean check(Gauge gauge) {
        return GAUGE.equals(gauge);
    }

    static {
        STRING = "";
        LOCATION = new ResourceLocation(Lollipop.MOD_ID, "empty");
        TEXT_FIELD = new TextField(Minecraft.getInstance().fontRenderer, 0, 0, 0, 0, "");
        ICON_BUTTON = new IconButton(0, 0, 0, 0, 0, 0, 0, LOCATION, Button::getWidth, new ChatScreen(""));
        GAUGE = new Gauge(0, 0, 0, 0, 0, 0, false, LOCATION, new ChatScreen(""));
    }
}
