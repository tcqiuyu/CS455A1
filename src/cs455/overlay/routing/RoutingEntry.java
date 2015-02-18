package cs455.overlay.routing;

import cs455.overlay.node.OverlayNode;

public class RoutingEntry {

    private int nodeID;
    private String localhost;

    public int getPort() {
        return port;
    }

    public String getLocalhost() {
        return localhost;
    }

    public int getNodeID() {
        return nodeID;
    }

    private int port;

    public RoutingEntry(OverlayNode overlayNode) {
        this.nodeID = overlayNode.getNodeID();
        this.localhost = overlayNode.getHost();
        this.port = overlayNode.getPort();
    }



}
