package dev.inlitum;

import dev.inlitum.config.DuraViewConfig;
import dev.inlitum.config.ModMenuIntegration;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DuraView implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("duraview");

	public static DuraViewConfig config;

	public static final String MODID = "duraview";

	@Override
	public void onInitialize() {
		AutoConfig.register(DuraViewConfig.class, GsonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(DuraViewConfig.class).getConfig();
	}


}