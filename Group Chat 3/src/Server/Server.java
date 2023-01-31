package Server;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                InetSocketAddress InetSocketAddress = null;
                InetSocketAddress socketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();

                String clientIpAddress = socketAddress.getAddress().getHostAddress();
                System.out.println(clientIpAddress);
                NodeInfo nodeInfo = new NodeInfo(socket, clientIpAddress);

                Thread thread = new Thread(nodeInfo);
                thread.start();

            }
        }catch (IOException e){
            closeServerSocket();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null){
                serverSocket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        ServerSocket serverSocket = new ServerSocket(4444);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}






























//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.net.ServerSocket;
//import java.net.Socket;
//
//public class Server {
//    public static final int PORT = 4444;
//    public static void main(String[] args) throws IOException, ClassNotFoundException {
//        new Server().runServer();
//    }
//
//    public void runServer() throws IOException, ClassNotFoundException{
//        ServerSocket serverSocket = new ServerSocket(PORT);
//        System.out.println("Server up & ready for connection...");
//        Socket socket = serverSocket.accept();
//        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
//        ObjectOutputStream objectOutputStream = new ObjectOutputStream((socket.getOutputStream()));
//        Message message = (Message)objectInputStream.readObject();
//        doSomething(message);
//        objectOutputStream.writeObject(message);
//        socket.close();
//    }
//    private void doSomething(Message message){
//        message.setContent("Its all ok");
//    }
//}
