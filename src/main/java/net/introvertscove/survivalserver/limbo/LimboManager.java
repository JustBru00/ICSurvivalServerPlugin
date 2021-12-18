package net.introvertscove.survivalserver.limbo;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import net.introvertscove.survivalserver.beans.LimboStatusBean;
import net.introvertscove.survivalserver.beans.MemberDataBean;
import net.introvertscove.survivalserver.commandhandlers.EpicCallback;
import net.introvertscove.survivalserver.discordbot.DiscordBotManager;
import net.introvertscove.survivalserver.plugin.IntrovertsPlugin;
import net.introvertscove.survivalserver.plugin.database.DatabaseManager;
import net.introvertscove.survivalserver.plugin.utils.Messager;
import net.introvertscove.survivalserver.plugin.utils.Reference;
import net.introvertscove.survivalserver.plugin.utils.UUIDFetcher;

public class LimboManager {

	@SuppressWarnings("deprecation")
	public static void runEveryThirtyMinutesAsync() {

		// Check through all members

		// Check if exemption has expired. If is has send an admin shout about it.
		// Not going to allow expiry on excemptions anymore.

		// Check if the time since last_logout is longer than
		// "time_from_last_logout_until.nag_message"
		// if it is then see if we already sent a nag message
		// if we already nagged them then we don't worry about it.

		// Check if the time since last_logout is longer than "sent_to_limbo"
		// send the you have been sent to limbo message.
		// unless it has already been sent.
		// Add limbo role

		// Check if the time since last_logout is longer than
		// "auto_retire_danger_message"
		// send the message if it hasn't already been sent.

		// Check if the time since last_logout is longer than "auto_retire"
		// If it is and the auto retire message was not already sent
		// Send auto retire message, send auto retire shout.
		// Add Gray retired role to discord member.
		// Remove covian role

		// All limbo message statuses get reset when the player successfully joins the
		// server again.
		// this means that they need to be blocked in the preloginevent from joining the
		// game.

		// limbo exceptions bypass ALL of the above stuff.

		for (MemberDataBean member : DatabaseManager.getAllMembers()) {
			if (member.getLimboExcemptionStatus().isExemptionActive()) {
				continue;
			}
			
			LimboStatusBean limboStatus = member.getLimboStatus();
			
			Instant lastLogout = Instant.ofEpochMilli(limboStatus.getLastLogout());
			Instant now = Instant.now();
			
			Optional<String> possibleUsername = UUIDFetcher.getCachedUsernameFromUuid(member.getMinecraftUuid());
			String username;
			if (possibleUsername.isPresent()) {
				username = possibleUsername.get();
			} else {
				username = member.getMinecraftUuid().toString();
			}
			
			
			if (Duration.between(lastLogout, now).getSeconds() > IntrovertsPlugin.getInstance().getConfig().getInt("limbo.time_from_last_logout_until.nag_message")) {
				if (!limboStatus.isNagMessageSuccessful()) {
					sendNagMessage(limboStatus, member);
				} 				
			}
			
			
			return;
			
			//FileConfiguration config = IntrovertsPlugin.getInstance().getConfig();
			
			//if (Duration.between(lastLogout, now).getSeconds() > IntrovertsPlugin.getInstance().getConfig().getInt("limbo.time_from_last_logout_until.sent_to_limbo")) {
				// Send the player to limbo
			//	if (!limboStatus.isCurrentlyInLimbo()) {
					// send limbo message
			//		DiscordBotManager.sendDirectMessageToDiscordUser(Reference.inLimboMessage.replace("{MEMBER_NAME}", username), member.getDiscordId());
					// Give limbo role
			//		DiscordBotManager.addRoleToDiscordUser(config.getInt("discord.in_limbo_role_id"), member.getDiscordId(), config.getInt("discord.guild_id"));
					// Announce to admins
			//		DiscordBotManager.sendMessageToAdminAnnouncementChannel(String.format("%s was just sent to limbo. (Offline for 8 days)", username));
					
			//		limboStatus.setInLimboMessageSent(true);
			//		limboStatus.setInLimboMessageSentAt(System.currentTimeMillis());
			//		limboStatus.setCurrentlyInLimbo(true);
			//		DatabaseManager.saveMemberDataToFile(member);	
			//	}
		//	}
			
			// Auto Retire Danger
			//if (Duration.between(lastLogout, now).getSeconds() > IntrovertsPlugin.getInstance().getConfig().getInt("limbo.time_from_last_logout_until.auto_retire_danger_message")) {
				// Send the player the auto retire danger message.
			//	if (!limboStatus.isRetiredDangerMessageSent()) {
					// send the retire danger message
			//		DiscordBotManager.sendDirectMessageToDiscordUser(Reference.inLimboMessage.replace("{MEMBER_NAME}", username), member.getDiscordId());
					// Announce to admins
				}
		//	}
			
			// Auto Retire
		//	if (Duration.between(lastLogout, now).getSeconds() > IntrovertsPlugin.getInstance().getConfig().getInt("limbo.time_from_last_logout_until.auto_retire")) {
				// Send the player the auto retire danger message.
			//	if (!limboStatus.isRetiredMessageSentToPlayer()) {
					// send the retire danger message
					// Give retired without honors role.
					// Announce to admins
			//	}
		//	}
			
			// TODO RESET ALL MESSAGE SENDING IF THE PLAYER LOGS IN SUCCESSFULLY PLAYERJOINEVENT
			// TODO BLOCK LIMBO PLAYERS IN PRELOGIN
	//	}
	

	}
	
