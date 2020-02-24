package owmii.lib.inventory;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import owmii.lib.block.TileBase;
import owmii.lib.inventory.slot.SlotBase;
import owmii.lib.inventory.slot.SlotOverlay;

import javax.annotation.Nullable;

public class EnergyContainerBase<I extends TileBase.EnergyStorage> extends ContainerBase<I> {
    public EnergyContainerBase(@Nullable ContainerType<?> containerType, int id, PlayerInventory playerInventory, I te) {
        super(containerType, id, playerInventory, te);
    }

    public EnergyContainerBase(@Nullable ContainerType<?> containerType, int id, PlayerInventory playerInventory, PacketBuffer buffer) {
        super(containerType, id, playerInventory, buffer);
    }

    protected void addChargingInv(I te, int x, int y) {
        for (int i = 0; i < te.getChargingSlots(); i++) {
            addSlot(new SlotBase(te.getInventory(), i, x + (i * 20), y).bg(SlotOverlay.SLOT).ov(SlotOverlay.ENERGY));
        }
    }

    @Override
    protected void addContainer(PlayerInventory playerInventory, I te) {
        super.addContainer(playerInventory, te);
        addChargingInv(te, 28, 47);
        addPlayerInv(playerInventory, 8, 140, 82);
    }
}
