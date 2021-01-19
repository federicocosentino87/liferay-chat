package liferay.chat.service.sever.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import liferay.chat.model.ChatUser;
import liferay.chat.model.Message;
import liferay.chat.model.MessageType;
import liferay.chat.server.ChatServer;

public class ChatServerImpl extends WebSocketServer implements ChatServer {

	private HashMap<WebSocket, ChatUser> users;

	private Set<WebSocket> connections;

	private ChatServerImpl(int port) {
        super(new InetSocketAddress(port));
        connections = new HashSet<>();
        users = new HashMap<>();
    }

	@Override
	public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
		connections.add(webSocket);

		_log.info("Connection established from: " + webSocket.getRemoteSocketAddress().getHostString());
		System.out.println("New connection from " + webSocket.getRemoteSocketAddress().getAddress().getHostAddress());
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		connections.remove(conn);
		// When connection is closed, remove the user.
		try {
			removeUser(conn);
		} catch (IOException e) {
			e.printStackTrace();
		}

		_log.info("Connection closed to: " + conn.getRemoteSocketAddress().getHostString());
		System.out.println("Closed connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			Message msg = mapper.readValue(message, Message.class);

			switch (msg.getType()) {
			case USER_JOINED:
				addUser(new ChatUser(msg.getChatUser().getName()), conn);
				break;
			case USER_LEFT:
				removeUser(conn);
				break;
			case TEXT_MESSAGE:
				broadcastMessage(msg);
			}

			System.out.println(
					"Message from user: " + msg.getChatUser() + ", text: " + msg.getData() + ", type:" + msg.getType());
			_log.info("Message from user: " + msg.getChatUser() + ", text: " + msg.getData());
		} catch (IOException e) {
			_log.error("Wrong message format.");
			// return error message to user
		}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {

		if (conn != null) {
			connections.remove(conn);
		}
		assert conn != null;
		System.out.println("ERROR from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
	}

	private void broadcastMessage(Message msg) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			String messageJson = mapper.writeValueAsString(msg);
			for (WebSocket sock : connections) {
				sock.send(messageJson);
			}
		} catch (IOException e) {
			_log.error("Cannot convert message to json.");
		}
	}

	private void addUser(ChatUser user, WebSocket conn) throws IOException {
		users.put(conn, user);
		acknowledgeUserJoined(user, conn);
		broadcastUserActivityMessage(MessageType.USER_JOINED);
	}

	private void removeUser(WebSocket conn) throws IOException {
		users.remove(conn);
		broadcastUserActivityMessage(MessageType.USER_LEFT);
	}

	private void acknowledgeUserJoined(ChatUser user, WebSocket conn) throws IOException {
		Message message = new Message();
		message.setType(MessageType.USER_JOINED_ACK);
		message.setChatUser(user);
		conn.send(new ObjectMapper().writeValueAsString(message));
	}

	private void broadcastUserActivityMessage(MessageType messageType) throws IOException {

		Message newMessage = new Message();

		ObjectMapper mapper = new ObjectMapper();
		String data = mapper.writeValueAsString(users.values());
		newMessage.setData(data);
		newMessage.setType(messageType);
		broadcastMessage(newMessage);
	}
	
	private static final Log _log = LogFactoryUtil.getLog(ChatServerImpl.class);
}