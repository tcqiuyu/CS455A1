package cs455.overlay.wireformats;

import java.io.*;
import java.util.Random;

public class OverlayNodeSendsData implements Event {

    private final int type = Protocol.OVERLAY_NODE_SENDS_DATA;

    private int destID;
    private int srcID;

    private int payload;

    private int[] trace = new int[1];

    public OverlayNodeSendsData(int destID, int srcID) {
        this.destID = destID;
        this.srcID = srcID;
        Random ran = new Random();
        trace[0] = srcID;
        this.payload = ran.nextInt();
    }

    public OverlayNodeSendsData(byte[] marshalledData) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledData);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();
        if (this.type == type) {
            destID = din.readInt();
            srcID = din.readInt();
            payload = din.readInt();
            int traceLength = din.readInt();
            int[] trace = new int[traceLength];
            for (int i = 0; i < traceLength; i++) {
                trace[i] = din.readInt();
            }
        } else {
            System.out.println("Message type does not match!");
        }

        baInputStream.close();
        din.close();
    }

    public int getDestID() {
        return destID;
    }

    public int getSrcID() {
        return srcID;
    }

    public int getPayload() {
        return payload;
    }

    public int[] getTrace() {
        return trace;
    }

    public void updateTrace(int nodeID) {
        int[] newTrace = new int[trace.length + 1];
        //copy to a longer array
        for (int i = 0; i < trace.length; i++) {
            newTrace[i] = trace[i];
        }
        newTrace[newTrace.length - 1] = nodeID;

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
        dout.writeInt(destID);
        dout.writeInt(srcID);
        dout.writeInt(payload);

        dout.writeInt(trace.length);
        for (int i = 0; i < trace.length; i++) {
            dout.writeInt(trace[i]);
        }

        dout.flush();

        marshalledBytes = baOutputStream.toByteArray();
        dout.close();
        return marshalledBytes;
    }

}
