package net.introvertscove.survivalserver.beans;



public class LimboExemptionStatusBean {

	private boolean exceptionActive;
	private String adminWhoAddedException;
	private long exceptionAddedStartingAt;
	private int exceptionExpiresAfterSeconds;
	private String excemptionReason;
	
	public LimboExemptionStatusBean(boolean exceptionActive, String adminWhoAddedException,
			long exceptionAddedStartingAt, int exceptionExpiresAfterSeconds, String excemptionReason) {
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

	public long getExceptionAddedStartingAt() {
		return exceptionAddedStartingAt;
	}

	public void setExceptionAddedStartingAt(long exceptionAddedStartingAt) {
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
