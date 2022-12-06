package dev.quarris.choppingblock.content;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class ChoppingBlock extends Block {

    private static final VoxelShape SHAPE = VoxelShapes.box(0, 0, 0, 1, 12 / 16f, 1);

    public ChoppingBlock() {
        super(Properties.of(Material.WOOD, MaterialColor.WOOD).harvestTool(ToolType.AXE).strength(2));
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
