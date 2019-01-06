package ai.chench.legendsofcysendal;

import ai.chench.legendsofcysendal.commands.CommandLoc;
import ai.chench.legendsofcysendal.listeners.UserInterfaceListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.getCommand("addsp").setExecutor(new CommandLoc(this));
        getServer().getPluginManager().registerEvents(new UserInterfaceListener(this), this);
    }

    public void onDisable() {

    }
}
