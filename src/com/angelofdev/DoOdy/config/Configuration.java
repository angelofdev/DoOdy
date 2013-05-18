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

package com.angelofdev.DoOdy.config;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.angelofdev.DoOdy.Log;


public class Configuration {

	public static MyConfiguration config;
	public static MyConfiguration data;
	
	static {
		config = new MyConfiguration();
		data = new MyConfiguration();
		
		if(load(config,"config.yml")){
			config = MyConfiguration.loadConfiguration("plugins/DoOdy/config.yml");
			Config.set();
			save(config);
		}
		Config.set();
		try {
			config.save();
		} catch (Exception e) {
			e.printStackTrace();
		}
		data = MyConfiguration.loadConfiguration("plugins/DoOdy/data/data.yml");
	}

	public static void start(){
		Log.info("Static Configuration loading...");
	}

	private static void exclaim(String filename){
		Log.info("Saved file "+ filename + "!");
	}

	private static void complain(String filename){
		Log.severe("On file "+ filename + ":");
		Log.severe("Invalid configuration! Did you use tabs or restrict permissions?");
	}

	private static void complainFileCreation(String filename){
		Log.severe("On file "+ filename + ":");
		Log.severe("Could NOT create default files! Did you restrict permissions?");
	}

	// return true if defaults need to be created
	private static boolean load(MyConfiguration y, String name){
		try {
			y.load("plugins/DoOdy/"+name);
		} catch (FileNotFoundException e) {
			return true;
		} catch (Exception e) {
			complain(name);
		}
		return false;
	}

	private static void save(MyConfiguration y){
		try {
			y.save();
			try {
				y.load("plugins/DoOdy/" + y.getFilename());
			} catch (Exception e) {
			}
			exclaim(y.getFilename());
		} catch (IOException e) {
			complainFileCreation(y.getFilename());
		}
	}
}