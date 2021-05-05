package net.introvertscove.survivalserver.plugin.commands;

import java.util.ArrayList;
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
import net.introvertscove.survivalserver.plugin.utils.SpectatorAccountsOptions;

public class SpectatorAccountsCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!sender.hasPermission("introvert.spectatoraccounts")) {
			Messager.msgSender("&cSorry you aren't built different.", sender);
			return true;
		}

		if (args.length == 0) {
			Messager.msgSender(
					"&cUmm... You forgot the command arguments again? Here you go, I hope you have a good day fellow administrator.",
					sender);
			Messager.msgSender("&6/spectatoraccounts <add,remove,list,option>", sender);
			return true;
		}

		if (args.length > 1) {
			if (args[0].equalsIgnoreCase("add")) {
				if (args.length == 3) {
					String possibleMemberUuid = args[1];
					UUID memberUuid;

					try {
						memberUuid = UUID.fromString(possibleMemberUuid);
					} catch (IllegalArgumentException e) {
						Messager.msgSender(
								"&cSorry the member's UUID isn't formatted correctly. Did you paste it wrong?", sender);
						return true;
					}

					String possibleSpectatorUuid = args[2];
					UUID spectatorUuid;

					try {
						spectatorUuid = UUID.fromString(possibleSpectatorUuid);
					} catch (IllegalArgumentException e) {
						Messager.msgSender(
								"&cSorry the specator's UUID isn't formatted correctly. Did you paste it wrong?",
								sender);
						return true;
					}

					Optional<MemberDataBean> possibleMemberData = DatabaseManager.getMemberData(memberUuid);

					if (possibleMemberData.isPresent()) {
						// Already a member
						MemberDataBean memberData = possibleMemberData.get();

						memberData.getSpectatorAccountUuids().add(spectatorUuid);

						DatabaseManager.saveMemberDataToFile(memberData);

						Messager.msgSender("&aSucessfully added the spectator account " + spectatorUuid.toString()
								+ " to the member " + memberData.getMinecraftUuid().toString()
								+ "'s spectator accounts list.", sender);
						return true;
					} else {
						Messager.msgSender("&cSorry that member is not registered on the member list.", sender);
						return true;
					}

				} else {
					Messager.msgSender("&cBad command arguments. /spectatoraccounts add <memberUUID> <spectatorUUID>",
							sender);
					return true;
				}
			} else if (args[0].equalsIgnoreCase("remove")) {
				if (args.length == 2) {

					String possibleSpectatorUuid = args[1];
					UUID spectatorUuid;

					try {
						spectatorUuid = UUID.fromString(possibleSpectatorUuid);
					} catch (IllegalArgumentException e) {
						Messager.msgSender(
								"&cSorry the specator's UUID isn't formatted correctly. Did you paste it wrong?",
								sender);
						return true;
					}

					for (String uuidKey : DatabaseManager.getMemberDataFile().getKeys(false)) {
						Optional<MemberDataBean> possibleMemberData = DatabaseManager
								.getMemberData(UUID.fromString(uuidKey));
						if (possibleMemberData.isPresent()) {
							MemberDataBean memberData = possibleMemberData.get();

							if (memberData.getSpectatorAccountUuids().contains(spectatorUuid)) {
								memberData.getSpectatorAccountUuids().remove(spectatorUuid);
								
								DatabaseManager.saveMemberDataToFile(memberData);

								Messager.msgSender("&aRemoved spectator account " + spectatorUuid.toString()
										+ " from the member " + memberData.getMinecraftUuid().toString()
										+ "'s spectator accounts list.", sender);
								return true;
							}
						}
					}

					Messager.msgSender(
							"&cFailed to find any member with the specator account " + spectatorUuid.toString() + ".",
							sender);
					return true;
				} else {
					Messager.msgSender("&cBad command arguments. /spectatoraccounts remove <spectatorUUID>", sender);
					return true;
				}
			} else if (args[0].equalsIgnoreCase("list")) {
				if (args.length == 2) {
					String possibleMemberUuid = args[1];
					UUID memberUuid;

					try {
						memberUuid = UUID.fromString(possibleMemberUuid);
					} catch (IllegalArgumentException e) {
						Messager.msgSender(
								"&cSorry the member's UUID isn't formatted correctly. Did you paste it wrong?", sender);
						return true;
					}

					Optional<MemberDataBean> possibleMemberData = DatabaseManager.getMemberData(memberUuid);
					if (possibleMemberData.isPresent()) {
						MemberDataBean memberData = possibleMemberData.get();

						ArrayList<UUID> specs = memberData.getSpectatorAccountUuids();

						if (specs.size() == 0) {
							Messager.msgSender(
									"&6The member" + memberUuid.toString() + " doesn't have any spectator accounts.",
									sender);
							return true;
						} else {
							Messager.msgSender("&6Member " + memberUuid + "'s spectator accounts:", sender);
							Messager.msgSender("&6Formatted: Specator Account UUID - Spectator Account Username",
									sender);
							for (UUID uuid : specs) {
								String username = Bukkit.getOfflinePlayer(uuid).getName();
								if (username == null) {
									username = "Unknown Username";
								}
								Messager.msgSender("&6" + uuid.toString() + " - " + username, sender);
							}
							return true;
						}
					} else {
						Messager.msgSender("&cCouldn't find any mamber with the UUID " + memberUuid.toString() + ".",
								sender);
						return true;
					}
				} else {
					Messager.msgSender("&cBad command arguments. /spectatoraccounts list <memberUUID>", sender);
					return true;
				}
			} else if (args[0].equalsIgnoreCase("option")) {
				if (args.length > 1) {
					if (args[1].equalsIgnoreCase("list")) {
						Messager.msgSender("&6List of acceptable options names and their current values:", sender);

						for (String option : IntrovertsPlugin.getInstance().getConfig()
								.getConfigurationSection("spectator_accounts").getKeys(false)) {
							boolean optionValue = IntrovertsPlugin.getInstance().getConfig().getBoolean("spectator_accounts." + option);
							Messager.msgSender("&6" + option +  ": " + optionValue, sender);
						}

						return true;
					} else if (args[1].equalsIgnoreCase("set")) {
						if (args.length == 4) {
							String optionName = args[2];
							String value = args[3];
							boolean trueOrFalse;

							try {
								trueOrFalse = Boolean.valueOf(value);
							} catch (Exception e) {
								Messager.msgSender("&cSorry the value " + value + " is not 'true' or 'false'.", sender);
								return true;
							}

							if (optionName.equalsIgnoreCase("display_action_bar")) {
								SpectatorAccountsOptions.setDisplayActionBar(trueOrFalse);
								Messager.msgSender("&aSet value display_action_bar to " + trueOrFalse, sender);
								return true;
							} else if (optionName.equalsIgnoreCase("force_spectator_gamemode_on_join")) {
								SpectatorAccountsOptions.setForceSpectatorGamemodeOnJoin(trueOrFalse);
								Messager.msgSender("&aSet value force_spectator_gamemode_on_join to " + trueOrFalse, sender);
								return true;
							} else if (optionName.equalsIgnoreCase("disable_spectator_accounts")) {
								SpectatorAccountsOptions.setDisableSpectatorAccounts(trueOrFalse);
								Messager.msgSender("&aSet value disable_spectator_accounts to " + trueOrFalse, sender);
								return true;
							} else if (optionName.equalsIgnoreCase("non_members_are_spectator")) {
								SpectatorAccountsOptions.setNonMembersAreSpectators(trueOrFalse);
								Messager.msgSender("&aSet value non_members_are_spectator to " + trueOrFalse, sender);
								return true;
							} else if (optionName.equalsIgnoreCase("prevent_chat")) {
								SpectatorAccountsOptions.setPreventChat(trueOrFalse);
								Messager.msgSender("&aSet value prevent_chat to " + trueOrFalse, sender);
								return true;
							} else {
								Messager.msgSender("&cSorry the option name " + optionName
										+ " is not valid. Use '/spectatoraccounts option list' to list the acceptable option names.",
										sender);
								return true;
							}
						} else {
							Messager.msgSender(
									"&cBad command arguments. /spectatoraccounts option set <optionName> <true,false>",
									sender);
							return true;
						}
					} else {
						Messager.msgSender("&cBad command arguments. /spectatoraccounts option <list,set>", sender);
						return true;
					}
				} else {
					Messager.msgSender("&cBad command arguments. /spectatoraccounts option <list,set>", sender);
					return true;
				}
			} else {
				Messager.msgSender("&cBad command arguments. /spectatoraccounts <add,remove,list,option>", sender);
				return true;
			}
		} else {
			Messager.msgSender("&cBad command arguments. /spectatoraccounts <add,remove,list,option>", sender);
			return true;
		}
	}

}
