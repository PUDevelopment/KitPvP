package eu.playerunion.kitpvp.listeners;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import eu.playerunion.kitpvp.Main;

public class PlayerListeners implements Listener {
	
	private Main main = Main.getInstance();
	
	private int countdownTask = 0;
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		Location spawn = this.main.getUtils().getSpawnLocation();
		
		p.teleport(spawn);
		
		p.sendMessage("§7KitPvP v" + this.main.getDescription().getVersion());
	}
	
	@EventHandler
	public void onDamage(final EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			
			if(p.getHealth() - e.getFinalDamage() <= 0.0D) {
				e.setCancelled(true);
				
				p.getLocation().getWorld().spawnParticle(Particle.CLOUD, p.getLocation(), 15);
				
				p.spigot().respawn();
				
				p.setHealth(20.0D);
				p.setGameMode(GameMode.SPECTATOR);
				
				Runnable countdownTask = new Runnable() {
					int countdown = 10;
					
					public void run() {
						this.countdown--;
						
						if(this.countdown == 1)
							Bukkit.getScheduler().cancelTask(PlayerListeners.this.countdownTask);
						
						p.sendTitle("§c§lÚjraéledés: " + this.countdown, "");
					}
				};
				
				Runnable respawnTask = () -> {
					Location spawn = this.main.getUtils().getSpawnLocation();
					
					p.teleport(spawn);
					
					p.sendTitle("", ""); // title törlése, mert sokáig ott dekkol.
					
					p.setGameMode(Bukkit.getDefaultGameMode());
					p.setHealth(20.0D);
				};
				
				this.countdownTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this.main, countdownTask, 0, 20L).getTaskId();
				Bukkit.getScheduler().runTaskLater(this.main, respawnTask, 180L);
				
				Bukkit.broadcastMessage("§e§l➩ §c" + p.getName() + " meghalt.");
			}
		}
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		
		if(e.getMessage().equalsIgnoreCase("!tesztkard")) {
			ItemStack item = new ItemStack(Material.GOLDEN_HELMET);
			ItemMeta meta = item.getItemMeta();
			
			meta.setDisplayName("§fTeszt helmet");
			meta.setCustomModelData(491317);
			
			item.setItemMeta(meta);
			
			p.getInventory().addItem(item);
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		
		if(p.getWorld().getBlockAt(e.getTo()).getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
			int rnd = new Random().nextInt(this.main.getUtils().getAvailableSpawns().size());
			Location to = this.main.getUtils().getAvailableSpawns().get(rnd);
			
			p.teleport(to.add(0.0, 0.4, 0.0));
			p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
			
			p.sendMessage("§c§l➩ §cBeléptél a pvp területre! Innentől kezdve sebezhető vagy!");
			to.getWorld().playEffect(to, Effect.DRAGON_BREATH, 1);
			
			this.main.getUtils().inGamePlayers.add(p);
			
			/** for(Entity ent : to.getWorld().getNearbyEntities(to, 3, 5, 3)) {
				if(ent instanceof LivingEntity && ent != p) {
					ent.setVelocity(ent.getLocation().toVector().multiply(-1));
					
					((LivingEntity) ent).damage(1.0);
				}
			} **/
				
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		Entity ent = e.getEntity();
		
		if(ent instanceof Player) {
			if(e.getCause() == DamageCause.ENTITY_EXPLOSION)
				e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onTarget(EntityTargetEvent e) {
		Entity ent = e.getEntity();
		Entity target = e.getTarget();
		
		if(ent instanceof Wither) {
			Wither wither = (Wither) ent;
			Vector direction = target.getLocation().toVector().subtract(ent.getLocation().toVector());
			Fireball fireball = wither.launchProjectile(Fireball.class);
			
			fireball.setDirection(direction);
			fireball.setYield(1.2F);
		}
	}

}
