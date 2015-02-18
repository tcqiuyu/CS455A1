package cs455.overlay.transport;

import cs455.overlay.node.Node;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPReceiverThread extends Thread {

    private Socket socket;
    private DataInputStream din;
    private Node node;

    public TCPReceiverThread(Node node, Socket socket) throws IOException {
        this.socket = socket;
        din = new DataInputStream(this.socket.getInputStream());
        this.node = node;
    }

    @Override
    public void run() {
        int dataLength;
        while (socket != null) {
            try {
                dataLength = din.readInt();
                byte[] data = new byte[dataLength];
                din.readFully(data, 0, dataLength);
                Event event = EventFactory.getInstance().getEvent(data);
                node.onEvent(event);
            } catch (IOException ioe) {
//                System.out.println(ioe.getMessage());
                System.out.println("Disconnected.");
                break;
            }

        }
    }

}
