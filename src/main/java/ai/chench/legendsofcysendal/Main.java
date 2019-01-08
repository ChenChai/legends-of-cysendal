package ai.chench.legendsofcysendal;

import ai.chench.legendsofcysendal.commands.CommandLoc;
import ai.chench.legendsofcysendal.commands.CommandSoulPoints;
import ai.chench.legendsofcysendal.listeners.KillListener;
import ai.chench.legendsofcysendal.listeners.UserInterfaceListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Main extends JavaPlugin {
    // custom yml config file to store player data
    private File playerDataYml = new File(this.getDataFolder() + "/playerDataYml.yml"); // this file is only accessed when savePlayerData() is called.
    public FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerDataYml); // this is how other classes can access this data. It functions similarly to the default config

    public void savePlayerData() {
        try {
            playerData.save(playerDataYml); // save custom yml
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        // load new default config file if one does not exist; this will not overwrite an old one
        saveDefaultConfig();

        // create new yml file if one does not exist
        if (!playerDataYml.exists()) {
            getLogger().info("No player data file detected, creating new one!");
            playerData.set("players", "hi");
            savePlayerData();
        } else {
            getLogger().info(playerData.getString("players"));
        }



        // bind commands so that when they are called in Minecraft, these functions will handle them.
        CommandSoulPoints commandSoulPoints = new CommandSoulPoints(this);
        getCommand("addsp").setExecutor(commandSoulPoints);
        getCommand("getsp").setExecutor(commandSoulPoints);
        getCommand("loc").setExecutor(new CommandLoc(this));

        // bind listeners to listen to events in the Minecraft
        getServer().getPluginManager().registerEvents(new UserInterfaceListener(this), this);
        getServer().getPluginManager().registerEvents(new KillListener(this), this);
    }

    public void onDisable() {

    }
}
