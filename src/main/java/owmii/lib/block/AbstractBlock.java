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
import net.minecraft.network.PacketBuffer;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import owmii.lib.inventory.ContainerBase;
import owmii.lib.util.Data;
import owmii.lib.util.IVariant;
import owmii.lib.util.Stack;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public abstract class AbstractBlock<E extends IVariant> extends Block implements IBlock<E> {

    public static final VoxelShape SEMI_FULL_SHAPE = makeCuboidShape(0.01D, 0.01D, 0.01D, 15.99D, 15.99D, 15.99D);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    protected final E variant;

    public AbstractBlock(Properties properties) {
        this(properties, IVariant.getEmpty());
    }

    public AbstractBlock(Properties properties, E variant) {
        super(properties);
        this.variant = variant;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return semiFullShape() ? SEMI_FULL_SHAPE : super.getShape(state, worldIn, pos, context);
    }

    protected boolean semiFullShape() {
        return false;
    }

    protected void setDefaultState() {
        setStateProps(state -> {});
    }

    protected void setStateProps(Consumer<BlockState> consumer) {
        BlockState state = this.stateContainer.getBaseState();
        if (this instanceof IWaterLoggable) {
            state = state.with(WATERLOGGED, false);
        }
        if (!getFacing().equals(Facing.NORMAL)) {
            state = state.with(FACING, Direction.NORTH);
        }
        if (hasLitProp()) {
            state = state.with(LIT, false);
        }
        consumer.accept(state);
        setDefaultState(state);
    }

    @Override
    public boolean isTransparent(BlockState state) {
        return !isSolid(state);
    }

    @Override
    public E getVariant() {
        return this.variant;
    }

    @Nullable
    public <T extends TileBase> ContainerBase getContainer(int id, PlayerInventory inventory, TileBase te) {
        return null;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileBase) {
            ((TileBase) tile).onAdded(world, state, oldState, isMoving);
        }
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileBase) {
            ((TileBase) tile).onRemoved(world, state, newState, isMoving);
        }
        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileBase) {
            ((TileBase) tile).neighborChanged(world, state, pos, block, fromPos, isMoving);
        }
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof INamedContainerProvider) {
            INamedContainerProvider provider = (INamedContainerProvider) tile;
            Container container = provider.createMenu(0, player.inventory, player);
            if (container != null) {
                if (player instanceof ServerPlayerEntity) {
                    NetworkHooks.openGui((ServerPlayerEntity) player, provider, buffer -> {
                        buffer.writeBlockPos(pos);
                        additionalGuiData(buffer, state, world, pos, player, hand, result);
                    });
                }
                return ActionResultType.SUCCESS;
            }
        }
        return super.onBlockActivated(state, world, pos, player, hand, result);
    }

    protected void additionalGuiData(PacketBuffer buffer, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileBase) {
            ((TileBase) tile).onPlaced(world, state, placer, stack);
        }
    }

    @Override
    public void harvestBlock(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        if (te instanceof TileBase) {
            TileBase tile = (TileBase) te;
            ItemStack stack1 = new ItemStack(this);
            CompoundNBT tag = Stack.getTagOrEmpty(stack1);
            CompoundNBT storable = tile.writeStorable(new CompoundNBT());
            if (!storable.isEmpty() && tile.isNBTStorable()) {
                tag.put(Data.TAG_TE_STORABLE, storable);
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
                CompoundNBT tag = Stack.getTagOrEmpty(stack);
                CompoundNBT storable = tile.writeStorable(new CompoundNBT());
                if (!storable.isEmpty() && tile.isNBTStorable()) {
                    tag.put(Data.TAG_TE_STORABLE, storable);
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
        if (getFacing().equals(Facing.HORIZONTAL)) if (!isPlacerFacing()) state = facing(context, false);
        else state = getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
        else if (getFacing().equals(Facing.ALL)) if (!isPlacerFacing()) state = facing(context, true);
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

    protected boolean isPlacerFacing() {
        return false;
    }

    protected Facing getFacing() {
        return Facing.NORMAL;
    }

    protected boolean hasLitProp() {
        return false;
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
        if (getFacing().equals(Facing.ALL) || getFacing().equals(Facing.HORIZONTAL)) builder.add(FACING);
        if (this instanceof IWaterLoggable) builder.add(WATERLOGGED);
        if (hasLitProp()) builder.add(LIT);
    }

    protected enum Facing {
        HORIZONTAL, ALL, NORMAL
    }
}
