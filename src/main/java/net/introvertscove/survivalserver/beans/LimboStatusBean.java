package net.introvertscove.survivalserver.beans;

public class LimboStatusBean {

	private long lastLogout;
	private long nagMessageLastSentAt;
	private boolean nagMessageSuccessful;

	private long placedInLimboAt;
	private boolean currentlyInLimbo;
	private long inLimboMessageSentAt;
	private boolean inLimboMessageSent;

	private long retireDangerMessageSentAt;
	private boolean retiredDangerMessageSent;

	private long retiredMessageSentToPlayerAt;
	private boolean retiredMessageSentToPlayer;

	private long retiredMessageSentToShoutsAt;
	private boolean retiredMessageSentToShouts;

	public LimboStatusBean(long lastLogout, long nagMessageLastSentAt, boolean nagMessageSuccessful,
			long placedInLimboAt, boolean currentlyInLimbo, long inLimboMessageSentAt, boolean inLimboMessageSent,
			long retireDangerMessageSentAt, boolean retiredDangerMessageSent, long retiredMessageSentToPlayerAt,
			boolean retiredMessageSentToPlayer, long retiredMessageSentToShoutsAt,
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

	public long getLastLogout() {
		return lastLogout;
	}

	public void setLastLogout(long lastLogout) {
		this.lastLogout = lastLogout;
	}

	public long getNagMessageLastSentAt() {
		return nagMessageLastSentAt;
	}

	public void setNagMessageLastSentAt(long nagMessageLastSentAt) {
		this.nagMessageLastSentAt = nagMessageLastSentAt;
	}

	public boolean isNagMessageSuccessful() {
		return nagMessageSuccessful;
	}

	public void setNagMessageSuccessful(boolean nagMessageSuccessful) {
		this.nagMessageSuccessful = nagMessageSuccessful;
	}

	public long getPlacedInLimboAt() {
		return placedInLimboAt;
	}

	public void setPlacedInLimboAt(long placedInLimboAt) {
		this.placedInLimboAt = placedInLimboAt;
	}

	public boolean isCurrentlyInLimbo() {
		return currentlyInLimbo;
	}

	public void setCurrentlyInLimbo(boolean currentlyInLimbo) {
		this.currentlyInLimbo = currentlyInLimbo;
	}

	public long getInLimboMessageSentAt() {
		return inLimboMessageSentAt;
	}

	public void setInLimboMessageSentAt(long inLimboMessageSentAt) {
		this.inLimboMessageSentAt = inLimboMessageSentAt;
	}

	public boolean isInLimboMessageSent() {
		return inLimboMessageSent;
	}

	public void setInLimboMessageSent(boolean inLimboMessageSent) {
		this.inLimboMessageSent = inLimboMessageSent;
	}

	public long getRetireDangerMessageSentAt() {
		return retireDangerMessageSentAt;
	}

	public void setRetireDangerMessageSentAt(long retireDangerMessageSentAt) {
		this.retireDangerMessageSentAt = retireDangerMessageSentAt;
	}

	public boolean isRetiredDangerMessageSent() {
		return retiredDangerMessageSent;
	}

	public void setRetiredDangerMessageSent(boolean retiredDangerMessageSent) {
		this.retiredDangerMessageSent = retiredDangerMessageSent;
	}

	public long getRetiredMessageSentToPlayerAt() {
		return retiredMessageSentToPlayerAt;
	}

	public void setRetiredMessageSentToPlayerAt(long retiredMessageSentToPlayerAt) {
		this.retiredMessageSentToPlayerAt = retiredMessageSentToPlayerAt;
	}

	public boolean isRetiredMessageSentToPlayer() {
		return retiredMessageSentToPlayer;
	}

	public void setRetiredMessageSentToPlayer(boolean retiredMessageSentToPlayer) {
		this.retiredMessageSentToPlayer = retiredMessageSentToPlayer;
	}

	public long getRetiredMessageSentToShoutsAt() {
		return retiredMessageSentToShoutsAt;
	}

	public void setRetiredMessageSentToShoutsAt(long retiredMessageSentToShoutsAt) {
		this.retiredMessageSentToShoutsAt = retiredMessageSentToShoutsAt;
	}

	public boolean isRetiredMessageSentToShouts() {
		return retiredMessageSentToShouts;
	}

	public void setRetiredMessageSentToShouts(boolean retiredMessageSentToShouts) {
		this.retiredMessageSentToShouts = retiredMessageSentToShouts;
	}

}
