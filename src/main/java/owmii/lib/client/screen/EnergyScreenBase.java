package owmii.lib.client.screen;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;
import owmii.lib.Lollipop;
import owmii.lib.block.TileBase;
import owmii.lib.client.screen.widget.IconButton;
import owmii.lib.client.util.Draw;
import owmii.lib.energy.SideConfig;
import owmii.lib.inventory.EnergyContainerBase;
import owmii.lib.inventory.slot.SlotBase;
import owmii.lib.network.packets.SNextEnergyConfigPacket;
import owmii.lib.network.packets.SNextRedstoneModePacket;
import owmii.lib.util.Empty;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class EnergyScreenBase<T extends TileBase.EnergyStorage, C extends EnergyContainerBase<T>> extends ContainerScreenBase<T, C> {
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(Lollipop.MOD_ID, "textures/gui/container/energy.png");
    private static final ResourceLocation GUI_MACHINE = new ResourceLocation(Lollipop.MOD_ID, "textures/gui/container/blank.png");
    private static final ResourceLocation GUI_WIDGET = new ResourceLocation(Lollipop.MOD_ID, "textures/gui/container/energy_widget.png");
    private static final ResourceLocation GUI_SLOT = new ResourceLocation(Lollipop.MOD_ID, "textures/gui/container/energy_slots.png");
    public static final ResourceLocation GUI_BUFFER = new ResourceLocation(Lollipop.MOD_ID, "textures/gui/container/buffer.png");
    protected IconButton[] configButtons = new IconButton[6];
    protected IconButton configButtonAll = Empty.ICON_BUTTON;
    protected IconButton showConfigButton = Empty.ICON_BUTTON;
    protected IconButton redStoneButton = Empty.ICON_BUTTON;
    protected boolean configVisible;

    public EnergyScreenBase(C container, PlayerInventory playerInventory, ITextComponent name) {
        super(container, playerInventory, name);
        this.xSize = 195;
        this.ySize = 164;
    }

    @Override
    protected void init() {
        super.init();
        boolean b = showConfigButton();
        mainButtons(this.x, this.y + (b ? 0 : -14));
        configButtons(this.x, this.y);
        this.showConfigButton.visible = b;
    }

    protected boolean showConfigButton() {
        return true;
    }

    protected void mainButtons(int x, int y) {
        this.showConfigButton = addIconButton(x + this.xSize - 18, y + 13, 15, 15, 15, 0, 15, getWidgetTexture(), (button) -> {
            this.configVisible = !this.configVisible;
        }).tooltip("info.lollipop.configuration", TextFormatting.GRAY);
        this.redStoneButton = addIconButton(x + this.xSize - 18, y + 29, 15, 15, 30, 0, 15, getWidgetTexture(), (button) -> {
            SNextRedstoneModePacket.send(this.mc.world, this.te.getPos());
            this.te.nextRedstoneMode();
        }).tooltip(this.te.getRedstoneMode().getDisplayName());
    }

    protected void configButtons(int x, int y) {
        this.configButtonAll = addIconButton(x + 151, y + 11, 15, 15, 30, 0, 15, getWidgetTexture(), (button) -> {
            SNextEnergyConfigPacket.send(6, this.mc.world, this.te.getPos());
            this.te.getSideConfig().nextTypeAllSides();
        }).tooltip("info.lollipop.side.all", TextFormatting.GRAY, TextFormatting.DARK_GRAY).tooltip(this.te.getSideConfig().getType(Direction.UP).getDisplayName());
        for (int i = 0; i < 6; i++) {
            int xOffset = getSideButtonOffsets(18).get(i).getLeft();
            int yOffset = getSideButtonOffsets(18).get(i).getRight();
            final Direction side = Direction.byIndex(i);
            this.configButtons[i] = addIconButton(x + 132 + xOffset, y + 28 + yOffset, 17, 17, 0, 30, 17, getWidgetTexture(), (button) -> {
                SNextEnergyConfigPacket.send(side.getIndex(), this.mc.world, this.te.getPos());
                this.te.getSideConfig().nextType(side);
            }).tooltip("info.lollipop.side." + side.getName(), TextFormatting.GRAY, TextFormatting.DARK_GRAY).tooltip(this.te.getSideConfig().getType(side).getDisplayName());
        }
    }

    private List<Pair<Integer, Integer>> getSideButtonOffsets(int spacing) {
        List<Pair<Integer, Integer>> pairs = Lists.newArrayList(
                Pair.of(0, spacing), Pair.of(0, -spacing), Pair.of(0, 0),
                Pair.of(spacing, spacing), Pair.of(-spacing, 0), Pair.of(spacing, 0)
        );
        return pairs;
    }

    @Override
    protected void refreshScreen() {
        this.configButtonAll.visible = this.configVisible;
        SideConfig config = this.te.getSideConfig();
        for (int i = 0; i < this.configButtons.length; i++) {
            this.configButtons[i].visible = this.configVisible;
            this.configButtons[i].setIconDiff(this.te.getSideConfig().getType(Direction.byIndex(i)).getXuv());
            if (this.configVisible && this.configButtons[i].isHovered()) {
                List<String> list = this.configButtons[i].getTooltip();
                list.add(config.getType(Direction.byIndex(i)).getDisplayName());
                list.remove(1);
            }
        }
        this.redStoneButton.setIconDiff(this.te.getRedstoneMode().getXuv());
        if (this.redStoneButton.isHovered()) {
            List<String> list = this.redStoneButton.getTooltip();
            list.add(this.te.getRedstoneMode().getDisplayName());
            list.remove(0);
        }
        if (this.configVisible) {
            if (this.configButtonAll.isHovered()) {
                List<String> list = this.configButtonAll.getTooltip();
                if (!config.isAllSame()) {
                    if (list.size() == 2) {
                        list.remove(1);
                    }
                } else {
                    String s = config.getType(Direction.byIndex(0)).getDisplayName();
                    if (list.size() == 1) {
                        list.add(s);
                    } else {
                        list.add(s);
                        list.remove(1);
                    }
                }
            }
        }
    }

    @Override
    protected void drawForeground(int mouseX, int mouseY) {
        super.drawForeground(mouseX, mouseY);
    }

    @Override
    protected void drawBackground(float partialTicks, int mouseX, int mouseY) {
        super.drawBackground(partialTicks, mouseX, mouseY);
        if (this.configVisible) {
            bindTexture(getConfigBackGround());
        } else {
            bindTexture(getMachineBackGround());
        }
        blit(this.x + 15, this.y, 15, 0, 161, 72);

        if (this.te.defaultEnergyCapacity() > 0) {
            bindTexture(getBackGround());
            Draw.gaugeV(this.x + 4, this.y + 4, 10, 64, 0, 164, this.te.getEnergyStorage());
        }
    }


    @Override
    public void drawSlot(Slot slot) {
        if (hideSlot(slot))
            return;
        bindTexture(getSlotBackGround());
        if (slot instanceof SlotBase) {
            ((SlotBase) slot).drawBG(this);
        }
        super.drawSlot(slot);
    }

    @Override
    protected boolean hideSlot(Slot slot) {
        return slot instanceof SlotBase && this.configVisible || super.hideSlot(slot);
    }

    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY) {
        List<String> list = new ArrayList<>();
        this.te.getListedEnergyInfo(list);
        if (!list.isEmpty() && isMouseOver(mouseX - 3, mouseY - 3, 12, 66)) {
            renderTooltip(list, mouseX, mouseY);
        }
        super.renderHoveredToolTip(mouseX, mouseY);
    }

    protected ResourceLocation getMachineBackGround() {
        return GUI_MACHINE;
    }

    protected ResourceLocation getConfigBackGround() {
        return GUI_MACHINE;
    }

    protected ResourceLocation getWidgetTexture() {
        return GUI_WIDGET;
    }

    @Override
    protected ResourceLocation getBackGround() {
        return GUI_TEXTURE;
    }

    @Override
    protected ResourceLocation getSlotBackGround() {
        return GUI_SLOT;
    }
}
