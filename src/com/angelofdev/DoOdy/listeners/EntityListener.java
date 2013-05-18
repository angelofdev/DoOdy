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


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PotionSplashEvent;

import com.angelofdev.DoOdy.config.Configuration;
import com.angelofdev.DoOdy.util.Debug;
import com.angelofdev.DoOdy.util.MessageSender;

public class EntityListener implements Listener {
	private MessageSender m = new MessageSender();

	public EntityListener() {
	}
	
	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void onAttack(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			if (!(event.getEntity() instanceof Player)) {
				Debug.checkBroadcast("<onAttack> &aDefender is not a Player, allowing.");
				return;
			} else {
				Player attacker = (Player) event.getDamager();
				String attackername = attacker.getName();

				if(Configuration.data.contains(attackername)) {
					if ((Configuration.config.getBoolean("Duty Deny PVP.enabled")) && (!attacker.hasPermission("doody.pvp"))) {
						event.setCancelled(true);
					} else {
						if (Configuration.config.getBoolean("Debug.enabled")) {
							if (attacker.isOp()) {
								Debug.normal("<onAttack> Warning! " + attackername + " is Op -Allowing pvp.");								
							} else if (attacker.hasPermission("doody.pvp")) {
								Debug.normal("<onAttack> Warning! " + attackername + " has doody.pvp -Allowing pvp.");
							} else if (Configuration.config.getBoolean("Duty Deny PVP.enabled") == false) {
								Debug.normal("<onAttack> Warning! 'Duty Deny PVP.enabled is set to False in config. Allowing " + attackername + " to pvp.");
							} else {
								//It should not have reached here
								Debug.severe("<onAttack> Another plugin may be causing a conflict. DoOdy Debug cannot make sense.");
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onShootEvent(EntityShootBowEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			String playerName = player.getName();
			if (Configuration.data.contains(playerName)) {
				m.player(player, "&6[DoOdy] &cThere's no need to shoot while on duty.");
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onSplashEvent(PotionSplashEvent event) {
		if (event.getEntity().getShooter() instanceof Player) {
			Player shooter = (Player) event.getEntity().getShooter();
			if (Configuration.data.contains(shooter.getName())) {
				m.player(shooter, "&6[DoOdy] &cThere's no need to throw potions on duty.");
				event.setCancelled(true);
			}
		}
	}
}
