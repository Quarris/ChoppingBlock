package dev.quarris.choppingblock.compat;

import net.minecraftforge.fml.ModList;

public class ModCompat {


    public static void load() {

    }

    public static boolean isLoaded(String modid) {
        return ModList.get().isLoaded(modid);
    }
}
