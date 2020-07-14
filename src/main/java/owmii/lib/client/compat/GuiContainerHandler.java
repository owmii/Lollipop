package owmii.lib.client.compat;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.renderer.Rectangle2d;
import owmii.lib.client.screen.AbstractContainerScreen;

import java.util.List;

public class GuiContainerHandler implements IGuiContainerHandler<AbstractContainerScreen> {
    @Override
    @SuppressWarnings("unchecked")
    public List<Rectangle2d> getGuiExtraAreas(AbstractContainerScreen containerScreen) {
        return containerScreen.getExtraAreas();
    }
}
