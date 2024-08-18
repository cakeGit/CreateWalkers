package com.cak.walkers;

import com.cak.walkers.content.registry.WalkersRegistry;
import com.cak.walkers.foundation.vehicle.implementation.TestingClientVehicle;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Walkers.MODID)
public class Walkers {
    
    public static final String MODID = "create_walkers";
    public static final Logger LOGGER = LogUtils.getLogger();
    
    public static TestingClientVehicle testingClientVehicle = new TestingClientVehicle();
    
    public static CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);
    
    public Walkers() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        REGISTRATE.registerEventListeners(modEventBus);
        WalkersRegistry.register(modEventBus);
    }
    
    @Mod.EventBusSubscriber(value = Dist.CLIENT)
    public static class ClientEvents {
        
        @SubscribeEvent
        public static void clientTick(TickEvent.ClientTickEvent event) {
//            if (Minecraft.getInstance().level == null) return;
//            testingClientVehicle.setCurrentLevel(Minecraft.getInstance().level);
//            testingClientVehicle.tick();
        }
        
        @SubscribeEvent
        public static void renderLevelStageEvent(RenderLevelStageEvent event) {
            if (Minecraft.getInstance().level == null || event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) return;
            if (testingClientVehicle.isInitialised())
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
