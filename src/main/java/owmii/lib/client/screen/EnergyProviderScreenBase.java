package owmii.lib.client.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import owmii.lib.block.TileBase;
import owmii.lib.inventory.EnergyContainerBase;

public class EnergyProviderScreenBase<T extends TileBase.EnergyProvider, C extends EnergyContainerBase<T>> extends EnergyScreenBase<T, C> {
    public EnergyProviderScreenBase(C container, PlayerInventory playerInventory, ITextComponent name) {
        super(container, playerInventory, name);
    }
}
