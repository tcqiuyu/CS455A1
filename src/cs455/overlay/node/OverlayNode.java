package cs455.overlay.node;

/**
 * Created by Qiu on 2/16/2015.
 */

public class OverlayNode {

    private String host;
    private int port;

    private int nodeID = -1;

    public OverlayNode(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void setID(int nodeID) {
        this.nodeID = nodeID;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getNodeID() {
        return nodeID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OverlayNode that = (OverlayNode) o;

        if (port != that.port) return false;
        if (!host.equals(that.host)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = host.hashCode();
        result = 31 * result + port;
        return result;
    }
}
