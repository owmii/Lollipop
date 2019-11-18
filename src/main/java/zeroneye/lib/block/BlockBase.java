package zeroneye.lib.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import zeroneye.lib.inventory.ContainerBase;
import zeroneye.lib.util.NBT;

import javax.annotation.Nullable;

public class BlockBase extends Block implements IBlockBase {
    public BlockBase(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult blockRayTraceResult) {
        TileEntity tileentity = world.getTileEntity(pos);
        if (tileentity instanceof INamedContainerProvider) {
            INamedContainerProvider provider = (INamedContainerProvider) tileentity;
            if (player instanceof ServerPlayerEntity && provider.createMenu(0, player.inventory, player) != null) {
                NetworkHooks.openGui((ServerPlayerEntity) player, provider, pos);
            }
            return true;
        }
        return super.onBlockActivated(state, world, pos, player, hand, blockRayTraceResult);
    }

    @Nullable
    public ContainerBase getContainer(int id, PlayerInventory playerInventory, IInvBase inv) {
        return null;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof TileBase) {
            TileBase tile = (TileBase) tileentity;
            if (stack.hasDisplayName()) {
                tile.setCustomName(stack.getDisplayName());
            } else {
                tile.setDefaultName(getTranslationKey());
            }
            CompoundNBT tag = stack.getTag() != null ? stack.getTag() : new CompoundNBT();
            if (!tag.isEmpty() && shouldStorNBTFromStack(tag)) {
                tile.readStorable(tag.getCompound(NBT.TAG_STACK));
            }
        }
    }

    protected boolean shouldStorNBTFromStack(CompoundNBT compound) {
        return true;
    }

    @Override
    public void harvestBlock(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity tileEntity, ItemStack stack) {
        if (tileEntity instanceof TileBase) {
            TileBase tile = (TileBase) tileEntity;
            ItemStack stack1 = new ItemStack(this);
            CompoundNBT tag = stack1.getTag() != null ? stack1.getTag() : new CompoundNBT();
            CompoundNBT storable = tile.writeStorable(new CompoundNBT());
            if (!storable.isEmpty()) {
                tag.put(NBT.TAG_STACK, storable);
                stack1.setTag(tag);
            }
            if (tile.hasCustomName()) {
                stack1.setDisplayName(tile.getCustomName());
            }
            spawnAsEntity(world, pos, stack1);
            player.addStat(Stats.BLOCK_MINED.get(this));
            player.addExhaustion(0.005F);
        } else {
            super.harvestBlock(world, player, pos, state, null, stack);
        }
    }

    @Override
    public boolean eventReceived(BlockState state, World world, BlockPos pos, int id, int param) {
        TileEntity tileEntity = world.getTileEntity(pos);
        return tileEntity != null && tileEntity.receiveClientEvent(id, param);
    }
}
