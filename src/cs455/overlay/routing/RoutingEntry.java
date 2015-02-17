package cs455.overlay.routing;

import cs455.overlay.node.OverlayNode;

public class RoutingEntry {

    private int nodeID;
    private String localhost;
    private int localhostLength;
    private int port;

    public RoutingEntry(OverlayNode overlayNode) {
        this.nodeID = overlayNode.getNodeID();
        this.localhost = overlayNode.getHost();
        this.localhostLength = localhost.length();
        this.port = overlayNode.getPort();
    }

}
