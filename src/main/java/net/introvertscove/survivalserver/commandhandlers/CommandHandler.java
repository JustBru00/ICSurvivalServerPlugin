package net.introvertscove.survivalserver.commandhandlers;

import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.introvertscove.survivalserver.plugin.utils.Messager;

public abstract class CommandHandler {

	/**
	 * This method should only be run if the command sender HAS PERMISSION TO DO THIS.
	 * @param args
	 * @param sender
	 * @param discordMsgChannel
	 * @return
	 */
	public abstract ResponseType handleCommand(String[] args, Optional<CommandSender> sender, Optional<MessageChannel> discordMsgChannel);
	
	public static void sendMessage(String uncoloredMessage, Optional<CommandSender> sender, Optional<MessageChannel> discordMsgChannel) {
		if (sender.isPresent()) {
			Messager.msgSender(uncoloredMessage, sender.get());
		}
	
		if (discordMsgChannel.isPresent()) {
			discordMsgChannel.get().sendMessage(ChatColor.stripColor(Messager.color(uncoloredMessage))).queue();
		}
	}
	
}
