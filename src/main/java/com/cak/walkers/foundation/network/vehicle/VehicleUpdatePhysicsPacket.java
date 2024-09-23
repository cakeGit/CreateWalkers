package com.cak.walkers.foundation.network.vehicle;

import com.cak.walkers.content.contraption.NetworkedVehicleData;
import com.cak.walkers.content.contraption.VehicleContraptionEntity;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

public class VehicleUpdatePhysicsPacket extends SimplePacketBase {
    
    VehicleContraptionEntity vehicleEntity;
    
    float prevYaw;
    float yaw;
    float prevPitch;
    float pitch;
    float rotationOffset;
    
    CompoundTag tagData;
    Vec3 vehiclePos;
    
    public VehicleUpdatePhysicsPacket(VehicleContraptionEntity vehicleEntity) {
//        this.vehicleEntity = vehicleEntity;
//        this.tagData = vehicleEntity.vehicleAnimationData.write(new CompoundTag(), vehicleEntity);
//
//        this.prevYaw = vehicleEntity.prevYaw;
//        this.yaw = vehicleEntity.yaw;
//        this.prevPitch = vehicleEntity.prevPitch;
//        this.pitch = vehicleEntity.pitch;
//        this.rotationOffset = vehicleEntity.rotationOffset;
//
//        this.vehiclePos = vehicleEntity.vehicle.getPosition();
    }
    
    public VehicleUpdatePhysicsPacket(FriendlyByteBuf buffer) {
        this.vehicleEntity = NetworkedVehicleData.VEHICLE_ANIMATION_TARGETS.get(buffer.readUUID());
        if (vehicleEntity != null) {
            this.tagData = buffer.readNbt();
            
            this.prevYaw = buffer.readFloat();
            this.yaw = buffer.readFloat();
            this.prevPitch = buffer.readFloat();
            this.pitch = buffer.readFloat();
            this.rotationOffset = buffer.readFloat();
            
            this.vehiclePos = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        }
    }
    
    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(vehicleEntity.getUUID());
        buffer.writeNbt(tagData);
        
        buffer.writeFloat(prevYaw);
        buffer.writeFloat(yaw);
        buffer.writeFloat(prevPitch);
        buffer.writeFloat(pitch);
        buffer.writeFloat(rotationOffset);
        
        buffer.writeDouble(vehiclePos.x);
        buffer.writeDouble(vehiclePos.y);
        buffer.writeDouble(vehiclePos.z);
    }
    
    @Override
    public boolean handle(NetworkEvent.Context context) {
//        if (vehicleEntity != null) {
//            vehicleEntity.vehicleAnimationData.read(tagData);
//            vehicleEntity.prevYaw = vehicleEntity.yaw;
//            vehicleEntity.yaw = this.yaw;
//            vehicleEntity.prevPitch = vehicleEntity.pitch;
//            vehicleEntity.pitch = this.pitch;
//            vehicleEntity.rotationOffset = this.rotationOffset;
//            vehicleEntity.setPos(this.vehiclePos);
//        }
        return true;
    }
    
}
