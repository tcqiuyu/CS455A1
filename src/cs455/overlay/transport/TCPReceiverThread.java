package cs455.overlay.transport;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import cs455.overlay.node.Node;

public class TCPReceiverThread implements Runnable {

	private Socket socket;
	private DataInputStream din;

	public TCPReceiverThread(Node n, Socket s) throws IOException {
		this.socket = s;
		din = new DataInputStream(socket.getInputStream());
		ServerSocket server = new ServerSocket(0);
		
	}

	@Override
	public void run() {
		int dataLength;
		while (socket != null) {
			try {
				dataLength = din.readInt();

				byte[] data = new byte[dataLength];
				din.readFully(data, 0, dataLength);

			} catch (SocketException se) {
				System.out.println(se.getMessage());
			} catch (IOException ioe) {
				System.out.println(ioe.getMessage());
			}

		}
	}

}
