package com.cak.walkers.foundation.network.vehicle;

import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

public class ShowDEBUGPositionPacket extends SimplePacketBase {
    
    Vec3 pos;
    
    public ShowDEBUGPositionPacket(Vec3 pos) {
        this.pos = pos;
    }
    
    public ShowDEBUGPositionPacket(FriendlyByteBuf buffer) {
        this.pos = new Vec3(
            buffer.readDouble(),
            buffer.readDouble(),
            buffer.readDouble()
        );
    }
    
    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeDouble(pos.x);
        buffer.writeDouble(pos.y);
        buffer.writeDouble(pos.z);
    }
    
    @Override
    public boolean handle(NetworkEvent.Context context) {
        CreateClient.OUTLINER.showLine(pos, pos, pos)
            .colored(0xff00ff)
            .lineWidth(1.1f);
        return true;
    }
    
}
