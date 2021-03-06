package net.introvertscove.survivalserver.plugin;

import java.util.logging.Logger;

import javax.security.auth.login.LoginException;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import net.introvertscove.survivalserver.discordbot.DiscordBotManager;
import net.introvertscove.survivalserver.limbo.LimboManager;
import net.introvertscove.survivalserver.plugin.commands.IntrovertsCoveCommand;
import net.introvertscove.survivalserver.plugin.commands.LimboExemptionCommand;
import net.introvertscove.survivalserver.plugin.commands.MemberCommand;
import net.introvertscove.survivalserver.plugin.commands.SpectatorAccountsCommand;
import net.introvertscove.survivalserver.plugin.commands.WhoIsCommand;
import net.introvertscove.survivalserver.plugin.database.DatabaseManager;
import net.introvertscove.survivalserver.plugin.listeners.ChatListener;
import net.introvertscove.survivalserver.plugin.listeners.PlayerLoginLogoutListener;
import net.introvertscove.survivalserver.plugin.utils.Messager;
import net.introvertscove.survivalserver.plugin.utils.SpectatorAccountsOptions;

public class IntrovertsPlugin extends JavaPlugin {

	private static IntrovertsPlugin instance;
	public static ConsoleCommandSender console = Bukkit.getConsoleSender();
	public static Logger logger = Bukkit.getLogger();
	public static String prefix = Messager.color("&8[&bIntroverts&fCove&8] &6");
	
	private static int spectatorActionBarTaskId = -1;
	private static int limboTaskId = -1;

	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelTask(spectatorActionBarTaskId);
		Bukkit.getScheduler().cancelTask(limboTaskId);
		
		instance = null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {
		instance = this;
		saveDefaultConfig();
		DatabaseManager.init();
		
		getCommand("member").setExecutor(new MemberCommand());
		getCommand("spectatoraccounts").setExecutor(new SpectatorAccountsCommand());
		getCommand("whois").setExecutor(new WhoIsCommand());
		getCommand("introvertscove").setExecutor(new IntrovertsCoveCommand());
		getCommand("limboexemption").setExecutor(new LimboExemptionCommand());
		
		PluginManager manager = Bukkit.getPluginManager();
		
		manager.registerEvents(new PlayerLoginLogoutListener(), instance);
		manager.registerEvents(new ChatListener(), instance);
		
		spectatorActionBarTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
			
			public void run() {
				if (SpectatorAccountsOptions.doDisplayActionBar()) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						if (DatabaseManager.isSpectatorAccount(p.getUniqueId())) {
							Messager.sendActionBar("&6You are a spectator account. You can only view chunks loaded by server members.", p);
						}
					}
				}				
			}
		}, 10*20, 2*20);
		
		limboTaskId = Bukkit.getScheduler().scheduleAsyncRepeatingTask(instance, new Runnable() {
			
			@Override
			public void run() {
				LimboManager.runEveryThirtyMinutesAsync();
				
			}
		}, 20*10, 20*30);
		
		
		try {
			DiscordBotManager.startBot();
		} catch (LoginException e) {
			e.printStackTrace();
		}
				
	}

	public static IntrovertsPlugin getInstance() {
		return instance;
	}
	
}
