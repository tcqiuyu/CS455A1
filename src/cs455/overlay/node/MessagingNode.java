package cs455.overlay.node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.Scanner;

import cs455.overlay.wireformats.Event;

public class MessagingNode implements Node{

	
	@Override
	public void onEvent(Event e) {
		// TODO Auto-generated method stub

    }

	public static void main(String[] args) throws InterruptedException{
		String host = "localhost";
		int port = 8899;
		
		try {
			Socket client = new Socket(host, port);
			Scanner scanner = new Scanner(System.in);
			Writer writer = new OutputStreamWriter(client.getOutputStream());
			while(scanner.hasNextLine()){
				String temp = scanner.nextLine();
				StringBuilder sb = new StringBuilder();
				sb.append(temp).append("\n");
				writer.write(sb.toString());
				//Use flush to make sure server received data in stream
				writer.flush();
			}
			writer.write("eof\n");
			writer.flush();
			
			writer.close();
			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
