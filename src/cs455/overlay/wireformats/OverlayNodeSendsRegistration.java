package cs455.overlay.wireformats;

import java.io.*;

public class OverlayNodeSendsRegistration implements Event, Protocol {

    private final int type = Protocol.OVERLAY_NODE_SENDS_REGISTRATION;

    private int ipLength;
    private String srcIP;

    private int srcPort;

    public OverlayNodeSendsRegistration(int ipLength, String srcIP, int port) {
        this.ipLength = ipLength;
        this.srcIP = srcIP;
        this.srcPort = port;
    }

    //used by registry (upon received data), to re-construct the message.
    public OverlayNodeSendsRegistration(byte[] marshalledBytes)
            throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(
                marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(
                baInputStream));

        int type = din.readInt();
        if (this.type == type) {
            ipLength = din.readInt();
            byte[] ipBytes = new byte[ipLength];
            din.readFully(ipBytes);
            srcIP = new String(ipBytes);
            srcPort = din.readInt();
        } else {
            System.out.println("Message type does not match!");
        }

        baInputStream.close();
        din.close();
    }

    public String getSrcIP() {
        return srcIP;
    }

    public int getSrcPort() {
        return srcPort;
    }


    @Override
    public int getType() {
        return type;
    }

    //get byte array to send
    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(
                baOutputStream));

        dout.writeInt(type);
        dout.writeInt(ipLength);
        byte[] ipBytes = srcIP.getBytes();
        dout.write(ipBytes);
        dout.writeInt(srcPort);
        dout.flush();

        marshalledBytes = baOutputStream.toByteArray();
        dout.close();
        return marshalledBytes;
    }

    @Override
    public String toString() {
        return type + ":" + ipLength + ":" + srcIP + ":" + srcPort;
    }
}
