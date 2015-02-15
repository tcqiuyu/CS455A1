package cs455.overlay.node;

import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.wireformats.Event;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Registry implements Node {

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

    public static void main(String args[]) {

        if (args.length != 1) {
            System.out.println("Please input port number!");
            return;
        }

        // get port number from command line
        int port = Integer.parseInt(args[0]);
        if (port <= 1024 || port >= 65536) {
            System.out
                    .println("Port number out of range, please use port between 1024 and 65536!");
            return;
        }


        //setup registry
        Registry registry = null;
        try {
            registry = new Registry(port);
            registry.start();
            System.out.println("Registry is up and listening on port " + port);
            System.out.println("Local address is " + registry.getLocolhost());
        } catch (IOException ioe) {
            System.out.println("Registry failed to start: " + ioe.getMessage());
        }

        //get command line input
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            registry.handleCommand(command);
        }
    }

    @Override
    public void onEvent(Event e) {
        // TODO Auto-generated method stub

    }

    public InetAddress getLocolhost() throws UnknownHostException {
        return InetAddress.getLocalHost();
    }

    private void start() throws IOException {
        tcpServer = new TCPServerThread(this, port);
        commandParser = new InteractiveCommandParser(this);
        tcpServer.start();
    }

    private void handleCommand(String command) {

        String[] commandArray = command.split(" ");
        String cmd = commandArray[0];

        int para;
        if (commandArray.length == 2) {
            para = Integer.parseInt(commandArray[1]);
        }


        switch (commandParser.getCommandValue(cmd)) {
            case InteractiveCommandParser.listMessagingNodes:

                break;
            case InteractiveCommandParser.setupOverlay:

                break;
            case InteractiveCommandParser.listRoutingTables:

                break;
            case InteractiveCommandParser.start:

                break;
            default:

                break;
        }

    }


}
