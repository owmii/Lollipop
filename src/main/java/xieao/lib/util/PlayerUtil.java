package xieao.lib.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nullable;
import java.util.UUID;

public class PlayerUtil {
    public static boolean isFake(PlayerEntity player) {
        return player instanceof FakePlayer;
    }

    @Nullable
    public static ServerPlayerEntity get(UUID uuid) {
        return ServerUtil.getServer().getPlayerList().getPlayerByUUID(uuid);
    }

    @Nullable
    public static ServerPlayerEntity get(String name) {
        return ServerUtil.getServer().getPlayerList().getPlayerByUsername(name);
    }

    public static boolean hasItem(PlayerEntity player, Item item) {
        return !getItem(player, item).isEmpty();
    }

    public static boolean hasItem(PlayerEntity player, ItemStack stack) {
        return !getItem(player, stack).isEmpty();
    }

    public static ItemStack getItem(PlayerEntity player, Item item) {
        return getItem(player, new ItemStack(item));
    }

    public static ItemStack getItem(PlayerEntity player, ItemStack stack) {
        for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
            ItemStack stack1 = player.inventory.getStackInSlot(i);
            if (stack1.isItemEqual(stack)) {
                return stack1;
            }
        }
        return ItemStack.EMPTY;
    }
}
