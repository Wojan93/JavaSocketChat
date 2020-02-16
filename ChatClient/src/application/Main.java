package application;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;

public class Main extends Application {
	
	WebSocketChatController wscc = new WebSocketChatController();
	
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("WebSocketClient.fxml"));
			
			WebSocketChatController controller = fxmlLoader.getController();
			fxmlLoader.setController(controller);
			AnchorPane root = fxmlLoader.load();
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setTitle("JavaFx");
			primaryStage.setOnHiding(e -> primaryStage_Hiding(e, fxmlLoader));
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void primaryStage_Hiding(WindowEvent e, FXMLLoader fxmlLoader) {
		((WebSocketChatController) fxmlLoader.getController())
				.closeSession(new CloseReason(CloseCodes.NORMAL_CLOSURE, "Stage is hiding"));
	}

	public static void main(String[] args) {
		launch(args);
	}
}
