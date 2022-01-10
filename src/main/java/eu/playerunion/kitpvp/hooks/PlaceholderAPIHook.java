package eu.playerunion.kitpvp.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import eu.playerunion.kitpvp.Main;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceholderAPIHook extends PlaceholderExpansion {
	
	private Main main = Main.getInstance();

	@Override
	public String getAuthor() {
		return this.main.getDescription().getAuthors().toString();
	}

	@Override
	public String getIdentifier() {
		return this.main.getName().toLowerCase();
	}

	@Override
	public String getVersion() {
		return this.main.getDescription().getVersion();
	}
	
	@Override
	public String onPlaceholderRequest(Player p, String id) {
		if(id.equalsIgnoreCase("ingame"))
			return "" + this.main.getUtils().inGamePlayers.size() + "/" + Bukkit.getOnlinePlayers().size();
		
		if(id.equalsIgnoreCase("killstreak"))
			return !this.main.getUtils().killStreaks.containsKey(p) ? "0" : "" + this.main.getUtils().killStreaks.get(p);
		
		if(id.equalsIgnoreCase("totem"))
			return !this.main.getUtils().totems.containsKey(p) ? "0" : "" + this.main.getUtils().totems.get(p);
		
		return "";
	}

}
