package liferay.chat.model;

public class Message {
	
	private ChatUser chatUser;
    private MessageType type;
    private String data;

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public ChatUser getChatUser() {
        return chatUser;
    }

    public void setChatUser(ChatUser ChatUser) {
        this.chatUser = ChatUser;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData(){
        return data;
    }
}
