package cs455.overlay.transport;

import cs455.overlay.node.Node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerThread extends Thread{

    private Node node;
    private ServerSocket serverSocket;
    private int port;

    public TCPServerThread(Node node, int port) throws IOException {
        this.node = node;
        this.port = port;
        serverSocket = new ServerSocket(this.port);
    }


    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                TCPConnection tcpConnection = new TCPConnection(node, socket);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        }
    }
}
