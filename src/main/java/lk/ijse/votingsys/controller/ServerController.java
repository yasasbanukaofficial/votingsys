package lk.ijse.votingsys.controller;

import lk.ijse.votingsys.dto.VoteDTO;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerController {
    private final CopyOnWriteArrayList<ObjectOutputStream> clientOutputStreams = new CopyOnWriteArrayList<>();
    private ObjectInputStream objectIS;
    private ObjectOutputStream objectOS;
    private VoteDTO voteDto = new VoteDTO();
    private boolean isConnected = true;

    public void startConnection() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(3000)){
                System.out.println("Server Started");
                System.out.println("Waiting for Client on port: 3000");
                while (isConnected) {
                    Socket clientSocket = serverSocket.accept();
                    objectOS = new ObjectOutputStream(clientSocket.getOutputStream());
                    objectIS = new ObjectInputStream(clientSocket.getInputStream());
                    System.out.println("Client Connected");

                    clientOutputStreams.add(objectOS);
                    objectOS.writeObject(voteDto);
                    objectOS.flush();

                    new Thread(() -> handleClient(clientSocket)).start();
                }
            } catch (Exception e) {
                System.out.println("Client Disconnected");
                e.printStackTrace();
            }
        }).start();
    }

    private void handleClient(Socket clientSocket) {
        new Thread(() -> {
            try {
                while (!clientSocket.isClosed()) {
                    VoteDTO receivedDTO = (VoteDTO) objectIS.readObject();
                    voteDto.addCount(receivedDTO.getOption());
                    broadcastVotes();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void broadcastVotes(){
        for (ObjectOutputStream cos : clientOutputStreams) {
            try {
                cos.reset();
                cos.writeObject(voteDto);
                cos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
