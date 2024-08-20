package com.cak.walkers.content.contraption;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.render.ContraptionEntityRenderer;
import com.simibubi.create.foundation.outliner.LineOutline;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class VehicleContraptionEntityRenderer extends ContraptionEntityRenderer<VehicleContraptionEntity> {
    
    public VehicleContraptionEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
    
    @Override
    public void render(VehicleContraptionEntity entity, float yaw, float partialTicks, PoseStack ms, MultiBufferSource buffers, int overlay) {
        ms.pushPose();
        NetworkedContraptionLegData ld = entity.legAnimationData;
        if (ld == null) return;
        SuperRenderTypeBuffer buffer = SuperRenderTypeBuffer.getInstance();
        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera()
            .getPosition();
        for (NetworkedLegData legData : ld.contraptionLegData.values()) {
            Vec3 relativePos = legData.getVisualPosition(Mth.lerp(partialTicks, entity.yo, entity.getY()), partialTicks).subtract(entity.getPosition(partialTicks))
                .subtract(0, 1.5, 0);
            CachedBufferer.partial(AllPartialModels.COGWHEEL_SHAFT, AllBlocks.SHAFT.getDefaultState())
                .translate(-0.5, 0, -0.5)
                .translate(relativePos.x, relativePos.y, relativePos.z)
                .renderInto(ms, buffers.getBuffer(RenderType.solid()));
        }
        ms.popPose();
        super.render(entity, yaw, partialTicks, ms, buffers, overlay);
    }
    
}
