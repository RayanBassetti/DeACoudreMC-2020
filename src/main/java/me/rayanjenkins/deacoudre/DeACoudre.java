package me.rayanjenkins.deacoudre;

import me.rayanjenkins.deacoudre.events.Game;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class DeACoudre extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Game g = new Game();
        getCommand("deacoudre").setExecutor(g);
        getServer().getPluginManager().registerEvents(g, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
