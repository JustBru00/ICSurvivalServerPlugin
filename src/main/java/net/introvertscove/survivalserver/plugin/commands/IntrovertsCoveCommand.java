package net.introvertscove.survivalserver.plugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.introvertscove.survivalserver.plugin.IntrovertsPlugin;
import net.introvertscove.survivalserver.plugin.utils.Messager;

public class IntrovertsCoveCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("introvertscove")) {
			if (sender.hasPermission("introvert.introvertscove")) {
				Messager.msgSender("&aRunning IntrovertsCoveSurvivalServer plugin version " + IntrovertsPlugin.getInstance().getDescription().getVersion() + ".", sender);
				return true;
			} else {
				Messager.msgSender("&cSorry you don't have permission.", sender);
				return true;
			}
		}

		
		return false;
	}

}
