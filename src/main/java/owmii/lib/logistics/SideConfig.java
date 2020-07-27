package owmii.lib.logistics;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.Constants;
import owmii.lib.block.AbstractEnergyStorage;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static owmii.lib.logistics.TransferType.ALL;
import static owmii.lib.logistics.TransferType.NONE;

public class SideConfig {
    private final TransferType[] sideTypes = new TransferType[]{NONE, NONE, NONE, NONE, NONE, NONE};
    private final AbstractEnergyStorage storage;
    private boolean isSetFromNBT;

    public SideConfig(AbstractEnergyStorage storage) {
        this.storage = storage;
    }

    public void init() {
        if (!this.isSetFromNBT) {
            for (Direction side : Direction.values()) {
                setType(side, this.storage.getTransferType());
            }
        }
    }

    public void read(CompoundNBT nbt) {
        if (nbt.contains("side_transfer_type", Constants.NBT.TAG_INT_ARRAY)) {
            int[] arr = nbt.getIntArray("side_transfer_type");
            for (int i = 0; i < arr.length; i++) {
                this.sideTypes[i] = TransferType.values()[arr[i]];
            }
            this.isSetFromNBT = true;
        }
    }

    public CompoundNBT write(CompoundNBT nbt) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0, valuesLength = this.sideTypes.length; i < valuesLength; i++) {
            list.add(i, this.sideTypes[i].ordinal());
        }
        nbt.putIntArray("side_transfer_type", list);
        return nbt;
    }

    public void nextTypeAll() {
        if (isAllEquals()) {
            for (Direction side : Direction.values()) {
                nextType(side);
            }
        } else {
            for (Direction side : Direction.values()) {
                setType(side, ALL);
            }
        }
    }

    public boolean isAllEquals() {
        int first = this.sideTypes[0].ordinal();
        for (int i = 1; i < 6; i++) {
            if (this.sideTypes[i].ordinal() != first) {
                return false;
            }
        }
        return true;
    }

    public void nextType(@Nullable Direction side) {
        setType(side, getType(side).next(this.storage.getTransferType()));
    }

    public TransferType getType(@Nullable Direction side) {
        if (side != null) {
            return this.sideTypes[side.getIndex()];
        }
        return NONE;
    }

    public void setType(@Nullable Direction side, TransferType type) {
        if (side == null || this.storage.getTransferType().equals(NONE))
            return;
        if (!this.storage.isEnergyPresent(side))
            return;

        this.sideTypes[side.getIndex()] = type;
        this.storage.markDirty();
    }
}
