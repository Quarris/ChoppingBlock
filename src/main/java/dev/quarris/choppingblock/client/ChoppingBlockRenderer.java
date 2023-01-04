package dev.quarris.choppingblock.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.quarris.choppingblock.content.ChoppingBlock;
import dev.quarris.choppingblock.content.ChoppingBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;

public class ChoppingBlockRenderer extends TileEntityRenderer<ChoppingBlockEntity> {

    public ChoppingBlockRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(ChoppingBlockEntity tile, float delta, MatrixStack ms, IRenderTypeBuffer buffer, int light, int overlay) {
        IRenderTypeBuffer itemBuffer = Minecraft.getInstance().renderBuffers().bufferSource();
        if (!tile.getAxe().isEmpty()) {
            Direction direction = tile.getBlockState().getValue(ChoppingBlock.FACING);
            ms.pushPose();
            this.setRotationsFromDirection(tile, ms, direction);
            Minecraft.getInstance().getItemRenderer().renderStatic(tile.getAxe(), ItemCameraTransforms.TransformType.HEAD, light, overlay, ms, itemBuffer);
            ms.popPose();
        }

        if (!tile.getItem().isEmpty()) {
            ms.pushPose();
            ms.translate(0.5, 12/16f, 5/16f);
            ms.mulPose(Vector3f.XP.rotation((float) (Math.PI / 2f)));
            Minecraft.getInstance().getItemRenderer().renderStatic(tile.getItem(), ItemCameraTransforms.TransformType.GROUND, light, overlay, ms, itemBuffer);
            ms.popPose();
        }
    }

    private void setRotationsFromDirection(ChoppingBlockEntity tile, MatrixStack ms, Direction direction) {
        switch (direction) {
            case NORTH: {
                ms.translate(1.27, 1.75, 0.1);
                ms.mulPose(Vector3f.YP.rotationDegrees(239 + tile.axeAX));
                ms.mulPose(Vector3f.ZP.rotationDegrees(200 + tile.axeAZ));
                break;
            }
            case EAST: {
                ms.translate(0.89, 1.75, 1.25);
                ms.mulPose(Vector3f.YP.rotationDegrees(150 + tile.axeAX));
                ms.mulPose(Vector3f.ZP.rotationDegrees(200 + tile.axeAZ));
                break;
            }
            case SOUTH: {
                ms.translate(-0.25, 1.75, 0.9);
                ms.mulPose(Vector3f.YP.rotationDegrees(60 + tile.axeAX));
                ms.mulPose(Vector3f.ZP.rotationDegrees(200 + tile.axeAZ));
                break;
            }
            case WEST: {
                ms.translate(0.15, 1.75, -0.21);
                ms.mulPose(Vector3f.YP.rotationDegrees(330 + tile.axeAX));
                ms.mulPose(Vector3f.ZP.rotationDegrees(200 + tile.axeAZ));
                break;
            }
            default: {}
        }
    }
}
