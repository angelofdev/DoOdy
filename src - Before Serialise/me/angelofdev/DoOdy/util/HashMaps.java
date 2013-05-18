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

package me.angelofdev.DoOdy.util;

import java.util.HashMap;

import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;

public class HashMaps {
	public static HashMap<String, Integer> expOrb = new HashMap<String, Integer>();
	public static HashMap<String, ItemStack[]> armour = new HashMap<String, ItemStack[]>();
	public static HashMap<String, GameMode> duty = new HashMap<String, GameMode>();
	public static HashMap<String, ItemStack[]> inventory = new HashMap<String, ItemStack[]>();

	public static void removeMaps(String playerName) {
		if (HashMaps.expOrb.containsKey(playerName)) {
			HashMaps.armour.remove(playerName);
		}
		if (HashMaps.expOrb.containsKey(playerName)) {
			HashMaps.duty.remove(playerName);
		}
		if (HashMaps.expOrb.containsKey(playerName)) {
			HashMaps.expOrb.remove(playerName);
		}
		if (HashMaps.inventory.containsKey(playerName)) {
			HashMaps.inventory.remove(playerName);
		}
	}
}
