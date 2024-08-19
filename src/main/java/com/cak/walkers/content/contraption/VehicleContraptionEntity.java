package com.cak.walkers.content.contraption;

import com.cak.walkers.content.registry.WalkersEntityTypes;
import com.cak.walkers.foundation.vehicle.implementation.ContraptionVehicleImplementation;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllEntityTypes;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class VehicleContraptionEntity extends OrientedContraptionEntity {
    
    public VehicleContraptionEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }
    
    boolean disassembleNextTick = false;
    ContraptionVehicleImplementation vehicle;
    
    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || vehicle == null) return;
        
        if (disassembleNextTick)
            disassemble();
        
        vehicle.setCurrentLevel(level());
        vehicle.tick();
        
        prevPitch = pitch;
        pitch = angleOfVehicleGradient(0);
        
        prevYaw = yaw;
        yaw = angleOfVehicleGradient(0);
        
        setPos(vehicle.getPosition());
        xo = getX();
        yo = getY();
        zo = getZ();
        //Change position to match the rotation an shit
    }
    
    @Override
    protected void writeAdditional(CompoundTag compound, boolean spawnPacket) {
        super.writeAdditional(compound, spawnPacket);
    }
    
    @Override
    protected void readAdditional(CompoundTag compound, boolean spawnPacket) {
        super.readAdditional(compound, spawnPacket);
    }
    
    @Override
    protected void tickContraption() {
        super.tickContraption();
    }
    
    public static VehicleContraptionEntity create(Level world, Contraption contraption, Direction initialOrientation) {
        VehicleContraptionEntity entity =
            new VehicleContraptionEntity(WalkersEntityTypes.WALKER_CONTRAPTION.get(), world);
        entity.setContraption(contraption);
        entity.setInitialOrientation(initialOrientation);
        entity.startAtInitialYaw();
        return entity;
    }
    
    private float angleOfVehicleGradient(float partialTicks) {
        return (float) Math.atan2(vehicle.getGradient(partialTicks), 1);
    }
    
    public void disassembleNextTick() {
        disassembleNextTick = true;
    }
    
}
