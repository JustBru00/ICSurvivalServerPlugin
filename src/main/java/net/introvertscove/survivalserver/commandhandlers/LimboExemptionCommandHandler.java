package net.introvertscove.survivalserver.commandhandlers;

import java.util.ArrayList;
import java.util.Optional;

import org.bukkit.command.CommandSender;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.introvertscove.survivalserver.beans.LimboExemptionStatusBean;
import net.introvertscove.survivalserver.beans.MemberDataBean;
import net.introvertscove.survivalserver.plugin.database.DatabaseManager;
import net.introvertscove.survivalserver.plugin.utils.TimeFormatter;
import net.introvertscove.survivalserver.plugin.utils.UUIDFetcher;

public class LimboExemptionCommandHandler extends CommandHandler {

	@Override
	public ResponseType handleCommand(String[] args, Optional<CommandSender> sender,
			Optional<MessageChannel> discordMsgChannel) {
		
		/**
		 * Limbo exemption Command Design:
		 * - /limboexemption <add,remove,details,listall>
		 * - /limboexemption add <memberUsernameOrUuid> <Reason>
		 * - /limboexemption remove <memberUsernameOrUuid>
		 * - /limboexemption details <memberUsernameOrUuid>
		 * - /limboexemption listall
		 * 
		 **/
		
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("add")) {
				// TODO
			} else if (args[0].equalsIgnoreCase("remove")) {
				// TODO
			} else if (args[0].equalsIgnoreCase("details")) {
				// TODO
			} else if (args[0].equalsIgnoreCase("listall")) {
				ArrayList<String> listOfMessages = new ArrayList<String>();
				listOfMessages.add("Member - Exemption Added At - Exemption Added By - Exemption Reason");
				
				final String listLineFormat = "{MEMBER} - {ADDED_AT} - {ADDED_BY} - {REASON}";
				
				for (MemberDataBean member : DatabaseManager.getAllMembers()) {
					LimboExemptionStatusBean exemptionStatus = member.getLimboExcemptionStatus();
					
					if (exemptionStatus.isExemptionActive()) {		
						Optional<String> username = UUIDFetcher.getCachedUsernameFromUuid(member.getMinecraftUuid());
						
						String messageLine = listLineFormat;
						
						if (username.isPresent()) {
							messageLine = messageLine.replace("{MEMBER}", username.get());
						} else {
							messageLine = messageLine.replace("{MEMBER}", member.getMinecraftUuid().toString());
						}
						
						messageLine = messageLine.replace("{ADDED_AT}", TimeFormatter.getTimeStampFrom(exemptionStatus.getExemptionAddedStartingAt()) + " UTC");
						
						messageLine = messageLine.replace("{ADDED_BY}", exemptionStatus.getAdminWhoAddedExemption());
						
						messageLine = messageLine.replace("{REASON}", exemptionStatus.getExemptionReason());	
						listOfMessages.add(messageLine);
					}					
				}
				
				if (listOfMessages.size() > 1) {
					if (sender.isPresent() && !discordMsgChannel.isPresent()) {
						for (String msg : listOfMessages) {
							sendMessage("&6" + msg, sender, discordMsgChannel);
						}
					} else if (!sender.isPresent() && discordMsgChannel.isPresent()) {
						StringBuilder builder = new StringBuilder();
						builder.append("```");
						for (String msg : listOfMessages) {
							builder.append(msg + "\n");
						}
						builder.append("```");
						sendMessage(builder.toString(), sender, discordMsgChannel);
					}					
				} else {
					sendMessage("&6No member has an active limbo exemption.", sender, discordMsgChannel);
				}
				
				return ResponseType.NEUTRAL;				
			} else {
				sendMessage("&cIncorrect arguments. /limboexemption <add,remove,details,listall>", sender, discordMsgChannel);
			}			
		} else {
			sendMessage("&cIncorrect arguments. /limboexemption <add,remove,details,listall>", sender, discordMsgChannel);
		}
		
		
		
		return ResponseType.FAILED;
	}

}
