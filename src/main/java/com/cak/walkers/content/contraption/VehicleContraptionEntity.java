package com.cak.walkers.content.contraption;

import com.cak.walkers.content.registry.WalkersEntityTypes;
import com.cak.walkers.foundation.network.WalkersPackets;
import com.cak.walkers.foundation.network.vehicle.VehicleAnimationDataPacket;
import com.cak.walkers.foundation.vehicle.implementation.ContraptionVehicleImplementation;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
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
        pitch = -(float) (angleOfVehicleGradient(0) * (180f / Math.PI));
        
        prevYaw = yaw;
        yaw = -(float) (vehicle.getYRot() * (180f / Math.PI));
        
        //TODO: check if the properties actually changed
        animationDataChanged = true;
        
        xo = getX();
        yo = getY();
        zo = getZ();
        
        setPos(vehicle.getPosition().add(vehicle.getAnchorOffset()));
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
        entity.startAtInitialYaw();
        assert entity.vehicle != null;
        entity.vehicle.setPosition(0, 4, 0);
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
    
}
