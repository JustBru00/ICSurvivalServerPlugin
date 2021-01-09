package net.introvertscove.survivalserver.beans;

import java.util.ArrayList;
import java.util.UUID;

public class MemberDataBean {

	private UUID minecraftUuid;
	private long discordId;
	private ArrayList<UUID> spectatorAccountUuids = new ArrayList<UUID>();
	private String lastIpAddress;
	
	private LimboExceptionStatusBean limboExcemptionStatus;
	
	
	
}
