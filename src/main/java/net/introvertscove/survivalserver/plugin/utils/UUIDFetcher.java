package net.introvertscove.survivalserver.plugin.utils;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.shanerx.mojang.Mojang;

import net.introvertscove.survivalserver.plugin.database.DatabaseManager;

public class UUIDFetcher {

	private static Mojang api;
	private static int cacheExpiry = 86400;
	
	private void init() {
		api = new Mojang().connect();
	}
	
	public static void updateCachedUuid(UUID uuid, String playerUserName) {
		DatabaseManager.getUuidCache().set(uuid.toString() + ".lastname", playerUserName);
		DatabaseManager.getUuidCache().set(uuid.toString() + ".at", System.currentTimeMillis());
		DatabaseManager.saveUuidCache();
	}
	
	/**
	 * This method will attempt to get the UUID from cache first.
	 * If the UUID is not cached, it will contact the Mojang API.
	 * @param playerUserName
	 * @return
	 */
	public Optional<UUID> getUuid(String playerUserName) {		
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
		
		updateCachedUuid(UUID.fromString(mojangUuid), playerUserName);
		
		return Optional.of(UUID.fromString(mojangUuid));
	}
	
}
