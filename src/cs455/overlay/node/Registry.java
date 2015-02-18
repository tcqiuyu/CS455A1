package cs455.overlay.node;

import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.util.InteractiveCommandHandler;
import cs455.overlay.util.RegistryEventHandler;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.Protocol;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class Registry implements Node {

    public static final int MAX_REGISTER_NUMBER = 128;
    private RoutingEntry[] routingEntries;
    private TCPServerThread tcpServer;
    private InteractiveCommandHandler commandHandler;
    private RegistryEventHandler eventHandler;
    private int port;
    private Map<Integer, OverlayNode> nodeMap = new HashMap<Integer, OverlayNode>();
    private ArrayList<Integer> idArray = new ArrayList<Integer>();
    private RoutingTable[] routingTableArray = new RoutingTable[MAX_REGISTER_NUMBER];

    private int finishedNodeCount = 0;

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

        // setup registry
        Registry registry = null;
        try {
            registry = new Registry(port);
            registry.start();
            System.out.println("Registry is up and listening on port " + port);
            System.out.println("Local address is " + registry.getLocolhost());
        } catch (IOException ioe) {
            System.out.println("Registry failed to start: " + ioe.getMessage());
        }

        // get command line input
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            registry.handleCommand(command);
        }
    }

    public synchronized void resetCount() {
        finishedNodeCount = 0;
    }

    public RoutingEntry[] getNodeEntries() {
        return routingEntries;
    }

    public RoutingTable[] getRoutingTableArray() {
        return routingTableArray;
    }

    @Override
    public void onEvent(Event e) throws IOException {
        // TODO Auto-generated method stub
        int type = e.getType();
        switch (type) {
            case Protocol.OVERLAY_NODE_SENDS_REGISTRATION:
                eventHandler.handleRegRequest(e);
                break;
            case Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION:
                eventHandler.handleDeregRequest(e);
                break;
            case Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS:
                eventHandler.handleSetupStatusReports(e);
                break;
            case Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY:
                eventHandler.handleTrafficSummaryReports(e);
                break;
            case Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED:
                eventHandler.handleTaskFinishReports(e);
                break;
            default:
                System.out.println("Unrecognized message type.");
        }
    }

    public InetAddress getLocolhost() throws UnknownHostException {
        return InetAddress.getLocalHost();
    }

    public int getNextID() {
        Random ran = new Random();
        int id;
        do {
            id = ran.nextInt(MAX_REGISTER_NUMBER);
        } while (nodeMap.containsKey(id));
        return id;
    }

    public synchronized int registerNode(int id, OverlayNode overlayNode)
            throws UnknownHostException {
        if (!nodeMap.containsValue(overlayNode)) {

            addToNodeMap(id, overlayNode);
            overlayNode.setID(id);
            System.out.println("Register successful, with ID: " + id);
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
                System.out.println("Deregistration successful. Node with ID "
                        + nodeID + " is deregistered.");
                return 1;
            } else {
                System.out
                        .println("Deregistration failed. Node information provided is not consistent with the one "
                                + "stored in registry.");
                return -1;
            }
        } else {
            System.out
                    .println("Deregistration failed. Node is not in overlay!");
            return -2;
        }
    }

    public int getPort() {
        return port;
    }

    public int getNodeAmount() {
        return nodeMap.size();
    }

    private void start() throws IOException {
        tcpServer = new TCPServerThread(this, port);
        commandHandler = new InteractiveCommandHandler(this);
        eventHandler = new RegistryEventHandler(this);
        tcpServer.start();
    }

    private void handleCommand(String command) {

        String[] commandArray = command.split(" ");
        String cmd = commandArray[0];

        int para = -1;
        if (commandArray.length == 2) {
            para = Integer.parseInt(commandArray[1]);
        }

        switch (commandHandler.getCommandValue(cmd)) {
            case InteractiveCommandHandler.listMessagingNodes:
                commandHandler.listMessagingNodes();
                break;
            case InteractiveCommandHandler.setupOverlay:
                if (commandArray.length == 1) {
                    commandHandler.setupOverlay(3);
                } else if (para > 0) {
                    commandHandler.setupOverlay(para);
                }
                break;
            case InteractiveCommandHandler.listRoutingTables:
                commandHandler.listRoutingTables();
                break;
            case InteractiveCommandHandler.start:
                commandHandler.start(para);
                break;
            default:

                break;
        }

    }

    public ArrayList<Integer> getIdArray() {
        return idArray;
    }

    //initiate general entries for each node, node with id i is stored in index i
    private void initOverlay() {
        Iterator<Integer> keyIter = nodeMap.keySet().iterator();

        routingEntries = new RoutingEntry[MAX_REGISTER_NUMBER];

        for (int i = 0; i < MAX_REGISTER_NUMBER; i++) {
            while (keyIter.hasNext()) {
                int nextID = keyIter.next();
                routingEntries[nextID] = new RoutingEntry(nodeMap.get(nextID));
            }
        }

    }

    private void setupIDArray() {
        int temp = 0;
        for (int id = 0; id < MAX_REGISTER_NUMBER; id++) {
            if (routingEntries[id] != null) {
                idArray.add(temp, id);
                temp++;
            }
        }
    }

    //build routing table for each messaging node, table for node i is stored in index i
    public void setupOverlay(int routingTableSize) {
        initOverlay();
        setupIDArray();
        for (int id = 0; id < MAX_REGISTER_NUMBER; id++) {
            if (routingEntries[id] != null) {

                RoutingTable routingTable = new RoutingTable(routingTableSize,
                        routingEntries, idArray, id);
                routingTableArray[id] = routingTable;
            }
        }
    }

    public Map<Integer, OverlayNode> getNodeMap() {
        return nodeMap;
    }

    public synchronized int getFinishedNodeCount() {
        return finishedNodeCount;
    }

    public synchronized void increFinishedNodeCount() {
        this.finishedNodeCount++;
    }
}
