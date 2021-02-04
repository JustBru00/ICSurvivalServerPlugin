package net.introvertscove.survivalserver.commandhandlers;

import java.util.Optional;
import java.util.UUID;

public interface EpicCallback {

	void runSync(Optional<UUID> uuid, Optional<String> lastName, String originalMcUsernameOrUuid);
	
}
