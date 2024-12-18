package Projects.GroupChat;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private ServerSocket serverSocket;
    private ExecutorService clientThreadPool;
    private Thread commandThread;
    private boolean serverRunning;

    private Server(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
            this.clientThreadPool = Executors.newFixedThreadPool(1); // Adjust the pool size as needed
            this.serverRunning = true;

            System.out.println("Server is running...");
            startServer();

        } catch (Exception e) {
            closeServer();
        }
    }

    private void startServer() {
        commandThread = new Thread(this::handleCommands);
        commandThread.start();

        while (serverRunning) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("A new user has connected\n");

                clientThreadPool.submit(() -> {
                    try {
                        ClientHandler clientHandler = new ClientHandler(socket);
                        Thread thread = new Thread(clientHandler);
                        thread.start();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                });

            } catch (IOException e) {
                if (!serverSocket.isClosed()) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    private void handleCommands() {
        Scanner scanner = new Scanner(System.in);
        while (serverRunning) {
            System.out.println("Enter \"close\" to switch of the server.");
            String command = scanner.nextLine();
            if (command.equalsIgnoreCase("close")) {
                closeServer();
            } else {
                System.out.println("Unknown command: " + command);
            }
        }
    }

    private void closeServer() {
        try {


            for (ClientHandler client : ClientHandler.clients) {
                try {
                    DataOutputStream dataOutputStream = new DataOutputStream(client.socket.getOutputStream());
                    dataOutputStream.writeUTF("\nServer has been closed.");
                    client.socket.close();

                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }

            }
            ClientHandler.clients.clear();
            serverRunning = false;

            serverSocket.close();
            clientThreadPool.shutdown();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        Server server = new Server(9876);
    }
}
