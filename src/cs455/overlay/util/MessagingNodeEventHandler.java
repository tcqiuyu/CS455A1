package cs455.overlay.util;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.ConnectionFactory;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.RegistryReportsDeregistrationStatus;
import cs455.overlay.wireformats.RegistryReportsRegistrationStatus;
import cs455.overlay.wireformats.RegistrySendNodeManifest;

import java.io.IOException;
import java.util.ArrayList;

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
        RegistryReportsDeregistrationStatus deregReport = (RegistryReportsDeregistrationStatus) event;
        if (deregReport.getStatus() > 0) {
            messagingNode.setRegistered(false);
            messagingNode.setNodeID(-1);
        }
        System.out.println(deregReport.getInfo());

    }

    public void handleTaskInitRequest(Event event) {

    }

    public void handleTrafficSummaryRequest(Event event) {

    }

    public void handleNodeManifest(Event event) {
        RegistrySendNodeManifest manifestReport = (RegistrySendNodeManifest) event;

        RoutingTable routingTable = manifestReport.getRoutingTable();
        ArrayList<Integer> idArray = manifestReport.getIdArray();
        messagingNode.setRoutingTable(routingTable);
        messagingNode.setIdArray(idArray);

        System.out.println("Establishing connection to routing nodes...");

        for (int i = 0; i < routingTable.getSize(); i++) {
            RoutingEntry entry = routingTable.getTable()[i];
            try {
                System.out.println("Connecting to Node ID: " + entry.getNodeID());
                ConnectionFactory.getInstance().getConnection(entry.getLocalhost(), entry.getPort(), messagingNode);
                System.out.println("Success!");
            } catch (IOException e) {
                System.out.println("Connection failed.");
                System.out.println(e.getMessage());
                //send fail setup status to registry

            }

        }
    }


    public void handleNodeSendData(Event event) {

    }
}
