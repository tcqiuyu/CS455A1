package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RegistryRequestsTrafficSummary implements Event {

    private final int type = Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY;

    public RegistryRequestsTrafficSummary() {

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
        dout.flush();

        marshalledBytes = baOutputStream.toByteArray();

        dout.close();
        return marshalledBytes;
    }

}
