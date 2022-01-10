package eu.playerunion.kitpvp.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import eu.playerunion.kitpvp.Main;

public class BaseCommand implements CommandExecutor, TabExecutor {

	private Main main = Main.getInstance();
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("kitpvp")) {
			if(!sender.hasPermission("playerunion.kitpvp")) {
				sender.sendMessage("§eKitPvP v" + this.main.getDescription().getVersion());
				sender.sendMessage("§6Fejleszti: " + this.main.getDescription().getAuthors().toString());
				
				return true;
			}
			
			if(args.length == 0) {
				sender.sendMessage("§eKitPvP v" + this.main.getDescription().getVersion());
				sender.sendMessage("§6Fejleszti: " + this.main.getDescription().getAuthors().toString());
				sender.sendMessage("");
				sender.sendMessage("§e/kitpvp setSpawn - Fő spawn beállítása.");
				sender.sendMessage("§e/kitpvp addSpawn - Spawnhely hozzáadása.");
				sender.sendMessage("§e/kitpvp autoRestart - Szerver újraindítása 5 perc múlva.");
				sender.sendMessage("§e/kitpvp setKillReward <összeg> - Pénzösszeg beállítása.");
				sender.sendMessage("§e/kitpvp setDeathPrice <összeg> - Pénzösszeg beállítása.");
				
				return true;
			}
			
			if(args.length == 1) {
				if(args[0].equalsIgnoreCase("setspawn")) {
					if(!(sender instanceof Player)) {
						sender.sendMessage("§cEzt a parancsot csak játékos használhatja!");
						
						return true;
					}
					
					Player p = (Player) sender;
					Location current = p.getLocation();
					
					ConfigurationSection spawnSec = this.main.getConfig().getConfigurationSection("beallitasok.spawn");
					
					spawnSec.set("vilag", current.getWorld().getName());
					spawnSec.set("x", current.getX());
					spawnSec.set("y", current.getY());
					spawnSec.set("z", current.getZ());
					spawnSec.set("pitch", (double) p.getLocation().getPitch());
					spawnSec.set("yaw", (double) p.getLocation().getYaw());
					
					this.main.saveConfig();
					
					sender.sendMessage("§eSpawn sikeresen beállítva!");
					
					return true;
				}
				
				if(args[0].equalsIgnoreCase("addSpawn")) {
					if(!(sender instanceof Player)) {
						sender.sendMessage("§cEzt a parancsot csak játékos használhatja!");
						
						return true;
					}
					
					Player p = (Player) sender;
					Location current = p.getLocation();
					
					this.main.getUtils().addSpawnLocation(current);
					
					sender.sendMessage("§eSpawn sikeresen hozzáadva!");
					
					return true;
				}
				
				if(args[0].equalsIgnoreCase("autoRestart")) {
					sender.sendMessage("§eVisszaszámlálás elindítva!");
					
					Bukkit.getScheduler().scheduleSyncRepeatingTask(this.main, new Runnable() {
						int seconds = 300;
						
						public void run() {
							if((this.seconds % 60) == 0 && (this.seconds != 0))
								Bukkit.broadcastMessage("§c§l➩ §fA szerver §c" + (this.seconds / 60) + " §fperc múlva újraindul!");
							
							if(this.seconds == 30)
								Bukkit.broadcastMessage("§c§l➩ §fA szerver §c" + this.seconds + " §fmásodperc múlva újraindul!");
							
							if(this.seconds == 10)
								Bukkit.broadcastMessage("§c§l➩ §fA szerver §c" + this.seconds + " §fmásodperc múlva újraindul!");
							
							if(this.seconds < 5 && this.seconds != 0)
								Bukkit.broadcastMessage("§c§l➩ §fA szerver §c" + this.seconds + " §fmásodperc múlva újraindul!");
							
							if(this.seconds == 2)
								Bukkit.getOnlinePlayers().forEach(player -> player.teleport(BaseCommand.this.main.getUtils().getSpawnLocation()));
							
							if(this.seconds == 0)
								Bukkit.spigot().restart();
							
							this.seconds--;
						}
					}, 0L, 20L);
					
					return true;
				}
			}
		}
		
		if(command.getName().equalsIgnoreCase("spawn")) {
			if(args.length == 0) {
				if(!(sender instanceof Player)) {
					sender.sendMessage("§cEzt a parancsot csak játékos használhatja!");
					
					return true;
				}
				
				Player p = (Player) sender;
				
				p.sendMessage("§fTeleportálás §e3 §fmásodperc múlva...");
				
				Runnable teleportTask = () -> {
					p.teleport(this.main.getUtils().getSpawnLocation());
				};
				
				Bukkit.getScheduler().runTaskLater(this.main, teleportTask, 60L);
				
				return true;
			}
			
			if(args.length == 1) {
				if(!sender.hasPermission("playerunion.forcespawn")) {
					sender.sendMessage("§cNincs jogosultságod a parancs használatára!");
					
					return true;
				}
				
				String player = args[0];
				
				if(Bukkit.getPlayer(player) == null) {
					sender.sendMessage("§cNincs ilyen elérhető játékos!");
					
					return true;
				}
				
				Player p = Bukkit.getPlayer(player);
				
				p.teleport(this.main.getUtils().getSpawnLocation());
				p.sendMessage("§fA spawnra lettél kényszerítve §e" + sender.getName() + " §fáltal!");
				
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		ArrayList<String> arguments = new ArrayList<String>();
		
		if(command.getName().equalsIgnoreCase("kitpvp")) {
			if(sender.hasPermission("playerunion.kitpvp")) {
				if(args.length == 1) {
					arguments.add("setSpawn");
					arguments.add("addSpawn");
					arguments.add("autoRestart");
					arguments.add("setKillReward");
					arguments.add("setDeathPrice");
				}
			}
		}
		
		if(command.getName().equalsIgnoreCase("spawn")) {
			if(args.length == 1) {
				if(sender.hasPermission("playerunion.forcespawn")) {
					Bukkit.getOnlinePlayers().forEach(p -> arguments.add(p.getName()));
				}
			}
		}
		
		return arguments;
	}

}
