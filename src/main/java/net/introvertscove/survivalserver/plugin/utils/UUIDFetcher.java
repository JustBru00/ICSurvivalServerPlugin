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
	private static int cacheExpiry = 2592000;
	
	private static void init() {
		api = new Mojang().connect();
	}
	
	
	public static void updateCachedUuid(UUID uuid, String playerUserName) {
		DatabaseManager.getUuidCacheFile().set(uuid.toString() + ".lastname", playerUserName);
		DatabaseManager.getUuidCacheFile().set(uuid.toString() + ".at", System.currentTimeMillis());
		DatabaseManager.saveUuidCacheFile();
	}
	
	/** 
	 * This method will attempt to get the UUID from the cache.
	 * If it can't be found. the Optional will be empty. This method will call {@link #getUuidAsyncAndRunCallback(String, EpicCallback)} to get the missing UUID in the cache.
	 * This will make it so that a user that attempts a command again, will be able to get the command to execute the second time.
	 * @param username
	 */
	public static Optional<UUID> getCachedUuidFromUsername(String username) {
		for (String uuid : DatabaseManager.getUuidCacheFile().getKeys(false)) {			
			if (DatabaseManager.getUuidCacheFile().getString(uuid + ".lastname").equals(username)) {
				if (Duration.between(Instant.ofEpochMilli(DatabaseManager.getUuidCacheFile().getLong(uuid + ".at")), Instant.now()).getSeconds() < cacheExpiry) {
					Messager.msgConsole("[UUIDFetcher] UUID was cached.");
					return Optional.of(UUID.fromString(uuid));
				}
			}			
		}
		
		getUuidAsyncAndRunCallback(username, new EpicCallback() {
			
			public void runSync(Optional<UUID> uuid, Optional<String> lastName, String originalMcUsernameOrUuid) {
				// Do nothing				
			}
		});
		
		return Optional.empty();
	}
	
	/**
	 * This method will attempt to get the last username for the provided UUID from the cache.
	 * If it can't be found. the Optional will be empty. This method will call {@link #getUuidAsyncAndRunCallback(String, EpicCallback)} to get the missing last name in the cache.
	 * This will make it so that a user that attempts a command again, will be able to get the command to execute the second time.
	 * @param uuid
	 * @return
	 */
	public static Optional<String> getCachedUsernameFromUuid(UUID uuid) {
		for (String key : DatabaseManager.getUuidCacheFile().getKeys(false)) {	
			if (key.equalsIgnoreCase(uuid.toString())) {
				if (Duration.between(Instant.ofEpochMilli(DatabaseManager.getUuidCacheFile().getLong(uuid + ".at")), Instant.now()).getSeconds() < cacheExpiry) {
					Messager.msgConsole("[UUIDFetcher] LastName was cached.");
					return Optional.of(DatabaseManager.getUuidCacheFile().getString(key + ".lastname"));
				}
			}						
		}	
		
		getUuidAsyncAndRunCallback(uuid.toString(), new EpicCallback() {
			
			public void runSync(Optional<UUID> uuid, Optional<String> lastName, String originalMcUsernameOrUuid) {
				// Do nothing				
			}
		});
		
		return Optional.empty();
	}
	
	
	/**
	 * This method will attempt to get the UUID from the cache. If it can't be found it will be grabbed from the mojang api.
	 * @param mcUsernameOrUuid
	 * @param callback
	 */
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

		for (String key : DatabaseManager.getUuidCacheFile().getKeys(false)) {	
			if (key.equalsIgnoreCase(uuid.toString())) {
				if (Duration.between(Instant.ofEpochMilli(DatabaseManager.getUuidCacheFile().getLong(uuid + ".at")), Instant.now()).getSeconds() < cacheExpiry) {
					Messager.msgConsole("[UUIDFetcher] LastName was cached. Skipping Mojang API call.");
					return Optional.of(DatabaseManager.getUuidCacheFile().getString(key + ".lastname"));
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
		
		for (String uuid : DatabaseManager.getUuidCacheFile().getKeys(false)) {			
			if (DatabaseManager.getUuidCacheFile().getString(uuid + ".lastname").equals(playerUserName)) {
				if (Duration.between(Instant.ofEpochMilli(DatabaseManager.getUuidCacheFile().getLong(uuid + ".at")), Instant.now()).getSeconds() < cacheExpiry) {
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
