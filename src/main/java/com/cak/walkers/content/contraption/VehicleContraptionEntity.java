package com.cak.walkers.content.contraption;

import com.cak.walkers.content.registry.WalkersEntityTypes;
import com.cak.walkers.foundation.network.WalkersPackets;
import com.cak.walkers.foundation.network.vehicle.ShowDEBUGPositionPacket;
import com.cak.walkers.foundation.network.vehicle.VehicleUpdatePhysicsPacket;
import com.cak.walkers.foundation.vehicle.implementation.ContraptionVehicleImplementation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class VehicleContraptionEntity extends OrientedContraptionEntity {
    
    public VehicleContraptionEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }
    
    protected boolean disassembleNextTick = false;
    /**
     * Server only
     */
    public @Nullable ContraptionVehicleImplementation vehicle;
    public NetworkedVehicleData vehicleAnimationData = new NetworkedVehicleData(this);
    
    public float rotationOffset;
    
    public Vec3 anchorOffset;
    public Vec3 vehiclePos;
    
    @Override
    public void tick() {
        super.tick();
        
        if (level().isClientSide) {
            if (!NetworkedVehicleData.VEHICLE_ANIMATION_TARGETS.containsKey(uuid))
                NetworkedVehicleData.VEHICLE_ANIMATION_TARGETS.put(uuid, this);
            vehicleAnimationData.tickAnimations();
//            Vec3 newPos = toGlobalVectorFromVehicle(
//                new Vec3(1, 0.5, 1)
//                    .subtract(anchorOffset), 1f
//            );
//            setPos(newPos);
            return;
        }
        if (disassembleNextTick)
            disassemble();
        if (vehicle == null)
            return;
        
        vehicle.tick();

//        prevPitch = pitch;
        pitch = (float) Math.toDegrees(angleOfVehicleGradient(0));

//        prevYaw = yaw;
        yaw = -(float) Math.toDegrees(vehicle.getYRot() - rotationOffset);

//        xo = getX();
//        yo = getY();
//        zo = getZ();
//
//        xOld = getX();
//        yOld = getY();
//        zOld = getZ();

//        Vec3 newPos = toGlobalVectorFromVehicle(
//            new Vec3(1, 0.5, 1)
//                .subtract(vehicle.getAnchorOffset()), 1f
//        );


//        setContraptionMotion(newPos.subtract(position()));
//        setPos(newPos);
        
        //Change position to match the rotation an shit

//        Vec3 newPos = toGlobalVectorFromVehicle(
//            new Vec3(1, 0.5, 1)
//                .add(vehicle.getAnchorOffset()), 1f
//        );
        Vec3 newPos = toGlobalVectorFromVehicle(
            new Vec3(1, 0.5, 1)
                .subtract(vehicle.getAnchorOffset()), 1f
        );
        setPos(newPos);
        
        vehicle.tickNetworkChanges();

//
        WalkersPackets.sendToNear(level(), blockPosition(), 20, new ShowDEBUGPositionPacket(newPos));
        WalkersPackets.sendToNear(level(), blockPosition(), 20, new VehicleUpdatePhysicsPacket(this));
    }
    
    @Override
    public boolean control(BlockPos controlsLocalPos, Collection<Integer> heldControls, Player player) {
        //temp until vehicle is written
        if (vehicle == null) return false;
        
        if (this.level().isClientSide) {
            return true;
        } else if (player.isSpectator()) {
            return false;
        } else if (!this.toGlobalVector(VecHelper.getCenterOf(controlsLocalPos), 1.0F).closerThan(player.position(), 8.0)) {
            return false;
        } else if (heldControls.contains(5)) {
            return false;
        }
        
        float moveDelta = (heldControls.contains(1) ? 1 : 0) + (heldControls.contains(2) ? -1 : 0);
        float rotDelta = (heldControls.contains(3) ? 1 : 0) + (heldControls.contains(4) ? -1 : 0);
        
        this.vehicle.applyControlInput(moveDelta, rotDelta);
        
        return true;
    }
    
    public boolean startControlling(BlockPos controlsLocalPos, Player player) {
        if (player != null && !player.isSpectator()) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public void stopControlling(BlockPos controlsLocalPos) {
        super.stopControlling(controlsLocalPos);
        
        if (this.vehicle != null) {
            this.vehicle.setControlInput(0, 0);
        }
    }
    
    @Override
    public boolean isControlledByLocalInstance() {
        return true;
    }
    
    public Vec3 toGlobalVectorFromVehicle(Vec3 localVec, float partialTicks) {
        Vec3 anchor = vehicle == null ? vehiclePos == null ? new Vec3(0, 0, 0) : vehiclePos : vehicle.getPosition();
        Vec3 rotationOffset = VecHelper.getCenterOf(BlockPos.ZERO);
        localVec = localVec.subtract(rotationOffset);
        localVec = applyRotation(localVec, partialTicks);
        localVec = localVec.add(rotationOffset)
            .add(anchor);
        return localVec;
    }
    
    @Override
    public void applyLocalTransforms(PoseStack matrixStack, float partialTicks) {
        super.applyLocalTransforms(matrixStack, partialTicks);
    }
    
    @Override
    protected void writeAdditional(CompoundTag compound, boolean spawnPacket) {
        super.writeAdditional(compound, spawnPacket);
        compound.putFloat("anchorPosX", (float) anchorOffset.x);
        compound.putFloat("anchorPosY", (float) anchorOffset.y);
        compound.putFloat("anchorPosZ", (float) anchorOffset.z);
    }
    
    @Override
    protected void readAdditional(CompoundTag compound, boolean spawnPacket) {
        super.readAdditional(compound, spawnPacket);
        anchorOffset = new Vec3(
            compound.getFloat("anchorPosX"),
            compound.getFloat("anchorPosY"),
            compound.getFloat("anchorPosZ")
        );
    }
    
    public static VehicleContraptionEntity create(Level world, VehicleContraption contraption, Direction initialOrientation) {
        VehicleContraptionEntity entity =
            new VehicleContraptionEntity(WalkersEntityTypes.WALKER_CONTRAPTION.get(), world);
        entity.setContraption(contraption);
        assert entity.vehicle != null;
        entity.vehicle.setPosition(Vec3.atCenterOf(contraption.anchor).add(entity.vehicle.getAnchorOffset()).subtract(1, 1, 1));
        entity.setPos(entity.toGlobalVectorFromVehicle(new Vec3(1, 0.5, 1).subtract(entity.vehicle.getAnchorOffset()), 0f));
        entity.setPrevPos(entity.position());
        entity.anchorOffset = contraption.vehicle.getAnchorOffset();
        entity.rotationOffset = entity.vehicle.getRotationOffset();
        entity.yaw = entity.vehicle.getRotationOffset();
        entity.prevYaw = entity.yaw;
        entity.vehicle.setCurrentLevel(world);
        return entity;
    }
    
    private void setPrevPos(Vec3 position) {
        xo = position.x;
        yo = position.y;
        zo = position.z;
    }
    
    private float angleOfVehicleGradient(float partialTicks) {
        return (float) Math.atan2(vehicle.getGradient(partialTicks), 1);
    }
    
    public void disassembleNextTick() {
        disassembleNextTick = true;
    }
    
    @Override
    public float getInitialYaw() {
        return (float) Math.toDegrees(rotationOffset);
    }
    
    public void setOldPos(Vec3 oldPosition) {
        xo = oldPosition.x;
        yo = oldPosition.y;
        zo = oldPosition.z;
    }
    
}
