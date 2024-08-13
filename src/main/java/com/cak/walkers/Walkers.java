package com.cak.walkers;

import com.cak.walkers.foundation.vehicle.TestingClientVehicle;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(Walkers.MODID)
public class Walkers {
    
    public static final String MODID = "create_walkers";
    public static final Logger LOGGER = LogUtils.getLogger();
    
    public static TestingClientVehicle testingClientVehicle = new TestingClientVehicle();
    
    public Walkers() {
    
    }
    
    @Mod.EventBusSubscriber(value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void clientTick(TickEvent.ClientTickEvent event) {
            if (Minecraft.getInstance().level == null) return;
            testingClientVehicle.setCurrentLevel(Minecraft.getInstance().level);
            testingClientVehicle.tick();
            testingClientVehicle.renderDebug();
        }
        
        @SubscribeEvent
        public static void clientTick(ClientChatEvent event) {
            if (event.getMessage().equals(".reset")) {
                testingClientVehicle = new TestingClientVehicle();
                event.setCanceled(true);
            }
        }
    }
    
}
