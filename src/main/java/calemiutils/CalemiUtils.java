package calemiutils;

import calemiutils.config.CUConfig;
import calemiutils.event.OverlayEvent;
import calemiutils.event.WrenchEvent;
import calemiutils.init.InitBlocks;
import calemiutils.init.InitEnchantments;
import calemiutils.world.WorldGenOre;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CUReference.MOD_ID)
public class CalemiUtils {

    public static CalemiUtils instance;

    public static final ItemGroup TAB = new CUTab();

    public CalemiUtils() {

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CUConfig.spec);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        instance = this;

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {

        MinecraftForge.EVENT_BUS.register(new WrenchEvent());

        DeferredWorkQueue.runLater(WorldGenOre::onCommonSetup);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {

        MinecraftForge.EVENT_BUS.register(new OverlayEvent());
        RenderTypeLookup.setRenderLayer(InitBlocks.BLUEPRINT, RenderType.func_228643_e_());
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {

    }
}