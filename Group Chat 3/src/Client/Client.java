package Client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import Message.Message;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static java.lang.System.exit;

public class Client {
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private String username;


    public Client(Socket socket, String username) {
        try{
            this.socket = socket;
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            this.username = username;
        }catch (IOException e){
            closeEverything();
        }
    }
    public void sendMessage(){
        try{
            int choice = 3;
            Message message = new Message(3,this.username);
            this.objectOutputStream.writeObject(message);
            this.objectOutputStream.flush();
            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()){
                System.out.println("Pop up Message : You are in Chat mode press 0 to select other option");
                String messageToSend = scanner.nextLine();
                if(messageToSend.equals("0")){
                    choice = menu();
                    System.out.println(choice);
                    if (choice == 3){
                        messageToSend = scanner.nextLine();
                    }else if(choice == 2){
                        String newMessage = username +" : "+ messageToSend;
                        Message send = new Message(choice,newMessage);
                        this.objectOutputStream.writeObject(send);
                        this.objectOutputStream.flush();
                        break;
                    }else if(choice == 4  || choice == 5){
                        String newMessage = username +" : "+ messageToSend;
                        Message send = new Message(choice,newMessage);
                        this.objectOutputStream.writeObject(send);
                        this.objectOutputStream.flush();
                        break;
                    }
                }
                String newMessage = username +" : "+ messageToSend;
                Message send = new Message(choice,newMessage);
                this.objectOutputStream.writeObject(send);
                this.objectOutputStream.flush();
            }
            WhatDo(choice);
        }catch(IOException e){
            closeEverything();
        }
    }
    public void WhatDo(int choice) throws IOException {
        System.out.println("Going To ShutDown");
        if(choice == 2){
            Leave();
        }else if (choice == 4  || choice == 5){
            System.out.println("Going To ShutDown");
            exit(1);
        }

    }
    public void Leave() throws IOException {
        System.out.println("Press 1 to Rejoin");
        System.out.println("Press 2 to Quite");
        Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine();
        while(Integer.parseInt(choice) != 1 && Integer.parseInt(choice) != 2 ){
            System.out.println("Wrong Input Please press 1 for Rejoin or Press 2 for Quite");
        }
        if (Integer.parseInt(choice) == 1){
            System.out.println("Please Enter your name");
            String username = scanner.nextLine();
            Socket socket = new Socket("localhost", 4444);
            Client client = new Client(socket,username);
            client.listenForMessage();
            client.sendMessage();
        }
        else{
            exit(1);
        }

    }
    public int menu(){
        System.out.println(" ");
        System.out.println(" ");
        System.out.println(" ");
        System.out.println("Welcome to Client side Menu");
        System.out.println("Press 2 to Leave the Chat");
        System.out.println("Press 3 to Continue Chatting");
        System.out.println("Press 4 to Shutdown the Client");
        System.out.println("Press 5 to Shutdown the server");
        System.out.println("Any Other Key will be consider as Opt 3 (That you want to Continue Chat");
        Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine();
        if(choice.equals("2") || choice.equals("3")|| choice.equals("4") || choice.equals("5")){
            return Integer.parseInt(choice);
        }else
            return 3;
    }
    public void listenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(socket.isConnected()){
                    try{
                        Message message = (Message)objectInputStream.readObject();
                        if(message.getContent().equals("You are already in the Chat Group")){
                            System.out.println("You are already in the Chat Group");
                            exit(1);
                        }
                        System.out.println(message.getContent());
                        objectOutputStream.flush();
                    }catch (IOException e){
                        closeEverything();
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }
    public void closeEverything(){
        try{
            if (this.objectOutputStream != null){
                this.objectOutputStream.close();
            }
            if (this.objectInputStream != null){
                this.objectInputStream.close();
            }
            if (this.socket !=null){
                this.socket.close();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("You are not Connected to Any Server Please press 1 to JOIN a server");
        String Choice = scanner.nextLine();
        while(Integer.parseInt(Choice) != 1){
            System.out.println("Wrong Input Please Press 1...");
        }

//        ///////////////////////////////////////////////
        BufferedReader reader;
        String serverip = null;
        int port = 2222;
        try {
            boolean ip = false;
            reader = new BufferedReader(new FileReader("Properties.txt"));
            String line = reader.readLine();
            serverip = line;
            line = reader.readLine();
            port = Integer.parseInt(line);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        //////////////////////////////////////////////////


        System.out.println("Please Enter your name");
        String username = scanner.nextLine();
        Socket socket = new Socket(serverip, port);
        Client client = new Client(socket,username);
        client.listenForMessage();
        client.sendMessage();
    }

}
