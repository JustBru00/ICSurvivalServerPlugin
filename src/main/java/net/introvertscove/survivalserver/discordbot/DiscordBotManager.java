package net.introvertscove.survivalserver.discordbot;

import java.util.UUID;

import javax.security.auth.login.LoginException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import me.clip.placeholderapi.PlaceholderAPI;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.introvertscove.survivalserver.plugin.IntrovertsPlugin;
import net.md_5.bungee.api.ChatColor;

/**
 * This class handles actual bot operation.
 * 
 * @author Justin Brubaker
 *
 */
public class DiscordBotManager extends ListenerAdapter {

	private static JDA jda;

	public static void startBot() throws LoginException {
		String botKey = IntrovertsPlugin.getInstance().getConfig().getString("discord.bot_api_key");
		
		jda = JDABuilder.createDefault(botKey)
				.enableIntents(GatewayIntent.GUILD_MEMBERS)
				.setMemberCachePolicy(MemberCachePolicy.ALL)
				.addEventListeners(new DiscordBotManager()).build();
		
		
		try {
			jda.awaitReady();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static JDA getJda() {
		return jda;
	}

	public static void stopBot() {
		jda.shutdownNow();
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		
		if (e.getAuthor().isBot()) {
			return;
		}

		Message msg = e.getMessage();
		String msgContent = msg.getContentDisplay();
		
		FileConfiguration configYml = IntrovertsPlugin.getInstance().getConfig();

		// COMMANDS
		
		
		
		if (msgContent.startsWith("//")) {
			boolean authorized = false;
			
			for (long id : configYml.getLongList("discord.admin_user_ids")) {
				if (e.getAuthor().getIdLong() == id) {					
					authorized = true;
					break;
				}
			}
			
			if (!authorized) {
				e.getChannel().sendMessage("Not Authorized.").queue();
				return;
			}
			
			if (msgContent.startsWith("//about")) {
				e.getChannel().sendMessage("The Introvert's Cove survival server bot. Version: " + IntrovertsPlugin.getInstance().getDescription().getVersion()).queue();				
				return;
			} else if (msgContent.startsWith("//mcstatus")) {
				int playersOnline = Bukkit.getOnlinePlayers().size();
				int playersMax = Bukkit.getServer().getMaxPlayers();
				
				String tps = null;
				String cpuProcess = null;
				
				try {
					tps = ChatColor.stripColor(PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(UUID.fromString("28f9bb08-b33c-4a7d-b098-ebf271383966")), "%spark_tps%"));
					cpuProcess = ChatColor.stripColor(PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(UUID.fromString("28f9bb08-b33c-4a7d-b098-ebf271383966")), "%spark_cpu_process%"));
					
					e.getChannel().sendMessage("```Survival Server Status:\nPlayers: " + playersOnline + "/" + playersMax + ".\nTPS: " + tps + "\nCPU: " + cpuProcess + "```").queue();
					return;
				} catch (Exception exception) {
					e.getChannel().sendMessage("```Survival Server Status:\nPlayers: " + playersOnline + "/" + playersMax + ".```").queue();
					return;
				}
			} else if (msgContent.startsWith("//help")) {
				// TODO
			}
		}
		
		
/**
		if (msgContent.startsWith("++")) {
			// Separate text by spaces
			String[] args = msgContent.split("\\s+");
			
			if (args.length >= 1) {
				System.out.println(msgContent.toString());
				System.out.println("Args length is okay");				
				if (args[0].equalsIgnoreCase("++link")) {
					if (args.length == 2) {
						String websiteUserName = args[1].toLowerCase();
						String discordName = null;
						try {
							discordName = WebsiteManager.getDiscordIDfromProfile(websiteUserName);
							System.out.println("DiscordTag read from " + websiteUserName + " is " + discordName);
							if (discordName == null) {
								e.getChannel().sendMessage("Failed to get discord username from website profile. Have you added it to your user profile page? Try ++link again in 15 minutes.").queue();
								return;
							}
						} catch (IOException e1) {
							e1.printStackTrace();
							e.getChannel().sendMessage("Failed to get discord username from website profile. Have you added it to your user profile page? Exception written to log.").queue();
							return;
						}
						
						// Have the discord name from website now.
						if (!e.getAuthor().getAsTag().equals(discordName)) {
							// They don't match
							e.getChannel().sendMessage("The profile for '" + websiteUserName + "' lists '" + discordName + "' as their discord tag." +
							" Soooo, either you need to update your profile or you are trying to link to the wrong website profile.").queue();
							return;
						}
						
						// Discord Tag from web matches actual discord tag.
						
						// Check if this user is linked already.
						UserData userData = DatabaseManager.getUserByWebsiteUsername(websiteUserName.toLowerCase());
						if (userData != null) {
							// User is already linked
							e.getChannel().sendMessage("The website account '" + userData.getWebUserName() + 
									"' is already linked to the discord tag '" + userData.getDiscordUserName() + "'.").queue();
							return;
						}
						
						 // Not linked already - time to link
						UserData newLinkedUser = new UserData(e.getAuthor().getAsTag(), e.getAuthor().getIdLong(), websiteUserName.toLowerCase(), Instant.now().toEpochMilli());
						try {
							DatabaseManager.saveUsertoFile(newLinkedUser);
						} catch (IOException e1) {
							e1.printStackTrace();
							e.getChannel().sendMessage("Failed to save new linked accounts to database. If this error continues please contact an administrator.").queue();
							return;
						}
						e.getChannel().sendMessage("Linked your discord account '" + discordName + "' to the website profile '" + websiteUserName + "'." + 
						" Your rank should sync from your profile within 30 minutes.").queue();
						BotMain.updateRanksNow();	
						return;
					} else {
						e.getChannel().sendMessage("Wrong arguments. ++link <websiteUserName>").queue();
						return;
					}
				} else if (args[0].equalsIgnoreCase("++about")) {
					e.getChannel()
							.sendMessage("709fics.com Discord Bot " + BotMain.VERSION + " created by JustBru00.").queue();
					return;
				} else if (args[0].equalsIgnoreCase("++help")) {
					System.out.println("++Help called");
					e.getChannel().sendMessage("Commands: \n"
							+ "++link <websiteUserName> - Attempts to link your website rank to your discord account. Make sure you have your discord Name#IDNum listed on your profile.\n"
							+ "++about - About this bot.\n" + "++help - Displays this help page.").queue();
					return;
				} else if (args[0].equalsIgnoreCase("++forcelink")) {
					if (e.getAuthor().getIdLong() == 218803433220734976L || e.getAuthor().getIdLong() == 185560146754273291L) {
						// Allow passage.
					} else {
						e.getChannel().sendMessage("You are not a bot administrator.").queue();
						return;
					}
					
					try {
						if (args[1] == null || args[2] == null) {
							e.getChannel().sendMessage("Incorrect arguments. ++forcelink <webUserNameExact> <DiscordIDNumberExact>").queue();
							return;
						}
					} catch (ArrayIndexOutOfBoundsException e6) {
						e.getChannel().sendMessage("Incorrect arguments. ArrayOutOfBounds ++forcelink <webUserNameExact> <DiscordIDNumberExact>").queue();
						return;
					}
					
					String websiteUserName = args[1];
					String discordId = args[2];
					
					// Check if this user is linked already.
					UserData userData = DatabaseManager.getUserByWebsiteUsername(websiteUserName);
					if (userData != null) {
						// User is already linked
						e.getChannel().sendMessage("The website account '" + userData.getWebUserName() + 
								"' is already linked to the discord tag '" + userData.getDiscordUserName() + "'.").queue();
						return;
					}
					User discordUser = null;
					try {
						discordUser = jda.getUserById(discordId);
					} catch (NumberFormatException e3) {
						e3.printStackTrace();
						e.getChannel().sendMessage("NumberFormatException. Can't convert discord id " + discordId + " to long.").queue();
						return;
					} 
					
					if (discordUser == null) {
						e.getChannel().sendMessage("So, that discord user doesn't appear to exist. That might be a problem. Maybe check the provided discord ID number?").queue();
						return;
					}
					
					 // Not linked already - time to link
					UserData newLinkedUser = new UserData(discordUser.getAsTag(), discordUser.getIdLong(), websiteUserName, Instant.now().toEpochMilli());
					try {
						DatabaseManager.saveUsertoFile(newLinkedUser);
					} catch (IOException e1) {
						e1.printStackTrace();
						e.getChannel().sendMessage("Failed to save new linked account to database. If this error continues please contact an administrator.").queue();
						return;
					}
					e.getChannel().sendMessage("Linked discord account '" + discordUser.getAsTag() + "' to the website profile '" + websiteUserName + "'." + 
					" Rank should sync from profile within 30 minutes.").queue();
					BotMain.updateRanksNow();
					return;
				}
			} else {
				// No args
				e.getChannel().sendMessage("Uhhh... Maybe you need a little help? ++help").queue();
			}
			**/

		}
	}

