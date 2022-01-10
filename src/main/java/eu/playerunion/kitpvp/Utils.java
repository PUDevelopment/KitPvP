package eu.playerunion.kitpvp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class Utils {
	
	private Main main = Main.getInstance();
	
	private ArrayList<Location> availableSpawns = new ArrayList<Location>();
	public ArrayList<Player> inGamePlayers = new ArrayList<Player>();
	
	public HashMap<Player, Integer> killStreaks = new HashMap<Player, Integer>();
	public HashMap<Player, Integer> totems = new HashMap<Player, Integer>();
	
	public Location getSpawnLocation() {
		ConfigurationSection spawnSec = this.main.getConfig().getConfigurationSection("beallitasok.spawn");
		Location spawn = new Location(Bukkit.getWorld(spawnSec.getString("vilag")), spawnSec.getDouble("x"), spawnSec.getDouble("y"), spawnSec.getDouble("z"));
		
		spawn.setPitch((float) spawnSec.getDouble("pitch"));
		spawn.setYaw((float) spawnSec.getDouble("yaw"));
		
		return spawn;
	}
	
	public ArrayList<Location> getAvailableSpawns() {
		return this.availableSpawns;
	}
	
	public void loadAvailableSpawns() {
		this.availableSpawns.clear();
		
		ConfigurationSection spawns = this.main.getConfig().getConfigurationSection("beallitasok.spawnok");
		Iterator<String> spawnsIter = spawns.getKeys(false).iterator();
		
		while(spawnsIter.hasNext()) {
			String key = spawnsIter.next();
			ConfigurationSection spawn = spawns.getConfigurationSection(key);
			World world = Bukkit.getWorld(spawns.getString("vilag") == null ? "world" : spawns.getString("vilag"));
			double x = spawn.getDouble("x");
			double y = spawn.getDouble("y");
			double z = spawn.getDouble("z");
			float pitch = (float) spawn.getDouble("pitch");
			float yaw = (float) spawn.getDouble("yaw");
			Location loc = new Location(world, x, y, z);
			
			loc.setPitch(pitch);
			loc.setYaw(yaw);
			
			this.availableSpawns.add(loc);
		}
	}
	
	public void addSpawnLocation(Location loc) {
		this.availableSpawns.add(loc);
		
		ConfigurationSection spawns = this.main.getConfig().getConfigurationSection("beallitasok.spawnok");
		int index = 1;
		
		for(Location location : this.availableSpawns) {
			spawns.set(String.valueOf(index) + ".vilag", location.getWorld().getName());
			
			ConfigurationSection spawn = spawns.getConfigurationSection(String.valueOf(index));
			
			spawn.set("x", location.getX());
			spawn.set("y", location.getY());
			spawn.set("z", location.getZ());
			spawn.set("pitch", location.getPitch());
			spawn.set("yaw", location.getYaw());
			
			index++;
		}
		
		this.main.saveConfig();
	}

}
