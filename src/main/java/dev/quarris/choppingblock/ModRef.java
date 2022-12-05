package dev.quarris.choppingblock;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.items.IItemHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModRef {

    public static final String ID = "choppingblock";
    public static final Logger LOGGER = LogManager.getLogger(ID);

    public static ResourceLocation res(String name) {
        return new ResourceLocation(ID, name);
    }

    public static class Capabilities {
        @CapabilityInject(IItemHandler.class)
        public static Capability<IItemHandler> ITEMS;
    }
}
