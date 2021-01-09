package net.introvertscove.survivalserver.beans;

import java.time.Instant;

public class LimboExceptionStatusBean {

	private boolean exceptionActive;
	private String adminWhoAddedException;
	private Instant exceptionAddedStartingAt;
	private int exceptionExpiresAfterSeconds;
	private String excemptionReason;
	
	public LimboExceptionStatusBean(boolean exceptionActive, String adminWhoAddedException,
			Instant exceptionAddedStartingAt, int exceptionExpiresAfterSeconds, String excemptionReason) {
		super();
		this.exceptionActive = exceptionActive;
		this.adminWhoAddedException = adminWhoAddedException;
		this.exceptionAddedStartingAt = exceptionAddedStartingAt;
		this.exceptionExpiresAfterSeconds = exceptionExpiresAfterSeconds;
		this.excemptionReason = excemptionReason;
	}

	public boolean isExceptionActive() {
		return exceptionActive;
	}

	public void setExceptionActive(boolean exceptionActive) {
		this.exceptionActive = exceptionActive;
	}

	public String getAdminWhoAddedException() {
		return adminWhoAddedException;
	}

	public void setAdminWhoAddedException(String adminWhoAddedException) {
		this.adminWhoAddedException = adminWhoAddedException;
	}

	public Instant getExceptionAddedStartingAt() {
		return exceptionAddedStartingAt;
	}

	public void setExceptionAddedStartingAt(Instant exceptionAddedStartingAt) {
		this.exceptionAddedStartingAt = exceptionAddedStartingAt;
	}

	public int getExceptionExpiresAfterSeconds() {
		return exceptionExpiresAfterSeconds;
	}

	public void setExceptionExpiresAfterSeconds(int exceptionExpiresAfterSeconds) {
		this.exceptionExpiresAfterSeconds = exceptionExpiresAfterSeconds;
	}

	public String getExcemptionReason() {
		return excemptionReason;
	}

	public void setExcemptionReason(String excemptionReason) {
		this.excemptionReason = excemptionReason;
	}
		
}
