package net.introvertscove.survivalserver.plugin.utils;

import net.introvertscove.survivalserver.plugin.IntrovertsPlugin;

public class SpectatorAccountsOptions {

	public static boolean doDisplayActionBar() {
		return IntrovertsPlugin.getInstance().getConfig().getBoolean("spectator_accounts.display_action_bar");
	}

	public static boolean doForceSpectatorGamemodeOnJoin() {
		return IntrovertsPlugin.getInstance().getConfig()
				.getBoolean("spectator_accounts.force_spectator_gamemode_on_join");
	}

	public static boolean isSpectatorAccountsDisabled() {
		return IntrovertsPlugin.getInstance().getConfig().getBoolean("spectator_accounts.disable_spectator_accounts");
	}

	public static boolean doAllowNonMembersAsSpectator() {
		return IntrovertsPlugin.getInstance().getConfig().getBoolean("spectator_accounts.non_members_are_spectator");
	}
	
	public static boolean doPreventChat() {
		return IntrovertsPlugin.getInstance().getConfig().getBoolean("spectator_accounts.prevent_chat");
	}

	public static void setDisplayActionBar(boolean value) {
		IntrovertsPlugin.getInstance().getConfig().set("spectator_accounts.display_action_bar", value);
		IntrovertsPlugin.getInstance().saveConfig();
	}

	public static void setForceSpectatorGamemodeOnJoin(boolean value) {
		IntrovertsPlugin.getInstance().getConfig().set("spectator_accounts.force_spectator_gamemode_on_join", value);
		IntrovertsPlugin.getInstance().saveConfig();
	}

	public static void setDisableSpectatorAccounts(boolean value) {
		IntrovertsPlugin.getInstance().getConfig().set("spectator_accounts.disable_spectator_accounts", value);
		IntrovertsPlugin.getInstance().saveConfig();
	}

	public static void setNonMembersAreSpectators(boolean value) {
		IntrovertsPlugin.getInstance().getConfig().set("spectator_accounts.non_members_are_spectator", value);
		IntrovertsPlugin.getInstance().saveConfig();
	}
	
	public static void setPreventChat(boolean value) {
		IntrovertsPlugin.getInstance().getConfig().set("spectator_accounts.prevent_chat", value);
		IntrovertsPlugin.getInstance().saveConfig();
	}
	
	

}
