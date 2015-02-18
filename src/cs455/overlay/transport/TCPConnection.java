package cs455.overlay.transport;

import cs455.overlay.node.Node;

import java.io.IOException;
import java.net.Socket;

public class TCPConnection {

    TCPReceiverThread receiver;
    TCPSender sender;
    private Socket socket;

    public TCPConnection(Node node, Socket socket) throws IOException {

        this.receiver = new TCPReceiverThread(node, socket);
        this.sender = new TCPSender(socket);
        this.socket = socket;
//        ConnectionFactory.getInstance().registerConnection(socket.getInetAddress().getHostAddress(), socket.getPort(), this);
        receiver.start();
    }

    public synchronized void sendData(byte[] data) throws IOException {
        sender.sendData(data);
    }

    public Socket getSocket() {
        return socket;
    }
}

