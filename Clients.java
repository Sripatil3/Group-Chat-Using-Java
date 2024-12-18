package Projects.GroupChat;

import java.io.*;
import java.net.Socket;


public class Clients
{
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Socket socket;
    private String name;
    private Clients(String iP,int port) {
        try
        {
            socket = new Socket(iP,port);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            enterInRoom();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

    }

    private void enterInRoom()  {
        new Thread(()->{
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("Enter username: ");
                this.name = reader.readLine();
                outputStream.writeUTF(name);
                outputStream.flush();

                while (socket.isConnected())
                {
                    System.out.print("You: ");
                    String msg = reader.readLine();
                    outputStream.writeUTF(msg);
                    outputStream.flush();
//                    if(!socket.getChannel().isOpen()){
//
//                        closeConnection(socket,inputStream,outputStream);
//                        break;
//                    }
                }
            }
            catch (Exception e)
            {
                closeConnection(socket,inputStream,outputStream);
            }

        }).start();

    }
    private void messageFromServer()
    {
        new Thread(()->{
            String msg="";
            while (socket.isConnected())
            {
                try {
                    msg = inputStream.readUTF();
                    System.out.println(msg);
//                    if(!socket.getChannel().isOpen()){
//                        closeConnection(socket,inputStream,outputStream);
//                        break;
//                    }

                }
                catch (Exception e)
                {
                    closeConnection(socket,inputStream,outputStream);
                }

            }
        }).start();
    }

    private void closeConnection(Socket socket, DataInputStream inputStream, DataOutputStream outputStream) {

        try
        {
            if(inputStream!=null)
            {
                inputStream.close();
            }
            if(outputStream!=null)
            {
                outputStream.close();
            }
            if(socket!=null)
            {
                socket.close();
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        Clients clients = new Clients("127.0.0.1",9876);
        clients.messageFromServer();
    }
}
