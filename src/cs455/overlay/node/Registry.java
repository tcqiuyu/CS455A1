package cs455.overlay.node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.wireformats.Event;

public class Registry implements Node{

	private Socket socket;

	private TCPServerThread tcpServer;
	private InteractiveCommandParser commandParser;

	private int port;

	public Registry() {
		// TODO Auto-generated constructor stub
	}

	public Registry(int port) {
		this.port = port;
	}

	private void setSocket(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void onEvent(Event e) {
		// TODO Auto-generated method stub

	}

	public static void main(String args[]) {

		if (args.length != 1) {
			System.out.println("Please input port number!");
			return;
		}

		// get port number from command line
		int port;
		try {
			port = Integer.parseInt(args[0]);
			if (port <= 1024 || port >= 65536) {
				System.out
						.println("Port number out of range, please use port between 1024 and 65536!");
				return;
			}
		} catch (NumberFormatException nfe) {
			System.out.println("Invalid port number!");
			return;
		}

		//setup registry
		Registry registry = null;
		try {
			registry = new Registry(port);
			registry.start();
			System.out.println("Registry is up and listening on port " + port);
		} catch (IOException ioe) {
			System.out.println("Registry failed to start: " + ioe.getMessage());
		}
		
		//get command line input
		Scanner scanner = new Scanner(System.in);
		while(scanner.hasNextLine()){
			String command = scanner.nextLine();
			registry.handleCommand(command);
		}
		
	}

	private void start() throws IOException {
		tcpServer = new TCPServerThread(this, port);
		commandParser = new InteractiveCommandParser(this);
		tcpServer.start();
	}

	private void handleCommand(String command) {

    }


}
