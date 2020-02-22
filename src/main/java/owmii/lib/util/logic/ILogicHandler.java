package owmii.lib.util.logic;

import net.minecraft.util.Direction;

import javax.annotation.Nullable;

public interface ILogicHandler {
    default TransferConfig.Type getTransferType(@Nullable Direction side) {
        return getTransferConfig().getType(side);
    }

    TransferConfig getTransferConfig();
}
