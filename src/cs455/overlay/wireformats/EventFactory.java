package cs455.overlay.wireformats;

public class EventFactory {

	// This is a singleton instance

	private static EventFactory instance = new EventFactory();

	private EventFactory() {
	}

	public static EventFactory getInstance() {
		return instance;
	}

	public static Event createEvent(Protocol protocol) {
		switch (protocol) {
		case NODE_REPORTS_OVERLAY_SETUP_STATUS:
			return new NodeReportsOverlaySetupStatus();
		case OVERLAY_NODE_REPORTS_TASK_FINISHED:
			return new OverlayNodeReportsTaskFinished();
		case OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY:
			return new OverlayNodeReportsTrafficeSummary();
		case OVERLAY_NODE_SENDS_DATA:
			return new OverlayNodeSendsData();
		case OVERLAY_NODE_SENDS_REGISTRATION:
			return new OverlayNodeSendsRegistration();
		case REGISTRY_REPORTS_REGISTRATION_STATUS:
			return new RegistryReportsRegistrationStatus();
		case REGISTRY_REQUESTS_TAST_INITIATE:
			return new RegistryRequestsTaskInitiate();
		case REGISTRY_REQUESTS_TRAFFIC_SUMMARY:
			return new RegistryRequestsTrafficSummary();
		case REGISTRY_SENDS_NODE_MANIFEST:
			return new RegistrySendNodeManifest();
		default:
			return null;
		}
	}

}
