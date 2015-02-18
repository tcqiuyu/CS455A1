package cs455.overlay.node;

import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.ConnectionFactory;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.InteractiveCommandHandler;
import cs455.overlay.util.MessagingNodeEventHandler;
import cs455.overlay.util.StatisticsCollectorAndDisplay;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.OverlayNodeSendsData;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;
import cs455.overlay.wireformats.Protocol;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class MessagingNode implements Node {

    private int localPort;
    private String regHost;
    private int regPort;
    private int nodeID;
    private boolean isRegistered = false;
    private TCPServerThread tcpServerThread;
    private InteractiveCommandHandler commandHandler;
    private MessagingNodeEventHandler eventHandler;

    private RoutingTable routingTable;
    private ArrayList<Integer> idArray;

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
        } catch (IOException e) {
            System.out.println("Failed to setup messaging Node: " + e.getMessage());
        }

        //register messaging node to registry

        System.out.println("Messaging Node is registering to registry.");
        try {
            messagingNode.register();
        } catch (IOException ioe) {
            System.out.println("Failed to register to registry:\n" + ioe.getMessage());
            return;
        }

        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            messagingNode.handleCommand(command);
        }

    }

    public RoutingTable getRoutingTable() {
        return routingTable;
    }

    public void setRoutingTable(RoutingTable routingTable) {
        this.routingTable = routingTable;
    }

    public ArrayList<Integer> getIdArray() {
        return idArray;
    }

    public void setIdArray(ArrayList<Integer> idArray) {
        this.idArray = idArray;
    }

    public int getNodeID() {
        return nodeID;
    }

    public void setNodeID(int nodeID) {
        this.nodeID = nodeID;
    }

    public void setRegistered(boolean isRegistered) {
        this.isRegistered = isRegistered;
    }

    private void register() throws IOException {


//        int localHostLength = getLocalhost().toString().length();
        int localHostLength = getLocalhost().getHostAddress().length();
        System.out.println("Creating registration request.");
        OverlayNodeSendsRegistration registrationRequest = new OverlayNodeSendsRegistration(localHostLength,
                getLocalhost().getHostAddress(), //toString(), WRONG!
                localPort);
        byte[] marshalledBytes = registrationRequest.getBytes();

        System.out.println("Connecting to registry...");
        TCPConnection connection = ConnectionFactory.getInstance().getConnection(regHost, regPort, this);
        System.out.println("Sending registration request...");
        connection.sendData(marshalledBytes);
    }

    private void start() throws IOException {
        tcpServerThread = new TCPServerThread(this);
        localPort = tcpServerThread.getLocalPort();
        commandHandler = new InteractiveCommandHandler(this);
        eventHandler = new MessagingNodeEventHandler(this);
        tcpServerThread.start();
        System.out.println("Messaging Node is up on: " + InetAddress.getLocalHost() + ", listening to port number: " + localPort);
    }

    @Override
    public void onEvent(Event e) {
        // TODO Auto-generated method stub
        int type = e.getType();
        switch (type) {
            case Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS:
                eventHandler.handleRegReports(e);
                break;
            case Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS:
                eventHandler.handleDeregReports(e);
                break;
            case Protocol.REGISTRY_SENDS_NODE_MANIFEST:
                try {
                    eventHandler.handleNodeManifest(e);
                } catch (IOException e1) {
                    System.out.println("Failed to handle node manifest.");
                    e1.printStackTrace();
                }
                break;
            case Protocol.REGISTRY_REQUESTS_TASK_INITIATE:
                eventHandler.handleTaskInitRequest(e);
                break;
            case Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY:
                eventHandler.handleTrafficSummaryRequest();
                break;
            case Protocol.OVERLAY_NODE_SENDS_DATA:
                eventHandler.handleNodeSendData(e);
                break;
            default:
                System.out.println("Unrecognized message type");
        }
    }

    public InetAddress getLocalhost() throws UnknownHostException {
        return InetAddress.getLocalHost();
    }

    public int getLocalPort() {
        return localPort;
    }

    public String getRegistryHost() {
        return regHost;
    }

    public int getRegistryPort() {
        return regPort;
    }

    private void handleCommand(String command) {
        switch (commandHandler.getCommandValue(command)) {
            case InteractiveCommandHandler.printCountersAndDiagnostics:
                StatisticsCollectorAndDisplay.getInstance().printCountersAndDiagnostics();
                break;
            case InteractiveCommandHandler.exitOverlay:
                commandHandler.exitOverlay();
                break;
            default:
                break;

        }

    }

    public void updateTracker(OverlayNodeSendsData overlayNodeSendsData) {
        if (overlayNodeSendsData.getDestID() == this.getNodeID()) {//receive data
            StatisticsCollectorAndDisplay.getInstance().increReceiveTracker();
            StatisticsCollectorAndDisplay.getInstance().increReceiveSummation(overlayNodeSendsData.getPayload());
        } else if (overlayNodeSendsData.getSrcID() == this.getNodeID()) {//send data
            StatisticsCollectorAndDisplay.getInstance().increSendTracker();
            StatisticsCollectorAndDisplay.getInstance().increSendSummationBy(overlayNodeSendsData.getPayload());
        } else {//relay data
            StatisticsCollectorAndDisplay.getInstance().increRelayTracker();
        }
    }
}
