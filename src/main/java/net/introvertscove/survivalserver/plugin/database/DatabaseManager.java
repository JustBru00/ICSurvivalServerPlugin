package net.introvertscove.survivalserver.plugin.database;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.bukkit.configuration.ConfigurationSection;

import net.introvertscove.survivalserver.beans.LimboExemptionStatusBean;
import net.introvertscove.survivalserver.beans.LimboStatusBean;
import net.introvertscove.survivalserver.beans.MemberDataBean;
import net.introvertscove.survivalserver.plugin.IntrovertsPlugin;
import net.introvertscove.survivalserver.plugin.utils.Messager;
import net.introvertscove.survivalserver.plugin.utils.PluginFile;

public class DatabaseManager {
	
	private static ConcurrentHashMap<UUID,Instant> onlinePlayerSessionStorage = new ConcurrentHashMap<UUID, Instant>();

	private static PluginFile memberData = null;
	private static PluginFile sessionHistory = null;
	private static PluginFile uuidCache = null;
	
	public static void init() {
		memberData = new PluginFile(IntrovertsPlugin.getInstance(), "member_data.yml");
		sessionHistory = new PluginFile(IntrovertsPlugin.getInstance(), "session_history.yml");		
		uuidCache = new PluginFile(IntrovertsPlugin.getInstance(), "uuid_cache.yml");	
	}	
	
	public static PluginFile getUuidCache() {
		return uuidCache;
	}
	
	public static void saveUuidCache() {
		uuidCache.save();
	}
	
	public static PluginFile getMemberData() {
		return memberData;
	}
	
	public static PluginFile getSessionHistory() {
		return sessionHistory;
	}
	
	public static void saveMemberData() {
		memberData.save();
	}
	
	public static void saveSessionHistory() {
		sessionHistory.save();
	}
	
	public static void reloadMemberData() {
		memberData.reload();
	}
	
	public static void reloadSessionHistory() {
		sessionHistory.reload();
	}
	
	public static String getTotalPlaytimeFormatted(UUID memberUuid) {
		StringBuilder totalPlaytimeFormatted = new StringBuilder();
		
		int totalPlayTimeSeconds = sessionHistory.getInt(memberUuid.toString() + ".total_playtime_seconds");
		
		if (totalPlayTimeSeconds <= 0) {
			// Less than or equals zero.
			totalPlaytimeFormatted.append("No playtime recorded");			
		} else {
			
			int days = (int) TimeUnit.SECONDS.toDays(totalPlayTimeSeconds);        
            long hours = TimeUnit.SECONDS.toHours(totalPlayTimeSeconds) - (days * 24);
            long minutes = TimeUnit.SECONDS.toMinutes(totalPlayTimeSeconds) - (TimeUnit.SECONDS.toHours(totalPlayTimeSeconds)* 60);
            long seconds = TimeUnit.SECONDS.toSeconds(totalPlayTimeSeconds) - (TimeUnit.SECONDS.toMinutes(totalPlayTimeSeconds) *60);
			
            if (days > 0) {
            	totalPlaytimeFormatted.append(days + " day(s), ");
            }
            
            totalPlaytimeFormatted.append(hours + " hour(s), ");
            totalPlaytimeFormatted.append(minutes + " minute(s), ");
            totalPlaytimeFormatted.append(seconds + " second(s)");			
		}
		
		return totalPlaytimeFormatted.toString();
	}
	
	/**
	 * Generates a {@link MemberDataBean} with the given UUID and the rest of the values set to defaults.
	 * @param uuid
	 * @return
	 */
	public static MemberDataBean getDefaultMemberDataBean(UUID uuid, long discordId) {
		LimboExemptionStatusBean limboExemption = new LimboExemptionStatusBean(false, "None", -1L, -1, "None");
		LimboStatusBean limboStatus = new LimboStatusBean(-1L, -1L, false, -1L, false, 
				-1L, false, -1L, false, -1L, false, 
				-1L, false);
		
		MemberDataBean memberData = new MemberDataBean(uuid);
		memberData.setDiscordId(discordId);
		memberData.setLastIpAddress("None recorded");
		memberData.setLimboExcemptionStatus(limboExemption);
		memberData.setLimboStatus(limboStatus);
		memberData.setSpectatorAccountUuids(new ArrayList<UUID>());	
		return memberData;
	}
	
	public static ArrayList<MemberDataBean> getAllMembers() {
		ArrayList<MemberDataBean> memberDataBeans = new ArrayList<MemberDataBean>();
		for (String key : memberData.getKeys(false)) {
			Optional<MemberDataBean> singleMember = getMemberData(UUID.fromString(key));
			if (singleMember.isPresent()) {
				memberDataBeans.add(singleMember.get());
			}			
		}
		return memberDataBeans;
	}
	
	public static boolean isSpectatorAccount(UUID uuid) {		
		for (String key : memberData.getKeys(false)) {
			List<String> spectatorAccounts = memberData.getStringList(key + ".spectator_accounts");
			for (String spectatorId : spectatorAccounts) {
				if (uuid.toString().equalsIgnoreCase(spectatorId)) {
					// Is a spectator account
					return true;
				}
			}
		}
		return false;
	}
	
	public static void logPlayerLoginToSessionHistory(UUID uuid) {
		onlinePlayerSessionStorage.put(uuid, Instant.now());
	}
	
