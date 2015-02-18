package cs455.overlay.wireformats;

import java.io.*;

public class NodeReportsOverlaySetupStatus implements Event {

    private final int type = Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS;

    private int status;

    private int infoLength;
    private String info;

    public NodeReportsOverlaySetupStatus(int status, int infoLength, String info) {
        this.status = status;
        this.infoLength = infoLength;
        this.info = info;
    }

    public NodeReportsOverlaySetupStatus(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();
        if (this.type == type) {
            status = din.readInt();
            infoLength = din.readInt();
            byte[] infoBytes = new byte[infoLength];
            din.readFully(infoBytes);
            info = new String(infoBytes);
        } else {
            System.out.println("Message type does not match!");
        }
    }

    public String getInfo() {
        return info;
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
        dout.writeInt(status);
        dout.writeInt(infoLength);
        byte[] infoBytes = info.getBytes();
        dout.write(infoBytes);

        dout.flush();

        marshalledBytes = baOutputStream.toByteArray();
        dout.close();
        return marshalledBytes;
    }

}
