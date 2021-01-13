package net.introvertscove.survivalserver.beans;

import java.time.Instant;

public class LimboStatusBean {

	private Instant lastLogout;
	private Instant nagMessageLastSentAt;
	private boolean nagMessageSuccessful;

	private Instant placedInLimboAt;
	private boolean currentlyInLimbo;
	private Instant inLimboMessageSentAt;
	private boolean inLimboMessageSent;

	private Instant retireDangerMessageSentAt;
	private boolean retiredDangerMessageSent;

	private Instant retiredMessageSentToPlayerAt;
	private boolean retiredMessageSentToPlayer;

	private Instant retiredMessageSentToShoutsAt;
	private boolean retiredMessageSentToShouts;

	public LimboStatusBean(Instant lastLogout, Instant nagMessageLastSentAt, boolean nagMessageSuccessful,
			Instant placedInLimboAt, boolean currentlyInLimbo, Instant inLimboMessageSentAt, boolean inLimboMessageSent,
			Instant retireDangerMessageSentAt, boolean retiredDangerMessageSent, Instant retiredMessageSentToPlayerAt,
			boolean retiredMessageSentToPlayer, Instant retiredMessageSentToShoutsAt,
			boolean retiredMessageSentToShouts) {
		super();
		this.lastLogout = lastLogout;
		this.nagMessageLastSentAt = nagMessageLastSentAt;
		this.nagMessageSuccessful = nagMessageSuccessful;
		this.placedInLimboAt = placedInLimboAt;
		this.currentlyInLimbo = currentlyInLimbo;
		this.inLimboMessageSentAt = inLimboMessageSentAt;
		this.inLimboMessageSent = inLimboMessageSent;
		this.retireDangerMessageSentAt = retireDangerMessageSentAt;
		this.retiredDangerMessageSent = retiredDangerMessageSent;
		this.retiredMessageSentToPlayerAt = retiredMessageSentToPlayerAt;
		this.retiredMessageSentToPlayer = retiredMessageSentToPlayer;
		this.retiredMessageSentToShoutsAt = retiredMessageSentToShoutsAt;
		this.retiredMessageSentToShouts = retiredMessageSentToShouts;
	}

	public Instant getLastLogout() {
		return lastLogout;
	}

	public void setLastLogout(Instant lastLogout) {
		this.lastLogout = lastLogout;
	}

	public Instant getNagMessageLastSentAt() {
		return nagMessageLastSentAt;
	}

	public void setNagMessageLastSentAt(Instant nagMessageLastSentAt) {
		this.nagMessageLastSentAt = nagMessageLastSentAt;
	}

	public boolean isNagMessageSuccessful() {
		return nagMessageSuccessful;
	}

	public void setNagMessageSuccessful(boolean nagMessageSuccessful) {
		this.nagMessageSuccessful = nagMessageSuccessful;
	}

	public Instant getPlacedInLimboAt() {
		return placedInLimboAt;
	}

	public void setPlacedInLimboAt(Instant placedInLimboAt) {
		this.placedInLimboAt = placedInLimboAt;
	}

	public boolean isCurrentlyInLimbo() {
		return currentlyInLimbo;
	}

	public void setCurrentlyInLimbo(boolean currentlyInLimbo) {
		this.currentlyInLimbo = currentlyInLimbo;
	}

	public Instant getInLimboMessageSentAt() {
		return inLimboMessageSentAt;
	}

	public void setInLimboMessageSentAt(Instant inLimboMessageSentAt) {
		this.inLimboMessageSentAt = inLimboMessageSentAt;
	}

	public boolean isInLimboMessageSent() {
		return inLimboMessageSent;
	}

	public void setInLimboMessageSent(boolean inLimboMessageSent) {
		this.inLimboMessageSent = inLimboMessageSent;
	}

	public Instant getRetireDangerMessageSentAt() {
		return retireDangerMessageSentAt;
	}

	public void setRetireDangerMessageSentAt(Instant retireDangerMessageSentAt) {
		this.retireDangerMessageSentAt = retireDangerMessageSentAt;
	}

	public boolean isRetiredDangerMessageSent() {
		return retiredDangerMessageSent;
	}

	public void setRetiredDangerMessageSent(boolean retiredDangerMessageSent) {
		this.retiredDangerMessageSent = retiredDangerMessageSent;
	}

	public Instant getRetiredMessageSentToPlayerAt() {
		return retiredMessageSentToPlayerAt;
	}

	public void setRetiredMessageSentToPlayerAt(Instant retiredMessageSentToPlayerAt) {
		this.retiredMessageSentToPlayerAt = retiredMessageSentToPlayerAt;
	}

	public boolean isRetiredMessageSentToPlayer() {
		return retiredMessageSentToPlayer;
	}

	public void setRetiredMessageSentToPlayer(boolean retiredMessageSentToPlayer) {
		this.retiredMessageSentToPlayer = retiredMessageSentToPlayer;
	}

	public Instant getRetiredMessageSentToShoutsAt() {
		return retiredMessageSentToShoutsAt;
	}

	public void setRetiredMessageSentToShoutsAt(Instant retiredMessageSentToShoutsAt) {
		this.retiredMessageSentToShoutsAt = retiredMessageSentToShoutsAt;
	}

	public boolean isRetiredMessageSentToShouts() {
		return retiredMessageSentToShouts;
	}

	public void setRetiredMessageSentToShouts(boolean retiredMessageSentToShouts) {
		this.retiredMessageSentToShouts = retiredMessageSentToShouts;
	}

}
