package dev.quarris.choppingblock.content;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
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

    private static final VoxelShape SHAPE = VoxelShapes.box(0, 0, 0, 1, 12/16f, 1);

    public ChoppingBlock() {
        super(Properties.of(Material.WOOD, MaterialColor.WOOD));
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult raytrace) {
        ChoppingBlockEntity choppingBlock = (ChoppingBlockEntity) level.getBlockEntity(pos);
        if (choppingBlock.interact(player, player.getItemInHand(hand))) {
            return ActionResultType.sidedSuccess(level.isClientSide());
        }

        return ActionResultType.FAIL;
    }

    @Override
    public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean p_196243_5_) {
        if (!state.is(newState.getBlock())) {
            TileEntity tile = level.getBlockEntity(pos);
            if (tile instanceof ChoppingBlockEntity) {
                InventoryHelper.dropContents(level, pos, NonNullList.of(((ChoppingBlockEntity) tile).getItem()));
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
