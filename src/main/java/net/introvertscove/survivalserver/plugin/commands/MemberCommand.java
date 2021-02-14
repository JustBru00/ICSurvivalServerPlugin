package net.introvertscove.survivalserver.plugin.commands;

import java.util.Optional;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.introvertscove.survivalserver.commandhandlers.MemberCommandHandler;
import net.introvertscove.survivalserver.plugin.utils.Messager;

public class MemberCommand implements CommandExecutor {

	public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {

		if (command.getName().equalsIgnoreCase("member")) {

			if (sender.hasPermission("introvert.member")) {
				Optional<MessageChannel> empty = Optional.empty();
				new MemberCommandHandler().handleCommand(args, Optional.of(sender), empty);
				return true;
			} else {
				Messager.msgSender("&cSorry you don't have permission.", sender);
				return true;
			}
		}

		return false;

	}
}
