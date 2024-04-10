package emk4;

import com.google.gson.Gson;
import emk4.Interfaces.Handleable;
import emk4.JSON.ClientNetInfo;
import emk4.JSON.LangNetInfo;
import java.io.*;
import java.net.*;
import java.util.*;

public class LangServer {

    private final Socket langSocket;
    private final Scanner in;
    private final PrintWriter out;
    private final String langCode;
    private final int port;

    public LangServer(Socket socket, String langCode, int port) throws IOException {
        langSocket = socket;
        this.langCode = langCode;
        this.port = port;
        in = new Scanner(langSocket.getInputStream());
        out = new PrintWriter(langSocket.getOutputStream(), true);
    }

    private void register(){
        System.out.println(in.nextLine());
        String netInfo = new Gson().toJson(new LangNetInfo(
                langCode,
                langSocket.getInetAddress().getHostAddress(),
                port
        ));
        out.println(netInfo);
        System.out.println(in.nextLine());
    }

    private static class TranslateHandler implements Runnable, Handleable {

        Socket translateSocket;
        Scanner in;
        PrintWriter out;

        public TranslateHandler(Socket socket) throws IOException{
            translateSocket = socket;
            in = new Scanner(translateSocket.getInputStream());
            out = new PrintWriter(translateSocket.getOutputStream(), true);
        }

        public void send(ClientNetInfo clientNetInfo, ResourceBundle dictionary) throws IOException{
            Socket sendSocket = new Socket(clientNetInfo.ipAddress, clientNetInfo.port);
            PrintWriter out = new PrintWriter(sendSocket.getOutputStream(), true);
            if (!dictionary.containsKey(clientNetInfo.word)) {
                out.println(HttpURLConnection.HTTP_NOT_FOUND);
            } else {
                out.println("Translated word: " + dictionary.getString(clientNetInfo.word));
            }
            sendSocket.close();
        }

        @Override
        public void run() {
            while(in.hasNext()){
                String clientJSON = in.nextLine();
                System.out.println(clientJSON);
                ClientNetInfo clientNetInfo = new Gson().fromJson(clientJSON, ClientNetInfo.class);
                ResourceBundle dictionary = ResourceBundle.getBundle("Translations_" + clientNetInfo.langCode);
                try{
                    send(clientNetInfo, dictionary);
                }catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        public void handleRequest() throws InterruptedException {
            Thread thread = new Thread(this);
            thread.start();
            thread.join();
        }

        @Override
        public void close() throws IOException {
            out.close();
            in.close();
            translateSocket.close();
        }
    }

    private void close() throws IOException {
        in.close();
        out.close();
        langSocket.close();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = Integer.parseInt(args[1]);
        String ipAddress = "127.0.0.1";
        Socket socket = new Socket(ipAddress, ProxyServer.port);
        LangServer langServer = new LangServer(socket, args[0], port);
        langServer.register();
        langServer.close();
        ServerSocket langServerSocket = new ServerSocket(port);
        while(true){
            System.out.println("=-=-=-=-=-=-==-=-=-=-=-=-==-=-=-=-=-=-=");
            System.out.println("Waiting for translation...");
            Socket translateSocket = langServerSocket.accept();
            TranslateHandler translateHandler = new TranslateHandler(translateSocket);
            translateHandler.handleRequest();
            translateHandler.close();
        }
    }



}
