package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class EventFactory {

	// This is a singleton instance

	private static EventFactory instance = new EventFactory();

	private EventFactory() {
	}

	public static EventFactory getInstance() {
		return instance;
	}

	public static Event createEvent(byte[] data) {

		ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
		DataInputStream din = new DataInputStream(new BufferedInputStream(
				baInputStream));

		try {
			int type = din.readInt();
			baInputStream.close();
			din.close();

			switch (type) {
			case Protocol.OVERLAY_NODE_SENDS_REGISTRATION:
				return new OverlayNodeSendsRegistration(data);
			case Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS:
				return new RegistryReportsRegistrationStatus(data);
				
			case Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION:
				return new OverlayNodeSendsDeregistration(data);
			case Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS:
				return new RegistryReportsDeregistrationStatus(data);
				
			case Protocol.REGISTRY_SENDS_NODE_MANIFEST:
				return new RegistrySendNodeManifest();
			case Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS:
				return new NodeReportsOverlaySetupStatus();
				
			case Protocol.REGISTRY_REQUESTS_TAST_INITIATE:
				return new RegistryRequestsTaskInitiate();
			case Protocol.OVERLAY_NODE_SENDS_DATA:
				return new OverlayNodeSendsData();
			case Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED:
				return new OverlayNodeReportsTaskFinished();
				
			case Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY:
				return new RegistryRequestsTrafficSummary();
			case Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY:
				return new OverlayNodeReportsTrafficeSummary();

			default:
				return null;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
