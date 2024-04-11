package emk4;

import com.google.gson.Gson;
import emk4.GUI.Controller;
import emk4.GUI.View;
import emk4.Interfaces.Handleable;
import emk4.JSON.ClientNetInfo;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    private final Socket clientSocket;
    private final Scanner in;
    private final PrintWriter out;
    private final int port;

    public Client(Socket clientSocket, int port) throws IOException {
        this.clientSocket = clientSocket;
        this.port = port;
        in = new Scanner(clientSocket.getInputStream());
        out = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    public int translate(String word, String langCode){
        System.out.println(in.nextLine());
        System.out.println("To translate: " + word);
        String clientInfo = new Gson().toJson(new ClientNetInfo(
                word,
                langCode,
                clientSocket.getInetAddress().getHostAddress(),
                port
        ));
        out.println(clientInfo);
        int httpCode = in.nextInt();
        System.out.println(httpCode);
        return httpCode;
    }

    public void close() throws IOException{
        in.close();
        out.close();
        clientSocket.close();
    }

    public static class ReceiveHandler implements Handleable {

        Socket receiveSocket;
        Scanner in;
        public String received;

        public ReceiveHandler(Socket socket) throws IOException {
            receiveSocket = socket;
            in = new Scanner(socket.getInputStream());
        }

        @Override
        public void handleRequest(){
            while (in.hasNext()){
                received = in.nextLine();
                System.out.println(received);
            }
        }

        @Override
        public void close() throws IOException {
            in.close();
            receiveSocket.close();
        }
    }

    public static void main(String[] args) throws IOException{
        if (args.length == 0) {
            initWithGUI();
        }else{
            initUsingArguments(args);
        }
    }

    public static void initWithGUI(){
        View view = new View();
        Controller controller = new Controller(view);
        controller.initView();
    }

    public static void initUsingArguments(String[] args) throws IOException {
        int port = Integer.parseInt(args[2]);
        String ipAddress = "127.0.0.1";
        Socket socket = new Socket(ipAddress, ProxyServer.port);
        Client client = new Client(socket, port);
        int httpCode = client.translate(args[0], args[1]);
        client.close();
        if (httpCode != 200) return;
        ServerSocket serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(5000);
        System.out.println("=-=-=-=-=-=-==-=-=-=-=-=-==-=-=-=-=-=-=");
        System.out.println("Waiting for response...");
        Socket receiveSocket = serverSocket.accept();
        ReceiveHandler receiveHandler = new ReceiveHandler(receiveSocket);
        receiveHandler.handleRequest();
        receiveSocket.close();
    }

}
