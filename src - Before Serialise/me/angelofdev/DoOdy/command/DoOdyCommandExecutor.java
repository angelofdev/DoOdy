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

package me.angelofdev.DoOdy.command;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.angelofdev.DoOdy.DoOdy;
import me.angelofdev.DoOdy.Log;
import me.angelofdev.DoOdy.config.Configuration;
import me.angelofdev.DoOdy.util.Debug;
import me.angelofdev.DoOdy.util.HashMaps;
import me.angelofdev.DoOdy.util.MessageSender;

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

public class DoOdyCommandExecutor implements CommandExecutor {
	private static MessageSender m = new MessageSender();

	@SuppressWarnings("unused")
	private DoOdy plugin;
	
	public DoOdyCommandExecutor(DoOdy plugin) {
		this.plugin = plugin;
	}
		
	public static ArrayList<String> myArr = new ArrayList<String>();

	private static void addPlayer(String playerName) {
		myArr.add(playerName);
	}
	public static List<String> configDeniedWorlds = Configuration.config.getStringList("Denied.worlds");

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {			
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
			if (args[0].equalsIgnoreCase("on")) {
				if ((sender instanceof Player)) {
					Player player = (Player) sender;
					String playerName = player.getName();
					if (player.isOp() || player.hasPermission("doody.duty")) {
						String worldName = player.getWorld().getName();
						if ((configDeniedWorlds.contains(worldName) && player.hasPermission("doody.worlds." + worldName)) || (!configDeniedWorlds.contains(worldName))) {
							if (!HashMaps.duty.containsKey(playerName)) {
								if (player.getGameMode() == GameMode.CREATIVE) {
									m.player(player, "&6[DoOdy] &cYou must be in Survival mode first!");
								} else {
									Debug.check(playerName + " used /doody on");
									setDoody(player);
								return true;
								}
							} else if (HashMaps.duty.containsKey(playerName)) {
								m.player(player, "&6[DoOdy] &cYou're already on Duty!");
							}
						} else {
							m.player(player, "&6[DoOdy] &cCannot go on duty in the world, &e" + worldName + " &c!");
						}
					}
				}
			}

			if (args[0].equalsIgnoreCase("off")) {
				if ((sender instanceof Player)) {
					Player player = (Player) sender;
					String playerName = player.getName();
					if (HashMaps.duty.containsKey(playerName)) {
						Debug.check(playerName + " used /doody off");
						removeDoody(player);
						return true;
					} else if (!HashMaps.duty.containsKey(playerName)) {
						m.player(player, "&6[DoOdy] &cYou're not on Duty!");
					}
				}
			}

			if (args[0].equalsIgnoreCase("list")) {
				if ((sender instanceof Player)) {
					Player player = (Player) sender;
					player.getName();
					m.player(player, "&a____________[ &6Players on Duty &a]____________");
					if  (!myArr.isEmpty()) {
						m.player(player, "&6" + myArr);												
					} else {
						m.player(player, "&6No players are on duty.");
					}
				} else {
					Log.info("____________[ Players on Duty ]____________");
					if  (!myArr.isEmpty()) {
						Log.info("" + myArr);												
					} else {
						Log.info("No players are on duty.");
					}						
				}
			}

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

						if (!HashMaps.duty.containsKey(targetPlayerName)) {
							if (targetPlayer.getGameMode() == GameMode.CREATIVE) {
								m.player(player, "&6[DoOdy] &e" + targetPlayerName + " &cmust be in Survival mode first!");
							} else {
								setDoodyOther(player, targetPlayer);
								return true;
							}
						} else if (HashMaps.duty.containsKey(targetPlayerName)) {
							m.player(player, "&6[DoOdy] &e" + targetPlayerName + " &cis already on Duty!");
						}
					}
				}
			} else if (args[1].equalsIgnoreCase("on")) {
				Player targetPlayer = null;
				if ((Bukkit.getServer().getPlayer(args[0]) != null)) {
					targetPlayer = Bukkit.getServer().getPlayer(args[0]);
					String targetPlayerName = targetPlayer.getName();

					if (!HashMaps.duty.containsKey(targetPlayerName)) {
						if (targetPlayer.getGameMode() == GameMode.CREATIVE) {
							Log.info("[DoOdy] " + targetPlayerName + " must be in Survival mode first!");
						} else {
							setDoody(targetPlayer);
							Log.info("Console put " + targetPlayerName + " on Duty.");
							return true;
						}
					} else if (HashMaps.duty.containsKey(targetPlayerName)) {
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

						if (HashMaps.duty.containsKey(targetPlayerName)) {
							Debug.check(playerName + "used /doody " + targetPlayerName + " off");
							removeDoodyOthers(player, targetPlayer);
							return true;
						} else if (!HashMaps.duty.containsKey(targetPlayerName)) {
							m.player(player, "&6[DoOdy] &e" + targetPlayerName + " &cis not on Duty!");
						}
					}
				}
			} else if (args[1].equalsIgnoreCase("off")){
				Player targetPlayer = null;
				if ((Bukkit.getServer().getPlayer(args[0]) != null)) {
					targetPlayer = Bukkit.getServer().getPlayer(args[0]);
					String targetPlayerName = targetPlayer.getName();

					if (HashMaps.duty.containsKey(targetPlayerName)) {
						removeDoody(targetPlayer);
						Log.info("[DoOdy] Console removed " + targetPlayerName + " from Duty.");
						return true;
					} else if (!HashMaps.duty.containsKey(targetPlayerName)) {
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
		if  (!myArr.isEmpty()) {
			m.player(player, "&a____________[ &6Players on Duty &a]____________");
			m.player(player, "&6" + myArr);												
		}
	}
	
	private static void getCmdsConsole() {
		Log.info("____________[ DoOdy Commands ]____________");
		Log.info("____________[ Short: /dm, /duty ]____________");
		Log.info("/doody <player> <on/off> [Put <player> <on/off> Duty Mode.]");
		Log.info("/doody list [Shows players on DoOdy Duty.]");
		Log.info("/doody reload [Reload the config.yml changes ingame.]");	
	}
	
	public static void setDoody(Player player) {
		String playerName = player.getName();
		Inventory playerInv = player.getInventory();
		try {
			//add player to duty list.
			addPlayer(playerName);
			//save player's xp level.
			HashMaps.expOrb.put(playerName, player.getLevel());

			//save player's inventory & clear it.
			HashMaps.inventory.put(playerName, playerInv.getContents());
			try {
				Integer size = playerInv.getSize();
				Integer i = 0;
				for(i=0; i < size; i++) {
					ItemStack item = playerInv.getItem(i);
					if (item.getAmount() != 0) {
						Configuration.inventory.set(playerName + "." + i.toString() + ".amount", item.getAmount());
						Short durab = item.getDurability();
						Configuration.inventory.set(playerName + "." + i.toString() + ".durability", durab.intValue());
						Configuration.inventory.set(playerName + "." + i.toString() + ".type", item.getTypeId());
						Configuration.inventory.save();
					}
				}
			} catch(Exception e) {
			}

			//save player's armour content.
			HashMaps.armour.put(playerName, player.getInventory().getArmorContents());

			playerInv.clear(); //Clear player inventory

			//Save player location to file.
			String worldname = player.getLocation().getWorld().getName();
			Configuration.location.set(playerName + ".world", worldname);
			Configuration.location.set(playerName + ".x", player.getLocation().getX());
			Configuration.location.set(playerName + ".y", player.getLocation().getY());
			Configuration.location.set(playerName + ".z", player.getLocation().getZ());
			Configuration.location.set(playerName + ".pitch", player.getLocation().getPitch());
			Configuration.location.set(playerName + ".yaw", player.getLocation().getYaw());
			Configuration.location.save();

			//put player on creative mode.
			player.setGameMode(GameMode.CREATIVE);
			HashMaps.duty.put(playerName, player.getGameMode());
			m.player(player, "&6[DoOdy] &aYou're now on Duty.");

			//Give Duty Tools?
			if (Configuration.config.getBoolean("Duty Tools.enabled")) {
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
			Debug.check("<setDoody> " + playerName + "'s data has been saved in #Maps.");
		} catch (Exception e) {
			player.setGameMode(GameMode.CREATIVE);
			player.getInventory().clear();
			Log.severe("Failed Storing #Map on /doody on");
			m.player(player, "&6[DoOdy] &cFailed storing data in #Maps.");
		}		
	}

	public static void removeDoody(Player player) {
		String playerName = player.getName();
		try {
			//remove player from list of players on duty.
			myArr.removeAll(Arrays.asList(playerName));
			//resore player's gamemode & remove data from #map.
			player.setGameMode(GameMode.SURVIVAL);
			//restore player's xp & remove data from #map.
			player.setLevel(HashMaps.expOrb.get(playerName));

			//restore player's location & remove data from #map.
			World world = Bukkit.getServer().getWorld(Configuration.location.getString(playerName + ".world"));
			double x = Configuration.location.getDouble(playerName + ".x");
			double y = Configuration.location.getDouble(playerName + ".y");
			double z = Configuration.location.getDouble(playerName + ".z");
			double pit = Configuration.location.getDouble(playerName + ".pitch");
			double ya = Configuration.location.getDouble(playerName + ".yaw");
			float pitch = (float) pit;
			float yaw = (float) ya;

			Location local = new Location(world, x, y, z, yaw, pitch);
			player.teleport(local);

			Configuration.location.set(playerName, null);
			Configuration.location.save();

			if (HashMaps.inventory.containsKey(playerName)) {
				player.getInventory().setContents(HashMaps.inventory.get(playerName));
			} else {
				player.getInventory().clear();
				try {
					Integer size = player.getInventory().getSize();
					Integer i = 0;
					for(i=0; i < size; i++) {
						ItemStack item = new ItemStack(0, 0);
						if(Configuration.inventory.getInt(playerName + "." + i.toString() + ".amount", 0) !=0) {
							Integer amount = Configuration.inventory.getInt(playerName + "." + i.toString() + ".amount", 0);
							Integer durability = Configuration.inventory.getInt(playerName + "." + i.toString() + ".durability", 0);
							Integer type = Configuration.inventory.getInt(playerName + "." + i.toString() + ".type", 0);
							item.setAmount(amount);
							item.setTypeId(type);
							item.setDurability(Short.parseShort(durability.toString()));
							player.getInventory().setItem(i, item);
						}
					}
				} catch(Exception e) {
					Log.severe("Failed Loading Player Inventory from file on /doody off");
				}
			}
			Configuration.inventory.set(playerName, null);
			Configuration.inventory.save();


			//respore player's armour contents & remove data from #map.
			if (HashMaps.armour.containsKey(playerName)) {
				player.getInventory().setArmorContents(HashMaps.armour.get(playerName));
			}

			m.player(player, "&6[DoOdy] &aYou're no longer on Duty.");	
			Debug.check("<removeDoody> " + playerName + "'s data restored & #maps cleared.");
		} catch (Exception e) {
			myArr.removeAll(Arrays.asList(playerName));
			player.setGameMode(GameMode.SURVIVAL);
			player.getInventory().clear();
			Log.warnings(playerName + " was on duty when plugin was disabled. Failed restoring inventory.");
			Log.warnings(playerName + " was on duty when plugin was disabled. Failed restoring location.");
			m.player(player, "&6[DoOdy] &cFailed restoring Inventory. Plugin encountered error.");
			HashMaps.removeMaps(playerName);
		}
		HashMaps.removeMaps(playerName);
	}
	
	private void setDoodyOther(Player player, Player targetPlayer) {
		String playerName = targetPlayer.getName();
		String targetPlayerName = targetPlayer.getName();
		if (Configuration.config.getBoolean("Debug.enabled")) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + playerName + "used /doody " + targetPlayer + " on");
		}
		Inventory targetPlayerInv = targetPlayer.getInventory();
		try {
			//add player to duty list.
			addPlayer(targetPlayerName);
			//save player's xp level.
			HashMaps.expOrb.put(targetPlayerName, targetPlayer.getLevel());

			//save player's inventory & clear it.
			HashMaps.inventory.put(targetPlayerName, targetPlayerInv.getContents());
			try {
				Integer size = targetPlayerInv.getSize();
				Integer i = 0;
				for(i=0; i < size; i++) {
					ItemStack item = targetPlayerInv.getItem(i);
					if (item.getAmount() != 0) {
						Configuration.inventory.set(targetPlayerName + "." + i.toString() + ".amount", item.getAmount());
						Short durab = item.getDurability();
						Configuration.inventory.set(targetPlayerName + "." + i.toString() + ".durability", durab.intValue());
						Configuration.inventory.set(targetPlayerName + "." + i.toString() + ".type", item.getTypeId());
						Configuration.inventory.save();
					}
				}
			} catch(Exception e) {
			}

			targetPlayerInv.clear();

			String worldname = targetPlayer.getLocation().getWorld().getName();
			Configuration.location.set(targetPlayerName + ".world", worldname);
			Configuration.location.set(targetPlayerName + ".x", targetPlayer.getLocation().getX());
			Configuration.location.set(targetPlayerName + ".y", targetPlayer.getLocation().getY());
			Configuration.location.set(targetPlayerName + ".z", targetPlayer.getLocation().getZ());
			Configuration.location.set(targetPlayerName + ".pitch", targetPlayer.getLocation().getPitch());
			Configuration.location.set(targetPlayerName + ".yaw", targetPlayer.getLocation().getYaw());
			Configuration.location.save();

			//save player's armour content.
			HashMaps.armour.put(targetPlayerName, targetPlayer.getInventory().getArmorContents());

			//put player on creative mode.
			targetPlayer.setGameMode(GameMode.CREATIVE);
			HashMaps.duty.put(targetPlayerName, targetPlayer.getGameMode());
			m.player(player, "&6[DoOdy] &e" + targetPlayerName + "&a is now on Duty.");
			m.player(targetPlayer, "&6[DoOdy] &e" + playerName + "&a put you on Duty.");

			//Give Duty Tools?
			if (Configuration.config.getBoolean("Duty Tools.enabled")) {
				targetPlayerInv.setItem(0, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 1"), 1, (short) 0));
				targetPlayerInv.setItem(1, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 2"), 1, (short) 0));
				targetPlayerInv.setItem(2, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 3"), 1, (short) 0));
				targetPlayerInv.setItem(3, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 4"), 1, (short) 0));
				targetPlayerInv.setItem(4, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 5"), 1, (short) 0));
				targetPlayerInv.setItem(5, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 6"), 1, (short) 0));
				targetPlayerInv.setItem(6, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 7"), 1, (short) 0));
				targetPlayerInv.setItem(7, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 8"), 1, (short) 0));
				targetPlayerInv.setItem(8, new ItemStack(Configuration.config.getInt("Duty Tools.items.slot 9"), 1, (short) 0));
			}
			Debug.check("<setDoodyOther> " + targetPlayerName + "'s data saved to #Maps.");
		} catch (Exception e) {
			targetPlayer.setGameMode(GameMode.CREATIVE);
			targetPlayer.getInventory().clear();
			Log.severe("Failed Storing #Map on /doody " + targetPlayerName + " on");
			m.player(player, "&6[DoOdy] &cFailed storing &e" + targetPlayerName + "'s &cdata in #Maps.");
		}
		Log.info(playerName + " put " + targetPlayerName + " on Duty.");		
	}
	
	private static void removeDoodyOthers(Player player, Player targetPlayer) {
		String playerName = player.getName();
		String targetPlayerName = targetPlayer.getName();
		try {
			//remove player from list of players on duty.
			myArr.removeAll(Arrays.asList(targetPlayerName));
			//resore player's gamemode & remove data from #map.
			targetPlayer.setGameMode(GameMode.SURVIVAL);
			HashMaps.duty.remove(targetPlayerName);
			//restore player's xp & remove data from #map.
			targetPlayer.setLevel(HashMaps.expOrb.get(targetPlayerName));
			HashMaps.expOrb.remove(targetPlayerName);

			//restore player's location & remove data from #map.
			World world = Bukkit.getServer().getWorld(Configuration.location.getString(targetPlayerName + ".world"));
			double x = Configuration.location.getDouble(targetPlayerName + ".x");
			double y = Configuration.location.getDouble(targetPlayerName + ".y");
			double z = Configuration.location.getDouble(targetPlayerName + ".z");
			double pit = Configuration.location.getDouble(targetPlayerName + ".pitch");
			double ya = Configuration.location.getDouble(targetPlayerName + ".yaw");
			float pitch = (float) pit;
			float yaw = (float) ya;

			Location local = new Location(world, x, y, z, yaw, pitch);
			targetPlayer.teleport(local);

			Configuration.location.set(targetPlayerName, null);
			Configuration.location.save();

			if (HashMaps.inventory.containsKey(targetPlayerName)) {
				targetPlayer.getInventory().setContents(HashMaps.inventory.get(targetPlayerName));
			} else {
				player.getInventory().clear();
				try {
					Integer size = targetPlayer.getInventory().getSize();
					Integer i = 0;
					for(i=0; i < size; i++) {
						ItemStack item = new ItemStack(0, 0);
						if(Configuration.inventory.getInt(targetPlayerName + "." + i.toString() + ".amount", 0) !=0) {
							Integer amount = Configuration.inventory.getInt(targetPlayerName + "." + i.toString() + ".amount", 0);
							Integer durability = Configuration.inventory.getInt(targetPlayerName + "." + i.toString() + ".durability", 0);
							Integer type = Configuration.inventory.getInt(targetPlayerName + "." + i.toString() + ".type", 0);
							item.setAmount(amount);
							item.setTypeId(type);
							item.setDurability(Short.parseShort(durability.toString()));
							targetPlayer.getInventory().setItem(i, item);
						}
					}
				} catch(Exception e) {
					Log.severe("Failed Loading Target Player Inventory from file on /doody <targetplayer> off");
				}
			}
			Configuration.inventory.set(targetPlayerName, null);
			Configuration.inventory.save();


			//respore player's armour contents & remove data from #map.
			if (HashMaps.armour.containsKey(targetPlayerName)) {
				targetPlayer.getInventory().setArmorContents(HashMaps.armour.get(targetPlayerName));
				HashMaps.armour.remove(targetPlayerName);
			}
			m.player(player, "&6[DoOdy] &e" + targetPlayerName + "&a is no longer on Duty.");
			m.player(targetPlayer, "&6[DoOdy] &e" + playerName + "&a removed you from your Duties.");
			Debug.check("<removeDoodyOthers> " + playerName + "'s data restored & #maps cleared.");
		} catch (Exception e) {
			myArr.removeAll(Arrays.asList(targetPlayerName));
			HashMaps.duty.remove(targetPlayerName);
			targetPlayer.setGameMode(GameMode.SURVIVAL);
			targetPlayer.getInventory().clear();
			Log.warnings(targetPlayerName + " was on duty when plugin was disabled. Failed restoring inventory.");
			Log.warnings(targetPlayerName + " was on duty when plugin was disabled. Failed restoring location.");
			m.player(player, "&6[DoOdy] &cFailed restoring &e" + targetPlayerName + "'s &cInventory. Plugin encountered error.");
			m.player(targetPlayer, "&6[DoOdy] &cFailed restoring Inventory. Plugin encountered error.");
		}
		Log.info(playerName + " removed " + targetPlayerName + " from Duty.");
	}
}
