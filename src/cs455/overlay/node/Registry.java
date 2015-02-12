package cs455.overlay.node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
		while ((temp = reader.readLine()) != null) {

			if(temp.indexOf("eof") != -1){
				break;
			} else{
				System.out.println(temp);
			}
		}
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

	private void printHelp() {
		// TODO
	}

	private void listMessagingNode() {
		// TODO
	}

	private void setupOverlay(int overlayNum) {
		// TODO
	}

	private void listRoutingTables() {
		// TODO
	}

	private void start(int messageNum) {
		// TODO
	}

}
