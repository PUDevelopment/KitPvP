package eu.playerunion.kitpvp;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import eu.playerunion.kitpvp.commands.BaseCommand;
import eu.playerunion.kitpvp.hooks.PlaceholderAPIHook;
import eu.playerunion.kitpvp.listeners.PlayerListeners;

public class Main extends JavaPlugin {
	
	private static Main instance;
	
	private Utils utils;
	
	public static Main getInstance() {
		return instance;
	}
	
	@Override
	public void onEnable() {
		instance = this;
		this.utils = new Utils();
		
		this.getLogger().info("A plugin indítása folyamatban...");
		
		this.saveDefaultConfig();
		this.utils.loadAvailableSpawns();
		
		this.getCommand("kitpvp").setExecutor(new BaseCommand());
		this.getCommand("spawn").setExecutor(new BaseCommand());
		
		Bukkit.getPluginManager().registerEvents(new PlayerListeners(), this);
		
		new PlaceholderAPIHook().register();
		
		this.getLogger().info("A plugin sikeresen elindult!");
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public Utils getUtils() {
		return this.utils;
	}

}
