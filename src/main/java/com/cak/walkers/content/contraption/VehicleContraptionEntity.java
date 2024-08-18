package com.cak.walkers.content.contraption;

import com.jozufozu.flywheel.api.MaterialManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class VehicleContraptionEntity extends AbstractContraptionEntity {
    
    public VehicleContraptionEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }
    
    @Override
    protected void tickContraption() {
        getVehicleContraption();
    }
    
    private void getVehicleContraption() {
    }
    
    @Override
    public Vec3 applyRotation(Vec3 localPos, float partialTicks) {
        return null;
    }
    
    @Override
    public Vec3 reverseRotation(Vec3 localPos, float partialTicks) {
        return null;
    }
    
    @Override
    protected StructureTransform makeStructureTransform() {
        return null;
    }
    
    @Override
    protected float getStalledAngle() {
        return 0;
    }
    
    @Override
    protected void handleStallInformation(double x, double y, double z, float angle) {
        
    }
    
    @Override
    public ContraptionRotationState getRotationState() {
        return null;
    }
    
    @Override
    public void applyLocalTransforms(PoseStack matrixStack, float partialTicks) {
        
    }
    
}
