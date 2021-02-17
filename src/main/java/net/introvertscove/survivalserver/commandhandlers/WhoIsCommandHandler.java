package net.introvertscove.survivalserver.commandhandlers;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.command.CommandSender;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.introvertscove.survivalserver.beans.MemberDataBean;
import net.introvertscove.survivalserver.plugin.database.DatabaseManager;
import net.introvertscove.survivalserver.plugin.utils.TimeFormatter;
import net.introvertscove.survivalserver.plugin.utils.UUIDFetcher;

public class WhoIsCommandHandler extends CommandHandler {

	@Override
	public ResponseType handleCommand(final String[] args, final Optional<CommandSender> sender,
			final Optional<MessageChannel> discordMsgChannel) {
		
		if (args.length == 1) {
			sendMessage("&6Attempting to get that player's profile. This might take a minute...", sender, discordMsgChannel);
			UUIDFetcher.getUuidAsyncAndRunCallback(args[0], new EpicCallback() {
				
				public void runSync(Optional<UUID> uuid, Optional<String> lastName, String originalMcUsernameOrUuid) {
					
					if (uuid.isPresent()) {
						String actualLastName;
						if (lastName.isPresent()) {
							actualLastName = lastName.get();
							
							Optional<MemberDataBean> possibleMemberData = DatabaseManager.getMemberData(uuid.get());
							if (possibleMemberData.isPresent()) {
								MemberDataBean memberData = possibleMemberData.get();
								
								/**
								 * Whois formatting
								 * 
								 * [prefix] 
								 * [prefix] &6WhoIs - Member {player_name} ({uuid})
								 * [prefix] &6Last logged out: {logout_time}		
								 * [prefix] &6Discord ID: {discord_id}			
								 * [prefix] &6Excempt from limbo: {limbo_exemption_active}			 
								 * [prefix] &6Spectator Accounts:
								 * [prefix] &6  {spectator_account_name} ({spectator_account_uuid})
								 * 
								 */
								
								
								
								sendMessage("&6WhoIs - Member {player_name} ({uuid})"
										.replace("{player_name}", actualLastName)
										.replace("{uuid}", uuid.get().toString()), sender, discordMsgChannel);
								sendMessage("&6Last logged out: {logout_time}"
										.replace("{logout_time}", TimeFormatter.getTimeStampFrom(memberData.getLimboStatus().getLastLogout())) + " UTC", sender, discordMsgChannel);
								sendMessage("Total Playtime: " + DatabaseManager.getTotalPlaytimeFormatted(uuid.get()) + ".", sender, discordMsgChannel);
								sendMessage("&6Discord ID: {discord_id}"
										.replace("{discord_id}", String.valueOf(memberData.getDiscordId())), sender, discordMsgChannel);
								sendMessage("&6Exempt from limbo: {limbo_exemption_active}"
										.replace("{limbo_exemption_active}", String.valueOf(memberData.getLimboExcemptionStatus().isExemptionActive())), sender, discordMsgChannel);
								if (memberData.getSpectatorAccountUuids().size() == 0) {
									sendMessage("&6Spectator Accounts: None", sender, discordMsgChannel);
								} else {
									sendMessage("&6Spectator Accounts:", sender, discordMsgChannel);
									for (UUID spectator : memberData.getSpectatorAccountUuids()) {
										UUIDFetcher.getUuidAsyncAndRunCallback(spectator.toString(), new EpicCallback() {
											
											public void runSync(Optional<UUID> uuid, Optional<String> lastName, String originalMcUsernameOrUuid) {
												WhoIsCommandHandler.sendMessage("&6  {spectator_account_name} ({spectator_account_uuid})"
														.replace("{spectator_account_name}", lastName.get())
														.replace("{spectator_account_uuid}", uuid.get().toString()), sender, discordMsgChannel);
											}
										});
									}
								}								
								return;
							} else {
								sendMessage("&cFailed to load the MemberData for that player. Are you sure they are a member?", sender, discordMsgChannel);
								return;
							}						
						} else {
							sendMessage("&cFailed to get the current player name from the provided UUID. Are you sure it is correct?", sender, discordMsgChannel);
							return;
						}						
					} else {
						sendMessage("&cFailed to get UUID from the provided player name or the UUID was not formatted correctly.", sender, discordMsgChannel);
						return;
					}					
				}
			});	
			return ResponseType.NEUTRAL;
		} else {
			sendMessage("&cIncorrect arguments. Please provide a member UUID or username after /whois. /whois <memberUuidOrUsername>", sender, discordMsgChannel);
			return ResponseType.FAILED;
		}
	}

}
