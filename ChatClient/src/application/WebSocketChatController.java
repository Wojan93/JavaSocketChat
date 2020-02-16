package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

public class WebSocketChatController {

	public WebSocketChatController() {
	}

	@FXML
	ImageView imageView;
	@FXML
	TextField textFieldUser;
	@FXML
	TextArea textArea;
	@FXML
	TextField textFieldMessage;
	@FXML
	Button btnSetUser;
	@FXML
	Button btnSendMsg;
	@FXML
	Button btnSendImage;

	private String user;
	private ChatWebSocketClient chatWebSocketClient;
	private ImageWebSocketClient imageWebSocketClient;

	@FXML
	private void initialize() {
		chatWebSocketClient = new ChatWebSocketClient();
		imageWebSocketClient = new ImageWebSocketClient();
		user = textFieldUser.getText();
	}

	@FXML
	private void btnSet_Click() {
		if (textFieldUser.getText().isEmpty()) {
			return;
		}
		user = textFieldUser.getText();
	}

	@FXML
	private void btnSend_Click() {
		chatWebSocketClient.sendMessage(textFieldMessage.getText());
	}
	
	@FXML
	private void btnSendImage_Click() {
		imageWebSocketClient.selectAndSendImage();
	}
	
	public void closeSession(CloseReason closeReason) {

		try {
			chatWebSocketClient.session.close(closeReason);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@ClientEndpoint
	public class ChatWebSocketClient {
		private Session session;

		public ChatWebSocketClient() {
			connectToWebSocket();
		}

		@OnOpen
		public void onOpen(Session session) {
			System.out.println("Connection is opened.");
			this.session = session;
		}

		@OnClose
		public void onClose(CloseReason closeReason) {
			System.out.println("Connection is closed: " + closeReason.getReasonPhrase());
		}

		@OnError
		public void onError(Throwable throwable) {
			System.out.println("Error occured");
			throwable.printStackTrace();
		}

		@OnMessage
		public void onMessage(String message, Session session) {
			System.out.println("Message was received");
			textArea.setText(textArea.getText() + message + "\n");
		}

		private void connectToWebSocket() {
			WebSocketContainer container = ContainerProvider.getWebSocketContainer();
			try {
				URI uri = URI.create("ws://localhost:8080/WebSocket/chatEndpoint");
				container.connectToServer(this, uri);
			} catch (DeploymentException | IOException e) {
				e.printStackTrace();
			}
		}

		public void sendMessage(String message) {
			try {
				System.out.println("Message was sent: " + message);
				session.getBasicRemote().sendText(user + ": " + message);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

	@ClientEndpoint
	public class ImageWebSocketClient {
		private Session session;

		public ImageWebSocketClient() {
			connectToWebSocket();
		}

		@OnOpen
		public void onOpen(Session session) {
			System.out.println("Connection is opened.");
			this.session = session;
		}

		@OnClose
		public void onClose(CloseReason closeReason) {
			System.out.println("Connection is closed: " + closeReason.getReasonPhrase());
		}

		@OnError
		public void onError(Throwable throwable) {
			System.out.println("Error occured");
			throwable.printStackTrace();
		}

		@OnMessage
		public void onMessage(InputStream input) {
			System.out.println("WebSocket message Received!");
			Image image = new Image(input);
			imageView.setImage(image);
		}

		private void connectToWebSocket() {
			WebSocketContainer container = ContainerProvider.getWebSocketContainer();
			try {
				URI uri = URI.create("ws://localhost:8080/WebSocket/imageEndpoint");
				container.connectToServer(this, uri);
			} catch (DeploymentException | IOException e) {
				e.printStackTrace();
			}
		}

		
		private void selectAndSendImage() {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Select Image to Send");
			fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
			File file = fileChooser.showOpenDialog(new Stage()); //btnSendImage.getScene().getWindow()
			try (InputStream input = new FileInputStream(file);
					OutputStream output = session.getBasicRemote().getSendStream()) {
				byte[] buffer = new byte[1024];
				int read;
				while ((read = input.read(buffer)) > 0) {
					output.write(buffer, 0, read);
				}
			} catch (IOException ex) {
				ex.printStackTrace();

			}
		}

	}

}