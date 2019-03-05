package atj;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;

import javax.websocket.OnClose;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class WebSocketChatStageControler {
	@FXML
	TextField userTextField;
	@FXML
	TextArea chatTextArea;
	@FXML
	TextField messageTextField;
	@FXML
	Button btnSet;
	@FXML
	Button btnSend;
	@FXML
	Button btnAttach;

	private String user;
	private WebSocketClient webSocketClient;
	private File fileToSend;
	private boolean fileAttach;

	@FXML
	private void initialize() {
		webSocketClient = new WebSocketClient();
		user = userTextField.getText();
		fileToSend = null;
		fileAttach = false;
	}

	@FXML
	private void btnSet_Click() {
		if (userTextField.getText().isEmpty() || user.equals(userTextField.getText())) {
			return;
		}
		user = userTextField.getText();

	}

	@FXML
	private void btnSend_Click() {
		if (messageTextField.getText().isEmpty())
			return;
		webSocketClient.sendMessage(messageTextField.getText());
		messageTextField.clear();
	}

	@FXML
	private void textFieldEnter(KeyEvent e) {
		if (e.getCode() == KeyCode.ENTER) {
			btnSend_Click();
		}
	}

	@FXML
	private void btnAttach_Click() {

		Stage stage = new Stage();
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Attache file");
		fileToSend = chooser.showOpenDialog(stage);
		messageTextField.insertText(0, "File to send: " + fileToSend.getName() + "  ");
		fileAttach = true;
	}

	public void closeSession(CloseReason closeReason) {
		try {
			webSocketClient.session.close(closeReason);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@ClientEndpoint
	public class WebSocketClient {
		private Session session;

		public WebSocketClient() {
			connectToWebSocket();
		}

		@OnOpen
		public void onOpen(Session session) {
			System.out.println("Connection is opened");
			this.session = session;
		}

		@OnClose
		public void onClose(CloseReason closeReason) {
			System.out.println("Connection is closed: " + closeReason.getReasonPhrase());
		}

		@OnError
		public void onError(Throwable throwable) {
			System.out.println("Error");
			throwable.printStackTrace();
		}

		@OnMessage
		public void onMessage(String message, Session session) {
			System.out.println("Message was received");
			chatTextArea.setText(chatTextArea.getText() + message + "\n");
		}

		@OnMessage
		public void onMessage(ByteBuffer fileBuffer, Session session) {
			System.out.println("File was received");

			try {
				FileChat manager = new FileChat();
				Platform.runLater(() -> manager.downloadFile(fileBuffer));

			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}

		private void connectToWebSocket() {
			WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
			try {
				URI uri = URI.create("ws://localhost:8080/WebSocketEndpoint/websocketendpoint");
				webSocketContainer.connectToServer(this, uri);
			} catch (DeploymentException | IOException e) {
				e.printStackTrace();
			}
		}

		public void sendMessage(String message) {
			if (fileAttach == false) {
				try {
					session.getBasicRemote().sendText(user + ": " + message);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			} else {
				try {
					ByteBuffer fileBuffer = ByteBuffer.allocateDirect((int) fileToSend.length());
					InputStream iStream = new FileInputStream(fileToSend);

					int Byte;
					while ((Byte = iStream.read()) != -1) {
						fileBuffer.put((byte) Byte);
					}
					iStream.close();
					fileBuffer.flip();

					session.getBasicRemote().sendText(user + " - send file: " + fileToSend.getName());
					session.getBasicRemote().sendBinary(fileBuffer);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				fileAttach = false;
				fileToSend = null;
			}
		}
	} // public class WebSocketClient
} // public class WebSocketChatStageControler