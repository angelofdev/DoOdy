/*
 *  DoOdy v1: Separates Admin/Mod duties so everyone can enjoy the game.
 *  Copyright (C) 2013  M.Y.Azad
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *   
 */

package com.angelofdev.DoOdy.listeners;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.InventoryHolder;

import com.angelofdev.DoOdy.command.DoOdyCommandExecutor;
import com.angelofdev.DoOdy.config.Configuration;
import com.angelofdev.DoOdy.util.Debug;
import com.angelofdev.DoOdy.util.HashMaps;
import com.angelofdev.DoOdy.util.MessageSender;

public class PlayerListener implements Listener {
	private MessageSender m = new MessageSender();
	
	public PlayerListener() {
	}
	List<String> deniedCommands = Configuration.config.getStringList("Denied.commands");
	List<String> configDropList = Configuration.config.getStringList("Duty Deny Drops.whitelist");
	List<String> configStorageDenied = Configuration.config.getStringList("Deny Storage.storage");
	List<String> configDeniedBlocks = Configuration.config.getStringList("Denied Blocks.Place");
	List<String> configDeniedItems = Configuration.config.getStringList("Denied Items.Place");

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getName();
		String message = event.getMessage().toLowerCase();
		if (DoOdyCommandExecutor.dutyList.contains(playerName)) {
			if (deniedCommands.contains(message)) {
				event.setCancelled(true);
				m.player(player, "&6[DoOdy] &cYou're not allowed to use this command on duty!");
				Debug.check("<onPlayerCommandPreprocess> " + playerName + " tried executing command in Denied Commands");
			}
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE) {
			String playerName = player.getName();
			if (player.isOp() || Configuration.data.contains(playerName) || player.hasPermission("doody.failsafe.bypass")){
				Debug.checkBroadcast("&e" + playerName + "&a<isOP&e|or|&awas on Duty&e|or|&ahas doody.failsafe.bypass>");
			} else {
				player.setGameMode(GameMode.SURVIVAL);
				player.getInventory().clear();
				if (DoOdyCommandExecutor.dutyList.contains(playerName)) {
					DoOdyCommandExecutor.dutyList.removeAll(Arrays.asList(playerName));
				}
				Debug.checkBroadcast("&e" + playerName + "&c<was illegally on Creative>");
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getName();
		if (Configuration.data.contains(playerName)) {
			try {
				DoOdyCommandExecutor.removeDoody(player);
			} catch (Exception e) {
				DoOdyCommandExecutor.dutyList.removeAll(Arrays.asList(playerName));
				player.setGameMode(GameMode.SURVIVAL);
				player.getInventory().clear();
				Debug.check("<PlayerListener|onPlayerQuit|Exception>");
			}
		}
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getName();
		if (Configuration.data.contains(playerName)) {
			try {
				DoOdyCommandExecutor.removeDoody(player);
			} catch (Exception e) {
				DoOdyCommandExecutor.dutyList.removeAll(Arrays.asList(playerName));
				player.setGameMode(GameMode.SURVIVAL);
				player.getInventory().clear();
				Debug.check("<PlayerListener|onPlayerKick|Exception>");
			}
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getName();
		if (Configuration.data.contains(playerName)) {
			String worldName = player.getWorld().getName();
			if (!player.hasPermission("doody.worlds." + worldName)) {
				try {
					DoOdyCommandExecutor.removeDoody(player);
				} catch (Exception e) {
					DoOdyCommandExecutor.dutyList.removeAll(Arrays.asList(playerName));
					player.setGameMode(GameMode.SURVIVAL);
					player.getInventory().clear();
				}
				Debug.check("<onPlayerWorldChange> " + playerName + " Does not have the permission 'doody.worlds." + worldName + "'");
			} else {
				if (player.isOp()) {
					Debug.normal("<onPlayerWorldChange> " + playerName + " is OP.");
				} else {
					Debug.check("<onPlayerWorldChange> " + playerName + " Player has the permission 'doody.worlds." + worldName + "'");
				}
			}
		}
	}
		
	@EventHandler(ignoreCancelled=true)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		String playerName = player.getName();
		
		if(Configuration.data.contains(playerName)) {
			event.getDrops().clear();
			event.setDroppedExp(0);
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getName();
		
		if(Configuration.data.contains(playerName) && HashMaps.armour.containsKey(playerName)) {
			player.getInventory().setArmorContents(HashMaps.armour.get(playerName));
			if (HashMaps.dutyLoc.containsKey(playerName)) {
				player.teleport(HashMaps.dutyLoc.get(playerName));
			}
			DoOdyCommandExecutor.dutyItems(player);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getName();
		Item item = event.getItemDrop();
		
		if (Configuration.data.contains(playerName) && Configuration.config.getBoolean("Duty Deny Drops.enabled")) {
			if (!(player.isOp() || player.hasPermission("doody.dropitems"))) {
				String itemName = item.getItemStack().getType().toString();
				if (!(configDropList.contains(itemName))) {
					String message = item.getItemStack().getType().name();
					String itemname = message.toLowerCase();
			
					event.getItemDrop().remove();
			
					if (Configuration.config.getBoolean("Duty Deny Drops.messages")) {
						player.sendMessage(ChatColor.GOLD + "[DoOdy] " + ChatColor.RED + "There's no need to drop " + ChatColor.YELLOW + itemname + ChatColor.RED + " while on Duty.");
					}
					Debug.check("<onPlayerDropItem> " + playerName + " got denied item drop. <Item not in whitelist(" + itemname + ")>");
				}
			} else {
				if (Configuration.config.getBoolean("Debug.enabled")) {
					String itemName = item.getItemStack().getType().toString().toLowerCase();
					if (configDropList.contains(itemName)) {
						Debug.normal("<onPlayerDropItem> Warning! " + itemName + " is whitelisted in config.");
						Debug.normal("<onPlayerDropItem> Warning! " + "Allowing " + playerName + " to drop " + itemName);
					} else {
						if (player.isOp()) {
							Debug.normal("<onPlayerDropItem> Warning! " + playerName + " is OP -Allowing item drop, " + itemName);
						} else if (player.hasPermission("doody.dropitems")) {
							Debug.normal("<onPlayerDropItem> Warning! " + playerName + " has doody.dropitems -Allowing item drop, " + itemName);
						} else {
							//It should not have reached here
							Debug.severe("<onPlayerDropItem> Another plugin may be causing a conflict. DoOdy Debug cannot make sense.");
						}
					}
				}
				return;
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player player = event.getPlayer();
			String playerName = player.getName();
			
			if (Configuration.data.contains(playerName) && Configuration.config.getBoolean("Deny Storage.enabled")) {
				Block block = event.getClickedBlock();
				Material blockMat = block.getType();
				Material itemInMainHand = player.getInventory().getItemInMainHand().getType();
				
				Debug.check("<onPlayerInteract>" + playerName + " Right Clicked on " + blockMat.name().toLowerCase());
				Debug.check("<onPlayerInteract>" + playerName + " Holding " + itemInMainHand.name().toLowerCase() + " in main hand.");
				if (configDeniedItems.contains(itemInMainHand.name().toLowerCase())) {
					if (!(player.isOp() || player.hasPermission("doody.allowplace.item"))) {
						event.setCancelled(true);
						if (Configuration.config.getBoolean("Denied Items.messages")) {
							player.sendMessage(ChatColor.RED + "There's no need to place " + ChatColor.YELLOW + itemInMainHand.name().toLowerCase() + ChatColor.RED + " things while on duty.");
						}
						return;
					} else {
						if (Configuration.config.getBoolean("Debug.enabled")) {
							if (player.isOp()) {
								Debug.normal("<onPlayerInteract> Warning! " + playerName + " is OP -Allowing place item");
							} else if (player.hasPermission("doody.allowplace.item")) {
								Debug.normal("<onPlayerInteract> Warning! " + playerName + " has doody.allowplace.item -Allowing place item");
							} else if (!(configDeniedItems.contains(itemInMainHand.name().toLowerCase()))) {
								Debug.normal("<onPlayerInteract> Warning! " + itemInMainHand.name() + " is not in 'Denied Items.Place' list -Allowing place item");
							} else {
								//It should not have reached here
								Debug.severe("<onPlayerInteract> Another plugin may be causing a conflict. DoOdy Debug cannot make sense of it.");
							}
						}
					}
				}
				if (configStorageDenied.contains(blockMat.name().toLowerCase())) {
					if (!(player.isOp() || player.hasPermission("doody.storage"))) {
						event.setCancelled(true);
						if (Configuration.config.getBoolean("Deny Storage.messages")) {
							player.sendMessage(ChatColor.RED + "There's no need to store things while on duty.");
						}
						Debug.check("<onPlayerInteract> " + playerName + " got denied storage interact. <Block :" + blockMat.name().toLowerCase() + " is in Deny Storage list>");
						return;
					} else {
						if (Configuration.config.getBoolean("Debug.enabled")) {
							if (player.isOp()) {
								Debug.normal("<onPlayerInteract> Warning! " + playerName + " is OP -Allowing storage interact");
							} else if (player.hasPermission("doody.storage")) {
								Debug.normal("<onPlayerInteract> Warning! " + playerName + " has doody.storage -Allowing storage interact");
							} else if (!(configStorageDenied.contains(blockMat.name().toLowerCase()))) {
								Debug.normal("<onPlayerInteract> Warning! " + blockMat.name().toLowerCase() + " is not in 'Deny Storage.storage' list -Allowing storage interact");
							} else {
								//It should not have reached here
								Debug.severe("<onPlayerInteract> Another plugin may be causing a conflict. DoOdy Debug cannot make sense of it.");
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		if (!(event.getInventory().getHolder() == null)) {
			HumanEntity player = event.getPlayer();
			String playerName = player.getName();
			if (Configuration.data.contains(playerName) && Configuration.config.getBoolean("Deny Storage.enabled")) {
				InventoryHolder holder = event.getInventory().getHolder();
				Debug.check("<onInventoryOpen> holder = " + holder.toString());
				if ((configStorageDenied.contains("craftmule") && holder.toString().toLowerCase().contains("mule")) 
						|| (configStorageDenied.contains("crafthorse") && holder.toString().toLowerCase().contains("horse"))
						|| (configStorageDenied.contains("craftminecarthopper") && holder.toString().toLowerCase().contains("hopper"))
						|| (configStorageDenied.contains("craftblastfurnace") && holder.toString().toLowerCase().contains("blast"))
						|| (configStorageDenied.contains("craftbarrel") && holder.toString().toLowerCase().contains("barrel"))
						|| (configStorageDenied.contains("craftsmoker") && holder.toString().toLowerCase().contains("smoker"))) {
					if (!(player.isOp() || player.hasPermission("doody.storage"))) {
						event.setCancelled(true);
						if (Configuration.config.getBoolean("Deny Storage.messages")) {
							player.sendMessage(ChatColor.RED + "There's no need to store things while on duty.");
						}
						Debug.check("<onInventoryOpen> Cancelled event player does not have " + ChatColor.GOLD + "doody.storage " +ChatColor.WHITE + "permissions.");
						return;
					} else {
						if (Configuration.config.getBoolean("Debug.enabled")) {
							if (player.isOp()) {
								Debug.normal("<onPlayerInteract> Warning! " + playerName + " is OP -Allowing storage interact");
							} else if (player.hasPermission("doody.storage")) {
								Debug.normal("<onPlayerInteract> Warning! " + playerName + " has doody.storage -Allowing storage interact");
							} else if (!(configStorageDenied.contains(holder.toString()))) {
								Debug.normal("<onPlayerInteract> Warning! " + holder.toString() + " is not in 'Deny Storage.storage' list -Allowing storage interact");
							}
						}
					}
				}
			}
		}
	}
	@EventHandler
	public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
		if (event.getRightClicked() instanceof ArmorStand) {
			Player player = event.getPlayer();
			String playerName = player.getName();
			Debug.check("<onPlayerIneteractAtEntity | L326> " + playerName + " interacted with instance of " + event.getRightClicked().toString());
			if (Configuration.data.contains(playerName) && (Configuration.config.getBoolean("Deny Storage.enabled"))) {
				Debug.check("<onPlayerIneteractAtEntity | L326> player is on duty and deny storage is enabled");
				if(!(player.isOp() || player.hasPermission("doody.storage"))) {
					event.setCancelled(true);
					if (Configuration.config.getBoolean("Deny Storage.messages")) {
						player.sendMessage(ChatColor.RED + "There's no need to store things while on Duty.");
					}
				} else {
					if (Configuration.config.getBoolean("Debug.enabled")) {
						if (player.isOp()) {
							Debug.normal("<onPlayerIneteractAtEntity> Warning! " + playerName + " is OP - Allowing storage interact");
						} else if (player.hasPermission("doody.storage")) {
							Debug.normal("<onPlayerIneteractAtEntity> Warning! " + playerName + " has doody.storage - Allowing storage interact");
						} else {
							//It should not have reached here
							Debug.severe("<onPlayerIneteractAtEntity> Another plugin may be causing a conflict. DoOdy Debug cannot make sense.");
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityInteract(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof StorageMinecart 
				|| event.getRightClicked() instanceof HopperMinecart 
				|| event.getRightClicked() instanceof ItemFrame) {
			Player player = event.getPlayer();
			String playerName = player.getName();
			
			if (Configuration.data.contains(playerName) && (Configuration.config.getBoolean("Deny Storage.enabled"))) {
				if (!(player.isOp() || player.hasPermission("doody.storage"))) {
					event.setCancelled(true);
					if (Configuration.config.getBoolean("Deny Storage.messages")) {
						player.sendMessage(ChatColor.RED + "There's no need to store things while on Duty.");
					}
					Debug.check("<onEntityInteract> Success! " + playerName + " got denied storage interact.");
				} else {
					if (Configuration.config.getBoolean("Debug.enabled")) {
						if (player.isOp()) {
							Debug.normal("<onEntityInteract> Warning! " + playerName + " is OP - Allowing storage interact");
						} else if (player.hasPermission("doody.storage")) {
							Debug.normal("<onEntityInteract> Warning! " + playerName + " has doody.storage - Allowing storage interact");
						} else {
							//It should not have reached here
							Debug.severe("<onEntityInteract> Another plugin may be causing a conflict. DoOdy Debug cannot make sense.");
						}
					}
				}
			}
		}
	}
		
	/** SLAPI = Saving/Loading API
	 * API for Saving and Loading Objects.
	 * @author Tomsik68
	 */
	public static class SLAPI {
		public static void save(Object obj,String path) throws Exception {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
			oos.writeObject(obj);
			oos.flush();
			oos.close();
		}
		public static Object load(String path) throws Exception	{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
			Object result = ois.readObject();
			ois.close();
			return result;
		}
	}
}
