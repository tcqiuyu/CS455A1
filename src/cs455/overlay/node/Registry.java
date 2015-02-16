package cs455.overlay.node;

import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.InteractiveCommandHandler;
import cs455.overlay.wireformats.Event;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class Registry implements Node {


    private TCPServerThread tcpServer;
    private InteractiveCommandHandler commandHandler;

    private int port;

    private Map<Integer, OverlayNode> nodeMap = new HashMap<Integer, OverlayNode>();


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

    public int getNextID() {
        Random ran = new Random();
        int id;
        do {
            id = ran.nextInt(128);
        } while (nodeMap.containsKey(id));
        return id;
    }

    public synchronized int registerNode(int id, OverlayNode overlayNode) throws UnknownHostException {
        if (!nodeMap.containsValue(overlayNode)) {

            addToNodeMap(id, overlayNode);
            overlayNode.setID(id);
            return id;
        } else {
            System.out.println("Node is already registered!");
            return -1;
        }
    }

    public synchronized void addToNodeMap(int id, OverlayNode overlayNode) {
        nodeMap.put(id, overlayNode);
    }

    public synchronized int deRegisterNode(String srcIP, int srcPort, int nodeID) {
        if (nodeMap.containsKey(nodeID)) {
            OverlayNode storedNode = nodeMap.get(nodeID);

            String storedIP = storedNode.getHost();
            int storedPort = storedNode.getPort();

            if (storedIP.equals(srcIP) && storedPort == srcPort) {
                nodeMap.remove(nodeID);
                System.out.println("Deregistration successful. Node with ID " + nodeID + " is deregistered.");
                return 1;
            } else {
                System.out.println("Deregistration failed. Node information provided is not consistent with the one " +
                        "stored in registry.");
                return -1;
            }
        } else {
            System.out.println("Deregistration failed. Node is not in overlay!");
            return -2;
        }
    }

    public int getNodeAmount() {
        return nodeMap.size();
    }

    private void start() throws IOException {
        tcpServer = new TCPServerThread(this, port);
        commandHandler = new InteractiveCommandHandler(this);
        tcpServer.start();
    }

    private void handleCommand(String command) {

        String[] commandArray = command.split(" ");
        String cmd = commandArray[0];

        int para;
        if (commandArray.length == 2) {
            para = Integer.parseInt(commandArray[1]);
        }


        switch (commandHandler.getCommandValue(cmd)) {
            case InteractiveCommandHandler.listMessagingNodes:

                break;
            case InteractiveCommandHandler.setupOverlay:

                break;
            case InteractiveCommandHandler.listRoutingTables:

                break;
            case InteractiveCommandHandler.start:

                break;
            default:

                break;
        }

    }


}
