package cs455.overlay.node;

import cs455.overlay.transport.ConnectionFactory;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.InteractiveCommandHandler;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class MessagingNode implements Node {

    private String regHost;
    private int regPort;

    private TCPServerThread tcpServerThread;
    private InteractiveCommandHandler commandParser;
    private int nodeID;


    public MessagingNode(String regHost, int regPort) {
        this.regHost = regHost;
        this.regPort = regPort;
    }

    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Please input registry address and port number!");
            return;
        }


        String regHost = args[0];
        int regPort = Integer.parseInt(args[1]);

        MessagingNode messagingNode = null;
        try {
            messagingNode = new MessagingNode(regHost, regPort);
            messagingNode.start();
            System.out.print("Messaging Node is up on host: " + regHost + ", port number: " + regPort);
        } catch (IOException e) {
            System.out.println("Failed to setup messaging Node: " + e.getMessage());
        }

        //register messaging node to registry
        try {
            messagingNode.register();
        } catch (IOException ioe) {
            System.out.println("Failed to register to registry: " + ioe.getMessage());
            return;
        }

        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            messagingNode.handleCommand(command);
        }

    }

    private void register() throws IOException {
        int regHostLength = regHost.length();
        Event registrationRequest = new OverlayNodeSendsRegistration(regHostLength, regHost, regPort);
        byte[] marshalledBytes = registrationRequest.getBytes();
        TCPConnection connection = ConnectionFactory.getInstance().getConnection(regHost, regPort, this);
        connection.sendData(marshalledBytes);
    }

    private void start() throws IOException {
        tcpServerThread = new TCPServerThread(this);
        commandParser = new InteractiveCommandHandler(this);
        tcpServerThread.start();
    }

    @Override
    public void onEvent(Event e) {
        // TODO Auto-generated method stub

    }

    public InetAddress getLocalhost() throws UnknownHostException {
        return InetAddress.getLocalHost();
    }

    public int getNodeID() {
        return nodeID;
    }

    public String getRegistryHost() {
        return regHost;
    }

    public int getRegistryPort() {
        return regPort;
    }

    private void handleCommand(String command) {
        switch (commandParser.getCommandValue(command)) {
            case InteractiveCommandHandler.printCountersAndDiagnostics:
                break;
            case InteractiveCommandHandler.exitOverlay:
                break;
            default:
                break;

        }

    }

}
