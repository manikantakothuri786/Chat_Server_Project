package Server;
import Message.Message;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class NodeInfo implements Runnable{

    public static ArrayList<NodeInfo> nodeInfo = new ArrayList<>();
    private Socket socket;
    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;
    private String clientUsername;

    private String iP;
    public NodeInfo(Socket socket, String clientIpAddress)throws IOException, ClassNotFoundException {
        try{
            this.socket = socket;
            this.iP=clientIpAddress;
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            Message message = (Message)objectInputStream.readObject();
            this.clientUsername = (String) message.getContent();
            //Check If User Already joined the chat
            boolean checkUser = false;
            for(NodeInfo nodeInfo_ : nodeInfo){
                if(nodeInfo_.iP.equals(clientIpAddress)){
                    message.setContent("You are already in the Chat Group");
                    this.objectOutputStream.writeObject(message);
                    nodeInfo_.objectOutputStream.flush();
                    checkUser = true;
                }
            }
            if(!checkUser) {
                nodeInfo.add(this);
                message.setContent("SERVER: " + clientUsername + " has entered the chat!");
                broadcastMessage(message);
            }
        }catch(IOException e){
            closeEverything(socket,objectInputStream,objectOutputStream);
        }
    }


    @Override
    public void run()
    {
        while (socket.isConnected()) {
            try {
                Message message2 = (Message)objectInputStream.readObject();
                int type = message2.getType();
                if(type == 3){
                    broadcastMessage(message2);
                }else if (type == 2 || type == 4){
                    removeNodeInfo();
                }else if (type == 5){

                }
            } catch (IOException e) {
                closeEverything(socket,objectInputStream,objectOutputStream);
                break;

            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void broadcastMessage(Message message){
        for(NodeInfo nodeInfo_ : nodeInfo){
            try{
                nodeInfo_.objectOutputStream.writeObject(message);
                nodeInfo_.objectOutputStream.flush();
            }catch (IOException e){
                closeEverything(socket,objectInputStream,objectOutputStream);
            }
        }
    }

    public void removeNodeInfo() {
        nodeInfo.remove(this);
        Message message = new Message(3,"SERVER "+ clientUsername + "has gone from chat!\n");
        broadcastMessage(message);
    }

    public void closeEverything(Socket socket,ObjectInputStream objectInputStream,ObjectOutputStream objectOutputStream){
        removeNodeInfo();
        try{
            if (objectOutputStream != null){
                objectOutputStream.close();
            }
            if (objectInputStream != null){
                objectInputStream.close();
            }
            if (socket !=null){
                socket.close();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}