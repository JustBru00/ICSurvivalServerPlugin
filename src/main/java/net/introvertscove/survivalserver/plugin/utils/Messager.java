/**
 * @author Justin "JustBru00" Brubaker
 * 
 * This is licensed under the MPL Version 2.0. See license info in LICENSE.txt
 */
package net.introvertscove.survivalserver.plugin.utils;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.introvertscove.survivalserver.plugin.IntrovertsPlugin;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * 
 * @author Justin Brubaker
 *
 */
public class Messager {

	private static final Pattern RGB_PATTERN = Pattern.compile("(&)?&#([0-9a-fA-F]{6})");

	public static String color(String uncolored) {

		return ChatColor.translateAlternateColorCodes('&', convertHexColorCodes(uncolored));

	}
	
	public static void sendActionBar(String msg, Player player) {
		msg = Messager.color(msg);
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
	}

	/**
	 * ISSUE #150 Converts hex color codes.
	 * 
	 * @param uncolored
	 * @return
	 */
	public static String convertHexColorCodes(String uncolored) {
		StringBuffer builder = new StringBuffer();

		Matcher matcher = RGB_PATTERN.matcher(uncolored);

		while (matcher.find()) {
			boolean escaped = (matcher.group(1) != null);

			if (!escaped) {
				try {
					String hexColorCode = matcher.group(2);
					matcher.appendReplacement(builder, parseHexColor(hexColorCode));
					continue;
				} catch (NumberFormatException e) {
					// Ignore
				}
			}
			matcher.appendReplacement(builder, "&#$2");
		}
		matcher.appendTail(builder);

		return builder.toString();
	}

	/**
	 * @throws NumberFormatException If the provided hex color code is incorrect or
	 *                               if the version less than 1.16.
	 */
	public static String parseHexColor(String hexColor) throws NumberFormatException {
		if (hexColor.startsWith("#")) {
			hexColor = hexColor.substring(1);
		}

		if (hexColor.length() != 6) {
			throw new NumberFormatException("Invalid Length");
		}

		Color.decode("#" + hexColor);

		StringBuilder assembledColorCode = new StringBuilder();

		assembledColorCode.append("\u00a7x");

		for (char curChar : hexColor.toCharArray()) {
			assembledColorCode.append("\u00a7").append(curChar);
		}

		return assembledColorCode.toString();
	}
	
	/**
	 * This method sends the mesage inside of a Bukkit sync task.
	 * @param msg
	 * @param sender
	 */
	public static void msgSenderSync(final String msg, final CommandSender sender) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(IntrovertsPlugin.getInstance(), new Runnable() {
			
			public void run() {
				Messager.msgSender(msg, sender);				
			}
		});
	}

	public static void msgConsole(String msg) {
		if (IntrovertsPlugin.console != null) {
			IntrovertsPlugin.console.sendMessage(IntrovertsPlugin.prefix + Messager.color(msg));
		} else {
			IntrovertsPlugin.logger.info(ChatColor.stripColor(Messager.color(msg)));
		}
	}

	public static void msgPlayer(String msg, Player player) {
		player.sendMessage(IntrovertsPlugin.prefix + Messager.color(msg));
	}

	public static void msgSender(String msg, CommandSender sender) {
		sender.sendMessage(IntrovertsPlugin.prefix + Messager.color(msg));
	}
}
