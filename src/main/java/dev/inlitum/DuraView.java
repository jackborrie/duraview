package dev.inlitum;

import dev.inlitum.config.DuraViewConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

public class DuraView implements ModInitializer {

    public static DuraViewConfig config;

    public static final String MODID = "duraview";

    @Override
    public void onInitialize() {
        AutoConfig.register(DuraViewConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(DuraViewConfig.class).getConfig();
    }
}