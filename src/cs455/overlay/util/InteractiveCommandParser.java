package cs455.overlay.util;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.node.Node;
import cs455.overlay.node.Registry;

import java.util.HashMap;
import java.util.Map;

public class InteractiveCommandParser {

    public static final int listMessagingNodes = 1;
    public static final int setupOverlay = 2;
    public static final int listRoutingTables = 3;
    public static final int start = 4;
    public static final int printCountersAndDiagnostics = 5;
    public static final int exitOverlay = 6;

    private Map<String, Integer> commandMap = new HashMap<String, Integer>();


    private Node node;

    public InteractiveCommandParser(Node node) {

        this.node = node;
    }

    public void listMessagingNodes() {
        Registry registry = (Registry) node;
    }

    public void setupOverlay(int size) {
        Registry registry = (Registry) node;
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
