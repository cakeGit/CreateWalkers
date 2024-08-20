package com.cak.walkers.content.registry;

import com.cak.walkers.content.components.controller.VehicleControllerBlock;
import com.cak.walkers.content.components.controller.VehicleControllerMovingInteractionBehavior;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.AllInteractionBehaviours;
import com.simibubi.create.AllMovementBehaviours;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.behaviour.MovingInteractionBehaviour;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.utility.Components;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.stream.Collectors;

import static com.cak.walkers.Walkers.MODID;
import static com.cak.walkers.Walkers.REGISTRATE;

/**
 * Todo delegate subtypes i just really cant be asked rn
 */
public class WalkersRegistry {

    protected static final DeferredRegister<CreativeModeTab> TAB_REGISTER = DeferredRegister
        .create(Registries.CREATIVE_MODE_TAB, Create.ID);
    
    public static final RegistryObject<CreativeModeTab> TAB = TAB_REGISTER
        .register("tab", () -> CreativeModeTab.builder()
            .title(Components.translatable("itemGroup." + MODID + ".tab"))
            .withTabsBefore(AllCreativeModeTabs.BASE_CREATIVE_TAB.getKey())
            .icon(() -> WalkersBlocks.VEHICLE_CONTROLLER.asStack())
            .displayItems((displayParameters, output) -> {
                output.acceptAll(
                    REGISTRATE.getAll(Registries.ITEM)
                        .stream()
                        .map(item -> item.get().getDefaultInstance())
                        .collect(Collectors.toSet()));
            })
            .build());
    
    /**@param bus bus 😩*/
    public static void register(IEventBus bus) {
        TAB_REGISTER.register(bus);
        REGISTRATE.setCreativeTab(TAB);
        
        WalkersBlocks.register();
        WalkersBlockEntityTypes.register();
        WalkersEntityTypes.register();
        WalkersContraptionTypes.register();
    }
    
}
