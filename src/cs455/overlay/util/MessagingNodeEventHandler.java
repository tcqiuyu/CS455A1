package cs455.overlay.util;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.ConnectionFactory;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.wireformats.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

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


    public void handleTrafficSummaryRequest(Event event) {
        sendTrafficSummary();
    }

    private void sendTrafficSummary() {

    }

    public void handleNodeManifest(Event event) throws IOException {
        RegistrySendNodeManifest manifestReport = (RegistrySendNodeManifest) event;

        RoutingTable routingTable = manifestReport.getRoutingTable();
        ArrayList<Integer> idArray = manifestReport.getIdArray();
        messagingNode.setRoutingTable(routingTable);
        messagingNode.setIdArray(idArray);

        System.out.println("Establishing connection to routing nodes...");

        TCPConnection registerConnection = null;
        registerConnection = ConnectionFactory.getInstance().getConnection(messagingNode.getRegistryHost(), messagingNode.getRegistryPort(), messagingNode);

        NodeReportsOverlaySetupStatus setupStatusReport;

        for (int i = 0; i < routingTable.getSize(); i++) {
            RoutingEntry entry = routingTable.getTable()[i];
            try {
//                System.out.println("Connecting to Node ID: " + entry.getNodeID());
                ConnectionFactory.getInstance().getConnection(entry.getLocalhost(), entry.getPort(), messagingNode);
//                System.out.println("Success!");
            } catch (IOException e) {
                System.out.println("Connection failed.");
                System.out.println(e.getMessage());
                //send fail setup status to registry
                String info = "Node ID " + messagingNode.getNodeID() + " initiate routing connection failed: " + e.getMessage();
                setupStatusReport = new NodeReportsOverlaySetupStatus(-1, info.length(), info);
                registerConnection.sendData(setupStatusReport.getBytes());

            }

        }

        String info = "Node ID: " + messagingNode.getNodeID() + " successfully connect to all nodes in its routing table.";
        System.out.println(info);
        setupStatusReport = new NodeReportsOverlaySetupStatus(messagingNode.getNodeID(), info.length(), info);
        registerConnection.sendData(setupStatusReport.getBytes());

    }

    public void handleTaskInitRequest(Event event) {
        RegistryRequestsTaskInitiate taskInitReq = (RegistryRequestsTaskInitiate) event;

        int packageNum = taskInitReq.getPackageNum();
        System.out.println("Received task initiate request. Sending " + packageNum + " packages.");
        try {
            initTask(packageNum);
        } catch (IOException e) {
            System.out.println("Failed to initiate task. " + e.getMessage());
        }
    }

    private void initTask(int packageNum) throws IOException {

        System.out.println("Processing task...");

//        System.out.println("Random destination is node " + randomDestID);

//        System.out.println("Sending packages to node " + nextEntry.getNodeID());

        ArrayList<Integer> idArray = messagingNode.getIdArray();
        RoutingEntry[] entries = messagingNode.getRoutingTable().getTable();

        for (int i = 0; i < packageNum; i++) {
            Random ran = new Random();

            int randomDestID = idArray.get(ran.nextInt(idArray.size()));

            //won't target itself
            while (randomDestID == messagingNode.getNodeID()) {
                randomDestID = idArray.get(ran.nextInt(idArray.size()));
            }

            int nextIndex = getNextRoutingIndex(randomDestID, entries);
            RoutingEntry nextEntry = entries[nextIndex];

            String host = nextEntry.getLocalhost();
            int port = nextEntry.getPort();

            TCPConnection connection = ConnectionFactory.getInstance().getConnection(host, port, messagingNode);

            OverlayNodeSendsData dataToSend = new OverlayNodeSendsData(randomDestID, messagingNode.getNodeID());
            connection.sendData(dataToSend.getBytes());
            messagingNode.updateTracker(dataToSend);//update packetSent & sentSum
        }

        try {
            System.out.println("Sent all " + packageNum + " packages. Reports to registry...");
            reportTaskFinished();
            System.out.println("Reports sent.");
        } catch (IOException e) {
            System.out.println("Failed to send task finish report to registry..." + e.getMessage());
        }
    }

    private int getNextRoutingIndex(int destID, RoutingEntry[] entries) {

        for (int i = 0; i < entries.length - 1; i++) {
            if (entries[i].getNodeID() == destID)
                return i;

            int diff1 = entries[i].getNodeID() - destID;
            int diff2 = entries[i + 1].getNodeID() - destID;
            if (diff1 < 0 && diff2 > 0)
                return i;
            if (entries[i].getNodeID() > entries[i + 1].getNodeID() && entries[i + 1].getNodeID() > destID)
                return i;
            if (entries[i].getNodeID() > entries[i + 1].getNodeID() && entries[i].getNodeID() < destID)
                return i;
        }
        return entries.length - 1;
    }

    public void handleNodeSendData(Event event) {
        OverlayNodeSendsData overlayNodeSendsData = (OverlayNodeSendsData) event;

        overlayNodeSendsData.updateTrace(messagingNode.getNodeID());

        //current node is dest node
        if (messagingNode.getNodeID() == overlayNodeSendsData.getDestID()) {
//            System.out.println("Received package from Node " + overlayNodeSendsData.getSrcID());
            messagingNode.updateTracker(overlayNodeSendsData);//update receive packet, and receive sum

            return;
        }

        RoutingEntry[] entries = messagingNode.getRoutingTable().getTable();

        int nextIndex = getNextRoutingIndex(overlayNodeSendsData.getDestID(), entries);

        RoutingEntry nextEntry = entries[nextIndex];
        String host = nextEntry.getLocalhost();
        int port = nextEntry.getPort();

//        System.out.println("Routing package from node " + overlayNodeSendsData.getSrcID() + ", to node " + overlayNodeSendsData.getDestID() + ", next node is node " + nextEntry.getNodeID());

        try {
            TCPConnection connection = ConnectionFactory.getInstance().getConnection(host, port, messagingNode);

//            System.out.println("Sending packages to node " + nextEntry.getNodeID());
            connection.sendData(overlayNodeSendsData.getBytes());

//            System.out.println("OVERLAY: from"+overlayNodeSendsData.getDestID()+)
            messagingNode.updateTracker(overlayNodeSendsData);

        } catch (IOException e) {
            System.out.println("Failed to connect to node " + nextEntry.getNodeID());
            System.out.println(e.getMessage());
        }
    }

    private void reportTaskFinished() throws IOException {

        String ip = messagingNode.getLocalhost().getHostAddress();
        int port = messagingNode.getLocalPort();
        int nodeID = messagingNode.getNodeID();
        OverlayNodeReportsTaskFinished taskFinishedReport = new OverlayNodeReportsTaskFinished(ip, port, nodeID);
        TCPConnection connection = ConnectionFactory.getInstance().getConnection(messagingNode.getRegistryHost(), messagingNode.getRegistryPort(), messagingNode);
        connection.sendData(taskFinishedReport.getBytes());

    }

}
