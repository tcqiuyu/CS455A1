package cs455.overlay.wireformats;

import java.io.*;

public class OverlayNodeReportsTrafficSummary implements Event {

    private final int type = Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY;

    private int nodeID;
    private int packetSent;
    private int packetRelayed;
    private long dataSentSum;
    private int packetReceived;
    private long dataReceivedSum;

    public OverlayNodeReportsTrafficSummary(int nodeID, int packetSent, int packetRelayed, long dataSentSum, int packetReceived, long dataReceivedSum) {
        this.nodeID = nodeID;
        this.packetSent = packetSent;
        this.packetRelayed = packetRelayed;
        this.dataSentSum = dataSentSum;
        this.packetReceived = packetReceived;
        this.dataReceivedSum = dataReceivedSum;
    }

    public OverlayNodeReportsTrafficSummary(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();
        if (this.type == type) {
            this.nodeID = din.readInt();
            this.packetSent = din.readInt();
            this.packetRelayed = din.readInt();
            this.dataSentSum = din.readLong();
            this.packetReceived = din.readInt();
            this.dataReceivedSum = din.readLong();
        } else {
            System.out.println("Message type does not match!");
        }

    }

    public int getNodeID() {
        return nodeID;
    }

    public int getPacketSent() {
        return packetSent;
    }

    public int getPacketRelayed() {
        return packetRelayed;
    }

    public long getDataSentSum() {
        return dataSentSum;
    }

    public int getPacketReceived() {
        return packetReceived;
    }

    public long getDataReceivedSum() {
        return dataReceivedSum;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;

        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(type);
        dout.writeInt(nodeID);
        dout.writeInt(packetSent);
        dout.writeInt(packetRelayed);
        dout.writeLong(dataSentSum);
        dout.writeInt(packetReceived);
        dout.writeLong(dataReceivedSum);

        dout.flush();

        marshalledBytes = baOutputStream.toByteArray();

        dout.close();
        return marshalledBytes;
    }

}
