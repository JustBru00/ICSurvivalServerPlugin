package net.introvertscove.survivalserver.commandhandlers;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.command.CommandSender;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.introvertscove.survivalserver.beans.LimboExemptionStatusBean;
import net.introvertscove.survivalserver.beans.MemberDataBean;
import net.introvertscove.survivalserver.plugin.database.DatabaseManager;
import net.introvertscove.survivalserver.plugin.utils.TimeFormatter;
import net.introvertscove.survivalserver.plugin.utils.UUIDFetcher;

public class LimboExemptionCommandHandler extends CommandHandler {

	@Override
	public ResponseType handleCommand(String[] args, final Optional<CommandSender> sender,
			final Optional<MessageChannel> discordMsgChannel) {

		/**
		 * Limbo exemption Command Design: - /limboexemption
		 * <add,remove,details,listall> - /limboexemption add <memberUsernameOrUuid>
		 * <Reason> - /limboexemption remove <memberUsernameOrUuid> - /limboexemption
		 * details <memberUsernameOrUuid> - /limboexemption listall
		 * 
		 **/

		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("add")) {
				if (args.length >= 3) {
					StringBuilder reasonBuilder = new StringBuilder();

					for (int i = 2; i < args.length; i++) {
						reasonBuilder.append(args[i] + " ");
					}

					final String reason = reasonBuilder.toString().trim();

					sendMessage("&6Fetching that member's profile. This might take a minute...", sender,
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

							// Check if member
							Optional<MemberDataBean> possibleMemberData = DatabaseManager.getMemberData(uuid.get());

							if (!possibleMemberData.isPresent()) {
								sendMessage(
										"&cCouldn't load the member data for that player. Are you sure they are a member?",
										sender, discordMsgChannel);
								return;
							}

							// Create new Exemption
							LimboExemptionStatusBean exemption;
							if (discordMsgChannel.isPresent()) {
								exemption = new LimboExemptionStatusBean(true, "DiscordBot", System.currentTimeMillis(),
										-1, reason);
							} else if (sender.isPresent()) {
								if (sender.get().getName() != null) {
									exemption = new LimboExemptionStatusBean(true, sender.get().getName(),
											System.currentTimeMillis(), -1, reason);
								} else {
									exemption = new LimboExemptionStatusBean(true, "NULL COMMAND_SENDER",
											System.currentTimeMillis(), -1, reason);
								}
							} else {
								sendMessage(
										"&CWHOA! How did you even get to this point in the code? Uhh... You better mention this to JustBru00.",
										sender, discordMsgChannel);
								return;
							}

							// Save to config file.
							MemberDataBean memberData = possibleMemberData.get();
							memberData.setLimboExcemptionStatus(exemption);

							DatabaseManager.saveMemberDataToFile(memberData);
							sendMessage(
									"&aAdded exemption successfully. View this limbo exemption with /limboexemption details <memberUsernameOrUuid>",
									sender, discordMsgChannel);
							return;
						}
					});

					return ResponseType.NEUTRAL;
				} else {
					sendMessage("&cIncorrect arguments. /limboexemption add <memberUsernameOrUuid> <reason>", sender,
							discordMsgChannel);
					return ResponseType.NEUTRAL;
				}
			} else if (args[0].equalsIgnoreCase("remove")) {
				if (args.length >= 2) {
					sendMessage("&6Fetching that member's profile. This might take a minute...", sender,
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

							// Check if member
							Optional<MemberDataBean> possibleMemberData = DatabaseManager.getMemberData(uuid.get());
							if (!possibleMemberData.isPresent()) {
								sendMessage(
										"&cCouldn't load the member data for that player. Are you sure they are a member?",
										sender, discordMsgChannel);
								return;
							}

							// Set exemption to false
							MemberDataBean memberData = possibleMemberData.get();
							memberData.getLimboExcemptionStatus().setExemptionActive(false);
												
							// Save to disk
							DatabaseManager.saveMemberDataToFile(memberData);
							
							sendMessage("&aSuccessfully removed that member's limbo exemption.", sender, discordMsgChannel);
							return;
						}
					});
					return ResponseType.NEUTRAL;
				} else {
					sendMessage("&cIncorrect arguments. /limboexemption remove <memberUsernameOrUuid>", sender,
							discordMsgChannel);
					return ResponseType.NEUTRAL;
				}
			} else if (args[0].equalsIgnoreCase("details")) {
				if (args.length >= 2) {
					sendMessage("&6Fetching that member's profile. This might take a minute...", sender,
							discordMsgChannel);
					UUIDFetcher.getUuidAsyncAndRunCallback(args[1], new EpicCallback() {
						
						public void runSync(Optional<UUID> uuid, Optional<String> lastName, String originalMcUsernameOrUuid) {
							if (!uuid.isPresent() || !lastName.isPresent()) {
								sendMessage(
										"&cFailed to process the player's UUID and username from the provided information. Are you sure you spelled everything correctly?",
										sender, discordMsgChannel);
								return;
							}
							
							// Check if member
							Optional<MemberDataBean> possibleMemberData = DatabaseManager.getMemberData(uuid.get());
							if (!possibleMemberData.isPresent()) {
								sendMessage(
										"&cCouldn't load the member data for that player. Are you sure they are a member?",
										sender, discordMsgChannel);
								return;
							}					
							
							// Read limbo exemption details from file.
							MemberDataBean memberData = possibleMemberData.get();
							LimboExemptionStatusBean exemption = memberData.getLimboExcemptionStatus();
							
							ArrayList<String> listOfMessages = new ArrayList<String>();
							listOfMessages.add(String.format("Member %s limbo exemption details:", lastName.get()));
							listOfMessages.add(String.format("Limbo exemption active? %s", String.valueOf(exemption.isExemptionActive())));
							listOfMessages.add(String.format("Exemption added by %s", exemption.getAdminWhoAddedExemption()));
							listOfMessages.add(String.format("Exemption added at %s UTC", TimeFormatter.getTimeStampFrom(exemption.getExemptionAddedStartingAt())));
							listOfMessages.add(String.format("Exemption reason: %s", exemption.getExemptionReason()));
							
							// Display to user.
							if (sender.isPresent() && !discordMsgChannel.isPresent()) {
								// Minecraft
								for (String msg : listOfMessages) {
									sendMessage("&6" + msg, sender, discordMsgChannel);
								}
							} else if (!sender.isPresent() && discordMsgChannel.isPresent()) {
								// Discord
								StringBuilder builder = new StringBuilder();
								builder.append("```");
								for (String msg : listOfMessages) {
									builder.append(msg + "\n");
								}
								builder.append("```");
								sendMessage(builder.toString(), sender, discordMsgChannel);
							}							
							return;
						}
					});					
				} else {
					sendMessage("&cIncorrect arguments. /limboexemption details <memberUsernameOrUuid>", sender,
							discordMsgChannel);
					return ResponseType.NEUTRAL;
				}
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

						messageLine = messageLine.replace("{ADDED_AT}",
								TimeFormatter.getTimeStampFrom(exemptionStatus.getExemptionAddedStartingAt()) + " UTC");

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
				sendMessage("&cIncorrect arguments. /limboexemption <add,remove,details,listall>", sender,
						discordMsgChannel);
			}
		} else {
			sendMessage("&cIncorrect arguments. /limboexemption <add,remove,details,listall>", sender,
					discordMsgChannel);
		}

		return ResponseType.FAILED;
	}

}
