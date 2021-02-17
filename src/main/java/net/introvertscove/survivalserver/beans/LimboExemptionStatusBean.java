package net.introvertscove.survivalserver.beans;



public class LimboExemptionStatusBean {

	private boolean exemptionActive;
	private String adminWhoAddedExemption;
	private long exemptionAddedStartingAt;
	private int exemptionExpiresAfterSeconds;
	private String exemptionReason;
	
	public LimboExemptionStatusBean(boolean exceptionActive, String adminWhoAddedException,
			long exceptionAddedStartingAt, int exceptionExpiresAfterSeconds, String excemptionReason) {
		super();
		this.exemptionActive = exceptionActive;
		this.adminWhoAddedExemption = adminWhoAddedException;
		this.exemptionAddedStartingAt = exceptionAddedStartingAt;
		this.exemptionExpiresAfterSeconds = exceptionExpiresAfterSeconds;
		this.exemptionReason = excemptionReason;
	}

	public boolean isExemptionActive() {
		return exemptionActive;
	}

	public void setExemptionActive(boolean exemptionActive) {
		this.exemptionActive = exemptionActive;
	}

	public String getAdminWhoAddedExemption() {
		return adminWhoAddedExemption;
	}

	public void setAdminWhoAddedExemption(String adminWhoAddedExemption) {
		this.adminWhoAddedExemption = adminWhoAddedExemption;
	}

	public long getExemptionAddedStartingAt() {
		return exemptionAddedStartingAt;
	}

	public void setExemptionAddedStartingAt(long exemptionAddedStartingAt) {
		this.exemptionAddedStartingAt = exemptionAddedStartingAt;
	}

	public int getExemptionExpiresAfterSeconds() {
		return exemptionExpiresAfterSeconds;
	}

	public void setExemptionExpiresAfterSeconds(int exemptionExpiresAfterSeconds) {
		this.exemptionExpiresAfterSeconds = exemptionExpiresAfterSeconds;
	}

	public String getExemptionReason() {
		return exemptionReason;
	}

	public void setExemptionReason(String exemptionReason) {
		this.exemptionReason = exemptionReason;
	}
		
}
