package Projects.GroupChat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    public static ArrayList<ClientHandler> clients = new ArrayList<>();
    public Socket socket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private String clientName;

    public ClientHandler(Socket socket)
    {
        this.socket = socket;
        try {
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());
            clientName = inputStream.readUTF();
            clients.add(this);
            broadCastMessage("\n"+clientName+" has joined the room.");

        }
        catch (Exception e)
        {
            closeSocket(socket,outputStream,inputStream);
        }
    }

    private void broadCastMessage(String msg) {


            if(!clients.isEmpty()) {
                clients.forEach((i) -> {
                    if (i!=null && !i.socket.isClosed() && !i.equals(this)) {
                        try {

                            i.outputStream.writeUTF("\n"+msg);
                            i.outputStream.flush();

                        } catch (Exception e) {
                            closeSocket(socket, outputStream, inputStream);
                        }

                    }
                });

            }


    }

    private void closeSocket(Socket socket, DataOutputStream outputStream, DataInputStream inputStream) {
        try {
           leftRoom();
            if(outputStream!=null)
            {
                outputStream.close();
            }
            if(inputStream!=null)
            {
                inputStream.close();
            }
            if (socket!=null)
            {
                socket.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        String msg = "";
        while (socket.isConnected())
        {
            try {
                msg = inputStream.readUTF();
                broadCastMessage(clientName+": "+msg);
            }
            catch (Exception e)
            {
                closeSocket(socket,outputStream,inputStream);
                break;
            }
        }
        

    }
    private void leftRoom()
    {

        broadCastMessage(clientName+" has left the room.");
        clients.remove(this);

    }


}
