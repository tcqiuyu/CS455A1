package cs455.overlay.util;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.RegistryReportsRegistrationStatus;

/**
 * Created by Qiu on 2/15/2015.
 */
public class MessagingNodeEventHandler {

    private MessagingNode messagingNode;

    public MessagingNodeEventHandler(MessagingNode messagingNode) {
        this.messagingNode = messagingNode;
    }


    public void handleRegReports(Event event) {
        RegistryReportsRegistrationStatus regReport = (RegistryReportsRegistrationStatus) event;
        int status = regReport.getStatus();
        if (status >= 0) {
            messagingNode.setNodeID(status);
            messagingNode.setRegistered(true);
            System.out.println(regReport.getInfo());
        } else {
            System.out.println(regReport.getInfo());
        }
    }

    public void handleDeregReports(Event event) {

    }

    public void handleTaskInitRequest(Event event) {

    }

    public void handleTrafficSummaryRequest(Event event) {

    }

    public void handleNodeManifest(Event event) {

    }

    public void handleNodeSendData(Event event) {

    }
}
