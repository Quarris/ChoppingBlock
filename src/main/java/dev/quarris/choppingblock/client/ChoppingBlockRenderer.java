package dev.quarris.choppingblock.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import dev.quarris.choppingblock.content.ChoppingBlock;
import dev.quarris.choppingblock.content.ChoppingBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

public class ChoppingBlockRenderer implements BlockEntityRenderer<ChoppingBlockEntity> {

    public ChoppingBlockRenderer(BlockEntityRendererProvider.Context ctx) {

    }

    @Override
    public void render(ChoppingBlockEntity tile, float delta, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        MultiBufferSource.BufferSource itemBuffer = Minecraft.getInstance().renderBuffers().bufferSource();
        if (!tile.getAxe().isEmpty()) {
            Direction direction = tile.getBlockState().getValue(ChoppingBlock.FACING);
            ms.pushPose();
            this.setRotationsFromDirection(tile, ms, direction);
            Minecraft.getInstance().getItemRenderer().renderStatic(tile.getAxe(), ItemTransforms.TransformType.HEAD, light, overlay, ms, itemBuffer, 0);
            ms.popPose();
        }

        if (!tile.getItem().isEmpty()) {
            ms.pushPose();
            ms.translate(0.5, 12/16f, 5/16f);
            ms.mulPose(Vector3f.XP.rotation((float) (Math.PI / 2f)));
            Minecraft.getInstance().getItemRenderer().renderStatic(tile.getItem(), ItemTransforms.TransformType.GROUND, light, overlay, ms, itemBuffer, 0);
            ms.popPose();
        }
    }

    private void setRotationsFromDirection(ChoppingBlockEntity tile, PoseStack ms, Direction direction) {
        switch (direction) {
            case NORTH -> {
                ms.translate(1.27, 1.75, 0.1);
                ms.mulPose(Vector3f.YP.rotationDegrees(239 + tile.axeAX));
                ms.mulPose(Vector3f.ZP.rotationDegrees(200 + tile.axeAZ));
            }
            case EAST -> {
                ms.translate(0.89, 1.75, 1.25);
                ms.mulPose(Vector3f.YP.rotationDegrees(150 + tile.axeAX));
                ms.mulPose(Vector3f.ZP.rotationDegrees(200 + tile.axeAZ));
            }
            case SOUTH -> {
                ms.translate(-0.25, 1.75, 0.9);
                ms.mulPose(Vector3f.YP.rotationDegrees(60 + tile.axeAX));
                ms.mulPose(Vector3f.ZP.rotationDegrees(200 + tile.axeAZ));
            }
            case WEST -> {
                ms.translate(0.15, 1.75, -0.21);
                ms.mulPose(Vector3f.YP.rotationDegrees(330 + tile.axeAX));
                ms.mulPose(Vector3f.ZP.rotationDegrees(200 + tile.axeAZ));
            }
            default -> {}
        }
    }
}
