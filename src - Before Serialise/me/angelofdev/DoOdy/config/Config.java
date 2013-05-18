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

package me.angelofdev.DoOdy.config;

import java.util.Arrays;


public class Config {

	public static void set(){
		//Worlds
		if (!Configuration.config.contains("Denied.worlds")) {
			Configuration.config.set("Denied.worlds", Arrays.asList(
					"noDutyWorld",
					"noDutyWorld2"));
		}
		//Comands
		if (!Configuration.config.contains("Denied.commands")) {
			Configuration.config.set("Denied.commands", Arrays.asList(
					"/example1",
					"/another example"));
		}
		//PVP
		if(!Configuration.config.contains("Duty Deny PVP.enabled")) {
			Configuration.config.set("Duty Deny PVP.enabled", true);
		}
		//Item Drops
		if(!Configuration.config.contains("Duty Deny Drops.enabled")) {
			Configuration.config.set("Duty Deny Drops.enabled", true);
		}
		if(!Configuration.config.contains("Duty Deny Drops.whitelist")) {
			Configuration.config.set("Duty Deny Drops.whitelist", Arrays.asList(
					"#1",
					"#3"));
		}
		if(!Configuration.config.contains("Duty Deny Drops.messages")) {
			Configuration.config.set("Duty Deny Drops.messages", true);
		}
		//Storage Interaction
		if(!Configuration.config.contains("Deny Storage.enabled")) {
			Configuration.config.set("Deny Storage.enabled", true);
		}
		if(!Configuration.config.contains("Deny Storage.messages")) {
			Configuration.config.set("Deny Storage.messages", true);
		}
		if(!Configuration.config.contains("Deny Storage.storage")) {
			Configuration.config.set("Deny Storage.storage", Arrays.asList(
					"23",
					"54",
					"61",
					"62",
					"95",
					"130"));
		}
		//Blocks Placement/Break
		if(!Configuration.config.contains("Denied Blocks.messages")) {
			Configuration.config.set("Denied Blocks.messages", true);
		}
		if(!Configuration.config.contains("Denied Blocks.Place")) {
			Configuration.config.set("Denied Blocks.Place", Arrays.asList(
					"46",
					"7"));
		}
		if(!Configuration.config.contains("Denied Blocks.Break")) {
			Configuration.config.set("Denied Blocks.Break", Arrays.asList(
					"7"));
		}
		//Duty Tools
		if (!Configuration.config.contains("Duty Tools.enabled")) {
			Configuration.config.set("Duty Tools.enabled", true);
		}
		if (!Configuration.config.contains("Duty Tools.items.slot 1")) {
			Configuration.config.set("Duty Tools.items.slot 1", 271);
		}
		if (!Configuration.config.contains("Duty Tools.items.slot 2")) {
			Configuration.config.set("Duty Tools.items.slot 2", 270);
		}
		if (!Configuration.config.contains("Duty Tools.items.slot 3")) {
			Configuration.config.set("Duty Tools.items.slot 3", 345);
		}
		if (!Configuration.config.contains("Duty Tools.items.slot 4")) {
			Configuration.config.set("Duty Tools.items.slot 4", 287);
		}
		if (!Configuration.config.contains("Duty Tools.items.slot 5")) {
			Configuration.config.set("Duty Tools.items.slot 5", 7);
		}
		if (!Configuration.config.contains("Duty Tools.items.slot 6")) {
			Configuration.config.set("Duty Tools.items.slot 6", 0);
		}
		if (!Configuration.config.contains("Duty Tools.items.slot 7")) {
			Configuration.config.set("Duty Tools.items.slot 7", 0);
		}
		if (!Configuration.config.contains("Duty Tools.items.slot 8")) {
			Configuration.config.set("Duty Tools.items.slot 8", 0);
		}
		if (!Configuration.config.contains("Duty Tools.items.slot 9")) {
			Configuration.config.set("Duty Tools.items.slot 9", 0);
		}
		if(!Configuration.config.contains("Debug.enabled")) {
			Configuration.config.set("Debug.enabled", false);
		}
		// Old Config cleanup stuff
		if(Configuration.config.contains("Allow")) {
			Configuration.config.set("Allow", null);
		}
	}
}

