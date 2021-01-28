package net.introvertscove.survivalserver.plugin.commands;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.introvertscove.survivalserver.beans.MemberDataBean;
import net.introvertscove.survivalserver.plugin.IntrovertsPlugin;
import net.introvertscove.survivalserver.plugin.database.DatabaseManager;
import net.introvertscove.survivalserver.plugin.utils.Messager;
import net.introvertscove.survivalserver.plugin.utils.UUIDFetcher;

public class NewMemberCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {

		if (command.getName().equalsIgnoreCase("member")) {

			if (!sender.hasPermission("introvert.member")) {
				Messager.msgSender("&cSorry you aren't built different.", sender);
				return true;
			}

			if (args.length == 0) {
				Messager.msgSender(
						"&cUmm... You forgot the command arguments again? Here you go, I hope you have a good day fellow administrator.",
						sender);
				Messager.msgSender("&6/member <add,remove>", sender);
				return true;
			}

			// /member add <MCName/UUID> <DiscordID>
			if (args.length > 1) {
				if (args[0].equalsIgnoreCase("add")) {
					// /member add <mcusername/uuid> <discordid>
					if (args.length == 3) {
						final String mcNameOrUuid = args[1];
						
						
						long discordId = -1;
						try {
							discordId = Long.parseLong(args[2]);
						} catch (NumberFormatException e) {
							Messager.msgSender(
									"&cSorry the provided discord id cannot be parsed as the Long datatype. Are you sure it is formatted correctly?",
									sender);
							return true;
						}
						
						final long actualDiscordId = discordId;
						
						if (mcNameOrUuid.length() <= 16) {
							// MC Username
							Messager.msgSender("&6Attempting to get that player's UUID. This might take a minute...", sender);
							
							// ASYNC CALL TO UUIDFETCHER
							Bukkit.getScheduler().scheduleAsyncDelayedTask(IntrovertsPlugin.getInstance(), new Runnable() {
								
								public void run() {
									final Optional<UUID> possibleUuid = UUIDFetcher.getUuid(mcNameOrUuid);
									
									if (!possibleUuid.isPresent()) {
										Messager.msgSenderSync("&cFailed to get the player " + mcNameOrUuid + "'s UUID. Are you sure the username is correct?", sender);
										return;
									}
									
									Bukkit.getScheduler().scheduleSyncDelayedTask(IntrovertsPlugin.getInstance(), new Runnable() {
										
										public void run() {
											UUID playerUuid = possibleUuid.get();
											Optional<MemberDataBean> possibleMemberData = DatabaseManager.getMemberData(playerUuid);

											if (possibleMemberData.isPresent()) {
												// Already a member
												Messager.msgSender(
														"&cSorry that minecraft UUID is already registered as a member. Use /whois "
																+ mcNameOrUuid + " for more information.",
														sender);
												return;
											}
											
											// New member
											MemberDataBean memberData = DatabaseManager.getDefaultMemberDataBean(playerUuid, actualDiscordId);

											DatabaseManager.saveMemberDataToFile(memberData);
											Messager.msgSender("&aSuccessfully saved new member " + memberData.getMinecraftUuid() + ".",
													sender);
											return;
										}
									});																
								}
							});
						
							
						} else {
							// Probably UUID
							UUID mcUuid;
							
							try {
								mcUuid = UUID.fromString(mcNameOrUuid);
							} catch (IllegalArgumentException e) {
								Messager.msgSender(
										"&cSorry the new member's UUID isn't formatted correctly. Did you paste it wrong?",
										sender);
								return true;
							}
							
							Optional<MemberDataBean> possibleMemberData = DatabaseManager.getMemberData(mcUuid);

							if (possibleMemberData.isPresent()) {
								// Already a member
								Messager.msgSender(
										"&cSorry that minecraft UUID is already registered as a member. Use /whois "
												+ mcNameOrUuid + " for more information.",
										sender);
								return true;
							}
							
							// New member
							MemberDataBean memberData = DatabaseManager.getDefaultMemberDataBean(mcUuid, discordId);

							DatabaseManager.saveMemberDataToFile(memberData);
							Messager.msgSender("&aSuccessfully saved new member " + memberData.getMinecraftUuid() + ".",
									sender);
							return true;
						}
						
						return true;						
					} else {
						Messager.msgSender("&cSorry incorrect arugments. /member add <mcUsername,UUID> <discordId>", sender);
						return true;
					}
				} else if (args[0].equalsIgnoreCase("remove")) {
					// /member remove <memberusername/UUID>
					//TODO
				} else {
					Messager.msgSender("&cSorry incorrect arugments. /member <add,remove>", sender);
					return true;
				}
			}
		}

		return false;
	}
	

}
