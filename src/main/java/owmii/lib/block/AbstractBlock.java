package owmii.lib.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.container.Container;
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
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import owmii.lib.inventory.ContainerBase;
import owmii.lib.util.Data;
import owmii.lib.util.Stack;

import javax.annotation.Nullable;

public abstract class AbstractBlock extends Block implements IBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public AbstractBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    public ContainerBase getContainer(int id, PlayerInventory inventory, TileBase te) {
        return null;
    }

    @Override
    public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof INamedContainerProvider) {
            INamedContainerProvider provider = (INamedContainerProvider) tile;
            Container container = provider.createMenu(0, player.inventory, player);
            if (container != null) {
                if (player instanceof ServerPlayerEntity) {
                    NetworkHooks.openGui((ServerPlayerEntity) player, provider, pos);
                }
                return ActionResultType.SUCCESS;
            }
        }
        return super.func_225533_a_(state, world, pos, player, hand, result);
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
            CompoundNBT tag = Stack.checkNBT(stack);
            if (!tag.isEmpty()) {
                tile.readStorable(tag.getCompound(Data.TAG_STORABLE));
            }
        }
    }

    @Override
    public void harvestBlock(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        if (te instanceof TileBase) {
            TileBase tile = (TileBase) te;
            ItemStack stack1 = new ItemStack(this);
            CompoundNBT tag = Stack.checkNBT(stack1);
            CompoundNBT storable = tile.writeStorable(new CompoundNBT());
            if (!storable.isEmpty() && tile.isNBTStorable()) {
                tag.put(Data.TAG_STORABLE, storable);
                stack1.setTag(tag);
            }
            if (tile.hasCustomName()) stack1.setDisplayName(tile.getCustomName());
            spawnAsEntity(world, pos, stack1);
            player.addStat(Stats.BLOCK_MINED.get(this));
            player.addExhaustion(0.005F);
        } else {
            super.harvestBlock(world, player, pos, state, te, stack);
        }
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
        if (this instanceof IWaterLoggable && state.get(WATERLOGGED))
            world.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(world));

        if (!state.isValidPosition(world, currentPos)) {
            TileEntity tileEntity = world.getTileEntity(currentPos);
            if (!world.isRemote() && tileEntity instanceof TileBase) {
                TileBase tile = (TileBase) tileEntity;
                ItemStack stack = new ItemStack(this);
                CompoundNBT tag = Stack.checkNBT(stack);
                CompoundNBT storable = tile.writeStorable(new CompoundNBT());
                if (!storable.isEmpty() && tile.isNBTStorable()) {
                    tag.put(Data.TAG_STORABLE, storable);
                    stack.setTag(tag);
                }
                if (tile.hasCustomName()) {
                    stack.setDisplayName(tile.getCustomName());
                }
                spawnAsEntity((World) world, currentPos, stack);
                world.destroyBlock(currentPos, false);
            }
        }

        return super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return getFluidState(state).isEmpty() || super.propagatesSkylightDown(state, reader, pos);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = getDefaultState();
        if (getFacing().equals(Facing.HORIZONTAL)) if (!placerFacing()) state = facing(context, false);
        else state = getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
        else if (getFacing().equals(Facing.ALL)) if (!placerFacing()) state = facing(context, true);
        else state = getDefaultState().with(FACING, context.getNearestLookingDirection().getOpposite());
        if (state != null && this instanceof IWaterLoggable) {
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
        Direction[] directions = context.getNearestLookingDirections();
        for (Direction direction : directions) {
            if (b || direction.getAxis().isHorizontal()) {
                Direction direction1 = b ? direction : direction.getOpposite();
                blockstate = blockstate.with(FACING, direction1);
                if (blockstate.isValidPosition(iworldreader, blockpos)) {
                    return blockstate;
                }
            }
        }
        return null;
    }

    @Override
    public BlockState rotate(BlockState state, IWorld world, BlockPos pos, Rotation direction) {
        if (!getFacing().equals(Facing.NORMAL)) {
            for (Rotation rotation : Rotation.values()) {
                if (!rotation.equals(Rotation.NONE)) {
                    if (isValidPosition(super.rotate(state, world, pos, rotation), world, pos)) {
                        return super.rotate(state, world, pos, rotation);
                    }
                }
            }
        }
        return state;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        if (getFacing().equals(Facing.ALL) || getFacing().equals(Facing.HORIZONTAL)) {
            return state.with(FACING, rot.rotate(state.get(FACING)));
        }
        return super.rotate(state, rot);
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        if (getFacing().equals(Facing.ALL) || getFacing().equals(Facing.HORIZONTAL)) {
            return state.rotate(mirror.toRotation(state.get(FACING)));
        }
        return super.mirror(state, mirror);
    }

    protected boolean placerFacing() {
        return false;
    }

    protected Facing getFacing() {
        return Facing.NORMAL;
    }

    @Override
    public IFluidState getFluidState(BlockState state) {
        return this instanceof IWaterLoggable && state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    public boolean eventReceived(BlockState state, World world, BlockPos pos, int id, int param) {
        TileEntity tileEntity = world.getTileEntity(pos);
        return tileEntity != null && tileEntity.receiveClientEvent(id, param);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        if (getFacing().equals(Facing.ALL) || getFacing().equals(Facing.HORIZONTAL)) builder.add(WATERLOGGED);
        if (this instanceof IWaterLoggable) builder.add(WATERLOGGED);
    }

    protected enum Facing {
        HORIZONTAL, ALL, NORMAL
    }
}
