package net.introvertscove.survivalserver.discordbot;

import java.util.Optional;
import java.util.UUID;

import javax.security.auth.login.LoginException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import me.clip.placeholderapi.PlaceholderAPI;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.introvertscove.survivalserver.commandhandlers.MemberCommandHandler;
import net.introvertscove.survivalserver.commandhandlers.WhoIsCommandHandler;
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

		jda = JDABuilder.createDefault(botKey).enableIntents(GatewayIntent.GUILD_MEMBERS)
				.setMemberCachePolicy(MemberCachePolicy.ALL).addEventListeners(new DiscordBotManager()).build();

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

			if (msgContent.startsWith("//about")) {
				if (!authorized) {
					e.getChannel().sendMessage("Not Authorized.").queue();
					return;
				}

				e.getChannel().sendMessage("The Introvert's Cove survival server bot. Version: "
						+ IntrovertsPlugin.getInstance().getDescription().getVersion()).queue();
				return;
			} else if (msgContent.startsWith("//mcstatus")) {
				if (!authorized) {
					e.getChannel().sendMessage("Not Authorized.").queue();
					return;
				}

				int playersOnline = Bukkit.getOnlinePlayers().size();
				int playersMax = Bukkit.getServer().getMaxPlayers();

				String tps = null;
				String cpuProcess = null;

				try {
					tps = ChatColor.stripColor(PlaceholderAPI.setPlaceholders(
							Bukkit.getOfflinePlayer(UUID.fromString("28f9bb08-b33c-4a7d-b098-ebf271383966")),
							"%spark_tps%"));
					cpuProcess = ChatColor.stripColor(PlaceholderAPI.setPlaceholders(
							Bukkit.getOfflinePlayer(UUID.fromString("28f9bb08-b33c-4a7d-b098-ebf271383966")),
							"%spark_cpu_process%"));

					e.getChannel().sendMessage("```Survival Server Status:\nPlayers: " + playersOnline + "/"
							+ playersMax + ".\nTPS: " + tps + "\nCPU: " + cpuProcess + "```").queue();
					return;
				} catch (Exception exception) {
					e.getChannel()
							.sendMessage(
									"```Survival Server Status:\nPlayers: " + playersOnline + "/" + playersMax + ".```")
							.queue();
					return;
				}
			} else if (msgContent.startsWith("//help")) {
				if (!authorized) {
					e.getChannel().sendMessage("Not Authorized.").queue();
					return;
				}

				// TODO
			} else if (msgContent.startsWith("//whois")) {
				if (!authorized) {
					e.getChannel().sendMessage("Not Authorized.").queue();
					return;
				}	

				Optional<CommandSender> empty = Optional.empty();
				new WhoIsCommandHandler().handleCommand(cleanUpCommandArguments(msgContent, "//whois"), empty, Optional.of(e.getChannel()));
				return;
			} else if (msgContent.startsWith("//member")) {
				if (!authorized) {
					e.getChannel().sendMessage("Not Authorized.").queue();
					return;
				}	

				Optional<CommandSender> empty = Optional.empty();
				new MemberCommandHandler().handleCommand(cleanUpCommandArguments(msgContent, "//member"), empty, Optional.of(e.getChannel()));
				return;
			}
		}

	}
	
	/**
	 * Cleans up the message content and formats the data as if the command was processed by spigot.
	 * @param rawMessageContent
	 * @param commandWithPrefix
	 * @return
	 */
	public String[] cleanUpCommandArguments(String rawMessageContent, String commandWithPrefix) {
		String cleanedUpContent = rawMessageContent.replace(commandWithPrefix, " ").trim();

		String[] args = cleanedUpContent.split("\\s+");

		if (args.length == 1) {
			if (args[0].length() == 0) {
				// The first argument is empty.
				args = new String[0];
			}
		}
		return args;
	}
}
