package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class OverlayNodeSendsRegistration implements Event, Protocol {

	private final int type = Protocol.OVERLAY_NODE_SENDS_REGISTRATION;

	private int ipLength;
	private String ip;

	private int port;

	public OverlayNodeSendsRegistration(int ipLength, String ip, int port){
		this.ipLength = ipLength;
		this.ip = ip;
		this.port = port;
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
			ip = new String(ipBytes);
		} else {
			System.err.println("Message type does not match!");
		}

		baInputStream.close();
		din.close();
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
		byte[] ipBytes = ip.getBytes();
		dout.write(ipBytes);

		dout.flush();

		marshalledBytes = baOutputStream.toByteArray();
		dout.close();
		return marshalledBytes;
	}

	@Override
	public String toString() {
		return type + ":" + ipLength + ":" + ip + ":" + port;
	}
}
