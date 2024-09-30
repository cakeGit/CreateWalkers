package com.cak.walkers.content.contraption;

import com.cak.walkers.content.registry.WalkersEntityTypes;
import com.cak.walkers.foundation.network.WalkersPackets;
import com.cak.walkers.foundation.network.vehicle.ShowDEBUGPositionPacket;
import com.cak.walkers.foundation.vehicle.fake_testing_to_be_replaced.VehiclePhysics;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class VehicleContraptionEntity extends OrientedContraptionEntity {
    
    //TODO Make this into synced datya
    VehiclePhysics vehiclePhysics;
    
    boolean disassembleNextTick = false;
    
    public VehicleContraptionEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }
    
    Vec3 legOffsetCenter;
    
    @Override
    public void tick() {
        if (!(contraption instanceof VehicleContraption vehicleContraption)) return;
        
        if (disassembleNextTick && !this.level().isClientSide) disassemble();
        
        if (vehiclePhysics == null) {
            setupVehiclePhysics(vehicleContraption);
        }
        
        super.tick();
        
        WalkersPackets.sendToNear(level(), blockPosition(), 20, new ShowDEBUGPositionPacket(position()));
        
        vehiclePhysics.tick(this);
    }
    
    private void setupVehiclePhysics(VehicleContraption vehicleContraption) {
        legOffsetCenter = new Vec3(0, 0, 0);
        
        List<VehiclePhysics.VehicleLeg> legs = new ArrayList<>();
        
        if (vehicleContraption.collectedLegPositions.isEmpty()) {
            legs.add(new VehiclePhysics.VehicleLeg(new Vec3(1.5, -2, 1.5)));
            legs.add(new VehiclePhysics.VehicleLeg(new Vec3(-1.5, -2, 1.5)));
            legs.add(new VehiclePhysics.VehicleLeg(new Vec3(-1.5, -2, -1.5)));
            legs.add(new VehiclePhysics.VehicleLeg(new Vec3(1.5, -2, -1.5)));
        } else {
            for (Vec3 pos : vehicleContraption.collectedLegPositions.values()) {
                legs.add(new VehiclePhysics.VehicleLeg(pos.subtract(Vec3.atCenterOf(vehicleContraption.assemblyAnchor))));
            }
        }
        
        int count = 0;
        for (VehiclePhysics.VehicleLeg pos : legs) {
            legOffsetCenter = legOffsetCenter.add(pos.getOffset());
            count++;
        }
        legOffsetCenter = legOffsetCenter.scale(1f / count);
        for (VehiclePhysics.VehicleLeg leg : legs) {
            leg.setOffset(leg.getOffset().subtract(legOffsetCenter.add(0, 2, 0)));
        }
        legOffsetCenter = legOffsetCenter.add(0.5, 0, 0.5);
        vehiclePhysics = new VehiclePhysics(position(), legs);
        setPos(vehiclePhysics.getPosition().subtract(legOffsetCenter));
    }
    
    @Override
    protected void writeAdditional(CompoundTag compound, boolean spawnPacket) {
        super.writeAdditional(compound, spawnPacket);
        if (vehiclePhysics != null) {
            compound.putDouble("VehiclePhysicsX", vehiclePhysics.getPosition().x);
            compound.putDouble("VehiclePhysicsY", vehiclePhysics.getPosition().y);
            compound.putDouble("VehiclePhysicsZ", vehiclePhysics.getPosition().z);
        }
    }
    
    @Override
    protected void readAdditional(CompoundTag compound, boolean spawnPacket) {
        super.readAdditional(compound, spawnPacket);
        if (vehiclePhysics != null)
            vehiclePhysics.setPosition(new Vec3(
                compound.getDouble("VehiclePhysicsX"),
                compound.getDouble("VehiclePhysicsY"),
                compound.getDouble("VehiclePhysicsZ")
            ));
    }
    
    public static VehicleContraptionEntity create(Level world, Contraption contraption, Direction initialOrientation) {
        VehicleContraptionEntity entity =
            new VehicleContraptionEntity(WalkersEntityTypes.WALKER_CONTRAPTION.get(), world);
        entity.setContraption(contraption);
        entity.setInitialOrientation(initialOrientation);
        entity.startAtInitialYaw();
        return entity;
    }
    
    public void disassembleNextTick() {
        disassembleNextTick = true;
    }
    
    public void setPosFromVehicle(Vec3 position) {
        setPos(position.subtract(legOffsetCenter));
    }
    
}
