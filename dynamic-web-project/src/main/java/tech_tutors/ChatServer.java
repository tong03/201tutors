package tech_tutors;

import java.io.IOException;
import java.util.Vector;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;

// Base web socket code from lecture files
@ServerEndpoint("/ChatServer")
public class ChatServer {

	private static Vector<Session> sessionVector = new Vector<Session>();
	
	@OnOpen
	public void open(Session session) {
		System.out.println("Opening WebSocket");
		sessionVector.add(session);
	}
	
	@OnMessage
	public void onMessage(String message, Session session) {
		System.out.println(message);
		Gson gson = new Gson();
		SocketMessage msg = gson.fromJson(message, SocketMessage.class);
		Message dbMessage = DBHandler.sendMessage(msg.chatID, msg.sentByTutor, msg.content);
		try {
			synchronized(sessionVector) {
				for(Session s : sessionVector) {
					s.getBasicRemote().sendText(gson.toJson(dbMessage));
				}
			}
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
			close(session);
		}
	}
	
	@OnClose
	public void close(Session session) {
		System.out.println("Disconnecting WebSocket");
		sessionVector.remove(session);
	}
	
	@OnError
	public void error(Throwable error) {
		error.printStackTrace();
		System.out.println("Error!");
	}
}

class SocketMessage {
	public int chatID;
	public boolean sentByTutor;
	public String content;
}