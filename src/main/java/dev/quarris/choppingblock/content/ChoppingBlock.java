package dev.quarris.choppingblock.content;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ToolActions;

public class ChoppingBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

    private static final VoxelShape SHAPE = Shapes.box(0, 0, 0, 1, 12 / 16f, 1);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public ChoppingBlock() {
        super(Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2).sound(SoundType.WOOD));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!player.isShiftKeyDown()) {
            return;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof ChoppingBlockEntity)) {
            return;
        }
        ChoppingBlockEntity choppingBlock = (ChoppingBlockEntity) blockEntity;
        ItemStack main = player.getMainHandItem();

        if (main.canPerformAction(ToolActions.AXE_DIG)) {
            if (choppingBlock.insertAxe(main)) {
                player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                level.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1, 1);
            }
        } else if (main.isEmpty() && choppingBlock.hasAxe()) {
            ItemStack axe = choppingBlock.extractAxe();
            player.setItemInHand(InteractionHand.MAIN_HAND, axe);
            level.playSound(player, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1, 1);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof ChoppingBlockEntity)) {
            return InteractionResult.PASS;
        }

        ChoppingBlockEntity choppingBlock = (ChoppingBlockEntity) blockEntity;
        ItemStack main = player.getMainHandItem();
        if (main.canPerformAction(ToolActions.AXE_DIG)) {
            if (choppingBlock.hasRecipe()) {
                if (!player.getCooldowns().isOnCooldown(main.getItem()) && hand == InteractionHand.MAIN_HAND) {
                    choppingBlock.doChop(player, main);
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }

        ItemStack held = player.getItemInHand(hand);
        if (choppingBlock.interact(player, held)) {
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return InteractionResult.PASS;
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState placed = this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
        FluidState fluid = ctx.getLevel().getFluidState(ctx.getClickedPos());
        placed = placed.setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
        return placed;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean p_196243_5_) {
        if (!state.is(newState.getBlock())) {
            BlockEntity tile = level.getBlockEntity(pos);
            if (tile instanceof ChoppingBlockEntity) {
                if (!level.isClientSide()) {
                    ChoppingBlockEntity choppingBlock = ((ChoppingBlockEntity) tile);
                    Containers.dropContents(level, pos, NonNullList.of(ItemStack.EMPTY, choppingBlock.getItem(), choppingBlock.getAxe()));
                }

                level.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, level, pos, newState, p_196243_5_);
        }
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public BlockState updateShape(BlockState p_51461_, Direction p_51462_, BlockState p_51463_, LevelAccessor p_51464_, BlockPos p_51465_, BlockPos p_51466_) {
        if (p_51461_.getValue(WATERLOGGED)) {
            p_51464_.scheduleTick(p_51465_, Fluids.WATER, Fluids.WATER.getTickDelay(p_51464_));
        }

        return super.updateShape(p_51461_, p_51462_, p_51463_, p_51464_, p_51465_, p_51466_);
    }

    @Override
    public RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ChoppingBlockEntity(pos, state);
    }
}
