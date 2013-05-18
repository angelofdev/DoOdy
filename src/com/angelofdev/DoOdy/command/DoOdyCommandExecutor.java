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

package com.angelofdev.DoOdy.command;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.angelofdev.DoOdy.DoOdy;
import com.angelofdev.DoOdy.Log;
import com.angelofdev.DoOdy.config.Configuration;
import com.angelofdev.DoOdy.util.Debug;
import com.angelofdev.DoOdy.util.HashMaps;
import com.angelofdev.DoOdy.util.MessageSender;

public class DoOdyCommandExecutor implements CommandExecutor {
	private static MessageSender m = new MessageSender();

	@SuppressWarnings("unused")
	private DoOdy plugin;
	
	public DoOdyCommandExecutor(DoOdy plugin) {
		this.plugin = plugin;
	}
		
	public static ArrayList<String> dutyList = new ArrayList<String>();

	private static void addPlayer(String playerName) {
		dutyList.add(playerName);
	}
	public static List<String> configDeniedWorlds = Configuration.config.getStringList("Denied.worlds");

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd == Configuration.config.getStringList("")) {
			return false;
		}
		if (args.length == 0) {
			if (cmd.getName().equalsIgnoreCase("doody")) {
				if ((sender instanceof Player)) {
					Player player = (Player) sender;
					getCmdsPlayer(player);
					return true;
				} else {
					getCmdsConsole();
				}
			}
		} else if (args.length == 1) {
			// /dm on
			if (args[0].equalsIgnoreCase("on")) {
				if ((sender instanceof Player)) {
					Player player = (Player) sender;
					String playerName = player.getName();
					if (player.isOp() || player.hasPermission("doody.duty")) {
						String worldName = player.getWorld().getName();
						if ((configDeniedWorlds.contains(worldName) && player.hasPermission("doody.worlds." + worldName)) || (!configDeniedWorlds.contains(worldName))) {
							if (!Configuration.data.contains(playerName)) {
								if (player.getGameMode() == GameMode.CREATIVE) {
									m.player(player, "&6[DoOdy] &cYou must be in Survival mode first!");
								} else {
									Debug.check(playerName + " used /doody on");
									setDoody(player);
								return true;
								}
							} else if (Configuration.data.contains(playerName)) {
								m.player(player, "&6[DoOdy] &cYou're already on Duty!");
							}
						} else {
							m.player(player, "&6[DoOdy] &cCannot go on duty in the world, &e" + worldName + " &c!");
						}
					}
				}
			}

			// /dm off
			if (args[0].equalsIgnoreCase("off")) {
				if ((sender instanceof Player)) {
					Player player = (Player) sender;
					String playerName = player.getName();
					if (Configuration.data.contains(playerName)) {
						Debug.check(playerName + " used /doody off");
						removeDoody(player);
						return true;
					} else if (!Configuration.data.contains(playerName)) {
						m.player(player, "&6[DoOdy] &cYou're not on Duty!");
					}
				}
			}

			// /dm list
			if (args[0].equalsIgnoreCase("list")) {
				if ((sender instanceof Player)) {
					Player player = (Player) sender;
					player.getName();
					m.player(player, "&a____________[ &6Players on Duty &a]____________");
					if  (!dutyList.isEmpty()) {
						m.player(player, "&6" + dutyList);												
					} else {
						m.player(player, "&6No players are on duty.");
					}
				} else {
					Log.info("____________[ Players on Duty ]____________");
					if  (!dutyList.isEmpty()) {
						Log.info("" + dutyList);												
					} else {
						Log.info("No players are on duty.");
					}						
				}
			}
			
			// /dm back
			if (args[0].equalsIgnoreCase("back")) {
				if ((sender instanceof Player)) {
					Player player = (Player) sender;
					String playerName = player.getName();
					if (Configuration.data.contains(playerName)) {
						if (HashMaps.dutyLoc.containsKey(playerName)) {
							player.teleport(HashMaps.dutyLoc.get(playerName));
							m.player(player, "&6[DoOdy] &aBack to last known duty location.");
							Debug.check(playerName + " &ateleported back to last known duty location");
						} else {
							m.player(player, "&6[DoOdy] &eYou have no last known duty location.");
							Debug.check("<on /dm back|L-156> Last known duty loc. Unknown.");
						}
					} else {
						m.player(player, "&6[DoOdy] &eYou are not on duty.");
						Debug.check("<on /dm back|L-160> " + playerName + " is not on duty.");
					}
				}
			}

			// /dm reload
			if (args[0].equalsIgnoreCase("reload")) {
				if ((sender instanceof Player)) {
					Player player = (Player) sender;
					player.getName();
					if (player.isOp() || player.hasPermission("doody.reload")) {
						try {
							Configuration.config.reload();
							m.player(player, "&6[DoOdy] &aConfig Reloaded.");
						} catch (FileNotFoundException e) {
							m.player(player, "&6[DoOdy] &cConfig Not Found!");
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (InvalidConfigurationException e) {
							m.player(player, "&6[DoOdy] &cConfig Not Valid Format!");
							e.printStackTrace();
						}
					} else {
						m.player(player, "&6[DoOdy] &cNeed permission node doody.reload");
					}
				} else {
					try {
						Configuration.config.reload();
						Log.info("Config Reloaded.");
					} catch (FileNotFoundException e) {
						Log.info("Config Not Found!");
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvalidConfigurationException e) {
						Log.info("Config Not Valid Format!");
						e.printStackTrace();
					}						
				}
			}
		} else if (args.length == 2) {

			// dm <player> on
			if ((sender instanceof Player)) {
				Player player = (Player) sender;
				player.getName();

				if ((player.getServer().getPlayer(args[0]) != null) && (args[1].equalsIgnoreCase("on"))) {
					if (player.isOp() || player.hasPermission("doody.others")) {
						Player targetPlayer = player.getServer().getPlayer(args[0]);
						String targetPlayerName = targetPlayer.getName();

						if (!Configuration.data.contains(targetPlayerName)) {
							if (targetPlayer.getGameMode() == GameMode.CREATIVE) {
								m.player(player, "&6[DoOdy] &e" + targetPlayerName + " &cmust be in Survival mode first!");
							} else {
								setDoodyOther(player, targetPlayer);
								return true;
							}
						} else if (Configuration.data.contains(targetPlayerName)) {
							m.player(player, "&6[DoOdy] &e" + targetPlayerName + " &cis already on Duty!");
						}
					}
				}
			} else if (args[1].equalsIgnoreCase("on")) {
				Player targetPlayer = null;
				if ((Bukkit.getServer().getPlayer(args[0]) != null)) {
					targetPlayer = Bukkit.getServer().getPlayer(args[0]);
					String targetPlayerName = targetPlayer.getName();

					if (!Configuration.data.contains(targetPlayerName)) {
						if (targetPlayer.getGameMode() == GameMode.CREATIVE) {
							Log.info("[DoOdy] " + targetPlayerName + " must be in Survival mode first!");
						} else {
							setDoody(targetPlayer);
							Log.info("Console put " + targetPlayerName + " on Duty.");
							return true;
						}
					} else if (Configuration.data.contains(targetPlayerName)) {
						Log.info("[DoOdy] " + targetPlayerName + " is already on Duty!");
					}
				} else {
					Log.info("Player is not Online!");
				}
				return true;
			}

			// dm <player> off
			if ((sender instanceof Player)) {
				Player player = (Player) sender;
				String playerName = player.getName();
				if ((player.getServer().getPlayer(args[0]) != null) && (args[1].equalsIgnoreCase("off"))) {
					if (player.isOp() || player.hasPermission("doody.others")) {
						Player targetPlayer = player.getServer().getPlayer(args[0]);
						String targetPlayerName = targetPlayer.getName();

						if (Configuration.data.contains(targetPlayerName)) {
							Debug.check(playerName + "used /doody " + targetPlayerName + " off");
							removeDoodyOthers(player, targetPlayer);
							return true;
						} else {
							m.player(player, "&6[DoOdy] &e" + targetPlayerName + " &cis not on Duty!");
						}
					}
				}
			} else if (args[1].equalsIgnoreCase("off")){
				Player targetPlayer = null;
				if ((Bukkit.getServer().getPlayer(args[0]) != null)) {
					targetPlayer = Bukkit.getServer().getPlayer(args[0]);
					String targetPlayerName = targetPlayer.getName();

					if (Configuration.data.contains(targetPlayerName)) {
						removeDoody(targetPlayer);
						Log.info("[DoOdy] Console removed " + targetPlayerName + " from Duty.");
						return true;
					} else {
						Log.info("[DoOdy] " + targetPlayerName + " is not on Duty!");
					}
				}						
				return true;
			}

			if ((args[0].equalsIgnoreCase("debug")) && (args[1].equalsIgnoreCase("on"))) {
				if ((sender instanceof Player)) {
					Player player = (Player) sender;
					player.getName();
					if (Configuration.config.getBoolean("Debug.enabled") == false) {
						if (player.isOp() || player.hasPermission("doody.debug")) {
							try {
								Configuration.config.set("Debug.enabled", true);
								Configuration.config.save();
								m.player(player, "&6[DoOdy] &aDebug Mode Enabled!");
								m.player(player, "&6[DoOdy] &aDebug messages are output to Server Console/Log.");
								Configuration.config.reload();
								m.player(player, "&6[DoOdy] &aDisable Debug Mode with /doody debug off");
							} catch (FileNotFoundException e) {
								m.player(player, "&6[DoOdy] &cConfig Not Found!");
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (InvalidConfigurationException e) {
								m.player(player, "&6[DoOdy] &cConfig Not Valid Format!");
								e.printStackTrace();
							}					
						} else {
							m.player(player, "&6[DoOdy] &cNeed permission node doody.reload");
						}
					} else {
						m.player(player, "&6[DoOdy] &cDebug Mode is already on!");						
					}
					return true;
				} else {
					Log.info("This command hasn't been ported yet.");
					return true;
				}
			}
			if ((args[0].equalsIgnoreCase("debug")) && (args[1].equalsIgnoreCase("off"))) {
				if ((sender instanceof Player)) {
					Player player = (Player) sender;
					player.getName();
					if (Configuration.config.getBoolean("Debug.enabled") == true) {
						if (player.isOp() || player.hasPermission("doody.debug")) {
							try {
								Configuration.config.set("Debug.enabled", false);
								Configuration.config.save();
								m.player(player, "&6[DoOdy] &aDebug Mode Disabled!.");
								Configuration.config.reload();
								m.player(player, "&6[DoOdy] &aHope debugging shed some light on any issues with DoOdy.");
							} catch (FileNotFoundException e) {
								m.player(player, "&6[DoOdy] &cConfig Not Found!");
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (InvalidConfigurationException e) {
								m.player(player, "&6[DoOdy] &cConfig Not Valid Format!");
								e.printStackTrace();
							}					
						} else {
							m.player(player, "&6[DoOdy] &cNeed permission node doody.reload");
						}
					} else {
						m.player(player, "&6[DoOdy] &cDebug Mode is already off!");						
					}
					return true;
				} else {
					Log.info("This command hasn't been ported yet.");
					return true;
				}
			}
		}
		return false;
	}

	private static void getCmdsPlayer(Player player) {
		m.player(player, "&a____________[ &6DoOdy Commands &a]____________");
		m.player(player, "&a____________[ &6Short: /dm, /duty &a]____________");

		if (player.isOp() || player.hasPermission("doody.duty")) {
			m.player(player, "&6/doody &bon &fTurns on Duty Mode.");
			m.player(player, "&6/doody &boff &fTurns off Duty Mode.");
		}
		if (player.isOp() || player.hasPermission("doody.others")) {
			m.player(player, "&6/doody &b<player> <on/off> &fPut <player> <on/off> Duty Mode.");
		}
		m.player(player, "&6/doody &blist &fShows players on DoOdy Duty.");
		if (player.isOp() || player.hasPermission("doody.reload")) {
			m.player(player, "&6/doody &breload &fReload the config.yml changes ingame.");
		}
		if (player.isOp() || player.hasPermission("doody.debug")) {
			m.player(player, "&6/doody &bdebug on/off &fEnable/Disable debug mode.");
		}
		if  (!dutyList.isEmpty()) {
			m.player(player, "&a____________[ &6Players on Duty &a]____________");
			m.player(player, "&6" + dutyList);												
		}
	}
	
	private static void getCmdsConsole() {
		Log.info("____________[ DoOdy Commands ]____________");
		Log.info("____________[ Short: /dm, /duty ]____________");
		Log.info("/doody <player> <on/off> [Put <player> <on/off> Duty Mode.]");
		Log.info("/doody list [Shows players on DoOdy Duty.]");
		Log.info("/doody reload [Reload the config.yml changes ingame.]");
		Log.info("/doody debug <on/off> [Enable/disable debug mode]");
	}
	
	//Enable Duty Mode
	public static void setDoody(Player player) {
		String playerName = player.getName();
		try {
			//add player to duty list.
			addPlayer(playerName);
			
			//save player's xp, and inventory and clear it.
			saveInv(player);

			//save player's armour content.
			HashMaps.armour.put(playerName, player.getInventory().getArmorContents());

			//Save player location to file.
			saveLoc(player);
			
			//put player on creative mode.
			player.setGameMode(GameMode.CREATIVE);
			m.player(player, "&6[DoOdy] &aYou're now on Duty.");

			//Give Duty Tools?
			dutyItems(player);
			
			Debug.check("<setDoody> " + playerName + "'s data has been saved in #Maps.");
		} catch (Exception e) {
			player.setGameMode(GameMode.CREATIVE);
			player.getInventory().clear();
			Log.severe("Failed Storing #Map on /doody on");
			m.player(player, "&6[DoOdy] &cFailed storing data in #Maps.");
		}		
	}

	//Remove Duty Mode
	public static void removeDoody(Player player) {
		String playerName = player.getName();
		try {
			//remove player from list of players on duty.
			dutyList.removeAll(Arrays.asList(playerName));
			
			//resore player's gamemode & remove data from #map.
			player.setGameMode(GameMode.SURVIVAL);
						
			//Save on duty location & flying status for /dm back
			HashMaps.dutyLoc.put(playerName, player.getLocation());

			//restore player's location.
			restoreLoc(player);
			
			//Restore player Inventory & XP
			restoreInv(player);

			//restore player's armour contents & remove data from #map.
			if (HashMaps.armour.containsKey(playerName)) {
				player.getInventory().setArmorContents(HashMaps.armour.get(playerName));
			}

			m.player(player, "&6[DoOdy] &aYou're no longer on Duty.");	
			Debug.check("<removeDoody> " + playerName + "'s data restored & #maps cleared.");
		} catch (Exception e) {
			dutyList.removeAll(Arrays.asList(playerName));
			player.setGameMode(GameMode.SURVIVAL);
			player.getInventory().clear();
			Log.warnings(playerName + " was on duty when plugin was disabled. Failed restoring inventory.");
			Log.warnings(playerName + " was on duty when plugin was disabled. Failed restoring location.");
			m.player(player, "&6[DoOdy] &cFailed restoring Inventory. Plugin encountered error.");
			HashMaps.removeMaps(playerName);
		}
		HashMaps.removeMaps(playerName);
	}
	
	//Put Another Player on Duty
	private static void setDoodyOther(Player player, Player targetPlayer) {
		String playerName = targetPlayer.getName();
		String targetPlayerName = targetPlayer.getName();
		if (Configuration.config.getBoolean("Debug.enabled")) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + playerName + "used /doody " + targetPlayer + " on");
		}
		try {
			//Add target player to duty list.
			addPlayer(targetPlayerName);
			
			//Save target player's xp, and inventory and clear it.
			saveInv(targetPlayer);

			//Save target player's Location
			saveLoc(targetPlayer);

			//Save target player's armour content.
			HashMaps.armour.put(targetPlayerName, targetPlayer.getInventory().getArmorContents());

			//Put target player on creative mode.
			targetPlayer.setGameMode(GameMode.CREATIVE);
			m.player(player, "&6[DoOdy] &e" + targetPlayerName + "&a is now on Duty.");
			m.player(targetPlayer, "&6[DoOdy] &e" + playerName + "&a put you on Duty.");

			//Give Duty Tools?
			dutyItems(targetPlayer);
			
			Debug.check("<setDoodyOther> " + targetPlayerName + "'s data saved to #Maps.");
		} catch (Exception e) {
			targetPlayer.setGameMode(GameMode.CREATIVE);
			targetPlayer.getInventory().clear();
			Log.severe("Failed Storing #Map on /doody " + targetPlayerName + " on");
			m.player(player, "&6[DoOdy] &cFailed storing &e" + targetPlayerName + "'s &cdata in #Maps.");
		}
		Log.info(playerName + " put " + targetPlayerName + " on Duty.");		
	}
	
	//Remove Another Player from Duty
	private static void removeDoodyOthers(Player player, Player targetPlayer) {
		String playerName = player.getName();
		String targetPlayerName = targetPlayer.getName();
		try {
			//remove player from list of players on duty.
			dutyList.removeAll(Arrays.asList(targetPlayerName));
			
			//resore player's gamemode & remove data from #map.
			targetPlayer.setGameMode(GameMode.SURVIVAL);
						
			//Save on duty location & flying status for /dm back
			HashMaps.dutyLoc.put(targetPlayerName, targetPlayer.getLocation());

			//Restore target player's location.
			restoreLoc(targetPlayer);

			//Restore target player's Inventory & XP
			restoreInv(targetPlayer);

			//respore player's armour contents & remove data from #map.
			if (HashMaps.armour.containsKey(targetPlayerName)) {
				targetPlayer.getInventory().setArmorContents(HashMaps.armour.get(targetPlayerName));
				HashMaps.armour.remove(targetPlayerName);
			}
			m.player(player, "&6[DoOdy] &e" + targetPlayerName + "&a is no longer on Duty.");
			m.player(targetPlayer, "&6[DoOdy] &e" + playerName + "&a removed you from your Duties.");
			Debug.check("<removeDoodyOthers> " + playerName + "'s data restored & #maps cleared.");
		} catch (Exception e) {
			dutyList.removeAll(Arrays.asList(targetPlayerName));
			targetPlayer.setGameMode(GameMode.SURVIVAL);
			targetPlayer.getInventory().clear();
			Log.warnings(targetPlayerName + " was on duty when plugin was disabled. Failed restoring inventory.");
			Log.warnings(targetPlayerName + " was on duty when plugin was disabled. Failed restoring location.");
			m.player(player, "&6[DoOdy] &cFailed restoring &e" + targetPlayerName + "'s &cInventory. Plugin encountered error.");
			m.player(targetPlayer, "&6[DoOdy] &cFailed restoring Inventory. Plugin encountered error.");
		}
		Log.info(playerName + " removed " + targetPlayerName + " from Duty.");
	}
	
	//Save Player Inventory & XP
	public static void saveInv(Player player) {
		String playerName = player.getName();
		Inventory playerInv = player.getInventory();
		
		//save XP
		Configuration.data.set(playerName + ".XP", player.getLevel());
		
		HashMaps.inventory.put(playerName, playerInv.getContents());
		try {
			Integer size = playerInv.getSize();
			Integer i = 0;
			for(i=0; i < size; i++) {
				ItemStack item = playerInv.getItem(i);
								
				if (item.getAmount() != 0) {
					Short durab = item.getDurability();
					
					Configuration.data.set(playerName + ".Inventory." + i.toString() + ".stack", item);
					Configuration.data.set(playerName + ".Inventory." + i.toString() + ".amount", item.getAmount());					
					Configuration.data.set(playerName + ".Inventory." + i.toString() + ".durability", durab.intValue());
					Configuration.data.save();
				}
			}
		} catch(Exception e) {
		}
		
		//Clear player inventory
		playerInv.clear();
	}
	
	//Restore Player Inventory & XP
	public static void restoreInv(Player player) {
		String playerName = player.getName();

		if (HashMaps.inventory.containsKey(playerName)) {
			player.getInventory().setContents(HashMaps.inventory.get(playerName));
		} else {
			player.getInventory().clear();
			try {
				Integer size = player.getInventory().getSize();
				Integer i = 0;
				for(i=0; i < size; i++) {
					ItemStack item = new ItemStack(0, 0);
					if(Configuration.data.getInt(playerName + ".Inventory." + i.toString() + ".amount", 0) != 0) {
						ItemStack stack = Configuration.data.getItemStack(playerName + ".Inventory." + i.toString() + ".stack");
						Integer amount = Configuration.data.getInt(playerName + ".Inventory." + i.toString() + ".amount", 0);
						Integer durability = Configuration.data.getInt(playerName + ".Inventory." + i.toString() + ".durability", 0);
						
						item.setType(stack.getType());
						item.setItemMeta(stack.getItemMeta());
						item.setAmount(amount);
						item.setDurability(Short.parseShort(durability.toString()));
						player.getInventory().setItem(i, item);
					}
				}
				player.setLevel(Configuration.data.getInt(playerName + ".XP"));
			} catch(Exception e) {
				Log.severe("Failed Loading Player Inventory from file on /doody off");
			}
		}
		Configuration.data.set(playerName, null);
		try {
			Configuration.data.save();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Duty Items as per Config
	public static void dutyItems(Player player) {
		//Give Duty Tools?
		if (Configuration.config.getBoolean("Duty Tools.enabled")) {
			Inventory playerInv = player.getInventory();
			playerInv.setItem(0, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 1"), 1, (short) 0));
			playerInv.setItem(1, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 2"), 1, (short) 0));
			playerInv.setItem(2, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 3"), 1, (short) 0));
			playerInv.setItem(3, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 4"), 1, (short) 0));
			playerInv.setItem(4, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 5"), 1, (short) 0));
			playerInv.setItem(5, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 6"), 1, (short) 0));
			playerInv.setItem(6, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 7"), 1, (short) 0));
			playerInv.setItem(7, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 8"), 1, (short) 0));
			playerInv.setItem(8, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 9"), 1, (short) 0));
		}
	}
	
	//Save Player Location
	public static void saveLoc(Player player) {
		String playerName = player.getName();

		//Save player location to file.
		String worldname = player.getLocation().getWorld().getName();			
		Configuration.data.set(playerName + ".Location." + "world", worldname);
		Configuration.data.set(playerName + ".Location." + "x", player.getLocation().getX());
		Configuration.data.set(playerName + ".Location." + "y", player.getLocation().getY());
		Configuration.data.set(playerName + ".Location." + "z", player.getLocation().getZ());
		Configuration.data.set(playerName + ".Location." + "pitch", player.getLocation().getPitch());
		Configuration.data.set(playerName + ".Location." + "yaw", player.getLocation().getYaw());
		try {
			Configuration.data.save();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Restore Player Location
	public static void restoreLoc(Player player) {
		String playerName = player.getName();
		World world = Bukkit.getServer().getWorld(Configuration.data.getString(playerName + ".Location." + "world"));
		
		double x = Configuration.data.getDouble(playerName + ".Location." + "x");
		double y = Configuration.data.getDouble(playerName + ".Location." + "y");
		double z = Configuration.data.getDouble(playerName + ".Location." + "z");
		double pit = Configuration.data.getDouble(playerName + ".Location." + "pitch");
		double ya = Configuration.data.getDouble(playerName + ".Location." + "yaw");
		float pitch = (float) pit;
		float yaw = (float) ya;

		Location local = new Location(world, x, y, z, yaw, pitch);
		player.teleport(local);

		Configuration.data.set(playerName + ".Location", null);
		try {
			Configuration.data.save();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
