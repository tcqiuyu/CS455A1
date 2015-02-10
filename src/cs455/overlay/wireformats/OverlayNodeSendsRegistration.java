package cs455.overlay.wireformats;

import java.net.InetAddress;

public class OverlayNodeSendsRegistration implements Event{

	private int messageType;
	private int ipLength;
	byte[] address;
	
	public Protocol getProtocol(){
		return Protocol.OVERLAY_NODE_SENDS_REGISTRATION;
	}
	
	@Override
	public int getType() {
		// TODO Auto-generated method stub
		messageType = this.getProtocol().getProtocolID();
		
		return messageType;
	}

	@Override
	public byte[] getBytes() {
		// TODO Auto-generated method stub
		return null;
	}

}
