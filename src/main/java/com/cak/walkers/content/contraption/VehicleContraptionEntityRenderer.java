package com.cak.walkers.content.contraption;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.render.ContraptionEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class VehicleContraptionEntityRenderer extends ContraptionEntityRenderer<VehicleContraptionEntity> {
    
    public VehicleContraptionEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
    
    @Override
    public void render(VehicleContraptionEntity entity, float yaw, float partialTicks, PoseStack ms, MultiBufferSource buffers, int overlay) {
        super.render(entity, yaw, partialTicks, ms, buffers, overlay);
    }
    
}
