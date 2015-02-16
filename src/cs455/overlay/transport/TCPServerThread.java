package cs455.overlay.transport;

import cs455.overlay.node.Node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerThread extends Thread {

    private Node node;
    private ServerSocket serverSocket;
    private int localPort;

    public TCPServerThread(Node node) {
        this.node = node;

        try {
            serverSocket = new ServerSocket(0);
            this.localPort = serverSocket.getLocalPort();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public TCPServerThread(Node node, int port) throws IOException {
        this.node = node;
        this.localPort = port;
        serverSocket = new ServerSocket(this.localPort);
    }

    public int getLocalPort() {
        return localPort;
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
