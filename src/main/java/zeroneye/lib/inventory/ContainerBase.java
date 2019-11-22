package zeroneye.lib.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zeroneye.lib.block.IInvBase;
import zeroneye.lib.block.TileBase;

import javax.annotation.Nullable;

public abstract class ContainerBase<I extends TileBase> extends Container {
    protected final I inv;

    protected ContainerBase(@Nullable ContainerType<?> containerType, int id, PlayerInventory playerInventory, I inv) {
        super(containerType, id);
        this.inv = inv;
        this.inv.setContainerOpen(true);
        this.inv.markDirtyAndSync();
    }

    @SuppressWarnings("unchecked")
    protected ContainerBase(@Nullable ContainerType<?> containerType, int id, PlayerInventory playerInventory, PacketBuffer buffer) {
        this(containerType, id, playerInventory, (I) getInventory(playerInventory, buffer.readBlockPos()));
        this.inv.setContainerOpen(true);
        this.inv.markDirtyAndSync();
    }

    @Override
    public void onContainerClosed(PlayerEntity p_75134_1_) {
        super.onContainerClosed(p_75134_1_);
        this.inv.setContainerOpen(false);
    }

    @SuppressWarnings("unchecked")
    protected static <T extends IInvBase> T getInventory(PlayerInventory playerInv, BlockPos pos) {
        World world = playerInv.player.getEntityWorld();
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof IInvBase)
            return (T) tile;
        return (T) IInvBase.EMPTY;
    }

    protected void addPlayerInv(PlayerInventory playerInventory, int x, int hY, int iY) {
        for (int l = 0; l < 3; ++l) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(playerInventory, k + l * 9 + 9, x + k * 18, l * 18 + iY));
            }
        }
        for (int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(playerInventory, i1, x + i1 * 18, hY));
        }
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack1 = slot.getStack();
            stack = stack1.copy();
            if (index < this.inv.getSizeInventory()) {
                if (!this.mergeItemStack(stack1, this.inv.getSizeInventory(), this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(stack1, 0, this.inv.getSizeInventory(), false)) {
                return ItemStack.EMPTY;
            }

            if (stack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }
        return stack;
    }

    public I getInv() {
        return inv;
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return true;
    }
}
