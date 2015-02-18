package cs455.overlay.node;

import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.ConnectionFactory;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.InteractiveCommandHandler;
import cs455.overlay.util.MessagingNodeEventHandler;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.OverlayNodeSendsData;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;
import cs455.overlay.wireformats.Protocol;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class MessagingNode extends Thread implements Node {

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

//    private AtomicInteger sendTracker = new AtomicInteger(0);
//    private AtomicInteger receiveTracker = new AtomicInteger(0);
//    private AtomicInteger relayTracker = new AtomicInteger(0);
//    private AtomicLong sendSummation = new AtomicLong(0);
//    private AtomicLong receiveSummation = new AtomicLong(0);

    private int sendTracker;
    private int receiveTracker;
    private int relayTracker;
    private long sendSummation;
    private long receiveSummation;
    private Queue<OverlayNodeSendsData> relayQueue = new LinkedList<OverlayNodeSendsData>();

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
            messagingNode.init();
        } catch (IOException e) {
            System.out.println("Failed to setup messaging Node: " + e.getMessage());
        }

        //register messaging node to registry

        System.out.println("Messaging Node is registering to registry.");
        try {
            messagingNode.register();
            messagingNode.start();
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

    public void run() {
        while (true) {
            OverlayNodeSendsData relayMsg;
            synchronized (relayQueue) {
                relayMsg = relayQueue.poll();
            }
            if (relayMsg != null) {
                try {
                    byte[] bytesToSend = relayMsg.getBytes();
                    int next = eventHandler.getNextRoutingIndex(relayMsg.getDestID(), routingTable.getTable());
                    RoutingEntry entry = routingTable.getTable()[next];
                    String host = entry.getLocalhost();
                    int port = entry.getPort();
                    TCPConnection connection = ConnectionFactory.getInstance().getConnection(host, port, this);
//                    relayTracker.addAndGet(1);
                    relayTracker++;
                    connection.sendData(bytesToSend);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    private void init() throws IOException {
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
                eventHandler.handleTrafficSummaryRequest(e);
                break;
            case Protocol.OVERLAY_NODE_SENDS_DATA:
                OverlayNodeSendsData overlayNodeSendsData = (OverlayNodeSendsData) e;
                try {
                    handleNodeSendsData(overlayNodeSendsData);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
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
                commandHandler.printCountersAndDiagnostics();
                break;
            case InteractiveCommandHandler.exitOverlay:
                commandHandler.exitOverlay();
                break;
            default:
                break;

        }

    }

    public void printCountersAndDiagnostics() {
        System.out.println("Packets sent: " + sendTracker);
        System.out.println("Packets received: " + receiveTracker);
        System.out.println("Packets relayed: " + relayTracker);
        System.out.println("Sum values sent: " + sendSummation);
        System.out.println("Sum values received: " + receiveSummation);
    }

    public synchronized void handleNodeSendsData(OverlayNodeSendsData overlayNodeSendsData) throws IOException {
        if (overlayNodeSendsData.getDestID() == this.getNodeID()) {//receive data
//            receiveTracker.addAndGet(1);
//            receiveSummation.addAndGet(overlayNodeSendsData.getPayload());
            receiveTracker++;
            receiveSummation = receiveSummation + overlayNodeSendsData.getPayload();
            if (receiveTracker == idArray.size()) {
                eventHandler.reportTaskFinished();
            }
        } else if (overlayNodeSendsData.getSrcID() == this.getNodeID()) {
//            sendTracker.addAndGet(1);
//            sendSummation.addAndGet(overlayNodeSendsData.getPayload());
            sendTracker++;
            sendSummation = sendSummation + overlayNodeSendsData.getPayload();
        } else {
            synchronized (relayQueue) {
                relayQueue.add(overlayNodeSendsData);
            }
        }
    }
}

