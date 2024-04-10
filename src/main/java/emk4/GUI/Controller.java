package emk4.GUI;

import emk4.Client;
import emk4.ProxyServer;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Controller {

    private final View view;


    public Controller(View view) {
        this.view = view;
    }

    public void initView(){
        view.getButton().addActionListener(e -> {
            String[] clientInput = view.getTextArea().getText().split(" ");
            String ipAddress = clientInput[0];
            int port = Integer.parseInt(clientInput[1]);
            try {
                Socket socket = new Socket(ipAddress, ProxyServer.port);
                Client client = new Client(socket, port);
                int httpCode = client.translate(clientInput[2], clientInput[3]);
                client.close();
                if(httpCode != 200) {
                    view.getResultArea().setText("Something went wrong.");
                    return;
                }
                System.out.println("=-=-=-=-=-=-==-=-=-=-=-=-==-=-=-=-=-=-=");
                System.out.println("Waiting for response...");
                ServerSocket serverSocket = new ServerSocket(port);
                Socket receiveSocket = serverSocket.accept();
                Client.ReceiveHandler receiveHandler = new Client.ReceiveHandler(receiveSocket);
                receiveHandler.handleRequest();
                view.getResultArea().setText(receiveHandler.received);
                receiveSocket.close();
                serverSocket.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }


        });
    }

}
