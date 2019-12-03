package zeroneye.lib.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import zeroneye.lib.inventory.ContainerBase;
import zeroneye.lib.util.NBT;

import javax.annotation.Nullable;

public class BlockBase extends Block implements IBlockBase {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final DirectionProperty H_FACING = HorizontalBlock.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public BlockBase(Properties properties) {
        super(properties);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        if (waterLogged()) {
            return !state.get(WATERLOGGED);
        }
        return super.propagatesSkylightDown(state, reader, pos);
    }

    @Override
    public IFluidState getFluidState(BlockState state) {
        if (waterLogged()) {
            return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
        }
        return super.getFluidState(state);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = getDefaultState();
        if (getFacingType().equals(FacingType.HORIZONTAL)) {
            if (!playerFacing()) {
                state = facing(context, false);
            } else {
                state = getDefaultState().with(H_FACING, context.getPlacementHorizontalFacing().getOpposite());
            }
        } else if (getFacingType().equals(FacingType.ALL)) {
            if (!playerFacing()) {
                state = facing(context, true);
            } else {
                state = getDefaultState().with(FACING, context.getNearestLookingDirection().getOpposite());
            }
        }

        if (waterLogged()) {
            IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
            state = state.with(WATERLOGGED, ifluidstate.getFluid() == Fluids.WATER);
        }

        return state;
    }

    @Nullable
    private BlockState facing(BlockItemUseContext context, boolean b) {
        BlockState blockstate = this.getDefaultState();
        IWorldReader iworldreader = context.getWorld();
        BlockPos blockpos = context.getPos();
        Direction[] adirection = context.getNearestLookingDirections();
        for (Direction direction : adirection) {
            if (b || direction.getAxis().isHorizontal()) {
                Direction direction1 = direction.getOpposite();
                blockstate = blockstate.with(getFacingType().equals(FacingType.ALL) ? FACING : H_FACING, direction1);
                if (blockstate.isValidPosition(iworldreader, blockpos)) {
                    return blockstate;
                }
            }
        }
        return null;
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (waterLogged()) {
            if (stateIn.get(WATERLOGGED)) {
                worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
            }
        }
        if ((getFacingType().equals(FacingType.ALL) || (getFacingType().equals(FacingType.HORIZONTAL))) && !playerFacing()) {
            return facing.getOpposite() == stateIn.get(getFacingType().equals(FacingType.ALL) ? FACING : H_FACING) && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : stateIn;
        }
        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public BlockState rotate(BlockState state, IWorld world, BlockPos pos, Rotation direction) {
        if (getFacingType().equals(FacingType.HORIZONTAL)) {
            return state.with(H_FACING, direction.rotate(state.get(H_FACING)));
        } else if (getFacingType().equals(FacingType.ALL)) {
            return state.with(FACING, direction.rotate(state.get(FACING)));
        }
        return state.rotate(direction);
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        if (getFacingType().equals(FacingType.HORIZONTAL)) {
            return state.rotate(mirrorIn.toRotation(state.get(H_FACING)));
        } else if (getFacingType().equals(FacingType.ALL)) {
            return state.rotate(mirrorIn.toRotation(state.get(FACING)));
        }
        return super.mirror(state, mirrorIn);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        if (getFacingType().equals(FacingType.HORIZONTAL)) builder.add(H_FACING);
        else if (getFacingType().equals(FacingType.ALL)) builder.add(FACING);
        if (waterLogged()) builder.add(WATERLOGGED);
    }

    protected FacingType getFacingType() {
        return FacingType.NORMAL;
    }

    private boolean playerFacing() {
        return false;
    }

    protected enum FacingType {
        HORIZONTAL, ALL, NORMAL
    }

    protected boolean waterLogged() {
        return false;
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
    public ContainerBase getContainer(int id, PlayerInventory playerInventory, TileBase.TickableInv inv) {
        return null;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof TileBase) {
            TileBase.TickableInv tile = (TileBase.TickableInv) tileentity;
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
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof TileBase.TickableInv) {
                if (((TileBase.TickableInv) tileentity).dropInventoryOnBreak() || !((TileBase.TickableInv) tileentity).isNBTStorable()) {
                    InventoryHelper.dropInventoryItems(worldIn, pos, (TileBase.TickableInv) tileentity);
                    worldIn.updateComparatorOutputLevel(pos, this);
                }
            }
        }
        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public void harvestBlock(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity tileEntity, ItemStack stack) {
        if (tileEntity instanceof TileBase) {
            TileBase tile = (TileBase) tileEntity;
            ItemStack stack1 = new ItemStack(this);
            CompoundNBT tag = stack1.getTag() != null ? stack1.getTag() : new CompoundNBT();
            CompoundNBT storable = tile.writeStorable(new CompoundNBT());
            if (!storable.isEmpty() && tile.isNBTStorable()) {
                tag.put(NBT.TAG_STACK, storable);
                stack1.setTag(tag);
            }
            if (tile instanceof TileBase.TickableInv) {
                if (((TileBase.TickableInv) tile).hasCustomName()) {
                    stack1.setDisplayName(((TileBase.TickableInv) tile).getCustomName());
                }
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
