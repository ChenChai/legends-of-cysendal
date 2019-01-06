package ai.chench.legendsofcysendal;

import ai.chench.legendsofcysendal.commands.CommandSoulPoints;
import ai.chench.legendsofcysendal.listeners.UserInterfaceListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        // bind commands so that when they are called in Minecraft, these functions will handle them.
        CommandSoulPoints commandSoulPoints = new CommandSoulPoints(this);
        this.getCommand("addsp").setExecutor(commandSoulPoints);
        this.getCommand("getsp").setExecutor(commandSoulPoints);


        getServer().getPluginManager().registerEvents(new UserInterfaceListener(this), this);
    }

    public void onDisable() {

    }
}
