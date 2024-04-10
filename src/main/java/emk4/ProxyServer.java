package emk4;

import com.google.gson.Gson;
import emk4.Interfaces.Handleable;
import emk4.JSON.ClientNetInfo;
import emk4.JSON.LangNetInfo;
import emk4.JSON.NetInfo;
import java.io.*;
import java.net.*;
import java.util.*;

public class ProxyServer {

    private static final Map<String, LangNetInfo> langServers = new HashMap<>();
    public static final int port = 4321;
    private static class RequestHandler implements Runnable, Handleable {
        Socket requestSocket;
        Scanner in;
        PrintWriter out;

        public RequestHandler(Socket socket) throws IOException {
            requestSocket = socket;
            in = new Scanner(requestSocket.getInputStream());
            out = new PrintWriter(requestSocket.getOutputStream(), true);
        }
        @Override
        public void close() throws IOException {
            out.close();
            in.close();
            requestSocket.close();
        }

        public void send(LangNetInfo langNetInfo, String translateRequest) throws IOException {
            Socket sendSocket = new Socket(langNetInfo.ipAddress, langNetInfo.port);
            PrintWriter out = new PrintWriter(sendSocket.getOutputStream(), true);
            out.println(translateRequest);
            sendSocket.close();
        }

        @Override
        public void run(){
            out.println("Connection established");
            while(in.hasNext()) {
                String receivedJSON = in.nextLine();
                NetInfo requestInfo = new Gson().fromJson(receivedJSON, NetInfo.class);
                switch (requestInfo.type) {
                    case "Register request":
                        LangNetInfo netInfo = new Gson().fromJson(receivedJSON, LangNetInfo.class);
                        langServers.put(netInfo.langCode, netInfo);
                        System.out.println(langServers);
                        out.println(HttpURLConnection.HTTP_OK);
                        break;
                    case "Translate request":
                        ClientNetInfo clientInfo = new Gson().fromJson(receivedJSON, ClientNetInfo.class);
                        if (!langServers.containsKey(clientInfo.langCode)){
                            out.println(HttpURLConnection.HTTP_NOT_FOUND);
                            return;
                        }else{
                            out.println(HttpURLConnection.HTTP_OK);
                            System.out.println(clientInfo);
                            LangNetInfo langNetInfo = langServers.get(clientInfo.langCode);
                            String translateRequest = new Gson().toJson(clientInfo);
                            try {
                                send(langNetInfo, translateRequest);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        break;
                }
            }
        }
        @Override
        public void handleRequest() throws InterruptedException {
            Thread thread = new Thread(this);
            thread.start();
            thread.join();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket proxySocket = new ServerSocket(port);
        while(true) {
            System.out.println("=-=-=-=-=-=-==-=-=-=-=-=-==-=-=-=-=-=-=");
            System.out.println("Waiting for request...");
            Socket requestSocket = proxySocket.accept();
            RequestHandler registerHandler = new RequestHandler(requestSocket);
            registerHandler.handleRequest();
            registerHandler.close();
        }
    }

}
