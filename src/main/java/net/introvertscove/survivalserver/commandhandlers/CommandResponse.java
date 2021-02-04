package net.introvertscove.survivalserver.commandhandlers;

public class CommandResponse {
	
	private String responseMessage;
	private ResponseType responseType;

	public CommandResponse(String _responseMessage, ResponseType _responseType) {
		responseMessage = _responseMessage;
		responseType = _responseType;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public ResponseType getResponseType() {
		return responseType;
	}

	public void setResponseType(ResponseType responseType) {
		this.responseType = responseType;
	}
	
	
	
}
