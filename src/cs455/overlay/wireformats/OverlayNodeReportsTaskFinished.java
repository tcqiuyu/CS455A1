package cs455.overlay.wireformats;

import java.io.*;

public class OverlayNodeReportsTaskFinished implements Event {

    private final int type = Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED;

    private String ip;
    private int port;
    private int nodeID;

    public OverlayNodeReportsTaskFinished(String ip, int port, int nodeID) {
        this.ip = ip;
        this.port = port;
        this.nodeID = nodeID;
    }

    public OverlayNodeReportsTaskFinished(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();

        if (this.type == type) {
            int ipLength = din.readInt();
            byte[] ipBytes = new byte[ipLength];
            din.readFully(ipBytes);
            this.ip = new String(ipBytes);
            this.port = din.readInt();
            this.nodeID = din.readInt();
        } else {
            System.out.println("Message type does not match!");
        }

        din.close();
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public int getNodeID() {
        return nodeID;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public byte[] getBytes() throws IOException {
        // TODO Auto-generated method stub
        byte[] marshalledBytes = null;

        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(type);
        dout.writeInt(ip.length());
        byte[] ipBytes = ip.getBytes();
        dout.write(ipBytes);
        dout.writeInt(port);
        dout.writeInt(nodeID);

        dout.flush();

        marshalledBytes = baOutputStream.toByteArray();
        dout.close();
        return marshalledBytes;
    }

}
