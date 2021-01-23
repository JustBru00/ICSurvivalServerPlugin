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

import net.introvertscove.survivalserver.beans.MemberDataBean;
import net.introvertscove.survivalserver.plugin.IntrovertsPlugin;
import net.introvertscove.survivalserver.plugin.database.DatabaseManager;
import net.introvertscove.survivalserver.plugin.utils.Messager;
import net.introvertscove.survivalserver.plugin.utils.SpectatorAccountsOptions;

public class PlayerLoginLogoutListener implements Listener {

	@EventHandler
	public void onPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
		final UUID playerUuid = e.getUniqueId();

		Optional<MemberDataBean> memberData = DatabaseManager.getMemberData(playerUuid);

		if (!memberData.isPresent()) {
			// NOT A MEMBER
			// CHECK SPECTATOR ACCOUNTS
			if (SpectatorAccountsOptions.doAllowNonMembersAsSpectator()
					|| (!SpectatorAccountsOptions.isSpectatorAccountsDisabled()
							&& DatabaseManager.isSpectatorAccount(playerUuid))) {
				// is a spectator

				if (SpectatorAccountsOptions.doForceSpectatorGamemodeOnJoin()) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(IntrovertsPlugin.getInstance(), new Runnable() {

						public void run() {
							// Set to spectator gamemode
							Bukkit.getPlayer(playerUuid).setGameMode(GameMode.SPECTATOR);
						}
					}, 5);
				}
				return;
			}

			e.setLoginResult(Result.KICK_WHITELIST);
			e.setKickMessage(Messager.color(
					"&cSorry your account is not on the member list for the Introvert's Cove.\n&cSee &fhttps://www.introvertscove.net/applications &cto apply to join the server.\n\n&cIf you believe this is in error please contact us @ contact@introvertcove.com."));
			return;
		}
	}

	@EventHandler
	public void onPlayerLogin(PlayerJoinEvent e) {
		final UUID playerId = e.getPlayer().getUniqueId();
		DatabaseManager.logPlayerLoginToSessionHistory(playerId);
		Optional<MemberDataBean> memberData = DatabaseManager.getMemberData(playerId);

		if (!memberData.isPresent() && e.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) {
			Messager.msgConsole("The member data for " + playerId + " couldn't be found. WHAT IS HAPPENING!!!");
			return;
		}

		memberData.get().setLastIpAddress(e.getPlayer().getAddress().getAddress().getHostAddress());

		DatabaseManager.saveMemberDataToFile(memberData.get());
	}

	@EventHandler
	public void onPlayerLogout(PlayerQuitEvent e) {
		final UUID playerId = e.getPlayer().getUniqueId();
		DatabaseManager.logPlayerLogoutToSessionHistory(e.getPlayer().getUniqueId());
		Optional<MemberDataBean> memberData = DatabaseManager.getMemberData(playerId);

		if (!memberData.isPresent() && e.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) {
			Messager.msgConsole("The member data for " + playerId + " couldn't be found. WHAT IS HAPPENING PART 2!!!");
			return;
		}

		memberData.get().getLimboStatus().setLastLogout(System.currentTimeMillis());

		DatabaseManager.saveMemberDataToFile(memberData.get());
	}

}
