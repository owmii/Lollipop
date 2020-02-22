package owmii.lib.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import owmii.lib.block.TileBase;

import javax.annotation.Nullable;

public class ContainerBase<I extends TileBase> extends Container {
    protected final I te;

    public ContainerBase(@Nullable ContainerType<?> containerType, int id, PlayerInventory playerInventory, I te) {
        super(containerType, id);
        this.te = te;
        this.te.setContainerOpen(true);
        this.te.markDirtyAndSync();
        addContainer(playerInventory, te);
    }

    public ContainerBase(@Nullable ContainerType<?> containerType, int id, PlayerInventory playerInventory, PacketBuffer buffer) {
        this(containerType, id, playerInventory, getInventory(playerInventory, buffer.readBlockPos()));
        this.te.setContainerOpen(true);
        this.te.markDirtyAndSync();
        addContainer(playerInventory, this.te);
    }

    @Override
    public void onContainerClosed(PlayerEntity p_75134_1_) {
        super.onContainerClosed(p_75134_1_);
        this.te.setContainerOpen(false);
    }

    protected void addContainer(PlayerInventory playerInventory, I te) {
    }

    @SuppressWarnings("unchecked")
    protected static <T extends TileBase> T getInventory(PlayerInventory playerInv, BlockPos pos) {
        World world = playerInv.player.getEntityWorld();
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileBase)
            return (T) tile;
        return (T) new TileBase(TileEntityType.SIGN);
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
            if (index < this.te.getInventory().getSlots()) {
                if (!this.mergeItemStack(stack1, this.te.getInventory().getSlots(), this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(stack1, 0, this.te.getInventory().getSlots(), false)) {
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

    protected boolean refresh(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity) {
            if (this.te.createMenu(0, player.inventory, player) != null) {
                NetworkHooks.openGui((ServerPlayerEntity) player, this.te, this.te.getPos());
                return true;
            }
        }
        return false;
    }

    public I getTile() {
        return this.te;
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return true;
    }
}
