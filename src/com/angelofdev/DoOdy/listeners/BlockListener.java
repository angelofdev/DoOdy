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

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.angelofdev.DoOdy.config.Configuration;
import com.angelofdev.DoOdy.util.Debug;
import com.angelofdev.DoOdy.util.MessageSender;

public class BlockListener implements Listener {
	private MessageSender m = new MessageSender();

	public BlockListener() {
	}

	List<String> configBlocksPlaceDenied = Configuration.config.getStringList("Denied Blocks.Place");
	List<String> configBlocksBreakDenied = Configuration.config.getStringList("Denied Blocks.Break");
	
	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getName();
				
		if (Configuration.data.contains(playerName)) {
			Block block = event.getBlock();
			Material blockMat = block.getType();
			String blockName = blockMat.toString().toLowerCase();
			if (configBlocksPlaceDenied.contains(blockName)) {
				if (!(player.isOp() || player.hasPermission("doody.allowplace") || player.hasPermission("doody.allowplace." + blockName))) {
					event.setCancelled(true);
					if (Configuration.config.getBoolean("Denied Blocks.messages")) {
						m.player(player, "&cThere's no need to place &e" + blockName + " &cwhile on Duty.");
					}
				} else {
					if (Configuration.config.getBoolean("Debug.enabled")) {
						if (player.isOp()) {
							Debug.normal("<onBlockPlace> Warning! " + playerName + " is OP -Allowing block place, " + blockName);
							return;
						} else if (player.hasPermission("doody.allowplace")) {
							Debug.normal("<onBlockPlace> Warning! " + playerName + " has doody.allowplace -Allowing block place, " + blockName);
							return;
						} else if (player.hasPermission("doody.allowplace." + blockName)) {
							Debug.normal("<onBlockPlace> Warning! " + playerName + " has doody.allowplace." +blockName + " -Allowing block place, " + blockName);
							return;
						} else if (!(configBlocksPlaceDenied.contains(blockName))) {
							Debug.normal("<onBlockPlace> Warning! " + blockName + " is not in 'Denied Blocks.Place' list -Allowing block place");
							return;
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
		String playerName = player.getName();		
		
		if (Configuration.data.contains(playerName)) {
			Block block = event.getBlock();
			Material blockMat = block.getType();
			String blockName = blockMat.toString().toLowerCase();
			if (configBlocksBreakDenied.contains(blockName)) {
				if (!(player.isOp() || player.hasPermission("doody.allowbreak"))) {
					event.setCancelled(true);
					if (Configuration.config.getBoolean("Denied Blocks.messages")) {
						m.player(player, "&cThere's no need to break &e" + blockName + "&cwhile on Duty.");
					}
				} else {
					if (Configuration.config.getBoolean("Debug.enabled")) {
						if (player.isOp()) {
							Debug.normal("<onBlockPlace> Warning! " + playerName + " is OP -Allowing block break, " + blockName);
						} else if (player.hasPermission("doody.allowbreak")) {
							Debug.normal("<onBlockPlace> Warning! " + playerName + " has doody.allowbreak -Allowing block break, " + blockName);
						} else if (!(configBlocksBreakDenied.contains(blockName))) {
							Debug.normal("<onBlockPlace> Warning! " + blockName + " is not in 'Denied Blocks.Break' list -Allowing block break");
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
