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

package com.angelofdev.DoOdy.util;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class HashMaps {
	public static HashMap<String, ItemStack[]> armour = new HashMap<String, ItemStack[]>();
	public static HashMap<String, ItemStack[]> inventory = new HashMap<String, ItemStack[]>();
	public static HashMap<String, Location> dutyLoc = new HashMap<String, Location>();

	public static void removeMaps(String playerName) {
		if (HashMaps.armour.containsKey(playerName)) {
			HashMaps.armour.remove(playerName);
		}
		if (HashMaps.inventory.containsKey(playerName)) {
			HashMaps.inventory.remove(playerName);
		}
	}

	public static void removeDutyLoc(String playerName) {
		if (HashMaps.dutyLoc.containsKey(playerName)) {
			HashMaps.dutyLoc.remove(playerName);
		}
	}
}
