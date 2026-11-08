package dev.tweak.apothic_infinite_exponential_tweak;

import dev.tweak.apothic_infinite_exponential_tweak.config.ExponentialTweakConfig;
import dev.tweak.apothic_infinite_exponential_tweak.event.ItemTooltipEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ApothicInfiniteExponentialTweak.MODID)
public class ApothicInfiniteExponentialTweak {
    public static final String MODID = "apothic_infinite_exponential_tweak";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public ApothicInfiniteExponentialTweak(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        // Register configuration
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ExponentialTweakConfig.SPEC, "apothic_infinite_exponential_tweak-server.toml");

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onConfigLoad);
        modEventBus.addListener(this::onConfigReload);

        // Register forge event bus listeners
        MinecraftForge.EVENT_BUS.register(ItemTooltipEventHandler.class);
        MinecraftForge.EVENT_BUS.register(this);

        LOGGER.info("[Apothic Infinite Exponential Tweak] Initialized successfully.");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(this::syncApothicInfiniteConfig);
    }

    private void onConfigLoad(final ModConfigEvent.Loading event) {
        syncApothicInfiniteConfig();
    }

    private void onConfigReload(final ModConfigEvent.Reloading event) {
        syncApothicInfiniteConfig();
    }

    @SubscribeEvent
    public void onServerAboutToStart(ServerAboutToStartEvent event) {
        syncApothicInfiniteConfig();
    }

    /**
     * Inspects and synchronizes Apothic-Infinite's config spec to unlock maximum duplicate reforge limit.
     */
    public void syncApothicInfiniteConfig() {
        if (!ExponentialTweakConfig.OVERRIDE_MAX_AFFIX_LEVEL_TO_MAX.get()) {
            return;
        }

        try {
            Class<?> infiniteConfigClass = Class.forName("com.maxwell.apothic_infnite.InfiniteConfig");
            java.lang.reflect.Field maxAffixField = infiniteConfigClass.getDeclaredField("MAX_AFFIX_LEVEL");
            Object maxAffixObj = maxAffixField.get(null);
            if (maxAffixObj instanceof net.minecraftforge.common.ForgeConfigSpec.DoubleValue doubleVal) {
                double targetMax = ExponentialTweakConfig.CUSTOM_MAX_AFFIX_LEVEL.get();
                if (doubleVal.get() < targetMax) {
                    doubleVal.set(targetMax);
                    LOGGER.info("[Apothic Infinite Exponential Tweak] Set Apothic-Infinite MAX_AFFIX_LEVEL to: {}", targetMax);
                }
            }
        } catch (ClassNotFoundException ignored) {
            // Apothic-Infinite might not be present
        } catch (Throwable t) {
            LOGGER.warn("[Apothic Infinite Exponential Tweak] Could not synchronize Apothic-Infinite config: {}", t.getMessage());
        }
    }
}

