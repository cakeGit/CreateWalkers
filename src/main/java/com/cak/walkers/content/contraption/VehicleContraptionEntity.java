package com.cak.walkers.content.contraption;

import com.cak.walkers.content.registry.WalkersEntityTypes;
import com.cak.walkers.foundation.network.WalkersPackets;
import com.cak.walkers.foundation.network.vehicle.ShowDEBUGPositionPacket;
import com.cak.walkers.foundation.network.vehicle.VehicleUpdatePhysicsPacket;
import com.cak.walkers.foundation.vehicle.fake_testing_to_be_replaced.VehiclePhysics;
import com.cak.walkers.foundation.vehicle.implementation.ContraptionVehicleImplementation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllEntityTypes;
import com.simibubi.create.content.contraptions.Contraption;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class VehicleContraptionEntity extends OrientedContraptionEntity {
    
    VehiclePhysics vehiclePhysics;
    
    boolean disassembleNextTick = false;
    
    public VehicleContraptionEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }
    
    Vec3 legOffsetCenter;
    
    @Override
    public void tick() {
        super.tick();
        if (!(contraption instanceof VehicleContraption vehicleContraption)) return;
        
        if (disassembleNextTick && !this.level().isClientSide) disassemble();
        
        if (vehiclePhysics == null) {
            setupVehiclePhysics(vehicleContraption);
        }
        
        WalkersPackets.sendToNear(level(), blockPosition(), 20, new ShowDEBUGPositionPacket(position()));
        
        vehiclePhysics.tick();
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
    
}
