package owmii.lib.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.ITextComponent;

public class ScreenBase extends Screen {
    public final Minecraft mc = Minecraft.getInstance();

    protected ScreenBase(ITextComponent titleIn) {
        super(titleIn);
    }

    @Override
    public <T extends Widget> T addButton(T button) {
        return super.addButton(button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int i, int i1, int i2) {
        if (super.keyPressed(i, i1, i2)) {
            return true;
        } else {
            InputMappings.Input code = InputMappings.getInputByCode(i, i1);
            if (i == 256 || Minecraft.getInstance().gameSettings.keyBindInventory.isActiveAndMatches(code)) {
                Minecraft.getInstance().player.closeScreen();
                return true;
            }
        }
        return false;
    }
}
