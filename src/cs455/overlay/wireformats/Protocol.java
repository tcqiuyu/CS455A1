package cs455.overlay.wireformats;

public interface Protocol {
		
		static final int OVERLAY_NODE_SENDS_REGISTRATION = 2;
		static final int REGISTRY_REPORTS_REGISTRATION_STATUS = 3;
		
		static final int OVERLAY_NODE_SENDS_DEREGISTRATION=4;
		static final int REGISTRY_REPORTS_DEREGISTRATION_STATUS=5;
		
		static final int REGISTRY_SENDS_NODE_MANIFEST=6;
		static final int NODE_REPORTS_OVERLAY_SETUP_STATUS=7;
		
		static final int REGISTRY_REQUESTS_TAST_INITIATE=8;
		static final int OVERLAY_NODE_SENDS_DATA=9;
		static final int OVERLAY_NODE_REPORTS_TASK_FINISHED=10;
		
		static final int REGISTRY_REQUESTS_TRAFFIC_SUMMARY=11;
		static final int OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY=12;
		
}
