package liferay.chat.model;


public class ChatMessage {
	
	public ChatMessage() {
		super();
	}
	
	public ChatMessage(long userId, String msgType, String data) {
		this.userId = userId;
		this.msgType = msgType;
		this.data = data;
	}

	long userId;
	private String msgType;
	private String data;

	public long getUserId() {
		return userId;
	}
	
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public String getMsgType() {
		return msgType;
	}
	
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	
	public String getData() {
		return data;
	}
	
	public void setData(String data) {
		this.data = data;
	}

}
