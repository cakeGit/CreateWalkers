package com.cak.walkers;

import com.cak.walkers.content.registry.WalkerBlockPartials;
import com.cak.walkers.content.registry.WalkersRegistry;
import com.cak.walkers.foundation.network.WalkersPackets;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Walkers.MODID)
public class Walkers {
    
    public static final String MODID = "create_walkers";
    public static final Logger LOGGER = LogUtils.getLogger();
    
    public static CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);
    
    public Walkers() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        REGISTRATE.registerEventListeners(modEventBus);
        WalkersRegistry.register(modEventBus);
        WalkersPackets.registerPackets();
        WalkerBlockPartials.register();
    }
    
    public static ResourceLocation asResource(String string) {
        return new ResourceLocation(MODID, string);
    }
    
}
