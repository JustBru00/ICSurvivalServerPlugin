package net.introvertscove.survivalserver.plugin;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import net.introvertscove.survivalserver.plugin.utils.Messager;

public class IntrovertsPlugin extends JavaPlugin {

	private static IntrovertsPlugin instance;
	public static ConsoleCommandSender console = Bukkit.getConsoleSender();
	public static Logger logger = Bukkit.getLogger();
	public static String prefix = Messager.color("&8[&bIntroverts&fCove&8] &6");
	

	@Override
	public void onDisable() {
		
		instance = null;
	}

	@Override
	public void onEnable() {
		instance = this;
		
		
	}

	public static IntrovertsPlugin getInstance() {
		return instance;
	}
	
}
