package owmii.lib.client.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.tuple.Pair;
import owmii.lib.Lollipop;
import owmii.lib.block.AbstractEnergyStorage;
import owmii.lib.block.IInventoryHolder;
import owmii.lib.client.screen.widget.IconButton;
import owmii.lib.logistics.inventory.AbstractEnergyContainer;
import owmii.lib.network.packets.NextEnergyConfigPacket;

import java.util.List;

public class AbstractEnergyScreen<T extends AbstractEnergyStorage<?, ?, ?> & IInventoryHolder, C extends AbstractEnergyContainer<T>> extends AbstractTileScreen<T, C> {
    protected IconButton[] configButtons = new IconButton[6];
    protected IconButton configButtonAll = IconButton.EMPTY;

    public AbstractEnergyScreen(C container, PlayerInventory inv, ITextComponent title, Texture backGround) {
        super(container, inv, title, backGround);
    }

    @Override
    protected void func_231160_c_() {
        super.func_231160_c_();
        if (hasConfigButtons()) {
            addSideConfigButtons(0, 4);
        }
        if (hasRedstoneButton()) {
            addRedstoneButton(0, 31);
        }
    }

    protected void addSideConfigButtons(int x, int y) {
        for (int i = 0; i < 6; i++) {
            final int id = i;
            Pair<Integer, Integer> offset = getSideButtonOffsets(6).get(i);
            int xOffset = offset.getLeft();
            int yOffset = offset.getRight();
            Direction side = Direction.byIndex(i);
            this.configButtons[i] = func_230480_a_(new IconButton(this.guiLeft + xOffset + this.xSize + x + 8, this.guiTop + yOffset + y + 10, Texture.CONFIG.get(this.te.getSideConfig().getType(side)), button -> {
                Lollipop.NET.toServer(new NextEnergyConfigPacket(id, this.te.getPos()));
                this.te.getSideConfig().nextType(side);
            }, this).setTooltip(tooltip -> {
                tooltip.add(new TranslationTextComponent("info.lollipop.side." + side.func_176610_l(), TextFormatting.DARK_GRAY).func_240699_a_(TextFormatting.GRAY));
                tooltip.add(this.te.getSideConfig().getType(side).getDisplayName());
            }));
        }

        this.configButtonAll = func_230480_a_(new IconButton(this.guiLeft + this.xSize + x + 14, this.guiTop + y + 4, Texture.CONFIG_BTN, button -> {
            Lollipop.NET.toServer(new NextEnergyConfigPacket(6, this.te.getPos()));
            this.te.getSideConfig().nextTypeAll();
        }, this).setTooltip(tooltip -> {
            tooltip.add(new TranslationTextComponent("info.lollipop.side.all", TextFormatting.DARK_GRAY).func_240699_a_(TextFormatting.GRAY));
            tooltip.add(this.te.getSideConfig().getType(Direction.UP).getDisplayName());
        }));
    }

    @Override
    public void func_231023_e_() {
        super.func_231023_e_();
        if (hasConfigButtons()) {
            for (int i = 0; i < 6; i++) {
                this.configButtons[i].setTexture(Texture.CONFIG.get(this.te.getSideConfig().getType(Direction.byIndex(i))));
            }
        }
    }

    private List<Pair<Integer, Integer>> getSideButtonOffsets(int spacing) {
        return Lists.newArrayList(Pair.of(0, spacing), Pair.of(0, -spacing), Pair.of(0, 0), Pair.of(spacing, spacing), Pair.of(-spacing, 0), Pair.of(spacing, 0));
    }

    @Override
    protected void drawBackground(MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        super.drawBackground(matrix, partialTicks, mouseX, mouseY);
        if (hasConfigButtons()) {
            Texture.CONFIG_BTN_BG.draw(matrix, this.configButtons[1].field_230690_l_ - 8, this.configButtons[1].field_230691_m_ - 4);
        }
    }

    @Override
    protected void drawForeground(MatrixStack matrix, int mouseX, int mouseY) {
        super.drawForeground(matrix, mouseX, mouseY);
        String title = this.field_230704_d_.getString();
        int width = this.field_230712_o_.getStringWidth(title);
        this.field_230712_o_.func_238405_a_(matrix, title, this.xSize / 2 - width / 2, -14.0F, 0x555555);
    }

    @Override
    public List<Rectangle2d> getExtraAreas() {
        final List<Rectangle2d> extraAreas = super.getExtraAreas();
        if (hasConfigButtons()) {
            Texture texture = Texture.CONFIG_BTN_BG;
            extraAreas.add(new Rectangle2d(this.configButtons[1].field_230690_l_ - 8, this.configButtons[1].field_230691_m_ - 4, texture.getWidth(), texture.getHeight()));
        }
        return extraAreas;
    }

    protected boolean hasConfigButtons() {
        return true;
    }

    protected boolean hasRedstoneButton() {
        return true;
    }
}
