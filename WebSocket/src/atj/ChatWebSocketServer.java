package atj;

import java.io.IOException;

import java.util.concurrent.ConcurrentLinkedQueue;


import javax.enterprise.context.ApplicationScoped;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ApplicationScoped
@ServerEndpoint("/chatEndpoint")
public class ChatWebSocketServer {

	private static final ConcurrentLinkedQueue<Session> sessions = new ConcurrentLinkedQueue<>();

	@OnOpen
	public void onOpen(Session session) {
		sessions.add(session);
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		sessions.remove(session);
	}

	@OnError
	public void onError(Throwable error) {

	}

	@OnMessage
	public void onMessage(String message, Session session) {

		try {
			for (Session aSession : session.getOpenSessions()) {
				if (aSession.isOpen()) {
					aSession.getBasicRemote().sendText(message);
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace(); 
		}
	}
}