	public static void logPlayerLogoutToSessionHistory(UUID uuid) {
		Instant logoutTime = Instant.now();
		Instant loginTime = onlinePlayerSessionStorage.get(uuid);
		
		if (loginTime == null) {
			Messager.msgConsole("Failed to retrive login time from HashMap for " + uuid + ".");
			return;
		}		
		
		long secondsOnline = Duration.between(loginTime, logoutTime).getSeconds();
		
		if (sessionHistory.getConfigurationSection(uuid.toString()) == null) {
			sessionHistory.set(uuid.toString() + ".total_playtime_seconds", 0L);
			sessionHistory.save();
		}
		
		long secondsOfPlayTime = sessionHistory.getLong(uuid.toString() + ".total_playtime_seconds");
		secondsOfPlayTime = secondsOfPlayTime + secondsOnline;
		sessionHistory.set(uuid.toString() + ".total_playtime_seconds", secondsOfPlayTime);
		
		sessionHistory.set(uuid.toString() + "." + loginTime.toEpochMilli(), logoutTime.toEpochMilli());
		sessionHistory.save();
	}
	
	public static void saveMemberDataToFile(MemberDataBean m) {
		String prefix = m.getMinecraftUuid().toString();
		
		List<String> spectators = new ArrayList<String>();
		for (UUID u : m.getSpectatorAccountUuids()) {
			spectators.add(u.toString());
		}
		
		memberData.set(prefix + ".discord_id", m.getDiscordId());
		memberData.set(prefix + ".spectator_accounts", spectators);
		memberData.set(prefix + ".last_ip", m.getLastIpAddress());
		
		LimboExemptionStatusBean exemption =  m.getLimboExcemptionStatus();
		memberData.set(prefix + ".limbo.exemption.active", exemption.isExemptionActive());
		memberData.set(prefix + ".limbo.exemption.by", exemption.getAdminWhoAddedExemption());
		memberData.set(prefix + ".limbo.exemption.at", exemption.getExemptionAddedStartingAt());
		memberData.set(prefix + ".limbo.exemption.expires_after", exemption.getExemptionExpiresAfterSeconds());
		memberData.set(prefix + ".limbo.exemption.reason", exemption.getExemptionReason());
		
		LimboStatusBean limboStatus = m.getLimboStatus();
		memberData.set(prefix + ".limbo.active.last_logout", limboStatus.getLastLogout());
		memberData.set(prefix + ".limbo.active.nag_message.last_sent_at", limboStatus.getNagMessageLastSentAt());
		memberData.set(prefix + ".limbo.active.nag_message.message_successful", limboStatus.isNagMessageSuccessful());
		
		memberData.set(prefix + ".in_limbo.placed_in_limbo_at", limboStatus.getPlacedInLimboAt());
		memberData.set(prefix + ".in_limbo.currently_in_limbo", limboStatus.isCurrentlyInLimbo());
		memberData.set(prefix + ".in_limbo.message.sent", limboStatus.isInLimboMessageSent());
		memberData.set(prefix + ".in_limbo.message.at", limboStatus.getInLimboMessageSentAt());
		
		memberData.set(prefix + ".retire_danger.message.sent", limboStatus.isRetiredDangerMessageSent());
		memberData.set(prefix + ".retire_danger.message.at", limboStatus.getRetireDangerMessageSentAt());
		
		memberData.set(prefix + ".retired.message_to_player.sent", limboStatus.isRetiredMessageSentToPlayer());
		memberData.set(prefix + ".retired.message_to_player.at", limboStatus.getRetiredMessageSentToPlayerAt());
		memberData.set(prefix + ".retired.message_to_shouts.sent", limboStatus.isRetiredMessageSentToShouts());
		memberData.set(prefix + ".retired.message_to_shouts.at", limboStatus.getRetiredMessageSentToShoutsAt());
		
		memberData.save();
	}
	
	public static Optional<MemberDataBean> getMemberData(UUID memberUuid) {
		ConfigurationSection memberSection = memberData.getConfigurationSection(memberUuid.toString());
		
		if (memberSection == null || memberData.getString(memberUuid.toString()).equalsIgnoreCase("Removed Member")) {
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
		long exemptionAt = memberSection.getLong("limbo.exemption.at");
		int exemptionExpiresAfter = memberSection.getInt("limbo.exemption.expires_after");
		String exemptionReason = memberSection.getString("limbo.exemption.reason");
		
		// limbo.active.X
		long activeLastLogout = memberSection.getLong("limbo.active.last_logout");
		long activeNagMessageLastSentAt = memberSection.getLong("limbo.active.nag_message.last_sent_at");
		boolean activeNagMessageSuccessful = memberSection.getBoolean("limbo.active.nag_message.message_successful");
		
		// limbo.in_limbo.X
		long placedInLimboAt = memberSection.getLong("limbo.in_limbo.placed_in_limbo_at");
		boolean currentlyInLimbo = memberSection.getBoolean("limbo.in_limbo.currently_in_limbo");
		long inLimboMessageSentAt = memberSection.getLong("limbo.in_limbo.message.at");
		boolean inLimboMessageSent = memberSection.getBoolean("limbo.in_limbo.message.sent");
		
		// limbo.retire_danger.X
		boolean retireDangerMessageSent = memberSection.getBoolean("limbo.retire_danger.message.sent");
		long retireDangerMessageSentAt = memberSection.getLong("limbo.retire_danger.message.at");
		
		// limbo.retired.X
		boolean retiredMessageToPlayerSent = memberSection.getBoolean("limbo.retired.message_to_player.sent");
		long retiredMessageToPlayerSentAt = memberSection.getLong("limbo.retired.message_to_player.at");
		boolean retiredMessageToShoutsSent = memberSection.getBoolean("limbo.retired.message_to_shouts.sent");
		long retiredMessageToShoutsSentAt = memberSection.getLong("limbo.retired.message_to_shouts.at");
		
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
