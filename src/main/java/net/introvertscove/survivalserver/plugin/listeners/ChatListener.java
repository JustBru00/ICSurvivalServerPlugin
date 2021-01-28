package net.introvertscove.survivalserver.plugin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import net.introvertscove.survivalserver.plugin.database.DatabaseManager;
import net.introvertscove.survivalserver.plugin.utils.Messager;
import net.introvertscove.survivalserver.plugin.utils.SpectatorAccountsOptions;

public class ChatListener implements Listener {

	@EventHandler
	public void onChatEvent(AsyncPlayerChatEvent e) {
		if (DatabaseManager.isSpectatorAccount(e.getPlayer().getUniqueId())) {
			if (SpectatorAccountsOptions.doPreventChat()) {
				e.setCancelled(true);
				Messager.msgPlayer("&cSorry, chat from spectator accounts is disabled.", e.getPlayer());
			}
		}
	}
	
}
