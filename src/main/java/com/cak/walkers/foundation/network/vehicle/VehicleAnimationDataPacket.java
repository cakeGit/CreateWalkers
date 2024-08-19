package com.cak.walkers.foundation.network.vehicle;

import com.cak.walkers.content.contraption.NetworkedContraptionLegData;
import com.cak.walkers.content.contraption.VehicleContraptionEntity;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class VehicleAnimationDataPacket extends SimplePacketBase {
    
    VehicleContraptionEntity vehicleEntity;
    
    float prevYaw;
    float yaw;
    float prevPitch;
    float pitch;
    
    CompoundTag tagData;
    
    public VehicleAnimationDataPacket(VehicleContraptionEntity vehicleEntity) {
        this.vehicleEntity = vehicleEntity;
        this.tagData = vehicleEntity.legAnimationData.write(new CompoundTag(), vehicleEntity);
        
        this.prevYaw = vehicleEntity.prevYaw;
        this.yaw = vehicleEntity.yaw;
        this.prevPitch = vehicleEntity.prevPitch;
        this.pitch = vehicleEntity.pitch;
    }
    
    public VehicleAnimationDataPacket(FriendlyByteBuf buffer) {
        this.vehicleEntity = NetworkedContraptionLegData.VEHICLE_ANIMATION_TARGETS.get(buffer.readUUID());
        System.out.println("ASNsdfhgJFK");
        if (vehicleEntity != null) {
            System.out.println("asdhfsadfhfh");
            this.tagData = buffer.readNbt();
            
            this.prevYaw = buffer.readFloat();
            this.yaw = buffer.readFloat();
            this.prevPitch = buffer.readFloat();
            this.pitch = buffer.readFloat();
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
    }
    
    @Override
    public boolean handle(NetworkEvent.Context context) {
        if (vehicleEntity != null) {
            System.out.println("ASNfdJFK");
            vehicleEntity.legAnimationData.read(tagData);
            vehicleEntity.prevYaw = this.prevYaw;
            vehicleEntity.yaw = this.yaw;
            vehicleEntity.prevPitch = this.prevPitch;
            vehicleEntity.pitch = this.pitch;
        }
        return true;
    }
    
}
