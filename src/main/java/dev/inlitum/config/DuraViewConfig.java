package dev.inlitum.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.inlitum.DuraView;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;

@Config(name = DuraView.MODID)
public class DuraViewConfig implements ConfigData {

    public boolean enabled      = true;
    public boolean displayAbove = false;
    
}
