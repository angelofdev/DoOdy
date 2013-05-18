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

package me.angelofdev.DoOdy.listeners;

import java.util.List;

import me.angelofdev.DoOdy.config.Configuration;
import me.angelofdev.DoOdy.util.Debug;
import me.angelofdev.DoOdy.util.HashMaps;
import me.angelofdev.DoOdy.util.MessageSender;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class DoOdyBlockListener implements Listener {
	private MessageSender m = new MessageSender();

	public DoOdyBlockListener() {
	}

	List<Integer> configBlocksPlaceDenied = Configuration.config.getIntegerList("Denied Blocks.Place");
	List<Integer> configBlocksBreakDenied = Configuration.config.getIntegerList("Denied Blocks.Break");
	
	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		String playername = player.getName();
				
		if (HashMaps.duty.containsKey(playername)) {
			Block block = event.getBlock();
			int blockID = block.getTypeId();
			String message = block.getType().name();
			String blockname = message.toLowerCase();
			if (configBlocksPlaceDenied.contains(blockID)) {
				if (!(player.isOp() || player.hasPermission("doody.allowplace"))) {
					event.setCancelled(true);
					if (Configuration.config.getBoolean("Denied Blocks.messages")) {
						m.player(player, "&cThere's no need to place &e" + blockname + " &cwhile on Duty.");
					}
				} else {
					if (Configuration.config.getBoolean("Debug.enabled")) {
						if (player.isOp()) {
							Debug.normal("<onBlockPlace> Warning! " + playername + " is OP -Allowing block place, " + blockname);
						} else if (player.hasPermission("doody.allowplace")) {
							Debug.normal("<onBlockPlace> Warning! " + playername + " has doody.allowplace -Allowing block place, " + blockname);
						} else if (!(configBlocksPlaceDenied.contains(blockID))) {
							Debug.normal("<onBlockPlace> Warning! " + blockname + " is not in 'Denied Blocks.Place' list -Allowing block place");
						} else {
							//It should not have reached here
							Debug.severe("<onBlockPlace> Another plugin may be causing a conflict. DoOdy Debug cannot make sense. Section onBlockPlace in DoOdyBlockListener");
						}
					}
				}
			}
		}		
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		String playername = player.getName();		
		
		if (HashMaps.duty.containsKey(playername)) {
			Block block = event.getBlock();
			String message = block.getType().name();
			String blockname = message.toLowerCase();
			int blockID = block.getTypeId();
			if (configBlocksBreakDenied.contains(blockID)) {
				if (!(player.isOp() || player.hasPermission("doody.allowbreak"))) {
					event.setCancelled(true);
					if (Configuration.config.getBoolean("Denied Blocks.messages")) {
						m.player(player, "&cThere's no need to break &e" + blockname + "&cwhile on Duty.");
					}
				} else {
					if (Configuration.config.getBoolean("Debug.enabled")) {
						if (player.isOp()) {
							Debug.normal("<onBlockPlace> Warning! " + playername + " is OP -Allowing block break, " + blockname);
						} else if (player.hasPermission("doody.allowbreak")) {
							Debug.normal("<onBlockPlace> Warning! " + playername + " has doody.allowbreak -Allowing block break, " + blockname);
						} else if (!(configBlocksBreakDenied.contains(blockID))) {
							Debug.normal("<onBlockPlace> Warning! " + blockname + " is not in 'Denied Blocks.Break' list -Allowing block break");
						} else {
							//It should not have reached here
							Debug.severe("<onBlockPlace> Another plugin may be causing a conflict. DoOdy Debug cannot make sense. Section onBlockBreak in DoOdyBlockListener");
						}
					}
				}
			}
		}
	}	
}
