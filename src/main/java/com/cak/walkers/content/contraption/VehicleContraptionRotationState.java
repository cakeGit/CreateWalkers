package com.cak.walkers.content.contraption;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.foundation.collision.Matrix3d;
import com.simibubi.create.foundation.utility.AngleHelper;

public class VehicleContraptionRotationState extends AbstractContraptionEntity.ContraptionRotationState {
    
    Matrix3d matrix;
    
    float rotationOffset;
    float pitch;
    float yaw;
    
    public VehicleContraptionRotationState(float rotationOffset, float pitch, float yaw) {
        this.rotationOffset = rotationOffset;
        this.pitch = pitch;
        this.yaw = yaw;
    }
    
    @Override
    public Matrix3d asMatrix() {
        if (matrix != null) return matrix;
        matrix = new Matrix3d();
        
        if (yaw + rotationOffset != 0)
            matrix.multiply(new Matrix3d().asYRotation(AngleHelper.rad(yaw + rotationOffset)));
        if (pitch != 0)
            matrix.multiply(new Matrix3d().asZRotation(AngleHelper.rad(pitch)));
        if (-rotationOffset != 0)
            matrix.multiply(new Matrix3d().asYRotation(AngleHelper.rad(-rotationOffset)));
        
        return matrix;
    }
    
    @Override
    public float getYawOffset() {
        return 0;
    }
    
    @Override
    public boolean hasVerticalRotation() {
        return pitch != 0;
    }
    
}
