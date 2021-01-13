package net.introvertscove.survivalserver.beans;

import java.util.ArrayList;
import java.util.UUID;

public class MemberDataBean {

	private UUID minecraftUuid;
	private long discordId;
	private ArrayList<UUID> spectatorAccountUuids = new ArrayList<UUID>();
	private String lastIpAddress;
	
	private LimboExemptionStatusBean limboExcemptionStatus;
	
	private LimboStatusBean limboStatus;
	
	public MemberDataBean(UUID minecraftUUID) {
		super();
		minecraftUuid = minecraftUUID;
	}

	public UUID getMinecraftUuid() {
		return minecraftUuid;
	}

	public void setMinecraftUuid(UUID minecraftUuid) {
		this.minecraftUuid = minecraftUuid;
	}

	public long getDiscordId() {
		return discordId;
	}

	public void setDiscordId(long discordId) {
		this.discordId = discordId;
	}

	public ArrayList<UUID> getSpectatorAccountUuids() {
		return spectatorAccountUuids;
	}

	public void setSpectatorAccountUuids(ArrayList<UUID> spectatorAccountUuids) {
		this.spectatorAccountUuids = spectatorAccountUuids;
	}

	public String getLastIpAddress() {
		return lastIpAddress;
	}

	public void setLastIpAddress(String lastIpAddress) {
		this.lastIpAddress = lastIpAddress;
	}

	public LimboExemptionStatusBean getLimboExcemptionStatus() {
		return limboExcemptionStatus;
	}

	public void setLimboExcemptionStatus(LimboExemptionStatusBean limboExcemptionStatus) {
		this.limboExcemptionStatus = limboExcemptionStatus;
	}

	public LimboStatusBean getLimboStatus() {
		return limboStatus;
	}

	public void setLimboStatus(LimboStatusBean limboStatus) {
		this.limboStatus = limboStatus;
	}	
}
