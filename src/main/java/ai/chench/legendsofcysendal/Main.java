package ai.chench.legendsofcysendal;

import ai.chench.legendsofcysendal.listeners.UserInterfaceListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new UserInterfaceListener(this), this);
    }

    public void onDisable() {

    }
}
