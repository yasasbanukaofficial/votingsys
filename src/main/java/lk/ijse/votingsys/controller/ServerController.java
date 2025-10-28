package lk.ijse.votingsys.controller;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerController {
    private Socket clientSocket;
    private ObjectInputStream objectIS;
    private ObjectOutputStream objectOS;
    private final boolean isConnected = true;

    public void startConnection() {
        new Thread(() -> {
            while (isConnected) {
                try (ServerSocket serverSocket = new ServerSocket(3000)){
                    System.out.println("Server Started");
                    System.out.println("Waiting for Client on port: 3000");

                    clientSocket = serverSocket.accept();
                    System.out.println("Client Connected");

                    objectIS = new ObjectInputStream(clientSocket.getInputStream());
                    objectOS = new ObjectOutputStream(clientSocket.getOutputStream());
                } catch (Exception e) {
                    System.out.println("Client Disconnected");
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
