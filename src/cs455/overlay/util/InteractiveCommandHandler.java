package cs455.overlay.util;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.node.Node;
import cs455.overlay.node.OverlayNode;
import cs455.overlay.node.Registry;
import cs455.overlay.transport.ConnectionFactory;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.wireformats.OverlayNodeSendsDeregistration;
import cs455.overlay.wireformats.RegistrySendNodeManifest;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class InteractiveCommandHandler {

    public static final int listMessagingNodes = 1;
    public static final int setupOverlay = 2;
    public static final int listRoutingTables = 3;
    public static final int start = 4;
    public static final int printCountersAndDiagnostics = 5;
    public static final int exitOverlay = 6;

    private Map<String, Integer> commandMap = new HashMap<String, Integer>();


    private Node node;

    public InteractiveCommandHandler(Node node) {

        this.node = node;
        initMap();
    }

    public void listMessagingNodes() {
        Registry registry = (Registry) node;
    }

    public void setupOverlay(int size) {
        Registry registry = (Registry) node;
        registry.setupOverlay(size);
        System.out.println("Successful setup overlay!");
        Map<Integer, OverlayNode> nodeMap = registry.getNodeMap();
        for (Map.Entry<Integer, OverlayNode> integerOverlayNodeEntry : nodeMap.entrySet()) {
            OverlayNode currentNode = integerOverlayNodeEntry.getValue();
            String host = currentNode.getHost();
            int port = currentNode.getPort();
            int id = currentNode.getNodeID();
            try {
                System.out.println("Looking for connection on host: " + host);
                TCPConnection connection = ConnectionFactory.getInstance().getConnection(host, port, registry);
                System.out.println("Broadcast manifest report.");
                RegistrySendNodeManifest sendManifest = new RegistrySendNodeManifest(registry.getRoutingTableArray()[id], registry.getIdArray());
                connection.sendData(sendManifest.getBytes());
            } catch (IOException e) {
                System.out.println("Failed to send manifest to messaging node: ID " + id + ", host " + currentNode.getHost());
                System.out.println(e.getMessage());
            }
        }
    }

    public void listRoutingTable() {
        Registry registry = (Registry) node;
    }

    public void start(int msgNumber) {
        Registry registry = (Registry) node;
    }

    public void printCountersAndDiagnostics() {
        MessagingNode msgNode = (MessagingNode) node;
    }

    public void exitOverlay() {
        MessagingNode msgNode = (MessagingNode) node;
        String host;
        int port = msgNode.getLocalPort();
        int nodeID = msgNode.getNodeID();

        try {
            host = msgNode.getLocalhost().getHostAddress();
            int hostLength = host.length();
            OverlayNodeSendsDeregistration deregRequest = new OverlayNodeSendsDeregistration(hostLength, host, port, nodeID);
            System.out.println("Deregistering current node with IP: " + host + ", node ID: " + nodeID);
            TCPConnection connection = ConnectionFactory.getInstance().getConnection(host, port, msgNode);
            connection.sendData(deregRequest.getBytes());
        } catch (UnknownHostException e) {
            System.out.println("Failed to get local host: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Failed to find connection to registry");
            e.printStackTrace();
        }

    }

    private void initMap() {
        commandMap.put("list-messaging-nodes", listMessagingNodes);
        commandMap.put("setup-overlay", setupOverlay);
        commandMap.put("list-routing-tables", listRoutingTables);
        commandMap.put("start", start);
        commandMap.put("print-counters-and-diagnostics", printCountersAndDiagnostics);
        commandMap.put("exit-overlay", exitOverlay);
    }

    public int getCommandValue(String s) {
        String cmd = s.toLowerCase();

        if (commandMap.containsKey(cmd)) {
            return commandMap.get(cmd.toLowerCase());
        } else {
            return -1;
        }
    }
}
