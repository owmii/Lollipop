package owmii.lib.util.logic;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static owmii.lib.util.logic.TransferConfig.Type.*;


public class TransferConfig {
    private final Type[] sideTypes = new Type[]{IN, IN, IN, IN, IN, IN};
    private final ILogicHandler handler;

    public TransferConfig(ILogicHandler storage) {
        this.handler = storage;
    }

    public void init() {
        for (Direction side : Direction.values()) {
            setType(side, IN);
        }
    }

    public void read(CompoundNBT nbt) {
        if (nbt.contains("SideConfigType", Constants.NBT.TAG_INT_ARRAY)) {
            int[] arr = nbt.getIntArray("SideConfigType");
            for (int i = 0; i < arr.length; i++) {
                this.sideTypes[i] = Type.values()[arr[i]];
            }
        }
    }

    public CompoundNBT write(CompoundNBT nbt) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0, valuesLength = this.sideTypes.length; i < valuesLength; i++) {
            list.add(i, this.sideTypes[i].ordinal());
        }
        nbt.putIntArray("SideConfigType", list);
        return nbt;
    }

    public void nextTypeAllSides() {
        if (isAllSame()) {
            for (Direction side : Direction.values()) {
                nextType(side);
            }
        } else {
            for (Direction side : Direction.values()) {
                setType(side, OUT);
            }
        }
    }

    public boolean isAllSame() {
        int first = this.sideTypes[0].ordinal();
        for (int i = 1; i < 6; i++) {
            if (this.sideTypes[i].ordinal() != first) {
                return false;
            }
        }
        return true;
    }

    public void nextType(@Nullable Direction side) {
        setType(side, getType(side).next(this.handler.getTransferConfig().getType(side)));
    }

    public Type getType(@Nullable Direction side) {
        if (side != null) {
            return this.sideTypes[side.getIndex()];
        }
        return OFF;
    }

    public void setType(@Nullable Direction side, Type type) {
        if (side == null)
            return;
        this.sideTypes[side.getIndex()] = type;
    }

    public enum Type {
        IN(34, false, true, TextFormatting.DARK_GRAY),
        OUT(17, true, false, TextFormatting.DARK_GRAY),
        OFF(51, false, false, TextFormatting.DARK_RED);

        private final int xuv;
        private final boolean canExtract;
        private final boolean canReceive;
        private final TextFormatting color;

        Type(int xuv, boolean canExtract, boolean canReceive, TextFormatting color) {
            this.xuv = xuv;
            this.canExtract = canExtract;
            this.canReceive = canReceive;
            this.color = color;
        }

        public boolean canExtract() {
            return this.canExtract;
        }

        public boolean canReceive() {
            return this.canReceive;
        }

        public Type next() {
            return next(OUT);
        }

        public Type next(Type type) {
            if (type.isIn()) {
                return OUT;
            } else if (type.isOut()) {
                return OFF;
            }
            return IN;
        }

        public boolean isOneType() {
            return isOut() || isIn();
        }


        public boolean isOut() {
            return equals(OUT);
        }

        public boolean isIn() {
            return equals(IN);
        }

        public boolean isOff() {
            return equals(OFF);
        }

        public int getXuv() {
            return this.xuv;
        }

        public String getDisplayName() {
            return TextFormatting.GRAY + I18n.format("info.lollipop.side.config." + name().toLowerCase(), this.color);
        }
    }
}
