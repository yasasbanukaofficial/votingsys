package lk.ijse.votingsys.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import lk.ijse.votingsys.dto.VoteDTO;

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
    private VoteDTO voteDTO = new VoteDTO();
    private boolean isConnected = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectToServer();
    }

    private void connectToServer() {
        new Thread(() -> {
            try {
                clientSocket = new Socket("localhost", 3000);
                objectOS = new ObjectOutputStream(clientSocket.getOutputStream());
                objectIS = new ObjectInputStream(clientSocket.getInputStream());
                Platform.runLater(() -> statuslbl.setText("Connected to Server"));

                isConnected = true;
                this.voteDTO = (VoteDTO) objectIS.readObject();
                displayCount();
                new Thread(() -> handleMessages(clientSocket)).start();
            } catch (Exception e) {
                Platform.runLater(() -> statuslbl.setText("Cannot Connect to Server"));
                isConnected = false;
                e.printStackTrace();
            }
        }).start();
    }

    private void displayCount() {
        Platform.runLater(() -> {
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                    new PieChart.Data("Option A", voteDTO.getOptionACount()),
                    new PieChart.Data("Option B", voteDTO.getOptionBCount()),
                    new PieChart.Data("Option C", voteDTO.getOptionCCount())
            );
            voteChart.setData(pieChartData);
        });
    }

    private void handleMessages(Socket clientSocket) {
        new Thread(() -> {
            try {
                while (isConnected && clientSocket.isConnected()) {
                    VoteDTO receivedDTO = (VoteDTO) objectIS.readObject();
                    if (voteDTO != null) {
                        this.voteDTO = receivedDTO;
                        displayCount();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void submitVote(MouseEvent mouseEvent) {
    }
}
