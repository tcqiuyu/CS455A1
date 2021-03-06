package cs455.overlay.util;

import cs455.overlay.node.OverlayNode;
import cs455.overlay.node.Registry;
import cs455.overlay.transport.ConnectionFactory;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.wireformats.*;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by Qiu on 2/15/2015.}
 */

public class RegistryEventHandler {

    private Registry registry;

    public RegistryEventHandler(Registry registry) {
        this.registry = registry;
    }

    public void handleRegRequest(Event event) throws IOException {
        OverlayNodeSendsRegistration regRequest = (OverlayNodeSendsRegistration) event;

        String mNodeIP = regRequest.getSrcIP();
        int mNodePort = regRequest.getSrcPort();

        System.out.println("Receive registration request from host: " + mNodeIP);
        OverlayNode overlayNode = new OverlayNode(mNodeIP, mNodePort);

        int nodeID = registry.getNextID();
//        System.out.println("Allocate ID " + nodeID + " to incoming messaging node");
        int regResult = registry.registerNode(nodeID, overlayNode);

        String info;
        int infoLength;
        if (regResult != -1) {//registration successful
            info = "Registration request successful. The number of messaging nodes currently constituting the overlay " +
                    "is (" + registry.getNodeAmount() + ")";
            infoLength = info.length();
        } else {//registration failed
            info = "Registration failed. The node already exists in overlay.";
            infoLength = info.length();
        }


        RegistryReportsRegistrationStatus regReport = new RegistryReportsRegistrationStatus(regResult, infoLength, info);
        System.out.println("Getting connection from incoming node..." + "IP: " + mNodeIP);
        TCPConnection connection = ConnectionFactory.getInstance().getConnection(mNodeIP, mNodePort, registry);
        System.out.println("Sending registration report...");
        try {
            connection.sendData(regReport.getBytes());
            System.out.println("Send registration report to messaging node");
        } catch (IOException ioe) {
            registry.deRegisterNode(overlayNode.getHost(), overlayNode.getPort(), overlayNode.getNodeID());
            System.out.println("Failed to send registration report: " + ioe.getMessage());
        }

    }

    public void handleDeregRequest(Event event) throws IOException {
        OverlayNodeSendsDeregistration deregRequest = (OverlayNodeSendsDeregistration) event;

        String srcIP = deregRequest.getIp();
        int srcPort = deregRequest.getPort();
        int nodeID = deregRequest.getNodeID();

        System.out.println("Receiving deregistration request with IP: " + ", node ID: " + nodeID);

        String info;
        int infoLength;
        RegistryReportsDeregistrationStatus deregReport = null;

        switch (registry.deRegisterNode(srcIP, srcPort, nodeID)) {
            case 1:
                info = "Deregistration successful. Node with ID " + nodeID + " is deregistered.";
                infoLength = info.length();
                deregReport = new RegistryReportsDeregistrationStatus(1, infoLength, info);
                break;
            case -1:
                info = "Deregistration failed. Node information provided is not consistent with the one stored in " +
                        "registry.";
                infoLength = info.length();
                deregReport = new RegistryReportsDeregistrationStatus(-1, infoLength, info);
                break;
            case -2:
                info = "Deregisration failed. Node is not in overlay.";
                infoLength = info.length();
                deregReport = new RegistryReportsDeregistrationStatus(-2, infoLength, info);
                break;
            default:
                break;
        }


        try {
            if (deregReport != null) {
                System.out.println("Send deregistration report to messaging node...");
                TCPConnection connection = ConnectionFactory.getInstance().getConnection(srcIP, srcPort, registry);
                connection.sendData(deregReport.getBytes());
            }
        } catch (IOException ioe) {
            OverlayNode node = new OverlayNode(srcIP, srcPort);
            node.setID(nodeID);
            registry.addToNodeMap(nodeID, node);
            System.out.println("Deregistration report failed to send: " + ioe.getMessage());
            return;
        }

        System.out.println("Deregistration report successful to send.");
    }

    public void handleTaskFinishReports(Event event) {
        OverlayNodeReportsTaskFinished taskFinishedReport = (OverlayNodeReportsTaskFinished) event;

        String ip = taskFinishedReport.getIp();
        int port = taskFinishedReport.getPort();
        int nodeID = taskFinishedReport.getNodeID();

        registry.increFinishedNodeCount();
        System.out.println("Node " + nodeID + ", from IP: " + ip + ", port: " + port + ", reports its task finished!");
        System.out.println(registry.getFinishedNodeCount() + " nodes finished their tasks.");

        if (registry.getFinishedNodeCount() == registry.getIdArray().size()) {
            System.out.println("All " + registry.getFinishedNodeCount() + " finished their tasks. Retrieving traffic summary...");
            sendTrafficRequest();
        }
    }

    private void sendTrafficRequest() {
        RegistryRequestsTrafficSummary trafficRequest = new RegistryRequestsTrafficSummary();
        Collection<TCPConnection> connections = ConnectionFactory.getInstance().getAllConnection();
        for (TCPConnection connection : connections) {
            try {
                connection.sendData(trafficRequest.getBytes());
            } catch (IOException e) {
                System.out.println("Failed to send traffic request.");
            }
        }
    }

    public synchronized void handleTrafficSummaryReports(Event event) {
        OverlayNodeReportsTrafficSummary trafficSummary = (OverlayNodeReportsTrafficSummary) event;

        synchronized (StatisticsCollectorAndDisplay.getInstance()) {
            StatisticsCollectorAndDisplay.getInstance().addTrafficSummary(trafficSummary);
            if (StatisticsCollectorAndDisplay.getInstance().getTrafficSummarySize() == registry.getIdArray().size()) {
                StatisticsCollectorAndDisplay.getInstance().printTrafficSummary();
                StatisticsCollectorAndDisplay.getInstance().reset();
                registry.resetCount();
            }
        }
    }

    public void handleSetupStatusReports(Event event) {
        NodeReportsOverlaySetupStatus setupStatusReport = (NodeReportsOverlaySetupStatus) event;
        System.out.println(setupStatusReport.getInfo());
    }


}
