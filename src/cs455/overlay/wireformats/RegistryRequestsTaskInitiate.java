package cs455.overlay.wireformats;

import java.io.*;

public class RegistryRequestsTaskInitiate implements Event {

    private final int type = Protocol.REGISTRY_REQUESTS_TASK_INITIATE;
    private int packageNum;

    public RegistryRequestsTaskInitiate(int packageNum) {
        this.packageNum = packageNum;
    }

    public RegistryRequestsTaskInitiate(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();
        if (this.type == type) {
            this.packageNum = din.readInt();
        } else {
            System.out.println("Message type does not match!");
        }

        baInputStream.close();
        din.close();
    }

    public int getPackageNum() {
        return packageNum;
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
        dout.writeInt(packageNum);

        dout.flush();

        marshalledBytes = baOutputStream.toByteArray();
        dout.close();
        return marshalledBytes;

    }

}
