package cs455.overlay.node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;

import cs455.overlay.wireformats.Event;

public class Registry implements Node, Runnable {

	Socket socket;

	public Registry() {
		// TODO Auto-generated constructor stub

	}

	private void setSocket(Socket socket) {
		this.socket = socket;
	}

	private void handleSocket() throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String temp;
		int index;
		while ((temp = reader.readLine()) != null) {

			if((index = temp.indexOf("eof")) != -1){
				break;
			} else{
				System.out.println(temp);
			}
		}
		  Writer writer = new OutputStreamWriter(socket.getOutputStream());
	         writer.write("Hello Client.\n");
	         writer.write("eof\n");
	         writer.flush();
	         writer.close();
	         reader.close();
	         socket.close();
		
	}

	@Override
	public void onEvent(Event e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			handleSocket();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String args[]) throws IOException {
		int port = 8899; 

		ServerSocket server = new ServerSocket(port);
		while (true) {
			Socket socket = server.accept();
			Registry reg = new Registry();
			reg.setSocket(socket);
			new Thread(reg).start();
			
		}

	}

	public void printusg() {
		// TODO
	}

	public void listMessagingNode() {
		// TODO
	}

	public void setupOverlay(int overlayNum) {
		// TODO
	}

	public void listRoutingTables() {
		// TODO
	}

	public void start(int messageNum) {
		// TODO
	}

}
