package dev.quarris.choppingblock.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.quarris.choppingblock.content.ChoppingBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.vector.Vector3f;

public class ChoppingBlockRenderer extends TileEntityRenderer<ChoppingBlockEntity> {

    public ChoppingBlockRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(ChoppingBlockEntity tile, float delta, MatrixStack ms, IRenderTypeBuffer buffer, int light, int overlay) {
        if (!tile.getItem().isEmpty()) {
            ms.pushPose();
            ms.translate(0.5, 12/16f, 5/16f);
            ms.mulPose(Vector3f.XP.rotation((float) (Math.PI / 2f)));
            Minecraft.getInstance().getItemRenderer().renderStatic(tile.getItem(), ItemCameraTransforms.TransformType.GROUND, light, overlay, ms, buffer);
            ms.popPose();
        }
    }
}
