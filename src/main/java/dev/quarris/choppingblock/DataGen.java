package dev.quarris.choppingblock;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;


@Mod.EventBusSubscriber(modid = ModRef.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGen {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        if (event.includeClient()) {
            event.getGenerator().addProvider(new ModBlockStateProvider(gen, ModRef.ID, existingFileHelper));
            event.getGenerator().addProvider(new ModModelProvider(gen, ModRef.ID, existingFileHelper));
            event.getGenerator().addProvider(new ModEnUsLangProvider(gen, ModRef.ID));
        }
    }

    public static class ModBlockStateProvider extends BlockStateProvider {

        public ModBlockStateProvider(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
            super(gen, modid, exFileHelper);
        }

        @Override
        protected void registerStatesAndModels() {
            this.simpleBlock(ModRegistry.CHOPPING_BLOCK.get(), this.models().withExistingParent("chopping_block", this.modLoc("chopping_block_raw")).texture("side", this.modLoc("block/chopping_block_side")).texture("end", this.mcLoc("block/oak_log_top")));
        }
    }

    public static class ModModelProvider extends ItemModelProvider {

        public ModModelProvider(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
            super(gen, modid, exFileHelper);
        }

        @Override
        protected void registerModels() {
            this.withExistingParent("chopping_block", this.modLoc("block/chopping_block"));
        }
    }

    public static class ModEnUsLangProvider extends LanguageProvider {

        public ModEnUsLangProvider(DataGenerator gen, String modid) {
            super(gen, modid, "en_us");
        }

        @Override
        protected void addTranslations() {
            this.add(ModRegistry.CHOPPING_BLOCK.get(), "Chopping Block");
            //this.add(ModRegistry.CHOPPING_BLOCK_ITEM.get(), "Chopping Block");
        }
    }
}
