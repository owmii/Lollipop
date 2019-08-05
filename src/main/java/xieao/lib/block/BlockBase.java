package xieao.lib.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockBase extends Block implements IBlockBase {
    public BlockBase(Properties properties) {
        super(properties);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof TileBase) {
            TileBase tile = (TileBase) tileentity;
            CompoundNBT tag = stack.getTag() != null ? stack.getTag() : new CompoundNBT();
            tile.readStorable(tag.getCompound("TileStorableNBT"));
        }
    }

    @Override
    public void harvestBlock(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity tileEntity, ItemStack stack) {
        if (tileEntity instanceof TileBase) {
            TileBase tile = (TileBase) tileEntity;
            ItemStack stack1 = new ItemStack(this);
            CompoundNBT tag = stack1.getTag() != null ? stack1.getTag() : new CompoundNBT();
            CompoundNBT storable = tile.writeStorable(new CompoundNBT());
            if (!storable.isEmpty()) {
                tag.put("TileStorableNBT", storable);
                stack1.setTag(tag);
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
