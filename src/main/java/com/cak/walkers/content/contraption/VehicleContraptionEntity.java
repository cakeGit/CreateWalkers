package com.cak.walkers.content.contraption;

import com.cak.walkers.content.registry.WalkersEntityTypes;
import com.cak.walkers.foundation.network.WalkersPackets;
import com.cak.walkers.foundation.network.vehicle.VehicleAnimationDataPacket;
import com.cak.walkers.foundation.vehicle.implementation.ContraptionVehicleImplementation;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import com.simibubi.create.foundation.utility.AngleHelper;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class VehicleContraptionEntity extends OrientedContraptionEntity {
    
    
    public VehicleContraptionEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }
    
    protected boolean disassembleNextTick = false;
    private boolean animationDataChanged = false;
    /**Server only*/
    public @Nullable ContraptionVehicleImplementation vehicle;
    public NetworkedContraptionLegData legAnimationData = new NetworkedContraptionLegData(this);
    
    public float rotationOffset;
    
    @Override
    public void tick() {
        super.tick();
        
        if (level().isClientSide) {
            if (!NetworkedContraptionLegData.VEHICLE_ANIMATION_TARGETS.containsKey(uuid))
                NetworkedContraptionLegData.VEHICLE_ANIMATION_TARGETS.put(uuid, this);
            legAnimationData.tickAnimations();
            return;
        }
        if (disassembleNextTick)
            disassemble();
        if (vehicle == null)
            return;
        
        if (animationDataChanged) {
            WalkersPackets.sendToNear(level(), blockPosition(), 20, new VehicleAnimationDataPacket(this));
        }
        
        vehicle.setCurrentLevel(level());
        vehicle.tick();
        
        prevPitch = pitch;
        pitch = (float) Math.toDegrees(angleOfVehicleGradient(0));
        
        prevYaw = yaw;
        yaw = -(float) Math.toDegrees(vehicle.getPrimaryYRot());
        
        //TODO: check if the properties actually changed
        animationDataChanged = true;
        
        xo = getX();
        yo = getY();
        zo = getZ();
        
        Vec3 newPos = vehicle.getPosition().add(
            vehicle.getAnchorOffset()
        );
        
        setContraptionMotion(newPos.subtract(position()));
        
        setPos(newPos);
        
        //Change position to match the rotation an shit
        
        if (vehicle.tickNetworkChanges()) {
            animationDataChanged = true;
        }
    }
    
    @Override
    protected void writeAdditional(CompoundTag compound, boolean spawnPacket) {
        super.writeAdditional(compound, spawnPacket);
    }
    
    @Override
    protected void readAdditional(CompoundTag compound, boolean spawnPacket) {
        super.readAdditional(compound, spawnPacket);
    }
    
    public static VehicleContraptionEntity create(Level world, Contraption contraption, Direction initialOrientation) {
        VehicleContraptionEntity entity =
            new VehicleContraptionEntity(WalkersEntityTypes.WALKER_CONTRAPTION.get(), world);
        entity.setContraption(contraption);
//        entity.setInitialOrientation(initialOrientation);
        assert entity.vehicle != null;
        entity.vehicle.setPosition(0, 4, 0);
        entity.rotationOffset = entity.vehicle.getRotationOffset();
        return entity;
    }
    
    @Override
    protected boolean updateOrientation(boolean rotationLock, boolean wasStalled, Entity riding, boolean isOnCoupling) {
        super.updateOrientation(rotationLock, wasStalled, riding, isOnCoupling);
        return true;
    }
    
    private float angleOfVehicleGradient(float partialTicks) {
        return (float) Math.atan2(vehicle.getGradient(partialTicks), 1);
    }
    
    public void disassembleNextTick() {
        disassembleNextTick = true;
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public void applyLocalTransforms(PoseStack matrixStack, float partialTicks) {
        float angleYaw = getViewYRot(partialTicks);
        float anglePitch = getViewXRot(partialTicks);
        
        matrixStack.translate(-.5f, 0, -.5f);
        
        TransformStack.cast(matrixStack)
            .nudge(getId())
            .centre()
            .rotateY(angleYaw)
            .rotateY(Math.toDegrees(rotationOffset))
            .rotateZ(anglePitch)
            .rotateY(-Math.toDegrees(rotationOffset))
            .unCentre();
    }
    
    @Override
    public ContraptionRotationState getRotationState() {
        return new VehicleContraptionRotationState(rotationOffset, pitch, yaw);
    }
    
    public Vec3 applyRotation(Vec3 localPos, float partialTicks) {
        return localPos
            .yRot(AngleHelper.rad(yaw + rotationOffset))
            .zRot(AngleHelper.rad(pitch))
            .yRot(AngleHelper.rad(-rotationOffset));
    }
    
    public Vec3 reverseRotation(Vec3 localPos, float partialTicks) {
        return localPos
            .yRot(AngleHelper.rad(rotationOffset))
            .zRot(AngleHelper.rad(-pitch))
            .yRot(AngleHelper.rad(-(yaw + rotationOffset)));
    }
    
}
