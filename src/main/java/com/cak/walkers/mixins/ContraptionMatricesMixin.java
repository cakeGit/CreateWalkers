package com.cak.walkers.mixins;

import com.cak.walkers.content.contraption.VehicleContraptionEntity;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ContraptionMatrices.class, remap = false)
public class ContraptionMatricesMixin {
    
    @Inject(method = "translateToEntity", at = @At("TAIL"))
    private static void translateToEntity(Matrix4f matrix, Entity entity, float partialTicks, CallbackInfo ci) {
        if (entity instanceof VehicleContraptionEntity vce) {
            Vec3 newPos = vce.toGlobalVectorFromVehicle(new Vec3(1, 0.5, 1).subtract(vce.anchorOffset), partialTicks);
            matrix.setTranslation((float) newPos.x, (float) newPos.y, (float) newPos.z);
        }
    }
    
}
