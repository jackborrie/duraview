package dev.inlitum.config;

import dev.inlitum.DuraView;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = DuraView.MODID)
public class DuraViewConfig implements ConfigData {

    public boolean enabled      = true;
    public boolean displayAbove = false;
    public boolean showAsPercentage = false;
    
}