	private static void sendToLimbo(LimboStatusBean limboStatus, MemberDataBean member) {
		// Limbo this player					
		UUIDFetcher.getUuidAsyncAndRunCallback(member.getMinecraftUuid().toString(), new EpicCallback() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void runSync(Optional<UUID> uuid, Optional<String> lastName, String originalMcUsernameOrUuid) {
				if (!lastName.isPresent()) {
					Messager.msgConsole("&c[LimboManager-SendToLimbo] Something went wrong while getting the last name of UUID. " + uuid.get());
					return;
				}
				
				
				Bukkit.getScheduler().scheduleAsyncDelayedTask(IntrovertsPlugin.getInstance(), new Runnable() {
					
					@Override
					public void run() {
						String limboMessage = Reference.inLimboMessage;
						limboMessage = limboMessage.replace("{MEMBER_NAME}", lastName.get());
						DiscordBotManager.sendDirectMessageToDiscordUser(limboMessage, member.getDiscordId());				
						DiscordBotManager.sendMessageToAdminAnnouncementChannel("Member " + lastName.get() + " was just sent to limbo. (14 days until auto retirement)");
						// TODO Role change
						Bukkit.getScheduler().scheduleSyncDelayedTask(IntrovertsPlugin.getInstance(), new Runnable() {
							
							@Override
							public void run() {
								Messager.msgConsole("&6Sent member " + lastName.get() + " to limbo.");											
								limboStatus.setCurrentlyInLimbo(true);
								limboStatus.setInLimboMessageSent(true);
								limboStatus.setPlacedInLimboAt(System.currentTimeMillis());
								DatabaseManager.saveMemberDataToFile(member);									
							}
						});
					}
				});							
			}
		});
	}
	
	
	private static void sendNagMessage(LimboStatusBean limboStatus, MemberDataBean member) {
		// Nag this player					
		UUIDFetcher.getUuidAsyncAndRunCallback(member.getMinecraftUuid().toString(), new EpicCallback() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void runSync(Optional<UUID> uuid, Optional<String> lastName, String originalMcUsernameOrUuid) {
				if (!lastName.isPresent()) {
					Messager.msgConsole("&c[LimboManager-NagMessage] Something went wrong while getting the last name of UUID. " + uuid.get());
					return;
				}
				
				
				Bukkit.getScheduler().scheduleAsyncDelayedTask(IntrovertsPlugin.getInstance(), new Runnable() {
					
					@Override
					public void run() {
						String nagMessage = Reference.preLimboNagMessage;
						nagMessage = nagMessage.replace("{MEMBER_NAME}", lastName.get());
						DiscordBotManager.sendDirectMessageToDiscordUser(nagMessage, member.getDiscordId());				
						DiscordBotManager.sendMessageToAdminAnnouncementChannel("Member " + lastName.get() + " was just sent the nag message. (3 days until limbo)");
						Bukkit.getScheduler().scheduleSyncDelayedTask(IntrovertsPlugin.getInstance(), new Runnable() {
							
							@Override
							public void run() {
								Messager.msgConsole("&6Sent nag message to the member " + lastName.get());											
								limboStatus.setNagMessageLastSentAt(System.currentTimeMillis());
								limboStatus.setNagMessageSuccessful(true);
								DatabaseManager.saveMemberDataToFile(member);									
							}
						});
					}
				});							
			}
		});
	}

}
