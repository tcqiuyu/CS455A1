package cs455.overlay.wireformats;

public class RegistrySendNodeManifest implements Event {

	private final int type = Protocol.REGISTRY_SENDS_NODE_MANIFEST;
	
	private int tableSize;
	
	
	
	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return type;
	}

	@Override
	public byte[] getBytes() {
		// TODO Auto-generated method stub
		return null;
	}

}
