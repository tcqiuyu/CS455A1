package cs455.overlay.transport;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class TCPReceiverThread extends TCPConnection implements Runnable {

	private Socket socket;
	private DataInputStream din;

	public TCPReceiverThread(Socket s) throws IOException {
		this.socket = s;
		din = new DataInputStream(socket.getInputStream());
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
