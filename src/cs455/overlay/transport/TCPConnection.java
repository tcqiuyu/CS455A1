package cs455.overlay.transport;

import java.io.IOException;
import java.net.Socket;

import cs455.overlay.node.Node;

public class TCPConnection {

	TCPReceiverThread receiver;
	TCPSender sender;
	
	public TCPConnection(Node node, Socket socket) throws IOException{
		
		this.receiver = new TCPReceiverThread(node, socket);
		this.sender = new TCPSender(socket);
		
	}
	
}
