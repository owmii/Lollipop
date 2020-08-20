package owmii.lib.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import owmii.lib.Lollipop;
import owmii.lib.block.AbstractTileEntity;
import owmii.lib.block.IInventoryHolder;
import owmii.lib.client.screen.widget.IconButton;
import owmii.lib.logistics.inventory.AbstractTileContainer;
import owmii.lib.network.packets.NextRedstoneModePacket;

public class AbstractTileScreen<T extends AbstractTileEntity<?, ?> & IInventoryHolder, C extends AbstractTileContainer<T>> extends AbstractContainerScreen<C> {
    protected final T te;
    protected IconButton redStoneButton = IconButton.EMPTY;

    public AbstractTileScreen(C container, PlayerInventory inv, ITextComponent title, Texture backGround) {
        super(container, inv, title, backGround);
        this.te = container.te;
    }

    protected void addRedstoneButton(int x, int y) {
        this.redStoneButton = addButton(new IconButton(this.guiLeft + this.xSize + x + 2, this.guiTop + y + 3, Texture.REDSTONE.get(this.te.getRedstoneMode()), b -> {
            Lollipop.NET.toServer(new NextRedstoneModePacket(this.te.getPos()));
            this.te.setRedstoneMode(this.te.getRedstoneMode().next());
        }, this).setTooltip(tooltip -> tooltip.add(this.te.getRedstoneMode().getDisplayName())));
    }

    @Override
    public void tick() {
        super.tick();
        this.redStoneButton.setTexture(Texture.REDSTONE.get(this.te.getRedstoneMode()));
    }

    @Override
    protected void drawBackground(MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        super.drawBackground(matrix, partialTicks, mouseX, mouseY);
        Texture.REDSTONE_BTN_BG.draw(matrix, this.redStoneButton.x - 2, this.redStoneButton.y - 4);
    }
}
