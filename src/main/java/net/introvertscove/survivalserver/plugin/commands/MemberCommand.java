package net.introvertscove.survivalserver.plugin.commands;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.introvertscove.survivalserver.beans.MemberDataBean;
import net.introvertscove.survivalserver.plugin.database.DatabaseManager;
import net.introvertscove.survivalserver.plugin.utils.Messager;

public class MemberCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

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
						String mcNameOrUuid = args[1];
						UUID mcUuid;

						// Probably UUID
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

						long discordId = -1;
						try {
							discordId = Long.parseLong(args[2]);
						} catch (NumberFormatException e) {
							Messager.msgSender(
									"&cSorry the provided discord id cannot be parsed as the Long datatype. Are you sure it is formatted correctly?",
									sender);
							return true;
						}

						// New member
						MemberDataBean memberData = DatabaseManager.getDefaultMemberDataBean(mcUuid, discordId);

						DatabaseManager.saveMemberDataToFile(memberData);
						Messager.msgSender("&aSuccessfully saved new member " + memberData.getMinecraftUuid() + ".",
								sender);
						return true;
					} else {
						Messager.msgSender("&cSorry incorrect arguments. /member add <mcUUID> <DiscordID>",
								sender);
						return true;
					}
				} else if (args[0].equalsIgnoreCase("remove")) {
					// /member remove <memberusername/UUID>
					if (args.length == 2) {
						String mcNameOrUuid = args[1];
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
							DatabaseManager.getMemberData().set(mcUuid.toString(), "Removed Member");
							
							if (Bukkit.getOfflinePlayer(mcUuid).isOnline()) {
								Bukkit.getPlayer(mcUuid).kickPlayer(Messager.color("&cYou were just removed from the server member list. See the discord server for more information."));
							}						
							
							Messager.msgSender("&aRemoved the member " + mcUuid + ".", sender);
							return true;
						} else {
							Messager.msgSender("&cSorry I can't remove a member that doesn't exist. It probably creates a black hole or something...", sender);
							return true;
						}
					} else {
						Messager.msgSender("&cSorry incorrect arguments. /member remove <mcUUID>",
								sender);
						return true;
					}					
				} else {
					Messager.msgSender("&cSorry incorrect arguments. /member <add,remove>", sender);
					return true;
				}

			} else {
				Messager.msgSender("&cSorry incorrect arguments. /member <add,remove>", sender);
				return true;
			}

		}

		return false;
	}

}
