package net.introvertscove.survivalserver.commandhandlers;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.introvertscove.survivalserver.beans.MemberDataBean;
import net.introvertscove.survivalserver.plugin.database.DatabaseManager;
import net.introvertscove.survivalserver.plugin.utils.Messager;
import net.introvertscove.survivalserver.plugin.utils.UUIDFetcher;

public class MemberCommandHandler extends CommandHandler {

	@Override
	public ResponseType handleCommand(String[] args, final Optional<CommandSender> sender,
			final Optional<MessageChannel> discordMsgChannel) {

		if (args.length > 1) {
			if (args[0].equalsIgnoreCase("add")) {
				if (args.length != 3) {
					sendMessage("&cIncorrect arguments. /member add <mcUsernameOrUuid> <discordId>", sender,
							discordMsgChannel);
					return ResponseType.FAILED;
				}

				long discordId = -1;
				try {
					discordId = Long.parseLong(args[2]);
				} catch (NumberFormatException e) {
					sendMessage(
							"&cSorry the provided discord id cannot be parsed as the Long datatype. Are you sure it is formatted correctly?",
							sender, discordMsgChannel);
					return ResponseType.FAILED;
				}

				final long actualDiscordId = discordId;

				sendMessage("&6Attempting to get that player's profile. This might take a minute...", sender,
						discordMsgChannel);
				UUIDFetcher.getUuidAsyncAndRunCallback(args[1], new EpicCallback() {

					public void runSync(Optional<UUID> uuid, Optional<String> lastName,
							String originalMcUsernameOrUuid) {
						if (!uuid.isPresent() || !lastName.isPresent()) {
							sendMessage(
									"&cFailed to process the player's UUID and username from the provided information. Are you sure you spelled everything correctly?",
									sender, discordMsgChannel);
							return;
						}

						UUID playerUuid = uuid.get();
						Optional<MemberDataBean> possibleMemberData = DatabaseManager.getMemberData(playerUuid);

						if (possibleMemberData.isPresent()) {
							// Already a member
							sendMessage(
									"&cSorry that minecraft UUID is already registered as a member. Use /whois "
											+ lastName.get() + " for more information.",
									sender, discordMsgChannel);
							return;
						}

						// New member
						MemberDataBean memberData = DatabaseManager.getDefaultMemberDataBean(playerUuid,
								actualDiscordId);

						DatabaseManager.saveMemberDataToFile(memberData);
						sendMessage("&aSuccessfully saved new member " + lastName.get() + ".", sender,
								discordMsgChannel);
						return;
					}
				});

				return ResponseType.NEUTRAL;
			} else if (args[0].equalsIgnoreCase("remove")) {
				if (args.length != 2) {
					sendMessage("&cIncorrect arguments. /member remove <mcUsernameOrUuid>", sender, discordMsgChannel);
					return ResponseType.FAILED;
				}
				
				sendMessage("&6Attempting to get that player's profile. This might take a minute...", sender,
						discordMsgChannel);
				UUIDFetcher.getUuidAsyncAndRunCallback(args[1], new EpicCallback() {
					
					public void runSync(Optional<UUID> uuid, Optional<String> lastName, String originalMcUsernameOrUuid) {
						if (!uuid.isPresent() || !lastName.isPresent()) {
							sendMessage(
									"&cFailed to process the player's UUID and username from the provided information. Are you sure you spelled everything correctly?",
									sender, discordMsgChannel);
							return;
						}
						
						UUID playerUuid = uuid.get();						
						Optional<MemberDataBean> maybeMemberData = DatabaseManager.getMemberData(playerUuid);
						

						if (maybeMemberData.isPresent()) {
							// Already a member
							DatabaseManager.getMemberData().set(uuid.get().toString(), "Removed Member");
							
							if (Bukkit.getOfflinePlayer(uuid.get()).isOnline()) {
								Bukkit.getPlayer(uuid.get()).kickPlayer(Messager.color("&cYou were just removed from the server member list. See the discord server for more information."));
							}						
							
							sendMessage("&aRemoved the member " + lastName.get() + ".", sender, discordMsgChannel);
							return;
						} else {
							sendMessage("&cSorry I can't remove a member that doesn't exist. It probably creates a black hole or something...", sender, discordMsgChannel);
							return;
						}
					}
				});
			}
		} else {
			sendMessage("&cIncorrect arguments. /member <add,remove>", sender, discordMsgChannel);
		}

		return ResponseType.FAILED;
	}

}
