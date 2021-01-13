package net.introvertscove.survivalserver.plugin.database;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;

import net.introvertscove.survivalserver.beans.LimboExemptionStatusBean;
import net.introvertscove.survivalserver.beans.LimboStatusBean;
import net.introvertscove.survivalserver.beans.MemberDataBean;
import net.introvertscove.survivalserver.plugin.IntrovertsPlugin;
import net.introvertscove.survivalserver.plugin.utils.PluginFile;

public class DatabaseManager {

	private static PluginFile memberData = null;
	private static PluginFile sessionHistory = null;
	
	public static void init() {
		memberData = new PluginFile(IntrovertsPlugin.getInstance(), "member_data.yml", "/resources/member_data.yml");
		sessionHistory = new PluginFile(IntrovertsPlugin.getInstance(), "session_history.yml", "/resources/session_history.yml");		
	}	
	
	private static PluginFile getMemberData() {
		return memberData;
	}
	
	private static PluginFile getSessionHistory() {
		return sessionHistory;
	}
	
	private static void saveMemberData() {
		memberData.save();
	}
	
	private static void saveSessionHistory() {
		sessionHistory.save();
	}
	
	private static void loadMemberData() {
		memberData.reload();
	}
	
	private static void loadSessionHistory() {
		sessionHistory.reload();
	}
	
	public static Optional<MemberDataBean> getMemberData(UUID memberUuid) {
		ConfigurationSection memberSection = memberData.getConfigurationSection(memberUuid.toString());
		
		if (memberSection == null) {
			return Optional.empty();
		}
		
		long discordId = memberSection.getLong("discord_id");
		
		ArrayList<UUID> spectatorAccounts = new ArrayList<UUID>();
		for (String line : memberSection.getStringList("spectator_accounts")) {
			spectatorAccounts.add(UUID.fromString(line));
		}
		
		String lastIp = memberSection.getString("last_ip");
		
		// limbo.exemption.X
		boolean exemptionActive = memberSection.getBoolean("limbo.exemption.active");
		String exemptionBy = memberSection.getString("limbo.exemption.by");
		Instant exemptionAt = Instant.ofEpochMilli(memberSection.getLong("limbo.exemption.at"));
		int exemptionExpiresAfter = memberSection.getInt("limbo.exemption.expires_after");
		String exemptionReason = memberSection.getString("limbo.exemption.reason");
		
		// limbo.active.X
		Instant activeLastLogout = Instant.ofEpochMilli(memberSection.getLong("limbo.active.last_logout"));
		Instant activeNagMessageLastSentAt = Instant.ofEpochMilli(memberSection.getLong("limbo.active.nag_message.last_sent_at"));
		boolean activeNagMessageSuccessful = memberSection.getBoolean("limbo.active.nag_message.message_successful");
		
		// limbo.in_limbo.X
		Instant placedInLimboAt = Instant.ofEpochMilli(memberSection.getLong("limbo.in_limbo.placed_in_limbo_at"));
		boolean currentlyInLimbo = memberSection.getBoolean("limbo.in_limbo.currently_in_limbo");
		Instant inLimboMessageSentAt = Instant.ofEpochMilli(memberSection.getLong("limbo.in_limbo.message.at"));
		boolean inLimboMessageSent = memberSection.getBoolean("limbo.in_limbo.message.sent");
		
		// limbo.retire_danger.X
		boolean retireDangerMessageSent = memberSection.getBoolean("limbo.retire_danger.message.sent");
		Instant retireDangerMessageSentAt = Instant.ofEpochMilli(memberSection.getLong("limbo.retire_danger.message.at"));
		
		// limbo.retired.X
		boolean retiredMessageToPlayerSent = memberSection.getBoolean("limbo.retired.message_to_player.sent");
		Instant retiredMessageToPlayerSentAt = Instant.ofEpochMilli(memberSection.getLong("limbo.retired.message_to_player.at"));
		boolean retiredMessageToShoutsSent = memberSection.getBoolean("limbo.retired.message_to_shouts.sent");
		Instant retiredMessageToShoutsSentAt = Instant.ofEpochMilli(memberSection.getLong("limbo.retired.message_to_shouts.at"));
		
		LimboExemptionStatusBean limboExemption = new LimboExemptionStatusBean(exemptionActive, exemptionBy, exemptionAt, exemptionExpiresAfter, exemptionReason);
		LimboStatusBean limboStatus = new LimboStatusBean(activeLastLogout, activeNagMessageLastSentAt, activeNagMessageSuccessful, placedInLimboAt, currentlyInLimbo, 
				inLimboMessageSentAt, inLimboMessageSent, retireDangerMessageSentAt, retireDangerMessageSent, retiredMessageToPlayerSentAt, retiredMessageToPlayerSent, 
				retiredMessageToShoutsSentAt, retiredMessageToShoutsSent);
		
		MemberDataBean memberData = new MemberDataBean(memberUuid);
		memberData.setDiscordId(discordId);
		memberData.setLastIpAddress(lastIp);
		memberData.setLimboExcemptionStatus(limboExemption);
		memberData.setLimboStatus(limboStatus);
		memberData.setSpectatorAccountUuids(spectatorAccounts);	
		
		return Optional.of(memberData);
	}
	
}
