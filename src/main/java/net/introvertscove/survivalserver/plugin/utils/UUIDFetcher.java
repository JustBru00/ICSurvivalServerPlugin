package net.introvertscove.survivalserver.plugin.utils;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.shanerx.mojang.Mojang;

import net.introvertscove.survivalserver.commandhandlers.EpicCallback;
import net.introvertscove.survivalserver.plugin.IntrovertsPlugin;
import net.introvertscove.survivalserver.plugin.database.DatabaseManager;

public class UUIDFetcher {

	private static Mojang api;
	private static int cacheExpiry = 86400;
	
	private static void init() {
		api = new Mojang().connect();
	}
	
	
	public static void updateCachedUuid(UUID uuid, String playerUserName) {
		DatabaseManager.getUuidCache().set(uuid.toString() + ".lastname", playerUserName);
		DatabaseManager.getUuidCache().set(uuid.toString() + ".at", System.currentTimeMillis());
		DatabaseManager.saveUuidCache();
	}
	
	@SuppressWarnings("deprecation")
	public static void getUuidAsyncAndRunCallback(final String mcUsernameOrUuid, final EpicCallback callback) {
		Bukkit.getScheduler().scheduleAsyncDelayedTask(IntrovertsPlugin.getInstance(), new Runnable() {
			
			public void run() {
				Optional<UUID> playerUuid;
				Optional<String> lastName;
				
				if (mcUsernameOrUuid.length() <= 16) {
					// Probably username					
					final Optional<UUID> possibleUuid = UUIDFetcher.getUuid(mcUsernameOrUuid);

					if (possibleUuid.isPresent()) {
						// Actual player with real uuid
						Bukkit.getScheduler().scheduleSyncDelayedTask(IntrovertsPlugin.getInstance(), new Runnable() {							
							public void run() {
								final Optional<String> finalLastName = Optional.of(mcUsernameOrUuid);
								callback.runSync(possibleUuid, finalLastName, mcUsernameOrUuid);								
							}
						});		
						return;
					} else {
						// Not actual player
						Bukkit.getScheduler().scheduleSyncDelayedTask(IntrovertsPlugin.getInstance(), new Runnable() {							
							public void run() {
								Optional<UUID> emptyUuid = Optional.empty();
								Optional<String> emptyLastName = Optional.empty();
								callback.runSync(emptyUuid, emptyLastName, mcUsernameOrUuid);								
							}
						});		
						return;
					}					
				} else {
					// Probably UUID
					try {
						playerUuid = Optional.of(UUID.fromString(mcUsernameOrUuid));
						if (playerUuid.isPresent()) {
							lastName = getLastName(playerUuid.get());
						} else {
							lastName = Optional.empty();
						}
						
						final Optional<UUID> finalUuid = playerUuid;
						final Optional<String> finalLastName = lastName;
						
						Bukkit.getScheduler().scheduleSyncDelayedTask(IntrovertsPlugin.getInstance(), new Runnable() {							
							public void run() {
								callback.runSync(finalUuid, finalLastName, mcUsernameOrUuid);								
							}
						});
						return;
					} catch (IllegalArgumentException e) {						
						Bukkit.getScheduler().scheduleSyncDelayedTask(IntrovertsPlugin.getInstance(), new Runnable() {							
							public void run() {
								Optional<UUID> emptyUuid = Optional.empty();
								Optional<String> emptyLastName = Optional.empty();
								callback.runSync(emptyUuid, emptyLastName, mcUsernameOrUuid);								
							}
						});		
						return;
					}			
				}	
				
			}
		});		
	}
	
	/**
	 * This method will contact the mojang api to get the last name for this player.
	 * @param uuid
	 * @return An Optional<String> containing the last playername for the uuid. The optional will be empty if mojang doesn't know it.
	 */
	public static Optional<String> getLastName(UUID uuid) {
		if (api == null) {
			init();
		}		

		for (String key : DatabaseManager.getUuidCache().getKeys(false)) {	
			if (key.equalsIgnoreCase(uuid.toString())) {
				if (Duration.between(Instant.ofEpochMilli(DatabaseManager.getUuidCache().getLong(uuid + ".at")), Instant.now()).getSeconds() < cacheExpiry) {
					Messager.msgConsole("[UUIDFetcher] LastName was cached. Skipping Mojang API call.");
					return Optional.of(DatabaseManager.getUuidCache().getString(key + ".lastname"));
				}
			}						
		}	
		
		String mojangPlayerName;
		try {
			mojangPlayerName = api.getPlayerProfile(uuid.toString()).getUsername();
			Messager.msgConsole("[UUIDFetcher] Got LastName of '" + mojangPlayerName + "' from UUID '" + uuid.toString() + "' from Mojang.");
		} catch (Exception e) {
			return Optional.empty();
		}
		
		updateCachedUuid(uuid, mojangPlayerName);
		return Optional.of(mojangPlayerName);
	}
	
	/**
	 * This method will attempt to get the UUID from cache first.
	 * If the UUID is not cached, it will contact the Mojang API.
	 * @param playerUserName
	 * @return
	 */
	public static Optional<UUID> getUuid(String playerUserName) {		
		if (api == null) {
			init();
		}
		
		for (String uuid : DatabaseManager.getUuidCache().getKeys(false)) {			
			if (DatabaseManager.getUuidCache().getString(uuid + ".lastname").equals(playerUserName)) {
				if (Duration.between(Instant.ofEpochMilli(DatabaseManager.getUuidCache().getLong(uuid + ".at")), Instant.now()).getSeconds() < cacheExpiry) {
					Messager.msgConsole("[UUIDFetcher] UUID was cached. Skipping Mojang API call.");
					return Optional.of(UUID.fromString(uuid));
				}
			}			
		}		
		
		String mojangUuid;
		try {
			mojangUuid = api.getUUIDOfUsername(playerUserName);			
		} catch (Exception e) {
			return Optional.empty();
		}
		
		mojangUuid = nonDashedToDashedUuid(mojangUuid);
		Messager.msgConsole("[UUIDFetcher] Got UUID of '" + mojangUuid + "' from player name '" + playerUserName + "' from Mojang.");
		updateCachedUuid(UUID.fromString(mojangUuid), playerUserName);
		
		return Optional.of(UUID.fromString(mojangUuid));
	}
	
	private static String nonDashedToDashedUuid(String nonDashedUuid) {
	    StringBuilder sb = new StringBuilder(nonDashedUuid);
	    sb.insert(8, "-");
	    sb = new StringBuilder(sb.toString());
	    sb.insert(13, "-");
	    sb = new StringBuilder(sb.toString());
	    sb.insert(18, "-");
	    sb = new StringBuilder(sb.toString());
	    sb.insert(23, "-");

	    return sb.toString();
	  }	
	
}
