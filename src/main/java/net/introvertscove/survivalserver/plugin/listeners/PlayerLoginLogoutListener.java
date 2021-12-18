package net.introvertscove.survivalserver.plugin.listeners;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.introvertscove.survivalserver.beans.LimboStatusBean;
import net.introvertscove.survivalserver.beans.MemberDataBean;
import net.introvertscove.survivalserver.discordbot.DiscordBotManager;
import net.introvertscove.survivalserver.plugin.IntrovertsPlugin;
import net.introvertscove.survivalserver.plugin.database.DatabaseManager;
import net.introvertscove.survivalserver.plugin.utils.Messager;
import net.introvertscove.survivalserver.plugin.utils.SpectatorAccountsOptions;
import net.introvertscove.survivalserver.plugin.utils.UUIDFetcher;

public class PlayerLoginLogoutListener implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
		final UUID playerUuid = e.getUniqueId();
		if (playerUuid != null) {
			UUIDFetcher.updateCachedUuid(playerUuid, e.getName());
			
			Optional<MemberDataBean> memberData = DatabaseManager.getMemberData(playerUuid);

			if (!memberData.isPresent()) {
				// NOT A MEMBER
				// CHECK SPECTATOR ACCOUNTS
				if (SpectatorAccountsOptions.doAllowNonMembersAsSpectator()
						|| (!SpectatorAccountsOptions.isSpectatorAccountsDisabled()
								&& DatabaseManager.isSpectatorAccount(playerUuid))) {
					// is a spectator
					return;
				}

				e.setLoginResult(Result.KICK_WHITELIST);
				e.setKickMessage(Messager.color(
						"&cSorry your account is not on the member list for the Introvert's Cove.\n&cSee &fhttps://www.introvertscove.net/applications &cto apply to join the server.\n\n&cIf you believe this is in error please contact us at contact@introvertcove.com."));
				return;
			} else {
				// IS A MEMBER
				// Check Limbo Status.
				LimboStatusBean limboStatus = memberData.get().getLimboStatus();
				if (limboStatus.isCurrentlyInLimbo() && !memberData.get().getLimboExcemptionStatus().isExemptionActive()) {
					Bukkit.getScheduler().scheduleAsyncDelayedTask(IntrovertsPlugin.getInstance(), new Runnable() {
						
						@Override
						public void run() {
							DiscordBotManager.sendMessageToAdminAnnouncementChannel("Member " + e.getName() + " just attempted to join the server, but they were blocked because they are in limbo.");					
						}
					});
					e.setLoginResult(Result.KICK_WHITELIST);
					e.setKickMessage(Messager.color("&cYou are currently in limbo.\nYou must talk to a server administrator to get out of limbo."));
					return;
				} else if (limboStatus.isRetiredMessageSentToPlayer() && !memberData.get().getLimboExcemptionStatus().isExemptionActive()) {
					Bukkit.getScheduler().scheduleAsyncDelayedTask(IntrovertsPlugin.getInstance(), new Runnable() {
						
						@Override
						public void run() {
							DiscordBotManager.sendMessageToAdminAnnouncementChannel("Member " + e.getName() + " just attempted to join the server, but they were blocked because they are retired");					
						}
					});
					e.setLoginResult(Result.KICK_WHITELIST);
					e.setKickMessage(Messager.color("&cYou have been automatically retired because of inactivity.\nYou will need to go though the application process again as if you were a new player."));
					return;
				}
			}

		}

	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerLogin(PlayerJoinEvent e) {
		final UUID playerId = e.getPlayer().getUniqueId();
		DatabaseManager.logPlayerLoginToSessionHistory(playerId);
		Optional<MemberDataBean> memberData = DatabaseManager.getMemberData(playerId);

		if (!memberData.isPresent()) {
			if (DatabaseManager.isSpectatorAccount(playerId)
					&& SpectatorAccountsOptions.doForceSpectatorGamemodeOnJoin()) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(IntrovertsPlugin.getInstance(), new Runnable() {

					public void run() {
						// Set to spectator gamemode
						try {
							Bukkit.getPlayer(playerId).setGameMode(GameMode.SPECTATOR);
						} catch (NullPointerException e) {
							Messager.msgConsole(
									"[PlayerLoginLogoutListener] Spectator player logged out before I could set them to spectator gamemode.");
						}
					}
				}, 5);
			}
			return;
		}

		memberData.get().setLastIpAddress(e.getPlayer().getAddress().getAddress().getHostAddress());
		
		// Reset Limbo Stuff Below
		
		if (memberData.get().getLimboStatus().isNagMessageSuccessful()) {
			memberData.get().getLimboStatus().setNagMessageSuccessful(false);
			memberData.get().getLimboStatus().setLastLogout(System.currentTimeMillis());
		} 
		
		if (memberData.get().getLimboStatus().isCurrentlyInLimbo()) {
			memberData.get().getLimboStatus().setCurrentlyInLimbo(false);
		}
		
		if (memberData.get().getLimboStatus().isRetiredDangerMessageSent()) {
			memberData.get().getLimboStatus().setRetiredDangerMessageSent(false);
		}
		
		if (memberData.get().getLimboStatus().isRetiredMessageSentToPlayer()) {
			memberData.get().getLimboStatus().setRetiredMessageSentToPlayer(false);
		}

		DatabaseManager.saveMemberDataToFile(memberData.get());
	}

	@EventHandler
	public void onPlayerLogout(PlayerQuitEvent e) {
		final UUID playerId = e.getPlayer().getUniqueId();
		DatabaseManager.logPlayerLogoutToSessionHistory(e.getPlayer().getUniqueId());
		Optional<MemberDataBean> memberData = DatabaseManager.getMemberData(playerId);

		if (!memberData.isPresent()) {
			return;
		}

		memberData.get().getLimboStatus().setLastLogout(System.currentTimeMillis());

		DatabaseManager.saveMemberDataToFile(memberData.get());
	}

}
