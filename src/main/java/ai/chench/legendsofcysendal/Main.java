package ai.chench.legendsofcysendal;

import ai.chench.legendsofcysendal.commands.CommandLoc;
import ai.chench.legendsofcysendal.commands.CommandSoulPoints;
import ai.chench.legendsofcysendal.listeners.KillListener;
import ai.chench.legendsofcysendal.listeners.UserInterfaceListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // load new default config file if one does not exist; this will not overwrite an old one
        saveDefaultConfig();

        // bind commands so that when they are called in Minecraft, these functions will handle them.
        CommandSoulPoints commandSoulPoints = new CommandSoulPoints(this);
        getCommand("addsp").setExecutor(commandSoulPoints);
        getCommand("getsp").setExecutor(commandSoulPoints);
        getCommand("loc").setExecutor(new CommandLoc(this));
        getCommand("party").setExecutor(new CommandParty(this));

        // bind listeners to listen to events in the Minecraft
        getServer().getPluginManager().registerEvents(new UserInterfaceListener(this), this);
        getServer().getPluginManager().registerEvents(new KillListener(this), this);
    }

    public void onDisable() {

    }
}
