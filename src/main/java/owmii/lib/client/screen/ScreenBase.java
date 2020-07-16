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
    public boolean func_231046_a_(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
        if (super.func_231046_a_(p_231046_1_, p_231046_2_, p_231046_3_)) {
            return true;
        } else {
            InputMappings.Input code = InputMappings.getInputByCode(p_231046_1_, p_231046_2_);
            if (p_231046_1_ == 256 || Minecraft.getInstance().gameSettings.keyBindInventory.isActiveAndMatches(code)) {
                if (this.mc.player != null) {
                    this.mc.player.closeScreen();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public <T extends Widget> T func_230480_a_(T p_230480_1_) {
        return super.func_230480_a_(p_230480_1_);
    }

    @Override
    public boolean func_231177_au__() {
        return false;
    }
}
