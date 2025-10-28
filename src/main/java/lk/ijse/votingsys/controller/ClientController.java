package lk.ijse.votingsys.controller;

import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    public TextField usernameTxt;
    public RadioButton rbA;
    public RadioButton rbB;
    public RadioButton rbC;
    public Button submitBtn;
    public PieChart voteChart;
    public Label statuslbl;

    private Socket clientSocket;
    private ObjectInputStream objectIS;
    private ObjectOutputStream objectOS;
    private final boolean isConnected = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectToServer();
    }

    private void connectToServer() {
        new Thread(() -> {
            while(isConnected) {
                try {
                    if (clientSocket == null || clientSocket.isClosed()) {
                        clientSocket = new Socket("localhost", 3000);
                        objectIS = new ObjectInputStream(clientSocket.getInputStream());
                        objectOS = new ObjectOutputStream(clientSocket.getOutputStream());
                        statuslbl.setText("Connected to Server");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void submitVote(MouseEvent mouseEvent) {
    }
}
