package dev.quarris.choppingblock.content;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class ChoppingBlock extends Block {

    private static final VoxelShape SHAPE = VoxelShapes.box(0, 0, 0, 1, 12 / 16f, 1);
    public static final DirectionProperty FACING = HorizontalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public ChoppingBlock() {
        super(Properties.of(Material.WOOD, MaterialColor.WOOD).harvestTool(ToolType.AXE).strength(2));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public void attack(BlockState state, World level, BlockPos pos, PlayerEntity player) {
        if (!player.isShiftKeyDown()) {
            return;
        }

        TileEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof ChoppingBlockEntity)) {
            return;
        }
        ChoppingBlockEntity choppingBlock = (ChoppingBlockEntity) blockEntity;
        ItemStack main = player.getMainHandItem();
        if (main.getToolTypes().contains(ToolType.AXE)) {
            if (choppingBlock.insertAxe(main)) {
                player.setItemInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                level.playSound(player, pos, SoundEvents.AXE_STRIP, SoundCategory.BLOCKS, 1, 1);
            }
        } else if (main.isEmpty() && choppingBlock.hasAxe()) {
            ItemStack axe = choppingBlock.extractAxe();
            player.setItemInHand(Hand.MAIN_HAND, axe);
            level.playSound(player, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 1, 1);
        }
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult raytrace) {
        TileEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof ChoppingBlockEntity)) {
            return ActionResultType.PASS;
        }

        ChoppingBlockEntity choppingBlock = (ChoppingBlockEntity) blockEntity;
        ItemStack main = player.getMainHandItem();
        if (main.getToolTypes().contains(ToolType.AXE)) {
            if (choppingBlock.hasRecipe()) {
                if (!player.getCooldowns().isOnCooldown(main.getItem()) && hand == Hand.MAIN_HAND) {
                    choppingBlock.doChop(player, main);
                }
                return ActionResultType.sidedSuccess(level.isClientSide());
            }
        }

        ItemStack held = player.getItemInHand(hand);
        if (choppingBlock.interact(player, held)) {
            return ActionResultType.sidedSuccess(level.isClientSide());
        }

        return ActionResultType.PASS;
    }

    public BlockState getStateForPlacement(BlockItemUseContext ctx) {
        BlockState placed = this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
        FluidState fluid = ctx.getLevel().getFluidState(ctx.getClickedPos());
        placed = placed.setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
        return placed;
    }

    @Override
    public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean p_196243_5_) {
        if (!state.is(newState.getBlock())) {
            TileEntity tile = level.getBlockEntity(pos);
            if (tile instanceof ChoppingBlockEntity) {
                if (!level.isClientSide()) {
                    ChoppingBlockEntity choppingBlock = ((ChoppingBlockEntity) tile);
                    InventoryHelper.dropContents(level, pos, NonNullList.of(ItemStack.EMPTY, choppingBlock.getItem(), choppingBlock.getAxe()));
                }

                level.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, level, pos, newState, p_196243_5_);
        }
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public BlockState updateShape(BlockState p_51461_, Direction p_51462_, BlockState p_51463_, IWorld p_51464_, BlockPos p_51465_, BlockPos p_51466_) {
        if (p_51461_.getValue(WATERLOGGED)) {
            p_51464_.getLiquidTicks().scheduleTick(p_51465_, Fluids.WATER, Fluids.WATER.getTickDelay(p_51464_));
        }

        return super.updateShape(p_51461_, p_51462_, p_51463_, p_51464_, p_51465_, p_51466_);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
        return SHAPE;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ChoppingBlockEntity();
    }
}
